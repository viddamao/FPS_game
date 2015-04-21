package view;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;

import model.Face;

import com.jogamp.opengl.swt.GLCanvas;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

import framework.JOGLFrame;
import framework.Pixmap;
import framework.Scene;

public class GameView extends Scene{

    private static JOGLFrame myFrame;
    private final String DEFAULT_MAP_FILE = "src/img/iceworld_0.bmp";
    String[] textureNames = new String[]{
	    "src/img/skybox_fr.rgb","src/img/skybox_lf.rgb","src/img/skybox_bk.rgb",
	    "src/img/skybox_rt.rgb","src/img/skybox_up.rgb","src/img/skybox_dn.rgb",
        };
    Texture[] skyboxTextures = new Texture[7];
    private final int MAP_ID = 1;
    private final float HEIGHT_RATIO = 0.25f;
    private final double SCREEN_WIDTH_CENTER = 350;
    private final double SCREEN_HEIGHT_CENTER = 350;
    private double MOVEMENT_INCRE = 0.1;
    private final double WALKING_SPEED = 0.1;
    private final double RUNNING_SPEED = 0.2;
    private final int MOUSE_CENTER_TOLERANCE = 50;

    private final static double LOOK_AT_DIST = 100;
    private static final int FLOOR_LEN = 48;
    private static final float ANGLE_INCRE = 0.5f;
    private static final float HEIGHT_INCRE = 0.25f;
    private static final int MAX_JUMP_HEIGHT = 10;

    private int myRenderMode;
    private int myStepSize;
    private Pixmap myHeightMap;
    private MapRenderer myMapRenderer;

    private float myScale;
    private boolean INIT_DONE = false;
    private boolean RESET_VIEW = false;
    private boolean isCompiled = false;
    private boolean IS_RUNNING = false;
    private boolean MOVE_FORWARD = false;
    private boolean MOVE_BACKWARD = false;
    private boolean MOVE_RIGHT = false;
    private boolean MOVE_LEFT = false;
    private boolean OBJECT_ASCEND = false;
    private boolean OBJECT_DESCEND = false;
    private boolean GAME_STARTED = false;

    private float xPos = 0, yPos = 1, zPos = -20;
    private float xLookAt = 0, yLookAt = 0, zLookAt = 100;
    private float xStep, zStep;
    private float viewAngle;
    private Point myMouseLocation;
    private double xDelta;
    private double yDelta;
    private int MOTION_JUMP = -MAX_JUMP_HEIGHT;
    private int[] _skybox={1,2,3,4,5,6};

    public GameView(String[] args) {
	super("Counter Strike v0.1");
	String name = (args.length > 1) ? args[0] : DEFAULT_MAP_FILE;
	try {
	    myHeightMap = new Pixmap((args.length > 1) ? args[0]
		    : DEFAULT_MAP_FILE);
	} catch (IOException e) {
	    System.err.println("Unable to load texture image: " + name);
	    System.exit(1);
	}

    }

    @Override
    public void init(GL2 gl, GLU glu, GLUT glut) {

	myRenderMode = GL2GL3.GL_QUADS;
	myScale = 0.05f;
	myStepSize = 1;
	viewAngle = 90;
	
	xStep = (float) Math.cos(Math.toRadians(viewAngle)); // step distances
	zStep = (float) Math.sin(Math.toRadians(viewAngle));

	xLookAt = (float) (xPos + (LOOK_AT_DIST * xStep)); // look-at posn
	yLookAt = 0;
	zLookAt = (float) (zPos + (LOOK_AT_DIST * zStep));
	isCompiled = false;
	myMouseLocation = MouseInfo.getPointerInfo().getLocation();

	myRenderMode = GL2GL3.GL_QUADS;
	
	myMapRenderer = MapRenderer.getMapRenderer();
	myMapRenderer.init(myHeightMap, myStepSize);
	myMapRenderer.build();
	
	
	gl.glGenTextures(6, _skybox, 0);
	for (int i=0;i<6;i++){
	    skyboxTextures[i] = makeTexture(gl,textureNames[i]); // for the sky box
	    
	}
	
	gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
	gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
	
	gl.glEnable(GLLightingFunc.GL_NORMALIZE);
	gl.glEnable(GLLightingFunc.GL_COLOR_MATERIAL);

    }

    @Override
    public void display(GL2 gl, GLU glu, GLUT glut) {

	if (!isCompiled) {
	    gl.glDeleteLists(MAP_ID, 1);
	    gl.glNewList(MAP_ID, GL2.GL_COMPILE);
	    GAME_STARTED = true;
	    drawMap(gl, glu, glut);
	    gl.glEndList();
	    isCompiled = true;
	}
	createSkybox(gl,glu,glut);
	gl.glScalef(myScale, myScale * HEIGHT_RATIO, myScale);
	gl.glCallList(MAP_ID);
	
    }

    
    private void createSkybox(GL2 gl,GLU glu,GLUT glut) {
	    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
	 // Store the current matrix
	    gl.glPushMatrix();
	 
	    // Reset and transform the matrix.
	    gl.glLoadIdentity();
	    glu.gluLookAt(
	        0,0,0,
	        xLookAt,yLookAt,zLookAt,
	        0,1,0);
	 
	    // Enable/Disable features
	    gl.glPushAttrib(gl.GL_ENABLE_BIT);
	    gl.glEnable(gl.GL_TEXTURE_2D);
	    gl.glDisable(gl.GL_DEPTH_TEST);
	    gl.glDisable(GLLightingFunc.GL_LIGHTING);
	    gl.glDisable(gl.GL_BLEND);
	    // Just in case we set all vertices to white.
	    gl.glColor4f(1,1,1,1);
	 
	    // Render the front quad
	    skyboxTextures[0].enable(gl);
	    skyboxTextures[0].bind(gl);
	    gl.glBegin(gl.GL_QUADS);
	        gl.glTexCoord2f(0, 0); gl.glVertex3f(  0.5f, -0.5f, -0.5f );
	        gl.glTexCoord2f(1, 0); gl.glVertex3f( -0.5f, -0.5f, -0.5f );
	        gl.glTexCoord2f(1, 1); gl.glVertex3f( -0.5f,  0.5f, -0.5f );
	        gl.glTexCoord2f(0, 1); gl.glVertex3f(  0.5f,  0.5f, -0.5f );
	    gl.glEnd();
	 
	    // Render the left quad
	    skyboxTextures[1].enable(gl);
	    skyboxTextures[1].bind(gl);
	    gl.glBegin(gl.GL_QUADS);
	        gl.glTexCoord2f(0, 0); gl.glVertex3f(  0.5f, -0.5f,  0.5f );
	        gl.glTexCoord2f(1, 0); gl.glVertex3f(  0.5f, -0.5f, -0.5f );
	        gl.glTexCoord2f(1, 1); gl.glVertex3f(  0.5f,  0.5f, -0.5f );
	        gl.glTexCoord2f(0, 1); gl.glVertex3f(  0.5f,  0.5f,  0.5f );
	    gl.glEnd();
	 
	    // Render the back quad
	    skyboxTextures[2].enable(gl);
	    skyboxTextures[2].bind(gl);
	    gl.glBegin(gl.GL_QUADS);
	        gl.glTexCoord2f(0, 0); gl.glVertex3f( -0.5f, -0.5f,  0.5f );
	        gl.glTexCoord2f(1, 0); gl.glVertex3f(  0.5f, -0.5f,  0.5f );
	        gl.glTexCoord2f(1, 1); gl.glVertex3f(  0.5f,  0.5f,  0.5f );
	        gl.glTexCoord2f(0, 1); gl.glVertex3f( -0.5f,  0.5f,  0.5f );
	 
	    gl.glEnd();
	 
	    // Render the right quad
	    skyboxTextures[3].enable(gl);
	    skyboxTextures[3].bind(gl);
	    gl.glBegin(gl.GL_QUADS);
	        gl.glTexCoord2f(0, 0); gl.glVertex3f( -0.5f, -0.5f, -0.5f );
	        gl.glTexCoord2f(1, 0); gl.glVertex3f( -0.5f, -0.5f,  0.5f );
	        gl.glTexCoord2f(1, 1); gl.glVertex3f( -0.5f,  0.5f,  0.5f );
	        gl.glTexCoord2f(0, 1); gl.glVertex3f( -0.5f,  0.5f, -0.5f );
	    gl.glEnd();
	 
	    
	    // Render the top quad
	    skyboxTextures[4].enable(gl);
	    skyboxTextures[4].bind(gl);
	    gl.glBegin(gl.GL_QUADS);
	        gl.glTexCoord2f(0, 1); gl.glVertex3f( -0.5f,  0.5f, -0.5f );
	        gl.glTexCoord2f(0, 0); gl.glVertex3f( -0.5f,  0.5f,  0.5f );
	        gl.glTexCoord2f(1, 0); gl.glVertex3f(  0.5f,  0.5f,  0.5f );
	        gl.glTexCoord2f(1, 1); gl.glVertex3f(  0.5f,  0.5f, -0.5f );
	    gl.glEnd();
	 
	    // Render the bottom quad
	    skyboxTextures[5].enable(gl);
	    skyboxTextures[5].bind(gl);
	    gl.glBegin(gl.GL_QUADS);
	        gl.glTexCoord2f(0, 0); gl.glVertex3f( -0.5f, -0.5f, -0.5f );
	        gl.glTexCoord2f(0, 1); gl.glVertex3f( -0.5f, -0.5f,  0.5f );
	        gl.glTexCoord2f(1, 1); gl.glVertex3f(  0.5f, -0.5f,  0.5f );
	        gl.glTexCoord2f(1, 0); gl.glVertex3f(  0.5f, -0.5f, -0.5f );
	    gl.glEnd();
	 
	    // Restore enable bits and matrix
	    gl.glPopAttrib();
	    gl.glPopMatrix();

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
	glu.gluLookAt(xPos, yPos, zPos, // from position
		xLookAt, yLookAt, zLookAt, // to position
		0, 1, 0);		 // up direction

    }

    /**
     * Establish lights in the scene.
     */
    @Override
    public void setLighting(GL2 gl, GLU glu, GLUT glut) {
	float[] light0pos = { 50005, 30000, 50000, 1 };
	gl.glEnable(GLLightingFunc.GL_LIGHTING);
	gl.glEnable(GLLightingFunc.GL_LIGHT0);
	gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_POSITION,
		light0pos, 0);
	float[] noAmbient = { 0.1f, 0.1f, 0.1f, 1f }; // low ambient light
	float[] spec = { 1f, 0.6f, 0f, 1f }; // low ambient light
	float[] diffuse = { 1f, 1f, 1f, 1f };
	// properties of the light
	gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_AMBIENT,
		noAmbient, 0);
	gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_SPECULAR,
		spec, 0);
	gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_DIFFUSE,
		diffuse, 0);
    }

    /**
     * Called when any key is pressed within the canvas.
     */
    @Override
    public void keyPressed(int keyCode) {
	switch (keyCode) {
	case KeyEvent.VK_R:
	    IS_RUNNING=!IS_RUNNING;
	    break;
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
	case KeyEvent.VK_SPACE:
	    if (MOTION_JUMP == -MAX_JUMP_HEIGHT){
		MOTION_JUMP = MAX_JUMP_HEIGHT;
	    };
	}
    }

    /**
     * Animate the scene by changing its state slightly.
     */
    @Override
    public void animate(GL2 gl, GLU glu, GLUT glut) {
	
	if (!INIT_DONE) {
	    gl.glPushMatrix();
	    INIT_DONE = true;
	}
	if (RESET_VIEW) {
	    gl.glPopMatrix();
	    RESET_VIEW = false;
	    INIT_DONE = false;
	}
	
	if (IS_RUNNING){
	    MOVEMENT_INCRE=RUNNING_SPEED;
	}
	else{
	    MOVEMENT_INCRE=WALKING_SPEED;
	}
	
	if (MOTION_JUMP > 0) {
	    gl.glTranslatef(0, -HEIGHT_INCRE, 0);
	    MOTION_JUMP -= 1;
	}
	if ((MOTION_JUMP <= 0) && (MOTION_JUMP != -MAX_JUMP_HEIGHT)) {
	    gl.glTranslatef(0, HEIGHT_INCRE, 0);
	    MOTION_JUMP -= 1;
	}
	if (MOTION_JUMP < -MAX_JUMP_HEIGHT) {
	    MOTION_JUMP = -MAX_JUMP_HEIGHT;
	}
	if (OBJECT_ASCEND) {
	    gl.glTranslatef(0, -0.1f, 0);
	    OBJECT_ASCEND = false;
	}
	if (OBJECT_DESCEND) {
	    gl.glTranslatef(0, 0.1f, 0);
	    OBJECT_DESCEND = false;
	}
	
	float xPos_bak=xPos,zPos_bak=zPos;
	
	if (MOVE_RIGHT) {
	    xPos -= zStep * MOVEMENT_INCRE;
	    zPos += xStep * MOVEMENT_INCRE;
	    MOVE_RIGHT = false;
	}
	if (MOVE_LEFT) {
	    xPos += zStep * MOVEMENT_INCRE;
	    zPos -= xStep * MOVEMENT_INCRE;
	    MOVE_LEFT = false;
	}
	if (MOVE_FORWARD) {
	    xPos += xStep * MOVEMENT_INCRE;
	    zPos += zStep * MOVEMENT_INCRE;
	    MOVE_FORWARD = false;
	}
	if (MOVE_BACKWARD) {
	    xPos -= xStep * MOVEMENT_INCRE;
	    zPos -= zStep * MOVEMENT_INCRE;
	    MOVE_BACKWARD = false;
	}
	
	if (collisionCheck(xPos,zPos)){
	    xPos=xPos_bak;
	    zPos=zPos_bak;
	}
	
	// Rotate Left
	if (xDelta > MOUSE_CENTER_TOLERANCE) {
	    viewAngle += ANGLE_INCRE;
	    xStep = (float) Math.cos(Math.toRadians(viewAngle));
	    zStep = (float) Math.sin(Math.toRadians(viewAngle));
	}
	// Rotate Right
	if (xDelta < -MOUSE_CENTER_TOLERANCE) {
	    viewAngle -= ANGLE_INCRE;
	    xStep = (float) Math.cos(Math.toRadians(viewAngle));
	    zStep = (float) Math.sin(Math.toRadians(viewAngle));
	}
	
	if (yDelta > MOUSE_CENTER_TOLERANCE) {
	    gl.glRotatef(ANGLE_INCRE, 1, 0, 0);
	}
	if (yDelta < -MOUSE_CENTER_TOLERANCE) {
	    gl.glRotatef(-ANGLE_INCRE, 1, 0, 0);
	}

	// Edge collision detection
	if (xPos < -FLOOR_LEN / 2)
	    xPos = -FLOOR_LEN / 2;
	else if (xPos > FLOOR_LEN / 2)
	    xPos = FLOOR_LEN / 2;
	if (zPos < -FLOOR_LEN / 2)
	    zPos = -FLOOR_LEN / 2;
	else if (zPos > FLOOR_LEN / 2)
	    zPos = FLOOR_LEN / 2;

	
//	System.out.print(xPos);
//	System.out.print(" ");
//	System.out.println(zPos);

	xLookAt = (float) (xPos + (xStep * LOOK_AT_DIST));
	zLookAt = (float) (zPos + (zStep * LOOK_AT_DIST));
    }

    private boolean collisionCheck(float xPos2, float zPos2) {
	// TODO Auto-generated method stub
	return false;
    }

    /**
     * Respond to the mouse being moved in the canvas.
     *
     * @param pt
     *            current position of the mouse
     */

    @Override
    public void mouseMoved(Point pt) {
	Point newMouseLocation = MouseInfo.getPointerInfo().getLocation();
	xDelta = newMouseLocation.getX() - SCREEN_WIDTH_CENTER;
	yDelta = newMouseLocation.getY() - SCREEN_HEIGHT_CENTER;
	myMouseLocation = newMouseLocation;

    }

    /**
     * Respond to the press and release of the mouse.
     *
     * @param pt
     *            current position of the mouse
     * @param button
     *            mouse button that was clicked
     */
    @Override
    public void mouseClicked(Point pt, int button) {
	// by default, do nothing
    }

    /**
     * Called when the mouse is pressed within the canvas and it hits something.
     */

   
    @Override
    public void selectObject(GL2 gl, GLU glu, GLUT glut, int numSelected,
	    int[] selectInfo) {
	// by default, do nothing
    }
    
    private Texture makeTexture(GL2 gl, String name) {
   	try {
   	    InputStream stream = new FileInputStream(name);
   	    Texture result = TextureIO.newTexture(stream, false, "rgb");
//   	     result.setTexParameteri(gl,gl.GL_TEXTURE_MAG_FILTER, gl.GL_NEAREST);
//   	     result.setTexParameteri(gl,gl.GL_TEXTURE_MIN_FILTER, gl.GL_NEAREST);
   	    return result;
   	} catch (IOException e) {
   	    System.err.println("Unable to load texture image: " + name);
   	    e.printStackTrace();
   	    // should never happen
   	    return null;
   	}
       }
    
    public static void main(String[] args) {

	myFrame = new JOGLFrame(new GameView(args));
	myFrame.setResizable(false);
    }

}
