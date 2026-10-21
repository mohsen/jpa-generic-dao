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
import java.util.List;

import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.EmbeddableType;
import jakarta.persistence.metamodel.Type;

import org.hibernate.SessionFactory;
import org.hibernate.metamodel.mapping.JdbcMapping;

import com.googlecode.genericdao.search.Metadata;

/**
 * Implementation of Metadata for a non-entity type in Hibernate.
 *
 * @author dwolverton
 */
public class HibernateNonEntityMetadata implements Metadata {

	private SessionFactory sessionFactory;
	private Type<?> type;
	private Class<?> collectionType;
	private Class<?> javaClass;

	public HibernateNonEntityMetadata(SessionFactory sessionFactory, Type<?> type, Class<?> collectionType) {
		this(sessionFactory, type, collectionType, type.getJavaType());
	}

	/**
	 * @param javaClass the Java class of the property as declared in the
	 *            model. This can differ from the class of the Hibernate type,
	 *            for example a <code>java.util.Date</code> property mapped as a
	 *            timestamp has type <code>java.sql.Timestamp</code>.
	 */
	public HibernateNonEntityMetadata(SessionFactory sessionFactory, Type<?> type, Class<?> collectionType, Class<?> javaClass) {
		this.sessionFactory = sessionFactory;
		this.type = type;
		this.collectionType = collectionType;
		this.javaClass = javaClass;
	}

	public String getIdProperty() {
		return null;
	}

	public Metadata getIdType() {
		return null;
	}

	public Serializable getIdValue(Object object) {
		return null;
	}

	public Class<?> getJavaClass() {
		return javaClass;
	}

	public String getEntityName() {
		throw new UnsupportedOperationException("Cannot get Entity Name of non-entity type.");
	}

	public String[] getProperties() {
		if (isEmeddable()) {
			List<String> names = HibernateMetadataUtil.getAttributeNames((EmbeddableType<?>) type, false);
			return names.toArray(new String[names.size()]);
		} else {
			return null;
		}
	}

	public Metadata getPropertyType(String property) {
		Attribute<?, ?> attribute = getAttribute(property);
		if (attribute == null) {
			return null;
		} else {
			return HibernateMetadataUtil.forAttribute(sessionFactory, attribute);
		}
	}

	public Object getPropertyValue(Object object, String property) {
		Attribute<?, ?> attribute = getAttribute(property);
		if (attribute == null) {
			return null;
		} else {
			return HibernateMetadataUtil.getAttributeValue(attribute, object);
		}
	}

	public boolean isCollection() {
		return collectionType != null;
	}

	public Class<?> getCollectionClass() {
		return collectionType;
	}

	public boolean isEmeddable() {
		return type instanceof EmbeddableType;
	}

	public boolean isEntity() {
		return false;
	}

	public boolean isNumeric() {
		Class<?> javaClass = getJavaClass();
		if (javaClass.isPrimitive())
			return javaClass != boolean.class && javaClass != char.class && javaClass != void.class;
		return Number.class.isAssignableFrom(javaClass);
	}

	public boolean isString() {
		if (type instanceof JdbcMapping) {
			return ((JdbcMapping) type).getJdbcType().isString();
		}
		return String.class.equals(getJavaClass());
	}


	private Attribute<?, ?> getAttribute(String property) {
		if (!isEmeddable())
			return null;
		try {
			return ((EmbeddableType<?>) type).getAttribute(property);
		} catch (IllegalArgumentException ex) {
			return null;
		}
	}
}
