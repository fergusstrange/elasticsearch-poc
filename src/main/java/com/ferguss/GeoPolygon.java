package com.ferguss;

import java.util.List;

public class GeoPolygon implements GeoShape {

	private final String type = "polygon";
	private List<List<Double[]>> coordinates;

	public GeoPolygon(List<List<Double[]>> coordinates) {
		this.coordinates = coordinates;
	}

	public String getType() {
		return type;
	}

	public List<List<Double[]>> getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(List<List<Double[]>> coordinates) {
		this.coordinates = coordinates;
	}
}
