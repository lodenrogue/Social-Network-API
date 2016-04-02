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
	private long userTargetId;

	@Column(name = "requesting_id")
	private long userRequestingId;

	public FriendRequest() {
	}

	public long getEntityId() {
		return entityId;
	}

	public void setEntityId(long entityId) {
		this.entityId = entityId;
	}

	public long getUserTargetId() {
		return userTargetId;
	}

	public void setUserTargetId(long userTargetId) {
		this.userTargetId = userTargetId;
	}

	public long getUserRequestingId() {
		return userRequestingId;
	}

	public void setUserRequestingId(long userRequestingId) {
		this.userRequestingId = userRequestingId;
	}
}