package com.example.lusog.monkeyscompra;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void abrirPantallaLista(View view){//abre la pantalla con la lista de los elementos
        Intent intento=new Intent(this,ListaCompra.class);
        startActivity(intento);
    }

    public void abrirPantallaAddElemento(View view){
        Intent intento2=new Intent(this,pantallaAddElemento.class);
        startActivity(intento2);
    }
}
