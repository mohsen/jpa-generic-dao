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
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;

import org.hibernate.SessionFactory;

import com.googlecode.genericdao.search.Metadata;

/**
 * Implementation of Metadata for a Hibernate entity.
 *
 * @author dwolverton
 */
public class HibernateEntityMetadata implements Metadata {

	private SessionFactory sessionFactory;
	private EntityType<?> entityType;
	private Class<?> collectionType;

	public HibernateEntityMetadata(SessionFactory sessionFactory, EntityType<?> entityType, Class<?> collectionType) {
		this.sessionFactory = sessionFactory;
		this.entityType = entityType;
		this.collectionType = collectionType;
	}

	public String getEntityName() {
		// Hibernate's entity name for a mapped class is its fully qualified class name
		return entityType.getJavaType().getName();
	}

	public String getIdProperty() {
		SingularAttribute<?, ?> idAttribute = getIdAttribute();
		return idAttribute == null ? null : idAttribute.getName();
	}

	public Metadata getIdType() {
		return new HibernateNonEntityMetadata(sessionFactory, entityType.getIdType(), null);
	}

	public Serializable getIdValue(Object object) {
		return (Serializable) sessionFactory.getPersistenceUnitUtil().getIdentifier(object);
	}

	public Class<?> getJavaClass() {
		return entityType.getJavaType();
	}

	public String[] getProperties() {
		List<String> pn = HibernateMetadataUtil.getAttributeNames(entityType, true);
		String[] result = new String[pn.size() + 1];
		result[0] = getIdProperty();
		for (int i = 0; i < pn.size(); i++) {
			result[i+1] = pn.get(i);
		}
		return result;
	}

	public Metadata getPropertyType(String property) {
		return HibernateMetadataUtil.forAttribute(sessionFactory, getAttribute(property));
	}

	public Object getPropertyValue(Object object, String property) {
		if (property.equals(getIdProperty()))
			return getIdValue(object);
		else
			return HibernateMetadataUtil.getAttributeValue(getAttribute(property), object);
	}

	public boolean isCollection() {
		return collectionType != null;
	}

	public Class<?> getCollectionClass() {
		return collectionType;
	}

	public boolean isEmeddable() {
		return false;
	}

	public boolean isEntity() {
		return true;
	}

	public boolean isNumeric() {
		return false;
	}

	public boolean isString() {
		return false;
	}

	private SingularAttribute<?, ?> getIdAttribute() {
		if (!entityType.hasSingleIdAttribute())
			return null;
		for (SingularAttribute<?, ?> attribute : entityType.getSingularAttributes()) {
			if (attribute.isId())
				return attribute;
		}
		return null;
	}

	private Attribute<?, ?> getAttribute(String property) {
		try {
			return entityType.getAttribute(property);
		} catch (IllegalArgumentException ex) {
			// as in HQL, "id" refers to the identifier property whatever its name
			if ("id".equals(property) && getIdAttribute() != null)
				return getIdAttribute();
			throw ex;
		}
	}
}
