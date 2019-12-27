package com.bingbingpa.accounts;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

/**
 * 인증되지 않은 사용자의 요청일 경우에는 account가 아닌 'anonymousUser'라는 문자열이 들어오므로 
 * expression에서 아래와 같이 처리해준다.
 * @author shpark
 *
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@AuthenticationPrincipal(expression = "#this == 'anonymousUser' ? null : account")
public @interface CurrentUser {

}
