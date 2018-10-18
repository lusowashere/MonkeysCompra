package com.example.lusog.monkeyscompra;
//clase creada sólo para poder crear de golpe un elemento en firebase
public class ElementoAux {
    public String Cantidad; //pongo string para que se pueda poner "una docena, una caja... etc"
    public String Nombre;
    public String Tipo;
    public boolean comprado;

    public ElementoAux(String Cantidad, String Nombre, String Tipo, boolean comprado){
        this.Cantidad=Cantidad;
        this.Nombre=Nombre;
        this.Tipo=Tipo;
        this.comprado=comprado;
    }


}
