package com.lodenrogue.socialnetwork.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lodenrogue.socialnetwork.model.Post;

public class PostFacade extends AbstractFacade<Post> {

	public PostFacade() {
		super(Post.class);
	}

	public List<Post> findAllByUser(long userId) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("userId", userId);
		return findAllFromQuery("FROM Post WHERE userId = :userId", parameters);
	}

}
