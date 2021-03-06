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
package org.spoutcraft.launcher.skin.options;

import net.technicpack.launchercore.exception.RestfulAPIException;
import net.technicpack.launchercore.util.ResourceUtils;
import org.spoutcraft.launcher.Memory;
import org.spoutcraft.launcher.entrypoint.SpoutcraftLauncher;
import net.technicpack.launchercore.util.Settings;
import org.spoutcraft.launcher.skin.LauncherFrame;
import org.spoutcraft.launcher.skin.components.ImageButton;
import org.spoutcraft.launcher.skin.components.LiteButton;
import org.spoutcraft.launcher.skin.components.LiteTextBox;
import org.spoutcraft.launcher.updater.LauncherInfo;
import org.spoutcraft.launcher.util.DesktopUtils;
import net.technicpack.launchercore.util.ZipUtils;
import net.technicpack.launchercore.util.Utils;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import net.technicpack.launchercore.util.LaunchAction;

public class LauncherOptions extends JDialog implements ActionListener, MouseListener, MouseMotionListener {
	private static final long serialVersionUID = 1L;
	private static final int FRAME_WIDTH = 300;
	private static final int FRAME_HEIGHT = 238;
	private static final String LAUNCHER_PREPEND = "Version:    ";
	private static final String QUIT_ACTION = "quit";
	private static final String SAVE_ACTION = "save";
	private static final String LOGS_ACTION = "logs";
	private static final String CONSOLE_ACTION = "console";
	private static final String CHANGEFOLDER_ACTION = "changefolder";
	private static final String BETA_ACTION = "beta";
	private static final String STABLE_ACTION = "stable";
	private static final String ESCAPE_ACTION = "escape";
	private JLabel background;
	private JLabel build;
	private LiteButton logs;
	private JComboBox memory;
	private JComboBox onLaunch;
	private JRadioButton beta;
	private JRadioButton stable;
	private JFileChooser fileChooser;
	private LiteButton console;
	private int mouseX = 0, mouseY = 0;
	private String installedDirectory;
	private LiteTextBox packLocation;
	private boolean directoryChanged = false;
	private boolean streamChanged = false;
	private boolean consoleToggle = false;
	private String buildStream = "stable";

	public LauncherOptions() {
		setTitle("Launcher Options");
		setSize(FRAME_WIDTH, FRAME_HEIGHT);
		addMouseListener(this);
		addMouseMotionListener(this);
		setResizable(false);
		setUndecorated(true);
		initComponents();
	}

	private void initComponents() {
		Font ubuntu = LauncherFrame.getUbuntuFont(12);
		Font orbitron = LauncherFrame.getOrbitronMediumFont(12);

		KeyStroke escape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
		Action escapeAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		};

		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escape, ESCAPE_ACTION);
		getRootPane().getActionMap().put(ESCAPE_ACTION, escapeAction);

		background = new JLabel();
		background.setBounds(0, 0, FRAME_WIDTH, FRAME_HEIGHT);
		LauncherFrame.setIcon(background, "options_background.png", background.getWidth(), background.getHeight());

		ImageButton optionsQuit = new ImageButton(ResourceUtils.getIcon("close.png", 20, 18), ResourceUtils.getIcon("hover_close.png", 20, 18));
		optionsQuit.setRolloverIcon(ResourceUtils.getIcon("hover_close.png", 20, 18));
		optionsQuit.setBounds(FRAME_WIDTH - 22, 12, 20, 18);
		optionsQuit.setActionCommand(QUIT_ACTION);
		optionsQuit.addActionListener(this);
		optionsQuit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		optionsQuit.setToolTipText("Cerrar");

		JLabel title = new JLabel("Opciones");
		title.setFont(orbitron.deriveFont(14F));
		title.setBounds(50, 10, 200, 20);
		title.setForeground(Color.WHITE);
		title.setHorizontalAlignment(SwingConstants.CENTER);

		build = new JLabel(LAUNCHER_PREPEND + SpoutcraftLauncher.getLauncherBuild());
		build.setBounds(10, title.getY() + title.getHeight() + 10, FRAME_WIDTH - 20, 20);
		build.setFont(ubuntu);
		build.setForeground(Color.WHITE);
		build.setHorizontalAlignment(SwingConstants.LEFT);

		JLabel memoryLabel = new JLabel("Memoria: ");
		memoryLabel.setFont(ubuntu);
		memoryLabel.setBounds(10, build.getY() + build.getHeight() + 10, 100, 20);
		memoryLabel.setForeground(Color.WHITE);
		memoryLabel.setHorizontalAlignment(SwingConstants.LEFT);

		memory = new JComboBox();
		memory.setBounds(memoryLabel.getX() + memoryLabel.getWidth() + 10, memoryLabel.getY(), 145, 20);
		memory.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		populateMemory(memory);

		JLabel onLaunchLabel = new JLabel("En el arranque: ");
		onLaunchLabel.setFont(ubuntu);
		onLaunchLabel.setBounds(10, memoryLabel.getY() + memoryLabel.getHeight() + 10, 100, 20);
		onLaunchLabel.setForeground(Color.WHITE);
		onLaunchLabel.setHorizontalAlignment(SwingConstants.LEFT);

		onLaunch = new JComboBox();
		onLaunch.setBounds(onLaunchLabel.getX() + onLaunchLabel.getWidth() + 10, onLaunchLabel.getY(), 145, 20);
		onLaunch.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		populateOnLaunch(onLaunch);

		installedDirectory = Settings.getDirectory();

		packLocation = new LiteTextBox(this, "");
		packLocation.setBounds(10, onLaunchLabel.getY() + onLaunchLabel.getHeight() + 10, FRAME_WIDTH - 20, 25);
		packLocation.setFont(ubuntu.deriveFont(10F));
		packLocation.setText(installedDirectory);
		packLocation.setEnabled(false);
		packLocation.setBackground(Color.BLACK);
		packLocation.setForeground(Color.WHITE);
		packLocation.setBorder(null);

		LiteButton changeFolder = new LiteButton("Cambiar carpeta");
		changeFolder.setBounds(FRAME_WIDTH / 2 + 5, packLocation.getY() + packLocation.getHeight() + 10, FRAME_WIDTH / 2 - 15, 25);
		changeFolder.setFont(ubuntu.deriveFont(14F));
		changeFolder.setActionCommand(CHANGEFOLDER_ACTION);
		changeFolder.addActionListener(this);
		changeFolder.setEnabled(!SpoutcraftLauncher.params.isPortable());
		changeFolder.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		logs = new LiteButton("Logs");
		logs.setFont(ubuntu.deriveFont(14F));
		logs.setBounds(10, packLocation.getY() + packLocation.getHeight() + 10, FRAME_WIDTH / 2 - 15, 25);
		logs.setForeground(Color.WHITE);
		logs.setActionCommand(LOGS_ACTION);
		logs.addActionListener(this);
		logs.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		LiteButton save = new LiteButton("Guardar");
		save.setFont(ubuntu.deriveFont(14F));
		save.setBounds(10, logs.getY() + logs.getHeight() + 10, FRAME_WIDTH - 20, 25);
		save.setActionCommand(SAVE_ACTION);
		save.addActionListener(this);
		save.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		consoleToggle = Settings.getShowConsole();
		console = new LiteButton(consoleToggle ? "Esconder consola" : "Mostrar consola");
		console.setFont(ubuntu.deriveFont(14F));
		console.setBounds(10, logs.getY() + logs.getHeight() + 10, FRAME_WIDTH / 2 - 15, 25);
		console.setForeground(Color.WHITE);
		console.setActionCommand(CONSOLE_ACTION);
		console.addActionListener(this);
		console.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		fileChooser = new JFileChooser(Utils.getLauncherDirectory());
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		Container contentPane = getContentPane();
		contentPane.add(build);
		contentPane.add(changeFolder);
		contentPane.add(packLocation);
		contentPane.add(logs);
		//contentPane.add(console);
		contentPane.add(optionsQuit);
		contentPane.add(title);
		contentPane.add(memory);
		contentPane.add(memoryLabel);
		contentPane.add(onLaunch);
		contentPane.add(onLaunchLabel);
		contentPane.add(save);
		contentPane.add(background);

		setLocationRelativeTo(this.getOwner());
	}

	@SuppressWarnings("restriction")
	private void populateMemory(JComboBox memory) {
		long maxMemory = 1024;
		String architecture = System.getProperty("sun.arch.data.model", "32");
		boolean bit64 = architecture.equals("64");

		try {
			OperatingSystemMXBean osInfo = ManagementFactory.getOperatingSystemMXBean();
			if (osInfo instanceof com.sun.management.OperatingSystemMXBean) {
				maxMemory = ((com.sun.management.OperatingSystemMXBean) osInfo).getTotalPhysicalMemorySize() / 1024 / 1024;
			}
		} catch (Throwable t) {
		}
		maxMemory = Math.max(512, maxMemory);

		if (maxMemory >= Memory.MAX_32_BIT_MEMORY && !bit64) {
			memory.setToolTipText("<html>Sets the amount of memory assigned to Minecraft<br/>" + "You have more than 1.5GB of memory available, but<br/>"
					+ "you must have 64bit java installed to use it.</html>");
		} else {
			memory.setToolTipText("<html>Sets the amount of memory assigned to Minecraft<br/>" + "More memory is not always better.<br/>"
					+ "More memory will also cause your CPU to work more.</html>");
		}

		if (!bit64) {
			maxMemory = Math.min(Memory.MAX_32_BIT_MEMORY, maxMemory);
		}
		System.out.println("Maximum usable memory detected: " + maxMemory + " mb");

		for (Memory mem : Memory.memoryOptions) {
			if (maxMemory >= mem.getMemoryMB()) {
				memory.addItem(mem.getDescription());
			}
		}

		int memoryOption = Settings.getMemory();
		try {
			Settings.setMemory(memoryOption);
			memory.setSelectedIndex(Memory.getMemoryIndexFromId(memoryOption));
		} catch (IllegalArgumentException e) {
			memory.removeAllItems();
			memory.addItem(String.valueOf(Memory.memoryOptions[0]));
			Settings.setMemory(1); // 512 == 1
			memory.setSelectedIndex(0); // 1st element
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		this.setLocation(e.getXOnScreen() - mouseX, e.getYOnScreen() - mouseY);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof JComponent) {
			action(e.getActionCommand(), (JComponent) e.getSource());
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	public void action(String action, JComponent c) {
		if (action.equals(QUIT_ACTION)) {
			dispose();
		} else if (action.equals(SAVE_ACTION)) {
			int mem = Memory.memoryOptions[memory.getSelectedIndex()].getSettingsId();
			Settings.setMemory(mem);
			Settings.setBuildStream(buildStream);
			Settings.setLaunchAction((LaunchAction)onLaunch.getSelectedItem());
			if (directoryChanged) {
				Settings.setMigrate(true);
				Settings.setMigrateDir(installedDirectory);
			}

			if (directoryChanged || streamChanged) {
				JOptionPane.showMessageDialog(c, "Un reinicio manual del launcher es requerido para que los cambios tomen efecto. Porfavor, reinicialo.", "Reinicio requerido", JOptionPane.INFORMATION_MESSAGE);
				dispose();
			}
			dispose();
		} else if (action.equals(LOGS_ACTION)) {
			File logDirectory = new File(Utils.getLauncherDirectory(), "logs");
			DesktopUtils.open(logDirectory);
		} else if (action.equals(CONSOLE_ACTION)) {
			consoleToggle = !consoleToggle;
			Settings.setShowConsole(consoleToggle);
			if (consoleToggle) {
				SpoutcraftLauncher.setupConsole();
			} else {
				SpoutcraftLauncher.destroyConsole();
			}
			console.setText(consoleToggle ? "Esconder consola" : "Mostrar consola");
		} else if (action.equals(CHANGEFOLDER_ACTION)) {
			int result = fileChooser.showOpenDialog(this);

			if (result == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				if (!ZipUtils.checkLaunchDirectory(file)) {
					JOptionPane.showMessageDialog(c, "Porfavor selecciona un directorio vacio.", "Ubicacion invalida", JOptionPane.WARNING_MESSAGE);
					return;
				}
				packLocation.setText(file.getPath());
				installedDirectory = file.getAbsolutePath();
				directoryChanged = true;
			}
		} else if (action.equals(BETA_ACTION)) {
			buildStream = Settings.BETA;
			build.setText(LAUNCHER_PREPEND + getLatestLauncherBuild(buildStream));
			streamChanged = true;
		} else if (action.equals(STABLE_ACTION)) {
			buildStream = Settings.STABLE;
			build.setText(LAUNCHER_PREPEND + getLatestLauncherBuild(buildStream));
			streamChanged = true;
		}

	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	private int getLatestLauncherBuild(String buildStream) {
		int build = 0;
		try {
			build = Integer.parseInt(SpoutcraftLauncher.getLauncherBuild());
		} catch (NumberFormatException ignore) {
		}

		try {
			build = LauncherInfo.getLatestBuild(buildStream);
			return build;
		} catch (RestfulAPIException e) {
			e.printStackTrace();
		}

		return build;
	}

	private void populateOnLaunch(JComboBox onLaunch) {
		onLaunch.addItem(LaunchAction.HIDE);
		onLaunch.addItem(LaunchAction.CLOSE);
		onLaunch.addItem(LaunchAction.NOTHING);
		LaunchAction selectedAction = Settings.getLaunchAction();
		if (selectedAction == null) {
			onLaunch.setSelectedItem(LaunchAction.HIDE);
			Settings.setLaunchAction(LaunchAction.HIDE);
		} else {
			onLaunch.setSelectedItem(Settings.getLaunchAction());
		}
	}

}
