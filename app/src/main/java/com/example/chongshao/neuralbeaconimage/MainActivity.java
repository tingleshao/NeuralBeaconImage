package com.example.chongshao.neuralbeaconimage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

public class MainActivity extends AppCompatActivity {

    Button selectImageButton;
    Button enhanceButton1;
    Button enhanceButton2;
    Button enhanceButton3;

    int[] intValues;
    float[] floatValues;
    int desiredSize = 256;


    TensorFlowInferenceInterface inferenceInterface;
    private static final String MODEL_FILE = "file:///android_asset/stylize_quantized.pb";
    private static final String INPUT_NODE = "input";
    private static final String STYLE_NODE = "style_num";
    private static final String OUTPUT_NODE = "transformer/expand/conv3/conv/Sigmoid";
    private static final int NUM_STYLES = 26;

    private final float[] styleVals = new float[NUM_STYLES];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // buttons
        // TODO: click this button, load the image
        selectImageButton = (Button)this.findViewById(R.id.button);

        // TODO: click button1, show the style transferred image
        enhanceButton1 = (Button)this.findViewById(R.id.button2);
        enhanceButton2 = (Button)this.findViewById(R.id.button3);
        enhanceButton3 = (Button)this.findViewById(R.id.button4);

        // TODO: we need to do fast marker style transfer model

        intValues = new int[desiredSize * desiredSize];
        floatValues = new float[desiredSize * desiredSize *3];

        // TODO: try the example style transfer
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
                inferenceInterface.feed(STYLE_NODE, styleVals, NUM_STYLES);

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

    public boolean isDebug() {
        return true;
    }

}
