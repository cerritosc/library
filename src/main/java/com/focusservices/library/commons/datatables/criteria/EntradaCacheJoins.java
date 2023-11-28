package com.focusservices.library.commons.datatables.criteria;

import java.util.Optional;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Predicate;
import com.focusservices.library.commons.datatables.mapping.Column;

public class EntradaCacheJoins {
    private From from;
    private CriteriaQuery query;
    private RecursivePredicateCreator recursivePredicateCreator;

    public EntradaCacheJoins(From from, CriteriaQuery query, RecursivePredicateCreator recursivePredicateCreator) {
        this.from = from;
        this.query = query;
        this.recursivePredicateCreator = recursivePredicateCreator;
    }

    public From getFrom() {
        return from;
    }

    public RecursivePredicateCreator getRecursivePredicateCreator() {
        return recursivePredicateCreator;
    }
    
    public Optional<Predicate> crearPredicate(CriteriaBuilder cb, Column filtro) throws NoSuchFieldException {
        return recursivePredicateCreator.crearPredicateDeColumna(from, query, cb, filtro);
    }
}
