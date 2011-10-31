var renderTextToTarget = function(rawText, renderTarget) {
    var markup = $(rawText).children("pre").children("code").html();
    $.ajax({
        'url' : "/preview/generate",
        'data': { markupText: markup},
        'dataType' : 'json',
        'type' : 'POST',
        'async' : true,
        'success' : function (m) {
            $(renderTarget).html(m.previewText);
        }});
};

$(document).ready(function() {
    $(".exampleRow").each(function (index, domEle) {
//        domEle == this
//        $(domEle).css("backgroundColor", "yellow");
//        if ($(this).is("#stop")) {
//          $("span").text("Stopped at div index #" + index);
//          return false;
//        }
        var rawText = $(domEle).children(".rawText");
        var renderTarget = $(domEle).children(".renderedText");
        renderTextToTarget($(rawText), $(renderTarget));
    });
//    $("#generatePreview").click(function(event) {
//        event.preventDefault();

//        });
//    });
});