/* Copyright 2013 David Wolverton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.genericdao.search.hibernate;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.ManagedType;
import jakarta.persistence.metamodel.PluralAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.Type;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;

import com.googlecode.genericdao.search.Metadata;
import com.googlecode.genericdao.search.MetadataUtil;

/**
 * Implementation of MetadataUtil for Hibernate
 *
 * A singleton instance of this class is maintained for each SessionFactory.
 * This should be accessed using
 * {@link HibernateMetadataUtil#getInstanceForSessionFactory(SessionFactory)}.
 *
 * <p>
 * Metadata is read from the Jakarta Persistence metamodel exposed by the
 * SessionFactory, since Hibernate 6 no longer provides
 * <code>ClassMetadata</code>.
 *
 * @author dwolverton
 */
public class HibernateMetadataUtil implements MetadataUtil {

	private static Map<SessionFactory, HibernateMetadataUtil> map = new HashMap<SessionFactory, HibernateMetadataUtil>();

	public static HibernateMetadataUtil getInstanceForSessionFactory(SessionFactory sessionFactory) {
		HibernateMetadataUtil instance = map.get(sessionFactory);
		if (instance == null) {
			instance = new HibernateMetadataUtil();
			instance.sessionFactory = sessionFactory;
			map.put(sessionFactory, instance);
		}
		return instance;
	}

	private SessionFactory sessionFactory;

	protected HibernateMetadataUtil() {
	}

	// --- Public Methods ---

	public Serializable getId(Object entity) {
		if (entity == null)
			throw new NullPointerException("Cannot get ID from null object.");
		return get(entity.getClass()).getIdValue(entity);
	}

	public boolean isId(Class<?> rootClass, String propertyPath) {
		if (propertyPath == null || "".equals(propertyPath))
			return false;
		// with hibernate, "id" always refers to the id property, no matter what
		// that property is named. just make sure the segment before this "id"
		// refers to an entity since only entities have ids.
		if (propertyPath.equals("id")
				|| (propertyPath.endsWith(".id") && get(rootClass, propertyPath.substring(0, propertyPath.length() - 3))
						.isEntity()))
			return true;

		// see if the property is the identifier property of the entity it
		// belongs to.
		int pos = propertyPath.lastIndexOf(".");
		if (pos != -1) {
			Metadata parentType = get(rootClass, propertyPath.substring(0, pos));
			if (!parentType.isEntity())
				return false;
			return propertyPath.substring(pos + 1).equals(parentType.getIdProperty());
		} else {
			return propertyPath.equals(get(rootClass).getIdProperty());
		}
	}

	public Metadata get(Class<?> entityClass) throws IllegalArgumentException {
		Class<?> unproxied = getUnproxiedClass(entityClass);
		EntityType<?> entityType = unproxied == null ? null : findEntityType(unproxied);
		if (entityType == null) {
			throw new IllegalArgumentException("Unable to introspect " + entityClass.toString()
					+ ". The class is not a registered Hibernate entity.");
		} else {
			return new HibernateEntityMetadata(sessionFactory, entityType, null);
		}
	}

	public Metadata get(Class<?> rootEntityClass, String propertyPath) throws IllegalArgumentException {
		Metadata md = get(rootEntityClass);
		if (propertyPath == null || "".equals(propertyPath))
			return md;

		try {
			String[] chain = propertyPath.split("\\.");

			for (int i = 0; i < chain.length; i++) {
				md = md.getPropertyType(chain[i]);
			}

			return md;

		} catch (HibernateException | IllegalArgumentException ex) {
			throw new IllegalArgumentException("Could not find property '" + propertyPath + "' on class "
					+ rootEntityClass + ".");
		}
	}

	@SuppressWarnings("unchecked")
	public <T> Class<T> getUnproxiedClass(Class<?> klass) {
		//The entity type will not be found if klass is not registered with Hibernate or when
		//it is a Hibernate proxy class (e.x. test.googlecode.genericdao.model.Person$HibernateProxy$xyz).
		//So if a class is not recognized, we will look at superclasses to see if
		//it is a proxy.
		while (findEntityType(klass) == null) {
			klass = klass.getSuperclass();
			if (klass == null || Object.class.equals(klass))
				return null;
		}

		return (Class<T>) klass;
	}

	@SuppressWarnings("unchecked")
	public <T> Class<T> getUnproxiedClass(Object entity) {
		return (Class<T>) Hibernate.getClassLazy(entity);
	}

	// --- Helpers shared with the Metadata implementations ---

	private EntityType<?> findEntityType(Class<?> klass) {
		try {
			return sessionFactory.getMetamodel().entity(klass);
		} catch (IllegalArgumentException ex) {
			return null;
		}
	}

	/**
	 * Return Metadata for the given attribute. For collections, the metadata
	 * describes the element type.
	 */
	static Metadata forAttribute(SessionFactory sessionFactory, Attribute<?, ?> attribute) {
		if (attribute instanceof PluralAttribute) {
			PluralAttribute<?, ?, ?> plural = (PluralAttribute<?, ?, ?>) attribute;
			return forType(sessionFactory, plural.getElementType(), plural.getJavaType(), plural.getElementType().getJavaType());
		} else {
			return forType(sessionFactory, ((SingularAttribute<?, ?>) attribute).getType(), null, getDeclaredType(attribute));
		}
	}

	private static Metadata forType(SessionFactory sessionFactory, Type<?> type, Class<?> collectionType, Class<?> javaClass) {
		if (type instanceof EntityType) {
			return new HibernateEntityMetadata(sessionFactory, (EntityType<?>) type, collectionType);
		} else {
			return new HibernateNonEntityMetadata(sessionFactory, type, collectionType, javaClass);
		}
	}

	/**
	 * Return the type of the field or getter that the attribute is mapped
	 * with.
	 */
	private static Class<?> getDeclaredType(Attribute<?, ?> attribute) {
		Member member = attribute.getJavaMember();
		if (member instanceof Field) {
			return ((Field) member).getType();
		} else if (member instanceof Method) {
			return ((Method) member).getReturnType();
		} else {
			return attribute.getJavaType();
		}
	}

	/**
	 * Return the names of the attributes of the given type in alphabetical
	 * order (the order Hibernate's ClassMetadata used), optionally leaving out
	 * the identifier attributes.
	 */
	static List<String> getAttributeNames(ManagedType<?> type, boolean excludeIds) {
		List<String> names = new ArrayList<String>();
		for (Attribute<?, ?> attribute : type.getAttributes()) {
			if (excludeIds && attribute instanceof SingularAttribute && ((SingularAttribute<?, ?>) attribute).isId())
				continue;
			names.add(attribute.getName());
		}
		Collections.sort(names);
		return names;
	}

	/**
	 * Read the value of an attribute from an object using the field or getter
	 * that Hibernate maps it with.
	 */
	static Object getAttributeValue(Attribute<?, ?> attribute, Object object) {
		if (object == null)
			return null;
		object = Hibernate.unproxy(object);
		Member member = attribute.getJavaMember();
		try {
			if (member instanceof Field) {
				Field field = (Field) member;
				field.setAccessible(true);
				return field.get(object);
			} else if (member instanceof Method) {
				Method method = (Method) member;
				method.setAccessible(true);
				return method.invoke(object);
			}
		} catch (IllegalAccessException ex) {
			throw new IllegalStateException("Unable to read property '" + attribute.getName() + "'.", ex);
		} catch (InvocationTargetException ex) {
			throw new IllegalStateException("Unable to read property '" + attribute.getName() + "'.", ex.getCause());
		}
		throw new IllegalStateException("Unable to read property '" + attribute.getName() + "': unsupported member " + member);
	}
}
