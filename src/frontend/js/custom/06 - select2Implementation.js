window.renderSelect2 = function(selector, urlsegment, rows) {
    $(selector)
        .wrap('<div style="position:relative"></div>')
        .select2({
            placeholder: 'Buscar y seleccionar',
            language: "es",
            dropdownParent: $(selector).parent(),
            width: '100%',
            minimumInputLength: 0,
            enableOnReadonly: false,
            ajax: {
                url: urlsegment,
                dataType: 'json',
                data: function (params) {
                    return {
                        q: params.term, // search term
                        page: params.page || 1,
                        rows: rows
                    };
                },
                cache: true
            }
        });
}
window.renderSelect2NoSearch = function(selector, urlsegment) {
    $(selector)
        .wrap('<div style="position:relative"></div>')
        .select2({
            placeholder: 'Seleccionar',
            language: "es",
            dropdownParent: $(selector).parent(),
            width: '100%',
            minimumResultsForSearch: Infinity,
            enableOnReadonly: false,
            ajax: {
                url: urlsegment,
                dataType: 'json',
                data: function (params) {
                    return {
                        q: params.term, // search term
                        page: params.page || 1,
                        rows: 10
                    };
                },
                cache: true
            }
        });
}
window.renderSelect2Multiple = function(selector, urlsegment) {
    $(selector)
        .wrap('<div style="position:relative"></div>')
        .select2({
            placeholder: 'Seleccionar',
            language: "es",
            dropdownParent: $(selector).parent(),
            width: '100%',
            multiple:true,
            minimumInputLength: -1,
            enableOnReadonly: false,
            ajax: {
                url: urlsegment,
                dataType: 'json',
                data: function (params) {
                    return {
                        q: params.term, // search term
                        page: params.page || 1,
                        rows: 10
                    };
                },
                cache: true
            }
        });

    $(selector).on('select2:opening select2:closing', function( event ) {
        var $searchfield = $(this).parent().find('.select2-search__field');
        $searchfield.prop('disabled', true);
    });
}
window.renderSelect2MultipleDepend = function(selector, urlsegment, depend) {
    $(selector)
        .wrap('<div style="position:relative"></div>')
        .select2({
            placeholder: 'Seleccionar',
            language: "es",
            dropdownParent: $(selector).parent(),
            width: '100%',
            multiple:true,
            minimumInputLength: -1,
            enableOnReadonly: false,
            ajax: {
                url: function (params) {
                    let id = $(depend).val();
                    if (id === null) {
                        id = 0;
                    }
                    return urlsegment + id;
                },
                dataType: 'json',
                data: function (params) {
                    return {
                        q: params.term, // search term
                        page: params.page || 1,
                        rows: 10
                    };
                },
                cache: true
            }
        });

    $(selector).on('select2:opening select2:closing', function( event ) {
        var $searchfield = $(this).parent().find('.select2-search__field');
        $searchfield.prop('disabled', true);
    });
}
window.renderSelect2Depend = function(selector, urlsegment, depend) {
    $(selector)
        .wrap('<div style="position:relative"></div>')
        .select2({
            placeholder: 'Buscar y seleccionar',
            language: "es",
            dropdownParent: $(selector).parent(),
            width: '100%',
            minimumInputLength: 0,
            enableOnReadonly: false,
            ajax: {
                url: function (params) {
                    let id = $(depend).val();
                    if (id === null) {
                        id = 0;
                    }
                    return urlsegment + id;
                },
                dataType: 'json',
                data: function (params) {
                    return {
                        q: params.term, // search term
                        page: params.page || 1,
                        rows: 10
                    };
                },
                cache: true
            }
        });
}
window.renderJQgridSelect2 = function(selector, urlsegment) {
    $(selector)
        .select2({
            placeholder: 'Buscar y filtrar',
            language: "es",
            width: '100%',
            allowClear: true,
            minimumInputLength: 0,
            enableOnReadonly: false,
            ajax: {
                url: urlsegment,
                dataType: 'json',
                data: function (params) {
                    return {
                        q: params.term, // search term
                        page: params.page || 1,
                        rows: 10
                    };
                },
                cache: true
            }
        });
}

window.renderSelect2DependData = function(selector, urlsegment, /*function*/ informacionBase, /*JquerySelector*/ select2Padre, /*opciones*/ opciones) {
    $(selector)
        .wrap('<div style="position:relative"></div>')
        .select2({
            placeholder: 'Buscar y seleccionar',
            language: "es",
            dropdownParent: $(selector).parent(),
            width: '100%',
            minimumInputLength: 0,
            enableOnReadonly: false,
            ajax: {
                url: urlsegment,
                dataType: 'json',
                data: function (params) {
                    let informacion = informacionBase();
                    informacion.q = params.term;
                    informacion.page = params.page || 1;
                    informacion.rows = 10;

                    return informacion;
                },
                cache: true
            }
        });

        // se establece a nulo el valor del item solo si ha cambiado la informacion base
        if(select2Padre) {
            select2Padre.on("change", function(){
                // solo aplicar el cambio si tiene valor previo
                if($(selector).select2("data").length > 0) {
                    $(selector).val(null).trigger("change");
                }

                if(opciones && opciones.establecerValorPorDefecto) {
                    renderSelect2ValorInicial($(selector), urlsegment, informacionBase);            
                }
            });
        }
}

window.renderSelect2ValorInicial = function(/*select2*/ selector, /*String*/ url, /*function*/ informacionBase) {
    var data = function (params) {
        let informacion = informacionBase ? informacionBase() : {};
        informacion.q = "";
        informacion.page = 1;
        informacion.rows = 1;

        return informacion;
    }();
    $.get(url, data, function(resultado) {
        if(resultado.results && resultado.results[0]) {
            var valorInicial = resultado.results[0];
            var option = new Option(valorInicial.text, valorInicial.id, true, true);
            selector.append(option).trigger('change');
        }
        else {
            console.warn("No se encontro valor inicial para el select2 especificado: " + selector);
        }
    });
}