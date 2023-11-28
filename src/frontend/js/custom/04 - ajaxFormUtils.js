/* Fill Form dinamico desde servidor, utiliza objeto tipo entidad y formulario a llenar. */
window.fillForm = function($form, data, soloData, resetearData = true) {
    if (resetearData) {
        if (soloData) {
            resetFormData($form);
        } else {
            resetForm($form);
        }
    }

    function crearItem(key, valor, etiqueta) {
        var option = new Option(valor, etiqueta, true, true);
        $("#" + key).append(option).trigger('change');
    }

    $.each(data, function (key, value) {
        var $ctrl = $form.find('[id=' + key + ']');
        if ($ctrl.is('select')) {
            if ($ctrl.hasClass("select2-hidden-accessible")) {
                $("#" + key).empty().val("").trigger('change');

                let slicedVal = value.split("|");
                if (slicedVal[1] !== "null") {
                    if (slicedVal[1].includes(',')) {
                        var valores = slicedVal[1].split(",");
                        var etiquetas = slicedVal[0].split(",");
                        for (var i in valores) {
                            crearItem(key, valores[i].trim(), etiquetas[i].trim());
                        }
                    } else {
                        crearItem(key, slicedVal[1], slicedVal[0]);
                    }
                }
            } else {
                $ctrl.val(value);
            }
        } else if ($ctrl.is('textarea')) {
            $ctrl.val(value);
        } else {
            switch ($ctrl.attr("type")) {
                case "text":
                    $ctrl.val(value);
                    break;
                case "number":
                    $ctrl.val(value);
                    break;
                case "hidden":
                    $ctrl.val(value);
                    break;
                case "checkbox":
                    if (value == '1')
                        $ctrl.prop('checked', true);
                    else
                        $ctrl.prop('checked', false);
                    break;
                case "radio":
                    $('input:radio[name="' + key + '"][value="' + value + '"]').prop('checked', true);
                    break;
                case "email":
                    $ctrl.val(value);
                    break;
            }
        }
    });
};

/* Reset Form, reset para todos los tipos de inputs, select2 y las validaciones */
window.resetForm = function($form) {
    $form.find('input:text, input:password, input:file, input[type=number], input:hidden, select, textarea')
        .not("[persist], input[name='_csrf'], input:radio")
        .val('')
        .attr('readonly', false);
    $form.find('input:radio, input:checkbox').prop('checked', false).removeAttr('selected').attr('readonly', false).attr('disabled', false);
    $form.find(".select2").attr('readonly', false).attr('disabled', false).trigger("change");
    $form.removeClass('was-validated');

    Validador.resetValidacion($form);
}

/* Reset Form, reset para todos los tipos de inputs, select2 y las validaciones */
window.resetFormData = function($form) {
    $form.find('input:text, input:password, input:file, input[type=number], input:hidden, select, textarea')
        .not("[persist], input[name='_csrf'], input:radio")
        .val('');
    $form.find('input:radio, input:checkbox').prop('checked', false).removeAttr('selected');
    $form.find(".select2").trigger("change");
    $form.removeClass('was-validated');

    Validador.resetValidacion($form);
}

/** Funcionalidad que deshabilita todos los inputs de un formulario
 *
 */
window.deshabilitarForm = function($form) {
    $form.find('input, select, textarea').prop('disabled', true);
}

window.habilitarForm = function($form) {
    $form.find('input, select, textarea').prop('disabled', false);
}

window.aplicarMensajeError = function($form) {
    $form.find('input, select, textarea')
            .not("input[type=hidden]")
            .each(function(id, valor) {
                // obtengo el campo 'data-mensaje-error'. Si no existe se pone 'campo requerido'
                var mensajeError = valor.dataset.mensajeError ? valor.dataset.mensajeError : "Campo requerido";
                valor.oninvalid = function() {
                    this.setCustomValidity(mensajeError);
                };
                
                // se remueve el mensaje ya que da error si no se hace esto
                var onChangeViejo = valor.onchange ? valor.onchange : function(){};
                valor.onchange = function(a, b, c) {
                    this.setCustomValidity("");
                    onChangeViejo(a, b, c);
                };
    });
}

window.showConfirmMessage = function(message, callbackConfirm, callbackCancel) {
    Swal.fire({
        title: "¿Está seguro?",
        text: message,
        type: "warning",
        showCancelButton: true,
        confirmButtonColor: "#3085d6",
        cancelButtonColor: '#d33',
        confirmButtonText: "Sí",
        cancelButtonText: "No"
    }).then(function (e) {
        if (e.value === true) {
            isFunction(callbackConfirm) ? callbackConfirm() : null;
        } else {
            isFunction(callbackCancel) ? callbackCancel() : null;
        }
    });

}

window.ajaxPostCall = function(url, data, messageSuccess, successCallback) {
    $.ajax({
        data: data,
        url: url,
        type: 'POST',
        success: function (result) {
            if (result.success === true) {
                Swal.fire({title: messageSuccess, type: 'success'});
                isFunction(successCallback) ? successCallback(result) : null;
            } else {
                Swal.fire({title: "¡Error!", text: result.message, type: 'error'});
            }
        },
        error: function (x, e, thrownError) {
            Swal.fire({title: "¡Error!", text: "Error interno del servidor.", type: 'error'});
        }
    });
}

window.ajaxGetCall = function(url, data, successCallback) {
    $.ajax({
        data: data,
        url: url,
        type: 'GET',
        success: function (result) {
            isFunction(successCallback) ? successCallback(result) : null;
        },
        error: function (x, e, thrownError) {
            Swal.fire({title: "¡Error!", text: "Error interno del servidor.", type: 'error'});
        }
    });
}

window.isFunction = function(functionToCheck) {
    return functionToCheck && {}.toString.call(functionToCheck) === '[object Function]';
}