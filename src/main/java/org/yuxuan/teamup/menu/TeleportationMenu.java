package org.yuxuan.teamup.menu;

import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.yuxuan.teamup.PluginMain;

public final class TeleportationMenu extends SingleDisplayMenu {
	private static final int SINGLE_LINE_MAX_TARGET_COUNT = 7;
	private static final int SINGLE_PAGE_MAX_TARGET_COUNT = SINGLE_LINE_MAX_TARGET_COUNT * 6;

	private final List<Player> targets;
	private int page;

	public TeleportationMenu(Player player, List<Player> targets) {
		super(player);
		this.targets = targets;
		updatePage();
	}

	private void updatePage() {
		int maxPage = getMaxPage();
		if (page < 0) {
			page = 0;
		}
		if (page >= maxPage) {
			page = maxPage - 1;
		}
		int currentPageYSize = getCurrentPageYSize();
		setTitle(ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "请求传送到玩家" + ChatColor.RESET.toString() + " (" + (page + 1) + "\\" + getMaxPage() + ")");
		setYSize(currentPageYSize);
		clearButtons();
		if (page >= 0 && page < maxPage) {
			{
				int start = page * SINGLE_PAGE_MAX_TARGET_COUNT;
				int end = Math.min(targets.size(), start + SINGLE_PAGE_MAX_TARGET_COUNT);
				int x = 0;
				int y = 0;
				for (int index = start; index < end; index++) {
					Player target = targets.get(index);
					drawTargetIcon(target, x, y);
					x++;
					if (x >= SINGLE_LINE_MAX_TARGET_COUNT) {
						x = 0;
						y++;
					}
				}
			}
			{
				ItemStack dividingLineIcon = new ItemStack(Material.THIN_GLASS);
				ItemMeta dividingLineIconMeta = dividingLineIcon.getItemMeta();
				dividingLineIconMeta.setDisplayName(ChatColor.DARK_GREEN.toString() + ChatColor.BOLD.toString() + "分割线");
				dividingLineIcon.setItemMeta(dividingLineIconMeta);
				for (int index = 0; index < currentPageYSize; index++) {
					addButton(7, index, dividingLineIcon, null);
				}
			}
			if (page > 0) {
				ItemStack previousIcon = new ItemStack(Material.WOOL, 1, (byte) 14);
				ItemMeta previousIconMeta = previousIcon.getItemMeta();
				previousIconMeta.setDisplayName(ChatColor.RED.toString() + ChatColor.BOLD.toString() + "上一页");
				previousIcon.setItemMeta(previousIconMeta);
				addButton(8, 0, previousIcon, event -> previousPage());
			}
			if (page < maxPage - 1) {
				ItemStack nextIcon = new ItemStack(Material.WOOL, 1, (byte) 11);
				ItemMeta nextIconMeta = nextIcon.getItemMeta();
				nextIconMeta.setDisplayName(ChatColor.BLUE.toString() + ChatColor.BOLD.toString() + "下一页");
				nextIcon.setItemMeta(nextIconMeta);
				addButton(8, currentPageYSize - 1, nextIcon, event -> nextPage());
			}
		}
	}

	public void drawTargetIcon(Player target, int x, int y) {
		ItemStack targetIcon = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta targetIconMeta = (SkullMeta) targetIcon.getItemMeta();
		targetIconMeta.setOwner(target.getName());
		targetIconMeta.setDisplayName(target.getDisplayName());
		targetIcon.setItemMeta(targetIconMeta);
		addButton(x, y, targetIcon, event -> {
			Player player = event.getPlayer();
			PluginMain.getInstance().teleportation.sendRequest(player, target);
			closeMenu();
		});
	}

	public int getCurrentPageYSize() {
		int itemCount = targets.size();
		if (itemCount == 0) {
			return 0;
		}
		int maxPage = getMaxPage();
		if (page < 0 || page >= maxPage) {
			return 0;
		} else {
			if (itemCount % SINGLE_PAGE_MAX_TARGET_COUNT != 0 && page == maxPage - 1) {
				return (int) Math.ceil((double) (itemCount % SINGLE_PAGE_MAX_TARGET_COUNT) / (double) SINGLE_LINE_MAX_TARGET_COUNT);
			} else {
				return 6;
			}
		}
	}

	public int getMaxPage() {
		int itemCount = targets.size();
		if (itemCount == 0) {
			return 1;
		}
		return Math.max((int) Math.ceil((double) itemCount / (double) SINGLE_PAGE_MAX_TARGET_COUNT), 1);
	}

	public void previousPage() {
		page--;
		if (page < 0) {
			page = 0;
		}
		updatePage();
	}

	public void nextPage() {
		page++;
		int maxPage = getMaxPage();
		if (page > maxPage - 1) {
			page = maxPage - 1;
		}
		updatePage();
	}

	@Override
	public void displayMenu() {
		updatePage();
		super.displayMenu();
	}
}
