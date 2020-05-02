package com.example.lusog.monkeyscompra;
//clase creada sólo para poder crear de golpe un elemento en firebase
public class ElementoAux {
    public String Cantidad; //pongo string para que se pueda poner "una docena, una caja... etc"
    public String Nombre;
    public String Tipo;
    public boolean comprado;
    public String Fecha;
    public String FechaCompra;
    public String FechaApuntado;
    public boolean urgente;

    public ElementoAux(String Cantidad, String Nombre, String Tipo, boolean comprado,String Fecha, String fechaCompra,String fechaApuntado,boolean urgente){
        this.Cantidad=Cantidad;
        this.Nombre=Nombre;
        this.Tipo=Tipo;
        this.comprado=comprado;
        this.Fecha=Fecha;
        this.FechaCompra=fechaCompra;
        this.FechaApuntado=fechaApuntado;
        this.urgente=urgente;
    }


}
