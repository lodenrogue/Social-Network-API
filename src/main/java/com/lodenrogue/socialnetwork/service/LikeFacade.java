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

}
