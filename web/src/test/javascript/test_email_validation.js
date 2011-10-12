TestCase('Email Validation Test', {
    setUp: function() {
	//test6
        this.email = {value: 'a@example.com', id: 'emailid'};
        this.error = {html: ''};
        this.errorMessage = 'invalid email address provided!';

    },

    tearDown: function() {
        delete this.email;
        delete this.error;
    },

    "test canary": function() {
        assertTrue(true);
    },

    "test can validate valid email": function() {
        validateEmail(this.email, this.error);

        assertEquals('', $(this.error).html);
    },

    "test error div is populated with error message on invalid email": function() {
        this.email.value = 'abcd';

        validateEmail(this.email, this.error);
        assertEquals("expected error message, found: " + this.error.innerHTML,
            this.errorMessage, $(this.error).html);
    },

    "test error is cleared after valid email address is entered": function() {
        this.email.value = 'abcd';
        validateEmail(this.email, this.error);
        assertEquals("expected error message, found: " + this.error.innerHTML,
            this.errorMessage, $(this.error).html);

        this.email.value = 'a@example.com';
        validateEmail(this.email, this.error);
        assertEquals("Unexpected error message, found: " + $(this.error).html,
            $(this.error).html, '');
    },

    "test focus is set to the email input box on error": function() {
        this.email.value = 'abcd';
        var oldSetFocus = setFocus;
        var setFocusCalled = false;

        setFocus = function(elementid) {
            setFocusCalled = true;
            assertEquals("emailid", elementid);
        }

        validateEmail(this.email, this.error);

        assertTrue(setFocusCalled);
        setFocus = oldSetFocus;
    }
});
