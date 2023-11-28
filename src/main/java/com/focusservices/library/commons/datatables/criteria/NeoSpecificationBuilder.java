package com.focusservices.library.commons.datatables.criteria;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.thymeleaf.util.StringUtils;
import com.focusservices.library.commons.datatables.mapping.Column;
import com.focusservices.library.commons.datatables.mapping.DataTablesInput;

public class NeoSpecificationBuilder<T> implements Specification<T> {
    private Class<T> claseEntidad;
    private DataTablesInput input;
    
    public NeoSpecificationBuilder(Class<T> claseEntidad, DataTablesInput input) {
        this.claseEntidad = claseEntidad;
        this.input = input;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        try {
            RecursivePredicateCreator predicateCreator = new RecursivePredicateCreator(claseEntidad);
            List<Predicate> filtrosProcesados = new ArrayList<>();
            if(input != null) {
                for(Column filtro : input.getColumns()) {
                    // solo se filtra si la columna es filtrable y no esta vacia
                    if(filtro.getSearchable() && !StringUtils.isEmpty(filtro.getSearch().getValue())) {
                        Optional<Predicate> predicado = predicateCreator.crearPredicateDeColumna(root, query, criteriaBuilder, filtro);
                        predicado.ifPresent(filtrosProcesados::add);
                    }
                }
                return criteriaBuilder.and(filtrosProcesados.toArray(new Predicate[]{}));
            }
            else return null;
        } catch (Exception e) {
            throw new IllegalArgumentException("Ocurrio un error al intentar ejecutar el criteria query... ", e);
        }
    }
}
