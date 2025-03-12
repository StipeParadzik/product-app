package com.ingemark.productapp.app.entity.identifiable;

import java.util.HashMap;
import java.util.Map;

import lombok.experimental.UtilityClass;

import com.ingemark.productapp.app.entity.identifiable.exceptions.FieldMustNotBeSetException;
import com.ingemark.productapp.app.entity.identifiable.exceptions.FieldValuesDontMatchException;

@UtilityClass
public class EntityVerifier
{
    public void verifyIdNotSet(IdentifiableEntity entity)
    {
        if (entity.getId() != null)
        {
            throw new FieldMustNotBeSetException("id must not be set for new entities", "entityId");
        }
    }

    public <E extends IdentifiableEntity> void verifyIdsMatch(Integer id, E entity)
    {
        if (id == null || !id.equals(entity.getId()))
        {
            throw new FieldValuesDontMatchException("id from the path does not match id of the entity",
                entity.getId(),
                id);
        }
    }

    public static Map<String, Object> createHashMap(String key1, Object value1, String key2, Object value2)
    {
        Map<String, Object> map = new HashMap();
        map.put(key1, value1);
        map.put(key2, value2);
        return map;
    }
}