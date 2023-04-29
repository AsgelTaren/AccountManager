package net.app.data;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import net.app.data.struct.Account;
import net.app.data.struct.Domain;

@SuppressWarnings("serial")
public class CustomRenderer extends DefaultTreeCellRenderer {

	// DataBasePanel
	private DataBasePanel panel;

	public CustomRenderer(DataBasePanel panel) {
		super();
		this.panel = panel;
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
			boolean leaf, int row, boolean hasFocus) {
		JLabel res = (JLabel) super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
		Object data = ((DefaultMutableTreeNode) value).getUserObject();
		if (data instanceof Domain d) {
			ImageIcon icon = panel.getIcons().get(d.getID());
			res.setIcon(icon == null ? DataBasePanel.menuIcons.get("domain") : icon);
		}
		if (data instanceof Account) {
			res.setIcon(DataBasePanel.menuIcons.get("user"));
		}
		if (data instanceof String) {
			res.setIcon(DataBasePanel.menuIcons.get("icon"));
		}
		return res;
	}

}
