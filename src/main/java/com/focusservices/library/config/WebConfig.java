package com.focusservices.library.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.extras.springsecurity6.dialect.SpringSecurityDialect;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule;

import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import org.springframework.beans.factory.annotation.Autowired;

@Configuration
public class WebConfig implements WebMvcConfigurer{
	
    @Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
          .addResourceHandler("/resources/**")
          .addResourceLocations("/resources/"); 
    }
        
    @Bean
    public String getPackage() {
    	return "com.focusservices.library";
    }
	
    @Bean
    @Description("Thymeleaf template resolver serving HTML 5")
    public ClassLoaderTemplateResolver templateResolver() {

        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();

        templateResolver.setPrefix("templates/");
        templateResolver.setCacheable(false);
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML");
        templateResolver.setCharacterEncoding("UTF-8");

        return templateResolver;
    }
    
    @Bean
    @Description("Thymeleaf template engine with Spring integration")
    public SpringTemplateEngine templateEngine() {

        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver());
        templateEngine.addDialect(layoutDialect());
        templateEngine.addDialect(new SpringSecurityDialect());
        return templateEngine;
    }

    @Bean
    @Description("Thymeleaf view resolver")
    public ViewResolver viewResolver() {
        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();

        viewResolver.setTemplateEngine(templateEngine());
        viewResolver.setCharacterEncoding("UTF-8");

        return viewResolver;
    }

    @Bean
    public LayoutDialect layoutDialect() {
        return new LayoutDialect();
    }

    /*Al serializar entidades previene llamadas a campos lazy */
    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = jsonConverter.getObjectMapper();
        Hibernate5JakartaModule hibernate5Module = new Hibernate5JakartaModule();
        hibernate5Module.disable(Hibernate5JakartaModule.Feature.USE_TRANSIENT_ANNOTATION);
        objectMapper.registerModule(hibernate5Module);
        return jsonConverter;
    }

}

