package detection;

public class AABB {
	public int xmin,ymin,xmax,ymax;
	public AABB(int upperLeftX,int upperLeftY,int lowerRightX,int lowerRightY){
		this.xmin = upperLeftX;
		this.ymin = upperLeftY;
		this.xmax = lowerRightX;
		this.ymax = lowerRightY;
	}
	public int getWidth() {
		return xmax - xmin;
	}
	public int getHeight() {
		return ymax - ymin;
	}
	public int getArea() {
		return getWidth() * getHeight();
	}
}
