package com.gastonnina.pomodrive.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gastonnina.pomodrive.R;

public class ListaNormalAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private ArrayList<Integer> imagenes;
	private ArrayList<String> textosPrincipales;
	private ArrayList<String> textosSecundarios;

	public ListaNormalAdapter(Context contexto) {
		inflater = LayoutInflater.from(contexto);
		imagenes = new ArrayList<Integer>();
		textosPrincipales = new ArrayList<String>();
		textosSecundarios = new ArrayList<String>();
	}

	public int getCount() {
		return textosPrincipales.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_list, null);
			holder = new ViewHolder();
			holder.imagen = (ImageView) convertView
					.findViewById(R.id.imagenItem);
			holder.titulo = (TextView) convertView
					.findViewById(R.id.textoPrincipalItem);
			holder.subtitulo = (TextView) convertView
					.findViewById(R.id.textoSecundarioItem);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.imagen.setImageResource(imagenes.get(position));
		holder.titulo.setText(textosPrincipales.get(position));
		holder.subtitulo.setText(textosSecundarios.get(position));
		return convertView;
	}

	static class ViewHolder {
		ImageView imagen;
		TextView titulo;
		TextView subtitulo;
	}

	public void adicionarItem(int idRecurso, String textoPrincipal,
			String textoSecundario) {
		imagenes.add(idRecurso);
		textosPrincipales.add(textoPrincipal);
		textosSecundarios.add(textoSecundario);
		notifyDataSetChanged();
	}

	public void adicionarItem(String textoPrincipal, String textoSecundario) {
		imagenes.add(0);
		textosPrincipales.add(textoPrincipal);
		textosSecundarios.add(textoSecundario);
	}

	public void adicionarItem(String textoPrincipal) {
		imagenes.add(0);
		textosPrincipales.add(textoPrincipal);
		textosSecundarios.add("");
	}

	public void eliminarTodo() {
		imagenes.clear();
		textosPrincipales.clear();
		textosSecundarios.clear();
		notifyDataSetChanged();
	}

}
