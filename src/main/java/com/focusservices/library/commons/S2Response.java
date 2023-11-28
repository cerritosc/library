package com.focusservices.library.commons;

import java.io.Serializable;
import java.util.List;

public class S2Response<T extends Serializable> {
    private List<T> results;

    private Pagination pagination;

    public S2Response() {
    }

    public S2Response(List<T> results, Boolean pagination) {
        this.results = results;
        this.pagination = new Pagination(pagination);
    }

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    public class Pagination {

        private Boolean more;

        public Pagination() {
        }

        public Pagination(Boolean more) {
            this.more = more;
        }

        public Boolean getMore() {
            return more;
        }

        public void setMore(Boolean more) {
            this.more = more;
        }

    }
}
