// cmd/MaxiUpgradeCommand.java
package com.maxi.maxiUpgrade.cmd;

import com.maxi.maxiUpgrade.MaxiUpgradePlugin;
import com.maxi.maxiUpgrade.compat.UpgradeDataService;
import com.maxi.maxiUpgrade.config.ConfigManager;
import com.maxi.maxiUpgrade.config.ItemsManager;
import com.maxi.maxiUpgrade.gui.UpgradeGuiManager;
import com.maxi.maxiUpgrade.services.MMOItemsRebuildService;
import com.maxi.maxiUpgrade.upgrade.UpgradeMessageService;
import com.maxi.maxiUpgrade.utils.YmlItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public final class MaxiUpgradeCommand implements CommandExecutor, TabCompleter {

    private final MaxiUpgradePlugin plugin;
    private final UpgradeGuiManager gui;
    private final UpgradeMessageService msg;

    private final ConfigManager config;
    private final ItemsManager items;
    private final UpgradeDataService data;
    private final MMOItemsRebuildService rebuildService;

    private static final String PERM_USE = "upgradeableitems.use";
    private static final String PERM_GIVE_STONE = "upgradeableitems.givestone";
    private static final String PERM_GIVE_ORB = "upgradeableitems.giveorb";
    private static final String PERM_GIVE_DUST = "upgradeableitems.givedust";
    private static final String PERM_GIVE_ALL = "upgradeableitems.giveall";
    private static final String PERM_SETLEVEL = "upgradeableitems.setlevel";
    private static final String PERM_RELOAD = "upgradeableitems.reload";
    private static final String PERM_DEBUG = "upgradeableitems.debug";

    private static final List<String> STONE_TIERS = List.of("low", "mid", "high", "legendary", "mythic");
    private static final List<String> PARCHMENT_TYPES = List.of("universal", "blessed", "luckbooster", "downgrade");

    public MaxiUpgradeCommand(
            MaxiUpgradePlugin plugin,
            UpgradeGuiManager gui,
            UpgradeMessageService msg,
            ConfigManager config,
            ItemsManager items,
            UpgradeDataService data,
            MMOItemsRebuildService rebuildService
    ) {
        this.plugin = plugin;
        this.gui = gui;
        this.msg = msg;
        this.config = config;
        this.items = items;
        this.data = data;
        this.rebuildService = rebuildService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {
            if (!(sender instanceof Player p)) return true;
            if (!sender.hasPermission(PERM_USE)) { sendKey(p, "commands.errors.no-permission", "§cNo permission."); return true; }
            gui.open(p);
            sendKey(p, "commands.gui.opened", "§aGUI opened.");
            return true;
        }

        String sub = args[0].toLowerCase(Locale.ROOT);

        if (sub.equals("gui")) {
            if (!(sender instanceof Player p)) return true;
            if (!sender.hasPermission(PERM_USE)) { sendKey(p, "commands.errors.no-permission", "§cNo permission."); return true; }
            gui.open(p);
            sendKey(p, "commands.gui.opened", "§aGUI opened.");
            return true;
        }

        if (sub.equals("reload")) {
            if (!sender.hasPermission(PERM_RELOAD)) {
                if (sender instanceof Player p) sendKey(p, "commands.errors.no-permission", "§cNo permission.");
                return true;
            }
            plugin.reloadAll();
            if (sender instanceof Player p) sendKey(p, "commands.reload.success", "§aReloaded.");
            else plugin.getLogger().info("Reloaded.");
            return true;
        }

        if (sub.equals("debug")) {
            if (!sender.hasPermission(PERM_DEBUG)) {
                if (sender instanceof Player p) sendKey(p, "commands.errors.no-permission", "§cNo permission.");
                return true;
            }

            boolean now = !plugin.getConfig().getBoolean("debug", false);
            plugin.getConfig().set("debug", now);
            plugin.saveConfig();
            plugin.reloadConfig();
            config.setConfig(plugin.getConfig());

            if (sender instanceof Player p) {
                if (now) sendKey(p, "commands.debug.enabled", "§eDEBUG ON");
                else sendKey(p, "commands.debug.disabled", "§eDEBUG OFF");
            } else plugin.getLogger().info("Debug: " + now);

            return true;
        }

        if (sub.equals("setlevel")) {
            if (!(sender instanceof Player p)) return true;
            if (!sender.hasPermission(PERM_SETLEVEL)) { sendKey(p, "commands.errors.no-permission", "§cNo permission."); return true; }
            if (args.length < 2) { p.sendMessage("§cKullanım: /" + label + " setlevel <level>"); return true; }

            int level;
            try { level = Integer.parseInt(args[1]); }
            catch (NumberFormatException ex) { p.sendMessage("§cLevel sayı olmalı."); return true; }

            int slot = p.getInventory().getHeldItemSlot();
            ItemStack hand = p.getInventory().getItem(slot);
            if (hand == null || hand.getType().isAir()) { p.sendMessage("§cElinde item yok."); return true; }

            data.setUpgradeLevel(hand, level);
            hand = rebuildService.rebuild(hand, p);

            p.getInventory().setItem(slot, hand);
            p.updateInventory();

            p.sendMessage("§aItem seviyesi setlendi: +" + Math.max(0, level));
            return true;
        }

        if (sub.equals("givestone")) {
            if (!sender.hasPermission(PERM_GIVE_STONE)) {
                if (sender instanceof Player p) sendKey(p, "commands.errors.no-permission", "§cNo permission.");
                return true;
            }
            if (args.length < 3) {
                sender.sendMessage("§cKullanım: /" + label + " givestone <player> <low|mid|high|legendary|mythic> [amount]");
                return true;
            }
            Player target = Bukkit.getPlayerExact(args[1]);
            if (target == null) { sender.sendMessage("§cOyuncu online değil."); return true; }

            String tier = args[2].toLowerCase(Locale.ROOT);
            if (!STONE_TIERS.contains(tier)) { sender.sendMessage("§cTier geçersiz. (low, mid, high, legendary, mythic)"); return true; }

            int amount = parseAmount(args, 3, 1);
            giveBuiltItem(target, tier + "-stone", amount);
            sender.sendMessage("§aVerildi: " + target.getName() + " -> " + tier + "-stone x" + amount);
            return true;
        }

        if (sub.equals("giveorb")) {
            if (!sender.hasPermission(PERM_GIVE_ORB)) {
                if (sender instanceof Player p) sendKey(p, "commands.errors.no-permission", "§cNo permission.");
                return true;
            }
            if (args.length < 2) { sender.sendMessage("§cKullanım: /" + label + " giveorb <player> [amount]"); return true; }
            Player target = Bukkit.getPlayerExact(args[1]);
            if (target == null) { sender.sendMessage("§cOyuncu online değil."); return true; }

            int amount = parseAmount(args, 2, 1);
            giveBuiltItem(target, "protection-orb", amount);
            sender.sendMessage("§aVerildi: " + target.getName() + " -> protection-orb x" + amount);
            return true;
        }

        if (sub.equals("givedust")) {
            if (!sender.hasPermission(PERM_GIVE_DUST)) {
                if (sender instanceof Player p) sendKey(p, "commands.errors.no-permission", "§cNo permission.");
                return true;
            }
            if (args.length < 2) { sender.sendMessage("§cKullanım: /" + label + " givedust <player> [amount]"); return true; }
            Player target = Bukkit.getPlayerExact(args[1]);
            if (target == null) { sender.sendMessage("§cOyuncu online değil."); return true; }

            int amount = parseAmount(args, 2, 1);
            giveBuiltItem(target, "luck-dust", amount);
            sender.sendMessage("§aVerildi: " + target.getName() + " -> luck-dust x" + amount);
            return true;
        }

        if (sub.equals("giveparchment")) {
            if (!sender.hasPermission(PERM_GIVE_ALL)) {
                if (sender instanceof Player p) sendKey(p, "commands.errors.no-permission", "§cNo permission.");
                return true;
            }
            if (args.length < 3) {
                sender.sendMessage("§cKullanım: /" + label + " giveparchment <player> <universal|blessed|luckbooster|downgrade> [amount]");
                return true;
            }
            Player target = Bukkit.getPlayerExact(args[1]);
            if (target == null) { sender.sendMessage("§cOyuncu online değil."); return true; }

            String type = args[2].toLowerCase(Locale.ROOT);
            if (!PARCHMENT_TYPES.contains(type)) { sender.sendMessage("§cTip geçersiz. (universal, blessed, luckbooster, downgrade)"); return true; }

            int amount = parseAmount(args, 3, 1);
            giveBuiltItem(target, type + "-parchment", amount);
            sender.sendMessage("§aVerildi: " + target.getName() + " -> " + type + "-parchment x" + amount);
            return true;
        }

        sender.sendMessage("§cBilinmeyen alt komut. /" + label + " help");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return filter(List.of("gui", "reload", "debug", "setlevel", "givestone", "giveorb", "givedust", "giveparchment"), args[0]);
        }
        String sub = args[0].toLowerCase(Locale.ROOT);

        if (sub.equals("givestone") && args.length == 3) return filter(STONE_TIERS, args[2]);
        if (sub.equals("giveparchment") && args.length == 3) return filter(PARCHMENT_TYPES, args[2]);

        if ((sub.equals("givestone") || sub.equals("giveorb") || sub.equals("givedust") || sub.equals("giveparchment")) && args.length == 2) {
            return filterOnline(args[1]);
        }

        return List.of();
    }

    private List<String> filter(List<String> list, String token) {
        String t = token == null ? "" : token.toLowerCase(Locale.ROOT);
        List<String> out = new ArrayList<>();
        for (String s : list) if (s.toLowerCase(Locale.ROOT).startsWith(t)) out.add(s);
        return out;
    }

    private List<String> filterOnline(String token) {
        String t = token == null ? "" : token.toLowerCase(Locale.ROOT);
        List<String> out = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getName().toLowerCase(Locale.ROOT).startsWith(t)) out.add(p.getName());
        }
        return out;
    }

    private int parseAmount(String[] args, int idx, int def) {
        if (args.length <= idx) return def;
        try { return Math.max(1, Integer.parseInt(args[idx])); }
        catch (NumberFormatException ignored) { return def; }
    }

    private void giveBuiltItem(Player target, String path, int amount) {
        ItemStack built = YmlItemBuilder.build(plugin, items, path);
        if (built == null) return;

        for (int i = 0; i < amount; i++) {
            Map<Integer, ItemStack> left = target.getInventory().addItem(built.clone());
            if (!left.isEmpty()) {
                for (ItemStack it : left.values()) target.getWorld().dropItemNaturally(target.getLocation(), it);
            }
        }
    }

    private void sendKey(Player p, String key, String fallback) {
        String v = msg.get(key);
        if (v == null || v.isBlank()) v = fallback;
        p.sendMessage(v);
    }
}
