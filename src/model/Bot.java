package model;

public class Bot implements Comparable<Object>{
    
    private float xPos,zPos;

    public Bot(){
	float xPos=0,zPos=0;
	String modelFileName="";
	
    }
    
    @Override
    public int compareTo(Object o) {
	Bot b=(Bot) o;
	return (int) (this.xPos-b.xPos);
    }
    
    
}
