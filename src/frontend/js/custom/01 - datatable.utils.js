window.renderDatatableFilters = function (table){

    $(table+' .dtfilter').each(function (index) {
        let column = $(this).data('column');
        let stype = $(this).data('stype');
        let regex = $(this).data('regex');
        switch (stype) {
            case 'select2':
                let url = $(this).data('url');
                renderDataTableSelect2(table, this, url);
                break;
            case 'select':
                renderDataTableSelect(table, this);
                break;
            case 'date':
                renderDatatableDatePicker(table, this, regex);
                break;
            default:
                $(this).on('search', function (e) {
                    if ($(table).DataTable().column(column).search() !== this.value) {
                        performSearchDt(table, column, this.value, regex);
                    }
                });
                break;
        }
    });

}

window.performSearchDt = function(table, column, value, regex){
    $(table).DataTable()
        .column(column)
        .search(value, regex)
        .draw();

}

window.renderDatatableDatePicker = function(table, selector, regex){
    $(selector).datepicker({
        format: "dd/mm/yyyy",
        autoclose: true,
        todayHighlight: true,
        orientation: "bottom",
        language: "es",
        clearBtn: true
    });
    $(selector).datepicker().on('changeDate', function (e) {
        let posicionReal = parseInt($(this).attr('data-column'));
        let date = $(e.currentTarget).val();
        if ($(table).DataTable().column(posicionReal).search() !== date) {
            $(table).DataTable()
                .column(posicionReal)
                .search(date, regex)
                .draw();
        }
    });
}

window.renderDataTableSelect2 = function(table, selector, urlsegment,rows) {
    $(selector)
        .select2({
            placeholder: 'Buscar y filtrar',
            dropdownParent: $(table),
            language: "es",
            width: '100%',
            allowClear: true,
            minimumInputLength: 0,
            ajax: {
                url: urlsegment,
                dataType: 'json',
                data: function (params) {
                    return {
                        q: params.term || '', // search term
                        page: params.page || 1,
                        rows: rows || 5
                    };
                },
                cache: true
            }
        });

    $(selector).on('select2:select', function (e) {
        let posicionReal = parseInt($(this).attr('data-column'));
        let id = e.params.data.id;
        if ($(table).DataTable().column(posicionReal).search() !== id) {
            $(table).DataTable()
                .column(posicionReal)
                .search(id)
                .draw();
        }
    });
    $(selector).on('select2:clear', function (e) {
        let posicionReal = parseInt($(this).attr('data-column'));
        $(table).DataTable()
            .column(posicionReal)
            .search("")
            .draw();

    });
}
window.renderDataTableSelect = function(table, selector) {
    $(selector)
        .select2({
            placeholder: 'Buscar y filtrar',
            language: "es",
            width: '100%',
            allowClear: true,
            minimumInputLength: 0
        });

    $(selector).on('select2:select', function (e) {
        let posicionReal = parseInt($(this).attr('data-column'));
        let id = e.params.data.id;
        if ($(table).DataTable().column(posicionReal).search() !== id) {
            $(table).DataTable()
                .column(posicionReal)
                .search(id)
                .draw();
        }
    });
    $(selector).on('select2:clear', function (e) {
        let posicionReal = parseInt($(this).attr('data-column'));
        $(table).DataTable()
            .column(posicionReal)
            .search("")
            .draw();

    });
}
/**
 * Populate form fields from a JSON object
 * Native Non JQuery
 *
 * @param form object The form element containing your input fields.
 * @param data array JSON data to populate the fields with.
 * @param basename string Optional basename which is added to `name` attributes
 */
window.populate = function(form, data, basename) {
    for (var key in data) {
        if (! data.hasOwnProperty(key)) {
            continue;
        }

        var name = key;
        var value = data[key];

        if ('undefined' === typeof value) {
            value = '';
        }

        if (null === value) {
            value = '';
        }

        // handle array name attributes
        if (typeof(basename) !== "undefined") {
            name = basename + "." + key;
        }

        if (value.constructor === Array) {
            name += '[]';
        } else if(typeof value == "object") {
            populate(form, value, name);
            continue;
        }

        // only proceed if element is set
        var element = form.elements.namedItem(name);
        if (! element ||element === $("meta[name='_csrf_header']").attr("content")) {
            continue;
        }

        var type = element.type || element[0].type;

        switch(type ) {
            default:
                element.value = value;
                break;

            case 'radio':
            case 'checkbox':
                for (var j=0; j < element.length; j++) {
                    element[j].checked = (String(value) === String(element[j].value));
                }
                break;

            case 'select-multiple':
                var values = value.constructor == Array ? value : [value];

                for(var k = 0; k < element.options.length; k++) {
                    element.options[k].selected = (values.indexOf(element.options[k].value) > -1 );
                }
                break;

            case 'select':
            case 'select-one':
                element.value = value.toString() || value;
                break;

            case 'date':
                element.value = new Date(value).toISOString().split('T')[0];
                break;
        }

    }
}