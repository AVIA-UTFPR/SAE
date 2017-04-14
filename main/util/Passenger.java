package util;

public class Passenger {
	
	private String name;

	private int pickUpX;
	private int pickUpY;
	
	private int dropOffX;
	private int dropOffY;

	public Passenger(int pickUpX, int pickUpY, int dropOffX, int dropOffY) {
		this.pickUpX = pickUpX;
		this.pickUpY = pickUpY;
		
		this.dropOffX = dropOffX;
		this.dropOffY = dropOffY;
	}
	
	public Passenger(String name, int pickUpX, int pickUpY, int dropOffX, int dropOffY) {
		this.name = name;
		
		this.pickUpX = pickUpX;
		this.pickUpY = pickUpY;
		
		this.dropOffX = dropOffX;
		this.dropOffY = dropOffY;
	}
	
	public String getName() {
		return name;
	}

	public int getPickUpX() {
		return pickUpX;
	}
	public int getPickUpY() {
		return pickUpY;
	}

	public int getDropOffX() {
		return dropOffX;
	}
	public int getDropOffY() {
		return dropOffY;
	}

}
