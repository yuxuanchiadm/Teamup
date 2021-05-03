package org.yuxuan.teamup.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.yuxuan.teamup.PluginMain;
import org.yuxuan.teamup.menu.TeleportationMenu;

public final class TeleportationManager {
	private final Map<Player, TeleportationRequest> associatedRequestMap = new HashMap<>();

	public void openMenu(Player player) {
		List<Player> targets = PluginMain.getInstance().getServer().getOnlinePlayers().stream()
			.filter(target -> PluginMain.getInstance().setting.getWorlds().stream()
				.anyMatch(target.getWorld().getName()::equalsIgnoreCase))
			.filter(target -> !target.equals(player))
			.collect(Collectors.toList());
		TeleportationMenu menu = new TeleportationMenu(player, targets);
		menu.displayMenu();
	}

	public void sendRequest(Player from, Player to) {
		if (PluginMain.getInstance().setting.getWorlds().stream().noneMatch(to.getWorld().getName()::equalsIgnoreCase)) {
			from.sendMessage(PluginMain.PLUGIN_MSG_PREFIX + "玩家所在的世界不允许传送");
			return;
		}
		if (associatedRequestMap.containsKey(from)) {
			from.sendMessage(PluginMain.PLUGIN_MSG_PREFIX + "你存在需要处理的传送请求");
			return;
		}
		if (associatedRequestMap.containsKey(to)) {
			from.sendMessage(PluginMain.PLUGIN_MSG_PREFIX + "玩家正忙");
			return;
		}
		TeleportationRequest request = new TeleportationRequest(from, to);
		associatedRequestMap.put(from, request);
		associatedRequestMap.put(to, request);
		from.sendMessage(PluginMain.PLUGIN_MSG_PREFIX + "传送请求已发送给 " + to.getDisplayName() + ChatColor.RESET.toString());
		to.sendMessage(PluginMain.PLUGIN_MSG_PREFIX + "从玩家 " + from.getDisplayName() + ChatColor.RESET.toString() + " 处收到了传送请求，输入 /tud 拒绝，输入 /tua 接受！");
	}

	public void cancelRequest(Player player) {
		TeleportationRequest request = associatedRequestMap.get(player);
		if (request == null) {
			player.sendMessage(PluginMain.PLUGIN_MSG_PREFIX + "未找到传送请求");
			return;
		}
		if (player.equals(request.getTo())) {
			player.sendMessage(PluginMain.PLUGIN_MSG_PREFIX + "你有从 " + request.getFrom().getDisplayName() + ChatColor.RESET.toString() + "处收到的未接受的传送请求，输入 /tud 拒绝，输入 /tua 接受！");
			return;
		}
		associatedRequestMap.remove(request.getFrom());
		associatedRequestMap.remove(request.getTo());
		request.getFrom().sendMessage(PluginMain.PLUGIN_MSG_PREFIX + "已取消发送给 " + request.getTo().getDisplayName() + ChatColor.RESET.toString() + " 的传送请求");
		request.getTo().sendMessage(PluginMain.PLUGIN_MSG_PREFIX + "从 " + request.getFrom().getDisplayName() + ChatColor.RESET.toString() + " 处收到的传送请求已取消");
	}

	public void acceptRequest(Player player) {
		TeleportationRequest request = associatedRequestMap.get(player);
		if (request == null) {
			player.sendMessage(PluginMain.PLUGIN_MSG_PREFIX + "未找到传送请求");
			return;
		}
		if (player.equals(request.getFrom())) {
			player.sendMessage(PluginMain.PLUGIN_MSG_PREFIX + "你有已发送给 " + request.getTo().getDisplayName() + ChatColor.RESET.toString() + " 但对方未接受的传送请求，输入 /tuc 来取消");
			return;
		}
		request.getFrom().teleport(request.getTo());
		associatedRequestMap.remove(request.getFrom());
		associatedRequestMap.remove(request.getTo());
		request.getTo().sendMessage(PluginMain.PLUGIN_MSG_PREFIX + "已接受从 " + request.getFrom().getDisplayName() + ChatColor.RESET.toString() + " 处收到的传送请求！");
	}

	public void declineRequest(Player player) {
		TeleportationRequest request = associatedRequestMap.get(player);
		if (request == null) {
			player.sendMessage(PluginMain.PLUGIN_MSG_PREFIX + "未找到传送请求");
			return;
		}
		if (player.equals(request.getFrom())) {
			player.sendMessage(PluginMain.PLUGIN_MSG_PREFIX + "你有已发送给 " + request.getTo().getDisplayName() + ChatColor.RESET.toString() + " 但对方未接受的传送请求，输入 /tuc 来取消");
			return;
		}
		associatedRequestMap.remove(request.getFrom());
		associatedRequestMap.remove(request.getTo());
		request.getFrom().sendMessage(PluginMain.PLUGIN_MSG_PREFIX + "发送给 " + request.getTo().getDisplayName() + ChatColor.RESET.toString() + " 的传送请求被拒绝");
		request.getTo().sendMessage(PluginMain.PLUGIN_MSG_PREFIX + "已拒绝从 " + request.getFrom().getDisplayName() + ChatColor.RESET.toString() + " 处收到的传送请求");
	}

	public void onPlayerQuit(Player player) {
		TeleportationRequest request = associatedRequestMap.get(player);
		if (request != null) {
			associatedRequestMap.remove(request.getFrom());
			associatedRequestMap.remove(request.getTo());
		}
	}
}
