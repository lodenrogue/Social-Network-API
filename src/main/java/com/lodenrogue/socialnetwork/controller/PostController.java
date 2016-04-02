package com.lodenrogue.socialnetwork.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.hateoas.Link;
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
import com.lodenrogue.socialnetwork.model.Post;
import com.lodenrogue.socialnetwork.service.CommentFacade;
import com.lodenrogue.socialnetwork.service.LikeFacade;
import com.lodenrogue.socialnetwork.service.PostFacade;

@RestController
@RequestMapping(path = "/api/v1")
public class PostController {

	@RequestMapping(path = "/posts/{id}", method = RequestMethod.GET)
	public HttpEntity<Object> getPost(@PathVariable long id) {
		Post post = new PostFacade().find(id);
		if (post == null) {
			return new ResponseEntity<Object>(new ErrorMessage("No post with id " + id + " found"), HttpStatus.NOT_FOUND);
		}
		else {
			post.add(createLinks(post));
			return new ResponseEntity<Object>(post, HttpStatus.OK);
		}
	}

	@RequestMapping(path = "/posts", method = RequestMethod.POST)
	public HttpEntity<Object> createPost(@RequestBody Post post) {

		List<String> missingFields = new ArrayList<String>();
		if (post.getUserId() == 0) missingFields.add("userId");
		if (post.getContent() == null) missingFields.add("content");

		// Check missing fields
		if (missingFields.size() > 0) {
			return new ResponseEntity<Object>(new MissingFieldsError(missingFields), HttpStatus.UNPROCESSABLE_ENTITY);
		}

		// Check that user exists
		if (((ResponseEntity<Object>) new UserController().getUser(post.getUserId())).getStatusCode() == HttpStatus.NOT_FOUND) {
			return new ResponseEntity<Object>(new ErrorMessage("No user with id " + post.getUserId() + " found"), HttpStatus.UNPROCESSABLE_ENTITY);
		}

		// Create Post
		else {
			post.setTimeCreated(Calendar.getInstance());
			post = new PostFacade().create(post);
			post.add(createLinks(post));
			return new ResponseEntity<Object>(post, HttpStatus.CREATED);
		}
	}

	@RequestMapping(path = "/posts/{postId}/comments", method = RequestMethod.GET)
	public HttpEntity<List<Comment>> getComments(@PathVariable long postId) {
		List<Comment> comments = new CommentFacade().findAllByPost(postId);
		for (Comment c : comments) {
			c.add(new CommentController().createLinks(c));
		}
		return new ResponseEntity<List<Comment>>(comments, HttpStatus.OK);
	}

	@RequestMapping(path = "/posts/{id}", method = RequestMethod.DELETE)
	public HttpEntity<Object> deletePost(@PathVariable long id) {
		// Delete comments
		List<Comment> comments = getComments(id).getBody();
		for (Comment c : comments) {
			new CommentController().deleteComment(c.getEntityId());
		}

		// Delete likes
		List<Like> likes = getLikes(id).getBody();
		for (Like l : likes) {
			new LikeController().deleteLike(l.getEntityId());
		}

		// Delete post
		new PostFacade().delete(id);
		return new ResponseEntity<Object>(HttpStatus.OK);
	}

	@RequestMapping(path = "/posts/{id}/likes", method = RequestMethod.POST)
	public HttpEntity<Object> createLike(@PathVariable long id, @RequestBody Like like) {
		List<String> missingFields = new ArrayList<String>();
		if (like.getUserId() == 0) missingFields.add("userId");
		if (like.getCommentId() == 0) missingFields.add("postId");

		// Check missing fields
		if (missingFields.size() > 0) {
			return new ResponseEntity<Object>(new MissingFieldsError(missingFields), HttpStatus.UNPROCESSABLE_ENTITY);
		}

		// Check that post id matches
		else if (like.getPostId() != id) {
			return new ResponseEntity<Object>(new ErrorMessage("postId: " + like.getPostId() + " does not match resource id: " + id), HttpStatus.UNPROCESSABLE_ENTITY);
		}

		// Check that the user exists
		else if (((ResponseEntity<Object>) new UserController().getUser(like.getUserId())).getStatusCode() == HttpStatus.NOT_FOUND) {
			return new ResponseEntity<Object>(new ErrorMessage("No user with id " + like.getUserId() + " found"), HttpStatus.UNPROCESSABLE_ENTITY);
		}

		// Create the like
		else {
			like = new LikeFacade().create(like);
			like.add(new LikeController().createLinks(like));
			return new ResponseEntity<Object>(like, HttpStatus.CREATED);
		}
	}

	@RequestMapping(path = "/posts/{id}/likes", method = RequestMethod.GET)
	public HttpEntity<List<Like>> getLikes(@PathVariable long id) {
		List<Like> likes = new LikeFacade().getLikesByPost(id);
		for (Like l : likes) {
			l.add(new LikeController().createLinks(l));
		}
		return new ResponseEntity<List<Like>>(likes, HttpStatus.OK);
	}

	public List<Link> createLinks(Post post) {
		List<Link> links = new ArrayList<Link>();
		links.add(linkTo(methodOn(PostController.class).getPost(post.getEntityId())).withSelfRel());
		links.add(linkTo(methodOn(UserController.class).getUser(post.getUserId())).withRel("author"));

		// Add comments link
		if (getComments(post.getEntityId()).getBody().size() > 0) {
			links.add(linkTo(methodOn(PostController.class).getComments(post.getEntityId())).withRel("comments"));
		}

		// Add likes link
		if (getLikes(post.getEntityId()).getBody().size() > 0) {
			links.add(linkTo(methodOn(PostController.class).getLikes(post.getEntityId())).withRel("likes"));
		}
		return links;
	}

}
