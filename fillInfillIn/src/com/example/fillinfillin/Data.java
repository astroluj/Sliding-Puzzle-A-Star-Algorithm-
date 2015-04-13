package com.example.fillinfillin;

public class Data {

	private int row, col, face;
	private Data data;

	public Data(int row, int col) {
		this.row = row;
		this.col = col;
	}

	public Data() {
		this.row = 0;
		this.col = 0;
		this.face =0 ;
	}
	
	public Data (int face, int row, int col)
	{
		this.face =face ;
		this.row =row ;
		this.col =col ;
	}

	public Data (Data data)
	{
		this.face =data.getFace () ;
		this.row =data.getRow () ;
		this.col =data.getCol() ;
	}
	
	public void setData(int row, int col) {
		if (row >= 0)
			this.row = row;
		if (col >= 0)
			this.col = col;
	}
	
	public void setData (int face, int row, int col)
	{
		if (face >= 0)
			this.face =face ;
		if (row >= 0)
			this.row =row ;
		if (col >= 0)
			this.col =col ;
	}

	public void setData (Data data) 
	{
		if (data.getFace() >= 0)
			this.face =data.getFace () ;
		if (data.getRow () >= 0)
			this.row =data.getRow () ;
		if (data.getCol () >= 0)
			this.col =data.getCol() ;
	}
	
	public void setFace (int face)
	{
		this.face =face ;
	}
	
	public void setRow (int row)
	{
		this.row =row ;
	}
	
	public void setCol (int col)
	{
		this.col =col ;
	}
	
	public Data getData2D() {
		data.row = this.row ;
		data.col = this.col ;
		data.face =this.face ;
		return data;
	}
	
	public Data clone ()
	{
		Data data = new Data();
		data.row = this.row;
		data.col = this.col;
		data.face =this.face ;
		return data;
	}
	
	public int getRow ()
	{
		return this.row ;
	}
	
	public int getCol ()
	{
		return this.col ;
	}
	
	public int getFace ()
	{
		return this.face ;
	}
}
