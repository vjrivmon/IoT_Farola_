package com.example.iot_farola;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Tab4 extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);}

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab4, container, false);
        //super.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        TextView nombre = v.findViewById(R.id.nombre);
        nombre.setText(usuario.getDisplayName());

        TextView correo = v.findViewById(R.id.correo);
        correo.setText(usuario.getEmail());

        TextView proveedor = v.findViewById(R.id.proveedor);
        proveedor.setText(usuario.getProviderId());

        TextView telf = v.findViewById(R.id.telefono);
        telf.setText(usuario.getPhoneNumber());

        TextView uid = v.findViewById(R.id.uid);
        uid.setText(usuario.getUid());


        RequestQueue colaPeticiones = Volley.newRequestQueue(requireActivity());
        ImageLoader lectorImagenes = new ImageLoader(colaPeticiones,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap> cache =
                            new LruCache<String, Bitmap>(10);
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }
                });
// Foto de usuario
        Uri urlImagen = usuario.getPhotoUrl();
        if (urlImagen != null) {
            NetworkImageView foto = (NetworkImageView) v.findViewById(R.id.imagen);
            foto.setImageUrl(urlImagen.toString(), lectorImagenes);
        }
        return v;

    }
}
