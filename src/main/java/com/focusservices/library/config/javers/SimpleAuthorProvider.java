package com.focusservices.library.config.javers;

import java.util.Optional;

import org.javers.spring.auditable.AuthorProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.springframework.context.annotation.Bean;
import com.focusservices.library.config.SecurityHelper;

@Configuration
@PropertySource("classpath:javers.properties")
public class SimpleAuthorProvider implements AuthorProvider {

    @Override
    public String provide() {
        Optional<String> nombreUsuarioActual = Optional.ofNullable(SecurityHelper.getLoggedInUserDetails().getUsername());
        return nombreUsuarioActual.orElseThrow(IllegalArgumentException::new);
    }
    
    @Bean
    public Javers javers() {
        return JaversBuilder.javers()
                .build();
    }
}
