import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.util.glu.GLU.*;

public class gameWorld {

    private int fps;
    private long lastFPS;
    public int myFPS = 0;
    public int myWidth, myHeight;
    public float myFOV;

    public gameWorld(){
        myFOV = 75;
        myWidth = 512;
        myHeight = 512;
    }

    public void updateFPS() {
        if (getTime() - lastFPS > 1000) {
            myFPS = fps;
            Display.setTitle("FPS: " + myFPS);
            fps = 0;
            lastFPS += 1000;
        }
        fps++;
    }

    public long getTime() {
        return (Sys.getTime() * 1000) / Sys.getTimerResolution();
    }

    public void start() {
        lastFPS = getTime(); //initialise lastFPS by setting to current Time
        try {
            Display.setDisplayMode(new DisplayMode(myWidth, myHeight));
            Display.create();

        } catch (LWJGLException e) {
            e.printStackTrace();
            System.exit(0);
        }

        initGL(); // init OpenGL

        while (!Display.isCloseRequested()) {
            update();
            renderGL();

            Display.update();
            //Display.sync(60); // cap fps to 60fps
        }

        Display.destroy();
    }

    public void update() {
        updateFPS();
    }

    public void initGL() {
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        gluPerspective(myFOV, ((float)myWidth) / ((float)myHeight), 0.01f, 2500f);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
    }

    public void renderGL() {
        double rotationx = Mouse.getX();
        double rotationy = Mouse.getY();
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glLoadIdentity();
        gluLookAt(500,500,500,50,50,50,0,1,0);
        GL11.glPushMatrix();
            GL11.glScalef(2.5f, 2.5f, 2.5f);
            GL11.glTranslatef(50,50,50);
            //GL11.glRotatef((float) rotationx, 1f, 0f, 0f);
            GL11.glRotatef((float) rotationy, 0f, 1f, 0f);
            GL11.glTranslatef(-50, -50, -50);
            GeometryFactory.gridFlat();
            GeometryFactory.tree();
        GL11.glPopMatrix();
    }

}