package me.hammale.boom;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;

public class boomPlayer extends PlayerListener {

	  public boom plugin;
	
	  public boomPlayer(boom plugin)
	  {
	    this.plugin = plugin;
	  }
	  int i = 1;
	@Override
	public void onPlayerMove(final PlayerMoveEvent e) {
		Block down = e.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN, 1);
		if(plugin.active.contains(e.getPlayer().getName()) && down.getType() != Material.AIR){
			Block b = e.getPlayer().getLocation().getBlock();
			b.setType(Material.FIRE);
		}
	}
}