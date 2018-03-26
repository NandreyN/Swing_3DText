import javax.swing.*;
import java.applet.Applet;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Font3D;
import javax.media.j3d.FontExtrusion;
import javax.media.j3d.Geometry;
import javax.media.j3d.Light;
import javax.media.j3d.Material;
import javax.media.j3d.PointLight;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Text3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.border.Border;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;

import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.picking.PickTool;
import com.sun.j3d.utils.picking.behaviors.PickRotateBehavior;
import com.sun.j3d.utils.picking.behaviors.PickTranslateBehavior;
import com.sun.j3d.utils.picking.behaviors.PickZoomBehavior;
import com.sun.j3d.utils.universe.SimpleUniverse;

public class MainFrameText extends JFrame {
    private SimpleUniverse u = null;
    private Color3f eColor = new Color3f(0.0f, 0.0f, 0.0f);
    private Color3f sColor = new Color3f(1.0f, 1.0f, 1.0f);
    private Color3f objColor = new Color3f(0.6f, 0.6f, 0.6f);
    private Color3f lColor1 = new Color3f(1.0f, 0.0f, 0.0f);
    private Color3f lColor2 = new Color3f(0.0f, 1.0f, 0.0f);
    private Color3f alColor = new Color3f(0.2f, 0.2f, 0.2f);
    private Color3f bgColor = new Color3f(0.05f, 0.05f, 0.2f);

    private Text3D textDisplayed;
    private Shape3D textShape;
    private TransformGroup transformGroup;
    private BranchGroup objRoot;
    private BranchGroup scene;

    private final int WIDTH, HEIGHT;

    private JMenuBar bar;
    private JMenu menu;
    private JMenuItem lightOne, lightTwo;
    private Color3f colorOne, colorTwo;
    private Canvas3D c;

    public MainFrameText(int width, int height) {
        GraphicsConfiguration config = SimpleUniverse
                .getPreferredConfiguration();
        c = new Canvas3D(config);

        this.WIDTH = width;
        this.HEIGHT = height;
        addMenu();
        init();
        //initListeners();
    }

    private void addMenu() {
        bar = new JMenuBar();
        setJMenuBar(bar);
        colorOne = lColor1;
        colorTwo = lColor2;

        menu = new JMenu("Lights");
        bar.add(menu);
        lightOne = new JMenuItem("Choose first");
        menu.add(lightOne);

        lightTwo = new JMenuItem("Choose second");
        menu.add(lightTwo);
        addMenuListeners();
    }

    private void addMenuListeners() {
        lightTwo.addActionListener(x -> {
            Color newColor = JColorChooser.showDialog(null, "Choose a color", Color.YELLOW);
            colorTwo = new Color3f(newColor);

            u.cleanup();
            u = new SimpleUniverse(c);
            recreateSceneWithMessage(textDisplayed.getString());
        });

        lightOne.addActionListener(x -> {
            Color newColor = JColorChooser.showDialog(null, "Choose a color", Color.YELLOW);
            colorOne = new Color3f(newColor);

            u.cleanup();
            u = new SimpleUniverse(c);
            recreateSceneWithMessage(textDisplayed.getString());

        });
    }

    public BranchGroup createSceneGraph(String msg) {
        // Create the root of the branch graph
        objRoot = new BranchGroup();

        // Create a Transformgroup to scale all objects so they
        // appear in the scene.
        transformGroup = new TransformGroup();
        Transform3D t3d = new Transform3D();
        t3d.setScale(0.4);
        transformGroup.setTransform(t3d);
        objRoot.addChild(transformGroup);

        // Create a bounds for the background and lights
        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0),
                100.0);

        // Set up the background
        Background bg = new Background(bgColor);
        bg.setApplicationBounds(bounds);
        transformGroup.addChild(bg);

        // Defines object look under illumination
        Material m = new Material(objColor, eColor, objColor, sColor, 100.0f);

        // Thant`s how the object looks like
        Appearance a = new Appearance();
        m.setLightingEnable(true);
        a.setMaterial(m);

        Font3D f3d = new Font3D(new Font("TestFont", Font.ITALIC, 1),
                new FontExtrusion());

        textDisplayed = new Text3D(f3d, msg, new Point3f(-2f,
                -0.7f, 0.0f));
        textDisplayed.setCapability(Geometry.ALLOW_INTERSECT);

        // Geometric properties of text, appearance is applied to shape
        textShape = new Shape3D();
        textShape.setGeometry(textDisplayed);
        textShape.setAppearance(a);
        transformGroup.addChild(textShape);

        // Create transformations for the positional lights
        Transform3D transform3D = getLightTransform3D(1, 2, 0);

        TransformGroup light1Transform = applyLightTransform(transform3D);
        transformGroup.addChild(light1Transform);

        Transform3D transform3D2 = getLightTransform3D(-3, -5, 2);
        TransformGroup secondLightTransform = applyLightTransform(transform3D2);
        transformGroup.addChild(secondLightTransform);

        initLights(bounds, light1Transform, colorOne, new Point3f(1, 2, 0));
        initLights(bounds, secondLightTransform, colorTwo, new Point3f(-3, -5, 2));

        objRoot.compile();
        return objRoot;
    }

    private TransformGroup applyLightTransform(Transform3D transform3D) {
        TransformGroup l1Trans = new TransformGroup(transform3D);
        l1Trans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        l1Trans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        l1Trans.setCapability(TransformGroup.ENABLE_PICK_REPORTING);
        return l1Trans;
    }

    private Transform3D getLightTransform3D(double v, double v1, double v2) {
        Transform3D transform3D = new Transform3D();
        Vector3d lPos1 = new Vector3d(v, v1, v2);
        transform3D.set(lPos1);
        return transform3D;
    }

    private void initLights(BoundingSphere bounds, TransformGroup transformGr, Color3f lightColor, Point3f lightPoint) {
        Point3f attenuation = new Point3f(1.0f, 0.0f, 0.0f);

        Light firstLight = new PointLight(lightColor, lightPoint, attenuation);
        firstLight.setInfluencingBounds(bounds);

        // Add the lights into the scene graph
        transformGr.addChild(firstLight);
    }

    public void init() {
        setLayout(new BorderLayout());
        add(c, BorderLayout.CENTER);

        c.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {

                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE && textDisplayed.getString().length() > 0) {
                    u.cleanup();
                    u = new SimpleUniverse(c);
                    String s = textDisplayed.getString();
                    s = s.substring(0, s.length() - 1);
                    recreateSceneWithMessage(s);
                } else if ((Character.isLetterOrDigit(e.getKeyCode()) || e.getKeyCode() == KeyEvent.VK_SPACE)
                        &&
                        textDisplayed.getString().length() < 8) {
                    u.cleanup();
                    u = new SimpleUniverse(c);
                    recreateSceneWithMessage(textDisplayed.getString() + e.getKeyChar());
                }
                super.keyReleased(e);
            }
        });

        u = new SimpleUniverse(c);
        scene = createSceneGraph("Sample");

        // This will move the ViewPlatform back a bit so the
        // objects in the scene can be viewed.
        u.getViewingPlatform().setNominalViewingTransform();
        u.addBranchGraph(scene);
    }

    private void recreateSceneWithMessage(String msg) {
        scene = createSceneGraph(msg);

        // This will move the ViewPlatform back a bit so the
        // objects in the scene can be viewed.
        u.getViewingPlatform().setNominalViewingTransform();
        u.addBranchGraph(scene);
    }

    public static void main(String[] args) {
        JFrame frame = new MainFrameText(1000, 500);
        frame.setBounds(100, 100, 1000, 500);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
