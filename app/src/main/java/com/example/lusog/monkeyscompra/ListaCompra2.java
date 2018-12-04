package com.example.lusog.monkeyscompra;

import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

public class ListaCompra2 extends AppCompatActivity {

    RecyclerView recyclerElementos;

    ArrayList<elemento> listaElementos;

    public FirebaseDatabase database;
    public DatabaseReference myRef;
    public Query myQuery;
    public ChildEventListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_compra2);

        listaElementos=new ArrayList<>();

        recyclerElementos=findViewById(R.id.recyclerViewListaCompra);

        /*
        //temporal
        listaElementos.add(new elemento("2","elemento1","otro",false,""));
        listaElementos.add(new elemento("1 caja","elemento2","otro",false,""));
        listaElementos.add(new elemento("2","elemento1","otro",false,""));
        listaElementos.add(new elemento("1 caja","elemento2","otro",false,""));
        listaElementos.add(new elemento("2","elemento1","otro",false,""));
        listaElementos.add(new elemento("1 caja","elemento2","otro",false,""));
        listaElementos.add(new elemento("2","elemento1","otro",false,""));
        listaElementos.add(new elemento("1 caja","elemento2","otro",false,""));
        */

        recyclerElementos.setLayoutManager(new LinearLayoutManager(this));
        AdapterItemListaCompra adapter=new AdapterItemListaCompra(listaElementos);
        recyclerElementos.setAdapter(adapter);


        database=FirebaseDatabase.getInstance();

        myRef=database.getReference("elementos");

        myQuery=myRef.orderByChild("comprado").equalTo(false);

        listener=myQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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
        })

    }




}
