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
    var targetUrl = $(this).attr("value") + $(this).text();

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
