package com.example.lusog.monkeyscompra;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.widget.CompoundButtonCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.function.Function;

public class pantallaAddElemento2 extends AppCompatActivity {

    ArrayList<elemento> listaElementos;
    RecyclerView recicler;
    FirebaseDatabase database;
    DatabaseReference myRef;
    ChildEventListener listener;
    public TextView textBoxNombre,textBoxCantidad;
    public CheckBox checkBoxUrgente;
    public Button buttClear;

    public int ordenElegido;

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
        checkBoxUrgente=findViewById(R.id.checkBoxUrgente);
        buttClear=findViewById(R.id.buttClear);

        ordenElegido=0;//por defecto alfabético

        final adapterItemAddElemento adaptador=new adapterItemAddElemento(listaElementos,database.getReference());
        adaptador.addTextBoxes(textBoxNombre,textBoxCantidad,checkBoxUrgente);

        textBoxNombre.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //System.out.println("ontextchanged:"+charSequence);
                adaptador.getFilter().filter(charSequence);
                if(charSequence.length()>0){
                    buttClear.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

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
                if(dataSnapshot.hasChild(("FechaCompra"))){
                    nuevoElemento.setFechaCompra(dataSnapshot.child("FechaCompra").getValue().toString());
                }
                if(dataSnapshot.hasChild(("FechaApuntado"))){
                    nuevoElemento.setFechaApuntado(dataSnapshot.child("FechaApuntado").getValue().toString());
                }
                if(dataSnapshot.hasChild(("urgente"))){
                    nuevoElemento.setUrgente((boolean) dataSnapshot.child("urgente").getValue());
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
                        if(dataSnapshot.hasChild(("FechaCompra"))){
                            e.setFechaCompra(dataSnapshot.child("FechaCompra").getValue().toString());
                        }
                        if(dataSnapshot.hasChild(("FechaApuntado"))){
                            e.setFechaApuntado(dataSnapshot.child("FechaApuntado").getValue().toString());
                        }
                        if(dataSnapshot.hasChild(("urgente"))){
                            e.setUrgente((boolean) dataSnapshot.child("urgente").getValue());
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

        //añado un listener para el checkbox y que cambie de color
        cambiarColorCheckBoxUrgente();
        checkBoxUrgente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cambiarColorCheckBoxUrgente();
            }
        });

        buttClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textBoxNombre.setText("");
                textBoxCantidad.setText("1");
                checkBoxUrgente.setChecked(false);
                cambiarColorCheckBoxUrgente();
                buttClear.setVisibility(View.GONE);
            }
        });


    }//fin de la función ON CREATE


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_elemento,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()){
            case R.id.ordenar:

                String[] posibles_ordenes={"Alfabético","Fecha de compra"};

                AlertDialog.Builder alt_bld=new AlertDialog.Builder(this);
                alt_bld.setTitle("ordenar por:");

                alt_bld.setSingleChoiceItems(posibles_ordenes, ordenElegido, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //System.out.println("pulsada opción "+i);
                        cambiarOrden(i);
                        dialogInterface.dismiss();
                    }
                });

                AlertDialog alert=alt_bld.create();
                alert.show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void buttAddElementoClick(View view) {

        TextView labelMensaje = (TextView) findViewById(R.id.labelMensaje);

        if (!textBoxCantidad.getText().toString().isEmpty() && !textBoxNombre.getText().toString().isEmpty()) {
            String stringCantidad = textBoxCantidad.getText().toString().replace("/"," ");
            //int intCantidad = Integer.parseInt(stringCantidad);

            //System.out.println("añadido elemento '" + textBoxNombre.getText().toString().trim() + "'");

            String nombreDelNuevoElemento=textBoxNombre.getText().toString().trim().replace("/"," ");
            nombreDelNuevoElemento=nombreDelNuevoElemento.substring(0,1).toUpperCase()+ nombreDelNuevoElemento.substring(1).toLowerCase();

            elemento nuevoElemento = new elemento(stringCantidad, nombreDelNuevoElemento, "otro", false, "anteriores");
            nuevoElemento.setFecha("Apuntado el "+get_dia_y_hora_actual());
            nuevoElemento.setFechaApuntado("Apuntado el "+get_dia_y_hora_actual());
            nuevoElemento.setUrgente(checkBoxUrgente.isChecked());

            //si el elemento ya existía, busco su fecha de compra
            for(int el=0;el<listaElementos.size();el++){
                if(listaElementos.get(el).Nombre.equals(nuevoElemento.Nombre)){//ya existía
                    nuevoElemento.fechaCompra=listaElementos.get(el).fechaCompra;
                }
            }

            myRef.child(nuevoElemento.Nombre).setValue(nuevoElemento.getElementoAux());
            /*myRef.child(nuevoElemento.Nombre).child("Cantidad").setValue(nuevoElemento.Cantidad);
            myRef.child(nuevoElemento.Nombre).child("Nombre").setValue(nuevoElemento.Nombre);
            myRef.child(nuevoElemento.Nombre).child("comprado").setValue(false);
            myRef.child(nuevoElemento.Nombre).child("Tipo").setValue("otro");
*/
            database.getReference().child("ultimaModificacion").setValue(get_dia_y_hora_actual());

            labelMensaje.setText("Añadido " + nuevoElemento.Nombre + "(" + nuevoElemento.Cantidad + ") a la lista de la compra");

            textBoxNombre.setText("");
            textBoxCantidad.setText("1");
            checkBoxUrgente.setChecked(false);
            cambiarColorCheckBoxUrgente();
            buttClear.setVisibility(View.GONE   );


        } else {
            labelMensaje.setText("Por favor, introduzca información en los recuadros de nombre y cantidades");
        }
    }// fin de la función BUTT ADD ELEMENTO CLICK


    public String get_dia_y_hora_actual(){
        Calendar cal = Calendar.getInstance();
        Date date=cal.getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/YY HH:mm");
        String formattedDate=dateFormat.format(date);
        return formattedDate;
    }

    public void cambiarOrden(int orden){
        ordenElegido=orden;
        switch (orden){
            case 0://por orden alfabético
                // System.out.println("elegido orden alfabético");
                Collections.sort(listaElementos, new Comparator<elemento>() {
                    @Override
                    public int compare(elemento el1, elemento el2) {
                        return el1.Nombre.compareToIgnoreCase(el2.Nombre);
                    }
                });

                //temporal
                /*for(elemento a:listaElementos){
                    System.out.println(a.Nombre+" - "+a.fechaCompra+" - "+fechaParseada(a));
                }*/
                break;

            case 1://por fecha
                Collections.sort(listaElementos, new Comparator<elemento>() {
                    @Override
                    public int compare(elemento el1, elemento el2) {
                        return fechaParseada(el2).compareToIgnoreCase(fechaParseada(el1));
                    }
                });
                break;
        }
        recicler.getAdapter().notifyDataSetChanged();
    }

    public String fechaParseada(elemento El){
        String texto_a_parsear;
       //if(El.comprado){
            if(El.fechaCompra==null || El.fechaCompra==""){
                texto_a_parsear=El.fecha;
            }else{
                texto_a_parsear=El.fechaCompra;
            }
        /*}else{
            if(El.fechaApuntado==null || El.fechaApuntado==""){
                texto_a_parsear=El.fecha;
            }else{
                texto_a_parsear=El.fechaApuntado;
            }
        }*/
        //System.out.println(texto_a_parsear);

        if(texto_a_parsear.indexOf('/')<2||texto_a_parsear.length()<5){
            return "00000000:00";
        }else {
            String dia, mes, hora,ano;
            dia = texto_a_parsear.substring(texto_a_parsear.indexOf('/') - 2, texto_a_parsear.indexOf('/'));
            mes = texto_a_parsear.substring(texto_a_parsear.indexOf('/') + 1, texto_a_parsear.indexOf('/') + 3);
            hora = texto_a_parsear.substring(texto_a_parsear.length() - 5);

            if(countOcurrences("/",texto_a_parsear)>1){
                ano=texto_a_parsear.substring(texto_a_parsear.lastIndexOf('/') + 1, texto_a_parsear.lastIndexOf('/') + 3);
            }else{
                ano="19";
            }


            return ano+mes + dia + hora;
        }
    }

    public int countOcurrences(String texto_a_buscar,String texto){
        int ocurrencias=0;
        String aux;
        for(int i=0;i<texto.length()-texto_a_buscar.length();i++){
            //aux=texto.substring(i,i+texto_a_buscar.length());
            if(texto.substring(i,i+texto_a_buscar.length()).compareToIgnoreCase(texto_a_buscar)==0){
                ocurrencias++;
            }
        }
        return ocurrencias;
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

    public void cambiarColorCheckBoxUrgente(){
        if(checkBoxUrgente.isChecked()){
            checkBoxUrgente.setTextColor(Color.parseColor("#FF4000"));
            if (Build.VERSION.SDK_INT < 21) {
                CompoundButtonCompat.setButtonTintList(checkBoxUrgente, ColorStateList.valueOf(Color.parseColor("#FE642E")));//Use android.support.v4.widget.CompoundButtonCompat when necessary else
            } else {
                checkBoxUrgente.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#FE642E")));//setButtonTintList is accessible directly on API>19
            }
        }else{
            checkBoxUrgente.setTextColor(Color.parseColor("#000000"));
            if (Build.VERSION.SDK_INT < 21) {
                CompoundButtonCompat.setButtonTintList(checkBoxUrgente, ColorStateList.valueOf(Color.parseColor("#2E2E2E")));//Use android.support.v4.widget.CompoundButtonCompat when necessary else
            } else {
                checkBoxUrgente.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#2E2E2E")));//setButtonTintList is accessible directly on API>19
            }
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

}
