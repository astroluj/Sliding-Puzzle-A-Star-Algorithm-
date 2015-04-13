package com.ledqrcode.basic;

public class Coordinate {

	private float x, y;

	public Coordinate() {

		this.x = 0;
		this.y = 0;
	}

	public Coordinate(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public void setCoordinate(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public void setX(float x) {
		this.x = x;
	}

	public void setY(float y) {
		this.y = y;
	}

	public Coordinate getCoordinate() {
		return new Coordinate(this.x, this.y);
	}

	public float getX() {
		return this.x;
	}

	public float getY() {
		return this.y;
	}

}
