package com.alwon.pos.auth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Enable async processing for notification services
 */
@Configuration
@EnableAsync
public class AsyncConfig {
}
