package owuor.f8th.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class NotificationsTable {

	// database table attributes
	public static final String TABLE_NOTIFY = "tblNotifications";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NID = "n_id";
	public static final String COLUMN_NOTIFY_ID = "notify_id";
	public static final String COLUMN_MESSAGE = "message";
	public static final String COLUMN_SENDER_ID = "sender_id";
	public static final String COLUMN_SENDER = "sender";
	public static final String COLUMN_SENDER_PHOTO = "sender_photo";
	public static final String COLUMN_STATUS = "status";
	public static final String COLUMN_SENT_AT = "sent_at";

	
	//database creation statement
	private static final String CREATE_TABLE = "create table " 
	      + TABLE_NOTIFY
	      + "(" 
	      + COLUMN_ID + " integer primary key autoincrement, " 
	      + COLUMN_NID + " integer not null, "
	      + COLUMN_NOTIFY_ID + " varchar(50) not null, "
	      + COLUMN_SENDER_ID + " varchar(50) not null, " 
	      + COLUMN_SENDER + " varchar(30) not null, " 
	      + COLUMN_STATUS + " varchar(10) not null, "  
	      + COLUMN_SENT_AT + " varchar(10) not null, " 
	      + COLUMN_MESSAGE + " text, "
	      + COLUMN_SENDER_PHOTO + " byte(500) "
	      + ");";
	
	
	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(CREATE_TABLE);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(LocalUsersTable.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");

		database.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFY);
		onCreate(database);
	}

}// END OF NotificationsTable