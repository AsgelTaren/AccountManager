package net.app.data;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.app.data.struct.Domain;
import net.app.data.struct.DomainData;

@SuppressWarnings("serial")
public class DomainPanel extends JDialog {
	// Fields
	private JTextField[] fields;
	private JLabel[] labels;

	// Action button
	private JButton save;

	public DomainPanel(JFrame parent, Domain dom, DataBasePanel panel, boolean edit) {
		super(parent,dom.toString(), true);
		setLayout(new GridBagLayout());

		JPanel sub = new JPanel();
		sub.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		fields = new JTextField[DomainData.values().length];
		labels = new JLabel[DomainData.values().length];

		gbc.insets = new Insets(5, 5, 5, 5);

		for (int i = 0; i < DomainData.values().length; i++) {
			fields[i] = new JTextField();
			labels[i] = new JLabel(DomainData.values()[i].getName());
			labels[i].setIcon(DomainData.values()[i].getIcon());
			labels[i].setIconTextGap(15);

			fields[i].setPreferredSize(new Dimension(200, 25));
			String data = dom.getData(DomainData.values()[i]);
			fields[i].setText(data.equals("<None>") ? "" : data);
			fields[i].setEditable(edit);

			gbc.gridx = 0;
			gbc.gridy = i;
			gbc.anchor = GridBagConstraints.WEST;
			sub.add(labels[i], gbc);

			gbc.gridx = 1;

			sub.add(fields[i], gbc);
		}

		sub.setBorder(BorderFactory.createTitledBorder("Domain Metadata"));

		gbc.gridx = 0;
		gbc.gridy = 0;

		add(sub, gbc);

		if (edit) {
			save = new JButton("Save");
			gbc.gridy = 1;
			gbc.anchor = GridBagConstraints.EAST;
			add(save, gbc);

			save.addActionListener(e -> {

				for (DomainData data : DomainData.values()) {
					dom.setData(data, fields[data.ordinal()].getText());
					closeDialog();
					panel.reloadIcon(dom);
					panel.reloadTree();
				}
			});
		}

		pack();
	}

	public void showDialog() {
		setVisible(true);
		setResizable(false);
	}

	public void closeDialog() {
		setVisible(false);
		dispose();
	}
}
