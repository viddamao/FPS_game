package model;

public class Bot implements Comparable<Object> {

    public static int numBot = 0;
    private float xPos, zPos, facing=180;
    private int hp=100;

    public Bot() {
	float xPos = 0, zPos = 0, facing = 180;
	int hp=100;
	String modelFileName = "";

    }

    public Bot(float x, float z, float f) {
	float xPos = x;
	float zPos = z;
	float facing = f;
	int hp=100;
	String modelFileName = "";
    }

    @Override
    public int compareTo(Object o) {
	Bot b = (Bot) o;
	return (int) (this.getxPos() - b.getxPos());
    }

    public void turn(float playerX, float playerZ) {
	float deltaX = xPos - playerX;
	float deltaZ = zPos - playerZ;
	float angle = (float) Math.toDegrees(Math
		.acos(((deltaX) * 0 + (-deltaZ))
			/ (Math.sqrt(deltaX * deltaX + deltaZ * deltaZ))));
	facing = deltaX < 0 ? angle : 360 - angle;
    }

    public void move() {

    }

    public void shoot() {

    }

    private boolean collisionCheck(float x, float z) {
	float[][] collisionModel = { { -13.5f, 14f, -2.7f, 2f },// Zhuzi SW
		{ -13.5f, 18f, -11.5f, 14f },// Qiang SW
		{ -0.5f, 24f, 1.1f, 19.7f },// Qiang W
		{ 2.8f, 14f, 15.8f, 2f },// Zhuzi NW
		{ 13.7f, 18f, 15.8f, 14f },// Qiang NW

		{ -13.5f, -2.7f, -2.7f, -13.5f },// Zhuzi SE
		{ -13.5f, -13.5f, -11.5f, -17.6f },// Qiang SE
		{ 2.8f, -2.7f, 15.8f, -13.5f }, // Zhuzi NE
		{ 13.7f, -13.5f, 15.8f, -17.6f } // Qiang NE
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

    public int getHp() {
	return hp;
    }

    public void setHp(int hp) {
	this.hp = hp;
    }
}
