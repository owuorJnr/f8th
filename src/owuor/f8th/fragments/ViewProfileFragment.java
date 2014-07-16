/**
*@author Dickson Owuor <dickytea@gmail.com>
*@version 1.0
*@since 2012-15-08
*@see
*
*
*/

package owuor.f8th.fragments;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.widget.LinearLayout;
import org.holoeverywhere.widget.TextView;

import owuor.f8th.R;
import owuor.f8th.ContentProvider.F8thContentProvider;
import owuor.f8th.database.LocalUsersTable;
import owuor.f8th.database.ProfilesTable;
import owuor.f8th.types.UserProfile;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ViewProfileFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

	UserProfile userProfile;
	String userId;
	
	TextView txtName,txtGender,txtCountry,txtViews;
	TextView txtFavVerse,txtWhy,txtDesire,txtEmptyView;
	ImageView imgPhoto,imgBack;
	
	LinearLayout profileView;
	
	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.view_profile,container,false);
		
		return view;
	}//end of method onCreateview()
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		
		this.getSupportActionBar().setTitle("Profile");
		//this.getSherlockActivity().getSupportActionBar().setSubtitle("");
		
		//imgBack = (ImageView)getView().findViewById(R.id.imgBack);
		txtEmptyView = (TextView)getView().findViewById(R.id.txtEmptyView);
		profileView = (LinearLayout)getView().findViewById(R.id.llProfileView);
		
		imgPhoto = (ImageView)getView().findViewById(R.id.imgProfilePhoto);
		txtName = (TextView)getView().findViewById(R.id.txtViewName);
		txtGender = (TextView)getView().findViewById(R.id.txtViewGender);
		txtCountry = (TextView)getView().findViewById(R.id.txtViewCountry);
		txtViews = (TextView)getView().findViewById(R.id.txtViews);
		txtFavVerse = (TextView)getView().findViewById(R.id.txtProfileFavVerse);
		txtWhy = (TextView)getView().findViewById(R.id.txtProfileWhy);
		txtDesire = (TextView)getView().findViewById(R.id.txtProfileDesire);
		
		if(userProfile != null){
			setView(userProfile);
		}else{
			getLoaderManager().initLoader(0, null, this);
		}
	}//end of method onActivityCreated()
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
	}// end of method onAttach()
	
	public void setView(UserProfile userProfile){
		if(userProfile!=null){
			
			this.getSupportActionBar().setSubtitle(userProfile.getFname());
			
			txtEmptyView.setVisibility(View.GONE);
			profileView.setVisibility(View.VISIBLE);
			
			txtName.setText(userProfile.getFname()+" "+userProfile.getLname());
			txtGender.setText(userProfile.getGender());
			txtCountry.setText(userProfile.getCountry());
			txtViews.setText(userProfile.getViews());
			txtFavVerse.setText(userProfile.getFavVerse());
			txtWhy.setText(userProfile.getWhy());
			txtDesire.setText(userProfile.getDesire());
			
		}else{
			txtEmptyView.setVisibility(View.VISIBLE);
			profileView.setVisibility(View.GONE);
			this.getSupportActionBar().setSubtitle("");
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
	}//end of method onSaveInstanceState()
	
	public void setUserProfile(UserProfile userProfile){
		this.userProfile = userProfile;
	}
	
	public void setUserId(String userId){
		this.userId = userId;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// TODO Auto-generated method stub
		String[] projection = { ProfilesTable.COLUMN_ID,
				ProfilesTable.COLUMN_UID, ProfilesTable.COLUMN_USER_ID,
				ProfilesTable.COLUMN_FNAME, ProfilesTable.COLUMN_LNAME,
				ProfilesTable.COLUMN_GENDER, ProfilesTable.COLUMN_COUNTRY,
				ProfilesTable.COLUMN_FAV_VERSE, ProfilesTable.COLUMN_WHY,
				ProfilesTable.COLUMN_DESIRE, ProfilesTable.COLUMN_VIEWS,
				ProfilesTable.COLUMN_PROFILE_PHOTO };

		Uri uri = Uri.parse(F8thContentProvider.CONTENT_URI + "/" + ProfilesTable.TABLE_USERS);
		String selection = LocalUsersTable.COLUMN_USER_ID + "='" + userId + "'";
		CursorLoader cursorLoader = new CursorLoader(getActivity(), uri,projection, selection, null, null);
		// Log.e("Local database","cursor loader returned");
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		// TODO Auto-generated method stub
		if (cursor != null && cursor.moveToFirst()){
			
			String uid = cursor.getString(cursor.getColumnIndexOrThrow(LocalUsersTable.COLUMN_UID));
			String userId = cursor.getString(cursor.getColumnIndexOrThrow(LocalUsersTable.COLUMN_USER_ID));
			String fname = cursor.getString(cursor.getColumnIndexOrThrow(LocalUsersTable.COLUMN_FNAME));
			String lname = cursor.getString(cursor.getColumnIndexOrThrow(LocalUsersTable.COLUMN_LNAME));
			String gender = cursor.getString(cursor.getColumnIndexOrThrow(LocalUsersTable.COLUMN_GENDER));
			String country = cursor.getString(cursor.getColumnIndexOrThrow(LocalUsersTable.COLUMN_COUNTRY));
			String about = cursor.getString(cursor.getColumnIndexOrThrow(LocalUsersTable.COLUMN_FAV_VERSE));
			String why = cursor.getString(cursor.getColumnIndexOrThrow(LocalUsersTable.COLUMN_WHY));
			String desire = cursor.getString(cursor.getColumnIndexOrThrow(LocalUsersTable.COLUMN_DESIRE));
			String views = cursor.getString(cursor.getColumnIndexOrThrow(LocalUsersTable.COLUMN_VIEWS));
			
			UserProfile user = new UserProfile(uid, userId, fname, lname, gender, country,about, why, desire, views);
			
			setView(user);
		}else{
			setView(null);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// TODO Auto-generated method stub
		
	}
	
	
}//END OF CLASS ViewProfileFragment