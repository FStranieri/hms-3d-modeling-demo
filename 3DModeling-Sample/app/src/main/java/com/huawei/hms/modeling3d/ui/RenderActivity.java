package com.huawei.hms.modeling3d.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.huawei.hms.modeling3d.R;

import org.rajawali3d.Object3D;
import org.rajawali3d.lights.DirectionalLight;
import org.rajawali3d.loader.LoaderOBJ;
import org.rajawali3d.renderer.Renderer;
import org.rajawali3d.view.SurfaceView;

import java.io.File;

public class RenderActivity extends AppCompatActivity {

    public static final String EXTRA_FILE_PATH = "FILEPATH";
    private ObjRenderer renderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_render);

        SurfaceView view = findViewById(R.id.surface);

        File objFile = findObjFile(getIntent().getExtras().getString(EXTRA_FILE_PATH));
        renderer = new ObjRenderer(this, objFile);

        view.setSurfaceRenderer(renderer);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                renderer.onTouchEvent(motionEvent);
                return false;
            }
        });
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

    class ObjRenderer extends Renderer {

        private final File objFile;

        public ObjRenderer(Context context, File objFile) {
            super(context);
            this.objFile = objFile;
        }

        @Override
        protected void initScene() {
            try {
                getCurrentScene().setBackgroundColor(Color.CYAN & Color.DKGRAY);
                DirectionalLight key = new DirectionalLight(-4,-4,-4);
                key.setPower(3);
                getCurrentScene().addLight(key);

                LoaderOBJ loader = new LoaderOBJ(this, this.objFile);
                loader.parse();
                Object3D obj = loader.getParsedObject();
                getCurrentScene().addChild(obj);

                getCurrentCamera().setPosition(3,4,-5);
                getCurrentCamera().setLookAt(obj.getPosition());
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
    }
}