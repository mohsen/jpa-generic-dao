package com.googlecode.genericdao.search.jpa;

import java.beans.PropertyDescriptor;
import java.io.Serializable;

import javax.persistence.Entity;

import org.apache.commons.beanutils.PropertyUtils;

import com.googlecode.genericdao.search.Metadata;
import com.googlecode.genericdao.search.MetadataUtil;

public class JPAAnnotationMetadataUtil implements MetadataUtil {

    public Metadata get(Class<?> klass) throws IllegalArgumentException {
        return JPAAnnotationMetadata.getMetadata(klass);
    }

    public Metadata get(Class<?> rootEntityClass, String propertyPath) throws IllegalArgumentException {
        Metadata md = get(rootEntityClass);
        if (propertyPath == null || propertyPath.equals("")) {
            return md;
        } else {
            for (String prop : propertyPath.split("\\.")) {
                if ("id".equals(prop)) {
                    md = md.getIdType();
                } else {
                    md = md.getPropertyType(prop);
                }
                if (md == null)
                    throw new IllegalArgumentException("Property path '" + propertyPath + "' invalid for type " + rootEntityClass.getName());
            }
            return md;
        }
    }

    public Serializable getId(Object object) {
        Metadata md = get(object.getClass());
        return md.getIdValue(object);
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

    @SuppressWarnings("unchecked")
    public <T> Class<T> getUnproxiedClass(Class<?> klass) {
        while (klass.getAnnotation(Entity.class) == null) {
            klass = klass.getSuperclass();
            if (Object.class.equals(klass))
                return null;
        }

        return (Class<T>) klass;
    }

    @SuppressWarnings("unchecked")
    public <T> Class<T> getUnproxiedClass(Object entity) {
        return (Class<T>) getUnproxiedClass(entity.getClass());
    }

    /**
     * Taken from http://stackoverflow.com/a/4881082/141438
     * 
     * @param obj
     * @param fieldName
     */
    public static void setNestedProperties(Object obj, String fieldName, Object val) {
        try {
            String[] fieldNames = fieldName.split("\\.");
            if (fieldNames.length > 1) {
                StringBuffer nestedProperty = new StringBuffer();
                for (int i = 0; i < fieldNames.length - 1; i++) {
                    String fn = fieldNames[i];
                    if (i != 0) {
                        nestedProperty.append(".");
                    }
                    nestedProperty.append(fn);

                    Object value = PropertyUtils.getProperty(obj, nestedProperty.toString());

                    if (value == null) {
                        PropertyDescriptor propertyDescriptor = PropertyUtils.getPropertyDescriptor(obj, nestedProperty.toString());
                        Class<?> propertyType = propertyDescriptor.getPropertyType();
                        Object newInstance = propertyType.newInstance();
                        PropertyUtils.setProperty(obj, nestedProperty.toString(), newInstance);
                    }
                }
            }
            PropertyUtils.setNestedProperty(obj, fieldName, val);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
