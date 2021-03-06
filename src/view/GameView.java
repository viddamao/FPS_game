package view;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2GL3;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.glu.GLU;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import model.Bot;
import model.Face;
import model.Vertex;
import model.Weapon;

import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import framework.JOGLFrame;
import framework.OBJException;
import framework.OBJModel;
import framework.Pixmap;
import framework.Scene;

public class GameView extends Scene {

    private static JOGLFrame myFrame;
    private final String DEFAULT_MAP_FILE = "src/img/iceworld_0.bmp";
    String[] textureNames = new String[] { "src/img/skybox/skybox_fr.rgb",
	    "src/img/skybox/skybox_lf.rgb", "src/img/skybox/skybox_bk.rgb",
	    "src/img/skybox/skybox_rt.rgb", "src/img/skybox/skybox_up.rgb",
	    "src/img/skybox/skybox_dn.rgb", };
    private String movementSoundFileName = "src/sound/run.wav";
    private String reloadSoundFileName = "src/sound/ak47_clipout.wav";
    private String addBotSoundFileName = "src/sound/com_go.wav";
    private String winSoundFileName = "src/sound/terwin.wav";
    private String killSoundFileName = "src/sound/die1.wav";
    
     
    Texture[] skyboxTextures = new Texture[7];
    private final int MAP_ID = 1;
    private final float HEIGHT_RATIO = 0.25f;
    private final double SCREEN_WIDTH_CENTER = 1366 / 2;
    private final double SCREEN_HEIGHT_CENTER = 768 / 2;
    private double MOVEMENT_INCRE = 0.1;
    private final double WALKING_SPEED = 0.1;
    private final double RUNNING_SPEED = 0.2;
    private final int MOUSE_CENTER_TOLERANCE = 50;

    private final static double LOOK_AT_DIST = 100;
    private static final int FLOOR_LEN = 48;
    private static final float ANGLE_INCRE = 0.5f;
    private static final float HEIGHT_INCRE = 0.25f;
    private static final int MAX_JUMP_HEIGHT = 10;

    private int myHP = 100;
    private int myShells = 30;

    private int myRenderMode;
    private int myStepSize;
    private Pixmap myHeightMap;
    private MapRenderer myMapRenderer;
    public ArrayList<Bot> myBots = new ArrayList<Bot>();
    public int numBot = 0;

    private float myScale;
    private boolean INIT_DONE = false;
    private boolean RESET_VIEW = false;
    private boolean isCompiled = false;
    private boolean IS_RUNNING = true;
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
    private double xDelta;
    private double yDelta;
    private int MOTION_JUMP = -MAX_JUMP_HEIGHT;
    private int[] _skybox = { 1, 2, 3, 4, 5, 6 };
    private OBJModel myModel;
    private OBJModel myGunModel;
    private String myGunModelFile = "src/img/tommy-gun.obj";
    private String mySpriteModelFile = "src/img/soldier.obj";
    private int PLAY_COUNTER = 0;
    private TextRenderer renderer;
    private int totalShells[] = {30,7};
    private int myWeaponDamage[] = {50,34};
    private String botNames[] = {"Duvall ","Ang ","Vidda ","HeaTon ","John ","Fisker ","kingZ ","Alex ","Allen "};
    private Weapon myWeapon;
    private TextRenderer renderer1;

    public GameView(String[] args) {
	super("Counter Strike v0.10");
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
	Image cursorImage = Toolkit.getDefaultToolkit().getImage(
		"src/img/crosshair.gif");
	myFrame.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));

	myRenderMode = GL2GL3.GL_QUADS;
	myScale = 0.05f;
	myStepSize = 4;
	viewAngle = 90;

	xStep = (float) Math.cos(Math.toRadians(viewAngle)); // step distances
	zStep = (float) Math.sin(Math.toRadians(viewAngle));

	xLookAt = (float) (xPos + (LOOK_AT_DIST * xStep)); // look-at posn
	yLookAt = 0;
	zLookAt = (float) (zPos + (LOOK_AT_DIST * zStep));
	isCompiled = false;
	MouseInfo.getPointerInfo().getLocation();

	myRenderMode = GL2GL3.GL_QUADS;

	myMapRenderer = MapRenderer.getMapRenderer();
	myMapRenderer.init(myHeightMap, myStepSize);
	myMapRenderer.build();

	gl.glGenTextures(6, _skybox, 0);
	for (int i = 0; i < 6; i++) {
	    skyboxTextures[i] = makeTexture(gl, textureNames[i]); // for the sky
								  // box

	}

	gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
	gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);

	gl.glEnable(GLLightingFunc.GL_NORMALIZE);
	gl.glEnable(GLLightingFunc.GL_COLOR_MATERIAL);

	try {
	    myModel = new OBJModel(mySpriteModelFile);
	    myGunModel = new OBJModel(myGunModelFile);
	    // System.out.println(myModel);
	} catch (OBJException e) {
	    System.out.println("Cannot load " + mySpriteModelFile);
	    e.printStackTrace();
	    System.exit(0);
	}
	Bot newBot = new Bot();
	newBot.setzPos(-10f);
	newBot.setFacing(90);
	myBots.add(newBot);
	numBot=1;

	myWeapon = new Weapon();
	
	renderer = new TextRenderer(new Font("SansSerif", Font.BOLD, 54));

	renderer1 = new TextRenderer(new Font("SansSerif", Font.BOLD, 27));
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
	createSkybox(gl, glu, glut);
	gl.glScalef(myScale, myScale * HEIGHT_RATIO, myScale);
	gl.glCallList(MAP_ID);

	//Weapon model
	gl.glPushMatrix();
	    gl.glTranslatef(-myWeapon.getxPos() * 20f, yPos + 60f, myWeapon.getzPos() * 20f);
	    gl.glScalef(30, 30, 30);
	    gl.glRotatef(myWeapon.getFacing(), 0, 1, 0);

	    if (myWeapon.getModel()==1){
	    gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, myRenderMode);
	    myGunModel.render(gl);
	    gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
	    }
	    else{
	     glut.glutSolidTeapot(0.1);	 
	    }
	    gl.glPopMatrix();
	
	// bot models
	for (Bot i : myBots) {
	    i.turn(xPos, zPos);
	    gl.glPushMatrix();
	    gl.glTranslatef(-i.getxPos() * 20f, yPos + 60f, i.getzPos() * 20f);
	    gl.glScalef(30, 110, 30);
	    gl.glRotatef(i.getFacing(), 0, 1, 0);
	    gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, myRenderMode);
	    myModel.render(gl);
	    gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
	    gl.glPopMatrix();

	}

	// Display HP
	renderer.beginRendering(1366, 768);
	// optionally set the color
	renderer.setColor(0f, 1f, 0.2f, 0.8f);
	renderer.draw("  |  ", 1366/2-30, 768/2+30);
	renderer.draw("__  __", 1366/2-70, 768/2);
	renderer.draw("  |  ", 1366/2-30, 768/2-60);
	
	
	renderer.draw("HP", 100, 100);
	renderer.draw(Integer.toString(myHP), 200, 100);
	renderer.draw(Integer.toString(myShells), 1130, 100);
	renderer.draw("/", 1200, 100);
	renderer.draw(Integer.toString(totalShells[myWeapon.getModel()]), 1230, 100);
	
	renderer.endRendering();

	renderer1.beginRendering(1366, 768);
	renderer1.setColor(0f, 1f, 0.2f, 0.8f);
	renderer1.draw("Bots remaining", 500, 720);
	renderer1.draw(Integer.toString(numBot), 730, 720);
	
	int currentYaxis=600,count=0;
	for (Bot i:myBots){
	    renderer1.draw(botNames[count], 1180, currentYaxis);
	    renderer1.draw(Integer.toString(i.getHp()), 1300, currentYaxis);
	    currentYaxis-=40;	
	    count++;
	}
	
	renderer1.endRendering();
	
	// Reticle
	// gl.glPushMatrix();
	// gl.glTranslatef(xPos*20, 90f, zPos*20+20f);
	// gl.glColor3f(0f, 1f,0f);
	// gl.glBegin(gl.GL_POLYGON);
	// gl.glVertex3f(4.5f, 4.5f, 0.0f);
	// gl.glVertex3f(4.5f, 10.5f, 0.0f);
	// gl.glVertex3f(5.5f, 10.5f, 0.0f);
	// gl.glVertex3f(4.5f, 5.5f, 0.0f);
	// gl.glEnd();
	// gl.glPopMatrix();
	//
	// gl.glPushMatrix();
	// gl.glTranslatef(xPos*20, 90f, zPos*20+20f);
	// gl.glColor3f(1f, 1f,1f);
	// gl.glBegin(gl.GL_POLYGON);
	// gl.glVertex3f(2.5f, 2.5f, 0.0f);
	// gl.glVertex3f(7.5f, 2.5f, 0.0f);
	// gl.glVertex3f(7.5f, 7.5f, 0.0f);
	// gl.glVertex3f(2.5f, 7.5f, 0.0f);
	// gl.glEnd();

    }

    private void createSkybox(GL2 gl, GLU glu, GLUT glut) {
	gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
	// Store the current matrix
	gl.glPushMatrix();

	// Reset and transform the matrix.
	gl.glLoadIdentity();
	glu.gluLookAt(0, 0, 0, xLookAt, yLookAt, zLookAt, 0, 1, 0);

	// Enable/Disable features
	gl.glPushAttrib(GL2.GL_ENABLE_BIT);
	gl.glEnable(GL.GL_TEXTURE_2D);
	gl.glDisable(GL.GL_DEPTH_TEST);
	gl.glDisable(GLLightingFunc.GL_LIGHTING);
	gl.glDisable(GL.GL_BLEND);
	// Just in case we set all vertices to white.
	gl.glColor4f(1, 1, 1, 1);

	// Render the front quad
	skyboxTextures[0].enable(gl);
	skyboxTextures[0].bind(gl);
	gl.glBegin(GL2GL3.GL_QUADS);
	gl.glTexCoord2f(0, 0);
	gl.glVertex3f(0.5f, -0.5f, -0.5f);
	gl.glTexCoord2f(1, 0);
	gl.glVertex3f(-0.5f, -0.5f, -0.5f);
	gl.glTexCoord2f(1, 1);
	gl.glVertex3f(-0.5f, 0.5f, -0.5f);
	gl.glTexCoord2f(0, 1);
	gl.glVertex3f(0.5f, 0.5f, -0.5f);
	gl.glEnd();

	// Render the left quad
	skyboxTextures[1].enable(gl);
	skyboxTextures[1].bind(gl);
	gl.glBegin(GL2GL3.GL_QUADS);
	gl.glTexCoord2f(0, 0);
	gl.glVertex3f(0.5f, -0.5f, 0.5f);
	gl.glTexCoord2f(1, 0);
	gl.glVertex3f(0.5f, -0.5f, -0.5f);
	gl.glTexCoord2f(1, 1);
	gl.glVertex3f(0.5f, 0.5f, -0.5f);
	gl.glTexCoord2f(0, 1);
	gl.glVertex3f(0.5f, 0.5f, 0.5f);
	gl.glEnd();

	// Render the back quad
	skyboxTextures[2].enable(gl);
	skyboxTextures[2].bind(gl);
	gl.glBegin(GL2GL3.GL_QUADS);
	gl.glTexCoord2f(0, 0);
	gl.glVertex3f(-0.5f, -0.5f, 0.5f);
	gl.glTexCoord2f(1, 0);
	gl.glVertex3f(0.5f, -0.5f, 0.5f);
	gl.glTexCoord2f(1, 1);
	gl.glVertex3f(0.5f, 0.5f, 0.5f);
	gl.glTexCoord2f(0, 1);
	gl.glVertex3f(-0.5f, 0.5f, 0.5f);

	gl.glEnd();

	// Render the right quad
	skyboxTextures[3].enable(gl);
	skyboxTextures[3].bind(gl);
	gl.glBegin(GL2GL3.GL_QUADS);
	gl.glTexCoord2f(0, 0);
	gl.glVertex3f(-0.5f, -0.5f, -0.5f);
	gl.glTexCoord2f(1, 0);
	gl.glVertex3f(-0.5f, -0.5f, 0.5f);
	gl.glTexCoord2f(1, 1);
	gl.glVertex3f(-0.5f, 0.5f, 0.5f);
	gl.glTexCoord2f(0, 1);
	gl.glVertex3f(-0.5f, 0.5f, -0.5f);
	gl.glEnd();

	// Render the top quad
	skyboxTextures[4].enable(gl);
	skyboxTextures[4].bind(gl);
	gl.glBegin(GL2GL3.GL_QUADS);
	gl.glTexCoord2f(0, 1);
	gl.glVertex3f(-0.5f, 0.5f, -0.5f);
	gl.glTexCoord2f(0, 0);
	gl.glVertex3f(-0.5f, 0.5f, 0.5f);
	gl.glTexCoord2f(1, 0);
	gl.glVertex3f(0.5f, 0.5f, 0.5f);
	gl.glTexCoord2f(1, 1);
	gl.glVertex3f(0.5f, 0.5f, -0.5f);
	gl.glEnd();

	// Render the bottom quad
	skyboxTextures[5].enable(gl);
	skyboxTextures[5].bind(gl);
	gl.glBegin(GL2GL3.GL_QUADS);
	gl.glTexCoord2f(0, 0);
	gl.glVertex3f(-0.5f, -0.5f, -0.5f);
	gl.glTexCoord2f(0, 1);
	gl.glVertex3f(-0.5f, -0.5f, 0.5f);
	gl.glTexCoord2f(1, 1);
	gl.glVertex3f(0.5f, -0.5f, 0.5f);
	gl.glTexCoord2f(1, 0);
	gl.glVertex3f(0.5f, -0.5f, -0.5f);
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
		0, 1, 0); // up direction

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
	    playSound(reloadSoundFileName);
	    myShells = totalShells[myWeapon.getModel()];
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
	case KeyEvent.VK_G:
	    if (xPos*xPos+zPos*zPos<4){
		myWeapon.setModel(1-myWeapon.getModel());
		myShells=totalShells[myWeapon.getModel()];
	    }
	    break;
	case KeyEvent.VK_SPACE:
	    if (MOTION_JUMP == -MAX_JUMP_HEIGHT) {
		MOTION_JUMP = MAX_JUMP_HEIGHT;
	    }
	    break;
	case KeyEvent.VK_EQUALS:
	    
	    if (numBot>=9) break;
	    float newRandX = (float) ((Math.random() - 1) * 20f);
	    float newRandZ = (float) ((Math.random() - 1) * 20f);
	    while (collisionCheck(newRandX, newRandZ)) {
		newRandX = (float) ((Math.random() - 1) * 20f);
		newRandZ = (float) ((Math.random() - 1) * 20f);

	    }
	    // System.out.println(newRandX);
	    // System.out.println(newRandZ);
	    Bot newBot = new Bot();
	    newBot.setxPos(newRandX);
	    newBot.setzPos(newRandZ);
	    myBots.add(newBot);
	    numBot+=1;
	    playSound(addBotSoundFileName);
	    break;
	case KeyEvent.VK_MINUS:
	    myBots.clear();
	    break;
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

	if (IS_RUNNING) {
	    MOVEMENT_INCRE = RUNNING_SPEED;
	} else {
	    MOVEMENT_INCRE = WALKING_SPEED;
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

	float xPos_bak = xPos, zPos_bak = zPos;

	if (MOVE_RIGHT) {
	    playSound(movementSoundFileName);
	    xPos -= zStep * MOVEMENT_INCRE;
	    zPos += xStep * MOVEMENT_INCRE;
	    MOVE_RIGHT = false;
	}
	if (MOVE_LEFT) {
	    playSound(movementSoundFileName);
	    xPos += zStep * MOVEMENT_INCRE;
	    zPos -= xStep * MOVEMENT_INCRE;
	    MOVE_LEFT = false;
	}
	if (MOVE_FORWARD) {
	    playSound(movementSoundFileName);
	    xPos += xStep * MOVEMENT_INCRE;
	    zPos += zStep * MOVEMENT_INCRE;
	    MOVE_FORWARD = false;
	}
	if (MOVE_BACKWARD) {
	    playSound(movementSoundFileName);
	    xPos -= xStep * MOVEMENT_INCRE;
	    zPos -= zStep * MOVEMENT_INCRE;
	    MOVE_BACKWARD = false;
	}

	if (collisionCheck(xPos, zPos)) {
	    xPos = xPos_bak;
	    zPos = zPos_bak;
	}

	// Rotate Left
	if (xDelta > MOUSE_CENTER_TOLERANCE) {
	    viewAngle += ANGLE_INCRE * 2;
	    xStep = (float) Math.cos(Math.toRadians(viewAngle));
	    zStep = (float) Math.sin(Math.toRadians(viewAngle));
	}
	// Rotate Right
	if (xDelta < -MOUSE_CENTER_TOLERANCE) {
	    viewAngle -= ANGLE_INCRE * 2;
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

//	 System.out.print(xPos);
//	 System.out.print(" ");
//	 System.out.println(zPos);
//	 System.out.print(" ");
//	 System.out.println(viewAngle);

	xLookAt = (float) (xPos + (xStep * LOOK_AT_DIST));
	zLookAt = (float) (zPos + (zStep * LOOK_AT_DIST));
    }

    private void playSound(String fileName) {
	File soundFile1 = new File(fileName);
	try {
	    if ((PLAY_COUNTER != 10)
		    && (fileName.equals(movementSoundFileName)))
		PLAY_COUNTER++;
	    else {
		PLAY_COUNTER = 0;
		AudioInputStream audioIn1 = AudioSystem
			.getAudioInputStream(soundFile1);

		Clip clip = AudioSystem.getClip();
		clip.open(audioIn1);
		if (clip.isRunning())
		    clip.stop();
		clip.start();
	    }
	} catch (UnsupportedAudioFileException | IOException e) {
	    System.out.println("unsupported audio file");
	    e.printStackTrace();
	} catch (LineUnavailableException e) {
	    System.out.println("Line unavailable");
	    e.printStackTrace();
	}
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

	// System.out.print(newMouseLocation.getX());
	// System.out.print(" ");
	// System.out.println(newMouseLocation.getY());

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
	// // play fire sound effect
	// File soundFile1 = new File("src/sound/ak47-1.wav");
	//
	// try {
	// AudioInputStream audioIn1 = AudioSystem
	// .getAudioInputStream(soundFile1);
	//
	// Clip clip = AudioSystem.getClip();
	// clip.open(audioIn1);
	// clip.setFramePosition(0);
	// clip.start();
	// clip.loop(1);
	// } catch (UnsupportedAudioFileException | IOException e) {
	// System.out.println("unsupported audio file");
	// e.printStackTrace();
	// } catch (LineUnavailableException e) {
	// System.out.println("Line unavailable");
	// e.printStackTrace();
	// }
    }

    /**
     * Respond to the press of the mouse.
     *
     * @param pt
     *            current position of the mouse
     * @param button
     *            mouse button that was pressed
     */
    public void mousePressed(Point pt, int button) {
	// play fire sound effect
	if (button == 1) {
	    if (myShells>0){
	    myShells--;
	    File soundFile1 = (myWeapon.getModel()==0)?new File("src/sound/ak47-1.wav"):new File("src/sound/deagle-1.wav");

	    try {
		AudioInputStream audioIn1 = AudioSystem
			.getAudioInputStream(soundFile1);

		Clip clip = AudioSystem.getClip();
		clip.open(audioIn1);
		clip.setFramePosition(0);
		clip.start();
	    } catch (UnsupportedAudioFileException | IOException e) {
		System.out.println("unsupported audio file");
		e.printStackTrace();
	    } catch (LineUnavailableException e) {
		System.out.println("Line unavailable");
		e.printStackTrace();
	    
	    }
	    double dist=0,alpha=0,beta=0,temp=0;
	    try{
	    for (Bot i:myBots){
		dist=Math.sqrt(Math.pow((xPos-i.getxPos()),2)+Math.pow((zPos-i.getzPos()),2));
		
		temp=(Math.atan(0.3/dist)/3.1415)*180;
		alpha=90+temp+i.getFacing()-180;
		beta=90-temp+i.getFacing()-180;
		 System.out.print(alpha);
		 System.out.print(" ");
		 System.out.println(beta);
		if ((viewAngle+alpha>=180)&&(viewAngle+beta<=180)){
		    i.setHp(i.getHp()-myWeaponDamage[myWeapon.getModel()]);
		    
		}
		
		if (i.getHp()<=0){
		    myBots.remove(i);
		    numBot--;
		    playSound(killSoundFileName);
		    if (numBot==0){
			playSound(winSoundFileName);
			continue;
		    }
		}
	    }
	    }
	    catch (ConcurrentModificationException e){
		
	    }
	    }
	}
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
	    // result.setTexParameteri(gl,gl.GL_TEXTURE_MAG_FILTER,
	    // gl.GL_NEAREST);
	    // result.setTexParameteri(gl,gl.GL_TEXTURE_MIN_FILTER,
	    // gl.GL_NEAREST);
	    return result;
	} catch (IOException e) {
	    System.err.println("Unable to load texture image: " + name);
	    e.printStackTrace();
	    // should never happen
	    return null;
	}
    }

    public static void main(String[] args) {

	myFrame = new JOGLFrame(new GameView(args), new Dimension(1366, 768));

    }

}
