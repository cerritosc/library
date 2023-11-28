// importaciones
import { Tooltip, Popover } from "bootstrap";

// previamente 'bootstrap.js'

// Popovers
// Note: Disable this if you're not using Bootstrap's Popovers
const popoverTriggerList = [].slice.call(document.querySelectorAll("[data-toggle=\"popover\"]"))
popoverTriggerList.map((popoverTriggerEl) => {
  return new Popover(popoverTriggerEl)
})

// Tooltips
// Note: Disable this if you're not using Bootstrap's Tooltips
const tooltipTriggerList = [].slice.call(document.querySelectorAll("[data-toggle=\"tooltip\"]"))
tooltipTriggerList.map((tooltipTriggerEl) => {
  return new Tooltip(tooltipTriggerEl)
})

// previamente 'feathers.js'
document.addEventListener("DOMContentLoaded", function() {
  feather.replace();
});

// previamente 'select2.js'
$.fn.select2.defaults.set( "theme", "bootstrap4" );


// previamente 'datatables.js'

$.extend($.fn.dataTable.defaults, {
    "dom": "ltipr",
    "language": {
        "url": DT_LANG_URL
    }
});

// previamente 'splash.js'
$(document).ready(function() {
  $('.splash').removeClass('active');
});

document.addEventListener("DOMContentLoaded", function() {
  /* Sidebar toggle behaviour */
  $(".sidebar-toggle").on("click touch", function() {
    $(".sidebar").toggleClass("toggled");
  });

  const active = $(".sidebar .active");

  if (active.length && active.parent(".collapse").length) {
    const parent = active.parent(".collapse");

    parent.prev("a").attr("aria-expanded", true);
    parent.addClass("show");
  }
});