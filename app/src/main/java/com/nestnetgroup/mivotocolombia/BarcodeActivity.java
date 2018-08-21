package com.nestnetgroup.mivotocolombia;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class BarcodeActivity extends AppCompatActivity {

    String HttpUrl = "http://monitorguardian.com/mivotocolombia/registro.php";
    RequestQueue requestQueue;
    ProgressDialog progressDialog;
    String editemailholder, editpassholder;
    Boolean CheckEditText;
    Button scan_barcode,registrar;
    EditText ecc,eapellidos,enombres,efn,egs,egenero,email,password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);

        requestQueue = Volley.newRequestQueue(BarcodeActivity.this);
        progressDialog = new ProgressDialog(BarcodeActivity.this);

        scan_barcode = findViewById(R.id.scan_barcode);
        registrar = findViewById(R.id.registrar);
        ecc = findViewById(R.id.cc);
        eapellidos = findViewById(R.id.apellidos);
        enombres = findViewById(R.id.nombres);
        efn = findViewById(R.id.fn);
        egs = findViewById(R.id.gs);
        egenero = findViewById(R.id.genero);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);


        registrar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                CheckEditTextIsEmptyOrNot();
                if (CheckEditText) {
                    Registarusuario();
                } else {
                    Toast.makeText(getApplicationContext(), "Por favor complete todos los campos del formulario.", Toast.LENGTH_LONG).show();
                }
            }
        });

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

    public void CheckEditTextIsEmptyOrNot() {
        editemailholder = email.getText().toString().trim();
        editpassholder = password.getText().toString().trim();
        CheckEditText = !TextUtils.isEmpty(editemailholder) && !TextUtils.isEmpty(editpassholder);
    }

    public  String codificar(String text) throws UnsupportedEncodingException {
        byte[] data = text.getBytes("UTF-8");
        String base64 = Base64.encodeToString(data, Base64.DEFAULT);
        return base64;
    }

    public void Registarusuario() {
        progressDialog.setMessage("Porfavor spere....");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, HttpUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {
                        progressDialog.dismiss();
                        if (ServerResponse.contains("exitoso")) {
                            Toast.makeText(getApplicationContext(), "Registro exitoso", Toast.LENGTH_LONG).show();
                            finish();
                            Intent intent = new Intent(BarcodeActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(BarcodeActivity.this, ServerResponse, Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Problemas de conexión o Usuario ya registrado", Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("cedula", ecc.getText().toString());
                params.put("apellidos", eapellidos.getText().toString());
                params.put("nombres", enombres.getText().toString());
                params.put("fecha_nacimiento", efn.getText().toString());
                params.put("sangre", egs.getText().toString());
                params.put("genero", egenero.getText().toString());

                try {
                    String emailcode= codificar(editemailholder);
                    String passwordcode= codificar(editpassholder);
                    params.put("email", emailcode);
                    params.put("password", passwordcode);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(BarcodeActivity.this);
        requestQueue.add(stringRequest);
    }



}
