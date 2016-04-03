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
import com.lodenrogue.socialnetwork.model.Friendship;
import com.lodenrogue.socialnetwork.service.FriendshipFacade;

import io.swagger.annotations.Api;

@Api(value = "Friendship")
@RestController
@RequestMapping(path = "/api/v1")
public class FriendshipController {

	@RequestMapping(path = "/friendships", method = RequestMethod.POST)
	public HttpEntity<Object> createFriendship(@RequestBody Friendship friendship) {

		List<String> missingFields = new ArrayList<String>();
		if (friendship.getUserId() == 0) missingFields.add("userId");
		if (friendship.getFriendId() == 0) missingFields.add("friendId");

		// Check missing fields
		if (missingFields.size() > 0) {
			return new ResponseEntity<Object>(new MissingFieldsError(missingFields), HttpStatus.UNPROCESSABLE_ENTITY);
		}

		// Check that user and friend are not the same
		else if (friendship.getUserId() == friendship.getFriendId()) {
			return new ResponseEntity<Object>(new ErrorMessage("User and friend id cannot be the same"), HttpStatus.CONFLICT);
		}

		// Check that user exists
		else if (((ResponseEntity<Object>) new UserController().getUser(friendship.getUserId())).getStatusCode() == HttpStatus.NOT_FOUND) {
			return new ResponseEntity<Object>(new ErrorMessage("No user with id " + friendship.getUserId() + " found"), HttpStatus.UNPROCESSABLE_ENTITY);
		}

		// Check that friend exists
		else if (((ResponseEntity<Object>) new UserController().getUser(friendship.getFriendId())).getStatusCode() == HttpStatus.NOT_FOUND) {
			return new ResponseEntity<Object>(new ErrorMessage("No user with id " + friendship.getFriendId() + " found"), HttpStatus.UNPROCESSABLE_ENTITY);
		}

		// Create friendship
		else {
			// Check that the friendship doesn't already exist
			boolean exists = new FriendshipFacade().friendshipExists(friendship.getUserId(), friendship.getFriendId());
			if (exists) {
				return new ResponseEntity<Object>(new ErrorMessage("That friendship already exists"), HttpStatus.CONFLICT);
			}
			else {
				// Double link friendship
				friendship.setEntityId(0);
				friendship = new FriendshipFacade().create(friendship);
				long userId = friendship.getUserId();
				long friendId = friendship.getFriendId();

				Friendship dlFriendship = new Friendship();
				dlFriendship.setUserId(friendId);
				dlFriendship.setFriendId(userId);
				dlFriendship = new FriendshipFacade().create(dlFriendship);

				friendship.add(createLinks(friendship));
				return new ResponseEntity<Object>(friendship, HttpStatus.CREATED);
			}
		}
	}

	@RequestMapping(path = "/friendships/{id}", method = RequestMethod.GET)
	public HttpEntity<Object> getFriendship(@PathVariable long id) {
		Friendship friendship = new FriendshipFacade().find(id);
		if (friendship == null) {
			return new ResponseEntity<Object>(new ErrorMessage("No friendship with id " + id + " found"), HttpStatus.NOT_FOUND);
		}
		else {
			friendship.add(createLinks(friendship));
			return new ResponseEntity<Object>(friendship, HttpStatus.OK);
		}
	}

	@RequestMapping(path = "/friendships/{id}", method = RequestMethod.DELETE)
	public HttpEntity<Object> deleteFriendship(@PathVariable long id) {
		// Delete double link
		Friendship friendship = new FriendshipFacade().find(id);
		if (friendship != null) {
			Friendship dlFriendship = new FriendshipFacade().findWithIds(friendship.getFriendId(), friendship.getUserId());
			new FriendshipFacade().delete(friendship.getEntityId());
			new FriendshipFacade().delete(dlFriendship.getEntityId());
		}
		return new ResponseEntity<Object>(HttpStatus.OK);
	}

	public List<Link> createLinks(Friendship friendship) {
		List<Link> links = new ArrayList<Link>();
		links.add(linkTo(methodOn(FriendshipController.class).getFriendship(friendship.getEntityId())).withSelfRel());
		links.add(linkTo(methodOn(UserController.class).getUser(friendship.getUserId())).withRel("user"));
		links.add(linkTo(methodOn(UserController.class).getUser(friendship.getFriendId())).withRel("friend"));
		return links;
	}

}
