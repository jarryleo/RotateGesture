package cn.leo.rotategesture;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageView = findViewById(R.id.imageView);
        RotateGestureDetector detector = new RotateGestureDetector(getWindow().getDecorView());
        detector.setCycle(false)
                .setStartAngle(60)
                .setEndAngle(300)
                .setOnRotateListener(new RotateGestureDetector.OnRotateListener() {
                    @Override
                    public void onRotate(int angle, int pivotX, int pivotY) {
                        mImageView.setRotation(angle);
                    }
                });
    }
}
