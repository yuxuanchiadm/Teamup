package org.yuxuan.teamup;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.yuxuan.teamup.config.TeamupSetting;
import org.yuxuan.teamup.core.TeleportationManager;
import org.yuxuan.teamup.inventory.EventInventoryHolder;

public final class PluginMain extends JavaPlugin implements Listener {
	public static String PLUGIN_MSG_PREFIX = ChatColor.GRAY + "[" + ChatColor.GOLD + "传送系统" + ChatColor.GRAY + "]" + ChatColor.RESET + " ";
	private static PluginMain INSTANCE;

	public TeamupSetting setting;
	public TeleportationManager teleportation;

	public PluginMain() {
		INSTANCE = this;
	}

	@Override
	public void onEnable() {
		saveDefaultConfig();

		setting = new TeamupSetting();
		teleportation = new TeleportationManager();

		getServer().getPluginManager().registerEvents(this, this);
	}

	@Override
	public void onDisable() {
		setting = null;
		teleportation = null;

		for (Player player : getServer().getOnlinePlayers()) {
			if (player.getOpenInventory().getTopInventory().getHolder() instanceof EventInventoryHolder) {
				player.closeInventory();
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event) {
		teleportation.onPlayerQuit(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
	public void onInventoryOpen(InventoryOpenEvent event) {
		if (event.getInventory().getHolder() instanceof EventInventoryHolder) {
			((EventInventoryHolder) event.getInventory().getHolder()).onInventoryOpen(event);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
	public void onInventoryClose(InventoryCloseEvent event) {
		if (event.getInventory().getHolder() instanceof EventInventoryHolder) {
			((EventInventoryHolder) event.getInventory().getHolder()).onInventoryClose(event);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getInventory().getHolder() instanceof EventInventoryHolder) {
			((EventInventoryHolder) event.getInventory().getHolder()).onInventoryClick(event);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
	public void onInventoryDrage(InventoryDragEvent event) {
		if (event.getInventory().getHolder() instanceof EventInventoryHolder) {
			((EventInventoryHolder) event.getInventory().getHolder()).onInventoryDrage(event);
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("teamupopen")) {
			if (args.length == 0) {
				if (sender instanceof Player) {
					Player player = (Player) sender;
					teleportation.openMenu(player);
					return true;
				} else {
					sender.sendMessage(PLUGIN_MSG_PREFIX + "你无法接受传送请求");
					return true;
				}
			} else if (args.length == 1) {
				Player player = getServer().getPlayerExact(args[0]);
				if (player != null) {
					teleportation.openMenu(player);
					return true;
				} else {
					sender.sendMessage(PLUGIN_MSG_PREFIX + "未找到玩家");
					return true;
				}
			}
		} else if (command.getName().equalsIgnoreCase("teamupcancel")) {
			if (args.length == 0) {
				if (sender instanceof Player) {
					Player player = (Player) sender;
					teleportation.cancelRequest(player);
					return true;
				} else {
					sender.sendMessage(PLUGIN_MSG_PREFIX + "你无法取消传送请求");
					return true;
				}
			}
		} else if (command.getName().equalsIgnoreCase("teamupaccept")) {
			if (args.length == 0) {
				if (sender instanceof Player) {
					Player player = (Player) sender;
					teleportation.acceptRequest(player);
					return true;
				} else {
					sender.sendMessage(PLUGIN_MSG_PREFIX + "你无法接受传送请求");
					return true;
				}
			}
		} else if (command.getName().equalsIgnoreCase("teamupdecline")) {
			if (args.length == 0) {
				if (sender instanceof Player) {
					Player player = (Player) sender;
					teleportation.declineRequest(player);
					return true;
				} else {
					sender.sendMessage(PLUGIN_MSG_PREFIX + "你无法拒绝传送请求");
					return true;
				}
			}
		}
        return false;
    }

	public static PluginMain getInstance() {
		return INSTANCE;
	}
}
