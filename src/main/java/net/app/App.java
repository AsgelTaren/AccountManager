package net.app;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import net.app.data.DataBase;
import net.app.data.DataBasePanel;
import net.app.data.struct.AccountData;
import net.app.data.struct.DomainData;

public class App {

	public static AccountData accView = AccountData.NAME;

	// JFrame
	private JFrame frame;

	// DataBase Panel
	private DataBasePanel panel;

	// MenuBar
	private JMenuBar menubar;

	// File
	private FileHandler handler;
	private Path workingPath;

	public App() {

	}

	public void init() throws Exception {
		String temp = System.getenv("APPDATA") + "//AccountManager//icons//";
		File f = new File(temp);
		f.mkdirs();

		workingPath = Files.createTempDirectory("accountManager");
		workingPath.toFile().deleteOnExit();
		System.out.println(workingPath);

		OpenDialog open = new OpenDialog(this);
		open.showDialog();
		handler = open.getResult();
		handler.ensureValidity();

		AccountData.loadIcons();
		DomainData.loadIcons();
		DataBasePanel.loadIcons();
		frame = new JFrame("AccountManager v0.0");
		frame.setIconImage(Utils.loadIMGInternal("res/menu/icon.png"));
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setTitle("AccountManager - " + handler.getZipFile().getFile().getAbsolutePath());
		
		frame.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				handler.requestSaveAndQuit(frame);
			}
		});

		DataBase data = new DataBase(handler.getJsonObject());

		panel = new DataBasePanel(data, frame);
		frame.setContentPane(panel);
		buildMenuBar();
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
	}

	private void buildMenuBar() {
		menubar = new JMenuBar();

		JMenu fileMenu = new JMenu("File");
		JMenuItem save = new JMenuItem("Save");
		save.addActionListener(e -> {
			handler.save();
		});
		save.setIcon(DataBasePanel.menuIcons.get("save"));
		fileMenu.add(save);

		menubar.add(fileMenu);

		JMenu view = new JMenu("View");
		JMenuItem setTag = new JMenuItem("Set Account Tag");
		setTag.addActionListener(e -> {
			AccountViewDialog dialog = new AccountViewDialog(frame, this);
			dialog.showDialog();
		});
		setTag.setIcon(DataBasePanel.menuIcons.get("settings"));
		view.add(setTag);

		JMenuItem reloadIcons = new JMenuItem("Reload Icons");
		reloadIcons.addActionListener(e -> {
			panel.reloadIcons();
		});
		reloadIcons.setIcon(DataBasePanel.menuIcons.get("reload"));
		view.add(reloadIcons);

		JMenuItem downloadIcons = new JMenuItem("Download Icons");
		downloadIcons.addActionListener(e -> {
			panel.downloadIcons();
			panel.reloadIcons();
			panel.reloadTree();
		});
		downloadIcons.setIcon(DataBasePanel.menuIcons.get("download"));
		view.add(downloadIcons);

		menubar.add(view);

		frame.setJMenuBar(menubar);
	}
	
	public DataBasePanel getDataBasePanel() {
		return panel;
	}

	public void repaint() {
		panel.reloadTree();
		panel.revalidate();
		panel.repaint();
	}

	public Path getWorkingPath() {
		return workingPath;
	}
}
