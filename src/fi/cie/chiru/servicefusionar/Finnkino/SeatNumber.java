package fi.cie.chiru.servicefusionar.Finnkino;

public class SeatNumber {
	
	private int row;
	private int col;

	public SeatNumber(int row, int col)
	{
	    this.row = row;
	    this.col = col;
	}
	
	public int getRow() 
	{
		return row;
	}
	public void setRow(int row) 
	{
		this.row = row;
	}
	public int getCol() 
	{
		return col;
	}
	public void setCol(int col) 
	{
		this.col = col;
	}

	public boolean areEquals(SeatNumber s)
	{
		if(s.getCol() == this.getCol() && s.getRow() == this.getRow())
			return true;
		else
			return false;
	}
}
