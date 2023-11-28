package com.focusservices.library.config.javers;

import java.util.Optional;

import org.javers.spring.auditable.AuthorProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.springframework.context.annotation.Bean;
import com.focusservices.library.config.SecurityHelper;

/**
 * Este bean se encarga de configurar el nombre del usuario actual que realiza
 * las acciones a ser logeadas.
 * 
 *  Se ha creado un archivo de propiedades para javers ya que por alguna razon 
 *  no esta tomando el por defecto de Spring...
 *
 * @author RBonilla
 */
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
