package model;

public class Bot implements Comparable<Object>{
    
    public static int numBot=0;
    private float xPos,zPos,facing;

    public Bot(){
	float xPos=0,zPos=0,facing=180;
	String modelFileName="";
	
    }
    
    public Bot(float x,float z,float f){
	float xPos=x;
	float zPos=z;
	float facing=f;
	String modelFileName="";
    }
    @Override
    public int compareTo(Object o) {
	Bot b=(Bot) o;
	return (int) (this.getxPos()-b.getxPos());
    }
    
    private void move(){
	
    }
    
    private void shoot(){
	
    }
    
    private boolean collisionCheck(float x, float z) {
	float[][] collisionModel = { { -13.5f, 14f, -2.7f, 2f },// Zhuzi SW
		{ -13.5f, 18f, -11.5f, 14f },// Qiang SW
		{ -0.5f, 24f, 1.1f, 19.7f },// Qiang W
		{ 2.8f, 14f, 15.8f, 2f },// Zhuzi NW
		{ 13.7f, 18f, 15.8f, 14f },// Qiang NW

		{ -13.5f, -2.7f, -2.7f, -13.5f },// Zhuzi SE
		{-13.5f,-13.5f,-11.5f,-17.6f},//Qiang SE
		{ 2.8f, -2.7f, 15.8f, -13.5f }, // Zhuzi NE
		{13.7f,-13.5f,15.8f,-17.6f}//Qiang NE
	};
	for (float[] i : collisionModel) {
	    if ((i[0] <= z) && (i[1] >= x) && (i[2] >= z) && (i[3] <= x)) {
		return true;
	    }
	}
	return false;
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
}
