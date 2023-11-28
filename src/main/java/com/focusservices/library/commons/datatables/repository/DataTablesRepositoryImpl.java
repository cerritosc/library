package com.focusservices.library.commons.datatables.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import com.focusservices.library.commons.datatables.mapping.DataTablesInput;
import com.focusservices.library.commons.datatables.mapping.DataTablesOutput;

import jakarta.persistence.EntityManager;
import java.io.Serializable;
import java.util.List;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import com.focusservices.library.commons.datatables.DatatablesUtils;
import com.focusservices.library.commons.datatables.criteria.NeoSpecificationBuilder;

@Slf4j
public class DataTablesRepositoryImpl<T, S extends Serializable> extends SimpleJpaRepository<T, S>
        implements DataTablesRepository<T, S> {

    DataTablesRepositoryImpl(JpaEntityInformation<T, ?> entityInformation,
                             EntityManager entityManager) {

        super(entityInformation, entityManager);
    }

    @Override
    public DataTablesOutput<T> findAll(DataTablesInput input) {
        return findAll(input, null, null, null);
    }

    @Override
    public DataTablesOutput<T> findAll(DataTablesInput input,
                                       Specification<T> additionalSpecification) {
        return findAll(input, additionalSpecification, null, null);
    }

    @Override
    public DataTablesOutput<T> findAll(DataTablesInput input,
                                       Specification<T> additionalSpecification, Specification<T> preFilteringSpecification) {
        return findAll(input, additionalSpecification, preFilteringSpecification, null);
    }

    @Override
    public <R> DataTablesOutput<R> findAll(DataTablesInput input, Function<T, R> converter) {
        return findAll(input, null, null, converter);
    }

    @Override
    public <R> DataTablesOutput<R> findAll(DataTablesInput input,
                                           Specification<T> additionalSpecification, Specification<T> preFilteringSpecification,
                                           Function<T, R> converter) {
        DataTablesOutput<R> output = new DataTablesOutput<>();
        output.setDraw(input.getDraw());
        if (input.getLength() == 0) {
            return output;
        }

        try {
            long recordsTotal =
                    preFilteringSpecification == null ? count() : count(preFilteringSpecification);
            if (recordsTotal == 0) {
                return output;
            }
            output.setRecordsTotal(recordsTotal);

            Page<T> data = findAll(Specification.where(new NeoSpecificationBuilder<>(super.getDomainClass(), input))
                            .and(additionalSpecification)
                            .and(preFilteringSpecification),
                    DatatablesUtils.createPageable(input));

            @SuppressWarnings("unchecked")
            List<R> content =
                    converter == null ? (List<R>) data.getContent() : data.map(converter).getContent();
            output.setData(content);
            output.setRecordsFiltered(data.getTotalElements());

        } catch (Exception e) {
            log.error("Ocurrio un error al ejecutar el query especificado...", e);
            output.setError(e.toString());
        }

        return output;
    }

}
