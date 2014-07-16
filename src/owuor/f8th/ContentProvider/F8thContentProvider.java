package owuor.f8th.ContentProvider;

import owuor.f8th.database.F8thDbHelper;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class F8thContentProvider extends ContentProvider {
	// http://www.vogella.com
	// database
	private F8thDbHelper database;

	// used for UriMatcher
	private static final int ALL_ITEMS = 20;
	private static final int SPECIFIC_TABLE = 10;

	private static final String AUTHORITY = "owuor.f8th.contentprovider";
	private static final String BASE_PATH = "users";

	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/users";
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/user";

	private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		uriMatcher.addURI(AUTHORITY, BASE_PATH, ALL_ITEMS);
		uriMatcher.addURI(AUTHORITY, BASE_PATH + "/*", SPECIFIC_TABLE);
	}

	// system calls onCreate() when it starts up the provider.
	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		database = new F8thDbHelper(getContext());
		return false;
	}


	@Override
	public String getType(Uri uri) {

		switch (uriMatcher.match(uri)) {
		case ALL_ITEMS:
			return "vnd.android.cursor.dir/vnd.owuor.f8th.contentprovider.users";
		case SPECIFIC_TABLE:
			return "vnd.android.cursor.item/vnd.owuor.f8th.contentprovider.users";
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}
	
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {

		SQLiteDatabase db = database.getWritableDatabase();
		String table = "";
		switch (uriMatcher.match(uri)) {
		case ALL_ITEMS:
			// do nothing
			break;
		case SPECIFIC_TABLE:
			table = uri.getPathSegments().get(1);
			break;
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
		
		int deleteCount = db.delete(table, selection,selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);// notifies
		Log.e("Content Provider", "record deleted");
		return deleteCount;
	}// end of method delete()


	@Override
	public Uri insert(Uri uri, ContentValues values) {

		SQLiteDatabase db = database.getWritableDatabase();
		String table = "";
		switch (uriMatcher.match(uri)) {
		case ALL_ITEMS:
			// do nothing
			break;
		case SPECIFIC_TABLE:
			table = uri.getPathSegments().get(1);
			break;
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
		
		long id = db.insert(table, null, values);
		getContext().getContentResolver().notifyChange(uri, null);
		Log.e("Content Provider", "record inserted");
		return Uri.parse(CONTENT_URI + "/" + id);
	}

	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,String[] selectionArgs, String sortOrder) {

		SQLiteDatabase db = database.getWritableDatabase();
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		
		String table = "";
		switch (uriMatcher.match(uri)) {
		case ALL_ITEMS:
			// do nothing
			break;
		case SPECIFIC_TABLE:
			table = uri.getPathSegments().get(1);
			break;
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}

		queryBuilder.setTables(table);
		Cursor cursor = queryBuilder.query(db, projection, selection,selectionArgs, null, null, sortOrder);

		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		Log.e("Content Provider", "record retrieved");
		return cursor;

	}// end of method query()

	@Override
	public int update(Uri uri, ContentValues values, String selection,String[] selectionArgs) {
		SQLiteDatabase db = database.getWritableDatabase();
		String table = "";
		switch (uriMatcher.match(uri)) {
		case ALL_ITEMS:
			// do nothing
			break;
		case SPECIFIC_TABLE:
			table = uri.getPathSegments().get(1);
			break;
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
		
		int updateCount = db.update(table, values, selection, selectionArgs);
		//int updateCount = db.update(LocalUsersTable.TABLE_LOCAL_USERS, values, selection, selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		Log.e("Content Provider", "table updated");
		return updateCount;
	}// end of method update()

}
