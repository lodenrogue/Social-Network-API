package com.lodenrogue.socialnetwork.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lodenrogue.socialnetwork.model.FriendRequest;

public class FriendRequestFacade extends AbstractFacade<FriendRequest> {

	public FriendRequestFacade() {
		super(FriendRequest.class);
	}

	public List<FriendRequest> findAllWithTarget(long userId) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("userId", userId);
		return findAllFromQuery("FROM FriendRequest WHERE targetUserId = :userId", parameters);
	}
}
