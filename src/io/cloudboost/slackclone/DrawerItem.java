package io.cloudboost.slackclone;

public class DrawerItem {
	private String name;
	private boolean header;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public DrawerItem(String name, boolean header) {
		super();
		this.name = name;
		this.header = header;
	}

	public boolean isHeader() {
		return header;
	}

	public void setHeader(boolean header) {
		this.header = header;
	}
}
