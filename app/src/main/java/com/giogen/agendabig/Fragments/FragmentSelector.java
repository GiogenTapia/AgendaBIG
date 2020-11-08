package com.giogen.agendabig.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.giogen.agendabig.ObjetosYDaos.Archivo;
import com.giogen.agendabig.ObjetosYDaos.ArchivoAdapter;
import com.giogen.agendabig.mostrar;
import com.giogen.agendabig.R;
import java.util.ArrayList;

public class FragmentSelector extends Fragment {
    private RecyclerView recyclerView;
    private ArrayList<Archivo>lista=new ArrayList<>();
    private Activity activity;
    private ArchivoAdapter adapter;

    public void setLista(ArrayList<Archivo> lista) {
        this.lista = lista;
    }
    public void setAdapter(ArchivoAdapter adapter) {
        this.adapter = adapter;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof Activity){
            this.activity=(Activity)context;
        }
    }
    //Creacion de nuestro fragment selectos, el cual nos ayudara como contenedor
    //de los diferentes archivos que tengamos en nuestras fichas
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View vista=inflater.inflate(R.layout.fragment_selector,container,false);
        recyclerView=(RecyclerView)vista.findViewById(R.id.rcclrArchivoFragment);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(activity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        //El metodo de click largo nos ayudara a poder obtener el metodo de mostrar detalle de nuestra clase mostrar
        //y se obtiene el objeto al cual se le esta haciendo click
        adapter.setOnItemLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int no=recyclerView.getChildAdapterPosition(v);
                Archivo ar=lista.get(no);

                ((mostrar)activity).mostrarDetalle(ar);
                return true;
            }
        });
        return vista;
    }
}