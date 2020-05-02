package com.example.lusog.monkeyscompra;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
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
        if(listaElementos.get(position).fechaApuntado!=""){
            holder.labelFecha.setText(listaElementos.get(position).fechaApuntado);
        }else {
            holder.labelFecha.setText(listaElementos.get(position).fecha);
        }
        if(listaElementos.get(position).comprado){
            holder.checkBoxComprado.setChecked(true);
            holder.cuadro.setBackgroundColor(Color.parseColor("#58FA82"));
        }else{
            holder.checkBoxComprado.setChecked(false);

            if (listaElementos.get(position).agotado) {
                holder.cuadro.setBackgroundColor(Color.parseColor("#ff9999"));
            } else {
                //si no está comprado, miro si es urgente y lo pinto de amarillo si no está agotado
                if(listaElementos.get(position).urgente){
                    holder.cuadro.setBackgroundColor(Color.parseColor("#FACC2E"));
                }else{
                    holder.cuadro.setBackgroundColor(Color.parseColor("#E6E6E6"));
                }
            }

        }

        //si es urgente muestro el dibujo de la exclamación
        if(listaElementos.get(position).urgente){
            holder.imagenUrgente.setVisibility(View.VISIBLE);
        }else{
            holder.imagenUrgente.setVisibility(View.GONE);
        }

        holder.checkBoxComprado.setOnClickListener( new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                boolean marcado=holder.checkBoxComprado.isChecked();
                listaElementos.get(position).comprado=marcado;

                if(marcado){
                    holder.cuadro.setBackgroundColor(Color.parseColor("#58FA82"));
                    //referencia.child("elementos").child(listaElementos.get(position).Nombre).child("Fecha").setValue("comprado el "+get_dia_y_hora_actual());
                    referencia.child("elementos").child(listaElementos.get(position).Nombre).child("FechaCompra").setValue("comprado el "+get_dia_y_hora_actual());

                }else{
                    holder.cuadro.setBackgroundColor(Color.parseColor("#E6E6E6"));
                    //referencia.child("elementos").child(listaElementos.get(position).Nombre).child("Fecha").setValue("Apuntado el "+get_dia_y_hora_actual());
                }

                referencia.child("elementos").child(listaElementos.get(position).Nombre).child("comprado").setValue(marcado);
                referencia.child("ultimaModificacion").setValue(get_dia_y_hora_actual());

            }
        });

        holder.cuadro.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                 boolean estaAgotado=listaElementos.get(position).agotado;
                 boolean esUrgente=listaElementos.get(position).urgente;

                 AlertDialog.Builder builderAlerta=new AlertDialog.Builder(view.getContext());
                 builderAlerta.setTitle("Marcar como");





                 String[] posibilidades={"urgente","agotado"};
                 boolean[] valores={esUrgente,estaAgotado};
                 builderAlerta.setMultiChoiceItems(posibilidades, valores, new DialogInterface.OnMultiChoiceClickListener() {
                     @Override
                     public void onClick(DialogInterface dialogInterface, int i, boolean checked) {
                        switch (i){
                            case 0:
                                referencia.child("elementos").child(listaElementos.get(position).Nombre).child("urgente").setValue(checked);
                                break;
                            case 1:
                                listaElementos.get(position).agotado = checked;
                                if (checked) {
                                    holder.cuadro.setBackgroundColor(Color.parseColor("#ff9999"));
                                } else {
                                    holder.cuadro.setBackgroundColor(Color.parseColor("#E6E6E6"));
                                }
                                break;
                            default:
                                break;
                        }
                     }
                 });


                final EditText textBoxCantidad=new EditText(view.getContext());
                textBoxCantidad.setText(listaElementos.get(holder.getLayoutPosition()).Cantidad);
                builderAlerta.setView(textBoxCantidad);

                final int posicion=holder.getLayoutPosition();

                 builderAlerta.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialogInterface, int i) {
                         String textoCantidad=textBoxCantidad.getText().toString().trim();
                         if(textoCantidad.compareTo( listaElementos.get(posicion).Cantidad)!=0){
                             //System.out.println("cambia la cantidad");
                             referencia.child("elementos").child(listaElementos.get(posicion).Nombre).child("Cantidad").setValue(textoCantidad);
                         }
                         dialogInterface.dismiss();
                     }
                 });

                 builderAlerta.create().show();

                /*
                final boolean estaAgotado=listaElementos.get(position).agotado;
                if(!listaElementos.get(position).comprado) {
                    AlertDialog.Builder builderAlerta = new AlertDialog.Builder(view.getContext());
                    if (estaAgotado) {
                        builderAlerta.setMessage("¿Desmarcar elemento como agotado?");
                    } else {
                        builderAlerta.setMessage("¿marcar elemento como agotado?");
                    }
                    builderAlerta.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            listaElementos.get(position).agotado = !estaAgotado;
                            if (listaElementos.get(position).agotado) {
                                holder.cuadro.setBackgroundColor(Color.parseColor("#ff9999"));
                            } else {
                                holder.cuadro.setBackgroundColor(Color.parseColor("#E6E6E6"));
                            }
                            dialogInterface.dismiss();
                        }
                    });
                    builderAlerta.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });

                    builderAlerta.create().show();
                }*/
                return false;
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
        ImageView imagenUrgente;
        View cuadro;

        public ViewHolderLista(View itemView) {
            super(itemView);

            labelNombre=itemView.findViewById(R.id.labelNombre);
            labelCantidad=itemView.findViewById(R.id.labelCantidad);
            labelFecha=itemView.findViewById(R.id.labelFecha);
            checkBoxComprado=itemView.findViewById(R.id.checkBoxComprado);
            cuadro=itemView.findViewById(R.id.cuadro);
            imagenUrgente=itemView.findViewById(R.id.imagenUrgente);
        }
    }

    public String get_dia_y_hora_actual(){
        Calendar cal = Calendar.getInstance();
        Date date=cal.getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/YY HH:mm");
        String formattedDate=dateFormat.format(date);
        return formattedDate;
    }


}
