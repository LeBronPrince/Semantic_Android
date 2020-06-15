package com.nex3z.tflitemnist;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nex3z.fingerpaintview.FingerPaintView;

import java.io.FileNotFoundException;
import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private ImageView imageView;
    private ImageView labelView;
    private Classifier mClassifier;
    private Button btn_select;
    private Button btn_run;
    private Bitmap labelBitmap=null;
    private Bitmap selectImageBitmap=null;
    public final int Height = 769;
    public final int Width = 769;
    private final int[] virtualLabel = new int[Height*Width];
    private int[][] resultArray = new int[Height][Width];
    public final int[][] labelColor = {{128, 64, 128}, {232, 35, 244}, {70, 70, 70}, {156, 102, 102}, {153, 153, 190},
            {30, 170, 250}, {0, 220, 220}, {35, 142, 107}, {152, 251, 152}, {180, 130, 70}, {60, 20, 220}, {0, 0, 255},
            {142, 0, 0}, {70, 0, 0}, {100, 60, 0}, {100, 80, 0}, {230, 0, 0}, {32, 11, 119}, {0, 0, 0}};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
    }


//    @OnClick(R.id.btn_select)
//    void onSelectClick() {
//        selectPic();
//    }


    private void selectPic(){
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    private void decodeBitmapImage(int[][] img){
        int[] imgPixel = new int[Width*Height];
        for (int i=0; i<Width; i++){
            for (int j=0; j<Height; j++){
                imgPixel[i*Width+j] = Color.argb(255, labelColor[img[i][j]][0], labelColor[img[i][j]][1], labelColor[img[i][j]][2]);
            }
        }
        labelBitmap = Bitmap.createBitmap(imgPixel, Width, Height, Bitmap.Config.ARGB_8888);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK){
            Uri uri = data.getData();
            Log.e("uri", uri.toString());
            ContentResolver cr = this.getContentResolver();
            try {
                selectImageBitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                imageView.setImageBitmap(selectImageBitmap);
            } catch (FileNotFoundException e) {
                Log.e("Exception", e.getMessage(),e);
            }
        }else{
            Log.i("MainActivtiy", "operation error");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void init() {
        try {
            imageView = findViewById(R.id.imageview);
            btn_select = findViewById(R.id.btn_select);
            labelView = findViewById(R.id.labelview);
            btn_run = findViewById(R.id.btn_run);
            btn_select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectPic();
                }
            });

            mClassifier = new Classifier(this);

            btn_run.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    decodeBitmapImage(virtualLabel);
//                    labelView.setImageBitmap(labelBitmap);
                    if (mClassifier == null) {
                        Log.e(LOG_TAG, "onDetectClick(): Classifier is not initialized");
                        return;
                    }
                    resultArray = mClassifier.classify(selectImageBitmap);
//                    renderResult(resultArray);
                    decodeBitmapImage(resultArray);
                    labelView.setImageBitmap(labelBitmap);
                }
            });

        } catch (IOException e) {
            Toast.makeText(this, R.string.failed_to_create_classifier, Toast.LENGTH_LONG).show();
            Log.e(LOG_TAG, "init(): Failed to create Classifier", e);
        }
    }

}
