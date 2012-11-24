package com.gastonnina.pomodrive;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.gastonnina.pomodrive.adapters.ListaNormalAdapter;
import com.gastonnina.pomodrive.db.PomodoroDatabaseAdapter;
/**
 * 
 * @author Gaston Nina
 *
 */
public class ListActivity extends Activity implements OnItemClickListener {
	// Lista
	private ListView lista;
	private ListaNormalAdapter adaptadorLista;
	// DB
	private PomodoroDatabaseAdapter db;
	// IDs
	private ArrayList<Long> ids = new ArrayList<Long>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);

		lista = (ListView) findViewById(R.id.taskList);
		lista.setOnItemClickListener(this);
		adaptadorLista = new ListaNormalAdapter(this);
		lista.setAdapter(adaptadorLista);
		registerForContextMenu(lista);

		db = new PomodoroDatabaseAdapter(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		db.abrir();
		cargarDatosLista();
	}

	public void cargarDatosLista() {
		// Limpiamos la lista
		ids.clear(); // Borramos la lista
		adaptadorLista.eliminarTodo(); // Borramos en contenido de la ListView
		Cursor cur = db.getAllPomodoros();
		if (cur.moveToFirst()) {
			do {
				int id = cur.getInt(0);
				String name = cur.getString(1);
				String type = cur.getString(2);
				int pomodoros = cur.getInt(3);
				ids.add((long) id);//array de posiciones
				
				//if (sexo.equals("m")) {
					adaptadorLista.adicionarItem(R.drawable.ico_undone,
							name, type+ "\n" + pomodoros);
				/*} else {
					adaptadorLista.adicionarItem(R.drawable.ic_action_mujer,
							nombre, correo + "\n" + telefono);
				}*/
			} while (cur.moveToNext());
		}
		adaptadorLista.notifyDataSetChanged();
	}

	public void onItemClick(AdapterView<?> arg0, View view, int position,
			long id) {
		/*Intent intent = new Intent(this, PersonaActivity.class);
		intent.putExtra("id", ids.get(position));//para enviar nuevo dato activity
		startActivity(intent);*/
	}

	// MENU OPCIONES **************************************************
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		/*switch (item.getItemId()) {
		case R.id.menu_adicionar:
			Intent intent = new Intent(this, FormularioActivity.class);
			startActivity(intent);
			break;
		}*/
		return super.onOptionsItemSelected(item);
	}

	// MENU CONTEXTO **************************************************
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		//--getMenuInflater().inflate(R.menu.item_lista, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo(); // Para obtener que item se presiono (posici�n
								// item)
		int index = info.position;
		/*switch (item.getItemId()) {
		case R.id.menu_editar:
			Intent intent = new Intent(this, FormularioActivity.class);
			intent.putExtra("id", ids.get(index));
			startActivity(intent);
			break;
		case R.id.menu_eliminar:
			db.eliminarPersona(ids.get(index));
			ids.remove(index);
			cargarDatosLista();
			break;
		}*/
		return super.onContextItemSelected(item);
	}

	@Override
	protected void onStop() {
		super.onStop();
		db.cerrar();
	}
}

