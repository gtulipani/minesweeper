package com.minesweeper.config;

import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AsyncConfig {

	//Added to avoid Warning: Executor is required to handle Callable return values
	@Bean
	protected WebMvcConfigurer webMvcConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
				configurer.setTaskExecutor(getTaskExecutor());
			}
		};
	}

	@Bean
	protected ConcurrentTaskExecutor getTaskExecutor() {
		return new ConcurrentTaskExecutor(Executors.newFixedThreadPool(5));
	}
}
