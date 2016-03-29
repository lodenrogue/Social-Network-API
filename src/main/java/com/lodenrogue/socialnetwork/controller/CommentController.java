package com.lodenrogue.socialnetwork.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.lodenrogue.socialnetwork.model.Comment;
import com.lodenrogue.socialnetwork.rest.MissingFieldsError;
import com.lodenrogue.socialnetwork.service.CommentFacade;

@RestController
public class CommentController {

	@RequestMapping(path = "/api/v1/comments/{id}", method = RequestMethod.GET)
	public HttpEntity<Object> getComment(@PathVariable long id) {
		Comment comment = new CommentFacade().find(id);
		if (comment == null) {
			return new ResponseEntity<Object>(new Error("No comment with id " + id + " found"), HttpStatus.NOT_FOUND);
		}
		else {
			comment.add(linkTo(methodOn(CommentController.class).getComment(id)).withSelfRel());
			comment.add(linkTo(methodOn(UserController.class).getUser(id)).withRel("author"));
			comment.add(linkTo(methodOn(PostController.class).getPost(id)).withRel("post"));
			return new ResponseEntity<Object>(comment, HttpStatus.OK);
		}
	}

	@RequestMapping(path = "/api/v1/comments", method = RequestMethod.POST)
	public HttpEntity<Object> createComment(@RequestBody Comment comment) {

		List<String> missingFields = new ArrayList<String>();
		if (comment.getUserId() == 0) missingFields.add("userId");
		if (comment.getContent() == null) missingFields.add("content");
		if (comment.getPostId() == 0) missingFields.add("postId");

		if (missingFields.size() > 0) {
			return new ResponseEntity<Object>(new MissingFieldsError(missingFields), HttpStatus.UNPROCESSABLE_ENTITY);
		}
		else {
			comment.setTimeCreated(Calendar.getInstance());
			comment = new CommentFacade().create(comment);
			comment.add(linkTo(methodOn(CommentController.class).getComment(comment.getEntityId())).withSelfRel());
			comment.add(linkTo(methodOn(UserController.class).getUser(comment.getUserId())).withRel("author"));
			comment.add(linkTo(methodOn(PostController.class).getPost(comment.getPostId())).withRel("post"));
			return new ResponseEntity<Object>(comment, HttpStatus.CREATED);
		}
	}

	@RequestMapping(path = "/api/v1/comments/{id}", method = RequestMethod.DELETE)
	public HttpEntity<Object> deleteComment(@PathVariable long id) {
		new CommentFacade().delete(id);
		return new ResponseEntity<Object>(HttpStatus.OK);
	}

}
