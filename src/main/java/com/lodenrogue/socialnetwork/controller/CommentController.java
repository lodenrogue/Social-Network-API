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

import com.lodenrogue.socialnetwork.error.ErrorMessage;
import com.lodenrogue.socialnetwork.error.MissingFieldsError;
import com.lodenrogue.socialnetwork.model.Comment;
import com.lodenrogue.socialnetwork.model.Like;
import com.lodenrogue.socialnetwork.service.CommentFacade;
import com.lodenrogue.socialnetwork.service.LikeFacade;

@RestController
@RequestMapping(path = "/api/v1")
public class CommentController {

	@RequestMapping(path = "/comments/{id}", method = RequestMethod.GET)
	public HttpEntity<Object> getComment(@PathVariable long id) {
		Comment comment = new CommentFacade().find(id);
		if (comment == null) {
			return new ResponseEntity<Object>(new ErrorMessage("No comment with id " + id + " found"), HttpStatus.NOT_FOUND);
		}
		else {
			comment.add(linkTo(methodOn(CommentController.class).getComment(id)).withSelfRel());
			comment.add(linkTo(methodOn(UserController.class).getUser(id)).withRel("author"));
			comment.add(linkTo(methodOn(PostController.class).getPost(id)).withRel("post"));
			return new ResponseEntity<Object>(comment, HttpStatus.OK);
		}
	}

	@RequestMapping(path = "/comments", method = RequestMethod.POST)
	public HttpEntity<Object> createComment(@RequestBody Comment comment) {

		List<String> missingFields = new ArrayList<String>();
		if (comment.getUserId() == 0) missingFields.add("userId");
		if (comment.getContent() == null) missingFields.add("content");
		if (comment.getPostId() == 0) missingFields.add("postId");

		// Check missing fields
		if (missingFields.size() > 0) {
			return new ResponseEntity<Object>(new MissingFieldsError(missingFields), HttpStatus.UNPROCESSABLE_ENTITY);
		}

		// Check that user exits
		else if (((ResponseEntity<Object>) new UserController().getUser(comment.getUserId())).getStatusCode() == HttpStatus.NOT_FOUND) {
			return new ResponseEntity<Object>(new ErrorMessage("No user with id " + comment.getUserId() + " found"), HttpStatus.UNPROCESSABLE_ENTITY);
		}

		// Check that post exists
		else if (((ResponseEntity<Object>) new PostController().getPost(comment.getPostId())).getStatusCode() == HttpStatus.NOT_FOUND) {
			return new ResponseEntity<Object>(new ErrorMessage("No post with id " + comment.getPostId() + " found"), HttpStatus.UNPROCESSABLE_ENTITY);
		}

		// Create Comment
		else {
			comment.setTimeCreated(Calendar.getInstance());
			comment = new CommentFacade().create(comment);
			comment.add(linkTo(methodOn(CommentController.class).getComment(comment.getEntityId())).withSelfRel());
			comment.add(linkTo(methodOn(UserController.class).getUser(comment.getUserId())).withRel("author"));
			comment.add(linkTo(methodOn(PostController.class).getPost(comment.getPostId())).withRel("post"));
			return new ResponseEntity<Object>(comment, HttpStatus.CREATED);
		}
	}

	@RequestMapping(path = "/comments/{id}", method = RequestMethod.DELETE)
	public HttpEntity<Object> deleteComment(@PathVariable long id) {
		new CommentFacade().delete(id);
		return new ResponseEntity<Object>(HttpStatus.OK);
	}

	@RequestMapping(path = "/comments/{id}/likes", method = RequestMethod.POST)
	public HttpEntity<Object> createLike(@PathVariable long id, @RequestBody Like like) {
		List<String> missingFields = new ArrayList<String>();
		if (like.getUserId() == 0) missingFields.add("userId");
		if (like.getCommentId() == 0) missingFields.add("commentId");

		// Check missing fields
		if (missingFields.size() > 0) {
			return new ResponseEntity<Object>(new MissingFieldsError(missingFields), HttpStatus.UNPROCESSABLE_ENTITY);
		}

		// Check that comment id matches
		else if (like.getCommentId() != id) {
			return new ResponseEntity<Object>(new ErrorMessage("commentId: " + like.getCommentId() + " does not match resource id: " + id), HttpStatus.UNPROCESSABLE_ENTITY);
		}

		// Check that the user exists
		else if (((ResponseEntity<Object>) new UserController().getUser(like.getUserId())).getStatusCode() == HttpStatus.NOT_FOUND) {
			return new ResponseEntity<Object>(new ErrorMessage("No user with id " + like.getUserId() + " found"), HttpStatus.UNPROCESSABLE_ENTITY);
		}

		// Create the like
		else {
			like = new LikeFacade().create(like);
			like = new LikeController().createLinks(like);
			return new ResponseEntity<Object>(like, HttpStatus.CREATED);
		}
	}

	@RequestMapping(path = "/comments/{id}/likes", method = RequestMethod.GET)
	public HttpEntity<List<Like>> getLikes(@PathVariable long id) {
		List<Like> likes = new LikeFacade().getLikesByComment(id);
		for (Like l : likes) {
			l = new LikeController().createLinks(l);
		}
		return new ResponseEntity<List<Like>>(likes, HttpStatus.OK);
	}

}
