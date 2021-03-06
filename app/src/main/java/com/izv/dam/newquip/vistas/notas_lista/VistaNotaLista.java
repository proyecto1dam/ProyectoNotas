package com.izv.dam.newquip.vistas.notas_lista;

import android.Manifest;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.izv.dam.newquip.R;
import com.izv.dam.newquip.adaptadores.AdaptadorItemNotaLista;
import com.izv.dam.newquip.adaptadores.AdaptadorNota;
import com.izv.dam.newquip.basedatos.AyudanteOrm;
import com.izv.dam.newquip.contrato.ContratoBaseDatos;
import com.izv.dam.newquip.contrato.ContratoNotaLista;
import com.izv.dam.newquip.databinding.ActivityNotaListaBinding;
import com.izv.dam.newquip.pojo.ItemNotaLista;
import com.izv.dam.newquip.pojo.MarcaNota;
import com.izv.dam.newquip.pojo.Nota;
import com.izv.dam.newquip.util.ImprimirPDF;
import com.izv.dam.newquip.util.ObtenedorDeLocalizacionActual;
import com.izv.dam.newquip.util.Permisos;
import com.izv.dam.newquip.util.PreferenciasCompartidas;
import com.izv.dam.newquip.util.UtilArray;
import com.izv.dam.newquip.util.UtilFecha;
import com.izv.dam.newquip.vistas.Usuarios.VistaMapa;
import com.izv.dam.newquip.vistas.main.VistaQuip;
import com.izv.dam.newquip.vistas.notas.VistaNota;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import android.databinding.DataBindingUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by alumno on 25/10/2016.
 */




public class VistaNotaLista extends AppCompatActivity implements ContratoNotaLista.InterfaceVista, LoaderManager.LoaderCallbacks<Cursor>{

    //private EditText titulo;
    //private RecyclerView items;
    //private FloatingActionButton anadirItem;
    //private Nota nota = new Nota(Nota.TIPO_LISTA);

    public  static  ContratoNotaLista.InterfaceVista  LISTA_ACTUAL;
    private PresentadorNotaLista presentador;
    private AdaptadorItemNotaLista adaptador;
    //private TextInputLayout til_titulo;
    private  ActivityNotaListaBinding binding;
    private  ObtenedorDeLocalizacionActual odla;
    private boolean marcarDesmarcar;

    boolean  listaGuardada; //variable usada para evitar duplicados

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_nota_lista);
        //setContentView(R.layout.activity_nota_lista);



        int orientacion = getResources().getConfiguration().orientation;

        if(orientacion== Configuration.ORIENTATION_LANDSCAPE ) {
            binding.notaListaTilTitulo.setPadding(300,16,300,16);
            binding.notaListaItems.setPadding(300,16,300,16);
        }

        marcarDesmarcar=true;
        listaGuardada = false;
        PreferenciasCompartidas prefs = new PreferenciasCompartidas(this);
        binding.notaListaAnadirItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItemNotaLista item = new ItemNotaLista();
                adaptador.añadirItem(item);
                //items.request
            }
        });
        presentador = new PresentadorNotaLista(this);

        RecyclerView items = binding.notaListaItems;
        items.setHasFixedSize(false); // true, la lista es estatica, false, los datos de la lista pueden variar.
        items.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)); // forma en que se visualizan los elementos, en este caso en vertical.
        adaptador = new AdaptadorItemNotaLista();
        items.setAdapter(adaptador); //TIENES QUE ASIGNAR EL ADAPTADOR AL RECYCLER ANTES DE EJECUTAR CUALQUIER METODO CON NOTIFYDATASETCHANGED


        Nota nota=new Nota(Nota.TIPO_LISTA);

        boolean enabled = false; //ESTO DETERMINARA, SI NADA MÁS ABRIR LA NOTA ESTA SERA EDITABLE O NO
        if (savedInstanceState != null) {
            nota = savedInstanceState.getParcelable("nota");
            enabled = savedInstanceState.getBoolean("enabled");
            ArrayList<ItemNotaLista> lista = savedInstanceState.getParcelableArrayList("lista");
            ArrayList<ItemNotaLista> borrados = savedInstanceState.getParcelableArrayList("borrados");
            adaptador.setLista(lista);
            adaptador.setBorrados(borrados);
            binding.setNota(nota);
        } else {
            Bundle b = getIntent().getExtras();
            if(b != null ) {
                nota = b.getParcelable("nota");
                enabled=prefs.isPrefsEditable();
            }
            else {
                //this.window
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                enabled=true;
            }
            binding.setNota(nota);
            getSupportLoaderManager().initLoader(0,null,this);
            //cargarItems(presentador.getItems(nota.getId()));
        }

        cambiarEditable(enabled);


        binding.notaListaTitulo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                binding.notaListaTilTitulo.setError(null); //esto hace que al escribir se pierda el error de no titulo.
            }
        });
    }
    @Override
    public void cargarItems(Cursor c) {
        cargarItems(ItemNotaLista.CursorToArrayListItemNotaLista(c));
    }

    @Override
    public void cargarItems(ArrayList<ItemNotaLista> l) {
        adaptador.setLista(l);
    }




    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("nota", binding.getNota());

        List<ItemNotaLista> list = adaptador.getLista();
        List<ItemNotaLista> borrados = adaptador.getBorrados();
        outState.putParcelableArrayList("lista", UtilArray.listToArrayList(list));
        outState.putParcelableArrayList("borrados", UtilArray.listToArrayList(borrados));
        outState.putBoolean("enabled",binding.notaListaTitulo.isEnabled());
    }


    @Override
    protected void onPause() {
        PreferenciasCompartidas prefs = new PreferenciasCompartidas(this);
        if (prefs.isPrefsGuardar()&& !listaGuardada) {
            String textoTitulo = binding.getNota().getTitulo().trim();
            if (textoTitulo.isEmpty()) { //comprobar que el titulo esta vacio
                if (!prefs.isPrefsTitulo()) { // si el titulo esta vacio, hay que comprobar que el usuario admite titulos vacios.
                    guardarLista(textoTitulo);
                }
            }
            else {
                guardarLista(textoTitulo);
            }
        }
        listaGuardada=false;
        presentador.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        presentador.onResume();
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_nota_lista,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_nota_lista_editar : {
                cambiarEditable(!binding.notaListaTitulo.isEnabled());
                return true;
            }

            case R.id.menu_nota_lista_guardar: {

                PreferenciasCompartidas prefs = new PreferenciasCompartidas(this);
                String textoTitulo = binding.getNota().getTitulo().trim();
                if (textoTitulo.isEmpty() && prefs.isPrefsTitulo()) {
                    binding.notaListaTilTitulo.setError(getResources().getString(R.string.errorTituloNoEscrito));
                }
                else {

                    guardarLista(textoTitulo);
                    listaGuardada=true;
                    finish();
                }
                return true;
            }

            case R.id.menu_nota_lista_borrar_items :{
                adaptador.borrarListaCompleta();
                return  true;
            }

            case R.id.menu_nota_lista_vaciar_items : {
                adaptador.vaciarContenitoTodosItems();
                return true;
            }

            case R.id.menu_nota_lista_marcar_items : {

                if (adaptador.comprobarTodosMarcados()) {
                    adaptador.marcarDesmarcarTodosCheckBox(false);
                }
                else if (adaptador.comprobarTodosDesmarcados()) {
                    adaptador.marcarDesmarcarTodosCheckBox(true);
                }
                else {
                    adaptador.marcarDesmarcarTodosCheckBox(marcarDesmarcar);
                    marcarDesmarcar = !marcarDesmarcar;
                }
                return true;
            }

            case R.id.menu_nota_lista_invertir_items : {
                adaptador.invertirCheckBox();
                return true;
            }

            case R.id.menu_nota_lista_imprimir_pdf : {

                if (Permisos.solicitarPermisos(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},this)) {

                    Nota nota = binding.getNota();
                    nota.setTitulo(nota.getTitulo().trim());
                    if((nota.getTitulo().isEmpty() || nota.getTitulo() == null) && adaptador.getLista().isEmpty()){
                        Toast.makeText(getApplicationContext(), getString(R.string.listaVaciaPdf), Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), getString(R.string.imprimirLista), Toast.LENGTH_SHORT).show();
                        ImprimirPDF.imprimirLista(this, nota,adaptador.getLista());
                    }
                }
                return true;
            }

            case R.id.menu_lista_map : {
                long id =binding.getNota().getId();
                if (id!=0) {
                    Intent i = new Intent(this, VistaMapa.class);
                    Bundle b = new Bundle();
                    b.putLong("id_nota",id);
                    i.putExtras(b);
                    startActivity(i);
                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
                }
                else {
                    Toast.makeText(this,R.string.listaSinMarcas, Toast.LENGTH_SHORT).show();
                }
                return true;
            }

            case android.R.id.home : {
                finish();
                return true;
            }
            default: {
                return false;
            }
        }
    }

    private void cambiarEditable(boolean editable) {

        adaptador.setViewAreEnabled(editable);
        if (editable)
            binding.notaListaAnadirItem.setVisibility(View.VISIBLE);
        else
            binding.notaListaAnadirItem.setVisibility(View.GONE);
        binding.notaListaTitulo.setEnabled(editable);
    }


    //LOAD MANAGER
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) { // HACE LA CONSULTA CON LOS DATOS,el ID ES EL ID A USAR DEL MANAGER
        String uri = ContratoBaseDatos.TablaItemNotaLista.URI_ITEM_NOTA_LISTA+"/"+ContratoBaseDatos.TablaNota.TABLA+"/"+String.valueOf(binding.getNota().getId());//URI PARA BUSCAR LOS ELEMENTOS DE UNA LISTA
        return new CursorLoader(this, Uri.parse(uri), ContratoBaseDatos.TablaItemNotaLista.PROJECTION_ALL, null, null, ContratoBaseDatos.TablaItemNotaLista.SORT_ORDER_DEFAULT);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) { //ASIGNA EL CURSOR A EL ADAPTADOR
        cargarItems(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { // ELIMINA LA REFERENCIA DE DATOS Y DEVUELVE EL ADAPTADOR A SU ESTADO INICIAL
        cargarItems((new ArrayList<ItemNotaLista>()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            getLoaderManager().destroyLoader(0);
            if (adaptador != null) {
                adaptador.setLista(new ArrayList<ItemNotaLista>());
                adaptador = null;
            }
        } catch (Throwable localThrowable) {
            // Proyectar la excepción
            localThrowable.printStackTrace();
        }
    }
    //FIN LOAD MANAGER

    private void guardarLista(String titulo) {

            adaptador.trimTextosLista();
           // List<ItemNotaLista> lista = adaptador.getLista();
            binding.getNota().setTitulo(titulo);//TRIM
            if (!(titulo==null || titulo.isEmpty())||!adaptador.getLista().isEmpty()) {
                presentador.onSaveNota(binding.getNota(), adaptador.getLista(), adaptador.getBorrados());
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.guardarListaPart1)+" "+titulo+" "+getResources().getString(R.string.guardarNotaPart2),
                        Toast.LENGTH_SHORT).show();
            }

        if (binding.getNota().getId()!=0)
            marcarLocalizacionLista();

    }

    public void marcarLocalizacionLista() {

         if (Permisos.solicitarPermisos(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, this)) {
             System.out.println("Si entra en el metodo");
             new AsyncTask<Void, Void, Void>() {
                 @Override
                 protected Void doInBackground(Void... params) {
                     //AÑADIDO MARCAS
                     try {
                         AyudanteOrm helper = OpenHelperManager.getHelper(VistaNotaLista.this, AyudanteOrm.class);
                         Dao dao = helper.getMarcaNotaDao();
                         //OBTENCION DATOS MARCA
                         String texto;
                         Nota n = binding.getNota();
                         if (n.getTitulo() != null && !n.getTitulo().trim().isEmpty())
                             texto = n.getTitulo().trim();
                         else
                             texto = getString(R.string.listaSinTexto);
                         Location l = odla.getLocalizacion();
                         //------------------------------------------------------------------------
                         MarcaNota marca = new MarcaNota(texto, new Date(), l.getLatitude(), l.getLongitude(), n.getId());
                         Log.v("MARCA", marca.toString());
                         dao.create(marca);


                     } catch (SQLException e) {

                         e.printStackTrace();
                     } catch (NullPointerException e) {

                         e.printStackTrace();
                     } finally {
                         try {
                             odla.desconectar();
                         }
                         catch (NullPointerException e) {}
                     }
                     return null;
                 }
             }.execute();
         }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Permisos.solicitarPermisos(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, this)) {
            odla = new ObtenedorDeLocalizacionActual(this);
        }
        LISTA_ACTUAL=this;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.zoom_fordward_in, R.anim.zoom_fordward_out);
    }
}
