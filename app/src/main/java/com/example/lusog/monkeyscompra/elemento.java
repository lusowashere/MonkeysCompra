package com.example.lusog.monkeyscompra;

/**
 * Created by lusog on 15/02/2018.
 */

public class elemento extends Object {
    public String Cantidad; //pongo string para que se pueda poner "una docena, una caja... etc"
    public String Nombre;
    public String Tipo;
    public boolean comprado,agotado,urgente;
    public String listaElemento; //lista en la que se está usando el elemento (puede ser "anteriores" o "compra". Según lo que sea el string es uno u otro
    public String fecha,fechaCompra,fechaApuntado;

    public elemento(String Cantidad,String Nombre,String Tipo, String listaElemento){
        this.Cantidad=Cantidad;
        this.Nombre=Nombre;
        this.Tipo=Tipo;
        this.listaElemento=listaElemento;
        comprado=false;//si no digo nada, entiendo que no está comprado
        fecha="";
        fechaCompra="";
        fechaApuntado="";
        agotado=false;
        urgente=false;
    }

    public elemento(String Cantidad, String Nombre, String Tipo, Boolean comprado, String listaElemento){
        this.Cantidad=Cantidad;
        this.Nombre=Nombre;
        this.Tipo=Tipo;
        this.comprado=comprado;
        this.listaElemento=listaElemento;
        fecha="";
        fechaCompra="";
        fechaApuntado="";
        agotado=false;
        urgente=false;
    }

    public elemento(String Cantidad, String Nombre, String Tipo, Boolean comprado){
        this.Cantidad=Cantidad;
        this.Nombre=Nombre;
        this.Tipo=Tipo;
        this.comprado=comprado;
        fecha="";
        fechaCompra="";
        fechaApuntado="";
        agotado=false;
        urgente=false;
    }

    public String descripcion(){ return "Nombre:" +Nombre+"  - Tipo:"+Tipo+"  - Cantidad:"+Cantidad;}


    public void toggleComprado(){
        comprado= !comprado;
    }

    @Override
    public String toString() {

        if(listaElemento=="compra"){
            if(comprado){
                return "COMPRADO:" +  Nombre+" (" + Cantidad + ")";
            }else{
                return Nombre + " (" + Cantidad + ")";
            }
        }else{
            if(!comprado){
                return "APUNTADO:"+ Nombre;
            }else{
                return Nombre;
            }
        }



    }

    public ElementoAux getElementoAux(){//para crear elementos de firebase
        ElementoAux el=new ElementoAux(Cantidad,Nombre,Tipo,comprado,fecha,fechaCompra,fechaApuntado,urgente);
        return el;
    }


    public void setFecha(String nuevaFecha){
        fecha=nuevaFecha;
    }

    public void setFechaCompra(String nuevaFecha){
        fechaCompra=nuevaFecha;
        if(comprado) {
            fecha = nuevaFecha;
        }
    }

    public void setFechaApuntado(String nuevaFecha){
        fechaApuntado=nuevaFecha;
        if(!comprado){
            fecha=nuevaFecha;
        }
    }

    public void setUrgente(boolean esUrgente){
        urgente=esUrgente;
    }

}
