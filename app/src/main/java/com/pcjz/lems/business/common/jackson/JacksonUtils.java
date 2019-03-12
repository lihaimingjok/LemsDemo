package com.pcjz.lems.business.common.jackson;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;

import java.util.List;

/**
 * Created by ThinkPad on 2017-08-17.
 */

public class JacksonUtils {

    public static List fromJsonArray(String json, Class type)
    {
        ObjectMapper objectMapper = new ObjectMapper();

        List list = null;
        try {
            list = (List)objectMapper.readValue(json, type);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return list;
    }

    public static JavaType getCollectionType(ObjectMapper mapper, Class<?> collectionClass, Class<?>... elementClasses) {
            return mapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }

    public static Object fromJson(String json, Class type, Class<?>... elements)
    {
        ObjectMapper objectMapper = new ObjectMapper();

        Object object = null;
        try {
            JavaType javaType = getCollectionType(objectMapper, type, elements);

            object = objectMapper.readValue(json, javaType);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return object;
    }

    public static Object fromJson(String json, Class type)
    {
        ObjectMapper objectMapper = new ObjectMapper();

        Object object = null;
        try {
            object = objectMapper.readValue(json, type);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return object;
    }

    public static Object fromJson(String json, TypeReference typeReference)
    {
        ObjectMapper objectMapper = new ObjectMapper();

        Object object = null;
        try {
            object = objectMapper.readValue(json, typeReference);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return object;
    }

    public static <T> T jacksonToCollection(String src,Class<?> collectionClass, Class<?>... valueType) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collectionClass, valueType);
        return (T)objectMapper.readValue(src, javaType);
    }
}
