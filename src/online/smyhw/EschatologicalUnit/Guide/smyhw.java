package online.smyhw.EschatologicalUnit.Guide;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;


import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;


public class smyhw extends JavaPlugin implements Listener 
{
	public static Plugin smyhw_;
	public static Logger loger;
	public static FileConfiguration configer;
	public static String prefix;
	public static HashMap<String,Continued> PointMap ;
	@Override
    public void onEnable() 
	{
		getLogger().info("EschatologicalUnit.SpawnMobs加载");
		getLogger().info("正在加载环境...");
		loger=getLogger();
		configer = getConfig();
		smyhw_=this;
		PointMap = new HashMap<String,Continued>();
		new CancleT();
		getLogger().info("正在加载配置...");
		saveDefaultConfig();
		prefix = configer.getString("config.prefix");
		getLogger().info("正在注册监听器...");
		Bukkit.getPluginManager().registerEvents(this,this);
		getLogger().info("EschatologicalUnit.Guide加载完成");
    }

	@Override
    public void onDisable() 
	{
		getLogger().info("EschatologicalUnit.Guide卸载");
    }
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
        if (cmd.getName().equals("euG"))
        {
                if(!sender.hasPermission("eu.plugin")) 
                {
                	sender.sendMessage(prefix+"非法使用 | 使用者信息已记录，此事将被上报");
                	loger.warning(prefix+"使用者<"+sender.getName()+">试图非法使用指令<"+args+">{权限不足}");
                	return true;
                }
                if(args.length<1) 
                {
                	sender.sendMessage(prefix+"非法使用 | 使用者信息已记录，此事将被上报");
                	loger.warning(prefix+"使用者<"+sender.getName()+">试图非法使用指令<"+args+">{参数不足}");
                	return true;
                }
                switch(args[0])
                {
                case "reload":
                {
                	reloadConfig();
                	configer=getConfig();
                	sender.sendMessage(prefix+"重载配置文件...");
                	return true;
                }
                case"set":
                {
                	if(args.length<2) {CSBZ(sender);return true;}
                	String PointName = args[1];
                	Location zb = ((Player)sender).getLocation();
                	sender.sendMessage(prefix+"导航点<"+PointName+">已经设定为<x="+(int)zb.getX()+";y="+(int)zb.getY()+";z="+(int)zb.getZ()+">");
                	configer.set("Guide."+"."+PointName+".x", (int)zb.getX());
                	configer.set("Guide."+"."+PointName+".y", (int)zb.getY());
                	configer.set("Guide."+"."+PointName+".z", (int)zb.getZ());
                	configer.set("Guide."+"."+PointName+".world", zb.getWorld().getName());
                	saveConfig();
                	return true;
                }
                case "do":
                {
                	if(args.length<2) {CSBZ(sender);return true;}
                	Continued temp1 = new Continued(args[1]);
                	if(configer.getString("Guide."+"."+args[1]+".world")==null)
                	{
                		sender.sendMessage(prefix+"导航点<"+args[1]+">不存在");
                		return true;
                	}
                	if(PointMap.containsKey(args[1]))
                	{
                		sender.sendMessage(prefix+"导航点<"+args[1]+">已经处于激活状态,无法重新激活");
                		return true;
                	}
                	PointMap.put(args[1], temp1);
                	sender.sendMessage(prefix+"导航点<"+args[1]+">已激活");
                	return true;
                }
                
                }
                return true;                                                       
        }
       return false;
	}
	
	static void CSBZ(CommandSender sender)
	{
		sender.sendMessage(prefix+"非法使用 | 使用者信息已记录，此事将被上报");
		loger.warning(prefix+"使用者<"+sender.getName()+">试图非法使用指令{参数不足}");
	}
	
}

class Continued extends BukkitRunnable
{
	String id;
	Location zb;
	World bworld;
	public Continued(String id)
	{
		this.id=id;
		String worldname = smyhw.configer.getString("Guide."+id+".world");
		bworld = Bukkit.getWorld(worldname);
		Double x = smyhw.configer.getDouble("Guide."+"."+id+".x");
		Double y = smyhw.configer.getDouble("Guide."+"."+id+".y");
		Double z = smyhw.configer.getDouble("Guide."+"."+id+".z");
		zb = new Location(bworld,x,y,z);
		this.runTaskTimer(smyhw.smyhw_,0,5);
	}

	@Override
	public void run() 
	{
		bworld.playEffect(zb, Effect.MOBSPAWNER_FLAMES, 1);
	}
	
}

//监测玩家是否到导航点
class CancleT extends BukkitRunnable
{
	public CancleT()
	{
		this.runTaskTimer(smyhw.smyhw_,0,20);
	}

	@Override
	public void run() 
	{
		Set<String> temp1 = smyhw.PointMap.keySet();
		Iterator<String> temp2 = temp1.iterator();
		while(temp2.hasNext())
		{
			String temp3 = temp2.next();
			Continued temp4 = smyhw.PointMap.get(temp3);
			if(!temp4.zb.getNearbyPlayers(1).isEmpty())
			{
				temp4.cancel();
				smyhw.loger.info(smyhw.prefix+"导航点<"+temp4.id+">已被玩家到达");
				smyhw.PointMap.remove(temp3);
				List temp5 = smyhw.configer.getStringList("Guide."+temp4.id+".cmd");
				temp4.bworld.playEffect(temp4.zb, Effect.CLICK1, 1);
				if(temp5==null) {continue;}
				Iterator<String> temp6 = temp5.iterator();
				while(temp6.hasNext())
				{
					String temp7 = temp6.next();
					Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),temp7);
				}//end 执行指令
			}//end 玩家到达的导航点
		}//end 遍历Map
	}
	
}