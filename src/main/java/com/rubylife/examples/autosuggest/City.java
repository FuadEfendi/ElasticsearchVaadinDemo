package com.rubylife.examples.autosuggest;

import java.io.Serializable;

public class City  implements Serializable {

	private static final long serialVersionUID = 1L;
	private String name;

	private int profileCount;

	public int getProfileCount() {
		return profileCount;
	}

	public void setProfileCount(int profileCount) {
		this.profileCount = profileCount;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
