package model;

import java.util.ArrayList;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import com.jogamp.opengl.util.gl2.GLUT;

import framework.ImprovedNoise;

public class Face implements Comparable {

    private ArrayList<Face> myAdjacentFaces;
    private ArrayList<Vertex> myVertices;
    private float[] myFaceNormal;
    private int myCol;
    private int myRow;
    private Vertex myAnchor;

    public Face() {
	myAdjacentFaces = new ArrayList<Face>();
	myVertices = new ArrayList<Vertex>();
	myFaceNormal = new float[3];

    }

    public void addAdjacentFace(Face f) {
	if (f != null) {
	    myAdjacentFaces.add(f);
	}
    }

    public void addVertex(Vertex v) {
	myVertices.add(v);
    }

    public void setAnchor(Vertex v) {
	myAnchor = v;
    }

    public Vertex getAnchor() {
	return myAnchor;
    }

    public float[] calculateFaceNormal(GL2 gl, GLU glu, GLUT glut) {
	Vertex v0 = myVertices.get(0);
	Vertex v1 = myVertices.get(1);
	Vertex v2 = myVertices.get(2);
	Vertex v3 = myVertices.get(3);

	float nx = (v0.getY() - v1.getY()) * (v0.getZ() + v1.getZ())
		+ (v1.getY() - v2.getY()) * (v1.getZ() + v2.getZ())
		+ (v2.getY() - v3.getY()) * (v2.getZ() + v3.getZ())
		+ (v3.getY() - v0.getY()) * (v3.getZ() + v0.getZ());
	float ny = (v0.getZ() - v1.getZ()) * (v0.getX() + v1.getX())
		+ (v1.getZ() - v2.getZ()) * (v1.getX() + v2.getX())
		+ (v2.getZ() - v3.getZ()) * (v2.getX() + v3.getX())
		+ (v3.getZ() - v0.getZ()) * (v3.getX() + v0.getX());
	float nz = (v0.getX() - v1.getX()) * (v0.getY() + v1.getY())
		+ (v1.getX() - v2.getX()) * (v1.getY() + v2.getY())
		+ (v2.getX() - v3.getX()) * (v2.getY() + v3.getY())
		+ (v3.getX() - v0.getX()) * (v3.getY() + v0.getY());

	myFaceNormal[0] = nx;
	myFaceNormal[1] = ny;
	myFaceNormal[2] = nz;

	return myFaceNormal;
    }

    public void drawFace(GL2 gl, GLU glu, GLUT glut) {
	for (Vertex v : myVertices) {
	    float[] normal = v.getVertexNormal(gl, glu, glut);
	    float z = v.getY();
	    gl.glColor3f((z / 255) * .84f + 0.4f, (z / 255) * .86f + 0.4f,
		    (z / 255) * .86f + 0.4f);
	    // gl.glColor3f(1.0f, 1.0f, 1.0f);
	    gl.glNormal3f(normal[0], normal[1], normal[2]);
	    gl.glVertex3f(v.getX(), v.getY(), v.getZ());
	}
    }

    @Override
    public int compareTo(Object o) {
	Face f = (Face) o;
	if (getAnchor().getY() > f.getAnchor().getY())
	    return -1;
	else if (getAnchor().getY() < f.getAnchor().getY())
	    return 1;
	else {
	    if (getAnchor().getX() > f.getAnchor().getX())
		return -1;
	    else if (getAnchor().getX() < f.getAnchor().getX())
		return 1;
	    else
		return 0;
	}
    }

    public int getMyCol() {
	return myCol;
    }

    public void setMyCol(int myCol) {
	this.myCol = myCol;
    }

    public int getMyRow() {
	return myRow;
    }

    public void setMyRow(int myRow) {
	this.myRow = myRow;
    }

    public ArrayList<Vertex> getMyVertices() {
	return myVertices;
    }

}