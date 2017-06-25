package com.example.chongshao.neuralbeaconimage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

public class MainActivity extends AppCompatActivity {

    Button selectImageButton;
    Button enhanceButton1;
    Button enhanceButton2;
    Button enhanceButton3;

    ImageView inputImageView;
    ImageView enhance1ImageView;
    ImageView enhance2ImageView;
    ImageView enhance3ImageView;

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

        // ImageViews
        inputImageView = (ImageView)this.findViewById(R.id.inputImageView);
        enhance1ImageView = (ImageView)this.findViewById(R.id.enhance1ImageView);

        inferenceInterface = new TensorFlowInferenceInterface(getAssets(), MODEL_FILE);

        // TODO: we need to do fast marker style transfer model
        intValues = new int[desiredSize * desiredSize];
        floatValues = new float[desiredSize * desiredSize *3];

        // buttons
        selectImageButton = (Button)this.findViewById(R.id.button);
        enhanceButton1 = (Button)this.findViewById(R.id.button2);
        enhanceButton2 = (Button)this.findViewById(R.id.button3);
        enhanceButton3 = (Button)this.findViewById(R.id.button4);

        enhanceButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inScaled = false;
                Bitmap inputImage = BitmapFactory.decodeResource(getResources(), R.drawable.tubingen_resize, options);
                Bitmap outputImage = Bitmap.createBitmap(inputImage);
                Log.d("DDL", "image width: " + Integer.toString(inputImage.getWidth()));
                inputImageView.setImageBitmap(inputImage);

                // send the pixels to intValues
                inputImage.getPixels(intValues, 0, inputImage.getWidth(), 0, 0, inputImage.getWidth(), inputImage.getHeight());

                // process the pixel values, send to floatValues
                for (int i = 0; i < intValues.length; ++i) {
                    final int val = intValues[i];
                    floatValues[i * 3] = ((val >> 16) & 0xFF) / 255.0f;
                    floatValues[i * 3 + 1] = ((val >> 8) & 0xFF) / 255.0f;
                    floatValues[i * 3 + 2] = (val & 0xFF) / 255.0f;
                }

                // fered the
                inferenceInterface.feed(INPUT_NODE, floatValues, 1, inputImage.getWidth(), inputImage.getHeight(), 3);
                inferenceInterface.feed(STYLE_NODE, styleVals, NUM_STYLES);

                inferenceInterface.run(new String[]{OUTPUT_NODE}, isDebug());;
                inferenceInterface.fetch(OUTPUT_NODE, floatValues);

                // convert float values back to int values 
                for (int i = 0; i < intValues.length; ++i) {
                    intValues[i] =
                            0xFF000000
                                    | (((int) (floatValues[i * 3] * 255)) << 16)
                                    | (((int) (floatValues[i * 3 + 1] * 255)) << 8)
                                    | ((int) (floatValues[i * 3 + 2] * 255));
                }

                outputImage.setPixels(intValues, 0, outputImage.getWidth(), 0, 0, outputImage.getWidth(), outputImage.getHeight());
                enhance1ImageView.setImageBitmap(outputImage);
            }
        });
    }

    public boolean isDebug() {
        return true;
    }
}