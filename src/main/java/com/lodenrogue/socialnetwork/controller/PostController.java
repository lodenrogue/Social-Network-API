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
import com.lodenrogue.socialnetwork.model.Post;
import com.lodenrogue.socialnetwork.rest.MissingFieldsError;
import com.lodenrogue.socialnetwork.service.CommentFacade;
import com.lodenrogue.socialnetwork.service.PostFacade;

@RestController
public class PostController {

	@RequestMapping(path = "/api/v1/posts/{id}", method = RequestMethod.GET)
	public HttpEntity<Object> getPost(@PathVariable long id) {
		Post post = new PostFacade().find(id);
		if (post == null) {
			return new ResponseEntity<Object>(new Error("No post with id " + id + " found"), HttpStatus.NOT_FOUND);
		}
		else {
			post.add(linkTo(methodOn(PostController.class).getPost(id)).withSelfRel());
			post.add(linkTo(methodOn(UserController.class).getUser(post.getUserId())).withRel("author"));
			if (getCommentsByPost(id).getBody().size() > 0) {
				post.add(linkTo(methodOn(PostController.class).getCommentsByPost(id)).withRel("comments"));
			}
			return new ResponseEntity<Object>(post, HttpStatus.OK);
		}
	}

	@RequestMapping(path = "/api/v1/posts", method = RequestMethod.POST)
	public HttpEntity<Object> createPost(@RequestBody Post post) {

		List<String> missingFields = new ArrayList<String>();
		if (post.getUserId() == 0) missingFields.add("userId");
		if (post.getContent() == null) missingFields.add("content");

		if (missingFields.size() > 0) {
			return new ResponseEntity<Object>(new MissingFieldsError(missingFields), HttpStatus.UNPROCESSABLE_ENTITY);
		}
		else {
			post.setTimeCreated(Calendar.getInstance());
			post = new PostFacade().create(post);
			post.add(linkTo(methodOn(PostController.class).getPost(post.getEntityId())).withSelfRel());
			post.add(linkTo(methodOn(UserController.class).getUser(post.getUserId())).withRel("author"));
			return new ResponseEntity<Object>(post, HttpStatus.CREATED);
		}
	}

	@RequestMapping(path = "/api/v1/posts/{postId}/comments", method = RequestMethod.GET)
	public HttpEntity<List<Comment>> getCommentsByPost(@PathVariable long postId) {
		List<Comment> comments = new CommentFacade().findAllByPost(postId);
		for (Comment c : comments) {
			c.add(linkTo(methodOn(CommentController.class).getComment(c.getEntityId())).withSelfRel());
			c.add(linkTo(methodOn(UserController.class).getUser(c.getUserId())).withRel("author"));
			c.add(linkTo(methodOn(PostController.class).getPost(postId)).withRel("post"));
		}
		return new ResponseEntity<List<Comment>>(comments, HttpStatus.OK);
	}

	@RequestMapping(path = "/api/v1/posts/{id}", method = RequestMethod.DELETE)
	public HttpEntity<Object> deletePost(@PathVariable long id) {
		// Delete comments
		List<Comment> comments = getCommentsByPost(id).getBody();
		for (Comment c : comments) {
			new CommentController().deleteComment(c.getEntityId());
		}

		// Delete post
		new PostFacade().delete(id);
		return new ResponseEntity<Object>(HttpStatus.OK);
	}

}
