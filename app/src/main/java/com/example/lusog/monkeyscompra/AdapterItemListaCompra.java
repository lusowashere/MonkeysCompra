package com.example.lusog.monkeyscompra;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AdapterItemListaCompra extends RecyclerView.Adapter<AdapterItemListaCompra.ViewHolderLista> {

    ArrayList<elemento> listaElementos;
    DatabaseReference referencia;

    public AdapterItemListaCompra(ArrayList<elemento> listaElementos, DatabaseReference ref) {
        this.listaElementos = listaElementos;
        referencia=ref;
    }

    @Override
    public AdapterItemListaCompra.ViewHolderLista onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lista_compra,parent,false);
        return new ViewHolderLista(view);
    }

    @Override
    public void onBindViewHolder(final AdapterItemListaCompra.ViewHolderLista holder, final int position) {
        holder.labelNombre.setText(listaElementos.get(position).Nombre);
        holder.labelCantidad.setText(listaElementos.get(position).Cantidad);
        holder.labelFecha.setText(listaElementos.get(position).fecha);
        if(listaElementos.get(position).comprado){
            holder.checkBoxComprado.setChecked(true);
            holder.cuadro.setBackgroundColor(Color.parseColor("#58FA82"));
        }else{
            holder.checkBoxComprado.setChecked(false);
            holder.cuadro.setBackgroundColor(Color.parseColor("#E6E6E6"));
        }

        holder.checkBoxComprado.setOnClickListener( new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                boolean marcado=holder.checkBoxComprado.isChecked();
                listaElementos.get(position).comprado=marcado;

                if(marcado){
                    holder.cuadro.setBackgroundColor(Color.parseColor("#58FA82"));
                    referencia.child(listaElementos.get(position).Nombre).child("Fecha").setValue("comprado el "+get_dia_y_hora_actual());
                }else{
                    holder.cuadro.setBackgroundColor(Color.parseColor("#E6E6E6"));
                }

                referencia.child(listaElementos.get(position).Nombre).child("comprado").setValue(marcado);

            }
        });

    }

    @Override
    public int getItemCount() {
        return listaElementos.size();
    }

    public class ViewHolderLista extends RecyclerView.ViewHolder {

        TextView labelNombre, labelCantidad,labelFecha;
        CheckBox checkBoxComprado;
        View cuadro;

        public ViewHolderLista(View itemView) {
            super(itemView);

            labelNombre=itemView.findViewById(R.id.labelNombre);
            labelCantidad=itemView.findViewById(R.id.labelCantidad);
            labelFecha=itemView.findViewById(R.id.labelFecha);
            checkBoxComprado=itemView.findViewById(R.id.checkBoxComprado);
            cuadro=itemView.findViewById(R.id.cuadro);

        }
    }

    public String get_dia_y_hora_actual(){
        Calendar cal = Calendar.getInstance();
        Date date=cal.getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM HH:mm");
        String formattedDate=dateFormat.format(date);
        return formattedDate;
    }


}
