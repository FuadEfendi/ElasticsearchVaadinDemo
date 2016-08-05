package com.rubylife.examples.autosuggest;

public class City {
	private String url;
	private String description;
	private String title;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTitle() {
		return title;
	}

    /**
     * @param title
     */
    protected void setTitle(String title) {
		this.title = title;
	}




}
