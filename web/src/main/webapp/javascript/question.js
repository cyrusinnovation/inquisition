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

    if($("#bodyInput") != undefined){
        if($("#bodyInput").val() != "")
        {
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
};

$('#bodyInput').keyup( $.debounce( 250, updateBodyPreview ) ); // This is the line you want!

$(document).ready(function() {

  $.each($(".searchTag"), function(x, y){

      $('.highlightable').highlight(y.text);
  })

});
