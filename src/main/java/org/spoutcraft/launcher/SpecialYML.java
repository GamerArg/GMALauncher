package org.spoutcraft.launcher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.bukkit.util.config.Configuration;

public class SpecialYML {
	private static volatile boolean updated = false;
	private static File specialYML = new File(PlatformUtils.getWorkingDirectory(), "spoutcraft" + File.separator + "special.yml");
	private static Object key = new Object();
	
	public static Configuration getSpecialYML() {
		updateSpecialYMLCache();
		Configuration config = new Configuration(specialYML);
		config.load();
		return config;
	}
	
	public static void updateSpecialYMLCache() {
		if (!updated) {
			synchronized(key) {
				String urlName = MirrorUtils.getMirrorUrl("special.yml", "http://dl.getspout.org/yml/special.yml", null);
				if (urlName != null) {
					try {
						
						String current = null;
						if (specialYML.exists()) {
							try {
								Configuration config = new Configuration(specialYML);
								config.load();
								current = config.getString("current");
							}
							catch (Exception ex){
								ex.printStackTrace();
							}
						}
						
						URL url = new URL(urlName);
						HttpURLConnection con = (HttpURLConnection)(url.openConnection());
						System.setProperty("http.agent", "");
						con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/534.30 (KHTML, like Gecko) Chrome/12.0.742.100 Safari/534.30");
						GameUpdater.copy(con.getInputStream(), new FileOutputStream(specialYML));
						
						Configuration config = new Configuration(specialYML);
						config.load();
						if (current != null) {
							config.setProperty("current", current);
							config.save();
						}
						
					}
					catch (IOException e) {
						e.printStackTrace();
					}
				}
				updated = true;
			}
		}
	}
}