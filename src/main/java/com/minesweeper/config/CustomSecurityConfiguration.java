package com.minesweeper.config;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class CustomSecurityConfiguration extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		mainConfiguration(http);
		noSecurityOnMatchers(http);
	}

	private void mainConfiguration(HttpSecurity http) throws Exception {
		http
				.cors().disable()
				.csrf().disable()
				.logout().disable()
				.x509().disable()
				.formLogin().disable()
				.httpBasic().disable()
				.rememberMe().disable()
				.sessionManagement().sessionCreationPolicy(STATELESS);
	}

	private void noSecurityOnMatchers(HttpSecurity http) throws Exception {
		http
				.authorizeRequests()
				.antMatchers("/health", "/info", "/prometheus", "/v2/api-docs", "/swagger*/**", "/webjars/**")
				.permitAll()
				.and()
				.authorizeRequests()
				.requestMatchers(EndpointRequest.toAnyEndpoint())
				.denyAll();
	}
}
