package com.huawei.hms.modeling3d.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.huawei.hms.modeling3d.R;

import org.rajawali3d.Object3D;
import org.rajawali3d.loader.LoaderOBJ;
import org.rajawali3d.math.Quaternion;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.renderer.Renderer;
import org.rajawali3d.view.SurfaceView;

import java.io.File;

public class RenderActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {

    public static final String EXTRA_FILE_PATH = "FILEPATH";
    private ObjRenderer renderer;
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_render);

        SurfaceView view = findViewById(R.id.surface);

        File objFile = findObjFile(getIntent().getExtras().getString(EXTRA_FILE_PATH));
        renderer = new ObjRenderer(this, objFile);
        view.setSurfaceRenderer(renderer);

        gestureDetector = new GestureDetector(this, this);
    }

    private File findObjFile(String path) {
        File[] folderFiles = new File(path).listFiles();

        for (File file : folderFiles) {
            if (file.isFile()) {
                String ext = file.getName().substring(file.getName().lastIndexOf(".") + 1);
                if (ext.equalsIgnoreCase("obj")) {
                    return file;
                }
            }
        }

        return null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return true;
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float distanceX, float distanceY) {
        renderer.setScroll(distanceX, distanceY);
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    class ObjRenderer extends Renderer {
        private final File objFile;
        private Object3D object3D;

        private boolean isScrolling = false;
        private float xDistance = 0, yDistance = 0;

        public ObjRenderer(Context context, File objFile) {
            super(context);
            this.objFile = objFile;
        }

        @Override
        protected void onRender(long elapsedRealtime, double deltaTime) {
            if (isScrolling) {
                Vector3 xAxis = renderer.getCurrentCamera().getOrientation().getXAxis();
                Vector3 yAxis = renderer.getCurrentCamera().getOrientation().getYAxis();
                Vector3 zAxis = renderer.getCurrentCamera().getOrientation().getZAxis();

                //Method to rotate the 3D model
                Quaternion x = new Quaternion(xAxis, -xDistance / 2);
                Quaternion y = new Quaternion(zAxis, -yDistance / 2);
                object3D.rotate(x.multiply(y));
                this.isScrolling = false;
            }
            super.onRender(elapsedRealtime, deltaTime);
        }

        @Override
        protected void initScene() {
            try {
                getCurrentScene().setBackgroundColor(Color.CYAN & Color.DKGRAY);

                LoaderOBJ loader = new LoaderOBJ(this, this.objFile);
                loader.parse();
                object3D = loader.getParsedObject();
                getCurrentScene().addChild(object3D);
                object3D.setPosition(0,0,-2);

                getCurrentCamera().setPosition(0, 0, 0);
                getCurrentCamera().setLookAt(object3D.getPosition());
            } catch (Exception e) {
                Log.d(getLocalClassName(), e.getMessage());
            }
        }

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {

        }

        @Override
        public void onTouchEvent(MotionEvent event) {
        }

        public void setScroll(float xDistance, float yDistance) {
            this.xDistance = xDistance;
            this.yDistance = yDistance;
            this.isScrolling = true;
        }
    }
}