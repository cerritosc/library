package com.focusservices.library.commons.datatables.specificationbuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

@SuppressWarnings({"rawtypes", "unchecked"})
public class GenericSpecificationContainer<E> implements Specification<E> {

	private final Specification specification;

	private final List<Map<String, String>> parameters;

	GenericSpecificationContainer(Specification specification, List<Map<String, String>> parameters) {
		this.specification = specification;
		this.parameters = Collections.unmodifiableList(parameters);
	}

	GenericSpecificationContainer(Specification specification) {
		this.specification = specification;
		this.parameters = Collections.emptyList();
	}

	public static GenericSpecificationContainer where(Specification specification) {
		return new GenericSpecificationContainer(specification);
	}

	public GenericSpecificationContainer<E> or(GenericSpecificationContainer<E> spec) {
		final ArrayList<Map<String, String>> parameterList = new ArrayList<>(this.parameters);
		parameterList.addAll(spec.parameters);
		return new GenericSpecificationContainer<>(Specification.where(this.specification).or(spec), parameterList);
	}

	public GenericSpecificationContainer<E> and(GenericSpecificationContainer<E> spec) {
		final ArrayList<Map<String, String>> parameterList = new ArrayList<>(this.parameters);
		parameterList.addAll(spec.parameters);
		return new GenericSpecificationContainer<>(Specification.where(this.specification).and(spec), parameterList);
	}

	@Override
	public Predicate toPredicate(Root<E> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
		return this.specification.toPredicate(root, query, criteriaBuilder);
	}

	public boolean hasParameters() {
		return !parameters.isEmpty();
	}

	public List<Map<String, String>> getParameters() {
		return parameters;
	}
}
