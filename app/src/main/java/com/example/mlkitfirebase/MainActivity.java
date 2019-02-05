package com.example.mlkitfirebase;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button snap;
    private Button detect;
    private TextView showtext;
    private ImageView images;
    private Bitmap imageBitmap;
    private FirebaseVisionTextRecognizer detector;
    private Task<FirebaseVisionText> result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        images = findViewById(R.id.imageview);
        showtext = findViewById(R.id.textView2);
        detect = findViewById(R.id.button2);
        snap = findViewById(R.id.button);

        //Snap on click listner
        snap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        //Detect on Click Listner
        detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDetect();
            }
        });
    }

    private void onDetect() {
        Toast.makeText(MainActivity.this, "onDetect working", Toast.LENGTH_SHORT).show();
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(imageBitmap);
        detector = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();
        processImage(image);
    }

    private void processImage(FirebaseVisionImage image) {
     result =
                detector.processImage(image)
                        .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                                Toast.makeText(MainActivity.this,"listner working", Toast.LENGTH_SHORT).show();
                                textProcessing(firebaseVisionText);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this,"listner failed", Toast.LENGTH_SHORT).show();
                            }
                        });

    }

    private void textProcessing(FirebaseVisionText firebaseVisionText) {
        List<FirebaseVisionText.TextBlock> blocks=firebaseVisionText.getTextBlocks();
        if(blocks.size()==0){
            Toast.makeText(MainActivity.this,"No text found", Toast.LENGTH_SHORT).show();
            return;
        }
        for(FirebaseVisionText.TextBlock block:firebaseVisionText.getTextBlocks()){
            Toast.makeText(MainActivity.this,"No text found", Toast.LENGTH_SHORT).show();
            String text=block.getText();
            showtext.setText(text);
        }
    }


    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            images.setImageBitmap(imageBitmap);
        }
    }
}
