package me.hammale.boom;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

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
	public void onPlayerInteract(PlayerInteractEvent event) {
	    Player player = event.getPlayer();
	    if(plugin.flame.contains(player.getName())){
	    ItemStack stack = event.getItem();
	    if ((event.getAction() == Action.RIGHT_CLICK_AIR) && 
	      (stack != null))
	    {
	      if (stack.getTypeId() == 259)
	      {
	        plugin.throwFlame(player);
	      }
	    }
	  }
	}   
}