package com.izv.dam.newquip.adaptadores;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.izv.dam.newquip.R;
import com.izv.dam.newquip.pojo.Nota;

import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;
import java.util.List;


public class AdaptadorNota  extends RecyclerView.Adapter<AdaptadorNota.ViewHolder> implements View.OnClickListener,View.OnLongClickListener{


    private Cursor cursor;
    private View.OnClickListener clickListener;
    private View.OnLongClickListener longClickListener;



    public AdaptadorNota(Cursor cursor) {
        this.cursor = cursor;
    }


    public AdaptadorNota(){
        this(null);
    }



    @Override
    public void onClick(View v) {
        if(clickListener != null)
            clickListener.onClick(v);
    }

    @Override
    public boolean onLongClick(View v) {
        if(longClickListener != null) {
            longClickListener.onLongClick(v);
            return true;
        }
        return false;
    }

    public class ViewHolder extends  RecyclerView.ViewHolder { //USADO PARA LAS NOTAS NORMALES
        private TextView tvTituloNota;
        private TextView tvRecordatorio;
        private ImageView ivTipoNota;
        private ImageView ivRecordatorio;
        public ViewHolder(View itemView) { // aqui irian todos los elementos del layout
            super(itemView);
            tvTituloNota = (TextView) itemView.findViewById(R.id.tvTituloNota);
            tvRecordatorio = (TextView) itemView.findViewById(R.id.tvFechaRecordatorioNota);
            ivTipoNota = (ImageView) itemView.findViewById(R.id.imagenTipoNota);
            ivRecordatorio = (ImageView) itemView.findViewById(R.id.imagenRecordatorio);
        }
        public TextView getTextView(){
            return tvTituloNota;
        }

        public TextView getTvRecordatorio() {
            return tvRecordatorio;
        }

        public ImageView getIvTipoNota() {
            return ivTipoNota;
        }

        public TextView getTvTituloNota() {
            return tvTituloNota;
        }

        public ImageView getIvRecordatorio() {
            return ivRecordatorio;
        }
    }



    public void setOnClickListener(View.OnClickListener listener) {
        this.clickListener = listener;
    }

    public void setLongClickListener(View.OnLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }

    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
        notifyDataSetChanged();
    }

    @Override
    public AdaptadorNota.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) { // esto es el layout por asi decirlo de cada item
        LayoutInflater inflador = LayoutInflater.from(parent.getContext());// el contexto se puede pasar tambien por el constructor.
        View vista = inflador.inflate(R.layout.item,parent,false); // el ultimo parametro no estoy seguro de como funciona, en false evita duplicados, pero hay que investigarlo mas
        vista.setOnClickListener(this);
        vista.setOnLongClickListener(this);
        return new ViewHolder(vista);// devolvemos un objeto de nuestra clase ViewHolder con la vista que acabamos de inflar.
    }

    @Override
    public void onBindViewHolder(AdaptadorNota.ViewHolder holder, int position) { // aqui se le asignan los valores a  los elementos de los item , en este caso solo es 1cur;
        if (cursor.moveToPosition(position)) {
            Nota nota = Nota.getNota(cursor);
            holder.getTextView().setText(nota.getTitulo());
            Log.v("TIPO",nota.getTipo()+"");
            switch (nota.getTipo()) { //AQUI CAMBIAREMOS LA IMAGEN QUE SE MUESTRA PARA QUE EL USUARIO SEPA A 1ª VISTA QUE TIPO DE NOTA ES.
                case Nota.TIPO_DEFECTO: {
                    holder.getIvTipoNota().setImageResource(R.drawable.ic_tipo_defecto48px);
                    break;
                }
                case Nota.TIPO_IMAGEN: {
                    holder.getIvTipoNota().setImageResource(R.drawable.ic_tipo_imagen48px);
                    break;
                }
                case Nota.TIPO_DIBUJO: {
                    holder.getIvTipoNota().setImageResource(R.drawable.ic_tipo_dibujo48px);
                    break;
                }
                case Nota.TIPO_AUDIO: {
                    break;
                }
                case Nota.TIPO_LISTA: {
                    holder.getIvTipoNota().setImageResource(R.drawable.ic_tipo_lista48px);
                    break;
                }
            }
            //ESPACIO PARA RECORDATORIO, IMPLEMENTAR MÁS ADELANTE
        }
    }

    @Override
    public int getItemCount() {
        if (cursor==null) {
            return 0;
        }
        return cursor.getCount();
    }

    @Override
    public int getItemViewType(int position) {
        if (cursor.moveToPosition(position)) {
            return Nota.getNota(cursor).getTipo(); //devolvemos el tipo que tiene guardado la nota.
        }
        return -1;
    }
}



//VERSION ANTIGUA ADAPTADOR PARA LISTVIEW
/*public class AdaptadorNota extends CursorAdapter {

    public AdaptadorNota(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tv = (TextView) view.findViewById(R.id.tvTituloNota);
        Nota n = Nota.getNota(cursor);
        tv.setText(n.getTitulo());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item, parent, false);
    }
}*/