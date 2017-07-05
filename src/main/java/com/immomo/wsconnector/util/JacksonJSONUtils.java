package com.immomo.wsconnector.util;

import java.io.IOException;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Jackson JSON 工具类
 * 
 * 参考：
 * https://github.com/FasterXML/jackson-databind/
 * 
 * @author zcx
 *
 */
public class JacksonJSONUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    private static final DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	
    static {

		objectMapper.configure(MapperFeature.USE_ANNOTATIONS, true);
        
        //=======Let's start with higher-level data-binding configuration.====
        // to enable standard indentation ("pretty-printing"):
        //格式化输出的JSON串
        //objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        // to allow serialization of "empty" POJOs (no properties to serialize)
        // (without this setting, an exception is thrown in those cases)
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        // to write java.util.Date, Calendar as number (timestamp):
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        // to allow coercion of JSON empty String ("") to null Object value:
        objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);

        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS,true);
        // to allow (non-standard) unquoted field names in JSON:
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES,false);
        // to allow use of apostrophes (single quotes), non standard
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES,true);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        
        //使用枚举对象的toString方法作为序列化的输出
        objectMapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        objectMapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);

        // JsonGenerator.Feature for configuring low-level JSON generation:

        // to force escaping of non-ASCII characters:
        //即把所有非ASCII字符如中文字符转义成\u738B\u4E8C\u9EBB这种unicode形式
        //objectMapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII,true);
        
        
        objectMapper.setDateFormat(simpleDateFormat);		
		
		objectMapper.getSerializationConfig()
			.with(simpleDateFormat)
			.with(MapperFeature.USE_ANNOTATIONS)
			.without(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		    //.without(SerializationFeature.WRITE_NULL_MAP_VALUES);
		
		 //  objectMapper.getSerializationConfig().setSerializationInclusion(
		 //			JsonSerialize.Inclusion.NON_NULL);
		   
		objectMapper.getDeserializationConfig()
			.with(simpleDateFormat)
			.with(MapperFeature.USE_ANNOTATIONS)
			.without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

	
	}
    


	public static <T> String toJSON(T t) {
		try {
			String jsonStr = objectMapper.writeValueAsString(t);
			return jsonStr;
		} catch (JsonGenerationException e) {
			throw new RuntimeException("JsonGenerationException", e);
		} catch (JsonMappingException e) {
			throw new RuntimeException("JsonMappingException", e);
		} catch (IOException e) {
			throw new RuntimeException("IOException", e);
		}
	}
	
	public static <T> T fromJSON(String jsonString, Class<T> clazz) {

		T object = null;
		try {
			object = objectMapper.readValue(jsonString, clazz);
		}catch(JsonParseException e){
			throw new RuntimeException("JsonParseException", e);
		}catch (JsonGenerationException e) {
			throw new RuntimeException("JsonGenerationException", e);
		} catch (JsonMappingException e) {
			throw new RuntimeException("JsonMappingException", e);
		} catch (IOException e) {
			throw new RuntimeException("IOException", e);
		}
		return object;
	}


	public static <T> T fromJSON(String jsonString, TypeReference<T> typeReference) {

		T object = null;
		try {
			object = objectMapper.readValue(jsonString, typeReference);
		}catch(JsonParseException e){
			throw new RuntimeException("JsonParseException", e);
		}catch (JsonGenerationException e) {
			throw new RuntimeException("JsonGenerationException", e);
		} catch (JsonMappingException e) {
			throw new RuntimeException("JsonMappingException", e);
		} catch (IOException e) {
			throw new RuntimeException("IOException", e);
		}
		return object;
	}
      
    
}