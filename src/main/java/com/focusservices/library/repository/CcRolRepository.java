package com.focusservices.library.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.focusservices.library.commons.datatables.repository.DataTablesRepository;
import com.focusservices.library.domain.CcRol;
import com.focusservices.library.domain.SecPrivilege;

//@JaversSpringDataAuditable
public interface CcRolRepository extends DataTablesRepository<CcRol, Integer> {
   
	Optional<CcRol> findBySkRol(Integer skRol);
	
        Slice<CcRol> findByStDescripcionIgnoreCaseContaining(String q, Pageable page);
        
        @Query("select p from CcRol r join r.secRolePrivilegees rp join rp.secPrivilege p where r.skRol = :skRol")
        List<SecPrivilege> getPrivilegiosDeRol(@Param("skRol") Integer skRol);
}
