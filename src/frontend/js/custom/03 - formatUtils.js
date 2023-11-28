/** Función para obtener el NIT con formato para mostrar en pantallas
 *
 * @param nit
 * @return nit con formato 0000-000000-000-0
 */
window.formatNIT = function(nit){
    var s1 = nit.substring(0, 4);
    var s2 = nit.substring(4, 10);
    var s3 = nit.substring(10, 13);
    var s4 = nit.substring(13);
    return s1 + "-" + s2 + "-" + s3 + "-" + s4;
}