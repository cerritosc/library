package com.focusservices.library.commons;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JsGridParams {
	Integer pageIndex;
	Integer pageSize;
	String sortOrder;
	String sortField;

	public boolean isSorted() {
		return this.getSortOrder() != null && this.getSortField() != null;
	}
}
