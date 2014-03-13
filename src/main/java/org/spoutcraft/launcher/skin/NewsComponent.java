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
package org.spoutcraft.launcher.skin;

import net.technicpack.launchercore.exception.RestfulAPIException;
import net.technicpack.launchercore.restful.PlatformConstants;
import net.technicpack.launchercore.restful.RestObject;
import net.technicpack.launchercore.restful.platform.Article;
import net.technicpack.launchercore.restful.platform.News;
import net.technicpack.launchercore.util.ResourceUtils;
import net.technicpack.launchercore.util.Utils;
import org.spoutcraft.launcher.skin.components.HyperlinkJTextPane;
import org.spoutcraft.launcher.skin.components.ImageHyperlinkButton;
import org.spoutcraft.launcher.skin.components.RoundedBox;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTextArea;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.List;
import java.util.logging.Level;

public class NewsComponent extends JComponent {
	private static final long serialVersionUID = 1L;

	public NewsComponent() {
		GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(LauncherFrame.getMinecraftFont(10));
	}

	public void loadArticles() {
		try {
			List<Article> articles = RestObject.getRestObject(News.class, PlatformConstants.NEWS).getNews();
			setupArticles(articles);
		} catch (RestfulAPIException e) {
			Utils.getLogger().log(Level.WARNING, "Unable to load news, hiding news section", e);
			this.setVisible(false);
			this.setEnabled(false);
		}
	}

	private void setupArticles(List<Article> articles) {
		Font articleFont = LauncherFrame.getUbuntuFont(10);
		int width = getWidth() - 16;
		int height = (getHeight()-50) / 5 - 16;

		for (int i = 0; i < 6; i++) {
			Article article = articles.get(i);
			String date = article.getDate();
			String title = article.getDisplayTitle();
			
			HyperlinkJTextPane link = new HyperlinkJTextPane("(" + date + ") " + title, article.getUrl());
			link.setFont(articleFont);
			link.setForeground(Color.WHITE);
			link.setBackground(new Color(255, 255, 255, 0));
			link.setBounds(8, 8 + ((height + 8) * i), width, 32);
			link.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			
			JTextArea summaryText = new JTextArea();
			summaryText.setText(article.getSummary());
			summaryText.setEditable(false);
			summaryText.setFont(articleFont);
			summaryText.setBounds(22, 8*3 + ((height + 8) * i), width-15, height-10);
			summaryText.setLineWrap(true);
			summaryText.setOpaque(false);
			summaryText.setForeground(Color.GRAY);			
			
			this.add(summaryText);
			this.add(link);
		}
		
		JButton newsLink = new ImageHyperlinkButton("http://gamerarg.com.ar/foro");
		newsLink.setBounds(76, getHeight()-40, width + 24, 40);
		newsLink.setIcon(ResourceUtils.getIcon("button_news.png"));
		newsLink.setRolloverIcon(ResourceUtils.getIcon("hover_button_news.png"));
		newsLink.setContentAreaFilled(false);
		newsLink.setBorderPainted(false);
		newsLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		
		this.add(newsLink);
		this.repaint();
	}
}
