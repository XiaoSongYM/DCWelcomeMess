//------------------------- DCWelcomeMess 加载成功！-------------------------
package com.MingJame.dcwelcomemess;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class DCWelcomeMess extends JavaPlugin implements Listener, CommandExecutor {

    private FileConfiguration customConfig;

    @Override
    public void onEnable() {
        // 注册事件监听器
        getServer().getPluginManager().registerEvents(this, this);
        // 注册命令执行器
        this.getCommand("dcwel").setExecutor(this);
        // 控制台输出信息
        getLogger().info("------------------------- DCWelcomeMess 加载成功！-------------------------");
        // 加载配置
        loadCustomConfig();
    }

    private void loadCustomConfig() {
        File customConfigFile = new File(getDataFolder(), "messages.yml");
        if (!customConfigFile.exists()) {
            customConfigFile.getParentFile().mkdirs();
            saveResource("messages.yml", false);
        }
        customConfig = YamlConfiguration.loadConfiguration(customConfigFile);
        try {
            customConfig.load(customConfigFile);
        } catch (Exception e) {
            getLogger().severe("无法加载 messages.yml 文件: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String getMessage(String key, Player player, int onlineCount) {
        String message = customConfig.getString(key, "默认消息")
                .replace("{player}", player.getName())
                .replace("{online}", String.valueOf(onlineCount))
                .replace("{max}", String.valueOf(Bukkit.getMaxPlayers()));
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        // 获取玩家加入后的在线人数
        int onlineCountAfterJoin = Bukkit.getOnlinePlayers().size();
        String welcomeMessage = getMessage("welcome-message", player, onlineCountAfterJoin);
        Bukkit.broadcastMessage(welcomeMessage);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        // 获取玩家离开后的在线人数
        int onlineCountAfterQuit = Bukkit.getOnlinePlayers().size() - 1;
        String quitMessage = getMessage("quit-message", player, onlineCountAfterQuit);
        Bukkit.broadcastMessage(quitMessage);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("dcwel")) {
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                loadCustomConfig();
                sender.sendMessage(ChatColor.GREEN + "消息文件重载成功.");
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "用法: /dcwel reload");
                return false;
            }
        }
        return false;
    }
}