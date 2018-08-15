package com.nestnetgroup.mivotocolombia;


import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import java.util.List;



public class MainActivity extends AppCompatActivity {

    private final int barcode_req_code=200;
    Button registro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registro= findViewById(R.id.buttonuno);
        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Codigo_Barra();
            }
        });

    }

    private void Codigo_Barra(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,barcode_req_code);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==barcode_req_code){

            if(resultCode==RESULT_OK){
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                barcodeRecognition(photo);
            }

        }
    }

    private void barcodeRecognition(Bitmap photo){
        FirebaseVisionBarcodeDetectorOptions options = new FirebaseVisionBarcodeDetectorOptions.Builder().setBarcodeFormats(FirebaseVisionBarcode.FORMAT_PDF417).build();
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(photo);
        FirebaseVisionBarcodeDetector detector = FirebaseVision.getInstance().getVisionBarcodeDetector(options);
        Task<List<FirebaseVisionBarcode>> result = detector.detectInImage(image)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionBarcode> barcodes) {
                        for (FirebaseVisionBarcode barcode: barcodes) {
                            String rawValue = barcode.getRawValue();
                            Log.e("REPUESTA : "," REPUESTA F: "+rawValue);
                            Toast.makeText(MainActivity.this,rawValue,Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this,"Problemas con la lectura de la cedula",Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
