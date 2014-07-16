package owuor.f8th.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class StoriesTable {

	// database table attributes
	public static final String TABLE_STORIES = "tblStories";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_favID = "fav_id";
	public static final String COLUMN_SID = "s_id";
	public static final String COLUMN_STORY_ID = "story_id";
	public static final String COLUMN_STORY = "story";
	public static final String COLUMN_AUTHOR_ID = "author_id";
	public static final String COLUMN_AUTHOR = "author";
	public static final String COLUMN_AUTHOR_PHOTO = "author_photo";
	public static final String COLUMN_FAVS = "favs";
	public static final String COLUMN_isFAV = "is_fav";
	public static final String COLUMN_isOWNER = "is_owner";
	public static final String COLUMN_GROUP_VISIBILITY = "group_visibility";

	// database creation statement
	private static final String CREATE_TABLE = "create table " + TABLE_STORIES
			+ "(" + COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_SID + " varchar(20) not null, " 
			+ COLUMN_favID + " varchar(20), " 
			+ COLUMN_STORY_ID
			+ " varchar(50) not null, " + COLUMN_AUTHOR_ID
			+ " varchar(50) not null, " + COLUMN_AUTHOR
			+ " varchar(30) not null, " + COLUMN_FAVS
			+ " varchar(10) not null, " + COLUMN_isFAV
			+ " varchar(10) not null, " + COLUMN_isOWNER
			+ " varchar(10) not null, " + COLUMN_STORY 
			+ " text, " + COLUMN_GROUP_VISIBILITY
			+ " varchar(50) ,"
			+ COLUMN_AUTHOR_PHOTO + " byte(500) " + ");";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(CREATE_TABLE);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(LocalUsersTable.class.getName(),
				"Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");

		database.execSQL("DROP TABLE IF EXISTS " + TABLE_STORIES);
		onCreate(database);
	}

}
