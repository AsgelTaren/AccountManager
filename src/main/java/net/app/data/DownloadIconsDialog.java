package net.app.data;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.app.data.struct.Domain;
import net.app.data.struct.DomainData;

@SuppressWarnings("serial")
public class DownloadIconsDialog extends JDialog {

	private Set<Entry<String, Domain>> set;
	private DataBasePanel panel;
	private JProgressBar bar;
	private JTextArea area;

	public DownloadIconsDialog(DataBasePanel panel) {
		super(panel.getJFrame(), "Download icons", false);
		this.panel = panel;

		JPanel pane = new JPanel();
		setContentPane(pane);
		pane.setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(5, 5, 5, 5);

		set = panel.getDataBase().getDomains().entrySet();

		bar = new JProgressBar(0, set.size());
		bar.setPreferredSize(new Dimension(300, 25));
		pane.add(bar, gbc);

		area = new JTextArea();
		area.setPreferredSize(new Dimension(300, 300));
		area.setEditable(false);
		gbc.gridy++;
		pane.add(new JScrollPane(area), gbc);

		setVisible(true);
		pack();
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setLocationRelativeTo(null);
	}

	public void download() {
		int i = 0;
		for (Entry<String, Domain> entry : set) {
			if (!entry.getValue().getData(DomainData.URL).equals("<None>")) {
				area.append("> Found website " + entry.getValue().getData(DomainData.URL) + " for domain "
						+ entry.getValue().getID() + "\n");
				panel.downloadIcon(entry.getValue());
				area.append("-> Done !\n");
				bar.setValue(i++);
			}
		}
		setVisible(false);
		dispose();
	}

	public void showDialog() {
		setVisible(true);
		setResizable(false);
	}

}
