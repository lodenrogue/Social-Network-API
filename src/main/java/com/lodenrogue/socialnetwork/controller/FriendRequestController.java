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
import com.lodenrogue.socialnetwork.model.FriendRequest;
import com.lodenrogue.socialnetwork.service.FriendRequestFacade;

import io.swagger.annotations.Api;

@Api(value = "Friend Request")
@RestController
@RequestMapping(path = "/api/v1")
public class FriendRequestController {

	@RequestMapping(path = "/friend-requests", method = RequestMethod.POST)
	public HttpEntity<Object> createFriendRequest(@RequestBody FriendRequest request) {

		List<String> missingFields = new ArrayList<String>();
		if (request.getRequesterUserId() == 0) missingFields.add("requestingUserId");
		if (request.getTargetUserId() == 0) missingFields.add("targetUserId");

		// Check missing fields
		if (missingFields.size() > 0) {
			return new ResponseEntity<Object>(new MissingFieldsError(missingFields), HttpStatus.UNPROCESSABLE_ENTITY);
		}

		// Check that the target and requester are not the same
		else if (request.getTargetUserId() == request.getRequesterUserId()) {
			return new ResponseEntity<Object>(new ErrorMessage("Target and requester id cannot be the same"), HttpStatus.CONFLICT);
		}

		// Check that target exists
		else if (((ResponseEntity<Object>) new UserController().getUser(request.getTargetUserId())).getStatusCode() == HttpStatus.NOT_FOUND) {
			return new ResponseEntity<Object>(new ErrorMessage("No user with id " + request.getTargetUserId() + " found"), HttpStatus.UNPROCESSABLE_ENTITY);
		}

		// Check that requester exists
		else if (((ResponseEntity<Object>) new UserController().getUser(request.getRequesterUserId())).getStatusCode() == HttpStatus.NOT_FOUND) {
			return new ResponseEntity<Object>(new ErrorMessage("No user with id " + request.getRequesterUserId() + " found"), HttpStatus.UNPROCESSABLE_ENTITY);
		}

		// Create request
		else {
			// Check that the request doesn't already exist
			boolean exists = new FriendRequestFacade().requestExists(request.getTargetUserId(), request.getRequesterUserId());
			if (exists) {
				return new ResponseEntity<Object>(new ErrorMessage("That request already exists"), HttpStatus.CONFLICT);
			}
			else {
				request = new FriendRequestFacade().create(request);
				request.add(createLinks(request));
				return new ResponseEntity<Object>(request, HttpStatus.CREATED);
			}
		}
	}

	@RequestMapping(path = "/friend-requests/{id}", method = RequestMethod.GET)
	public HttpEntity<Object> getFriendRequest(@PathVariable long id) {
		FriendRequest request = new FriendRequestFacade().find(id);
		if (request == null) {
			return new ResponseEntity<Object>(new ErrorMessage("No request with id " + id + " found"), HttpStatus.NOT_FOUND);
		}
		else {
			request.add(createLinks(request));
			return new ResponseEntity<Object>(request, HttpStatus.OK);
		}
	}

	@RequestMapping(path = "/friend-requests/{id}", method = RequestMethod.DELETE)
	public HttpEntity<Object> deleteFriendRequest(@PathVariable long id) {
		new FriendRequestFacade().delete(id);
		return new ResponseEntity<Object>(HttpStatus.OK);
	}

	public List<Link> createLinks(FriendRequest request) {
		List<Link> links = new ArrayList<Link>();
		links.add(linkTo(methodOn(FriendRequestController.class).getFriendRequest(request.getEntityId())).withSelfRel());
		links.add(linkTo(methodOn(UserController.class).getUser(request.getTargetUserId())).withRel("target"));
		links.add(linkTo(methodOn(UserController.class).getUser(request.getRequesterUserId())).withRel("requester"));
		return links;
	}

}
