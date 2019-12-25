package com.bingbingpa.accounts;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class AccountServiceTest {
	
	@Autowired
	AccountService accountService;
	
	@Autowired
	AccountRepository accountRepository;
	
	@Test
	public void findByUsername() {
		// Given
		String password = "shpark";
		String username = "guriguri1576@gmail.com";
		Account account = Account.builder()
					.email(username)
					.password(password)
					.roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
					.build();
		
		this.accountRepository.save(account);
		
		// When
		UserDetailsService userDetailService = accountService;
		UserDetails userDetails = userDetailService.loadUserByUsername(username);
		
		// Then
		assertThat(userDetails.getPassword()).isEqualTo(password);
	}
}
