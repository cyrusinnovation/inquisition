var validateEmail = function(email, error) {
    var matchResult = email.value.match('.+@.+\\..+');
    if (matchResult == null) {
        error.innerHTML = 'invalid email address provided!';
        setFocus(email.id);
    }
    else {
        error.innerHTML = '';
    }
};

var validatePassword = function(password, error) {
	if(password.value.length < 8)
	{
		error.innerHTML = 'Password must be at least eight characters!';
	}
	else
	{
		error.innerHTML = '';
	}
};

var setFocus = function(elementid) {
  setTimeout("document.getElementById('" + elementid + "').focus();", 100);
};

var dialogFunction = function() {
  $("#dialog").dialog({
    autoOpen: false,
    modal: true
  })};

$(document).ready(dialogFunction);

$(".confirmLink").click(function(e) {
  e.preventDefault();
  var targetUrl = $(this).attr("value");

  $("#dialog").dialog({
    buttons : {
      "Confirm" : function() {
          var f = document.createElement('form');
          $(this).after($(f).attr({
          method: 'post',
          action: $(this).attr('value')
      }).append('<input type="hidden" name="_method" value="DELETE" />'));
      $(f).submit();
//        window.location.href = targetUrl;
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

   // if the element is of type delete question, then
   // override default action
   // submit delete of /questions/${questionid}
});
