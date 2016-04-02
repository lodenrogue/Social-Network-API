package com.lodenrogue.socialnetwork.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.lodenrogue.socialnetwork.error.ErrorMessage;
import com.lodenrogue.socialnetwork.model.Like;
import com.lodenrogue.socialnetwork.service.LikeFacade;

@RestController
@RequestMapping(path = "/api/v1")
public class LikeController {

	@RequestMapping(path = "/likes/{id}", method = RequestMethod.GET)
	public HttpEntity<Object> getLike(@PathVariable long id) {
		Like like = new LikeFacade().find(id);
		if (like == null) {
			return new ResponseEntity<Object>(new ErrorMessage("No like with id " + id + " found"), HttpStatus.NOT_FOUND);
		}
		else {
			like = createLinks(like);
			return new ResponseEntity<Object>(like, HttpStatus.OK);
		}
	}

	public Like createLinks(Like like) {
		like.add(linkTo(methodOn(LikeController.class).getLike(like.getEntityId())).withSelfRel());
		like.add(linkTo(methodOn(UserController.class).getUser(like.getUserId())).withRel("author"));

		if (like.getPostId() != 0) {
			like.add(linkTo(methodOn(PostController.class).getPost(like.getPostId())).withRel("post"));
		}
		else if (like.getCommentId() != 0) {
			like.add(linkTo(methodOn(CommentController.class).getComment(like.getCommentId())).withRel("comment"));
		}
		return like;
	}

}
