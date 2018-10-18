package com.example.lusog.monkeyscompra;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ListaCompra extends AppCompatActivity {

    public List<elemento> listaDeElementos;
    //public List<String> listaElementosMarcados;
    public FirebaseDatabase database;
    public DatabaseReference myRef;
    public ChildEventListener listener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_compra);

        listaDeElementos=new ArrayList<>();


        database=FirebaseDatabase.getInstance();
        myRef=database.getReference("elementos");



        leerListaDeCompra();
    }

    public void leerListaDeCompra(){//lee la lista de la compra de firebase y la mete en el listview


        Query myQuery=myRef.orderByChild("comprado").equalTo(false);

        listener=myQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                elemento a=new elemento( dataSnapshot.child("Cantidad").getValue().toString(),(String)dataSnapshot.child("Nombre").getValue(),(String)dataSnapshot.child("Tipo").getValue(),"compra");


                System.out.println(a.descripcion());
                addElementoToLista(a);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                System.out.println(dataSnapshot.getValue());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }//fin de la función leerListaCompra

    public void addToLista(String nombre){
        final ListView lista=(ListView) findViewById(R.id.listView2);
        //final ArrayAdapter<String> adaptador_de_array=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        final ArrayAdapter<String> adaptador_de_array=(ArrayAdapter) lista.getAdapter();
        adaptador_de_array.add(nombre);
        lista.setAdapter(adaptador_de_array);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        myRef.removeEventListener(listener);

    }

    public void addElementoToLista(elemento elementoNuevo){
        listaDeElementos.add(elementoNuevo);



        //System.out.println("Añadido elemento "+elementoNuevo.Nombre+" a la lista");
        mostrarLista();
    }

    public void mostrarLista(){
        //System.out.println("Mostrando Lista");

        final TextView titulo=(TextView) findViewById(R.id.titulo);
        titulo.setText("Lista de la compra ("+listaDeElementos.size()+" elementos)");

        final ListView lista=(ListView) findViewById(R.id.listView2);


        ArrayAdapter<elemento> adaptador_de_array;


        adaptador_de_array = new ArrayAdapter<elemento>(this, android.R.layout.simple_list_item_1, listaDeElementos);

        lista.setAdapter(null);
        lista.setAdapter(adaptador_de_array);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {

                listaDeElementos.get(i).toggleComprado();
                System.out.println("Se ha tocado el elemento "+listaDeElementos.get(i).toString());
                mostrarLista();//vuelvo a pintar la lista entera


            }



        });//fin del onItemClickListener



    }//fin de la función mostrarLista




    public void quitarElementos(View view){ //todos los elementos marcados como "comprados en la lista los actualiza como tal en la base de datos online
        for(elemento e:listaDeElementos){
            if(e.comprado){
                myRef.child(e.Nombre).child("comprado").setValue(true);
            }
        }
        listaDeElementos.clear();
        leerListaDeCompra();
        mostrarLista();
    }


}
