package com.bingbingpa.configs;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Set;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;

import com.bingbingpa.accounts.Account;
import com.bingbingpa.accounts.AccountRole;
import com.bingbingpa.accounts.AccountService;
import com.bingbingpa.common.BaseControllerTest;

public class AuthServerConfigTest extends BaseControllerTest {
	
	@Autowired
	AccountService accountService;
	
	@Test
	@DisplayName("인증 토큰을 발급 받는 테스트")
	public void getAuthToken() throws Exception {
		// Given
		String username = "guriguir1576@gmail.com";
		String password = "shpark";
		Account account = Account.builder()
				.email(username)
				.password(password)
				.roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
				.build();
		this.accountService.saveAccount(account);
		
		String clientId = "myApp";
		String clientSecret = "pass";
		this.mockMvc.perform(post("/oauth/token")
					.with(httpBasic(clientId, clientSecret))
					.param("username", username)
					.param("password", password)
					.param("grant_type",  "password"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("access_token").exists());
				
		
	}
}
