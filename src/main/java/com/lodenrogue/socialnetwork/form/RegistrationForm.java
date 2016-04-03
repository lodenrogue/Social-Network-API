package com.lodenrogue.socialnetwork.form;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import com.lodenrogue.socialnetwork.model.User;

public class RegistrationForm {
	@Size(min=3)
	@NotEmpty(message = "*")
	private String username;

	@Size(min = 6, message = "*")
	private String password;

	@NotEmpty(message = "*")
	@Email
	private String email;

	private String firstName;

	private String lastName;

	@Pattern(regexp = "^[0-9]{5}(?:-[0-9]{4})?$", message = "Invalid zipcode")
	private String postalCode;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

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

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

//	public User buildUser() {
//		User user = new User();
//		user.setEmail(email);
//		user.setPassword(password);
//		user.setUsername(username);
//		return user;
//	}

}
