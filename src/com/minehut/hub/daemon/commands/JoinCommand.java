package com.minehut.hub.daemon.commands;

import com.minehut.core.Core;
import com.minehut.core.command.Command;
import com.minehut.core.player.Rank;
import com.minehut.core.util.common.chat.C;
import com.minehut.core.util.common.chat.F;
import com.minehut.daemon.Kingdom;
import com.minehut.hub.Hub;
import com.minehut.hub.daemon.DaemonManager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by luke on 7/6/15.
 */
public class JoinCommand extends Command {
    private DaemonManager daemonManager;

    public JoinCommand(JavaPlugin plugin, DaemonManager daemonManager) {
        super(plugin, "join", Rank.regular);
        this.daemonManager = daemonManager;
    }

    @Override
    public boolean call(Player player, ArrayList<String> args) {

        /* Make sure they specify a name */
        if (args == null || args.size() < 1) {
            F.message(player, "Please specify a name!");
            F.message(player, C.gray + "Example: " + C.aqua + "/join Minehut");
            return true;
        }

        Bukkit.getScheduler().runTaskAsynchronously(Hub.getInstance(), new JoinCommandRunnable(player.getUniqueId(), args.get(0)));

        return false;
    }
    
    public class JoinCommandRunnable implements Runnable {

    	private UUID playerUUID;
    	private String kingdomName;
    	
    	public JoinCommandRunnable(UUID playerUUID, String kingdomName) {
    		this.playerUUID = playerUUID;
    		this.kingdomName = kingdomName;
    	}
    	
    	 @Override
         public void run() {
    		 Player player = Bukkit.getPlayer(this.playerUUID);
             Kingdom kingdom = daemonManager.daemonFactory.getKingdom(this.kingdomName);
             if (kingdom == null) {
                 F.warning(player, C.red + this.kingdomName + C.gray + " is not a valid kingdom");
                 F.warning(player, "You can see what kingdom you own with " + C.purple + "/mykingdom");
             } else {
                 String startup = daemonManager.daemonFactory.getStartup(kingdom);
                 if (startup.equalsIgnoreCase("100%")) {
                     Core.getInstance().getStatusManager().sendToKingdom(player, kingdom.getName());
                 } else {
//                     F.log("Startup Return: " + startup);
                     if (!daemonManager.checkForStartupMonitor(kingdom, player)) {
                         if(daemonManager.daemonFactory.startKingdom(kingdom)) {
                             daemonManager.addStartupMonitor(kingdom, player);
                         } else {
                             F.warning(player, "Kingdom Servers are currently maxed out!");
                             F.warning(player, "Once someone leaves their server, you will be able to join yours.");
                         }
                     }

                 }
             }
         }
    	
    }
    
}
