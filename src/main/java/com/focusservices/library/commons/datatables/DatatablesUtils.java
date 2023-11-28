package com.focusservices.library.commons.datatables;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import com.focusservices.library.commons.datatables.mapping.Column;
import com.focusservices.library.commons.datatables.mapping.DataTablesInput;

public class DatatablesUtils {
    
    /**
     * Creates a 'LIMIT ..OFFSET .. ORDER BY ..' clause for the given {@link DataTablesInput}.
     *
     * @param input el input del grid
     * @return a {@link Pageable}, must not be {@literal null}.
     */
    public static Pageable createPageable(DataTablesInput input) {
        List<Sort.Order> orders = new ArrayList<>();
        for (com.focusservices.library.commons.datatables.mapping.Order order : input.getOrder()) {
            Column column = input.getColumns().get(order.getColumn());
            if (column.getOrderable()) {
                String sortColumn = column.getData();
                Sort.Direction sortDirection = Sort.Direction.fromString(order.getDir());
                orders.add(new Sort.Order(sortDirection, sortColumn));
            }
        }
        Sort sort = orders.isEmpty() ? Sort.unsorted() : Sort.by(orders);

        if (input.getLength() == -1) {
            input.setStart(0);
            input.setLength(Integer.MAX_VALUE);
        }
        return new DataTablesPageRequest(input.getStart(), input.getLength(), sort);
    }

    private static class DataTablesPageRequest implements Pageable {
        private final int offset;
        private final int pageSize;
        private final Sort sort;

        DataTablesPageRequest(int offset, int pageSize, Sort sort) {
            this.offset = offset;
            this.pageSize = pageSize;
            this.sort = sort;
        }

        @Override
        public long getOffset() {
            return offset;
        }

        @Override
        public int getPageSize() {
            return pageSize;
        }

        @Override
        @NonNull
        public Sort getSort() {
            return sort;
        }

        @Override
        @NonNull
        public Pageable next() {
            throw new UnsupportedOperationException();
        }

        @Override
        @NonNull
        public Pageable previousOrFirst() {
            throw new UnsupportedOperationException();
        }

        @Override
        @NonNull
        public Pageable first() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean hasPrevious() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getPageNumber() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Pageable withPage(int pageNumber) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

    }
}
