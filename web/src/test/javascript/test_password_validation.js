TestCase('Password Validation Test', {
    setUp: function() {
	//test6
        this.password = {value: 'password', id: 'passwordid'};
        this.error = {innerHTML: ''};
        this.errorMessage = 'Password must be at least eight characters!';

    },

    tearDown: function() {
        delete this.password;
        delete this.error;
    },
	
	"test can validate valid password": function() {
		validatePassword(this.password, this.error);
		assertEquals("expected no error message, found: " + this.error.innerHTML, '', this.error.innerHTML);
	}, 
	
	"test password less than 8 characters causes validation error": function(){
		this.password.value = 'toshort';
		validatePassword(this.password, this.error);
		assertEquals("expected error message, found: " + this.error.innerHTML, this.errorMessage, this.error.innerHTML);
	},
	
	"test error div is cleared after valid password is entered": function(){
		this.password.value = 'toshort';
		validatePassword(this.password, this.error);
		this.password.value = 'password';
		validatePassword(this.password, this.error);

		assertEquals("expected error div to be cleared after password successfully validated but got: " + this.error.innerHTML, '', this.error.innerHTML);
	},
	"test when the user does not enter anything": function() {
		this.password.value = '';
		validatePassword(this.password, this.error);
		assertEquals("expected error message, found: " + this.error.innerHTML, this.errorMessage, this.error.innerHTML);
	}	
});
