package me.hammale.boom;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;

public class boomEntity extends EntityListener
{
  public boom plugin;

  public boomEntity(boom plugin)
  {
    this.plugin = plugin;
  }

  public void onEntityDeath(EntityDeathEvent e) {
    Entity entity = e.getEntity();
    if ((entity instanceof LivingEntity)) {
      LivingEntity le = (LivingEntity)entity;
      if (plugin.pigs.contains(le.getUniqueId())){
      le.getWorld().createExplosion(le.getLocation(), 5.0F);
      plugin.pigs.remove(le.getUniqueId());
      }
    }
  }
}