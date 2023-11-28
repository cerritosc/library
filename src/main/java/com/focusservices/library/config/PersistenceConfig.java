package com.focusservices.library.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.naming.NamingException;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jndi.JndiTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.focusservices.library.commons.datatables.repository.DataTablesRepositoryFactoryBean;

@Slf4j
@Configuration
@EnableTransactionManagement
@PropertySource("classpath:hibernate.properties")
@EnableJpaRepositories(repositoryFactoryBeanClass = DataTablesRepositoryFactoryBean.class, basePackages = {"com.focusservices.library.repository"})
@EnableJpaAuditing(auditorAwareRef="auditorProvider")
public class PersistenceConfig {
    private static final Logger LOG = LoggerFactory.getLogger(PersistenceConfig.class);

    @Bean
    public AuditorAware<String> auditorProvider() {
        return new AuditorAwareImpl();
    }
    
    /** Lo que viene a continuacion son propiedades usadas para las conexiones locales.
     * 
     */
    @Value("${db.driver}")
    private String nombreDriver;
    
    @Value("${db.connection.url}")
    private String url;
    
    @Value("${db.connection.username}")
    private String usuario;
    
    @Value("${db.connection.password}")
    private String password;
    
    @Value("${db.dialect}")
    private String dialectoSql;
    
    @Value("${db.hibernate.hbm2ddl.auto}")
    private String generacionDdl;
    
    @Value("${db.datasource:}")
    private String urlDataSourceJndi;
    
    /** En este metodo se crea el bean encargado de crear EntityManagerFactories 
     *
     * @param dataSource el dataSource definido para la aplicacion.
     * @param jpaAdapter el adaptador de JPA, definido mas abajo.
     * @return un bean que crea entityManagerFactories
     */
    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean crearEntityManagerFactory(
            @Autowired DataSource dataSource
            , @Autowired JpaVendorAdapter jpaAdapter
    ) {
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(jpaAdapter);
        factory.setPackagesToScan("com.focusservices.library.domain", "com.focusservices.library.dto");
        factory.setDataSource(dataSource);
        factory.setJpaPropertyMap(crearPropiedaresHibernate());
        return factory;
    }
    
    /** Crea un map con las propiedades deseadas que se encuentren en Hibernate.
     *  La mayoria de estas se deja aqui porque seria algo engorroso escribirlas desde un archivo
     *  de configuracion y rara vez cambian.
     * 
     * @return las propiedades de configuracion de Hibernate.
     */
    private Map<String, Object> crearPropiedaresHibernate() {
        Properties propiedades = new Properties();
        propiedades.setProperty("hibernate.transaction.factory_class", "org.hibernate.transaction.CMTTransactionFactory");
        propiedades.setProperty("hibernate.transaction.manager_lookup_class", "org.hibernate.transaction.JBossTransactionManagerLookup");
        propiedades.setProperty("hibernate.connection.release_mode", "on_close");
        propiedades.setProperty("hibernate.dialect", dialectoSql);
        propiedades.setProperty("hibernate.proc.param_null_passing", "true");
        propiedades.setProperty("hibernate.enable_lazy_load_no_trans", "true");
        propiedades.setProperty("hibernate.temp.use_jdbc_metadata_defaults", "false");
        propiedades.setProperty("hibernate.show_sql", "true"); // DEJAR a FALSE,
        propiedades.setProperty("hibernate.hbm2ddl.auto", generacionDdl);
        return propertiesToMap(propiedades);
    }
    
    private Map<String, Object> propertiesToMap(Properties propiedades) {
        Map<String, Object> mapa = new HashMap<>();
        for(Map.Entry<Object, Object> entrada : propiedades.entrySet()) {
            mapa.put(entrada.getKey().toString(), entrada.getValue());
        }
        
        return mapa;
    }

    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager() {
        return new JpaTransactionManager();
    }
    
    /** Se crea el dataSource de la aplicacion condicionalmente buscando primeramente uno
     *  configurado por JDNI. Si no existe un dataSource, se procede a crear uno 
     *  usando las variables locales establecidas en el archivo hibernate.properties.
     * 
     * @return el dataSource que la aplicacion utilizara.
     */
    @Bean
    public DataSource crearDataSource() {
        try {
            return crearDataSourceJNDI();
        } catch (Exception ex) {
            LOG.warn("Ocurrio un error al intentar consultar el nombre del dataSource de JNDI"
                    + ", procediendo a usar el configurado en el archivo hibernate.properties... ");
            return crearDataSourceLocal();
        }
    }

    /** Este dataSource es el preferido por la aplicacion y es el consultado desde JNDI.
     *  Se asume que la variable que guarda el origendel dataSource es configurada
     *  como variable de entorno, ESTA VARIABLE NO ESTA ESTABLECIDA EN HIBERNATE.PROPERTIES
     *  
     * @return dataSource configurado.
     * @throws NamingException si no se encuentra el dataSource.
     */
    private DataSource crearDataSourceJNDI() throws NamingException {
        DataSource dataSourceJndi = (DataSource) new JndiTemplate().lookup(urlDataSourceJndi);
        return dataSourceJndi;
    }

    /** Se crea un dataSource local utilizando las variables establecidas en hibernate.properties.
     *  
     *  NOTA: Para aplicaciones en produccion se prefiere que se use JNDI por dos motivos:
     * 
     *  1) Aqui no se ha configurado un pool de conexiones, lo cual es lo recomendado para cualquier aplicacion.
     *  2) Por cuestiones de seguridad no se debe configurar la base con un properties, en especial si es la 
     *      de produccion.
     * 
     * @return un dataSource con la informacion local.
     */
    private DataSource crearDataSourceLocal() {
        return DataSourceBuilder.create()
          .driverClassName(nombreDriver)
          .url(url)
          .username(usuario)
          .password(password)
          .build(); 
    }
}
