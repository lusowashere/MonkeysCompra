package com.example.lusog.monkeyscompra;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterItemListaCompra extends RecyclerView.Adapter<AdapterItemListaCompra.ViewHolderLista> {

    ArrayList<elemento> listaElementos;

    public AdapterItemListaCompra(ArrayList<elemento> listaElementos) {
        this.listaElementos = listaElementos;
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
        if(listaElementos.get(position).comprado){
            holder.checkBoxComprado.setChecked(true);
        }else{
            holder.checkBoxComprado.setChecked(false);
        }

        holder.checkBoxComprado.setOnClickListener( new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                boolean marcado=holder.checkBoxComprado.isChecked();
                listaElementos.get(position).comprado=marcado;

                if(marcado){
                    holder.cuadro.setBackgroundColor(Color.parseColor("#58FA82"));
                }else{
                    holder.cuadro.setBackgroundColor(Color.rgb(255,255,255));
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return listaElementos.size();
    }

    public class ViewHolderLista extends RecyclerView.ViewHolder {

        TextView labelNombre, labelCantidad;
        CheckBox checkBoxComprado;
        View cuadro;

        public ViewHolderLista(View itemView) {
            super(itemView);

            labelNombre=itemView.findViewById(R.id.labelNombre);
            labelCantidad=itemView.findViewById(R.id.labelCantidad);
            checkBoxComprado=itemView.findViewById(R.id.checkBoxComprado);
            cuadro=itemView.findViewById(R.id.cuadro);

        }
    }
}
