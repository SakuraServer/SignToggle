/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.ja731j.signtoggle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author ja731j
 */
public class SignToggle extends JavaPlugin implements Listener {

    static HashSet<String> inuse;

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("The SignToggle plugin has been loaded");
        inuse = new HashSet<String>();
    }

    @Override
    public void onDisable() {
        getLogger().info("The SignToggle plugin has been unloaded");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player) || !cmd.getName().equalsIgnoreCase("signtoggle")) {
            return false;
        }
        Player p = (Player)sender;
        if(!p.hasPermission("signtoggle")){
            p.sendMessage("You don't have the permissions!");
            return true;
        }
        
        if (inuse.contains(sender.getName())) {
            Bukkit.broadcastMessage("Disabled SignToggle");
            inuse.remove(sender.getName());
        } else {
            Bukkit.broadcastMessage("Enabled SignToggle");
            inuse.add(sender.getName());
        }
        return true;
    }

    @EventHandler
    public void onHit(PlayerInteractEvent e) {
        Block b = e.getClickedBlock();

        if (e.getPlayer() == null || !e.getPlayer().hasPermission("signtoggle")) {
            return;
        }

        if (!inuse.contains(e.getPlayer().getName())) {
            return;
        }

        if (b != null) {
            if (b.getType() == Material.WALL_SIGN) {
                org.bukkit.block.Sign s = (org.bukkit.block.Sign) b.getState();
                String[] lines = s.getLines();
                BlockFace face = ((org.bukkit.material.Sign) (s.getData())).getFacing();

                b.setType(Material.SIGN_POST);
                s = (org.bukkit.block.Sign) b.getState();
                ((org.bukkit.material.Sign) (s.getData())).setFacingDirection(face);
                for (int i = 0; i < 4; i++) {

                    s.setLine(i, lines[i]);
                }
                s.update();
            } else if (b.getType() == Material.SIGN_POST) {
                org.bukkit.block.Sign s = (org.bukkit.block.Sign) b.getState();
                String[] lines = s.getLines();
                BlockFace face = ((org.bukkit.material.Sign) (s.getData())).getFacing();

                b.setType(Material.WALL_SIGN);
                s = (org.bukkit.block.Sign) b.getState();
                ((org.bukkit.material.Sign) (s.getData())).setFacingDirection(face);
                for (int i = 0; i < 4; i++) {
                    s.setLine(i, lines[i]);
                }
                s.update();
            }
        }

    }
}
