import javax.swing.*;
import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GraphicsConfiguration;

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

    public MainFrameText() {
        init();
    }

    public BranchGroup createSceneGraph(Canvas3D canvas) {
        Color3f eColor = new Color3f(0.0f, 0.0f, 0.0f);
        Color3f sColor = new Color3f(1.0f, 1.0f, 1.0f);
        Color3f objColor = new Color3f(0.6f, 0.6f, 0.6f);
        Color3f lColor1 = new Color3f(1.0f, 0.0f, 0.0f);
        Color3f lColor2 = new Color3f(0.0f, 1.0f, 0.0f);
        Color3f alColor = new Color3f(0.2f, 0.2f, 0.2f);
        Color3f bgColor = new Color3f(0.05f, 0.05f, 0.2f);

        Transform3D t;

        // Create the root of the branch graph
        BranchGroup objRoot = new BranchGroup();

        // Create a Transformgroup to scale all objects so they
        // appear in the scene.
        TransformGroup objScale = new TransformGroup();
        Transform3D t3d = new Transform3D();
        t3d.setScale(0.4);
        objScale.setTransform(t3d);
        objRoot.addChild(objScale);

        // Create a bounds for the background and lights
        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0),
                100.0);

        // Set up the background
        Background bg = new Background(bgColor);
        bg.setApplicationBounds(bounds);
        objScale.addChild(bg);

        Material m = new Material(objColor, eColor, objColor, sColor, 100.0f);
        Appearance a = new Appearance();
        m.setLightingEnable(true);
        a.setMaterial(m);
        Font3D f3d = new Font3D(new Font("TestFont", Font.PLAIN, 1),
                new FontExtrusion());

        Text3D pick = new Text3D(f3d, new String("Pick me"), new Point3f(-2.0f,
                -0.7f, 0.0f));
        pick.setCapability(Geometry.ALLOW_INTERSECT);
        pick.setString("Ololo");
        Shape3D s3D2 = new Shape3D();
        s3D2.setGeometry(pick);
        s3D2.setAppearance(a);

        // Create a transform group node and initialize it to the
        // identity. Enable the TRANSFORM_WRITE capability so that
        // our behavior code can modify it at runtime.
        TransformGroup spinTg2 = new TransformGroup();
        spinTg2.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        spinTg2.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        spinTg2.setCapability(TransformGroup.ENABLE_PICK_REPORTING);

        spinTg2.addChild(s3D2);
        objScale.addChild(spinTg2);


        // Create transformations for the positional lights
        t = new Transform3D();
        Vector3d lPos1 = new Vector3d(0.0, 0.0, 2.0);
        t.set(lPos1);
        TransformGroup l1Trans = new TransformGroup(t);
        l1Trans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        l1Trans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        l1Trans.setCapability(TransformGroup.ENABLE_PICK_REPORTING);
        objScale.addChild(l1Trans);

        t = new Transform3D();
        Vector3d lPos2 = new Vector3d(0.5, 1.2, 2.0);
        t.set(lPos2);
        TransformGroup l2Trans = new TransformGroup(t);
        l2Trans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        l2Trans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        l2Trans.setCapability(TransformGroup.ENABLE_PICK_REPORTING);
        objScale.addChild(l2Trans);

        // Create Geometry for point lights
        ColoringAttributes caL1 = new ColoringAttributes();
        //ColoringAttributes caL2 = new ColoringAttributes();
        caL1.setColor(lColor1);
        //caL2.setColor(lColor2);
        Appearance appL1 = new Appearance();
        //Appearance appL2 = new Appearance();
        appL1.setColoringAttributes(caL1);
        //appL2.setColoringAttributes(caL2);
        l1Trans.addChild(new Sphere(0.05f, Sphere.GENERATE_NORMALS
                | Sphere.ENABLE_GEOMETRY_PICKING, 15, appL1));
        //l2Trans.addChild(new Sphere(0.05f, Sphere.GENERATE_NORMALS
          //      | Sphere.ENABLE_GEOMETRY_PICKING, 15, appL2));

        // Create lights
        AmbientLight aLgt = new AmbientLight(alColor);

        Light lgt1;
        //Light lgt2;

        Point3f lPoint = new Point3f(0.0f, 0.0f, 0.0f);
        Point3f atten = new Point3f(1.0f, 0.0f, 0.0f);
        lgt1 = new PointLight(lColor1, lPoint, atten);
        //lgt2 = new PointLight(lColor2, lPoint, atten);

        // Set the influencing bounds
        aLgt.setInfluencingBounds(bounds);
        lgt1.setInfluencingBounds(bounds);
        //lgt2.setInfluencingBounds(bounds);

        // Add the lights into the scene graph
        objScale.addChild(aLgt);
        l1Trans.addChild(lgt1);
        //l2Trans.addChild(lgt2);


        // Let Java 3D perform optimizations on this scene graph.
        objRoot.compile();

        return objRoot;
    }
    public void init() {
        setLayout(new BorderLayout());
        GraphicsConfiguration config = SimpleUniverse
                .getPreferredConfiguration();
        Canvas3D c = new Canvas3D(config);
        add("Center", c);

        u = new SimpleUniverse(c);
        BranchGroup scene = createSceneGraph(c);

        // This will move the ViewPlatform back a bit so the
        // objects in the scene can be viewed.
        u.getViewingPlatform().setNominalViewingTransform();

        u.addBranchGraph(scene);
    }

    public static void main(String[] args) {
        JFrame frame = new MainFrameText();
        frame.setBounds(100, 100, 500, 500);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
