package util;

public class GridCell {
	
	private Coordinate coordinate;

	private boolean hasObstacle;
	private boolean isVisible;
	private boolean accident;

	public GridCell(Coordinate coordinate) {
		this.coordinate = coordinate;
	}
	
	public GridCell(int gridX, int gridY) {
		this.coordinate = new Coordinate(gridX, gridY);
	}
	
	public GridCell(Coordinate coordinate, boolean hasObstacle) {
		this.coordinate = coordinate;
		
		this.hasObstacle = hasObstacle;
	}

	public GridCell(int gridX, int gridY, boolean hasObstacle) {
		this.coordinate = new Coordinate(gridX, gridY);
		this.hasObstacle = hasObstacle;
	}
	
	public GridCell(int gridX, int gridY, boolean hasObstacle, boolean isVisible) {
		this.coordinate = new Coordinate(gridX, gridY);
		
		this.hasObstacle = hasObstacle;
		this.isVisible = isVisible;
	}
	

	public int getgridX() {
		return this.coordinate.getX();
	}
	public int getGridY() {
		return this.coordinate.getY();
	}

	public void setHasObstacle(boolean hasObstacle) {
		this.hasObstacle = hasObstacle;
	}
	
	public boolean hasObstacle() {
		return this.hasObstacle;
	}

	public void setIsVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}
	
	public boolean hasAccident() {
		return this.accident;
	}
	
	public void setAccident(boolean accident) {
		this.accident = accident;
	}
	
	public boolean isVisible() {
		return this.isVisible;
	}

	public static String getIndex (int x, int y) {
		return String.format("%d,%d", x, y);
	}
	
}
