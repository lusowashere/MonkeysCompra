package com.example.lusog.monkeyscompra;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.widget.CompoundButtonCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class adapterItemAddElemento extends RecyclerView.Adapter<adapterItemAddElemento.ViewHolderElementos> implements Filterable {

    ArrayList<elemento> listaElementos;
    ArrayList<elemento> listaOriginal;
    DatabaseReference referencia;
    TextView textBoxNombre,textBoxCantidad;
    CheckBox checkBoxUrgente;
    boolean hayTextBoxes;

    public adapterItemAddElemento(ArrayList<elemento> listaElementos, DatabaseReference ref) {
        this.listaElementos = listaElementos;
        this.listaOriginal=listaElementos;
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

        if(listaElementos.get(position).comprado){
            holder.buttAdd.setBackgroundResource(R.drawable.butt_mas);
            holder.cuadro.setBackgroundColor(Color.parseColor("#E6E6E6"));
            if(listaElementos.get(position).fechaCompra!=""){
                holder.labelCantidad.setText(listaElementos.get(position).fechaCompra);
            }else{holder.labelCantidad.setText(listaElementos.get(position).fecha);}
        }else{
            holder.buttAdd.setBackgroundResource(R.drawable.butt_menos);
            holder.cuadro.setBackgroundColor(Color.parseColor("#F7D358"));
            if(listaElementos.get(position).fechaApuntado!=""){
                holder.labelCantidad.setText(listaElementos.get(position).fechaApuntado);
            }else{holder.labelCantidad.setText(listaElementos.get(position).fecha);}
        }

        holder.buttAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                elemento elemAux=listaElementos.get(position);
                if(elemAux.comprado){
                    if(hayTextBoxes){
                        textBoxCantidad.setText(elemAux.Cantidad);
                        textBoxNombre.setText(elemAux.Nombre);
                        checkBoxUrgente.setChecked(elemAux.urgente);
                        if(elemAux.urgente){
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
                        holder.cuadro.setBackgroundColor(Color.parseColor("#A9F5E1"));

                    }
                }else{
                    referencia.child("elementos").child(listaElementos.get(position).Nombre).child("comprado").setValue(true);
                    referencia.child("elementos").child(listaElementos.get(position).Nombre).child("Fecha").setValue(listaElementos.get(position).fechaCompra);
                    //referencia.child("elementos").child(listaElementos.get(position).Nombre).child("FechaCompra").setValue("comprado el "+get_dia_y_hora_actual());
                    referencia.child("ultimaModificacion").setValue(get_dia_y_hora_actual());
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
                        referencia.child("elementos").child(listaElementos.get(position).Nombre).setValue(null);
                        referencia.child("ultimaModificacion").setValue(get_dia_y_hora_actual());
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

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String textoBusqueda=charSequence.toString();
                if(textoBusqueda.isEmpty()){
                    listaElementos=listaOriginal;
                }else{
                    ArrayList<elemento> f=new ArrayList<>();
                    for(elemento posibleElemento:listaOriginal){
                        if(posibleElemento.Nombre.toLowerCase().contains(textoBusqueda.toLowerCase())){
                            f.add(posibleElemento);
                        }
                    }
                    listaElementos=f;
                }
                FilterResults filterResults=new FilterResults();
                filterResults.values=listaElementos;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                listaElementos=(ArrayList<elemento>) filterResults.values;
                notifyDataSetChanged();
            }
        };
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


    public void addTextBoxes(TextView nombre,TextView cantidad,CheckBox urgente){
        textBoxCantidad=cantidad;
        textBoxNombre=nombre;
        checkBoxUrgente=urgente;
        hayTextBoxes=true;
    }



    public String get_dia_y_hora_actual(){
        Calendar cal = Calendar.getInstance();
        Date date=cal.getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM HH:mm");
        String formattedDate=dateFormat.format(date);
        return formattedDate;
    }


}
