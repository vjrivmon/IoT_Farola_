package com.example.iot_farola.presentacion;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.example.iot_farola.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Tab4 extends Fragment {
    Button btnAnonimo;

    @Override
    public void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);}

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.edicion_cuenta, container, false);
        Toolbar toolbar = v.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        RequestQueue colaPeticiones = Volley.newRequestQueue(requireActivity());
        ImageLoader lectorImagenes = new ImageLoader(colaPeticiones,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap> cache =
                            new LruCache<String, Bitmap>(10);
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                    public Bitmap getBitmap(String url) {
                        Bitmap output = cache.get(url);

                        if (output == null) {
                            // La imagen no está en la caché, no podemos aplicar el recorte circular.
                            return null;
                        }

                        int width = output.getWidth();
                        int height = output.getHeight();
                        int diameter = Math.min(width, height);
                        Bitmap circularBitmap = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888);

                        Canvas canvas = new Canvas(circularBitmap);
                        final int color = 0xff424242;
                        final Paint paint = new Paint();
                        final Rect rect = new Rect(0, 0, diameter, diameter);
                        final RectF rectF = new RectF(rect);
                        final float roundPx = diameter / 2;

                        paint.setAntiAlias(true);
                        canvas.drawARGB(0, 0, 0, 0);
                        paint.setColor(color);
                        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

                        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                        canvas.drawBitmap(output, rect, rect, paint);

                        return circularBitmap;
                    }
                });
        Uri urlImagen = usuario.getPhotoUrl();
        if (urlImagen != null) {
            NetworkImageView foto = (NetworkImageView) v.findViewById(R.id.imagen5);
            foto.setImageUrl(urlImagen.toString(), lectorImagenes);
        }


        btnAnonimo = v.findViewById(R.id.UnificarCuenta);
        EditText nombre = v.findViewById(R.id.nombre);
        nombre.setText(usuario.getDisplayName());

        if(usuario.isAnonymous()){
            nombre.setText("Invitado/a");
        }

        EditText correo = v.findViewById(R.id.correoE);
        correo.setText(usuario.getEmail());

        /*TextView proveedor = v.findViewById(R.id.proveedor);
        proveedor.setText(usuario.getProviderId());*/

        EditText telf = v.findViewById(R.id.telefono);
        EditText nusu = v.findViewById(R.id.usuario);
        EditText postal = v.findViewById(R.id.postal);
        EditText contr = v.findViewById(R.id.contrasenya);
        telf.setText(usuario.getPhoneNumber());

        TextView uid = v.findViewById(R.id.Uid);
        uid.setText(usuario.getUid());
        Button button = v.findViewById(R.id.btn_cerrar_sesion1);
        Button editar = v.findViewById(R.id.toggleButton);
        Button guardar = v.findViewById(R.id.Guardar);



        editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Call the cerrarSesion method when the button is clicked
               if(nombre.isEnabled()){
                   nombre.setEnabled(false);
                   correo.setEnabled(false);
                   telf.setEnabled(false);
                   nusu.setEnabled(false);
                   postal.setEnabled(false);
                   contr.setEnabled(false);
               }else{
                   nombre.setEnabled(true);
                   correo.setEnabled(true);
                   telf.setEnabled(true);
                   nusu.setEnabled(true);
                   postal.setEnabled(true);
                   contr.setEnabled(true);
               }
            }
        });


        // Set an OnClickListener for the button
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Call the cerrarSesion method when the button is clicked
                cerrarSesion(view);
            }
        });

        btnAnonimo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), Registro.class);
                i.putExtra("unificar",true);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                | Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                getActivity().finish();
            }
        });

        if (usuario != null && usuario.isAnonymous()) {
            // El usuario actual es anónimo, por lo que muestras el botón
            button.setVisibility(View.GONE);
            guardar.setVisibility(View.GONE);
            editar.setVisibility(View.GONE);
            btnAnonimo.setVisibility(View.VISIBLE);
        } else {
            // El usuario no es anónimo, ocultas el botón
            button.setVisibility(View.VISIBLE);
            btnAnonimo.setVisibility(View.GONE);
            guardar.setVisibility(View.VISIBLE);
            editar.setVisibility(View.VISIBLE);
        }
        return v;

    }
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            // Manejar la selección del elemento de menú "action_settings".
            return true;
        }
        if (id == R.id.acercaDe){
            lanzarAcercaDe();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void lanzarAcercaDe() {
        Intent i = new Intent(requireActivity(), AcercaDeActivity.class);
        //mp.pause();
        startActivity(i);
    }

    public void cerrarSesion(View view) {
        AuthUI.getInstance().signOut(requireContext())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent i = new Intent(requireActivity(), LoginActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        requireActivity().finish();
                    }
                });
    }

}