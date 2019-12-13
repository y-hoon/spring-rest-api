package com.bingbingpa.commns;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * junit5 부터는 기본적으로 @DisplayName으로 테스트명을 쓸 수 있다.
 * @author shpark
 *
 */
@Target(value = ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface TestDescription {
	
	String value();
}
