package net.app;

import java.io.File;
import java.io.FileReader;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.EncryptionMethod;

public class FileHandler {

	private App app;

	// Data
	private ZipFile zipFile;
	private File accountsFile;
	private JsonObject obj;
	private ZipParameters params;

	public FileHandler(App app, ZipFile zipFile) {
		this.app = app;
		this.zipFile = zipFile;
		
		params = new ZipParameters();
		params.setEncryptFiles(true);
		params.setEncryptionMethod(EncryptionMethod.AES);
	}

	public void ensureValidity() {
		obj = new JsonObject();
		accountsFile = new File(app.getWorkingPath().toAbsolutePath().toString() + "/accounts.json");
		try {
			obj = JsonParser.parseReader(new FileReader(accountsFile)).getAsJsonObject();
			if(!obj.has("domains")) {
				obj.add("domains", new JsonArray());
			}
			if(!obj.has("accounts")) {
				obj.add("domains", new JsonArray());
			}
		} catch (Exception e) {
			obj = new JsonObject();
			obj.add("domains", new JsonArray());
			obj.add("accounts", new JsonArray());
		}
	}

	public void requestSaveAndQuit(JFrame frame) {
		int choice = JOptionPane.showConfirmDialog(frame,
				"<html>Do you want to save your work at<b>" + zipFile.getFile().getAbsolutePath() + "</b>?</html>",
				"Warning", JOptionPane.YES_NO_CANCEL_OPTION);
		if (choice == JOptionPane.YES_OPTION) {
			
			System.exit(0);
		} else if (choice == JOptionPane.NO_OPTION) {
			System.exit(0);
		}
	}
	
	public void save() {
		try {
			Utils.write(app.getDataBasePanel().getDataBase().getAsJsonObject().toString(), accountsFile);
			zipFile.addFile(accountsFile,params);
		} catch (ZipException e) {
			e.printStackTrace();
		}
	}

	public ZipFile getZipFile() {
		return zipFile;
	}

	public File getAccountsFile() {
		return accountsFile;
	}

	public JsonObject getJsonObject() {
		return obj;
	}

}
