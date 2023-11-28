package com.focusservices.library.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.focusservices.library.commons.datatables.repository.DataTablesRepository;
import com.focusservices.library.domain.SecPrivilege;

//@JaversSpringDataAuditable
public interface SecPrivilegeRepository extends DataTablesRepository<SecPrivilege, Integer> {
   
	Optional<SecPrivilege> findBySkPrivilege(Integer skPrivilege);
	
        @Query(value = "select p from SecPrivilege p where (p.cdprivilege + ' - ' + p.stdescription) like '%' + :query + '%'")
        Slice<SecPrivilege> findByCodigoYNombrePrivilegio(@Param("query") String query, Pageable page);
        
        @Query(value = "select p from SecPrivilege p where p.cdprivilege like 'OPG_%' and (p.cdprivilege + ' - ' + p.stdescription) like '%' + :query + '%'")
        Slice<SecPrivilege> findPadresByCodigoYNombrePrivilegio(@Param("query") String q, Pageable page);

        @Query("select ph from SecPrivilege p join p.secPrivilegeHijos ph where p.skPrivilege = :skPrivilege")
        List<SecPrivilege> findHijosBySkPrivilege(@Param("skPrivilege") Integer skPrivilege);
}
