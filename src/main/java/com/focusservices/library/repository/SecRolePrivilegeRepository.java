package com.focusservices.library.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.focusservices.library.commons.datatables.repository.DataTablesRepository;
import com.focusservices.library.domain.SecRolePrivilege;

//@JaversSpringDataAuditable
public interface SecRolePrivilegeRepository extends DataTablesRepository<SecRolePrivilege, Integer> {
   
	Optional<SecRolePrivilege> findBySkRolePrivilege(Integer skRolePrivilege);
	
	Slice<SecRolePrivilege> findBySkRolePrivilegeIgnoreCaseContaining(String q, Pageable page);

        @Query("select rp from SecRolePrivilege rp join rp.secPrivilege p join rp.ccRol r "
                + "where r.skRol = :skRol and p.skPrivilege = :skPrivilege")
        Optional<SecRolePrivilege> findBySkRolAndSkPrivilege(@Param("skRol") Integer skRol, @Param("skPrivilege") Integer skPrivilege);
        
        @Query("select rp from SecPrivilege p join p.secRolePrivilegees rp join rp.ccRol r "
                + "where r.skRol = :skRol and p.skPrivilege in :privilegiosHijos")
        List<SecRolePrivilege> findPrivilegiosAsignados(@Param("skRol") Integer skRol, @Param("privilegiosHijos") Set<Integer> privilegiosHijos);
}
