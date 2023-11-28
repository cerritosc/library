package com.focusservices.library.versionamiento;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.apache.commons.beanutils.PropertyUtils;
import org.javers.core.commit.CommitId;
import org.javers.core.metamodel.annotation.TypeName;
import com.focusservices.library.commons.datatables.mapping.DataTablesInput;
import com.focusservices.library.commons.datatables.mapping.DataTablesOutput;

/** Utileria del versionamiento. Son varios metodos que han sido dejado
 *  aqui para el reuso.
 *
 * @author Vansi Olivares
 */
public class VersionamientoUtils {
    private static final Pattern PATTERN_COLUMNA_PREFIJO = Pattern.compile("^e[0-9]-.*");
    
    private VersionamientoUtils() {
        
    }

    /** Obtiene el campo de la clase marcado con la anotacion {@link jakarta.persistence.Id}.
     *  Este metodo solo tiene soporte para entidades con un campo id.
     * 
     * @param clase la clase a examinar
     * @return el campo marcado como id o una excepcion si no existe.
     * @see jakarta.persistence.Id
     */
    public static String getIdTabla(Class<?> clase) {
        Field[] campos = clase.getDeclaredFields();
        return Stream.of(campos)
                .filter(campo -> campo.getAnnotation(Id.class) != null)
                .map(Field::getName)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("La siguiente clase no posee campo id: " + clase));
    }

    /** Obtiene el nombre del campo id de la entidad tal cual como se encuentra en la base de datos.
     *  Este metodo solo tiene soporte para entidades con un campo id.
     * 
     * @param clase la clase a examinar
     * @return el nombre del campo en la base de datos o una excepcion si no se pudo encontrar.
     */
    public static String getIdTablaFisica(Class<?> clase) {
        Field[] campos = clase.getDeclaredFields();
        Field campoId = Stream.of(campos)
                .filter(campo -> campo.getAnnotation(Id.class) != null)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("La siguiente clase no posee campo id: " + clase));

        Column columna = campoId.getAnnotation(Column.class);
        return columna != null ? columna.name() : campoId.getName();
    }

    /** Obtiene el nombre de la tabla fisica tal cual se encuentra en la base de datos, ya sea
     *  extrayendolo de la notacion {@link jakarta.persistence.Table} o el nombre de la entidad
     *  si esta anotacion no fue usada.
     * 
     * @param clase la clase a examinar
     * @return el nombre de la tabla en la base de datos de esta entidad.
     * @see jakarta.persistence.Table
     */
    public static String getNombreTablaFisica(Class<?> clase) {
        Table tabla = clase.getAnnotation(Table.class);
        return tabla != null ? tabla.name() : clase.getSimpleName();
    }

    /** Obtiene el valor de una propiedad de un objeto dado.
     *  Este metodo seria el equivalente de llamar el metodo getPropiedad() del valor.
     * 
     * @param valor el valor a obtener su propiedad
     * @param propiedad la propiedad que debe ser obtenida
     * @return el valor de la propiedad para el objeto enviado
     */
    public static Object getValorDePropiedad(Object valor, String propiedad) {
        if(valor == null) {
            throw new IllegalArgumentException("Error al obtener la propiedad " 
                    + propiedad 
                    + ": se ha enviado un valor nulo");
        }
        try {
            return PropertyUtils.getProperty(valor, propiedad);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Ocurrio un error al obtener la propiedad "
                    + propiedad
                    + " del objeto: "
                    + valor.getClass().getCanonicalName() ,
                     ex);
        }
    }
    
    /** Crea una respuesta de DataTablesOutput en base al total de registro (QueryResult)
     *  y lo solicitado por el cliente (DataTablesInput).
     * 
     * @param <T> el tipo de DataTablesOutput deseado
     * @param resultado el listado de objetos a enviar junto con su conteo total
     * @param input los filtros que el usuario envio
     * @return una respuesta correctamente formateada para DataTables
     */
    public static <T> DataTablesOutput<T> crearDataTablesOutput(QueryResult<List<T>> resultado, DataTablesInput input) {
        DataTablesOutput<T> output = new DataTablesOutput<>();
        output.setDraw(input.getDraw());
        output.setData(resultado.getValue());
        output.setRecordsFiltered(resultado.getCount());
        output.setRecordsTotal(resultado.getCount());
        return output;
    }
    
    /** Transforma un commitId, que es un objeto interno que representa un id universal de una transaccion
     *  a la clase BigInteger.
     * 
     * @param commitId el commitId a transformar
     * @return el commitId pero como BigInteger.
     */
    public static BigInteger commitIdToBigInteger(CommitId commitId) {
        return commitId
                    .valueAsNumber()
                    .setScale(0)
                    .toBigInteger();
    }
    
    /** Mapea de forma adecuada los registros al cliente.
     *  Puede ser que el valor final quiera ser pre-procesado a nivel de clase,
     *  por lo que se formatea aqui.
     * 
     * @param valor
     * @return 
     */
    public static Object transformarValor(Object valor) {
        if(valor == null) {
            return "";
        }
        else if(valor instanceof LocalDate) {
            LocalDate valorLocalDate = (LocalDate) valor;
            return valorLocalDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
        else if(valor instanceof LocalDateTime) {
            LocalDateTime valorLocalDateTime = (LocalDateTime) valor;
            return valorLocalDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        }
        else return valor;
    }
    
    /** Indica si la cadena enviad es o pertenece a una entidad, tal como la clase
     *  MetadataTablaVersionada las define: prefijo + "-" + nombreColumna.
     * 
     * @param prueba la cadena a probar
     * @return true si cumple con lo anterior, falso si no.
     */
    public static boolean isColumnaDeEntidad(String prueba) {
        return PATTERN_COLUMNA_PREFIJO.matcher(prueba)
                .matches();
    }
    
    public static String getNombreVersionamiento(Class<?> clase) {
        TypeName tipo = clase.getAnnotation(TypeName.class);
        if(tipo != null) {
            return tipo.value();
        }
        else return clase.getCanonicalName();
    }
    
    public static BigInteger bigDecimalIdToBigInteger(BigDecimal commitId) {
        return commitId
                    .setScale(0)
                    .toBigInteger();
    }
}
