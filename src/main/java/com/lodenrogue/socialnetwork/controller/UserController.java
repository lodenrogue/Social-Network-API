package com.lodenrogue.socialnetwork.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.ArrayList;
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
import com.lodenrogue.socialnetwork.model.FriendRequest;
import com.lodenrogue.socialnetwork.model.Friendship;
import com.lodenrogue.socialnetwork.model.Like;
import com.lodenrogue.socialnetwork.model.Post;
import com.lodenrogue.socialnetwork.model.User;
import com.lodenrogue.socialnetwork.security.PasswordStorage;
import com.lodenrogue.socialnetwork.security.PasswordStorage.CannotPerformOperationException;
import com.lodenrogue.socialnetwork.service.CommentFacade;
import com.lodenrogue.socialnetwork.service.FriendRequestFacade;
import com.lodenrogue.socialnetwork.service.FriendshipFacade;
import com.lodenrogue.socialnetwork.service.LikeFacade;
import com.lodenrogue.socialnetwork.service.PostFacade;
import com.lodenrogue.socialnetwork.service.UserFacade;

import io.swagger.annotations.Api;

@Api(value = "User")
@RestController
@RequestMapping(path = "/api/v1")
public class UserController {

	@RequestMapping(path = "/users", method = RequestMethod.POST)
	public HttpEntity<Object> createUser(@RequestBody User user) {
	
		List<String> missingFields = new ArrayList<String>();
		if (user.getFirstName() == null) missingFields.add("firstName");
		if (user.getLastName() == null) missingFields.add("lastName");
		if (user.getEmail() == null) missingFields.add("email");
		if (user.getPassword() == null) missingFields.add("password");
	
		// Check missing fields
		if (missingFields.size() > 0) {
			return new ResponseEntity<Object>(new MissingFieldsError(missingFields), HttpStatus.UNPROCESSABLE_ENTITY);
		}
	
		// Check if email already exists in database
		else if (new UserFacade().findByEmail(user.getEmail()) != null) {
			return new ResponseEntity<Object>(new ErrorMessage("User with that email already exists"), HttpStatus.CONFLICT);
		}
	
		// Create user
		else {
			try {
				// Hash password
				user.setPassword(PasswordStorage.createHash(user.getPassword()));
			}
			catch (CannotPerformOperationException e) {
				e.printStackTrace();
			}
	
			user = new UserFacade().create(user);
			user.add(createLinks(user));
			return new ResponseEntity<Object>(user, HttpStatus.CREATED);
		}
	}

	@RequestMapping(path = "/users/{id}", method = RequestMethod.GET)
	public HttpEntity<Object> getUser(@PathVariable long id) {
		User user = new UserFacade().find(id);
		if (user == null) {
			return new ResponseEntity<Object>(new ErrorMessage("No user with id " + id + " found"), HttpStatus.NOT_FOUND);
		}
		else {
			user.setPassword("");
			user.add(createLinks(user));
			return new ResponseEntity<Object>(user, HttpStatus.OK);
		}
	}

	@RequestMapping(path = "/users/{userId}/posts", method = RequestMethod.GET)
	public HttpEntity<List<Post>> getPosts(@PathVariable long userId) {
		List<Post> posts = new PostFacade().findAllByUser(userId);
		for (Post p : posts) {
			p.add(new PostController().createLinks(p));
		}
		return new ResponseEntity<List<Post>>(posts, HttpStatus.OK);
	}

	@RequestMapping(path = "/users/{userId}/comments", method = RequestMethod.GET)
	public HttpEntity<List<Comment>> getComments(@PathVariable long userId) {
		List<Comment> comments = new CommentFacade().findAllByUser(userId);
		for (Comment c : comments) {
			c.add(new CommentController().createLinks(c));
		}
		return new ResponseEntity<List<Comment>>(comments, HttpStatus.OK);
	}

	@RequestMapping(path = "/users/{userId}/friend-requests", method = RequestMethod.GET)
	public HttpEntity<List<FriendRequest>> getFriendRequests(@PathVariable long userId) {
		List<FriendRequest> requests = new FriendRequestFacade().findAllWithTarget(userId);
		for (FriendRequest r : requests) {
			r.add(new FriendRequestController().createLinks(r));
		}
		return new ResponseEntity<List<FriendRequest>>(requests, HttpStatus.OK);
	}

	@RequestMapping(path = "/users/{userId}/friendships", method = RequestMethod.GET)
	public HttpEntity<List<Friendship>> getFriendships(@PathVariable long userId) {
		List<Friendship> friendships = new FriendshipFacade().findAllByUser(userId);
		for (Friendship f : friendships) {
			f.add(new FriendshipController().createLinks(f));
		}
		return new ResponseEntity<List<Friendship>>(friendships, HttpStatus.OK);
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

		// Delete likes
		List<Like> likes = new LikeFacade().getLikesByUser(id);
		for (Like l : likes) {
			new LikeController().deleteLike(l.getEntityId());
		}

		// Delete friend requests
		List<FriendRequest> madeRequests = new FriendRequestFacade().findAllFromRequester(id);
		for (FriendRequest r : madeRequests) {
			new FriendRequestController().deleteFriendRequest(r.getEntityId());
		}
		List<FriendRequest> receivedRequests = getFriendRequests(id).getBody();
		for (FriendRequest r : receivedRequests) {
			new FriendRequestController().deleteFriendRequest(r.getEntityId());
		}

		// Delete friendships
		List<Friendship> friendships = getFriendships(id).getBody();
		for (Friendship f : friendships) {
			new FriendshipController().deleteFriendship(f.getEntityId());
		}

		// Delete user
		new UserFacade().delete(id);
		return new ResponseEntity<Object>(HttpStatus.OK);
	}

	public List<Link> createLinks(User user) {
		List<Link> links = new ArrayList<Link>();
		links.add(linkTo(methodOn(UserController.class).getUser(user.getEntityId())).withSelfRel());

		// Add posts link
		if (getPosts(user.getEntityId()).getBody().size() > 0) {
			links.add(linkTo(methodOn(UserController.class).getPosts(user.getEntityId())).withRel("posts"));
		}

		// Add friend requests link
		if (getFriendRequests(user.getEntityId()).getBody().size() > 0) {
			links.add(linkTo(methodOn(UserController.class).getFriendRequests(user.getEntityId())).withRel("friend-requests"));
		}

		// Add friendships link
		if (getFriendships(user.getEntityId()).getBody().size() > 0) {
			links.add(linkTo(methodOn(UserController.class).getFriendships(user.getEntityId())).withRel("friendships"));
		}
		return links;
	}
}
