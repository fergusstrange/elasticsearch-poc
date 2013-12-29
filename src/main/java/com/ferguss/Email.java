package com.ferguss;

import java.util.Date;

public class Email {

	private String emailAddress;
	private Date emailDate;
	private GeoShape geoshape;

	public Date getEmailDate() {
		return emailDate;
	}

	public void setEmailDate(Date emailDate) {
		this.emailDate = emailDate;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public GeoShape getGeoshape() {
		return geoshape;
	}

	public void setGeoshape(GeoShape geoShape) {
		this.geoshape = geoShape;
	}
}
