package com.lodenrogue.socialnetwork.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lodenrogue.socialnetwork.model.Like;

public class LikeFacade extends AbstractFacade<Like> {

	public LikeFacade() {
		super(Like.class);
	}

	public List<Like> getLikesByComment(long commentId) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("commentId", commentId);
		return findAllFromQuery("FROM Like WHERE commentId = :commentId", parameters);
	}

	public List<Like> getLikesByPost(long postId) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("postId", postId);
		return findAllFromQuery("FROM Like WHERE postId = :postId", parameters);
	}

	public List<Like> getLikesByUser(long userId) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("userId", userId);
		return findAllFromQuery("FROM Like WHERE userId = :userId", parameters);
	}

}
