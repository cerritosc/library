package com.focusservices.library.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import com.focusservices.library.commons.ServiceResponse;
import com.focusservices.library.domain.User;
import com.focusservices.library.commons.datatables.mapping.DataTablesInput;
import com.focusservices.library.commons.datatables.mapping.DataTablesOutput;

public interface UserService {
    Optional<User> findById(Integer id);
    ServiceResponse saveValidated(User user);
    ServiceResponse delete(Integer id);
    List<User> findAll();
    Optional<User> findByMail(String mail);
	
	// metodos para obtener data como lista
    Slice<User> getList(Integer page, Integer rows);
    Slice<User> getListByQ(String q, Pageable page);
	
    // metodos para DatatTable
    DataTablesOutput<User> findAll(DataTablesInput input);
}
