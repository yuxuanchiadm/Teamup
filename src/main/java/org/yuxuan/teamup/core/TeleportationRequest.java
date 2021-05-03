package org.yuxuan.teamup.core;

import org.bukkit.entity.Player;

public final class TeleportationRequest {
	private final Player from;
	private final Player to;

	public TeleportationRequest(Player from, Player to) {
		this.from = from;
		this.to = to;
	}

	public Player getFrom() {
		return from;
	}

	public Player getTo() {
		return to;
	}
}
