package net.app.data.struct;

import com.google.gson.JsonObject;

public class Domain {

	private String id;
	private String[] metadata;

	public Domain(String id) {
		this.metadata = new String[DomainData.values().length];
		this.id = id;
	}

	public Domain(JsonObject obj) {
		id = obj.get("id").getAsString();
		metadata = new String[DomainData.values().length];
		for (DomainData data : DomainData.values()) {
			if(obj.has(data.name()))
			setData(data, obj.get(data.name()).getAsString());
		}
	}

	public void setData(DomainData data, String value) {
		this.metadata[data.ordinal()] = ((value == null) ? "" : value);
	}

	public String getData(DomainData data) {
		return (metadata[data.ordinal()] == null || metadata[data.ordinal()].equals("")) ? "<None>"
				: metadata[data.ordinal()];
	}

	public String getID() {
		return id;
	}

	@Override
	public String toString() {
		return "<html>" +getData(DomainData.NAME) + " <b><i>(" + id + ")</i></b></html>";
	}

	public JsonObject getAsJsonObject() {
		JsonObject obj = new JsonObject();
		obj.addProperty("id", id);
		for (DomainData data : DomainData.values()) {
			if (metadata[data.ordinal()] != null) {
				obj.addProperty(data.name(), metadata[data.ordinal()]);
			}
		}
		return obj;
	}

}