package com.example.lusog.monkeyscompra;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference myRef;
    Query queryElementosApuntados;
    TextView labelUltimaModificacion;
    Button botonElementosApuntados;
    ValueEventListener listenerCantidad,listenerUltimaModificacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        labelUltimaModificacion=findViewById(R.id.labelUltimaModificacion);
        botonElementosApuntados=findViewById(R.id.buttListaCompra);

        database=FirebaseDatabase.getInstance();

        myRef=database.getReference("elementos");

        queryElementosApuntados=myRef.orderByChild("comprado").equalTo(false);

        listenerCantidad=queryElementosApuntados.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                botonElementosApuntados.setText("Comprobar la lista de la compra\n(" +dataSnapshot.getChildrenCount()+ " elementos apuntados)");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listenerUltimaModificacion=database.getReference("ultimaModificacion").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                labelUltimaModificacion.setText("Ultima modificación: "+dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public void abrirPantallaLista(View view){//abre la pantalla con la lista de los elementos
        Intent intento=new Intent(this,ListaCompra2.class);
        startActivity(intento);
    }

    public void abrirPantallaAddElemento(View view){
        Intent intento2=new Intent(this,pantallaAddElemento2.class);
        startActivity(intento2);
    }
}
