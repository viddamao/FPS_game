package finalproject_team28;

import java.awt.Point;
import java.awt.event.KeyEvent;
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

    private final String DEFAULT_MAP_FILE = "img/iceworld.jpg";
    private final int MAP_ID = 1;
    private final float HEIGHT_RATIO = 0.25f;

    private int myRenderMode;
    private int myStepSize;
    private ArrayList<List<Face>> myFaces;
    private Pixmap myHeightMap;

    private MapRenderer myMapRenderer;

    private float myScale;
    private boolean INIT_DONE = false;
    private boolean RESET_VIEW = false;
    private boolean isCompiled = false;
    private boolean MOVE_FORWARD = false;
    private boolean MOVE_BACKWARD = false;
    private boolean MOVE_RIGHT = false;
    private boolean MOVE_LEFT = false;
    private boolean OBJECT_ASCEND = false;
    private boolean OBJECT_DESCEND = false;

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
	myScale = 0.05f;
	myStepSize = 1;
	isCompiled = false;

	myRenderMode = GL2GL3.GL_QUADS;
	myMapRenderer = MapRenderer.getMapRenderer();
	myMapRenderer.init(myHeightMap, myStepSize);
	myMapRenderer.build();

	// make all normals unit length
	gl.glEnable(GLLightingFunc.GL_NORMALIZE);
    }

    @Override
    public void display(GL2 gl, GLU glu, GLUT glut) {
	if (!isCompiled) {
	    gl.glDeleteLists(MAP_ID, 1);
	    gl.glNewList(MAP_ID, GL2.GL_COMPILE);
	    drawMap(gl, glu, glut);
	    gl.glEndList();
	    isCompiled = true;
	}

	gl.glScalef(myScale, myScale * HEIGHT_RATIO, myScale);
	gl.glCallList(MAP_ID);

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
	glu.gluLookAt(0, 12, -27, // from position
		0, 7, 10, // to position
		0, 0, 1); // up direction

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
	switch (keyCode) {
	case KeyEvent.VK_PERIOD:
	    myScale += 0.01f;
	    break;
	case KeyEvent.VK_COMMA:
	    myScale -= 0.01f;
	    break;
	case KeyEvent.VK_W:
	    MOVE_FORWARD = true;
	    break;
	case KeyEvent.VK_S:
	    MOVE_BACKWARD = true;
	    break;
	case KeyEvent.VK_D:
	    MOVE_RIGHT = true;
	    break;
	case KeyEvent.VK_A:
	    MOVE_LEFT = true;
	    break;
	case KeyEvent.VK_U:
	    OBJECT_ASCEND = true;
	    break;
	case KeyEvent.VK_I:
	    OBJECT_DESCEND = true;
	    break;
	}
    }

    /**
     * Animate the scene by changing its state slightly.
     */
    @Override
    public void animate(GL2 gl, GLU glu, GLUT glut) {
	if (!INIT_DONE) {
	    gl.glTranslatef(0, 7.0f, 0);
	    gl.glPushMatrix();
	    INIT_DONE = true;
	}
	if (RESET_VIEW) {
	    gl.glPopMatrix();
	    RESET_VIEW = false;
	    INIT_DONE = false;
	}
	if (OBJECT_ASCEND) {
	    gl.glTranslatef(0, -0.1f, 0);
	    OBJECT_ASCEND = false;
	}
	if (OBJECT_DESCEND) {
	    gl.glTranslatef(0, 0.1f, 0);
	    OBJECT_DESCEND = false;
	}
	if (MOVE_RIGHT) {
	    gl.glTranslatef(-0.1f, 0, 0);
	    MOVE_RIGHT = false;
	}
	if (MOVE_LEFT) {
	    gl.glTranslatef(0.1f, 0, 0);
	    MOVE_LEFT = false;
	}
	if (MOVE_FORWARD) {
	    gl.glTranslatef(0, 0, 0.1f);
	    MOVE_FORWARD = false;
	}
	if (MOVE_BACKWARD) {
	    gl.glTranslatef(0, 0, -0.1f);
	    MOVE_BACKWARD = false;
	}

    }

    
    /**
     * Respond to the mouse being moved in the canvas.
     *
     * @param pt current position of the mouse
     */

    @Override
    public void mouseMoved (Point pt) {
        // by default, do nothing
    }
    
    /**
     * Respond to the press and release of the mouse.
     *
     * @param pt current position of the mouse
     * @param button mouse button that was clicked
     */
    @Override
    public void mouseClicked (Point pt, int button) {
        // by default, do nothing
    }
    
    
    /**
     * Called when the mouse is pressed within the canvas and it hits something.
     */

    @Override
    public void selectObject (GL2 gl, GLU glu, GLUT glut, int numSelected, int[] selectInfo) {
        // by default, do nothing
    }
    public static void main(String[] args) {
	new JOGLFrame(new GameView(args));
    }

}
