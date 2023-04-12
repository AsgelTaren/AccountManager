package net.app.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.app.data.struct.Account;
import net.app.data.struct.Domain;

public class DataBase {

	private ArrayList<Account> accounts;
	private HashMap<String, Domain> domains;

	public DataBase(JsonObject obj) {
		this.accounts = new ArrayList<Account>();
		this.domains = new HashMap<String, Domain>();
		JsonArray domarr = obj.get("domains").getAsJsonArray();
		for (int i = 0; i < domarr.size(); i++) {
			addDomain(new Domain(domarr.get(i).getAsJsonObject()));
		}

		JsonArray accarr = obj.get("accounts").getAsJsonArray();
		for (int i = 0; i < accarr.size(); i++) {
			addAccount(new Account(accarr.get(i).getAsJsonObject()));
		}
	}

	public void addAccount(Account a) {
		if (!accounts.contains(a)) {
			if (!domains.containsKey(a.getDomain())) {
				domains.put(a.getDomain(), new Domain(a.getDomain()));
			}
			accounts.add(a);
		}
	}

	public void addDomain(Domain d) {
		if (!domains.containsKey(d.getID())) {
			domains.put(d.getID(), d);
		}
	}

	public ArrayList<Account> getAccounts() {
		return accounts;
	}

	public HashMap<String, Domain> getDomains() {
		return domains;
	}

	public JsonObject getAsJsonObject() {
		JsonObject res = new JsonObject();
		JsonArray domarr = new JsonArray();
		for (Entry<String, Domain> entry : domains.entrySet()) {
			domarr.add(entry.getValue().getAsJsonObject());
		}

		JsonArray accarr = new JsonArray();
		for (Account acc : accounts) {
			accarr.add(acc.getAsJsonObject());
		}
		res.add("domains", domarr);
		res.add("accounts", accarr);
		return res;
	}

}