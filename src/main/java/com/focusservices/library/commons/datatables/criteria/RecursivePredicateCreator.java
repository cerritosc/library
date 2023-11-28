package com.focusservices.library.commons.datatables.criteria;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.thymeleaf.util.StringUtils;
import com.focusservices.library.commons.datatables.mapping.Column;

public class RecursivePredicateCreator<T> {
    private final Class<T> claseEntidad;
    private final Map<TuplaClases<?, ?>, EntradaCacheJoins> cacheEntradas;

    public RecursivePredicateCreator(Class<T> claseEntidad) {
        this.claseEntidad = claseEntidad;
        this.cacheEntradas = new HashMap<>();
    }
    
    public Optional<Predicate> crearPredicateDeColumna(From<?, ?> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder, Column filtro) throws SecurityException, NoSuchFieldException {
        String nombrePropiedad = filtro.getData();
        if(StringUtils.contains(nombrePropiedad, ".")) {
            // si he llegado aqui, significa que esta es una entidad el campo
            // por lo que debo hacer un join a este
            String nombreCampo = StringUtils.substringBefore(nombrePropiedad, ".");
            String propiedadCampo = StringUtils.substringAfter(nombrePropiedad, ".");
            
            // solo si el campo existe se procede
            if(!existeCampoEnEntidad(nombreCampo)) {
                return Optional.empty();
            }
            
            Class<?> claseCampo = getClaseCampo(nombreCampo);
            if(RecursivePredicateCreator.isEntity(claseCampo)) {
                // creo una nueva regla con los parametros ajustados
                Column filtroJoin = new Column();
                filtroJoin.setData(propiedadCampo);
                filtroJoin.setName(filtro.getName());
                filtroJoin.setOrderable(filtro.getOrderable());
                filtroJoin.setSearch(filtro.getSearch());
                filtroJoin.setSearchable(filtro.getSearchable());
                
                /** Verifico si la entrada esta en una cache de joins, de otra manera se haran otras por gusto
                 */
                TuplaClases<T, ?> tuplaClases = new TuplaClases<>(claseEntidad, claseCampo);
                EntradaCacheJoins entradaCache = cacheEntradas.get(tuplaClases);
                if(entradaCache == null) {
                    Join<?, T> joinCreado = root.join(nombreCampo);
                    RecursivePredicateCreator<?> recursivePredicateCreator = new RecursivePredicateCreator<>(claseCampo);
                    entradaCache = new EntradaCacheJoins(joinCreado, query, recursivePredicateCreator);
                    
                    cacheEntradas.put(tuplaClases, entradaCache);
                }
                
                return entradaCache.crearPredicate(criteriaBuilder, filtroJoin);
            }
            else {
                throw new UnsupportedOperationException("No se puede consultar esta propiedad ya que no es una entity: " + nombreCampo + ", clase encontrada: " + claseCampo);
            }
        }
        else {
            // solo si el campo existe se procede
            if(!existeCampoEnEntidad(nombrePropiedad)) {
                return Optional.empty();
            }
            
            // si no es entity solo hago el predicate y ya
            Class<?> claseField = getClaseCampo(nombrePropiedad);
            // por el momento quedan fijos los filtros
            FilterToPredicate filtros = FiltrosPorDefecto.getFiltroParaField(claseField, FiltrosPorDefecto.PARAMETRO_POR_DEFECTO);
            // creo el filtro a usar
            CriteriaFilter criteriaFilter = new CriteriaFilter(nombrePropiedad, claseField, filtro.getSearch().getValue());
            // ejecuto el filtro
            return Optional.of(filtros.toPredicate(root, criteriaBuilder, criteriaFilter));
        }
    }
    
    private boolean existeCampoEnEntidad(String nombreCampo) {
        try {
            Field campoEnEntidad = claseEntidad.getDeclaredField(nombreCampo);
            return true;
        } catch (NoSuchFieldException ex) {
            return false;
        } catch (SecurityException ex) {
            return false;
        }
    }

    private Class<?> getClaseCampo(String nombreCampo) throws NoSuchFieldException, SecurityException {
        Field campoEnEntidad = claseEntidad.getDeclaredField(nombreCampo);
        // se bifurca que si es entidad oneToMany se trae el tipo generico declarado en el campo.
        if(isRelacionOneToMany(campoEnEntidad)) {
            ParameterizedType stringListType = (ParameterizedType) campoEnEntidad.getGenericType();
            return (Class<?>) stringListType.getActualTypeArguments()[0];
        }
        // sino solo obtengo la clase del campo declarado
        else {
            Class<?> claseCampo = campoEnEntidad.getType();
            return claseCampo;
        }
    }
    
    private static <T> boolean isRelacionOneToMany(Field campo) {
        return campo.getAnnotation(OneToMany.class) != null;
    }
    
    public static <T> boolean isEntity(Class<T> clase1) {
        return clase1.getAnnotation(Entity.class) != null;
    }

    
}
