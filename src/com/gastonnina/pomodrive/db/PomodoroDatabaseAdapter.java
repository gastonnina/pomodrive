package com.gastonnina.pomodrive.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PomodoroDatabaseAdapter {

	private PomodoroDatabaseHelper databaseHelper;
	private SQLiteDatabase db;

	public PomodoroDatabaseAdapter(Context context) {
		databaseHelper = new PomodoroDatabaseHelper(context);
	}

	public void abrir() {
		db = databaseHelper.getWritableDatabase();
	}

	public void cerrar() {
		databaseHelper.close();
	}

	public long adicionarPersona(String nombre, long telefono, String correo,
			String sexo) {
		ContentValues contentValues = new ContentValues();
		contentValues.put("nombre", nombre);
		contentValues.put("telefono", telefono);
		contentValues.put("correo", correo);
		contentValues.put("sexo", sexo);
		return db.insert("personas", null, contentValues);
	}

	public int actualizarPersona(long id, String nombre, long telefono,
			String correo, String sexo) {
		ContentValues contentValues = new ContentValues();
		contentValues.put("nombre", nombre);
		contentValues.put("telefono", telefono);
		contentValues.put("correo", correo);
		contentValues.put("sexo", sexo);
		return db.update("personas", contentValues, "_id=?", new String[] { id
				+ "" });
	}

	public boolean eliminarPersona(long id) {
		return db.delete("personas", "_id=" + id, null) > 0;
	}

	public Cursor obtenerPersona(long id) {
		return db.query("personas", new String[] { "_id", "nombre", "telefono",
				"correo", "sexo" }, "_id=?", new String[] { id + "" }, null,
				null, null);
	}

	public Cursor obtenerTodasPersonas() {
		return db.query("personas", new String[] { "_id", "nombre", "telefono",
				"correo", "sexo" }, null, null, null, null, null);
	}
	
	public Cursor getAllPomodoros(){
		return db.query("pomodoro", new String[] { "id", "name", "type",
				"pomodoros", "created" }, null, null, null, null, null);
	}

	/**
	 * Clase que determina la estructura de la base de datos.
	 */
	private static class PomodoroDatabaseHelper extends SQLiteOpenHelper {

		public PomodoroDatabaseHelper(Context context) {
			super(context, "pomodrive.db", null, 1);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			//db.execSQL("CREATE TABLE personas (_id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT NOT NULL, telefono INTEGER, correo TEXT, sexo TEXT);");
			db.execSQL("CREATE TABLE pomodoro (id INTEGER PRIMARY KEY ASC, name TEXT, type TEXT, pomodoros INTEGER, unplanned INTEGER, interruptions INTEGER, created DATETIME, closed DATETIME, parent INTEGER, visible BOOLEAN, ordinal INTEGER, done BOOLEAN, estimated INTEGER , \"user\" VARCHAR);");
			db.execSQL("CREATE TABLE config( name TEXT PRIMARY KEY, value TEXT );");
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS pomodoro");
			db.execSQL("DROP TABLE IF EXISTS config");
			onCreate(db);
		}
	}

}

