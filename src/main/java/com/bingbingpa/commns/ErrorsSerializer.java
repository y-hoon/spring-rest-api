package com.bingbingpa.commns;

import java.io.IOException;

import org.springframework.boot.jackson.JsonComponent;
import org.springframework.validation.Errors;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

@JsonComponent //objectMapper에 등록 
public class ErrorsSerializer extends JsonSerializer<Errors> {

	@Override
	public void serialize(Errors erros, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		gen.writeStartArray();
		/**
		 * stream()에서 forEach만 할꺼면 stream() 생략 가능. 
		 */
		erros.getFieldErrors().forEach(e -> {
			try {
				gen.writeStartObject();
				gen.writeStringField("field", e.getField());
				gen.writeStringField("objectName", e.getObjectName());
				gen.writeStringField("code", e.getCode());
				gen.writeStringField("defaultMessage", e.getDefaultMessage());
				Object rejectedValue = e.getRejectedValue();
				if(rejectedValue != null) {
					gen.writeStringField("rejectedValue", rejectedValue.toString());
				}
				gen.writeEndObject();
			} catch (IOException el) {
				el.printStackTrace();
			}
		});
		erros.getGlobalErrors().forEach( e -> {
			try {
				gen.writeStartObject();
				gen.writeStringField("objectName", e.getObjectName());
				gen.writeStringField("code", e.getCode());
				gen.writeStringField("defaultMessage", e.getDefaultMessage());
				gen.writeEndObject();
			} catch (IOException el) {
				el.printStackTrace();
			}
		});
		gen.writeEndArray();
	}
}
