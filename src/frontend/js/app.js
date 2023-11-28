/** Algunas librerias no son importadas aqui sino que son declaradas como nombres globales.
 *  Si se requiere un nombre global agregar la dependencia en el plugin:
 * 
 *  		Webpack.ProvidePlugin
 * 
 *  Todo lo demas debe ser agregado aqui. La mayoria de librerias puede ser importado usando simplemente:
 * 
 *       import 'libreria';
 * 
 *  Las librerias asumen que fueron instaladas usando node y que estan en la carpeta 'node-modules'. Para instalar una libreria use:
 * 
 *      node install libreria
 * 
 *  En algunas librerias se debe especificar los archivos especificos a importar. En esos casos se debe importar asi:
 * 
 *      import "libreria/js/mi-libreria.js"
 * 
 * 
 *  Para compilar el proyecto ejecute:
 *      
 *      npx webpack
 **/

// librerias
import "script-loader/addScript.js"
import "bootstrap";
import "bootstrap-fileinput";
import 'bootstrap-daterangepicker/daterangepicker.js'
import 'bootstrap-datepicker/js/bootstrap-datepicker';
import 'bootstrap-datepicker/js/locales/bootstrap-datepicker.es';
import 'bootstrap-duallistbox/dist/jquery.bootstrap-duallistbox.js';
import 'jquery-mask-plugin/dist/jquery.mask.min';
import 'jquery-validation'
import 'gasparesganga-jquery-loading-overlay/dist/loadingoverlay.js'
import "inputmask/dist/jquery.inputmask.js"
import "inputmask/dist/bindings/inputmask.binding.js"

import 'select2/dist/js/select2.min.js';
import 'select2/dist/js/i18n/es';

import DataTable from 'datatables.net';
global.DataTable = DataTable;

import 'datatables.net-bs4';
import 'datatables.net-select';
import 'datatables.net-select-bs4';
import 'datatables.net-responsive';

import moment from "moment";
global.moment = moment;

import Swal from 'sweetalert2/dist/sweetalert2.all.min';
global.Swal = Swal;


// importacion de archivos de template
import "./template/custom-template.js"

// importacion de archivos personalizados
import "./custom/01 - datatable.utils.js"
import "./custom/02 - formUtils.js"
import "./custom/03 - formatUtils.js"
import "./custom/04 - ajaxFormUtils.js"
import "./custom/05 - ajaxSend.js"
import "./custom/06 - select2Implementation.js"
import "./custom/07 - jquery.spring-friendly.js"