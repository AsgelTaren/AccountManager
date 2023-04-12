package net.app;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.EncryptionMethod;

@SuppressWarnings("serial")
public class OpenDialog extends JDialog {

	private FileHandler result;

	public OpenDialog(App app) {
		super((JFrame) null, "Test", true);

		setLayout(new GridBagLayout());
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setAutoRequestFocus(true);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.anchor = GridBagConstraints.CENTER;

		// Upper panel
		JPanel upper = new JPanel();
		upper.setLayout(new GridBagLayout());
		upper.setBorder(BorderFactory.createTitledBorder("File"));

		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.gridx = 0;
		gbc.gridy = 0;

		add(upper, gbc);

		// File
		JLabel label = new JLabel("File");
		gbc.gridwidth = 1;
		upper.add(label, gbc);

		JTextField field = new JTextField();
		field.setPreferredSize(new Dimension(300, 25));
		field.setEditable(false);
		field.addActionListener(e -> {
			System.out.println("Test ");
		});
		gbc.gridx = 1;
		upper.add(field, gbc);

		// Password
		label = new JLabel("Password");
		gbc.gridx = 0;
		gbc.gridy++;
		upper.add(label, gbc);

		JPasswordField password = new JPasswordField();
		password.setPreferredSize(new Dimension(300, 25));
		gbc.gridx = 1;
		upper.add(password, gbc);

		gbc.gridx = 0;
		gbc.gridy++;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;

		JButton changeFile = new JButton("Change file");
		changeFile.addActionListener(e -> {
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			chooser.setAcceptAllFileFilterUsed(false);
			chooser.addChoosableFileFilter(new FileNameExtensionFilter("ZIP Archive", "zip"));
			int choice = chooser.showOpenDialog(this);
			if (choice == JFileChooser.APPROVE_OPTION) {
				field.setText(chooser.getSelectedFile().getAbsolutePath());
				field.setBorder(UIManager.getLookAndFeel().getDefaults().getBorder("TextField.border"));
				field.revalidate();
			}
		});
		upper.add(changeFile, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		JButton open = new JButton("Open");
		open.addActionListener(e -> {
			File target = new File(field.getText());
			if (!target.exists() || !(target.getName().endsWith(".zip") || target.getName().endsWith(".rar"))) {
				field.setBorder(new LineBorder(Color.RED, 1));
				field.revalidate();
				return;
			}
			field.setBorder(new LineBorder(Color.GREEN, 1));
			field.revalidate();

			ZipParameters zipParams;
			zipParams = new ZipParameters();
			zipParams.setEncryptFiles(true);
			zipParams.setEncryptionMethod(EncryptionMethod.AES);

			ZipFile zipFile = new ZipFile(target);
			try {
				System.out.println(zipFile.isEncrypted());
				zipFile.setPassword(password.getPassword());
				zipFile.extractAll(app.getWorkingPath().toString());
				password.setBorder(new LineBorder(Color.GREEN, 1));
				password.revalidate();
				closeDialog();
				result = new FileHandler(app, zipFile);
			} catch (ZipException e1) {
				e1.printStackTrace();
				password.setBorder(new LineBorder(Color.RED, 1));
				password.revalidate();
			}
		});
		add(open, gbc);

		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(e -> System.exit(0));

		gbc.gridx = 1;
		add(cancel, gbc);

		pack();

		setLocationRelativeTo(null);
	}

	public void showDialog() {
		setVisible(true);
		setResizable(false);
	}

	public void closeDialog() {
		setVisible(false);
		dispose();
	}

	public FileHandler getResult() {
		return result;
	}
}
