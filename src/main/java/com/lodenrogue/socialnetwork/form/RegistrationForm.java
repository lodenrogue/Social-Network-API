package com.lodenrogue.socialnetwork.form;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

public class RegistrationForm {
	@Size(min = 6, message = "*")
	private String password;

	@NotEmpty(message = "*")
	@Email
	private String email;

	@NotEmpty(message = "*")
	private String firstName;

	@NotEmpty(message = "*")
	private String lastName;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

}
