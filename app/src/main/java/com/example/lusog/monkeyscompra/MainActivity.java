package com.example.lusog.monkeyscompra;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference myRef;
    Query queryElementosApuntados,queryNuevosElementos;
    TextView labelUltimaModificacion;
    Button botonElementosApuntados;
    ValueEventListener listenerCantidad,listenerUltimaModificacion;
    ChildEventListener listenerNotificaciones;
    String CHANNEL_ID;
    boolean alertasActivadas;
    String horaAlertas;

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

        CHANNEL_ID="canal_monkeys_compra";

        guardarTexto("activo","Y");

        queryNuevosElementos=myRef.orderByChild("comprado").equalTo(false);
        listenerNotificaciones=new ChildEventListener() {
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
        };

        //setNotificaciones(true);
        //notificar("texto","titulo");

    }

    public void abrirPantallaLista(View view){//abre la pantalla con la lista de los elementos
        Intent intento=new Intent(this,ListaCompra2.class);
        startActivity(intento);
    }

    public void abrirPantallaAddElemento(View view){
        Intent intento2=new Intent(this,pantallaAddElemento2.class);
        startActivity(intento2);
    }

    public void setNotificaciones(boolean activadas){
        alertasActivadas=activadas;



        if(activadas){

            guardarTexto("alertasActivadas","Y");

            Calendar cal = Calendar.getInstance();
            Date date=cal.getTime();
            DateFormat dateFormat = new SimpleDateFormat("YYMMddHH:mm");
            horaAlertas=dateFormat.format(date);

            //queryNuevosElementos=myRef.orderByChild("comprado").equalTo(false);
            listenerNotificaciones=new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if(fechaParseada(dataSnapshot.child("FechaApuntado").getValue().toString()).compareTo(horaAlertas)>0 && leerTextoGuardado("activo").compareTo("N")==0) {
                        notificar("se ha apuntado el elemento " + dataSnapshot.child("Nombre").getValue().toString(), "Elemento añadido a monkeys compra");
                    }else {
                        System.out.println("comprobando el elemento "+dataSnapshot.child("Nombre").getValue()+" apuntado en la fecha "+fechaParseada(dataSnapshot.child("FechaApuntado").getValue().toString()) +" y es anterior a "+horaAlertas);
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {}

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

                @Override
                public void onCancelled(DatabaseError databaseError) {}

            };
            queryNuevosElementos.addChildEventListener(listenerNotificaciones);
        }else{
            guardarTexto("alertasActivadas","N");
            queryNuevosElementos.removeEventListener(listenerNotificaciones);
        }
    }


    //FUNCIONES AUXILIARES


    public void notificar(String texto, String titulo){

        Intent intentoNotific=new Intent(this,ListaCompra2.class);
        intentoNotific.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intentoNotific,0);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,CHANNEL_ID)
                //.setSmallIcon(R.drawable.ic_launcher_foreground)
                .setSmallIcon(R.drawable.butt_mas)
                .setContentTitle(titulo)
                .setContentText(texto)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        createNotificationChannel();
        NotificationManagerCompat notificationManager=NotificationManagerCompat.from(this);
        notificationManager.notify(1,builder.build());
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "nombre canal";
            String description = "descripcion canal";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public String fechaParseada(String texto_a_parsear){


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
                ano="20"; //cualquiera que se apunte ahora será del 20
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
            OutputStreamWriter osw=new OutputStreamWriter(openFileOutput(rutaArchivo,Context.MODE_PRIVATE));
            osw.write(texto);
            osw.close();

        }catch (Exception e){
            System.out.println("no se ha podido guardar el archivo");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("onResume");
        guardarTexto("activo","Y");
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("onPause");
        guardarTexto("activo","N");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity,menu);

        alertasActivadas=leerTextoGuardado("alertasActivadas").compareTo("Y")==0;
        for(int i=0;i<menu.size();i++){
            if(menu.getItem(i).getItemId()==R.id.checkBoxNotificaciones){
                menu.getItem(i).setChecked(alertasActivadas);
            }
        }

        setNotificaciones(alertasActivadas);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.checkBoxNotificaciones:
                alertasActivadas=!alertasActivadas;
                item.setChecked(alertasActivadas);
                setNotificaciones(alertasActivadas);
                break;
            default:
                break;
        }

        return true;
    }
}
