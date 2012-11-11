package com.gastonnina.pomodrive.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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

	public long insertPomodoro(String name, long estimated) {
		ContentValues contentValues = new ContentValues();
		contentValues.put("name", name);
		contentValues.put("estimated", estimated);
		contentValues.put("type", "Pomodoro");
		return db.insert("pomodoro", null, contentValues);
	}

	public int updatePomodoro(long id, String name, long estimated) {
		ContentValues cVal = new ContentValues();
		cVal.put("name", name);
		cVal.put("estimated", estimated);
		return db.update("pomodoro", cVal, "id=?", new String[] { id + "" });
	}

	public boolean deletePomodoro(long id) {
		return db.delete("pomodoro", "id=" + id, null) > 0;
	}

	public Cursor getPomodoroById(long id) {
		Log.i("INFO", "el id es --->" + id);
		return db.query("pomodoro", new String[] { "id", "name", "estimated" },
				"id=?", new String[] { id + "" }, null, null, null);
	}

	public Cursor getAllPomodoros() {
		return db.query("pomodoro", new String[] { "id", "name", "type",
				"estimated", "pomodoros", "unplanned", "interruptions",
				"created" }, null, null, null, null, null);
	}
	/**
	 * Obtiene todos los datos de configuracion
	 * @return cursor 
	 */
	public Cursor getAllConfigs(){
		return db.rawQuery("SELECT name, value FROM config WHERE name='pomodoroLength' OR name='time.shortBreakLength' OR name='time.longBreakLength' OR name='time.longBreakInterval'",null);
	}
	public long updateConfig(String name, String value) {
		ContentValues cVal = new ContentValues();
		cVal.put("name", name);
		cVal.put("value", value);
		return db.update("config", cVal, "name=?", new String[] { name + "" });
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

