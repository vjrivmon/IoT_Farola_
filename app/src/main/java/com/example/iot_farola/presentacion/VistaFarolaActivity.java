package com.example.iot_farola.presentacion;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;


import com.example.iot_farola.Aplicacion;
import com.example.iot_farola.R;
import com.example.iot_farola.datos.RepositorioFarolas;
import com.example.iot_farola.modelo.Farola;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class VistaFarolaActivity extends AppCompatActivity {
    private static int RESQUEST_CODE =1234;

    private static final int TU_CODIGO_DE_SOLICITUD_DE_PERMISO = 15485;

    private RepositorioFarolas farolas;
    private Uri uriUltimaFoto;
    private int pos;
    private Farola farola;
    private ImageView foto;
    private ImageView borrar;

    ActivityResultLauncher<Intent> edicionLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        actualizaVistas();
                    }
                }
            });
    ActivityResultLauncher<Intent> tomarFotoLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode()==Activity.RESULT_OK && uriUltimaFoto!=null) {
                        farola.setFoto(uriUltimaFoto.toString());
                        ponerFoto(foto, farola.getFoto());
                    } else {
                        Toast.makeText(VistaFarolaActivity.this,
                                "Error en captura", Toast.LENGTH_LONG).show();
                    }
                }
            });

    ActivityResultLauncher<Intent> galeriaLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Uri uri = result.getData().getData();
                        getContentResolver().takePersistableUriPermission(uri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        farola.setFoto(uri.toString());
                        ponerFoto(foto, uri.toString());
                    } else {
                        Toast.makeText(VistaFarolaActivity.this,
                                "Foto no cargada", Toast.LENGTH_LONG).show();
                    }
                }
            });

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_farola);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);*/
        farolas = ((Aplicacion) getApplication()).farolas;
        Bundle extras = getIntent().getExtras();
        pos = extras.getInt("pos", 0);
        farola = farolas.elemento(pos);
        foto = findViewById(R.id.foto);
        borrar =findViewById(R.id.borrar);
        LineChart lineChart = findViewById(R.id.LineChart);
        setupLineChart(lineChart);
        borrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                borrarFoto(view);
            }
        });
    }
    /*public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("MainActivity", "onCreateOptionsMenu called");
        getMenuInflater().inflate(R.menu.vista_lugar, menu);
        return true;
    }
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_settings) {
            // Handle the settings action here.
            return true;
        } else if (itemId == R.id.acercaDe) {
            // Handle the "Acerca de" action by launching the AcercaDeActivity.
            lanzarAcercaDe();
            return true;
        } else if (itemId == R.id.accion_compartir) {
            compartir(lugar);
            return true;
        } else if (itemId == R.id.accion_llegar) {
            verMapa(lugar);
            return true;
        }else if (itemId == R.id.accion_borrar) {
            borrarLugar(pos);
            return true;
        }else if(itemId == R.id.accion_editar){
            editarLugar(pos,edicionLauncher);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    public void actualizaVistas() {
        TextView nombre = findViewById(R.id.nombre);
        ImageView logoTipo = findViewById(R.id.logo_tipo);
        TextView tipo = findViewById(R.id.tipo);
        TextView direccion = findViewById(R.id.direccion);
        TextView telefono = findViewById(R.id.telefono);
        RatingBar valoracion = findViewById(R.id.valoracion);
        ImageView foto = findViewById(R.id.foto);
        ponerFoto(foto, farola.getFoto());

        nombre.setText(farola.getNombre());
        direccion.setText(farola.getDireccion());
        telefono.setText(Integer.toString(farola.getTelefono()));
    }
    /*void editarLugar(int pos,ActivityResultLauncher<Intent> launcher) {
        Intent i = new Intent(this, EdicionLugarActivity.class);
        i.putExtra("pos", pos);
        launcher.launch(i);
        //startActivity(i);
        Toast.makeText(this, "Editar: " +
                        ((Aplicacion) getApplication()).lugares.elemento(pos).getNombre(),
                Toast.LENGTH_SHORT).show();
    }
    public void borrarLugar(int id) {
        new AlertDialog.Builder(this)
                .setTitle("Borrado de lugar")
                .setMessage("¿Estás seguro que quieres eliminar este lugar?")
                .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        lugares.borrar(id);
                        finish();
                    }
                })
                .setNegativeButton("NO", null)
                .show();
    }*/
    /*public void compartir(Farola farola) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT, farola.getNombre() + " - " + lugar.getUrl());
        startActivity(i);
    }*/

    public void llamarTelefono(Farola farola) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + farola.getTelefono())));
        } else {
            // Si no tienes permiso, solicita permiso antes de realizar la llamada
            //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, TU_CODIGO_DE_SOLICITUD_DE_PERMISO);
        }
    }


    private void lanzarAcercaDe() {
        Intent i = new Intent(this, AcercaDeActivity.class);
        //mp.pause();
        startActivity(i);
    }


    public void llamarTelefono(View view) {
        llamarTelefono(farola);
    }

    public void fotoDeGaleria(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        galeriaLauncher.launch(intent);
    }
    protected void ponerFoto(ImageView imageView, String uri) {
        if (uri != null && !uri.isEmpty() && !uri.equals("null")) {
            imageView.setImageURI(Uri.parse(uri));
        } else {
            imageView.setImageBitmap(null);
        }
    }
    public void tomarFoto(View view) {
        try {
            File file = File.createTempFile(
                    "img_" + (System.currentTimeMillis()/ 1000), ".jpg" ,
                    getExternalFilesDir(Environment.DIRECTORY_PICTURES));
            if (Build.VERSION.SDK_INT >= 24) {
                uriUltimaFoto = FileProvider.getUriForFile(
                        this, "es.upv.jtomas.mislugares.fileProvider", file);
            } else {
                uriUltimaFoto = Uri.fromFile(file);
            }
            Intent intent   = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra (MediaStore.EXTRA_OUTPUT, uriUltimaFoto);
            tomarFotoLauncher.launch(intent);
        } catch (IOException ex) {
            Toast.makeText(this, "Error al crear fichero de imagen",
                    Toast.LENGTH_LONG).show();
        }
    }
    public void eliminarFoto(View view) {
        farola.setFoto("");
        ponerFoto(foto, "");
    }
    @Override
    public void onResume() {
        super.onResume();
        actualizaVistas();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==RESQUEST_CODE && resultCode==RESULT_OK) {
            actualizaVistas();
        }
    }
    public void borrarFoto(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Borrar Foto")
                .setMessage("¿Estás seguro que quieres eliminar esta foto?")
                .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        farola.setFoto(""); // Borra la referencia a la foto en el objeto Lugar
                        ponerFoto(foto, ""); // Limpia la vista de la foto
                    }
                })
                .setNegativeButton("NO", null)
                .show();
    }
    private void setupLineChart(LineChart lineChart) {
        // Configuración del gráfico
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.getDescription().setEnabled(false);

        // Agregar datos al gráfico de líneas (puedes llamar a tu método addDataToLineChart aquí)
        addDataToLineChart(lineChart);
    }
    private void addDataToLineChart(LineChart lineChart) {
        ArrayList<Entry> entries = new ArrayList<>();

        // Agregar datos de ejemplo (puedes reemplazarlos con tus propios datos)
        entries.add(new Entry(1f, 10f));
        entries.add(new Entry(2f, 25f));
        entries.add(new Entry(3f, 15f));
        entries.add(new Entry(4f, 32f));
        entries.add(new Entry(5f, 18f));

        LineDataSet dataSet = new LineDataSet(entries, "Datos de Ejemplo");
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);

        LineData lineData = new LineData(dataSets);
        lineChart.setData(lineData);
    }

}
