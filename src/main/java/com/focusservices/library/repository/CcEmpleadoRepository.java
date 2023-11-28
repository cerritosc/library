package com.focusservices.library.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.focusservices.library.commons.datatables.repository.DataTablesRepository;
import com.focusservices.library.domain.CcEmpleado;
import com.focusservices.library.domain.CcRol;

//@JaversSpringDataAuditable
public interface CcEmpleadoRepository extends DataTablesRepository<CcEmpleado, Integer> {
   
	Optional<CcEmpleado> findBySkEmpleado(Integer skEmpleado);
	
	Slice<CcEmpleado> findByStNombreCompletoIgnoreCaseContaining(String q, Pageable page);

        @Query("select e from CcEmpleado e left join e.ccUsuarioes u where (u.skUsuario is null or e.skEmpleado = :skEmpleado) and e.stNombreCompleto like CONCAT(CONCAT('%', :query), '%')")
        Slice<CcEmpleado> findEmpleadoSinUsuario(@Param("skEmpleado") Integer skEmpleado, @Param("query") String query, Pageable pagina);
        
        @Query("select e from CcEmpleado e left join e.ccRol r where r.cdRol = :cdRol and e.bnEstado = 1")
        Slice<CcEmpleado> findEmpleadobyRol(@Param("cdRol") String cdRol, Pageable pagina);
        
         @Query("SELECT x FROM CcUsuario u join u.ccEmpleado x WHERE x.ccRol.cdRol='BACK_OFFICE' and x.bnEstado=true")
		Slice<CcEmpleado> findEmpleadosBackOfficeActivos(String q, Pageable pagina);
        
        @Query("SELECT x.ccRol FROM CcEmpleado x WHERE x.skEmpleado=:skEmpleado")
        Optional<CcRol> getRolEmpleado(Integer skEmpleado);
        
         @Query(value =" SELECT CONCAT(E.ST_NOMBRES,  ' ' , E.ST_APELLIDOS)  FROM CC_EMPLEADO E WHERE E.SK_EMPLEADO = ("
                 + " SELECT DISTINCT U.FK_EMPLEADO FROM CC_USUARIO U WHERE LOWER(U.CD_USUARIO) = :cdUsuario)", nativeQuery = true)
        public String findEmpleadoAsignado(@Param("cdUsuario") String cdUsuario);
        
        //AGREGANDO SIN ASIGNACION 
        
         @Query(value =" SELECT distinct  '-1' AS USUARIO, 'Sin Asignación'  AS NOMBRES from CC_EMPLEADO  " + 
                 " UNION " + 
                 " select LOWER(U.CD_USUARIO) AS USUARIO , concat(C.ST_NOMBRES ,' ' , C.ST_APELLIDOS) as NOMBRES  from CC_EMPLEADO C ,  " +
                  " CC_USUARIO U WHERE  U.FK_EMPLEADO = C.SK_EMPLEADO AND  "
                 + " C.FK_ROL = ( select DISTINCT  SK_ROL from CC_ROL where upper(cd_rol) = 'BACK_OFFICE') AND C.BN_ESTADO = 1 ", nativeQuery = true)
        public List<String[]> findEmpleadosBackOffice();
        
        
         @Query(value =" select distinct SK_EMPLEADO , concat(ST_NOMBRES, ' ' , ST_APELLIDOS) as nombre from CC_EMPLEADO where sk_empleado in (select distinct " + 
                 "  FK_EMPLEADO_ASIGNADO from GS_ORDEN_TRABAJO_ASIG   where FK_EMPLEADO_ASIGNA = :skEmpleado) "  , nativeQuery = true)
         public List<String[]> findEmpleadosOrdenTrabajo(@Param("skEmpleado") Integer skEmpleado );
        

         @Query(value =" SELECT distinct SK_EMPLEADO , concat(ST_NOMBRES, ' ' , ST_APELLIDOS) as nombre from CC_EMPLEADO  WHERE FK_SUPERVISOR = :skEmpleado"
                , nativeQuery = true)
        public List<String[]> findEmpleadoBySupervisor(@Param("skEmpleado") Integer skEmpleado);
        
         
}
