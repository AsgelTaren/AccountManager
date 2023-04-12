package net.app.data;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JPopupMenu.Separator;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import net.app.Utils;
import net.app.data.struct.Account;
import net.app.data.struct.AccountData;
import net.app.data.struct.Domain;
import net.app.data.struct.DomainData;
import net.sf.image4j.codec.ico.ICODecoder;

@SuppressWarnings("serial")
public class DataBasePanel extends JPanel {

	public static final HashMap<String, ImageIcon> menuIcons = new HashMap<String, ImageIcon>();

	public static final void loadIcons() {
		loadSingleIcon("edit");
		loadSingleIcon("visit");
		loadSingleIcon("reload");
		loadSingleIcon("download");
		loadSingleIcon("remove");
		loadSingleIcon("add");
		loadSingleIcon("settings");
		loadSingleIcon("save");
		loadSingleIcon("domain");
		loadSingleIcon("upload");
		loadSingleIcon("user");
	}

	private static final void loadSingleIcon(String id) {
		try {
			InputStream stream = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream("res/menu/" + id + ".png");
			if (stream == null) {
				System.out.println("Unable to load icon for menu: " + id);
				return;
			}
			menuIcons.put(id, new ImageIcon(Utils.resize(Utils.loadIMG(stream), 16, 16)));
		} catch (Exception e) {
			System.out.println("Unable to load icon for menu: " + id);
		}
	}

	// DataBase
	private DataBase database;

	// JTree
	private JTree tree;
	private CustomRenderer renderer;

	// Search Bar
	private JTextField search;

	// Root
	private DefaultMutableTreeNode root;

	// JFrame
	private JFrame frame;

	// Icons
	private HashMap<String, ImageIcon> icons;

	public DataBasePanel(DataBase database, JFrame frame) {
		this.database = database;
		this.frame = frame;

		icons = new HashMap<String, ImageIcon>();
		reloadIcons();

		buildTree();
		renderer = new CustomRenderer(this);
		tree.setCellRenderer(renderer);
		setLayout(new BorderLayout());
		add(new JScrollPane(tree), BorderLayout.CENTER);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		tree.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e)) {
					if (e.getClickCount() == 2 && !e.isConsumed()) {
						openDialog(false);
					}
				}
				if (SwingUtilities.isRightMouseButton(e)) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getSelectionPath()
							.getLastPathComponent();
					Object o = node.getUserObject();
					if (o instanceof Domain d) {
						forDomain(node, d).show(e.getComponent(), e.getX(), e.getY());
					}
					if (o instanceof Account a) {
						forAccount(node, a).show(e.getComponent(), e.getX(), e.getY());
					}
					if (o instanceof String) {
						forGlobal().show(e.getComponent(), e.getX(), e.getY());
					}
				}
			}
		});

		tree.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {

			}

			@Override
			public void keyPressed(KeyEvent e) {
				openDialog(false);
			}

			@Override
			public void keyReleased(KeyEvent e) {

			}

		}

		);

		search = new JTextField();
		search.setToolTipText("Search");
		add(search, BorderLayout.NORTH);
		search.addActionListener(e -> {
			tree.clearSelection();
			Pattern p = Pattern.compile(search.getText(), Pattern.CASE_INSENSITIVE);
			for (int i = 0; i < root.getChildCount(); i++) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) root.getChildAt(i);
				Domain d = (Domain) node.getUserObject();
				if (p.matcher(node.getUserObject().toString()).find()
						|| search.getText().contains(d.getData(DomainData.URL))) {
					tree.setSelectionPath(new TreePath(new Object[] { root, node }));
					return;
				}
				for (int j = 0; j < node.getChildCount(); j++) {
					DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(j);
					if (p.matcher(child.getUserObject().toString()).find()) {
						tree.setSelectionPath(new TreePath(new Object[] { root, node, child }));
						return;
					}
				}
			}
		});

	}

	private void openDialog(boolean edit) {
		if (tree.getSelectionPath() != null) {
			Object o = ((DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent()).getUserObject();
			if (o instanceof Account a) {
				AccountPanel p = new AccountPanel(frame, a, this, edit);
				p.showDialog();
			} else if (o instanceof Domain d) {
				DomainPanel p = new DomainPanel(frame, d, this, edit);
				p.showDialog();

			}
		}
	}

	public void reloadIcons() {
		for (Entry<String, Domain> entry : database.getDomains().entrySet()) {
			reloadIcon(entry.getValue());
		}
	}

	public void reloadIcon(Domain d) {
		String loc = System.getenv("APPDATA") + "\\AccountManager\\icons";
		File f = new File(loc + "\\icon_" + d.getID() + ".png");
		if (f.exists()) {
			icons.put(d.getID(), new ImageIcon(Utils.loadIMG(f)));
		} else {
			icons.remove(d.getID());
		}
	}

	public void downloadIcons() {
		for (Entry<String, Domain> entry : database.getDomains().entrySet()) {
			if (!entry.getValue().getData(DomainData.URL).equals("<None>")) {
				downloadIcon(entry.getValue());
			}
		}
	}

	public void downloadIcon(Domain d) {
		String loc = System.getenv("APPDATA") + "\\AccountManager\\icons";
		File f = new File(loc + "\\icon_" + d.getID() + ".png");
		if (f.exists()) {
			return;
		}

		try {
			String s = d.getData(DomainData.URL) + "/favicon.ico";
			if (!s.startsWith("http")) {
				s = "https://" + s;
			}
			URL url = new URL(s);

			List<BufferedImage> res = ICODecoder.read(url.openStream());
			ImageIO.write(Utils.resize(res.get(0), 16, 16), "PNG", f);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public HashMap<String, ImageIcon> getIcons() {
		return icons;
	}

	private void buildTree() {
		root = new DefaultMutableTreeNode("AccountManager v1.0");

		HashMap<String, DefaultMutableTreeNode> map = new HashMap<String, DefaultMutableTreeNode>();
		for (Entry<String, Domain> entry : database.getDomains().entrySet()) {
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(entry.getValue());
			map.put(entry.getKey(), node);
			root.add(node);
		}

		for (Account acc : database.getAccounts()) {
			if (!map.containsKey(acc.getDomain())) {
				Domain d = database.getDomains().get(acc.getDomain());
				if (d == null) {
					d = new Domain(acc.getDomain());
					database.getDomains().put(acc.getDomain(), d);
				}
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(d);
				root.add(node);
				map.put(d.getID(), node);
			}

			map.get(acc.getDomain()).add(new DefaultMutableTreeNode(acc));
		}

		tree = new JTree(root);
	}

	public JPopupMenu forDomain(DefaultMutableTreeNode node, Domain d) {
		JPopupMenu menu = new JPopupMenu();

		JMenuItem edit = new JMenuItem("Edit");
		edit.addActionListener(e -> {
			openDialog(true);
		});
		edit.setIcon(menuIcons.get("edit"));
		menu.add(edit);

		JMenuItem addAccount = new JMenuItem("Add Account");
		addAccount.addActionListener(e -> {
			String s = JOptionPane.showInputDialog(frame, "Select a name");
			if (s != null) {
				Account res = new Account(d.getID());
				res.setData(AccountData.NAME, s);
				database.addAccount(res);
				DefaultMutableTreeNode child = new DefaultMutableTreeNode(res);
				node.add(child);
				reloadTree();
			}

		});
		addAccount.setIcon(menuIcons.get("add"));
		menu.add(addAccount);

		JMenuItem removeDom = new JMenuItem("Remove Domain");
		removeDom.addActionListener(e -> {
			int choice = JOptionPane.showConfirmDialog(frame,
					"<html>Are you sure you want to remove <b>" + d.toString() + "</b></html>?", "Warning",
					JOptionPane.YES_NO_OPTION);
			if (choice == JOptionPane.YES_OPTION) {
				for (int i = 0; i < node.getChildCount(); i++) {
					DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
					database.getAccounts().remove(child.getUserObject());

				}
				database.getDomains().remove(d.getID());
				root.remove(node);
				reloadTree();
			}
		});
		removeDom.setIcon(menuIcons.get("remove"));
		menu.add(removeDom);

		menu.add(new Separator());

		JMenuItem openWebsite = new JMenuItem("Open Website");
		openWebsite.setEnabled(Desktop.isDesktopSupported() && !d.getData(DomainData.URL).equals("<None>"));
		openWebsite.addActionListener(e -> {
			try {
				String s = d.getData(DomainData.URL);
				if (!s.startsWith("http")) {
					s = "https://" + s;
				}
				Desktop.getDesktop().browse(new URI(s));
			} catch (IOException | URISyntaxException e1) {
				e1.printStackTrace();
			}
		});
		openWebsite.setIcon(menuIcons.get("visit"));
		menu.add(openWebsite);

		JMenuItem openLogin = new JMenuItem("Open Login");
		openLogin.setEnabled(Desktop.isDesktopSupported() && !d.getData(DomainData.LOGIN_URL).equals("<None>"));
		openLogin.addActionListener(e -> {
			try {
				String s = d.getData(DomainData.LOGIN_URL);
				if (!s.startsWith("http")) {
					s = "https://" + s;
				}
				Desktop.getDesktop().browse(new URI(s));
			} catch (IOException | URISyntaxException e1) {
				e1.printStackTrace();
			}
		});
		openLogin.setIcon(menuIcons.get("visit"));
		menu.add(openLogin);

		JMenu iconMenu = new JMenu("Icon");

		JMenuItem reloadIcon = new JMenuItem("Reload Icon");
		reloadIcon.addActionListener(e -> {
			reloadIcon(d);
		});
		reloadIcon.setIcon(menuIcons.get("reload"));
		iconMenu.add(reloadIcon);

		JMenuItem downloadIcon = new JMenuItem("Download Icon");
		downloadIcon.addActionListener(e -> {
			downloadIcon(d);
			reloadIcon(d);
		});
		downloadIcon.setEnabled(Desktop.isDesktopSupported() && !d.getData(DomainData.URL).equals("<None>"));
		downloadIcon.setIcon(menuIcons.get("download"));
		iconMenu.add(downloadIcon);

		JMenuItem uploadIcon = new JMenuItem("Upload Icon");
		uploadIcon.addActionListener(e -> {
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int choice = chooser.showDialog(frame, "Select Icon");
			if (choice == JFileChooser.APPROVE_OPTION) {
				BufferedImage img = Utils.loadIMG(chooser.getSelectedFile());
				String loc = System.getenv("APPDATA") + "\\AccountManager\\icons";
				File f = new File(loc + "\\icon_" + d.getID() + ".png");
				try {
					ImageIO.write(Utils.resize(img, 16, 16), "PNG", f);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				reloadIcon(d);
			}
		});
		uploadIcon.setIcon(menuIcons.get("upload"));
		iconMenu.add(uploadIcon);

		JMenuItem clearIcon = new JMenuItem("Clear Icon");
		clearIcon.addActionListener(e -> {
			if (icons.containsKey(d.getID())) {
				String loc = System.getenv("APPDATA") + "\\AccountManager\\icons";
				File f = new File(loc + "\\icon_" + d.getID() + ".png");
				f.delete();
				reloadIcon(d);
			}
		});
		clearIcon.setIcon(menuIcons.get("remove"));
		iconMenu.add(clearIcon);

		menu.add(new Separator());
		menu.add(iconMenu);

		return menu;

	}

	public JPopupMenu forAccount(DefaultMutableTreeNode node, Account a) {
		JPopupMenu menu = new JPopupMenu();

		JMenuItem edit = new JMenuItem("Edit");
		edit.addActionListener(e -> {
			openDialog(true);
		});
		edit.setIcon(menuIcons.get("edit"));
		menu.add(edit);

		JMenuItem remove = new JMenuItem("Remove");
		remove.addActionListener(e -> {
			int choice = JOptionPane.showConfirmDialog(frame,
					"<html>Are you sure you want to remove <b>" + a.toString() + "</b>?</html>", "Warning",
					JOptionPane.YES_NO_OPTION);
			if (choice == JOptionPane.YES_OPTION) {
				database.getAccounts().remove(a);
				DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
				parent.remove(node);
				((DefaultTreeModel) tree.getModel()).reload(root);
			}
		});
		remove.setIcon(menuIcons.get("remove"));
		menu.add(remove);

		return menu;
	}

	public JPopupMenu forGlobal() {
		JPopupMenu menu = new JPopupMenu();

		JMenuItem create = new JMenuItem("Create Domain");
		create.addActionListener(e -> {
			String choice = JOptionPane.showInputDialog(frame, "Select a name", "Prompt", JOptionPane.YES_NO_OPTION);
			if (choice != null) {
				if (!database.getDomains().containsKey(choice)) {
					Domain d = new Domain(choice);
					database.getDomains().put(choice, d);
					DefaultMutableTreeNode node = new DefaultMutableTreeNode(d);
					root.add(node);
					reloadIcon(d);
					reloadTree();
				}
			}
		});
		menu.add(create);

		return menu;
	}

	public void reloadTree() {
		Enumeration<TreePath> list = tree.getExpandedDescendants(new TreePath(new Object[] { root }));
		((DefaultTreeModel) tree.getModel()).reload(root);
		((DefaultTreeModel) tree.getModel()).nodeStructureChanged(root);

		if (list != null) {
			Iterator<TreePath> it = list.asIterator();
			while (it.hasNext()) {
				tree.expandPath(it.next());
			}
		}
	}

	public void reloadTree(DefaultMutableTreeNode node) {
		Enumeration<TreePath> list = tree.getExpandedDescendants(new TreePath(new Object[] { root }));
		((DefaultTreeModel) tree.getModel()).nodeStructureChanged(node);

		Iterator<TreePath> it = list.asIterator();
		while (it.hasNext()) {
			tree.expandPath(it.next());
		}
	}

	public DataBase getDataBase() {
		return database;
	}

}
