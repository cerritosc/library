package com.focusservices.library.repository.dto;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.focusservices.library.commons.datatables.repository.DataTablesRepository;
import com.focusservices.library.dto.SecPrivilegePadreDto;

//@JaversSpringDataAuditable
public interface SecPrivilegePadreDtoRepository extends DataTablesRepository<SecPrivilegePadreDto, String> {
   
        @Query(value = "select pp from SecPrivilegePadreDto pp where pp.skRol = :skRol order by pp.codigoOrden")
	Slice<SecPrivilegePadreDto> findBySkRol(@Param("skRol") Integer skRol, Pageable page);

        @Query(value = "select pp.skPrivilege from SecPrivilegePadreDto pp where pp.skRol = :skRol and pp.skPrivilegePadre = :skPrivilegePadre and pp.skRolePrivilege is null")
        List<Integer> findPrivilegiosFaltantes(@Param("skRol")Integer skRol, @Param("skPrivilegePadre") Integer skPrivilegePadre);
}
