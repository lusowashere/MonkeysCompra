package com.example.lusog.monkeyscompra;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

public class pantallaAddElemento2 extends AppCompatActivity {

    ArrayList<elemento> listaElementos;
    RecyclerView recicler;
    FirebaseDatabase database;
    DatabaseReference myRef;
    ChildEventListener listener;
    public TextView textBoxNombre,textBoxCantidad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_add_elemento2);
        listaElementos=new ArrayList<>();

        database=FirebaseDatabase.getInstance();
        myRef=database.getReference("elementos");

        recicler=findViewById(R.id.recyclerPosiblesElementos);
        textBoxNombre=findViewById(R.id.textBoxNombre);
        textBoxCantidad=findViewById(R.id.textBoxCantidad);

        adapterItemAddElemento adaptador=new adapterItemAddElemento(listaElementos,myRef);
        adaptador.addTextBoxes(textBoxNombre,textBoxCantidad);

        recicler.setLayoutManager(new LinearLayoutManager(this));
        recicler.setAdapter(adaptador);

        listener=myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                elemento nuevoElemento=new elemento(
                        dataSnapshot.child("Cantidad").getValue().toString(),
                        dataSnapshot.child("Nombre").getValue().toString(),
                        dataSnapshot.child( "Tipo").getValue().toString(),
                        (boolean) dataSnapshot.child("comprado").getValue()
                );

                if(dataSnapshot.hasChild("Fecha")){
                    nuevoElemento.setFecha(dataSnapshot.child("Fecha").getValue().toString());
                }

                listaElementos.add(nuevoElemento);
                recicler.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                String nombreCambiado=dataSnapshot.child("Nombre").getValue().toString();

                for(elemento e:listaElementos){
                    if(e.Nombre.equals(nombreCambiado)){
                        e.Cantidad=dataSnapshot.child("Cantidad").getValue().toString();
                        e.comprado=(boolean) dataSnapshot.child("comprado").getValue();
                        if(dataSnapshot.hasChild("Fecha")){
                            e.setFecha(dataSnapshot.child("Fecha").getValue().toString());
                        }
                        recicler.getAdapter().notifyDataSetChanged();
                    }
                }

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String nombreRemoved=dataSnapshot.child("Nombre").getValue().toString();

                Iterator<elemento> iter=listaElementos.iterator();

                while(iter.hasNext()){
                    if(iter.next().Nombre.equals(nombreRemoved)){
                        iter.remove();
                    }
                }

                recicler.getAdapter().notifyDataSetChanged();

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });




    }//fin de la función ON CREATE



    public void buttAddElementoClick(View view) {

        TextView labelMensaje = (TextView) findViewById(R.id.labelMensaje);

        if (!textBoxCantidad.getText().toString().isEmpty() && !textBoxNombre.getText().toString().isEmpty()) {
            String stringCantidad = textBoxCantidad.getText().toString();
            //int intCantidad = Integer.parseInt(stringCantidad);

            System.out.println("añadido elemento '" + textBoxNombre.getText().toString().trim() + "'");

            elemento nuevoElemento = new elemento(stringCantidad, textBoxNombre.getText().toString().trim(), "otro", false, "anteriores");
            nuevoElemento.setFecha("Apuntado el "+get_dia_y_hora_actual());

            myRef.child(nuevoElemento.Nombre).setValue(nuevoElemento.getElementoAux());
            /*myRef.child(nuevoElemento.Nombre).child("Cantidad").setValue(nuevoElemento.Cantidad);
            myRef.child(nuevoElemento.Nombre).child("Nombre").setValue(nuevoElemento.Nombre);
            myRef.child(nuevoElemento.Nombre).child("comprado").setValue(false);
            myRef.child(nuevoElemento.Nombre).child("Tipo").setValue("otro");
*/


            labelMensaje.setText("Añadido " + nuevoElemento.Nombre + "(" + nuevoElemento.Cantidad + ") a la lista de la compra");

            textBoxNombre.setText("");
            textBoxCantidad.setText("1");


        } else {
            labelMensaje.setText("Por favor, introduzca información en los recuadros de nombre y cantidades");
        }
    }// fin de la función BUTT ADD ELEMENTO CLICK


    public String get_dia_y_hora_actual(){
        Calendar cal = Calendar.getInstance();
        Date date=cal.getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM HH:mm");
        String formattedDate=dateFormat.format(date);
        return formattedDate;
    }

}
