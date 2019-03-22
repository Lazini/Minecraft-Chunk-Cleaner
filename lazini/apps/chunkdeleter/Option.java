package lazini.apps.chunkdeleter;

public class Option {

	private int radius, minY, maxY;
	private String blockIDs[];
  
	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public int getMinY() {
		return minY;
	}

	public void setMinY(int minY) {
		this.minY = minY;
	}

	public int getMaxY() {
		return maxY;
	}

	public void setMaxY(int maxY) {
		this.maxY = maxY;
	}

	public String[] getBlockIDs() {
		return blockIDs;
	}

	public void setBlockIDs(String[] blockIDs) {
		this.blockIDs = blockIDs;
	}

}
