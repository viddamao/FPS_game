package view;

import java.io.IOException;
import java.io.InputStream;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

public class Skybox implements GLEventListener {

    private Texture skyboxTexture;

    @Override
    public void init(GLAutoDrawable drawable) {

	GL2 gl = (GL2) drawable.getGL();
	gl.glShadeModel(GLLightingFunc.GL_SMOOTH);
	try {
	    GLProfile myProfile = drawable.getGLProfile();
	    InputStream stream = getClass().getResourceAsStream(
		    "src/img/skybox_up.rgb");
	    TextureData data = TextureIO.newTextureData(myProfile, stream,
		    false, "rgb");
	    skyboxTexture = TextureIO.newTexture(data);
	} catch (IOException exc) {
	    exc.printStackTrace();
	    System.exit(1);
	}
	gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
	gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
	// Enable VSync
	gl.setSwapInterval(1);

	// Setup the drawing area and shading mode
	gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width,
	    int height) {
	GL2 gl = (GL2) drawable.getGL();
	GLU glu = new GLU();

	if (height <= 0) {

	    height = 1;
	}
	final float h = (float) width / (float) height;
	gl.glViewport(0, 0, width, height);
	gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
	gl.glLoadIdentity();
	glu.gluPerspective(45.0f, h, 1.0, 20.0);
	gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
	gl.glLoadIdentity();
    }

    @Override
    public void display(GLAutoDrawable drawable) {
	GLU glu = new GLU();
	GL2 gl = (GL2) drawable.getGL();
	gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
	gl.glLoadIdentity();
	gl.glPushMatrix();
	// Reset and transform the matrix.
	gl.glLoadIdentity();
	glu.gluLookAt(1, 2, 0, 0, 0, 0, 0, 1, 0);
	// Enable/Disable features
	gl.glPushAttrib(GL2.GL_ENABLE_BIT);
	gl.glEnable(GL.GL_TEXTURE_2D);
	gl.glDisable(GL.GL_DEPTH_TEST);
	gl.glDisable(GLLightingFunc.GL_LIGHTING);
	gl.glDisable(GL.GL_BLEND);
	gl.glColor4f(1, 1, 1, 1);
	skyboxTexture.enable(drawable.getGL());
	skyboxTexture.bind(drawable.getGL());
	// right
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

	// left
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
	// back
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
	// up
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
	// down
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
	// front
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
	gl.glPopAttrib();
	gl.glPopMatrix();
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
	// TODO Auto-generated method stub

    }

}