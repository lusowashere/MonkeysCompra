package com.example.lusog.monkeyscompra;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

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

        database=FirebaseDatabase.getInstance();

        myRef=database.getReference("elementos");

        myQuery=myRef.orderByChild("comprado").equalTo(false);

        recyclerElementos.setLayoutManager(new LinearLayoutManager(this));
        AdapterItemListaCompra adapter=new AdapterItemListaCompra(listaElementos,database.getReference());
        recyclerElementos.setAdapter(adapter);

        listener=myQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                elemento e=new elemento(dataSnapshot.child("Cantidad").getValue().toString(),dataSnapshot.child("Nombre").getValue().toString(),dataSnapshot.child("Tipo").getValue().toString(),(boolean) dataSnapshot.child("comprado").getValue());
                if(dataSnapshot.hasChild("Fecha")){
                    e.setFecha(dataSnapshot.child("Fecha").getValue().toString());
                }

                if(dataSnapshot.hasChild(("FechaCompra"))){
                    e.setFechaCompra(dataSnapshot.child("FechaCompra").getValue().toString());
                }
                if(dataSnapshot.hasChild(("FechaApuntado"))){
                    e.setFechaApuntado(dataSnapshot.child("FechaApuntado").getValue().toString());
                }
                if(dataSnapshot.hasChild("urgente")){
                    e.setUrgente((boolean)dataSnapshot.child("urgente").getValue());
                }


                boolean elementoEnLista=false;

                for(elemento elem:listaElementos){
                    if(elem.Nombre.equals(e.Nombre)){
                        elementoEnLista=true;
                        elem.comprado=false;
                        elem.setFecha(e.fecha);
                        elem.setFechaApuntado(e.fechaApuntado);
                        elem.setFechaCompra(e.fechaCompra);
                        elem.setUrgente(e.urgente);
                        //recyclerElementos.getAdapter().notifyDataSetChanged();
                    }
                }

                if(!elementoEnLista) {
                    listaElementos.add(e);
                    //recyclerElementos.getAdapter().notifyDataSetChanged();

                }
                reordenar_y_refrescar();
                actualizar_numero_elementos();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                String elementoModificado=dataSnapshot.child("Nombre").getValue().toString();

                for(elemento elem:listaElementos){
                    if(elem.Nombre.equals(elementoModificado)){
                        elem.Cantidad=dataSnapshot.child("Cantidad").getValue().toString();
                        elem.comprado=(boolean) dataSnapshot.child("comprado").getValue();//este no tiene sentido
                        if(dataSnapshot.hasChild("Fecha")){
                            elem.setFecha(dataSnapshot.child("Fecha").getValue().toString());//este tampoco
                        }
                        if(dataSnapshot.hasChild(("FechaCompra"))){
                            elem.setFechaCompra(dataSnapshot.child("FechaCompra").getValue().toString());
                        }
                        if(dataSnapshot.hasChild(("FechaApuntado"))){
                            elem.setFechaApuntado(dataSnapshot.child("FechaApuntado").getValue().toString());
                        }
                        if(dataSnapshot.hasChild("urgente")){
                            elem.setUrgente((boolean)dataSnapshot.child("urgente").getValue());
                        }
                        recyclerElementos.getAdapter().notifyDataSetChanged();
                        //reordenar_y_refrescar();
                        actualizar_numero_elementos();
                    }
                }
                //reordenar_y_refrescar();
                //actualizar_numero_elementos();

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String nombreRemovido=dataSnapshot.child("Nombre").getValue().toString();
                System.out.println("child removed:"+nombreRemovido);
                for(elemento elem:listaElementos){
                    if(elem.Nombre.equals(nombreRemovido)){
                        elem.comprado=true;
                        //recyclerElementos.getAdapter().notifyDataSetChanged();

                        actualizar_numero_elementos();
                    }
                }
                reordenar_y_refrescar();

                //esto no lo pongo porque si no en cuanto se marca el check desaparece
                /*
                Iterator<elemento> itr=listaElementos.iterator();

                while(itr.hasNext()){

                    if(nombreRemovido.equals(itr.next().Nombre)){
                        itr.remove();
                        recyclerElementos.getAdapter().notifyDataSetChanged();
                    }
                }*/

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }



    public void quitarElementosMarcados(View view){
        Iterator<elemento> iter=listaElementos.iterator();
        elemento el;

        while(iter.hasNext()){
            el=iter.next();
            //if(iter.next().comprado){
            if(el.comprado || el.agotado){
                iter.remove();
            }
        }

        //recyclerElementos.getAdapter().notifyDataSetChanged();
        reordenar_y_refrescar();
        actualizar_numero_elementos();

    }

    public void actualizar_numero_elementos(){
        TextView texto=findViewById(R.id.labelCantidadElementos);
        texto.setText(listaElementos.size()+" elementos");
    }
    public String leerTextoGuardado(String rutaArchivo) {

        String textoLeido="";
        try{
            BufferedReader br=new BufferedReader(new InputStreamReader(openFileInput(rutaArchivo)));
            textoLeido=br.readLine();
            br.close();
        }catch (Exception e){
            textoLeido="";
        }
        return textoLeido;
    }

    public void guardarTexto(String rutaArchivo,String texto){
        try{
            OutputStreamWriter osw=new OutputStreamWriter(openFileOutput(rutaArchivo, Context.MODE_PRIVATE));
            osw.write(texto);
            osw.close();

        }catch (Exception e){
            System.out.println("no se ha podido guardar el archivo");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        guardarTexto("activo","Y");
    }

    @Override
    protected void onPause() {
        super.onPause();
        guardarTexto("activo","N");
    }

    public void reordenar_y_refrescar(){
        Collections.sort(listaElementos, new Comparator<elemento>() {
            @Override
            public int compare(elemento el1, elemento el2) {
                if(el1.urgente==el2.urgente){
                    return el1.Nombre.compareTo(el2.Nombre);
                }else{
                    if(el1.urgente){return -1;}else{return 1;}
                }
            }
        });
        recyclerElementos.getAdapter().notifyDataSetChanged();
    }

}
