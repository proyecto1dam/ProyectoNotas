package com.izv.dam.newquip.vistas.main;

import android.content.Context;
import android.database.Cursor;

import com.izv.dam.newquip.contrato.ContratoMain;
import com.izv.dam.newquip.pojo.Nota;
import com.izv.dam.newquip.util.PreferenciasCompartidas;

public class PresentadorQuip implements ContratoMain.InterfacePresentador{

    private ContratoMain.InterfaceModelo modelo;
    private ContratoMain.InterfaceVista vista;
   // private ContratoMain.InterfaceModelo.OnDataLoadListener oyente;

    public PresentadorQuip(ContratoMain.InterfaceVista vista) {
        this.vista = vista;
        this.modelo = new ModeloQuip((Context)vista);
        /*oyente = new ContratoMain.InterfaceModelo.OnDataLoadListener() {
            @Override
            public void setCursor(Cursor c) {
                PresentadorQuip.this.vista.mostrarDatos(c);
            }
        };*/
    }

    @Override
    public void recuperarNota(Nota n) {
        modelo.recuperarNota(n);
    }

    @Override
    public void onAddNota() {
        this.vista.mostrarAgregarNota();
    }

    @Override
    public void onAddNotaLista() {
        this.vista.mostrarAgregarNotaLista();
    }


    @Override
    public void onDeleteNota(Nota n) {
        this.modelo.deleteNota(n);
        //this.modelo.loadData(oyente);
    }

    @Override
    public void onEditNota(Nota n) {
        this.vista.mostrarEditarNota(n);
    }

    @Override
    public void onPause() {
    }

    @Override
    public void onResume() {
        //this.modelo.loadData(oyente);
    }


    public  void borrarConjunto (String where, boolean papelera) {
        this.modelo.borrarConjunto(where,papelera);
    }

    @Override
    public void onShowBorrarNota(int position) {
        Nota n = this.modelo.getNota(position);
        this.vista.mostrarConfirmarBorrarNota(n);
    }

    @Override
    public void onDeleteNota(int position) {
        this.modelo.deleteNota(position);
        //this.modelo.loadData(oyente);
    }

    @Override
    public void onClickNota(int position) {
        Nota n = this.modelo.getNota(position);
        if (n.isPapelera()) {
            vista.mostrarConfirmarRecuperarNota(n);
        }
        else
            this.onEditNota(n);
    }



    public void cargarCursorModelo(Cursor c){
        modelo.setCursor(c);
    }

    public void desconectarUsuario(Context c){
        PreferenciasCompartidas prefs = new PreferenciasCompartidas(c);
        prefs.borrarDatosUsuario();
        //modelo.borrarTodo();
    }
}
