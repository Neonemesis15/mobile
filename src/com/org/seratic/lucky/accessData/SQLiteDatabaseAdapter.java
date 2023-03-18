package com.org.seratic.lucky.accessData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteDiskIOException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.org.seratic.lucky.R;

/***
 * Helper singleton class to manage SQLiteDatabase Create and Restore
 * 
 * @author dariochamorro
 * 
 */

public class SQLiteDatabaseAdapter extends SQLiteOpenHelper {

	private static SQLiteDatabase sqliteDb;
	private static SQLiteDatabaseAdapter instance;	
	private static final int DATABASE_VERSION = 4;
	private static String DB_PATH_PREFIX = "/data/data/";
	private static String DB_PATH_SUFFIX = "/databases/";
	private static final String TAG = "OpenAlmanacSQLiteDatabaseAdapter";
	private static String db;
	private Context context;

	/**
	 * @param context
	 * @param name
	 * @param factory
	 * @param version
	 */
	private SQLiteDatabaseAdapter(Context context, String name, CursorFactory factory, int version) {

		super(context, name, factory, version);
		this.context = context;

		Log.i(TAG, "Create or Open database : " + name);

	}

	/**
	 * @param context
	 * @param databaseName
	 */
	private static void initialize(Context context, String databaseName) {
		if (instance == null) {

			if (!checkDatabase(context, databaseName)) {

				try {
					copyDataBase(context, databaseName);
				} catch (IOException e) {
					Log.e(TAG, "Database " + databaseName + " does not exists and there is no Original Version in Asset dir");
				}
			}

			Log.i(TAG, "Try to create instance of database (" + databaseName + ")");
			instance = new SQLiteDatabaseAdapter(context, databaseName, null, DATABASE_VERSION);
			sqliteDb = instance.getWritableDatabase();
			Log.i(TAG, "instance of database (" + databaseName + ") created !");
		}
	}

	/**
	 * @param context
	 * @return
	 */
	public static final SQLiteDatabaseAdapter getInstance(Context context) {

		db = context.getString(R.string.db_name);
		initialize(context, db);
		return instance;
	}

	public SQLiteDatabase getDatabase() {
		return sqliteDb;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(TAG, "onCreate : nothing to do");
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(TAG, "onCreate : nothing to do");

	}

	private void copyDataBase(String databaseName) throws IOException {
		copyDataBase(context, databaseName);
	}

	private static void copyDataBase(Context aContext, String databaseName) throws IOException {

		InputStream myInput = aContext.getAssets().open(databaseName);

		String outFileName = getDatabasePath(aContext, databaseName);

		Log.i(TAG, "Check if create dir : " + DB_PATH_PREFIX + aContext.getPackageName() + DB_PATH_SUFFIX);

		File f = new File(DB_PATH_PREFIX + aContext.getPackageName() + DB_PATH_SUFFIX);
		if (!f.exists())
			f.mkdir();

		Log.i(TAG, "Trying to copy local DB to : " + outFileName);

		OutputStream myOutput = new FileOutputStream(outFileName);

		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}

		myOutput.flush();
		myOutput.close();
		myInput.close();

		Log.i(TAG, "DB (" + databaseName + ") copied!");
	}

	public boolean checkDatabase(String databaseName) {
		return checkDatabase(context, databaseName);
	}

	public static boolean checkDatabase(Context aContext, String databaseName) {
		boolean retorno = false;
		SQLiteDatabase checkDB = null;

		String myPath = getDatabasePath(aContext, databaseName);
		try {
			Log.i(TAG, "Trying to conntect to : " + myPath);
			checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
			Log.i(TAG, "Database " + databaseName + " found!");
			checkDB.close();
		} catch (SQLiteDiskIOException e) {
			retorno = true;
		} catch (SQLiteException e) {
			Log.i(TAG, "Database " + databaseName + " does not exists!");
		}
		return retorno || (checkDB != null);
	}

	/***
	 * Method that returns database path in the application's data directory
	 * 
	 * @param databaseName
	 *            : database name
	 * @return : complete path
	 */
	private String getDatabasePath(String databaseName) {
		return getDatabasePath(context, databaseName);
	}

	/***
	 * Static Method that returns database path in the application's data
	 * directory
	 * 
	 * @param aContext
	 *            : application context
	 * @param databaseName
	 *            : database name
	 * @return : complete path
	 */
	private static String getDatabasePath(Context aContext, String databaseName) {
		return DB_PATH_PREFIX + aContext.getPackageName() + DB_PATH_SUFFIX + databaseName;
	}
}