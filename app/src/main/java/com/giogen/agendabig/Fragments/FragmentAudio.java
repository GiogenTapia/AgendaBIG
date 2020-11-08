package com.giogen.agendabig.Fragments;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.giogen.agendabig.ObjetosYDaos.Archivo;
import com.giogen.agendabig.R;

import java.io.IOException;

public class FragmentAudio extends Fragment {
    private Archivo archivo;
    //WIDGETS PARA REPRODUCIR MULTIMEDIA
    private Button btnReproducir;
    private Button btnPausar;
    private MediaPlayer mediaPlayer=new MediaPlayer();
    // En esta parte se crea nuestro fragment que servira para obtener la multimedia de audio
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View vista=inflater.inflate(R.layout.fragment_audio,container,false);
        btnReproducir=(Button)vista.findViewById(R.id.btnReproducirAudio);
        btnPausar=(Button)vista.findViewById(R.id.btnPausarAudio);
        //Obtenemos la ruta del archivo que se reproducira
        Uri uri=Uri.parse(archivo.getRuta());
        try {
            mediaPlayer.setDataSource(getContext(),uri);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Metodo de clic para poder pausar nuestra multimedia
        btnPausar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            mediaPlayer.pause();
            }
        });
        //Metodo de clic para comenzar a reproducir nuestra multimedia
        btnReproducir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mediaPlayer=new MediaPlayer();
                mediaPlayer.start();
            }
        });
        return vista;
    }

    public void setArchivo(Archivo archivo) {
        this.archivo = archivo;
    }
}
