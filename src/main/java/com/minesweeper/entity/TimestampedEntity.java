package com.minesweeper.entity;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class TimestampedEntity {
	@Column(nullable = false, name = "created_on")
	private Instant createdOn;

	@Column(nullable = false, name = "last_modified")
	private Instant lastModified;

	@PrePersist
	protected void onCreate() {
		final Instant now = Instant.now();
		lastModified = now;
		createdOn = now;
	}

	@PreUpdate
	protected void onUpdate() {
		lastModified = Instant.now();
	}
}
