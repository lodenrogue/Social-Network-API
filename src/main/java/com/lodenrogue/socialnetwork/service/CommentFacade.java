package com.lodenrogue.socialnetwork.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lodenrogue.socialnetwork.model.Comment;

public class CommentFacade extends AbstractFacade<Comment> {

	public CommentFacade() {
		super(Comment.class);
	}

	public List<Comment> findAllByPost(long postId) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("postId", postId);
		return findAllFromQuery("FROM Comment WHERE postId = :postId", parameters);
	}

	public List<Comment> findAllByUser(long userId) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("userId", userId);
		return findAllFromQuery("FROM Comment WHERE userId = :userId", parameters);
	}

}
