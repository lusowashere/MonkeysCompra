package com.example.lusog.monkeyscompra;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class pantallaAddElemento extends AppCompatActivity {

    public FirebaseDatabase database;
    public DatabaseReference myRef;
    public List<elemento> listaElementos;
    public ChildEventListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_add_elemento);

        database=FirebaseDatabase.getInstance();
        myRef=database.getReference("elementos");

        listaElementos=new ArrayList<>();
        addElementosAnteriores();
    }


    @Override
    protected void onDestroy(){
        super.onDestroy();

        myRef.removeEventListener(listener);

    }

    public void addElementosAnteriores(){
        Query myQuery=myRef.orderByChild("Nombre");
        listaElementos.clear();
        listener=myQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //addElementoALista(dataSnapshot.child("Nombre").getValue().toString());
                elemento a=new elemento( dataSnapshot.child("Cantidad").getValue().toString(),(String)dataSnapshot.child("Nombre").getValue(),(String)dataSnapshot.child("Tipo").getValue(),(boolean)dataSnapshot.child("comprado").getValue(),"anteriores");
                //System.out.println("leido elemento:" + a.toString());
                addElementoALista(a);
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
        });
    }//fin de la función addElementosAnteriores

    public void addElementoALista(elemento elemento_to_add){
        //System.out.println("addElemento:"+elemento_to_add.toString());
        listaElementos.add(elemento_to_add);
        final ListView lista=(ListView) findViewById(R.id.listaAnteriores);

        ArrayAdapter<elemento> adaptador_de_array=new ArrayAdapter<elemento>(this,android.R.layout.simple_list_item_1,listaElementos);
        lista.setAdapter(null);
        lista.setAdapter(adaptador_de_array);
        //final ArrayAdapter<String> adaptador_de_array=(ArrayAdapter) lista.getAdapter();
        //adaptador_de_array.add(elemento_to_add.toString());
        //lista.setAdapter(adaptador_de_array);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                EditText txtBoxNombre = (EditText) findViewById(R.id.textBoxNombre);
                txtBoxNombre.setText(listaElementos.get(i).Nombre);

                EditText txtBoxCantidad = (EditText) findViewById(R.id.textBoxCantidad);
                txtBoxCantidad.setText( listaElementos.get(i).Cantidad);
            }

        });

        lista.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int posElemento, long l) {
                AlertDialog.Builder alerta=new AlertDialog.Builder(pantallaAddElemento.this);
                alerta.setTitle("Eliminar elemento");
                alerta.setMessage("¿Desea eliminar el elemento \""+listaElementos.get(posElemento)+"\" de la lista de sugerencias?");
                alerta.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        //System.out.println("Eliminado elemento "+listaElementos.get(posElemento));
                        myRef.child(listaElementos.get(posElemento).Nombre).setValue(null);//elimino el elemento
                        dialog.dismiss();
                        addElementosAnteriores();
                    }
                });

                alerta.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();
                    }
                });

                AlertDialog aaa=alerta.create();
                aaa.show();

                addElementosAnteriores();

                return false;
            }
        });


    }

    public void buttAddElementoClick(View view)
    {
        EditText txtBoxNombre=(EditText) findViewById(R.id.textBoxNombre);
        EditText txtBoxCantidad=(EditText) findViewById(R.id.textBoxCantidad);
        TextView labelMensaje=(TextView) findViewById(R.id.labelMensaje);

        if(!txtBoxCantidad.getText().toString().isEmpty() && !txtBoxNombre.getText().toString().isEmpty()) {
            String stringCantidad = txtBoxCantidad.getText().toString();
            //int intCantidad = Integer.parseInt(stringCantidad);

            System.out.println("añadido elemento '" + txtBoxNombre.getText().toString().trim()+"'");

            elemento nuevoElemento = new elemento( stringCantidad, txtBoxNombre.getText().toString().trim(), "otro",false,"anteriores");

            myRef.child(nuevoElemento.Nombre).setValue(nuevoElemento.getElementoAux());
            /*myRef.child(nuevoElemento.Nombre).child("Cantidad").setValue(nuevoElemento.Cantidad);
            myRef.child(nuevoElemento.Nombre).child("Nombre").setValue(nuevoElemento.Nombre);
            myRef.child(nuevoElemento.Nombre).child("comprado").setValue(false);
            myRef.child(nuevoElemento.Nombre).child("Tipo").setValue("otro");
*/


            labelMensaje.setText("Añadido " + nuevoElemento.Nombre +"("+nuevoElemento.Cantidad+ ") a la lista de la compra");

            txtBoxNombre.setText("");
            txtBoxCantidad.setText("1");

            addElementosAnteriores();


        }else{
            labelMensaje.setText("Por favor, introduzca información en los recuadros de nombre y cantidades");
        }
    }
}
