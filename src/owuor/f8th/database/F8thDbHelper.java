package owuor.f8th.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class F8thDbHelper extends SQLiteOpenHelper{

	
	private static final String DATABASE_NAME = "f8th.db";
	private static final int DATABASE_VERSION = 1;

	
	public F8thDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}


	// Method is called during creation of the database
	@Override
	public void onCreate(SQLiteDatabase database) {
		// TODO Auto-generated method stub
		LocalUsersTable.onCreate(database);
		NotificationsTable.onCreate(database);
		StoriesTable.onCreate(database);
		ProfilesTable.onCreate(database);
		GroupsTable.onCreate(database);
	}

	
	 // Method is called during an upgrade of the database,
	  // e.g. if you increase the database version
	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		LocalUsersTable.onUpgrade(database, oldVersion, newVersion);
		NotificationsTable.onUpgrade(database, oldVersion, newVersion);
		StoriesTable.onUpgrade(database, oldVersion, newVersion);
		ProfilesTable.onUpgrade(database, oldVersion, newVersion);
		GroupsTable.onUpgrade(database, oldVersion, newVersion);
	}

}//END OF CLASS ShareDbHelper