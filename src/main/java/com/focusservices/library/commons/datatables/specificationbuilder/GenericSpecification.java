package com.focusservices.library.commons.datatables.specificationbuilder;

import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import com.focusservices.library.commons.datatables.Constants;

import jakarta.persistence.criteria.*;

/**
 * <p>
 * Class which implements {@link Specification} interface
 * to generate {@link Predicate} object
 * by checking given {@link FilterCriteria} object
 * </p>
 *
 * @param <T>
 * @param <C> Created on October, 2018
 * @author Destan Sarpkaya
 * @author Ersan Ceylan
 * @author Gökhan Birinci
 */

@SuppressWarnings({"rawtypes", "unchecked"})
@AllArgsConstructor
class GenericSpecification<E, T, C extends Comparable<? super C>> implements Specification<E> {

    private final FilterCriteria<T> filterCriteria;

    @Override
    public Predicate toPredicate(Root<E> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

        final CriteriaOperation operation = filterCriteria.getOperation();
        final String key = filterCriteria.getKey();

        switch (operation) {
            case JOIN:
                return joinPredicate(root, key, filterCriteria);
            case JOIN_FETCH:
                return joinFetchPredicate(root, query, key, filterCriteria);
            case EQUAL:
                return equalPredicate(root, criteriaBuilder, filterCriteria);
            case IS_NULL:
                return isNullPredicate(root, criteriaBuilder, filterCriteria);
            case IS_NOT_NULL:
                return isNotNullPredicate(root, criteriaBuilder, filterCriteria);
            case LIKE:
                return likePredicate(root, criteriaBuilder, filterCriteria);
            case IN:
                return inPredicate(root, filterCriteria);
            case GREATER_THAN:
                return greaterThanPredicate(root, criteriaBuilder, filterCriteria);
            case GREATER_THAN_OR_EQUAL_TO:
                return greaterThanOrEqualToPredicate(root, criteriaBuilder, filterCriteria);
            case LESS_THAN:
                return lessThanPredicate(root, criteriaBuilder, filterCriteria);
            case LESS_THAN_OR_EQUAL_TO:
                return lessThanOrEqualToPredicate(root, criteriaBuilder, filterCriteria);
            default:
                return null;
        }
    }

    private Predicate joinPredicate(Root<E> root, String key, FilterCriteria<T> filterCriteria) {
        final JoinType joinType = filterCriteria.getJoinType();
        root.join(key, joinType);
        return null;
    }

    private Predicate joinFetchPredicate(Root<E> root, CriteriaQuery<?> query, String key, FilterCriteria<T> filterCriteria) {
        final Class clazz = query.getResultType();
        final JoinType joinType = filterCriteria.getJoinType();
        if (clazz.equals(Long.class) || clazz.equals(long.class)) {
            // If clazz is long then it's a count query for pageable
            root.join(key, joinType);
        } else {
            root.fetch(key, joinType);
            query.distinct(true);
        }
        return null;
    }

    private Predicate equalPredicate(Root<E> root, CriteriaBuilder criteriaBuilder, FilterCriteria<T> filterCriteria) {
        final Path<?> path = resolvePath(root, filterCriteria.getKey(), filterCriteria.getRelationType());
        return criteriaBuilder.equal(path, filterCriteria.getValue());
    }

    private Predicate isNullPredicate(Root<E> root, CriteriaBuilder criteriaBuilder, FilterCriteria<T> filterCriteria) {
        final Path<?> path = resolvePath(root, filterCriteria.getKey(), filterCriteria.getRelationType());
        return criteriaBuilder.isNull(path);
    }

    private Predicate isNotNullPredicate(Root<E> root, CriteriaBuilder criteriaBuilder, FilterCriteria<T> filterCriteria) {
        final Path<?> path = resolvePath(root, filterCriteria.getKey(), filterCriteria.getRelationType());
        return criteriaBuilder.isNotNull(path);
    }

    private Predicate likePredicate(Root<E> root, CriteriaBuilder criteriaBuilder, FilterCriteria<T> filterCriteria) {
        final Path<?> path = resolvePath(root, filterCriteria.getKey(), filterCriteria.getRelationType());
        if (Constants.IS_FILTER_CRITERIA_CASE_SENSITIVE) {
            return criteriaBuilder.like(path.as(String.class), "%" + filterCriteria.getValue() + "%");
        } else {
            return criteriaBuilder.like(criteriaBuilder.lower(path.as(String.class)),
                    "%" + String.valueOf(filterCriteria.getValue()).toLowerCase() + "%");
        }
    }

    private Predicate inPredicate(Root<E> root, FilterCriteria<T> filterCriteria) {
        final Path<?> path = resolvePath(root, filterCriteria.getKey(), filterCriteria.getRelationType());
        return path.in(filterCriteria.getValue());
    }

    private Predicate greaterThanPredicate(Root<E> root, CriteriaBuilder criteriaBuilder, FilterCriteria<T> filterCriteria) {
        final ComparableFilterCriteria<C> comparableFilterCriteria = getComparableFilterCriteria();
        final Path<?> path = resolvePath(root, filterCriteria.getKey(), filterCriteria.getRelationType());
        return criteriaBuilder.greaterThan(path.as(comparableFilterCriteria.getClazz()), comparableFilterCriteria.getValue());
    }

    private Predicate greaterThanOrEqualToPredicate(Root<E> root, CriteriaBuilder criteriaBuilder, FilterCriteria<T> filterCriteria) {
        final ComparableFilterCriteria<C> comparableFilterCriteria = getComparableFilterCriteria();
        final Path<?> path = resolvePath(root, filterCriteria.getKey(), filterCriteria.getRelationType());
        return criteriaBuilder.greaterThanOrEqualTo(path.as(comparableFilterCriteria.getClazz()), comparableFilterCriteria.getValue());
    }

    private Predicate lessThanPredicate(Root<E> root, CriteriaBuilder criteriaBuilder, FilterCriteria<T> filterCriteria) {
        final ComparableFilterCriteria<C> comparableFilterCriteria = getComparableFilterCriteria();
        final Path<?> path = resolvePath(root, filterCriteria.getKey(), filterCriteria.getRelationType());
        return criteriaBuilder.lessThan(path.as(comparableFilterCriteria.getClazz()), comparableFilterCriteria.getValue());
    }

    private Predicate lessThanOrEqualToPredicate(Root<E> root, CriteriaBuilder criteriaBuilder, FilterCriteria<T> filterCriteria) {
        final ComparableFilterCriteria<C> comparableFilterCriteria = getComparableFilterCriteria();
        final Path<?> path = resolvePath(root, filterCriteria.getKey(), filterCriteria.getRelationType());
        return criteriaBuilder.lessThanOrEqualTo(path.as(comparableFilterCriteria.getClazz()), comparableFilterCriteria.getValue());
    }

    private ComparableFilterCriteria<C> getComparableFilterCriteria() {
        if (this.filterCriteria instanceof ComparableFilterCriteria) {
            return (ComparableFilterCriteria) filterCriteria;
        }
        throw new ClassCastException();
    }

    private Path<?> resolvePath(Root<E> root, String key, RelationType relationType) {
        if (relationType.equals(RelationType.NO_RELATION)) {
            return root.get(key);
        } else if (relationType.equals(RelationType.TO_ONE)) {
            final String[] columns = key.split("\\.");

            if (columns.length == 1) {
                return root.get(key);
            }
            // throw exception if columns less than or equal to 1
            final Join<T, ?> joinedTable = root.join(columns[0], JoinType.LEFT);
            Path<Object> path = joinedTable.get(columns[1]);
            for (int i = 2; i < columns.length; i++) {
                path = path.get(columns[i]);
            }
            return path;
        } else {
            final String[] columns = key.split("\\.");
            final Join<T, ?> joinedTable = root.join(columns[0], JoinType.LEFT);
            return joinedTable.get(columns[1]);
        }
    }

}
