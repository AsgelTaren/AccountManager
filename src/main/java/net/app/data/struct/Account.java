package net.app.data.struct;

import com.google.gson.JsonObject;

import net.app.App;

public class Account {

	private String domain;

	private String[] metadata;

	public Account(String domain) {
		this.domain = domain;
		this.metadata = new String[AccountData.values().length];
	}

	public Account(JsonObject obj) {
		domain = obj.get("domain").getAsString();
		metadata = new String[AccountData.values().length];
		for (AccountData data : AccountData.values()) {
			if(obj.has(data.name()))
			setData(data, obj.get(data.name()).getAsString());
		}
	}

	public String getData(AccountData data) {
		return metadata[data.ordinal()] == null || metadata[data.ordinal()].equals("") ? "<None>"
				: metadata[data.ordinal()];
	}

	public void setData(AccountData data, String value) {
		metadata[data.ordinal()] = (value == null) ? "" : value;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	@Override
	public String toString() {
		return getData(App.accView);
	}

	public JsonObject getAsJsonObject() {
		JsonObject obj = new JsonObject();
		obj.addProperty("domain", domain);
		for (AccountData data : AccountData.values()) {
			if (metadata[data.ordinal()] != null) {
				obj.addProperty(data.name(), metadata[data.ordinal()]);
			}
		}
		return obj;
	}

}