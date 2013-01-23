package net.ja731j.signtoggle;

import java.util.HashSet;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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
    private static HashSet<String> inuse;

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("The SignToggle plugin has been loaded");
        inuse = new HashSet<String>();
    }

    @Override
    public void onDisable() {
        inuse.clear();
        getLogger().info("The SignToggle plugin has been unloaded");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!cmd.getName().equalsIgnoreCase("signtoggle")) {
            return false;
        }
        if (!(sender instanceof Player)){
            sender.sendMessage("This command cannot run from console!");
            return true;
        }
        
        final Player p = (Player) sender;
        if (!p.hasPermission("signtoggle.use")) {
            p.sendMessage("You don't have the permissions!");
            return true;
        }

        if (inuse.contains(sender.getName())) {
            p.sendMessage("Disabled SignToggle");
            inuse.remove(sender.getName());
        } else {
            p.sendMessage("Enabled SignToggle");
            inuse.add(sender.getName());
        }
        return true;
    }

    @EventHandler
    public void onHit(final PlayerInteractEvent e) {
        if (e.isCancelled()){
            return;
        }
        
        if (e.getPlayer() == null || !e.getPlayer().hasPermission("signtoggle.use")) {
            return;
        }

        //If not enabled don't proceed
        if (!inuse.contains(e.getPlayer().getName())) {
            return;
        }

        final Block b = e.getClickedBlock();

        //If target block is not null and is some kind of sign, proceed
        if (b != null  && (b.getType() == Material.WALL_SIGN || b.getType() == Material.SIGN_POST)) {
            //Get sign data
            org.bukkit.block.Sign s = (org.bukkit.block.Sign) b.getState();
            String[] lines = s.getLines();
            if (lines[0].trim().equalsIgnoreCase("[private]")){
                e.getPlayer().sendMessage("This is a private sign");
                return;
            }
            BlockFace face = ((org.bukkit.material.Sign) (s.getData())).getFacing();

            //Switch between sign posts and wall signs
            if (b.getType() == Material.WALL_SIGN) {
                b.setType(Material.SIGN_POST);
            } else {
                b.setType(Material.WALL_SIGN);
            }

            //Get block.Sign again because it has changed
            s = (org.bukkit.block.Sign) b.getState();
            
            //Set sign data
            ((org.bukkit.material.Sign) (s.getData())).setFacingDirection(face);
            for (int i = 0; i < 4; i++) {
                s.setLine(i, lines[i]);
            }
            s.update();

            // simply take a log
            final Location l = b.getLocation();
            if (!l.getWorld().getName().equals("entrance")){
                System.out.println("[SignToggle] " + e.getPlayer().getName() + " toggled sign at ("
                                   + l.getWorld().getName() + ": "+l.getBlockX()+", "+l.getBlockY()+", "+l.getBlockZ()+")");
            }
        }
    }
}
