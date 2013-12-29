package com.ferguss;

public class GeoPoint implements GeoShape {

	private final String type = "point";
	private Double[] coordinates;

	public GeoPoint(Double[] coordinates) {
		this.coordinates = coordinates;
	}

	public String getType() {
		return type;
	}

	public Double[] getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(Double[] coordinates) {
		this.coordinates = coordinates;
	}
}


