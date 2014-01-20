package com.tracer.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;

import com.tracer.activity.login.LoginActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class DataBaseHelper extends SQLiteOpenHelper {

	private static String DB_PATH = "/data/data/com.radiantexp.quitbuddy/databases/";

	private static String DB_NAME = "tracer.sqlite";

	private SQLiteDatabase myDataBase;

	private final Context myContext;

	private static final String TAG = "DataBaseHelper";

	private static DataBaseHelper mDBConnection;

	/**
	 * Constructor Takes and keeps a reference of the passed context in order to
	 * access to the application assets and resources.
	 * 
	 * @param context
	 */
	public DataBaseHelper(Context context) {

		super(context, DB_NAME, null, 1);
		this.myContext = context;
	}

	/**
	 * Creates a empty database on the system and rewrites it with your own
	 * database.
	 * */
	public void createDataBase() throws IOException {

		boolean dbExist = checkDataBase();

		if (dbExist) {
			// do nothing - database already exist
		} else {

			// By calling this method and empty database will be created into
			// the default system path
			// of your application so we are gonna be able to overwrite that
			// database with our database.
			this.getReadableDatabase();

			try {

				copyDataBase();

			} catch (IOException e) {

				throw new Error("Error copying database");

			}
		}

	}

	/**
	 * Check if the database already exist and opens the connection to avoid
	 * re-copying the file each time you open the application if not copies your
	 * database from your local assets-folder to the just created empty database
	 * in the system folder.
	 * 
	 */
	public void checkAndOpenDatabase() {

		boolean dbExist = checkDataBase();

		if (dbExist) {
			// if database already exists then open the conenction
			try {

				openDataBase();

			} catch (SQLException sqlException) {

				sqlException.printStackTrace();

			} catch (Exception e) {

				e.printStackTrace();
				redirectToHomepage();

				// intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				// intent.addCategory(Intent.CATEGORY_HOME);

				// throw new Error("Error opening database");

			}

		} else {

			// By calling this method and empty database will be created into
			// the default system path
			// of your application so we are gonna be able to overwrite that
			// database with our database.
			this.getReadableDatabase();

			try {

				copyDataBase();
				try {

					openDataBase();

				} catch (SQLException sqlException) {

					sqlException.printStackTrace();

				} catch (Exception e) {

					e.printStackTrace();
					redirectToHomepage();
					// throw new Error("Error opening database");

				}
			} catch (IOException e) {

				e.printStackTrace();
				redirectToHomepage();
				// throw new Error("Error copying database");

			}
		}

	}

	/**
	 * Check if the database already exist to avoid re-copying the file each
	 * time you open the application.
	 * 
	 * @return true if it exists, false if it doesn't
	 */
	private boolean checkDataBase() {

		File dbFile = new File(DB_PATH + DB_NAME);
		return dbFile.exists();
	}

	/**
	 * Copies your database from your local assets-folder to the just created
	 * empty database in the system folder, from where it can be accessed and
	 * handled. This is done by transfering bytestream.
	 * */
	private void copyDataBase() throws IOException {

		// Open your local db as the input stream
		InputStream myInput = myContext.getApplicationContext().getAssets().open(DB_NAME);

		// Path to the just created empty db
		String outFileName = DB_PATH + DB_NAME;

		// Open the empty db as the output stream
		OutputStream myOutput = new FileOutputStream(outFileName);

		// transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}

		// Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();

	}

	public void openDataBase() throws SQLException {

		if (myDataBase != null) {
			close();
		}
		// Open the database
		String myPath = DB_PATH + DB_NAME;
		myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);

	}

	public void deleteDataBase() throws SQLException {

		// Open the database
		String myPath = DB_PATH + DB_NAME;
		File file = new File(myPath);
		file.delete();

	}

	public void createNewDataBase() throws SQLException {

		// Open the database
		String myPath = DB_PATH + DB_NAME;

		File file = new File(myPath);
		try {
			file.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static synchronized DataBaseHelper getDBAdapterInstance(Context context) {
		if (mDBConnection == null) {
			mDBConnection = new DataBaseHelper(context);
		}
		return mDBConnection;
	}

	@Override
	public synchronized void close() {
		if (myDataBase != null && myDataBase.isOpen())
			myDataBase.close();

		super.close();

	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	// Add your public helper methods to access and get content from the
	// database.
	// You could return cursors by doing "return myDataBase.query(....)" so it'd
	// be easy
	// to you to create adapters for your views.

	public Cursor selectRecordsFromDB(String tableName, String[] tableColumns, String whereClase, String whereArgs[], String groupBy,
			String having, String orderBy) {
		return myDataBase.query(tableName, tableColumns, whereClase, whereArgs, groupBy, having, orderBy);
	}

	/**
	 * select records from db and return in list
	 * 
	 * @param tableName
	 * @param tableColumns
	 * @param whereClase
	 * @param whereArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @return ArrayList<ArrayList<String>>
	 */
	public ArrayList<ArrayList<String>> selectRecordsFromDBList(String query, String[] selectionArgs) {
		ArrayList<ArrayList<String>> retList = new ArrayList<ArrayList<String>>();
		ArrayList<String> list = new ArrayList<String>();
		Cursor cursor = myDataBase.rawQuery(query, selectionArgs);
		if (cursor.moveToFirst()) {
			do {
				list = new ArrayList<String>();
				for (int i = 0; i < cursor.getColumnCount(); i++) {
					list.add(cursor.getString(i));
				}
				retList.add(list);
			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return retList;
	}

	public Cursor selectRecordsFromDBase(String query, String[] selectionArgs) {
		return myDataBase.rawQuery(query, selectionArgs);
	}

	public Cursor selectRecordsFromDB(String query, String[] selectionArgs) {
		Cursor cursor = myDataBase.rawQuery(query, selectionArgs);
		/*
		 * String pathToFile = null; while(cursor.moveToFirst()) {
		 * pathToFile=cursor.getString(0);
		 */
		return cursor;
	}

	/**
	 * This function used to insert the Record in DB.
	 * 
	 * @param tableName
	 * @param nullColumnHack
	 * @param initialValues
	 * @return the row ID of the newly inserted row, or -1 if an error occurred
	 */
	public long insertRecordsInDB(String tableName, String nullColumnHack, ContentValues initialValues) {
		return myDataBase.insert(tableName, nullColumnHack, initialValues);
	}

	/**
	 * This function used to update the Record in DB.
	 * 
	 * @param tableName
	 * @param initialValues
	 * @param whereClause
	 * @param whereArgs
	 * @return true / false on updating one or more records
	 */
	public boolean updateRecordInDB(String tableName, ContentValues initialValues, String whereClause, String whereArgs[]) {
		return myDataBase.update(tableName, initialValues, whereClause, whereArgs) > 0;

	}

	/**
	 * This function used to insert the Record in DB.
	 * 
	 * @param tableName
	 * @param whereClause
	 * @param whereArgs
	 * @return the number of rows affected if a whereClause is passed in, 0
	 *         otherwise. To remove all rows and get a count pass "1" as the
	 *         whereClause.
	 */
	public long deleteRecordsInDB(String tableName, String whereClause, String[] whereArgs) {
		return myDataBase.delete(tableName, whereClause, whereArgs);
	}

	public void redirectToHomepage() {

		Toast.makeText(myContext, "Error while connecting to Database", Toast.LENGTH_SHORT).show();

		Log.e(TAG, "Exception while connecting to Database and redirecting to home screen");

		Intent intent = new Intent(myContext, LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		myContext.startActivity(intent);
	}
}
