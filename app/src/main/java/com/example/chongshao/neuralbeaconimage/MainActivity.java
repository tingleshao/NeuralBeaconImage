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

    int[] intValues;
    float[] floatValues;
    int desiredSize = 256;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // buttons
        selectImageButton = (Button)this.findViewById(R.id.button);
        enhanceButton1 = (Button)this.findViewById(R.id.button2);
        enhanceButton2 = (Button)this.findViewById(R.id.button3);
        enhanceButton3 = (Button)this.findViewById(R.id.button4);

        // TODO: we need to do fast marker style transfer model

        intValues = new int[desiredSize * desiredSize];
        floatValues = new float[desiredSize * desiredSize *3];

        TensorFlowInferenceInterface inferenceInterface;

        enhanceButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: this image size should be 256 * 256?
                Bitmap inputImage = BitmapFactory.decodeResource(getResources(), R.drawable.tubingen);
                Bitmap outputImage = Bitmap.createBitmap(inputImage);
                inputImage.getPixels(intValues, 0, inputImage.getWidth(), 0, 0, inputImage.getWidth(), inputImage.getHeight());

                for (int i = 0; i < intValues.length; ++i) {
                    final int val = intValues[i];
                    floatValues[i * 3] = ((val >> 16) & 0xFF) / 255.0f;
                    floatValues[i * 3 + 1] = ((val >> 8) & 0xFF) / 255.0f;
                    floatValues[i * 3 + 2] = (val & 0xFF) / 255.0f;
                }
                inferenceInterface.feed(INPUT_NODE, floatValues, 1, inputImage.getWidth(), inputImage.getHeight(), 3);
                inferenceInterface.feed(STYLE_NODE, styleValues, NUM_STYLES);

                inferenceInterface.run(new String[]{OUTPUT_NODE}, isDebug());;
                inferenceInterface.fetch(OUTPUT_NODE, floatValues);



                for (int i = 0; i < intValues.length; ++i) {
                    intValues[i] =
                            0xFF000000
                                    | (((int) (floatValues[i * 3] * 255)) << 16)
                                    | (((int) (floatValues[i * 3 + 1] * 255)) << 8)
                                    | ((int) (floatValues[i * 3 + 2] * 255));
                }

                outputImage.setPixels(intValues, 0, outputImage.getWidth(), 0, 0, outputImage.getWidth(), outputImage.getHeight());
            }
        });
    }
}
