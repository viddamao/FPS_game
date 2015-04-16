package view;


import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import framework.JOGLFrame;

public class MouseController implements MouseMotionListener {
    int mx = 0;
    int my = 0;
    int screenX;
    int screenY;
    int screenXDiv2;
    int screenYDiv2;
    public int dmx = 0;
    public int dmy = 0;
    JOGLFrame f;

    Robot robot;

    public MouseController() throws Exception {
	robot = new Robot();
	Toolkit t = Toolkit.getDefaultToolkit();
	screenX = t.getScreenSize().width;
	screenY = t.getScreenSize().height;
	screenXDiv2 = screenX >> 1;
	screenYDiv2 = screenY >> 1;
    }

    public void setFrame(JOGLFrame f) {
	if (this.f != null)
	    this.removeFrame(this.f);

	this.f = f;
	f.addMouseMotionListener(this);

    }

    public void removeFrame(JOGLFrame f) {
	f.removeMouseMotionListener(this);
	this.f = null;

    }

    public void mouseDragged(MouseEvent e) {
	this.mouseMoved(e);
    }

    public void mouseMoved(Point pt) {
	mx = (int) pt.getX();
	my = (int) pt.getY();
	if (mx != screenXDiv2 || my != screenYDiv2) {
	    if (mx != screenXDiv2) {
		dmx += mx - screenXDiv2;
	    }
	    if (my != screenYDiv2) {
		dmy += my - screenYDiv2;
	    }
	    robot.mouseMove(screenXDiv2, screenYDiv2);
	}
    }

    @Override
    public void mouseMoved(MouseEvent e) {
	// TODO Auto-generated method stub
	
    }

}
