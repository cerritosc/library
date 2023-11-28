package com.focusservices.library.repository;

import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.focusservices.library.commons.datatables.repository.DataTablesRepository;
import com.focusservices.library.domain.CcUsuario;

//@JaversSpringDataAuditable
public interface CcUsuarioRepository extends DataTablesRepository<CcUsuario, Integer> {
   
	Optional<CcUsuario> findBySkUsuario(Integer skUsuario);
	
	Slice<CcUsuario> findBySkUsuarioIgnoreCaseContaining(String q, Pageable page);

	Slice<CcUsuario> findByCdUsuarioIgnoreCaseContaining(String q, Pageable page);
	
	@Query(value="SELECT "
			+ "	CASE cr.CD_ROL  "
				+ "	WHEN 'COORDINADOR' THEN (SELECT COUNT(1) FROM GS_SOLICITUD gs WHERE CONVERT(DATE, GETDATE()) = CONVERT (DATE, gs.FC_CREA_FECHA)) "
				+ "	ELSE (SELECT COUNT(1) FROM GS_SOLICITUD gs WHERE gs.ST_CREA_USUARIO = :usuario AND CONVERT(DATE, GETDATE()) = CONVERT (DATE, gs.FC_CREA_FECHA)) "
			+ "	END AS COUNT_LLAMADAS "
			+ "FROM CC_USUARIO cu  "
			+ "	INNER JOIN CC_EMPLEADO ce ON ce.SK_EMPLEADO = cu.FK_EMPLEADO  "
			+ "	INNER JOIN CC_ROL cr ON cr.SK_ROL = ce.FK_ROL  "
			+ "WHERE CU.CD_USUARIO = :usuario ",nativeQuery = true)
	Integer getLlamadasHoy(@Param("usuario") String usuario);
	
	@Query(value="SELECT  "
			+ "	CASE cr.CD_ROL  "
				+ "	WHEN 'COORDINADOR' THEN ( "
										+ "	SELECT COUNT(1)  "
										+ "FROM GS_SOLICITUD gs  "
										+ "INNER JOIN CC_TIPO_TRAMITE ctt ON gs.FK_TIPO_TRAMITE = ctt.SK_TIPO_TRAMITE  "
										+ "WHERE CONVERT(DATE, GETDATE()) = CONVERT (DATE, gs.FC_CREA_FECHA) "
										+ "AND ctt.CD_TIPO_TRAMITE = :tipoTramite "
										+ "	) "
				+ "	ELSE ( "
					+ "	SELECT COUNT(1)  "
					+ "	FROM GS_SOLICITUD gs "
					+ "	INNER JOIN CC_TIPO_TRAMITE ctt ON gs.FK_TIPO_TRAMITE = ctt.SK_TIPO_TRAMITE  "
					+ "	WHERE gs.ST_CREA_USUARIO = :usuario AND CONVERT(DATE, GETDATE()) = CONVERT (DATE, gs.FC_CREA_FECHA) "
					+ "	AND ctt.CD_TIPO_TRAMITE = :tipoTramite "
					+ "	) "
			+ "	END AS COUNT_LLAMADAS "
			+ "FROM CC_USUARIO cu  "
			+ "	INNER JOIN CC_EMPLEADO ce ON ce.SK_EMPLEADO = cu.FK_EMPLEADO  "
			+ "	INNER JOIN CC_ROL cr ON cr.SK_ROL = ce.FK_ROL  "
			+ "WHERE CU.CD_USUARIO = :usuario ",nativeQuery = true)
	Integer getLlamadasHoyByTipoTramite(@Param("usuario") String usuario, @Param("tipoTramite") String tipoTramite);

	@Query(value = "SELECT a FROM CcUsuario a WHERE a.ccEmpleado.ccRol.cdRol = 'COORDINADOR'")
	Slice<CcUsuario> findByCoord();

	@Query(value = "SELECT a FROM CcUsuario a WHERE a.ccEmpleado.ccRol.cdRol = 'COORDINADOR'" +
			" and lower (CONCAT(a.ccEmpleado.stNombres, ' ', a.ccEmpleado.stApellidos)) like lower(concat('%', :nombre, '%'))")
	Slice<CcUsuario> findByCoordByQ(String nombre);
	
	@Query(value = "SELECT CD_ROL FROM CC_ROL WHERE SK_ROL IN( " +
			"SELECT FK_ROL FROM CC_EMPLEADO WHERE SK_EMPLEADO IN ( SELECT FK_EMPLEADO FROM CC_USUARIO WHERE CD_USUARIO = ?1)) ",nativeQuery = true)
	String findByUser(String user);

	CcUsuario findCcUsuarioByCdUsuario(String cdUsuario);
}
