package br.com.seasonpessoal.fakegranapp.util;


import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * Created by carlos on 24/02/19.
 */
public class JSONConverter {

    public static String toJSON(Object object) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJSON(String json, Class<T> klass) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, klass);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
