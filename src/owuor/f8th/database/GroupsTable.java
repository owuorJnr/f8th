package owuor.f8th.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class GroupsTable {

	public static final String TABLE_GROUPS = "tblGroups";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_GID = "g_id";
	public static final String COLUMN_MID = "m_id";
	public static final String COLUMN_GRP_ID = "grp_id";
	public static final String COLUMN_GRP_OWNER_ID = "owner_id";
	public static final String COLUMN_GRP_OWNER = "owner";
	public static final String COLUMN_GRP_NAME = "name";
	public static final String COLUMN_GRP_TYPE = "type";
	public static final String COLUMN_GRP_CITY = "city";
	public static final String COLUMN_GRP_COUNTRY = "country";
	public static final String COLUMN_GRP_PHOTO = "photo";
	public static final String COLUMN_GRP_SIZE = "size";
	public static final String COLUMN_USER_TYPE = "user_type";

	// database creation statement
	private static final String CREATE_TABLE = "create table " + TABLE_GROUPS
			+ "(" + COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_GID + " varchar(20), " 
			+ COLUMN_MID + " varchar(20), " 
			+ COLUMN_GRP_ID
			+ " varchar(50) not null, " + COLUMN_GRP_OWNER_ID
			+ " varchar(50) , " + COLUMN_GRP_OWNER
			+ " varchar(50) , " + COLUMN_GRP_NAME
			+ " varchar(30) not null, " + COLUMN_GRP_TYPE
			+ " varchar(30) not null, " + COLUMN_GRP_SIZE
			+ " varchar(20) not null, " + COLUMN_USER_TYPE
			+ " varchar(50) not null, " + COLUMN_GRP_CITY
			+ " varchar(50) not null, " + COLUMN_GRP_COUNTRY
			+ " varchar(20) not null, " + COLUMN_GRP_PHOTO + " byte(500) "
			+ ");";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(CREATE_TABLE);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(LocalUsersTable.class.getName(),
				"Upgrading database from version " + oldVersion + " to "+ newVersion + ", which will destroy all old data");

		database.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUPS);
		onCreate(database);
	}

}
