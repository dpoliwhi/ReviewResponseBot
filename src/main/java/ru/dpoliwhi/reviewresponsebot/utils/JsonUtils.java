package ru.dpoliwhi.reviewresponsebot.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.dpoliwhi.reviewresponsebot.exceptions.IncorrectJSONException;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.List;

@Component
public class JsonUtils {
    private static final String CONVERTING_ERROR_MESSAGE = "Error while convert JSON to Object ";
    private static final String DESERIALIZATION_ERROR_MESSAGE = "Cant deserialize JSON to Object!";
    private static final String TO_CLASS_MESSAGE = " to class ";

    private Logger logger;

    @Autowired
    private ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            logger.error("Something went wrong while try convert object to JSON", e);
        }
        throw new IncorrectJSONException("Check you object cant convert to JSON " + object.toString());
    }

    public <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            logger.error(String.format("%s %s", CONVERTING_ERROR_MESSAGE, clazz.getName()));
        }
        throw new IncorrectJSONException(DESERIALIZATION_ERROR_MESSAGE + json + TO_CLASS_MESSAGE + clazz.getName());
    }

    public <T> T fromJson(InputStream inputStream, Class<T> clazz) {
        try {
            return objectMapper.readValue(inputStream, clazz);
        } catch (Exception e) {
            logger.error(String.format("%s %s", CONVERTING_ERROR_MESSAGE, clazz.getName()));
        }
        throw new IncorrectJSONException("Cant deserialize JSON to inputStream to class " + clazz.getName());
    }

    public <T> T prepareDTO(Object rawData, Class type) {
        return (T) objectMapper.convertValue(rawData, type);
    }

    public <T> T prepareDTO(Object rawData, TypeReference type) {
        return (T) objectMapper.convertValue(rawData, type);
    }

    public String toString(Object rawData) throws JsonProcessingException {
        return objectMapper.writeValueAsString(rawData);
    }

    public <T> T fromJson(String json, TypeReference<T> type) {
        try {
            return objectMapper.readValue(json, type);
        } catch (Exception e) {
            logger.error(String.format("%s %s", CONVERTING_ERROR_MESSAGE, type.getType().getTypeName()));
        }
        throw new IncorrectJSONException(DESERIALIZATION_ERROR_MESSAGE + json + TO_CLASS_MESSAGE + type.getType().getTypeName());
    }

    public <T> List<T> fromJsonToList(String json, Class<T> type) {
        try {
            CollectionType collectionType = objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, type);
            return objectMapper.readValue(json, collectionType);
        } catch (Exception e) {
            logger.error(String.format("%s %s", "Error while convert JSON to List<Object>", type.getTypeName()));
        }
        throw new IncorrectJSONException("Cant deserialize JSON to List<Object>!"
                + json + TO_CLASS_MESSAGE + type.getTypeName());
    }

    public <T> T fromJson(InputStream inputStream, TypeReference<T> type) {
        try {
            return objectMapper.readValue(inputStream, type);
        } catch (Exception e) {
            logger.error(CONVERTING_ERROR_MESSAGE + type.getType().getTypeName());
        }
        throw new IncorrectJSONException("Cant deserialize JSON to inputStream to class " + type.getType().getTypeName());
    }
}
