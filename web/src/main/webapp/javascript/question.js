var dialogFunction = function() {
    $("#dialog").dialog({
        autoOpen: false,
        modal: true
    })
};

$(document).ready(dialogFunction);

$(".confirmLink").click(function(e) {
    e.preventDefault();
    var targetUrl = $(this).attr("value");

    $("#dialog").dialog({
        buttons : {
            "Confirm" : function() {
                var f = document.createElement('form');
                $(this).after($(f).attr({
                    method: 'POST',
                    action: $(this).attr('value')
                }).append('<input type="hidden" name="_method" value="DELETE" />'));
                $(f).submit();
            },
            "Cancel" : function() {
                $(this).dialog("close");
            }
        }
    });

    $("#dialog").dialog("open");
});

$("#questionDelete").click(function(event) {
    event.preventDefault();
});

var dialogTagFunction = function() {
    $("#dialogDeleteTag").dialog({
        autoOpen: false,
        modal: true
    })
};

$(document).ready(dialogTagFunction);

$(".deleteTag").click(function(event) {
    event.preventDefault();
    var parentListItemNode = $(this).parent("li")
    var targetUrl = $(this).attr("value");

    $("#dialogDeleteTag").dialog({
        buttons : {
            "Confirm" : function() {
                var f = document.createElement('form');
                $(this).after($(f).attr({
                    method: 'POST',
                    action: targetUrl
                }).append('<input type="hidden" name="_method" value="DELETE" />'));
                $(f).submit();
                $(this).dialog("close");
                parentListItemNode.remove()
            },
            "Cancel" : function() {
                $(this).dialog("close");
            }
        }
    });
    $("#dialogDeleteTag").dialog("open");
});

$(document).ready(function() {
    $("#tags").tagit({ tagSource: function(search, showChoices) {
        $.ajax({
            'url' : "/questions/tags/" + search.term,
            'dataType' : 'json',
            'async' : false,
            'success' : function (m) {
                showChoices(m.tags);
            }
        });
    }
    });
});

$(document).ready(function() {
    $("#tagQuery").keydown(function(event) {
        if (event.keyCode == 13) {
            $("questionAddTagForm").submit();
        }
    });
});

$(document).ready(function() {
    $(".searchTag").click(function(event) {
        event.preventDefault();
        var tag = $(this).text();
        var target = $(this).attr('href');
        var f = document.createElement('form');

        $(this).after($(f).attr({
            method: 'POST',
            action: target
        }).append('<input type="hidden" name="tagQuery" id="search-query" value="' + tag + '" />'));
        $(f).submit();
    });

    if ($("#bodyInput") != undefined) {
        if ($("#bodyInput").val() != "") {
            updateBodyPreview();
        }
    }
});

function updateBodyPreview() {
    var markup = $("#bodyInput").val();
    $.ajax({
        'url' : "/preview/generate",
        'data': { markupText: markup},
        'dataType' : 'json',
        'type' : 'POST',
        'async' : true,
        'success' : function (m) {
            $("#previewPane").html(m.previewText);
        }
    });
}
;

$('#bodyInput').keyup($.debounce(250, updateBodyPreview)); // This is the line you want!

$(document).ready(function() {

    $.each($(".searchTag"), function(x, y) {

        $('.highlightable').highlight(y.text);
    })

});

(function($) {
    $.widget("ui.combobox", {
        _create: function() {
            var self = this,
                    select = this.element.hide(),
                    selected = select.children(":selected"),
                    value = selected.val() ? selected.text() : "";
            var input = this.input = $("<input>")
                    .insertAfter(select)
                    .val(value)
                    .autocomplete({
                        delay: 0,
                        minLength: 0,
                        source: function(request, response) {
                            var matcher = new RegExp($.ui.autocomplete.escapeRegex(request.term), "i");
                            response(select.children("option").map(function() {
                                var text = $(this).text();
                                if (this.value && ( !request.term || matcher.test(text) ))
                                    return {
                                        label: text.replace(
                                                new RegExp(
                                                        "(?![^&;]+;)(?!<[^<>]*)(" +
                                                                $.ui.autocomplete.escapeRegex(request.term) +
                                                                ")(?![^<>]*>)(?![^&;]+;)", "gi"
                                                ), "<strong>$1</strong>"),
                                        value: text,
                                        option: this
                                    };
                            }));
                        },
                        select: function(event, ui) {
                            ui.item.option.selected = true;
                            self._trigger("selected", event, {
                                item: ui.item.option
                            });
                        },
                        change: function(event, ui) {
                            if (!ui.item) {
                                var matcher = new RegExp("^" + $.ui.autocomplete.escapeRegex($(this).val()) + "$", "i"),
                                        valid = false;
                                select.children("option").each(function() {
                                    if ($(this).text().match(matcher)) {
                                        this.selected = valid = true;
                                        return false;
                                    }
                                });
                                if (!valid) {
                                    // remove invalid value, as it didn't match anything
                                    $(this).val("");
                                    select.val("");
                                    input.data("autocomplete").term = "";
                                    return false;
                                }
                            }
                        }
                    })
                    .addClass("ui-widget ui-widget-content ui-corner-left");

            input.data("autocomplete")._renderItem = function(ul, item) {
                return $("<li></li>")
                        .data("item.autocomplete", item)
                        .append("<a>" + item.label + "</a>")
                        .appendTo(ul);
            };

            this.button = $("<button type='button'>&nbsp;</button>")
                    .attr("tabIndex", -1)
                    .attr("title", "Show All Items")
                    .insertAfter(input)
                    .button({
                        icons: {
                            primary: "ui-icon-triangle-1-s"
                        },
                        text: false
                    })
                    .removeClass("ui-corner-all")
                    .addClass("ui-corner-right ui-button-icon")
                    .click(function() {
                        // close if already visible
                        if (input.autocomplete("widget").is(":visible")) {
                            input.autocomplete("close");
                            return;
                        }

                        // work around a bug (likely same cause as #5265)
                        $(this).blur();

                        // pass empty string as value to search for, displaying all results
                        input.autocomplete("search", "");
                        input.focus();
                    });
        },

        destroy: function() {
            this.input.remove();
            this.button.remove();
            this.element.show();
            $.Widget.prototype.destroy.call(this);
        }
    });
})(jQuery);

$(function() {
    $("#combobox").combobox();
    $("#toggle").click(function() {
        $("#combobox").toggle();
    });
});
