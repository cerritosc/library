package com.focusservices.library.commons.datatables.criteria;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Predicate;

public interface FilterToPredicate {
    
    Predicate toPredicate(From root, CriteriaBuilder cb, CriteriaFilter regla);
}
