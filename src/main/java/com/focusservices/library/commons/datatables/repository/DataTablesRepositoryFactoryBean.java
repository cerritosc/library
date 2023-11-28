package com.focusservices.library.commons.datatables.repository;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import jakarta.persistence.EntityManager;
import java.io.Serializable;

/**
 * {@link FactoryBean} creating DataTablesRepositoryFactory instances.
 *
 * @author Damien Arrachequesne
 */
public class DataTablesRepositoryFactoryBean<R extends JpaRepository<T, S>, T, S extends Serializable>
        extends JpaRepositoryFactoryBean<R, T, S> {

    public DataTablesRepositoryFactoryBean(Class<? extends R> repositoryInterface) {
        super(repositoryInterface);
    }

    @Override
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
        return new DataTablesRepositoryFactory(entityManager);
    }

    private static class DataTablesRepositoryFactory
            extends JpaRepositoryFactory {

        public DataTablesRepositoryFactory(EntityManager entityManager) {
            super(entityManager);
        }

        @Override
        protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
            Class<?> repositoryInterface = metadata.getRepositoryInterface();
            if (DataTablesRepository.class.isAssignableFrom(repositoryInterface)) {
                return DataTablesRepositoryImpl.class;
            } else {
                return super.getRepositoryBaseClass(metadata);
            }
        }
    }
}
