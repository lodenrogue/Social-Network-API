package com.lodenrogue.socialnetwork.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "friend_requests")
public class FriendRequest extends ResourceSupport {
	@Id
	@GeneratedValue
	@Column(name = "id")
	@JsonProperty(value = "id")
	private long entityId;

	@Column(name = "target_id")
	private long targetUserId;

	@Column(name = "requester_id")
	private long requesterUserId;

	public FriendRequest() {
	}

	public long getEntityId() {
		return entityId;
	}

	public void setEntityId(long entityId) {
		this.entityId = entityId;
	}

	public long getTargetUserId() {
		return targetUserId;
	}

	public void setTargetUserId(long targetUserId) {
		this.targetUserId = targetUserId;
	}

	public long getRequesterUserId() {
		return requesterUserId;
	}

	public void setRequesterUserId(long requesterUserId) {
		this.requesterUserId = requesterUserId;
	}
}