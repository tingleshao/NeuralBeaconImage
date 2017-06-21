package com.example.chongshao.neuralbeaconimage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button selectImageButton;
    Button enhanceButton1;
    Button enhanceButton2;
    Button enhanceButton3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // buttons
        selectImageButton = (Button)this.findViewById(R.id.button);
        enhanceButton1 = (Button)this.findViewById(R.id.button2);
        enhanceButton2 = (Button)this.findViewById(R.id.button3);
        enhanceButton3 = (Button)this.findViewById(R.id.button4);

        // TODO: click the button will run a style transfer model
        // TODO: before that we need to do fast marker style transfer model

        enhanceButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap inputImage = BitmapFactory.decodeResource(getResources(), R.drawable.tubingen);
                
            }
        });
    }
}
