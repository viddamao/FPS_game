package model;

public class Weapon {
    

    private float xPos, zPos, facing;
    private int model=0;
    public Weapon() {
	float xPos = 0, zPos = 0, facing = 180;
	int model=0;
	String modelFileName = "";

    }
    public float getxPos() {
   	return xPos;
       }

       public void setxPos(float xPos) {
   	this.xPos = xPos;
       }

       public float getzPos() {
   	return zPos;
       }

       public void setzPos(float zPos) {
   	this.zPos = zPos;
       }

       public float getFacing() {
   	return facing;
       }

       public void setFacing(float facing) {
   	this.facing = facing;
       }

    public int getModel() {
	return model;
    }

    public void setModel(int model) {
	this.model = model;
    }
}
