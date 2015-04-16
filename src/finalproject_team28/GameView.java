package finalproject_team28;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2GL3;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;

import com.jogamp.opengl.util.gl2.GLUT;


import framework.JOGLFrame;
import framework.Pixmap;
import framework.Scene;

public class GameView extends Scene {

    private final String DEFAULT_MAP_FILE = "img/sierra_elev.jpg";
    private final int MAP_ID = 1;
    
    private int myRenderMode;
    private int myStepSize;
    private ArrayList<List<Face>> myFaces;
    private Pixmap myHeightMap;

    private MapRenderer myMapRenderer;

    public GameView(String[] args) {
	super("Shooting Game");

	String name = (args.length > 1) ? args[0] : DEFAULT_MAP_FILE;
	try {
	    myHeightMap = new Pixmap((args.length > 1) ? args[0]
		    : DEFAULT_MAP_FILE);
	} catch (IOException e) {
	    System.err.println("Unable to load texture image: " + name);
	    System.exit(1);
	}

    }

    public void init(GL2 gl, GLU glu, GLUT glut) {
	myFaces = new ArrayList<List<Face>>();
	myRenderMode = GL2GL3.GL_QUADS;
	gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

	myRenderMode = GL2GL3.GL_QUADS;

	myMapRenderer = MapRenderer.getMapRenderer();
	myMapRenderer.init(myHeightMap, myStepSize);
	myMapRenderer.build();

	/* initialize viewing values */
	gl.glMatrixMode(gl.GL_PROJECTION);
	gl.glLoadIdentity();
	gl.glOrtho(0.0, 1.0, 0.0, 1.0, -1.0, 1.0);
    }

    @Override
    public void display(GL2 gl, GLU glu, GLUT glut) {
	gl.glDeleteLists(MAP_ID, 1);
	gl.glNewList(MAP_ID, GL2.GL_COMPILE);

	drawMap(gl, glu, glut);
	gl.glEndList();
	
    }

    private void drawMap(GL2 gl, GLU glu, GLUT glut) {
	gl.glBegin(myRenderMode);
	{
	    for (List<Face> faces : myMapRenderer.getFaces()) {
		for (Face f : faces) {
		    f.drawFace(gl, glu, glut);
		}
	    }
	}
	gl.glEnd();
	
    }

    @Override
    public void setCamera(GL2 gl, GLU glu, GLUT glut) {
	glu.gluLookAt(0, 7, -33, // from position
		0, 5, 20, // to position
		0, 0, 0); // up direction

    }
    
    /**
     * Establish lights in the scene.
     */
    @Override
    public void setLighting(GL2 gl, GLU glu, GLUT glut) {
	float[] light0pos = { 0, 150, 0, 1 };
	float[] light0dir = { 0, -1, 0, 0 };
	gl.glEnable(GLLightingFunc.GL_LIGHTING);
	gl.glEnable(GLLightingFunc.GL_LIGHT0);
	gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_POSITION,
		light0pos, 0);
	gl.glLightfv(GLLightingFunc.GL_LIGHT0,
		GLLightingFunc.GL_SPOT_DIRECTION, light0dir, 0);
	gl.glLightf(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_SPOT_CUTOFF, 20);
    }

    /**
     * Called when any key is pressed within the canvas.
     */
    @Override
    public void keyPressed(int keyCode) {
	//TODO add key Events
    }
    
    public static void main(String[] args) {
	new JOGLFrame(new GameView(args));
    }

}
