package net.app;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

import net.app.data.struct.AccountData;

@SuppressWarnings("serial")
public class AccountViewDialog extends JDialog {

	public AccountViewDialog(JFrame frame, App app) {
		super(frame, "Change Viewed Tag", true);

		setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		JLabel label = new JLabel("Selected Tag");
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(5, 5, 5, 5);
		add(label, gbc);

		JComboBox<AccountData> box = new JComboBox<AccountData>(AccountData.values());
		box.setSelectedItem(App.accView);
		gbc.gridx = 1;
		add(box, gbc);

		JButton apply = new JButton("Ok");
		apply.addActionListener(e -> {
			App.accView = (AccountData) box.getSelectedItem();
			app.repaint();
			closeDialog();
		});
		gbc.gridx = 0;
		gbc.gridy = 1;
		add(apply, gbc);

		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(e -> {
			closeDialog();
		});
		gbc.gridx = 1;
		add(cancel, gbc);

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
