package com.giogen.agendabig.Fragments;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.giogen.agendabig.R;
import com.giogen.agendabig.ObjetosYDaos.Archivo;

public class FragmentDetalle extends Fragment {
    private Archivo archivo;
    //WIDGETS PARA AGREGAR LA IMAGEN
    private ImageView imageView;

    //Metodo en el cual se infla nuestro fragmento en el xml de fragment_detalle, con este
    //fragment se obtiene la visualizacion de nuestra imagen.

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View vista=inflater.inflate(R.layout.fragment_detalle,container,false);
        imageView=(ImageView)vista.findViewById(R.id.imgvwImagen);
        Uri uri=Uri.parse(archivo.getRuta());
        imageView.setImageURI(uri);
        return vista;
    }

    public void setArchivo(Archivo archivo) {

        this.archivo = archivo;
    }
}