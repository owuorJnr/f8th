package owuor.f8th.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ProfilesTable {

	// database table attributes
	public static final String TABLE_USERS = "tblUserProfiles";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_UID = "u_id";
	public static final String COLUMN_USER_ID = "user_id";
	public static final String COLUMN_PROFILE_PHOTO = "photo";
	public static final String COLUMN_FNAME = "fname";
	public static final String COLUMN_LNAME = "lname";
	public static final String COLUMN_GENDER = "gender";
	public static final String COLUMN_COUNTRY = "country";
	public static final String COLUMN_FAV_VERSE = "fav_verse";
	public static final String COLUMN_WHY = "why";
	public static final String COLUMN_DESIRE = "desire";
	public static final String COLUMN_VIEWS = "views";

	
	//database creation statement
			private static final String CREATE_TABLE = "create table " 
			      + TABLE_USERS
			      + "(" 
			      + COLUMN_ID + " integer primary key autoincrement, " 
			      + COLUMN_UID + " varchar(20) not null, "
			      + COLUMN_USER_ID + " varchar(50) not null, " 
			      + COLUMN_FNAME + " varchar(20) not null, " 
			      + COLUMN_LNAME + " varchar(20) not null, " 
			      + COLUMN_GENDER + " varchar(8) not null, "  
			      + COLUMN_COUNTRY + " varchar(20) not null, " 
			      + COLUMN_FAV_VERSE + " text, "
			      + COLUMN_WHY + " text, "  
			      + COLUMN_DESIRE + " text, "
			      + COLUMN_VIEWS + " varchar(20), "
			      + COLUMN_PROFILE_PHOTO + " byte(500) "
			      + ");";
	
	
	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(CREATE_TABLE);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(LocalUsersTable.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");

		database.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
		onCreate(database);
	}

}// END OF ProfilesTable