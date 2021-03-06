package com.izv.dam.newquip.pojo;

import android.content.ContentValues;
import android.database.Cursor;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.graphics.BitmapRegionDecoder;
import android.os.Parcel;
import android.os.Parcelable;

import com.android.databinding.library.baseAdapters.BR;
import com.izv.dam.newquip.contrato.ContratoBaseDatos;
import com.izv.dam.newquip.util.UtilBoolean;

public class Nota  extends BaseObservable implements Parcelable {

    //TIPOS DE NOTAS

    public static final int TIPO_DEFECTO= 1;//solo texto.
    public static final int TIPO_IMAGEN= 2;
    public static final int TIPO_DIBUJO= 3; // la diferencia entre este y el de imagen, es que la imagen guardada se ha creado con la aplicación interna de dibujo
    public static final int TIPO_LISTA= 5;


    //Es una forma de hayar rapidamente que con que tipo de nota estamos trabajando, donde mas se usara esto es en el adaptador, para determinar que
    // interfaz abriremos al hacer click en una nota, si el layout: activity_nota o activity_nota_lista
    private long id;
    private String titulo, nota,imagen,audio;
    private int tipo;
    private boolean papelera;


    public Nota() {
        this(0, "", "",null,null,TIPO_DEFECTO);
    } // he puesto el texto y la descripción vacios para evitar conflictos con los metodos .trim

    public Nota(long id, String titulo, String nota, String imagen, String audio,int tipo) {
        this.id = id;
        this.titulo = titulo;
        this.nota = nota;
        this.imagen=imagen;
        this.audio=audio;
        this.tipo=tipo;
        papelera=false;
    }

    public  Nota (int tipo) {
        this(0, "", "",null,null,tipo);
    }

    protected Nota(Parcel in) {
        id = in.readLong();
        titulo = in.readString();
        nota = in.readString();
        imagen = in.readString();
        audio = in.readString();
        tipo = in.readInt();
        papelera = in.readByte()!=0;
    }

    public static final Creator<Nota> CREATOR = new Creator<Nota>() {
        @Override
        public Nota createFromParcel(Parcel in) {
            return new Nota(in);
        }

        @Override
        public Nota[] newArray(int size) {
            return new Nota[size];
        }
    };



    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }



    public void setId(String id) {
        try {
            this.id = Long.parseLong(id);
        } catch(NumberFormatException e){
            this.id = 0;
        }
    }

    public boolean isPapelera() {
        return papelera;
    }

    public void setPapelera(boolean papelera) {
        this.papelera = papelera;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    @Bindable
    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
        notifyPropertyChanged(BR.imagen);
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public ContentValues getContentValues(){
        return this.getContentValues(false);
    }

    public ContentValues getContentValues(boolean withId){
        ContentValues valores = new ContentValues();
        if(withId){
            valores.put(ContratoBaseDatos.TablaNota._ID, this.getId());
        }
        valores.put(ContratoBaseDatos.TablaNota.TITULO, this.getTitulo());
        valores.put(ContratoBaseDatos.TablaNota.NOTA, this.getNota());
        valores.put(ContratoBaseDatos.TablaNota.IMAGEN,this.getImagen());
        valores.put(ContratoBaseDatos.TablaNota.AUDIO,this.getAudio());
        valores.put(ContratoBaseDatos.TablaNota.TIPO,this.getTipo());
        valores.put(ContratoBaseDatos.TablaNota.PAPELERA,this.isPapelera());
        return valores;
    }

    public static Nota getNota(Cursor c){
        Nota objeto = new Nota();
        objeto.setId(c.getLong(c.getColumnIndex(ContratoBaseDatos.TablaNota._ID)));
        objeto.setTitulo(c.getString(c.getColumnIndex(ContratoBaseDatos.TablaNota.TITULO)));
        objeto.setNota(c.getString(c.getColumnIndex(ContratoBaseDatos.TablaNota.NOTA)));
        objeto.setImagen(c.getString(c.getColumnIndex(ContratoBaseDatos.TablaNota.IMAGEN)));
        objeto.setAudio(c.getString(c.getColumnIndex(ContratoBaseDatos.TablaNota.AUDIO)));
        objeto.setTipo(c.getInt(c.getColumnIndex(ContratoBaseDatos.TablaNota.TIPO)));
        objeto.setPapelera(c.getInt(c.getColumnIndex(ContratoBaseDatos.TablaNota.PAPELERA))!=0);
        return objeto;
    }

    @Override
    public String toString() {
        return "Nota{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", nota='" + nota + '\'' +
                ", imagen='" + imagen + '\'' +
                ", audio='" + audio + '\'' +
                ", tipo=" + tipo +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(titulo);
        dest.writeString(nota);
        dest.writeString(imagen);
        dest.writeString(audio);
        dest.writeInt(tipo);
        dest.writeByte(UtilBoolean.booleanToByte(papelera));
    }

    public boolean comprobarNotaVacia() {
        if ((titulo==null || titulo.isEmpty()) && (nota==null || nota.isEmpty()) && (imagen==null || imagen.isEmpty()) && (audio==null || audio.isEmpty())) {
            return true;
        }
        return false;
    }

    public boolean comprobarNotaVaciaParaPDF() {
        if ((titulo==null || titulo.isEmpty()) && (nota==null || nota.isEmpty()) && (imagen==null || imagen.isEmpty())) {
            return true;
        }
        return false;
    }
}