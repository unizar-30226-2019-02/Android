package es.unizar.eina.ebrozon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;

import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.content.SharedPreferences;

import android.widget.PopupWindow;

import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import es.unizar.eina.ebrozon.lib.Common;
import es.unizar.eina.ebrozon.lib.Ventas;


public class PantallaPrincipal extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    SharedPreferences sharedpreferences;
    private SwipeRefreshLayout swipeLayout;

    private String un;
    private String cor;
    private String pr;
    private String im;

    private Ventas productos; //develop

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

          sharedpreferences = getSharedPreferences(Common.MyPreferences, Context.MODE_PRIVATE);

        recuperarUsuario();
        listarProductosCiudad(pr);

        // Refresh
        swipeLayout = findViewById(R.id.listaProductosRefresh);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() { // Cada vez que se realiza el gesto para refrescar
                listarProductosCiudad(pr);
                swipeLayout.setRefreshing(false);
            }
        });

        // Filtros
        Button botonFiltros = findViewById(R.id.principal_filtros);
        botonFiltros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PantallaPrincipal.this, Filtros.class));
            }
        });

        // Subir producto
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.principal_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(view);
            }
        });

        // Menu hamburguesa

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.drawerMenu);
        navigationView.setNavigationItemSelectedListener(this);

        // Parte de arriba del menu hamburguesa
        View menuArriba = navigationView.getHeaderView(0);
        TextView menuNombre = (TextView) menuArriba.findViewById(R.id.menuNombre);
        menuNombre.setText(un);
        TextView menuCorreo = (TextView) menuArriba.findViewById(R.id.menuCorreo);
        menuCorreo.setText(cor);
        if (im != null && !im.equals("")) {
            ImageView menuImagen = (ImageView) menuArriba.findViewById(R.id.menuImagen);
            //menuImagen.setImageDrawable();
        }
    }

    private void recuperarUsuario() {
        Map<String, ?> m = sharedpreferences.getAll();

        un = (String) m.get(Common.un);
        // Petición de recuperar usuario (un) y actualizar los demás datos
        cor = (String) m.get(Common.cor);
        pr = (String) m.get(Common.pr);
        im = (String) m.get(Common.im);

        if (pr == null || pr.equals("")) {
            pr = "Zaragoza";
        }
    }

    private void gestionarRespuesta(String response) {
        response = response.replace("[","").replace("]","");
        response = response.replace("{","").replace("\"","");

        String[] listaProductos = response.split("\\}");

        for (int i=1; i<listaProductos.length; i++) {
            listaProductos[i] = listaProductos[i].substring(1);
        }

        productos = new Ventas();
        String[] producto;

        for (int i=0; i<listaProductos.length; i++) {
            producto = listaProductos[i].split(",");
            for (int j=0; j<producto.length; j++) {
                producto[j] = producto[j].split(":", 2)[1];
                if (producto[j].equals("null")) producto[j] = "";
            }
            productos.anyadirVenta(producto);
        }

        listarProductos();
    }

    private void gestionarListar(String urlPetition) {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest postRequest = new StringRequest(Request.Method.POST, urlPetition,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        gestionarRespuesta(response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", "Error al recibir la lista de productos");
                    }
                }
        );
        queue.add(postRequest);
    }

    private void listarProductosCiudad(String ciudad) {
        gestionarListar(Common.url + "/listarProductosCiudad?ci=" + ciudad);
    }

    private void listarProductosUsuario(String usuario) {
        gestionarListar(Common.url + "/listarProductosUsuario?un=" + usuario);
    }

    private void listarProductos() {
        String[] from = productos.getResumenAtributos();
        int[] to = {R.id.ProductoResumenTitulo, R.id.ProductoResumenPrecio,
                R.id.ProductoResumenDescripcion, R.id.ProductoResumenImagen};

        SimpleAdapter simpleAdapter = new SimpleAdapter(getBaseContext(), productos.getResumenes(), R.layout.content_producto_resumen, from, to);
        final ListView androidListView = (ListView) findViewById(R.id.listaProductos);
        androidListView.setAdapter(simpleAdapter);

        androidListView.setClickable(true);
        androidListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                HashMap<String, String> venta = productos.getVenta(position);

                Intent intent = new Intent(PantallaPrincipal.this, Producto.class);
                intent.putExtra("Venta", venta);
                startActivity(intent);
            }
        });
    }



    public void showPopup(View view){

        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_subir_producto, null);

        final PopupWindow popupWindow = new PopupWindow(popupView,-1,-2,true);
        popupWindow.showAtLocation(view, Gravity.BOTTOM, 20, 20);

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }

    public void subirProducto(View view){
         startActivity(new Intent(this, SubirProducto.class));
    }

    public void subirSubasta(View view){
        startActivity(new Intent(this, SubirSubasta.class) );
    }


    @Override
    public void onBackPressed() {
        //mantener en esta paginna
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
           // super.onBackPressed();
        }
    }

    public void logout(){
        SharedPreferences    sharedpreferences = getSharedPreferences(Common.MyPreferences, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.commit();
        startActivity(new Intent(PantallaPrincipal.this, PreLogin.class));
    }

    @SuppressWarnings("StatementWithEmptyBody")
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_principal) {
            // Handle the camera action
        } else if (id == R.id.nav_ajustes) {

        } else if (id == R.id.nav_ayuda) {

        } else if (id == R.id.nav_en_venta) {

        } else if (id == R.id.nav_siguiendo) {

        } else if (id == R.id.nav_mensajes) {

        }else if (id == R.id.nav_cerrar_sesion) {
            logout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
