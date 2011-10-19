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
    if (password.value.length < 8) {
        error.innerHTML = 'Password must be at least eight characters!';
    }
    else {
        error.innerHTML = '';
    }
};

var setFocus = function(elementid) {
    setTimeout("document.getElementById('" + elementid + "').focus();", 100);
};

