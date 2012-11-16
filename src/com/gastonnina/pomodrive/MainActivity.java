package com.gastonnina.pomodrive;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Chronometer.OnChronometerTickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import com.gastonnina.pomodrive.adapters.ListaNormalAdapter;
import com.gastonnina.pomodrive.db.PomodoroDatabaseAdapter;
/**
 * Main class to make the pomodoro 
 * @author Gastón Nina <gastonnina@gmail.com>
 *
 */
public class MainActivity extends Activity {
	MainActivity that = this;
	TextView lblReloj,lblCurTask;
	Button btnTimer;
	CountDownTimer reloj;
	Chronometer relojb;
	//long minute = 60000;//minuto real
	long minute = 5000;//5 segundos
	// long minute =1000;
	long second = 1000;
	long pomodoroLength = 25 * minute;// 25//5
	long timeShortBreakLength = 5 * minute;// 5//3
	long timeLongBreakLength = 20 * minute;// 20//7
	int timeLongBreakInterval = 4;
	long countPomodoro = 0;// All pomodoros since you have started the app
	long usedPomodoro = 0;
	long estimatedPomodoro = 0;
	long waited = 0;
	

	Toast toast;

	boolean isFirstTime = true;
	boolean isTimeBreak = false;
	boolean isTimeShortBreak = true;
	
	

	long startTime;
	long countUp;
	// The pomodors are less than an one hour
	SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
	Date rdate;
	private curPomodoro curPom;

	// Lista
	private ListView lista;
	private ListaNormalAdapter adaptadorLista;
	// DB
	private PomodoroDatabaseAdapter db;
	// IDs
	private ArrayList<Long> ids = new ArrayList<Long>();
	//IDS Config
	private int[] idsC = new int[5];
	private MediaPlayer mp1, mp2;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Typeface font = Typeface.createFromAsset(getAssets(),
				"fonts/roboto_thin.ttf");
		
		mp1 = new MediaPlayer();
		mp2 = new MediaPlayer();
		
		lblCurTask = (TextView) findViewById(R.id.lblCurTask);
		
		lista = (ListView) findViewById(R.id.taskList);
		//lista.setOnItemClickListener(this);
		adaptadorLista = new ListaNormalAdapter(this);
		lista.setAdapter(adaptadorLista);
		lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> padre, View itemClickeado,
					int posicion, long id) {
				long id1 = ids.get(posicion);
				//FIXME poner curent para almacenar en BD con cada pomodoro
				// Acá el código para cada item
				ponerActual(id1);
				SlidingDrawer slidingDrawer1 = (SlidingDrawer) findViewById(R.id.slidingDrawer1);
				slidingDrawer1.animateClose();
				/*
				Toast t1 = Toast.makeText(that, "Se presiono "
						+ posicion, Toast.LENGTH_LONG);
				//
				t1.show();*/
			}
		});
		registerForContextMenu(lista);
		//MainActivity that = this;
		db = new PomodoroDatabaseAdapter(this);
		
		toast = new Toast(this);
		relojb = (Chronometer) findViewById(R.id.chrono);
		curPom = new curPomodoro();
		lblReloj = (TextView) findViewById(R.id.lblReloj);
		btnTimer = (Button) findViewById(R.id.btnTimer);
		
		//fuente
		lblReloj.setTypeface(font);
		btnTimer.setTypeface(font);
		
		btnTimer.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startBucle();
			}
		});

	}
	
	void setPomodoroLenght(long t) {
		pomodoroLength = t * minute;
	}

	void setTimeShortBreakLength(long t) {
		timeShortBreakLength = t * minute;
	}

	void setTimeLongBreakLength(long t) {
		timeLongBreakLength = t * minute;
	}

	void settimeLongBreakInterval(long t) {
		timeLongBreakInterval = 4;
	}

	/**
	 * Rescata y configura tiempos de pomodoro
	 */
	void setConfig() {
		Log.i("INFO", "Dentro de config setp 1");
		Cursor curq = db.getAllConfigs();
		Log.i("INFO", "Dentro de config setp 2");
		int cc = 0;
		idsC[0] = 0;
		idsC[1] = 0;
		idsC[2] = 0;
		idsC[3] = 0;
		Log.i("INFO", "Dentro de config setp 3");
		if (curq.moveToFirst()) {
			Log.i("INFO", "Dentro de config setp 4");
			do {
				if (cc < 4) {
					String name = curq.getString(0);
					String value = curq.getString(1);
					Log.i("INFO", "RECUPERADP--" + name + "===" + value);
					if (name.equals("pomodoroLength")) {
						idsC[0] = Integer.parseInt(value);
						setPomodoroLenght((long) idsC[0]);
					} else if (name.equals("time.shortBreakLength")) {
						idsC[1] = Integer.parseInt(value);
						setTimeShortBreakLength((long) idsC[1]);
					} else if (name.equals("time.longBreakLength")) {
						idsC[2] = Integer.parseInt(value);
						setTimeLongBreakLength((long) idsC[2]);
					} else if (name.equals("time.longBreakInterval")) {
						idsC[3] = Integer.parseInt(value);
						settimeLongBreakInterval((long) idsC[3]);
					}
				}
				cc++;
			} while (curq.moveToNext());
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//Log.i("INFO","opcion---"+item.getItemId());
		final Dialog miDialog = new Dialog(that);
		switch (item.getItemId()) {
		case R.id.menu_adicionar:
			/*Intent intent = new Intent(this, FormularioActivity.class);
			startActivity(intent);*/
	    	miDialog.setContentView(R.layout.form_pomodoro);
	    	miDialog.setTitle(R.string.TitleAdd);
	    		
	    	
			TextView pomTxtId = (TextView) miDialog.findViewById(R.id.pomTxtId);
			final TextView pomTxtName = (TextView) miDialog.findViewById(R.id.pomTxtName);
			final TextView lblFrmTxtEstimated = (TextView) miDialog.findViewById(R.id.lblFrmTxtEstimated);
			final SeekBar pomBarEstimated = (SeekBar) miDialog.findViewById(R.id.pomBarEstimated);
			
			pomBarEstimated
					.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
						public void onStopTrackingTouch(SeekBar bar) {
							// txt2.setText("seekbar has been stop");
						}

						public void onStartTrackingTouch(SeekBar bar) {
							// txt2.setText("seekbar has started");
						}

						public void onProgressChanged(SeekBar bar,
								int paramInt, boolean paramBoolean) {
							lblFrmTxtEstimated.setText(getString(R.string.lblEstimated)
									+ ": " + (paramInt + 1));
						}
	         });
	    	pomTxtId.setText("");
	    	pomTxtName.setText("");
	    	lblFrmTxtEstimated.setText(getString(R.string.lblEstimated)+ ": 1");
	    	pomBarEstimated.setProgress(0);

			//FIXME - mover a un general
			//Recojemos el boton para anadirle una accion
			Button btnSave = (Button) miDialog.findViewById(R.id.btnFrmSave);
			
			btnSave.setOnClickListener(new OnClickListener() {
			     public void onClick(View v) {
						//actualiza BD
						db.insertPomodoro(pomTxtName.getText().toString(),(long) (pomBarEstimated.getProgress()+1));
						miDialog.dismiss();
						cargarDatosLista();
			     }
			 });   
			Button btnCancel = (Button) miDialog.findViewById(R.id.btnFrmCancel);
			btnCancel.setOnClickListener(new OnClickListener() {
			     public void onClick(View v) {
			    	 miDialog.dismiss();
			     }
			});
	    	miDialog.show();//importante
			break;
			case R.id.menu_settings:
				miDialog.setContentView(R.layout.form_config);
		    	miDialog.setTitle(R.string.TitleConfig);
		    	
				final TextView cnfBrkLongInterval = (TextView) miDialog.findViewById(R.id.cnfBrkLongInterval);
				
				final TextView lblCnfPomLength = (TextView) miDialog.findViewById(R.id.lblCnfPomLength);
				final TextView lblCnfBrkShortLength = (TextView) miDialog.findViewById(R.id.lblCnfBrkShortLength);
				final TextView lblCnfBrkLongLength = (TextView) miDialog.findViewById(R.id.lblCnfBrkLongLength);
				
				final SeekBar cnfBarPomLength = (SeekBar) miDialog.findViewById(R.id.cnfBarPomLength);
				final SeekBar cnfBarBrkShortLength = (SeekBar) miDialog.findViewById(R.id.cnfBarBrkShortLength);
				final SeekBar cnfBarBrkLongLength = (SeekBar) miDialog.findViewById(R.id.cnfBarBrkLongLength);
				cnfBarPomLength
						.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
							public void onStopTrackingTouch(SeekBar bar) {
								// txt2.setText("seekbar has been stop");
							}
	
							public void onStartTrackingTouch(SeekBar bar) {
								// txt2.setText("seekbar has started");
							}
	
							public void onProgressChanged(SeekBar bar,
									int paramInt, boolean paramBoolean) {
								lblCnfPomLength.setText(getString(R.string.strLblCnfPomLength)
										+ ": " + (paramInt + 1));
							}
						});
				cnfBarBrkShortLength.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
					
					@Override
					public void onStopTrackingTouch(SeekBar bar) {
					}
					
					@Override
					public void onStartTrackingTouch(SeekBar bar) {
					}
					
					@Override
					public void onProgressChanged(SeekBar bar, 
							int paramInt, boolean paramBoolean) {
						lblCnfBrkShortLength.setText(getString(R.string.strLblCnfBrkShortLength)
						+ ": " + (paramInt + 1));
					}
					});
				cnfBarBrkLongLength
					.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

						@Override
						public void onStopTrackingTouch(SeekBar bar) {
						}

						@Override
						public void onStartTrackingTouch(SeekBar bar) {
						}

						@Override
						public void onProgressChanged(SeekBar bar,
								int paramInt, boolean paramBoolean) {
							lblCnfBrkLongLength
									.setText(getString(R.string.strLblCnfBrkLongLength)
											+ ": " + (paramInt + 1));
						}
					});
				setConfig();
				
			
			lblCnfPomLength.setText(getString(R.string.strLblCnfPomLength) + ": "+ idsC[0]);
			cnfBarPomLength.setProgress((int) (idsC[0] - 1));
			
			lblCnfBrkShortLength.setText(getString(R.string.strLblCnfBrkShortLength) + ": " + idsC[1]);
			cnfBarBrkShortLength.setProgress((int) (idsC[1]) - 1);
			
			lblCnfBrkLongLength.setText(getString(R.string.strLblCnfBrkLongLength) + ": " + idsC[2]);
			cnfBarBrkLongLength.setProgress((int) (idsC[2]) - 1);
			
			cnfBrkLongInterval.setText(String.valueOf(idsC[3]));
			
				//FIXME - mover a un general
				//Recojemos el boton para anadirle una accion
				Button btnCnfSave = (Button) miDialog.findViewById(R.id.btnFrmSave);
				
				btnCnfSave.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {

					// actualiza BD
					db.updateConfig("pomodoroLength", String.valueOf((cnfBarPomLength.getProgress() + 1)));

					db.updateConfig("time.shortBreakLength", String.valueOf((cnfBarBrkShortLength.getProgress() + 1)));

					db.updateConfig("time.longBreakLength", String.valueOf((cnfBarBrkLongLength.getProgress() + 1)));

					db.updateConfig("time.longBreakInterval", cnfBrkLongInterval.getText().toString());
					setConfig();
					miDialog.dismiss();
				}
				 });   
				Button btnCnfCancel = (Button) miDialog.findViewById(R.id.btnFrmCancel);
				btnCnfCancel.setOnClickListener(new OnClickListener() {
				     public void onClick(View v) {
				    	 miDialog.dismiss();
				     }
				});
		    	miDialog.show();//importante
			break;
		}
		//-- tiene que llamar a menu popup y de ese recien a editar
		return super.onOptionsItemSelected(item);
	}
	
	// MENU CONTEXTO **************************************************
		@Override
		public void onCreateContextMenu(ContextMenu menu, View v,
				ContextMenuInfo menuInfo) {
			super.onCreateContextMenu(menu, v, menuInfo);
			getMenuInflater().inflate(R.menu.item_list, menu);
		}
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo(); // Para obtener que item se presiono (posicion
								// item)
		final int index = info.position;
		//para todas las opciones
		final long id = ids.get(index);
		
		switch (item.getItemId()) {
		case R.id.menu_show:
			final Dialog miDialogShow = new Dialog(that);
			miDialogShow.setContentView(R.layout.activity_detail);
			miDialogShow.setTitle(R.string.TitleShow);
	    	
	    	Cursor cursor_show = db.getPomodoroById(id);
	    	
	    	if (cursor_show.moveToFirst()) {
				String name = cursor_show.getString(1);
				int estimated = cursor_show.getInt(2);
				int pomodoros = cursor_show.getInt(3);
				int unplanned = cursor_show.getInt(4);
				int interruptions = cursor_show.getInt(5);
				final String[] numbers = new String[] { 
						"E: "+estimated, "P:"+pomodoros, 
						"U: "+unplanned, "I: "+interruptions};	
				TextView lblDetTxtTask=(TextView) miDialogShow.findViewById(R.id.lblDetTxtTask);
		    	GridView gridView = (GridView) miDialogShow.findViewById(R.id.gridView1);
		    	 
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
						android.R.layout.simple_list_item_1, numbers);
		 
				gridView.setAdapter(adapter);
				
				lblDetTxtTask.setText(""+name);
			}
	    	Button btnCancelShow = (Button) miDialogShow.findViewById(R.id.btnFrmCancel);
			btnCancelShow.setOnClickListener(new OnClickListener() {
			     public void onClick(View v) {
			    	 miDialogShow.dismiss();
			     }
			});
	    	miDialogShow.show();//importante
			break;
		case R.id.menu_editar:
			/*Intent intent = new Intent(this, FormularioActivity.class);
			intent.putExtra("id", ids.get(index));
			startActivity(intent);*/
			final Dialog miDialog = new Dialog(that);
	    	miDialog.setContentView(R.layout.form_pomodoro);
	    	miDialog.setTitle(R.string.TitleEdit);
	    	
	    	
	    	Cursor cursor = db.getPomodoroById(id);
	    	
	    	final TextView pomTxtId=(TextView) miDialog.findViewById(R.id.pomTxtId);
			final TextView pomTxtName=(TextView) miDialog.findViewById(R.id.pomTxtName);
			final TextView lblFrmTxtEstimated = (TextView) miDialog.findViewById(R.id.lblFrmTxtEstimated);
			final SeekBar pomBarEstimated = (SeekBar) miDialog.findViewById(R.id.pomBarEstimated);
			pomBarEstimated
			.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
				public void onStopTrackingTouch(SeekBar bar) {
					// txt2.setText("seekbar has been stop");
				}

				public void onStartTrackingTouch(SeekBar bar) {
					// txt2.setText("seekbar has started");
				}

				public void onProgressChanged(SeekBar bar,
						int paramInt, boolean paramBoolean) {
					lblFrmTxtEstimated.setText(getString(R.string.lblEstimated)
							+ ": " + (paramInt + 1));
				}
     });
			if (cursor.moveToFirst()) {
				String name = cursor.getString(1);
				int estimated = cursor.getInt(2);				
				pomTxtId.setText(""+id);
				pomTxtName.setText(""+name);
				lblFrmTxtEstimated.setText(getString(R.string.lblEstimated)+": "+estimated);
				pomBarEstimated.setProgress(estimated-1);
			}
			//FIXME - mover a un general
			//Recojemos el boton para anadirle una accion
			Button btnSave = (Button) miDialog.findViewById(R.id.btnFrmSave);
			
			btnSave.setOnClickListener(new OnClickListener() {
			     public void onClick(View v) {
						//actualiza BD
					db.updatePomodoro(
							Long.parseLong(pomTxtId.getText().toString()),
							pomTxtName.getText().toString(),
							(long) (pomBarEstimated.getProgress() + 1));
						miDialog.dismiss();
						cargarDatosLista();
			     }
			 });   
			Button btnCancel = (Button) miDialog.findViewById(R.id.btnFrmCancel);
			btnCancel.setOnClickListener(new OnClickListener() {
			     public void onClick(View v) {
			    	 miDialog.dismiss();
			     }
			});
	    	miDialog.show();//importante
			break;
		case R.id.menu_eliminar:
			AlertDialog.Builder aDialog = new AlertDialog.Builder(that);
			aDialog.setTitle(R.string.TitleDelete);
			aDialog.setMessage(R.string.confirmDelete);
			aDialog.setPositiveButton(R.string.yes,
					new AlertDialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							db.deletePomodoro(id);
							ids.remove(index);
							cargarDatosLista();
						}
					});
			aDialog.setNegativeButton(R.string.no,
					new AlertDialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							//Cancel Option doesn't do anything 
						}
					});

			aDialog.create();
			aDialog.show();
			break;
		}
		return super.onContextItemSelected(item);
	}
	@Override
	protected void onStart() {
		super.onStart();
		db.abrir();
		//cargamos datos de BD para el reloj
		setConfig();
		lblReloj.setText(sdf.format(pomodoroLength));
		cargarDatosLista();
	}
	void playSoundShort(){
		mp1 = MediaPlayer.create(this, R.raw.sound_short);
		mp1.start();
	}
	public void stopSoundShort() {
		mp1.stop();
		mp1.release();
	}
	void playSoundLong(){
		mp2 = MediaPlayer.create(this, R.raw.sound_long);
		mp2.start();
	}
	public void stopSoundLong() {
		mp2.stop();
		mp2.release();
	}
	public void ponerActual(long id){
		Cursor cursor_show = db.getPomodoroById(id);
		if (cursor_show.moveToFirst()) {
			
			String name = cursor_show.getString(1);
			int estimated = cursor_show.getInt(2);
			int pomodoros = cursor_show.getInt(3);
			lblCurTask.setText(""+name+" ("+pomodoros+"/"+estimated+")");
			//put
			curPom.id=id;
			curPom.name=name;
			curPom.pomodoros=pomodoros;
			curPom.estimated=estimated;
		}
	}
	public void cargarDatosLista() {
		// Limpiamos la lista
		ids.clear(); // Borramos la lista
		adaptadorLista.eliminarTodo(); // Borramos en contenido de la ListView
		Cursor cur = db.getAllPomodoros();
		if(cur.getCount()>0){
			if(cur.getCount()<2){
				btnTimer.setTextAppearance(this, R.style.PomodriveTheme_grid2);
			}else{
				btnTimer.setTextAppearance(this, R.style.PomodriveTheme_grid1);
			}
		
		if (cur.moveToFirst()) {
			do {
				int id = cur.getInt(0);
				String name = cur.getString(1);
				String type = cur.getString(2);
				int estimated = cur.getInt(3);
				int pomodoros = cur.getInt(4);
				int unplanned = cur.getInt(5);
				int interruptions = cur.getInt(6);
				ids.add((long) id);// array de posiciones
				// if (sexo.equals("m")) {
				adaptadorLista.adicionarItem(R.drawable.ic_launcher, name, type
						+ "\nE: " + estimated+" P: "+pomodoros+" U: "+unplanned+" I: "+interruptions);
				/*
				 * } else {
				 * adaptadorLista.adicionarItem(R.drawable.ic_action_mujer,
				 * nombre, correo + "\n" + telefono); }
				 */
			} while (cur.moveToNext());
		}
		}else{
			btnTimer.setTextAppearance(this, R.style.PomodriveTheme_grid0);
		}
		
		adaptadorLista.notifyDataSetChanged();
	}

	public void startBucle() {
		Log.i(TAG_RELOJ,"--->INICIA BUCLE");
		if (!isTimeBreak) {
			//btnTimer.setBackground(getResources().getDrawable(R.drawable.stop_icon));
			//Drawable img = getResources().getDrawable( R.drawable.ico_stop );
			btnTimer.setCompoundDrawablesWithIntrinsicBounds( getResources().getDrawable( R.drawable.ico_stop ), null, null, null );
			//btnTimer.setBackgroundResource(R.drawable.ico_stop);
			
			btnTimer.setText(getString(R.string.lblStop));
			btnTimer.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Log.i(TAG_RELOJ,"CLICK STOP");
					cancelBucle();
				}
			});
			Log.i(TAG_RELOJ,"INICIA POMODORO");
			startPomodoro(lblReloj);
		} else {
			Log.i(TAG_RELOJ,"INICIA DESCASO");
			long modu = (countPomodoro % timeLongBreakInterval);
			Log.d(TAG_INFO, "--" + countPomodoro + "-%--" + timeLongBreakInterval
					+ "==" + modu);
			if (modu == 0) {
				Log.i(TAG_RELOJ,"INICIA DESCASO LARGO");
				isTimeShortBreak=false;
				startLongBreak(lblReloj);// long break
			} else {
				Log.i(TAG_RELOJ,"INICIA DESCASO CORTO");
				isTimeShortBreak=true;
				startShortBreak(lblReloj);// short break
			}
		}

	}

	public void cancelBucle() {
		//FIXME - para que no maneje try
		Log.i(TAG_RELOJ, "CANCELA SONIDO");
		try {
			stopSoundShort();
		} catch (Exception e) {
			Log.e("ERROR", "SONIDO-corto-" + e.getMessage());
		}
		try {

			stopSoundLong();
		} catch (Exception e) {
			Log.e("ERROR", "SONIDO-largo-" + e.getMessage());
		}
		try {
			reloj.cancel();
		} catch (Exception e) {
			Log.e("ERROR", "RELOJ-DOWN-" + e.getMessage());
		}
		try {
			relojb.stop();
		} catch (Exception e) {
			Log.e("ERROR", "RELOJB-UP-" + e.getMessage());
		}
		
		
		
		
		Log.i(TAG_RELOJ,"CANCELA ACTO");
		//Drawable img = getResources().getDrawable( R.drawable.ico_play );
		btnTimer.setCompoundDrawablesWithIntrinsicBounds( getResources().getDrawable( R.drawable.ico_play ), null, null, null );
		//btnTimer.setBackgroundResource(R.drawable.ico_play);
		btnTimer.setText(getString(R.string.lblStart));
		btnTimer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG_RELOJ,"CLICK START");		
				isTimeBreak = false;
				startBucle();
			}
		});
	}

	public void contador(long... ls) {
		isFirstTime = false;
		Log.i(TAG_RELOJ,"contador--->countPomodoro---->"+countPomodoro);

		if (ls[1] == 1) {
			isFirstTime = true;
			reloj = new CountDownTimer(ls[0], second) {

				public void onTick(long millisUntilFinished) {
					rdate = new Date(millisUntilFinished);
					lblReloj.setText(sdf.format(rdate));
				}

				public void onFinish() {
					// animacion
					// //sleep(second);
					lblReloj.setText("00:00");
					countPomodoro++;
					db.updatePomodoroByClock(curPom.id);
					cargarDatosLista();
					ponerActual(curPom.id);
					startBucle();
				}
			};
		} else {
			waited = ls[0];
			waited += 1000;

			startTime = SystemClock.elapsedRealtime();

			relojb.getBase();
			relojb.setOnChronometerTickListener(new OnChronometerTickListener() {
				@Override
				public void onChronometerTick(Chronometer arg0) {
					// countUp = (SystemClock.elapsedRealtime() -
					// arg0.getBase());
					countUp = (SystemClock.elapsedRealtime() - startTime);
					countUp += 1000;
					//////Log.i(TAG, "waited--" + waited + "--" + countUp);
					// countUp = countUp / 1000;sdf.format(rdate)
					// Log.i(TAG,"2_countUp="+countUp);
					// Log.i(TAG,"arg0="+arg0.getBase());
					if (waited >= countUp) {
						// rdate= new Date(countUp);
						// String asText = (countUp / 60) + ":" + (countUp %
						// 60);
						// lblReloj.setText(asText);
						rdate = new Date(countUp);
						lblReloj.setText(sdf.format(rdate));

					} else {
						relojb.stop();
						if(isTimeShortBreak){//corto
							playSoundShort();
						}else{//largo
							playSoundLong();
						}
					}
				}
			});
		}

	}

	private static final String TAG = "MainActivity";
	private static final String TAG_INFO = "INFO";
	private static final String TAG_RELOJ = "RELOJ";

	public void startPomodoro(View view) {
		isTimeBreak = false;
		lblReloj.setTextAppearance(this, R.style.PomodriveTheme_yellowGoogle);
		if (!isFirstTime)
			reloj.cancel();
		contador(pomodoroLength, 1);
		reloj.start();
		isTimeBreak = true;
	}

	public void startShortBreak(View view) {
		lblReloj.setTextAppearance(this, R.style.PomodriveTheme_greenGoogle);
		if (!isFirstTime)
			reloj.cancel();
		contador(timeShortBreakLength, 1000);
		relojb.start();
		isTimeBreak = false;
	}

	public void startLongBreak(View view) {		
		lblReloj.setTextAppearance(this, R.style.PomodriveTheme_redGoogleAlpha);
		if (!isFirstTime)
			reloj.cancel();
		contador(timeLongBreakLength, 1000);
		relojb.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	void savePomodoro(View view){
		
	}
	void cancelPomodoro(View view){
		
	}
	
	private static class curPomodoro {
		public long id=0;
		public String name="";
		public long pomodoros=0;
		public long estimated=0;
		public long unplanned=0;
		public long interruptions=0;
		public long created=0;
		public curPomodoro() {
			super();
		}


	}
}
