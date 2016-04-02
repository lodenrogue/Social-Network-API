package com.lodenrogue.socialnetwork.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.lodenrogue.socialnetwork.error.ErrorMessage;
import com.lodenrogue.socialnetwork.error.MissingFieldsError;
import com.lodenrogue.socialnetwork.model.Comment;
import com.lodenrogue.socialnetwork.model.Post;
import com.lodenrogue.socialnetwork.model.User;
import com.lodenrogue.socialnetwork.service.CommentFacade;
import com.lodenrogue.socialnetwork.service.PostFacade;
import com.lodenrogue.socialnetwork.service.UserFacade;

@RestController
@RequestMapping(path = "/api/v1")
public class UserController {

	@RequestMapping(path = "/users/{id}", method = RequestMethod.GET)
	public HttpEntity<Object> getUser(@PathVariable long id) {
		// Find the user
		User user = new UserFacade().find(id);
		if (user == null) {
			return new ResponseEntity<Object>(new ErrorMessage("No user with id " + id + " found"), HttpStatus.NOT_FOUND);
		}
		else {
			user.add(linkTo(methodOn(UserController.class).getUser(id)).withSelfRel());
			if (getPosts(id).getBody().size() > 0) {
				user.add(linkTo(methodOn(UserController.class).getPosts(id)).withRel("posts"));
			}
			return new ResponseEntity<Object>(user, HttpStatus.OK);
		}
	}

	@RequestMapping(path = "/users", method = RequestMethod.POST)
	public HttpEntity<Object> createUser(@RequestBody User user) {

		List<String> missingFields = new ArrayList<String>();
		if (user.getFirstName() == null) missingFields.add("firstName");
		if (user.getLastName() == null) missingFields.add("lastName");
		if (user.getEmail() == null) missingFields.add("email");

		if (missingFields.size() > 0) {
			return new ResponseEntity<Object>(new MissingFieldsError(missingFields), HttpStatus.UNPROCESSABLE_ENTITY);
		}
		else {
			user = new UserFacade().create(user);
			user.add(linkTo(methodOn(UserController.class).getUser(user.getEntityId())).withSelfRel());
			return new ResponseEntity<Object>(user, HttpStatus.CREATED);
		}
	}

	@RequestMapping(path = "/users/{userId}/posts", method = RequestMethod.GET)
	public HttpEntity<List<Post>> getPosts(@PathVariable long userId) {
		List<Post> posts = new PostFacade().findAllByUser(userId);
		for (Post p : posts) {
			p.add(linkTo(methodOn(PostController.class).getPost(p.getEntityId())).withSelfRel());
			p.add(linkTo(methodOn(UserController.class).getUser(userId)).withRel("author"));
		}
		return new ResponseEntity<List<Post>>(posts, HttpStatus.OK);
	}

	@RequestMapping(path = "/users/{userId}/comments", method = RequestMethod.GET)
	public HttpEntity<List<Comment>> getComments(@PathVariable long userId) {
		List<Comment> comments = new CommentFacade().findAllByUser(userId);
		for (Comment c : comments) {
			c.add(linkTo(methodOn(CommentController.class).getComment(c.getEntityId())).withSelfRel());
			c.add(linkTo(methodOn(UserController.class).getUser(userId)).withRel("author"));
			c.add(linkTo(methodOn(PostController.class).getPost(c.getPostId())).withRel("post"));
		}
		return new ResponseEntity<List<Comment>>(comments, HttpStatus.OK);
	}

	@RequestMapping(path = "/users/{id}", method = RequestMethod.DELETE)
	public HttpEntity<Object> deleteUser(@PathVariable long id) {
		// Delete posts
		List<Post> posts = getPosts(id).getBody();
		for (Post p : posts) {
			new PostController().deletePost(p.getEntityId());
		}

		// Delete comments
		List<Comment> comments = getComments(id).getBody();
		for (Comment c : comments) {
			new CommentController().deleteComment(c.getEntityId());
		}

		// Delete user
		new UserFacade().delete(id);
		return new ResponseEntity<Object>(HttpStatus.OK);
	}
}
