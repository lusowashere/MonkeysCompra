package com.example.lusog.monkeyscompra;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class adapterItemAddElemento extends RecyclerView.Adapter<adapterItemAddElemento.ViewHolderElementos> {

    ArrayList<elemento> listaElementos;
    DatabaseReference referencia;
    TextView textBoxNombre,textBoxCantidad;
    boolean hayTextBoxes;

    public adapterItemAddElemento(ArrayList<elemento> listaElementos, DatabaseReference ref) {
        this.listaElementos = listaElementos;
        referencia=ref;
        hayTextBoxes=false;
    }

    @Override
    public adapterItemAddElemento.ViewHolderElementos onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_elementos,parent,false);
        return new ViewHolderElementos(view);
    }

    @Override
    public void onBindViewHolder(final adapterItemAddElemento.ViewHolderElementos holder, final int position) {
        holder.labelNombre.setText(listaElementos.get(position).Nombre);
        holder.labelCantidad.setText(listaElementos.get(position).fecha);
        if(listaElementos.get(position).comprado){
            holder.buttAdd.setBackgroundResource(R.drawable.butt_mas);
            holder.cuadro.setBackgroundColor(Color.parseColor("#E6E6E6"));
        }else{
            holder.buttAdd.setBackgroundResource(R.drawable.butt_menos);
            holder.cuadro.setBackgroundColor(Color.parseColor("#F7D358"));
        }

        holder.buttAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listaElementos.get(position).comprado){
                    if(hayTextBoxes){
                        textBoxCantidad.setText(listaElementos.get(position).Cantidad);
                        textBoxNombre.setText(listaElementos.get(position).Nombre);
                    }
                }else{
                    referencia.child(listaElementos.get(position).Nombre).child("comprado").setValue(true);
                }
            }
        });

        holder.buttEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alerta=new AlertDialog.Builder(v.getContext());
                alerta.setMessage("¿Eliminar elemento '"+listaElementos.get(position).Nombre+"'?");
                alerta.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        referencia.child(listaElementos.get(position).Nombre).setValue(null);
                        dialogInterface.dismiss();
                    }
                });
                alerta.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                alerta.create().show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return listaElementos.size();
    }

    public class ViewHolderElementos extends RecyclerView.ViewHolder {

        TextView labelNombre, labelCantidad;
        Button buttAdd, buttEliminar;
        View cuadro;

        public ViewHolderElementos(View itemView) {
            super(itemView);

            labelNombre=itemView.findViewById(R.id.labelNombre);
            labelCantidad=itemView.findViewById(R.id.labelCantidad);
            buttAdd=itemView.findViewById(R.id.buttMas);
            buttEliminar=itemView.findViewById(R.id.buttEliminar);
            cuadro=itemView.findViewById(R.id.cuadro);

        }
    }


    public void addTextBoxes(TextView nombre,TextView cantidad){
        textBoxCantidad=cantidad;
        textBoxNombre=nombre;
        hayTextBoxes=true;
    }

}
