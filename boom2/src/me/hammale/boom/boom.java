package me.hammale.boom;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Egg;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

public class boom extends JavaPlugin
{
	public boolean isHatching;
	public long delayTime;
	public FileConfiguration config;
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ArrayList<UUID> pigs = new ArrayList();
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ArrayList<String> nuker = new ArrayList();
	static int targetX;
    static int targetY;
    static int targetZ;
    static Location target;
    static Vector targetvector;
	
	public HashSet<String> active = new HashSet<String>();
	public HashSet<String> strike = new HashSet<String>();
	
	 private final nukePlayer eggeventlistener = new nukePlayer(this);
	 private final mineBlock mineBlock = new mineBlock(this);
	 private final boomPlayer boomPlayer = new boomPlayer(this);
	 private final boomEntity boomEntity = new boomEntity(this);
	 
  public void onDisable()
  {
    System.out.println(this + " is now disabled!");
  }

  public void onEnable()
  {
    System.out.println(this + " is now enabled!");
    loadConfiguration();
    airstrikeFolder();
    PluginManager pm = getServer().getPluginManager();
    pm.registerEvent(Event.Type.PLAYER_EGG_THROW, this.eggeventlistener, Event.Priority.Normal, this);
    pm.registerEvent(Event.Type.REDSTONE_CHANGE, this.mineBlock, Event.Priority.Normal, this);
    pm.registerEvent(Event.Type.BLOCK_PLACE, this.mineBlock, Event.Priority.Normal, this);
    pm.registerEvent(Event.Type.PLAYER_MOVE, this.boomPlayer, Event.Priority.Normal, this);
    pm.registerEvent(Event.Type.ENTITY_DEATH, this.boomEntity, Event.Priority.Normal, this);
  }
  public boolean onCommand(final CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(cmd.getName().equalsIgnoreCase("boom")){
			if(args.length != 0){
			if(args[0].equalsIgnoreCase("mine")){
			if(sender instanceof Player){
				sender.sendMessage(ChatColor.RED + "You have 3 seconds to get off this block!");
		    	Player p = (Player) sender;
		    	final Block b = p.getLocation().getBlock();
				getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
				    public void run() {
				    	Block b1 = b.getRelative(BlockFace.DOWN, 1);				    	
				    	addBlock(b, b1);
				    	sender.sendMessage(ChatColor.GREEN + "Mine set!");
				    }
				}, 60L);
			return true;
		}
			}else if (args[0].equalsIgnoreCase("torch")) {
				  	Player p = (Player) sender;
				  	if(active.contains(p.getName())){
				  		sender.sendMessage(ChatColor.RED + "Exiting torch mode...");
				  		active.remove(p.getName());
				  	}else{
				  		sender.sendMessage(ChatColor.GREEN + "Entering torch mode...");
				  		active.add(p.getName());
				  	}
					return true;			  
			  }else if (args[0].equalsIgnoreCase("nuke")) {
				  	Player p = (Player) sender;
				  	if(nuker.contains(p.getName())){
				  		sender.sendMessage(ChatColor.RED + "Nuke deactivated!");
				  		nuker.remove(p.getName());
				  		endNuke(p);
				  	}else{
				  		nuker.add(p.getName());
				  		sender.sendMessage(ChatColor.GREEN + "Nuke activated!");
				  		if(nukeIsFirst(p) == false){
				  			endNuke(p);
				  		}
				  		if(args.length == 1){
				  			int power = 50;
				  			addNuke(p, power);
				  		}else{
				  			int power = Integer.parseInt(args[1]);
				  			addNuke(p, power);
				  		}
				  	}
					return true;			  
			  }else if (args[0].equalsIgnoreCase("pig")) {
				  	Player p = (Player) sender;
				  		sender.sendMessage(ChatColor.LIGHT_PURPLE + "Launching pig...");
				  		launchPig(p);
					return true;			  
			  }else if (args[0].equalsIgnoreCase("zap")) {
				  if(args.length == 1){
				  	Player p = (Player) sender;
			  		sender.sendMessage(ChatColor.GREEN + "ZAP!");					  
					   Block b = p.getTargetBlock(null, 100);
					   Location l = b.getLocation();
					   b.getWorld().strikeLightning(l);
					   return true;				  
				  }else{
					  Player p = getServer().getPlayer(args[1]);
					  if(p.isOnline()){
						  sender.sendMessage(ChatColor.GREEN + "Zapping " + p.getName() + "!");
						  Location l = p.getLocation();
						  p.getWorld().strikeLightning(l);
					  }else{
						  sender.sendMessage(ChatColor.RED + p.getName() + "is not online!");
					  }
				  }
				return true;			  
		  }else if (args[0].equalsIgnoreCase("airstrike")) {
			  	Player p = (Player) sender;
			  	if(isFirst(p) == true){		  	
		  		sender.sendMessage(ChatColor.LIGHT_PURPLE + "Initiating airstrike! Please mark points!");
		  		strike.add(p.getName());
		  		return true;
			  	}else{
				  	removeAirFile(p);
			  		sender.sendMessage(ChatColor.LIGHT_PURPLE + "Initiating airstrike! Please mark points!");
			  		strike.add(p.getName());
			  		return true;
			  	}
		  }else{
				  return false;
			  }
		}else{
			return false;
		}
		}
		return false; 
	}
  
  private void removeAirFile(Player p) {
		File f = new File("plugins/Boom/airstrike/" + p.getName() + ".dat");
		boolean success = f.delete();
	    if (!success){
	      throw new IllegalArgumentException("[Boom] Deletion failed!");
	  	}
  }

public void loadConfiguration(){
	    //We must initialize the config
	    config = getConfig();
	    config.options().copyDefaults(true); 
	    
	    for(World w : this.getServer().getWorlds()) {
	    String wrld = w.getName();    
	    String path = "World." + wrld + "." + "MineBlockId";
	    config.addDefault(path, 87);
	    config.options().copyDefaults(true);
	    saveConfig();
	    }
	}
	    
	private void addBlock(Block b, Block b1) {
		b.setType(Material.STONE_PLATE);		
		try{
		File file = new File("plugins/Boom/mines.txt");
		  
        java.util.Scanner scan;  
        String str = null;  
  
        if (file.exists()) {  
  
            scan = new java.util.Scanner(file);  
            str = scan.nextLine();  
            while (scan.hasNextLine()) {  
                str = str.concat("\n" + scan.nextLine());  
            }  
        }  
     
		  int x = (int)b.getLocation().getX();
		  int y = (int)b.getLocation().getY();
		  int z = (int)b.getLocation().getZ();
		  
		  str = (x + "," + y + "," + z);
        
  
        PrintWriter out = new PrintWriter(new FileWriter(file, true));  
  
        out.println(str);  
        out.close();
		}catch (Exception e){
		  System.err.println("Error: " + e.getMessage());
		}	
		
	}
	
  public void eggThrown(final Location loc, final Player player, final World world, Egg egg, Event event, String power){
      int ipower = Integer.parseInt(power);    
	  player.getWorld().createExplosion(loc, ipower);
  }
  
	private void addNuke(Player p, int power) {	
		File f = new File("plugins/Boom/nuke");
		  boolean exists = f.exists();
		  if (!exists) {
			  try{
				  if(f.mkdir()){
					  System.out.println("[Boom] Directory created!");
				  }else{
					  System.out.println("[Boom] ERROR! Directory not created!");
				  } 
			  }catch(Exception e){
				  e.printStackTrace();
			  } 
		  }
		
		try{
		File file = new File("plugins/Boom/nuke/" + p.getName() + ".dat");
		  
        java.util.Scanner scan;  
        String str = null;  
  
        if (file.exists()) {  
  
            scan = new java.util.Scanner(file);  
            str = scan.nextLine();  
            while (scan.hasNextLine()) {  
                str = str.concat("\n" + scan.nextLine());  
            }  
        }  
		  
		  str = Integer.toString(power);
        
  
        PrintWriter out = new PrintWriter(new FileWriter(file, true));  
  
        out.println(str);  
        out.close();
		}catch (Exception e){
		  System.err.println("Error: " + e.getMessage());
		}	
		
	}
  
	public static void endNuke(Player p){
		File f = new File("plugins/Boom/nuke/" + p.getName() + ".dat");
		boolean success = f.delete();
	    if (!success){
	      throw new IllegalArgumentException("[Boom] Deletion failed!");
	  	}
	}
	
	public boolean nukeIsFirst(Player p) {		
		try{
		File file = new File("plugins/Boom/nuke/" + p.getName() + ".dat"); 
  
        if (file.exists()) { 
        	return false;
        }else{
        	return true;
        }

		}catch (Exception e){
		  System.err.println("Error: " + e.getMessage());
		  return false;
		}
	}
	
	public String readNuke(Player p){
		
		try{
			  FileInputStream fstream = new FileInputStream("plugins/Boom/nuke/" + p.getName() + ".dat");
			  DataInputStream in = new DataInputStream(fstream);
			  BufferedReader br = new BufferedReader(new InputStreamReader(in));
			  String strLine;
			  while ((strLine = br.readLine()) != null){
			  return strLine;
			  }
			  in.close();
			  return null;
			    }catch (Exception e){
			  System.err.println("Error: " + e.getMessage());
			  }
		return null;		
	}
	
  public void launchPig(Player p) {
	  Vector dir = p.getLocation().getDirection();
	    Vector v = new Vector(dir.getX() * 4.0D, 2.0D, dir.getZ() * 4.0D);

	    p.getWorld().playEffect(p.getLocation(), Effect.SMOKE, 1000, 1000);

	    LivingEntity e = p.getWorld().spawnCreature(p.getLocation(), CreatureType.PIG);
	    e.setVelocity(v);
	    UUID id = e.getUniqueId();
	    pigs.add(id);
	  }
  public void airstrikeFolder(){
	  File file = new File("plugins/Boom/airstrike");
	  boolean exists = file.exists();
	  if (!exists) {
		  try{
			  if(file.mkdir()){
				  System.out.println("[Boom] Directory created!");
			  }else{
				  System.out.println("[Boom] ERROR! Directory not created!");
			  } 
		  }catch(Exception e){
			  e.printStackTrace();
		  } 
	  }
  }
  
	public void addStrike(Player p, Block b) {		
		try{
		File file = new File("plugins/Boom/airstrike/" + p.getName() + ".dat");
		  
        java.util.Scanner scan;  
        String str = null;  
  
        if (file.exists()) { 
            scan = new java.util.Scanner(file);  
            str = scan.nextLine();  
            while (scan.hasNextLine()) {  
                str = str.concat("\n" + scan.nextLine());  
            }  
        }  
     
		  int x = (int)b.getLocation().getX();
		  int y = (int)b.getLocation().getY();
		  int z = (int)b.getLocation().getZ();
		  
		  str = (x + "," + y + "," + z);
        
        PrintWriter out = new PrintWriter(new FileWriter(file, true));  
  
        out.println(str);  
        out.close();
		}catch (Exception e){
		  System.err.println("Error: " + e.getMessage());
		}	
		
	}
	
	public static void endStrike(Player p){
		p.sendMessage(ChatColor.BLUE + "Airstrike complete!");
		File f = new File("plugins/Boom/airstrike/" + p.getName() + ".dat");
		boolean success = f.delete();
	    if (!success){
	      throw new IllegalArgumentException("[Boom] Deletion failed!");
	  	}
	}
  
	public boolean isFirst(Player p) {		
		try{
		File file = new File("plugins/Boom/airstrike/" + p.getName() + ".dat"); 
  
        if (file.exists()) { 
        	return false;
        }else{
        	return true;
        }

		}catch (Exception e){
		  System.err.println("Error: " + e.getMessage());
		  return false;
		}
	}
	
  public void travel(final Location l1, final Location l2, final Player p)
  {
    final World w = p.getWorld();
    double d = l1.distance(l2);
    
    int distance = (int) (d * 4);
    
    getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {

        public void run() {
        	loopThrough(l1, l2, w);
        	endStrike(p);
        }
    }, distance);    
  }

  private void loopThrough(Location loc1, Location loc2, World w) {
	  int i = 1;
	  int minx = Math.min(loc1.getBlockX(), loc2.getBlockX()),
	  miny = Math.min(loc1.getBlockY(), loc2.getBlockY()),
	  minz = Math.min(loc1.getBlockZ(), loc2.getBlockZ()),
	  maxx = Math.max(loc1.getBlockX(), loc2.getBlockX()),
	  maxy = Math.max(loc1.getBlockY(), loc2.getBlockY()),
	  maxz = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
	  for(int x = minx; x<=maxx;x++){
		  for(int y = miny; y<=maxy;y++){
			  for(int z = minz; z<=maxz;z++)
			  {
				  i++;
				  if(i%10 == 0){
					  Location l = w.getBlockAt(x, y, z).getLocation();
					  w.createExplosion(l, 4F);
				  }
			  }
		  }
	  }
  }
  
	public void startStrike(final Player p) {

		p.sendMessage(ChatColor.BLUE + "You have 5 seconds to clear the drop zone!");
		
	    getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {

	        public void run() {
	    		Location second = readAirstrike(p, 2);    		
	    		Location first = readAirstrike(p, 1);
	    		travel(first, second, p);
	        }
	    }, 100L);
	}
 
	public Location readAirstrike(Player p, int n){
		
		try{
			  FileInputStream fstream = new FileInputStream("plugins/Boom/airstrike/" + p.getName() + ".dat");
			  DataInputStream in = new DataInputStream(fstream);
			  BufferedReader br = new BufferedReader(new InputStreamReader(in));
			  String strLine;
			  int i = 1;
			  while ((strLine = br.readLine()) != null){
			  String delims = ",";
			  String[] cords = strLine.split(delims);

			  int x = Integer.parseInt(cords[0]);
			  int y = Integer.parseInt(cords[1]);
			  int z = Integer.parseInt(cords[2]);
			  
			  Location l = p.getWorld().getBlockAt(x, y, z).getLocation();
			  if(i == 1){
				  if(n == 1){
					  in.close();
					  return l;
				  }
			  }else{
				  if(n == 2){
					  in.close();
					  return l;
				  }
			  }
			  i++;
			  }
			  in.close();
			  return null;
			    }catch (Exception e){//Catch exception if any
			  System.err.println("Error: " + e.getMessage());
			  }
		return null;		
	}
	
}