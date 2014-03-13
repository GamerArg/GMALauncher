/*
 * This file is part of Technic Launcher.
 * Copyright (C) 2013 Syndicate, LLC
 *
 * Technic Launcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Technic Launcher is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Technic Launcher.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.spoutcraft.launcher.skin.backgrounds;

import net.technicpack.launchercore.util.ResourceUtils;
import org.spoutcraft.launcher.skin.components.EnhancedBackground;

import javax.swing.JLabel;

public class HexxitBackground extends EnhancedBackground {
	private JLabel foreground;
	private HexxitClouds firstBackgroundClouds;
	private HexxitClouds secondBackgroundClouds;
	private HexxitClouds firstForegroundClouds;
	private HexxitClouds secondForegroundClouds;
	private HexxitFlash flash;
	private HexxitRain firstRain;
	private HexxitRain secondRain;

	public HexxitBackground() {
		super("hexxit");
	}

	@Override
	public void setVisible(boolean aFlag) {
		firstBackgroundClouds.setAnimating(aFlag);
		secondBackgroundClouds.setAnimating(aFlag);
		firstForegroundClouds.setAnimating(aFlag);
		secondForegroundClouds.setAnimating(aFlag);
		firstRain.setAnimating(aFlag);
		secondRain.setAnimating(aFlag);
		flash.setAnimating(aFlag);
		super.setVisible(aFlag);
	}
}
