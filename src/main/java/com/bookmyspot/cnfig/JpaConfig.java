package com.bookmyspot.cnfig;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class JpaConfig {
    // This enables automatic timestamp management with @CreatedDate and @LastModifiedDate
}