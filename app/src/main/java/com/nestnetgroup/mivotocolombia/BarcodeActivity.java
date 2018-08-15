package com.nestnetgroup.mivotocolombia;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class BarcodeActivity extends AppCompatActivity {


    Button scan_barcode,registrar;
    EditText ecc,eapellidos,enombres,efn,egs,egenero;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);
        scan_barcode = findViewById(R.id.scan_barcode);
        registrar = findViewById(R.id.registrar);
        ecc = findViewById(R.id.cc);
        eapellidos = findViewById(R.id.apellidos);
        enombres = findViewById(R.id.nombres);
        efn = findViewById(R.id.fn);
        egs = findViewById(R.id.gs);
        egenero = findViewById(R.id.genero);
    }


    public void scanQRCode(View v) {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.PDF_417);
        integrator.setPrompt("Scan a barcode");
        integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (result != null) {
            String contents = result.getContents();
            if (contents != null) {
                String resul= result.toString();
                String cedula = resul.substring(74,84).trim();
                ecc.setText(cedula);
                String apellidouno = resul.substring(84,107).trim();
                String apellidodos = resul.substring(107,130).trim();
                String nombreuno = resul.substring(130,153).trim();
                String nombredos = resul.substring(153,176).trim();
                String ano=resul.substring(178,182).trim();
                String mes=resul.substring(182,184).trim();
                String dia=resul.substring(184,186).trim();
                String sangre = resul.substring(192,194).trim();
                String sex = resul.substring(177,178).trim();
                eapellidos.setText(apellidouno+" "+apellidodos);
                enombres.setText(nombreuno+" "+nombredos);
                efn.setText(ano+"-"+mes+"-"+dia);
                egs.setText(sangre);
                egenero.setText(sex);
            } else {
                Toast.makeText(getApplicationContext(),getString(R.string.result_failed_why),Toast.LENGTH_SHORT).show();
            }
        }
    }



}
