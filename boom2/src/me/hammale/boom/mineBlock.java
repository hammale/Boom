package me.hammale.boom;

import java.util.Random;
import java.io.*;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.util.Vector;

public class mineBlock extends BlockListener {

	Random rand = new Random();
	double speed = 0.5d;
	double angle = 3.0d;
	
public static boom plugin;
    public mineBlock(boom instance) {
    plugin = instance;
    }
      
    public void onBlockRedstoneChange(BlockRedstoneEvent event) {
    	final Block b = event.getBlock();
    	 
    	if (isPressurePlate(b.getType())) {
    	if(checkLocation(b)){
    	if(event.getNewCurrent() == 1) {
            TNTPrimed tnt = event.getBlock().getLocation().getWorld().spawn(
            event.getBlock().getLocation(), TNTPrimed.class);
            tnt.setVelocity(new Vector((rand.nextFloat() - 0.5f) / angle, speed, (rand.nextFloat() - 0.5f)
            / angle));
            tnt.setFuseTicks(5);
    	}
    	}
    	}
    	}
    	 
    	public static boolean isPressurePlate(Material mat) {
    	return (mat == Material.STONE_PLATE);
    	}
    	public static boolean checkLocation(Block b){
    		
    		try{
    			  // Open the file that is the first 
    			  // command line parameter
    			  FileInputStream fstream = new FileInputStream("plugins/Boom/mines.txt");
    			  // Get the object of DataInputStream
    			  DataInputStream in = new DataInputStream(fstream);
    			  BufferedReader br = new BufferedReader(new InputStreamReader(in));
    			  String strLine;
    			  while ((strLine = br.readLine()) != null){
    			  String delims = ",";
    			  String[] cords = strLine.split(delims);

    			  int x = Integer.parseInt(cords[0]);
    			  int y = Integer.parseInt(cords[1]);
    			  int z = Integer.parseInt(cords[2]); 			  
    			  Location l = b.getWorld().getBlockAt(x, y, z).getLocation();
    			  if(b.getLocation().getX() == l.getX() && b.getLocation().getY() == l.getY() && b.getLocation().getZ() == l.getZ()){
    				  in.close();
    				  return true;
    			  }
    			  }
    			  in.close();
    			  return false;
    			    }catch (Exception e){//Catch exception if any
    			  System.err.println("Error: " + e.getMessage());
    			  return false;
    			  }
    	}
    	
    	public void onBlockPlace(BlockPlaceEvent e){
    		if(plugin.strike.contains(e.getPlayer().getName()) && e.getBlock().getTypeId() == 76){
    			Block b = e.getPlayer().getLocation().getBlock();
    			if(plugin.isFirst(e.getPlayer()) == true){
    				plugin.addStrike(e.getPlayer(), b);
    				e.getPlayer().sendMessage(ChatColor.GREEN + "First point set, move to second location!");
    			}else{
    				plugin.strike.remove(e.getPlayer().getName());
    				plugin.addStrike(e.getPlayer(), b);
    				e.getPlayer().sendMessage(ChatColor.GREEN + "Second point set, clear the drop zone!");
    				plugin.startStrike(e.getPlayer());
    			}
    		}
    	}
}