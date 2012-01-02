package me.hammale.boom;

import org.bukkit.Location;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerEggThrowEvent;

public class nukePlayer extends PlayerListener {

public static boom plugin;
    public nukePlayer(boom instance) {
    plugin = instance;
    }
      
    public void onPlayerEggThrow(PlayerEggThrowEvent event) {
     Egg egg = event.getEgg();
     Location loc = egg.getLocation();
     Player p = event.getPlayer();
     if(plugin.nuker.contains(p.getName())){
    	 plugin.eggThrown(loc, p, p.getWorld(), egg, event, plugin.readNuke(p));
     }
    }
    
}