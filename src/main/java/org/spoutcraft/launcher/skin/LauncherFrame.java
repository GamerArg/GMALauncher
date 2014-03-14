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

import net.technicpack.launchercore.mirror.MirrorStore;
import org.spoutcraft.launcher.donor.DonorSite;
import net.technicpack.launchercore.install.AvailablePackList;
import net.technicpack.launchercore.install.InstalledPack;
import net.technicpack.launchercore.install.user.IAuthListener;
import net.technicpack.launchercore.install.user.User;
import net.technicpack.launchercore.install.user.UserModel;
import net.technicpack.launchercore.install.user.skins.ISkinListener;
import net.technicpack.launchercore.install.user.skins.SkinRepository;
import net.technicpack.launchercore.util.DownloadListener;
import net.technicpack.launchercore.util.ImageUtils;
import net.technicpack.launchercore.util.ResourceUtils;
import org.spoutcraft.launcher.launcher.Launcher;
import org.spoutcraft.launcher.skin.components.BackgroundImage;
import org.spoutcraft.launcher.skin.components.ImageButton;
import org.spoutcraft.launcher.skin.components.ImageHyperlinkButton;
import org.spoutcraft.launcher.skin.components.LiteButton;
import org.spoutcraft.launcher.skin.components.LiteProgressBar;
import org.spoutcraft.launcher.skin.components.RoundedBox;
import org.spoutcraft.launcher.skin.options.LauncherOptions;
import org.spoutcraft.launcher.skin.options.ModpackOptions;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static net.technicpack.launchercore.util.ResourceUtils.getResourceAsStream;

public class LauncherFrame extends JFrame implements ActionListener, KeyListener, MouseWheelListener, DownloadListener, ISkinListener, IAuthListener {
	public static final Color TRANSPARENT = new Color(45, 45, 45, 160);
	private static final long serialVersionUID = 1L;
	private static final int FRAME_WIDTH = 880;
	private static final int FRAME_HEIGHT = 520;
	private static final String OPTIONS_ACTION = "options";
	private static final String PACK_OPTIONS_ACTION = "packoptions";
	private static final String PACK_REMOVE_ACTION = "packremove";
	private static final String EXIT_ACTION = "exit";
	private static final String MINIMIZE_ACTION = "minimize";
	private static final String PACK_LEFT_ACTION = "packleft";
	private static final String PACK_RIGHT_ACTION = "packright";
	private static final String LAUNCH_ACTION = "launch";
	private static final String LOGOUT = "logout";
	private static final int SPACING = 7;
	public static URL icon = LauncherFrame.class.getResource("/org/spoutcraft/launcher/resources/icon.png");
	private LiteProgressBar progressBar;
	private LauncherOptions launcherOptions = null;
	private ModpackOptions packOptions = null;
	private ModpackSelector packSelector;
	private BackgroundImage packBackground;
	private ImageButton packOptionsBtn;
	private ImageButton packRemoveBtn;
	private ImageHyperlinkButton platform;
	private JLabel customName;
	private ImageButton launch;
	private JLabel userHead;
	private JLabel loggedInMsg;
	private LiteButton logout;
	private RoundedBox barBox;
	private NewsComponent news;
	private long previous = 0L;
	private User currentUser = null;

	private SkinRepository mSkinRepo;
	private UserModel mUserModel;
	private AvailablePackList mPackList;
	private DonorSite mDonorSite;
    private MirrorStore mirrorStore;

	public LauncherFrame(SkinRepository skinRepo, UserModel userModel, AvailablePackList packList, DonorSite donorSite, MirrorStore mirrorStore) {
		this.mSkinRepo = skinRepo;
		this.mUserModel = userModel;
		this.mPackList = packList;
		this.mDonorSite = donorSite;
        this.mirrorStore = mirrorStore;

		this.mUserModel.addAuthListener(this);

		initComponents(packList);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds((dim.width - FRAME_WIDTH) / 2, (dim.height - FRAME_HEIGHT) / 2, FRAME_WIDTH, FRAME_HEIGHT);
		setResizable(false);
		packBackground = new BackgroundImage(this, FRAME_WIDTH, FRAME_HEIGHT);
		this.addMouseListener(packBackground);
		this.addMouseMotionListener(packBackground);
		this.addMouseWheelListener(this);
		getContentPane().add(packBackground);
		this.setUndecorated(true);
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		
		this.setTitle("GamerArg Minecraft");
		InputStream iconStream = getResourceAsStream("/org/spoutcraft/launcher/resources/icon.png");
		try {
			this.setIconImage(ImageIO.read(iconStream));
		} catch (IOException e) {}
	}

	public void skinReady(User user) { }
	public void faceReady(User user) {
		if (this.currentUser != null && this.currentUser.getUsername().equals(user.getUsername()))
			userHead.setIcon(new ImageIcon(ImageUtils.scaleImage(this.mSkinRepo.getFaceImage(user), 48, 48)));
	}

	private void initComponents(AvailablePackList packList) {
		Font orbitron = getOrbitronMediumFont(12);

		// Launch button
		ImageButton launch = new ImageButton(ResourceUtils.getIcon("play.png", 170, 71), ResourceUtils.getIcon("hover_play.png", 170, 71));
		launch.setPressedIcon(ResourceUtils.getIcon("pressed_play.png"));
		launch.setBounds(FRAME_WIDTH-174, FRAME_HEIGHT-73, 170, 71);
		launch.setActionCommand(LAUNCH_ACTION);
		launch.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		launch.addActionListener(this);

		// User info area
		JLabel userArea = new JLabel();
		userArea.setBounds(698, 155, 173, 75);
		userArea.setIcon(ResourceUtils.getIcon("profile.png", 173, 38));

		userHead = new JLabel();
		userHead.setBounds(userArea.getX() + userArea.getWidth() - 53, userArea.getY() + 20, 48, 48);
		userHead.setIcon(new ImageIcon(ImageUtils.scaleImage(this.mSkinRepo.getDefaultFace(), 48, 48)));

		loggedInMsg = new JLabel("");
		loggedInMsg.setFont(orbitron);
		loggedInMsg.setBounds(userArea.getX() + 12, userArea.getY() + 15, 113, 30);
		loggedInMsg.setForeground(Color.white);

		logout = new LiteButton("Salir", new Color(0,0,0,0), new Color(0,0,0,0), new Color(0,0,0,0), Color.white, Color.white, Color.white);
		logout.setFont(orbitron);
		logout.setOpaque(false);
		logout.setForeground(Color.white);
		logout.setBounds(userArea.getX(), userArea.getY() + 27, 60, 30);
		logout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		logout.setActionCommand(LOGOUT);
		logout.addActionListener(this);

		// Pack Selector Background
		JLabel selectorBackground = new JLabel();
		selectorBackground.setBounds(15, 0, 200, 520);
		selectorBackground.setBackground(TRANSPARENT);
		selectorBackground.setOpaque(true);

		// Progress Bar Background box
		barBox = new RoundedBox(TRANSPARENT);
		barBox.setVisible(false);
		barBox.setBounds(FRAME_WIDTH-170, FRAME_HEIGHT-120, 160, 35);

		// Progress Bar
		progressBar = new LiteProgressBar(this);
		progressBar.setBounds(FRAME_WIDTH-160, FRAME_HEIGHT-117, 150, 20);
		progressBar.setVisible(false);
		progressBar.setStringPainted(true);
		progressBar.setOpaque(true);
		progressBar.setFont(orbitron);

		// News Items
		news = new NewsComponent();
		news.setBounds(50, 155, 280, 306);

		// Link background box
		int linkAreaX = 651;
		int linkAreaY = 250;
		int linkAreaWith = 265;
		int linkAreaHeight = 120;

		int linkWidth = linkAreaWith - (SPACING * 2);
		int linkHeight = (linkAreaHeight - (SPACING * 4)) / 3;
		
		// Donate link
		JButton donate = new ImageHyperlinkButton("http://www.gamerarg.com.ar/donar/");
		donate.setBounds(linkAreaX + SPACING, linkAreaY + SPACING, linkWidth, linkHeight);
		donate.setIcon(ResourceUtils.getIcon("button_donate.png"));
		donate.setRolloverIcon(ResourceUtils.getIcon("hover_button_donate.png"));
		donate.setContentAreaFilled(false);
		donate.setBorderPainted(false);
		donate.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		// Forums link
		JButton forums = new ImageHyperlinkButton("http://gamerarg.com.ar/foro");
		forums.setBounds(linkAreaX + SPACING, donate.getY() + donate.getHeight() + SPACING, linkWidth, linkHeight);
		forums.setIcon(ResourceUtils.getIcon("button_forum.png"));
		forums.setRolloverIcon(ResourceUtils.getIcon("hover_button_forum.png"));
		forums.setContentAreaFilled(false);
		forums.setBorderPainted(false);
		forums.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		
		// Browse link
		JButton browse = new ImageHyperlinkButton("http://gamerarg.com.ar");
		browse.setBounds(linkAreaX + SPACING, forums.getY() + forums.getHeight() + SPACING, linkWidth, linkHeight);
		browse.setIcon(ResourceUtils.getIcon("button_web.png"));
		browse.setRolloverIcon(ResourceUtils.getIcon("hover_button_web.png"));
		browse.setContentAreaFilled(false);
		browse.setBorderPainted(false);
		browse.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		
		int controlsY = 16;
		
		// Exit Button
		ImageButton exit = new ImageButton(ResourceUtils.getIcon("close.png", 20, 18), ResourceUtils.getIcon("hover_close.png", 20, 18));
		exit.setBounds(FRAME_WIDTH - 29, controlsY, 20, 18);
		exit.setActionCommand(EXIT_ACTION);
		exit.addActionListener(this);
		exit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		exit.setToolTipText("Cerrar");
		
		// Minimize Button
		ImageButton minimize = new ImageButton(ResourceUtils.getIcon("minimize.png", 20, 18), ResourceUtils.getIcon("hover_minimize.png", 20, 18));
		minimize.setBounds(exit.getX() - 22, controlsY, 20, 18);
		minimize.setActionCommand(MINIMIZE_ACTION);
		minimize.addActionListener(this);
		minimize.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		minimize.setToolTipText("Minimizar");
		
		// Options Button
		ImageButton options = new ImageButton(ResourceUtils.getIcon("options.png", 20, 18), ResourceUtils.getIcon("hover_options.png", 20, 18));
		options.setBounds(minimize.getX() - 20, controlsY, 20, 18);
		options.setActionCommand(OPTIONS_ACTION);
		options.addActionListener(this);
		options.addKeyListener(this);
		options.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		options.setToolTipText("Opciones");

		Container contentPane = getContentPane();
		contentPane.setLayout(null);

		// Pack Selector -- Do not remove --
		packSelector = new ModpackSelector(this, packList, mUserModel, mirrorStore);
		packSelector.setBounds(15, 0, 200, 520);

		contentPane.add(launch);
		contentPane.add(userHead);
		contentPane.add(loggedInMsg);
		contentPane.add(logout);
		contentPane.add(userArea);
		contentPane.add(progressBar);
		contentPane.add(browse);
		contentPane.add(forums);
		contentPane.add(donate);
		contentPane.add(news);
		contentPane.add(exit);
		contentPane.add(minimize);
		contentPane.add(options);
	}

	private void setIcon(JButton button, String iconName, int size) {
		try {
			button.setIcon(new ImageIcon(ImageUtils.scaleImage(ImageIO.read(ResourceUtils.getResourceAsStream("/org/spoutcraft/launcher/resources/" + iconName)), size, size)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Font getOrbitronLightFont(int size) {
		Font font;
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, getResourceAsStream("/org/spoutcraft/launcher/resources/orbitron-light-webfont.ttf")).deriveFont((float) size);
		} catch (Exception e) {
			e.printStackTrace();
			font = new Font("Arial", Font.PLAIN, size);
		}
		return font;
	}
	
	public static Font getOrbitronMediumFont(int size) {
		Font font;
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, getResourceAsStream("/org/spoutcraft/launcher/resources/orbitron-medium-webfont.ttf")).deriveFont((float) size).deriveFont(1);
		} catch (Exception e) {
			e.printStackTrace();
			font = new Font("Arial", Font.PLAIN, size);
		}
		return font;
	}
	
	public static Font getUbuntuFont(int size) {
		Font font;
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, getResourceAsStream("/org/spoutcraft/launcher/resources/Ubuntu-Regular-webfont.ttf")).deriveFont((float) size);
		} catch (Exception e) {
			e.printStackTrace();
			font = new Font("Arial", Font.PLAIN, size);
		}
		return font;
	}

	public static void setIcon(JLabel label, String iconName, int w, int h) {
		try {
			label.setIcon(new ImageIcon(ImageUtils.scaleImage(ImageIO.read(ResourceUtils.getResourceAsStream("/org/spoutcraft/launcher/resources/" + iconName)), w, h)));
		} catch (IOException e) {
			e.printStackTrace();
		}

}

	public NewsComponent getNews() {
		return news;
	}

	public BackgroundImage getBackgroundImage() {
		return packBackground;
	}

	public RoundedBox getBarBox() {
		return barBox;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof JComponent) {
			action(e.getActionCommand());
		}
	}

	private void action(String action) {
		if (action.equals(OPTIONS_ACTION)) {
			if (launcherOptions == null || !launcherOptions.isVisible()) {
				launcherOptions = new LauncherOptions();
				launcherOptions.setModal(true);
				launcherOptions.setVisible(true);
			}
		} else if (action.equals(PACK_REMOVE_ACTION)) {
			int result = JOptionPane.showConfirmDialog(this, "Are you sure you want to remove this pack?\n This will delete all files in: " + getSelector().getSelectedPack().getInstalledDirectory(), "Remove Pack", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if (result == JOptionPane.YES_OPTION) {
				getSelector().removePack();
			}
		} else if (action.equals(PACK_OPTIONS_ACTION)) {
			if (getSelector().getSelectedPack().getInfo() != null && (packOptions == null || !packOptions.isVisible())) {
				System.out.println("Opening options for " + getSelector().getSelectedPack());
				packOptions = new ModpackOptions(getSelector().getSelectedPack(), mPackList);
				packOptions.setModal(true);
				packOptions.setVisible(true);
			}
		} else if (action.equals(EXIT_ACTION)) {
			System.exit(0);
		} else if (action.equals(MINIMIZE_ACTION)) {
			this.setState(Frame.ICONIFIED);
		} else if (action.equals(PACK_LEFT_ACTION)) {
			getSelector().selectPreviousPack();
		} else if (action.equals(PACK_RIGHT_ACTION)) {
			getSelector().selectNextPack();
		} else if (action.equals(LAUNCH_ACTION)) {
			if (Launcher.isLaunching()) {
				return;
			}

			InstalledPack pack = packSelector.getSelectedPack();

			if (!pack.getName().equals("addpack") && (pack.isLocalOnly() || pack.getInfo() != null)) {
				Launcher.launch(currentUser, pack, pack.getBuild());
			}
		} else if (action.equals(LOGOUT)) {
			if (Launcher.isLaunching()) {
				return;
			}

			mUserModel.setCurrentUser(null);
		}
	}

	public ModpackSelector getSelector() {
		return packSelector;
	}

	@Override
	public void stateChanged(final String status, final float progress) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				int intProgress = Math.round(progress);
				progressBar.setValue(intProgress);
				String text = status;
				if (text.length() > 12) {
					text = text.substring(0, 12);
				}
				progressBar.setString(intProgress + "% " + text);
			}
		});
	}

	public JProgressBar getProgressBar() {
		return progressBar;
	}

	public void disableForm() {
	}

	public void enableForm() {
		progressBar.setVisible(false);
		lockLoginButton(true);
	}

	public void lockLoginButton(boolean unlock) {
		launch.setEnabled(unlock);
		packRemoveBtn.setEnabled(unlock);
		packOptionsBtn.setEnabled(unlock);
	}

	public ImageButton getPackOptionsBtn() {
		return packOptionsBtn;
	}

	public ImageButton getPackRemoveBtn() {
		return packRemoveBtn;
	}

	public ImageHyperlinkButton getPlatform() {
		return platform;
	}

	public void enableComponent(JComponent component, boolean enable) {
		component.setVisible(enable);
		component.setEnabled(enable);
	}

	public void userChanged(User user) {
		this.currentUser = user;

		if (user == null)
		{
			this.setVisible(false);
			return;
		}
		
		if (!currentUser.isOffline()) {
			mUserModel.setLastUser(currentUser);
		}

		loggedInMsg.setText(currentUser.getDisplayName());

		this.faceReady(currentUser);
		this.setVisible(true);
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (e.getWhen() != previous) {
			if (e.getUnitsToScroll() > 0) {
				getSelector().selectNextPack();
			} else if (e.getUnitsToScroll() < 0) {
				getSelector().selectPreviousPack();
			}
			this.previous = e.getWhen();
		}

	}
}
