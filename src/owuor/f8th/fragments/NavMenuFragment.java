package owuor.f8th.fragments;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.app.ListFragment;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.TextView;
import org.holoeverywhere.widget.Toast;

import owuor.f8th.R;
import owuor.f8th.ContentProvider.F8thContentProvider;
import owuor.f8th.activities.F8thActivity;
import owuor.f8th.adapters.NavDrawerAdapter;
import owuor.f8th.database.LocalUsersTable;
import owuor.f8th.interfaces.IUserManagerRequestListener;
import owuor.f8th.interfaces.NavDrawerItem;
import owuor.f8th.types.NavMenuItem;
import owuor.f8th.types.NavMenuSection;
import owuor.f8th.types.UserProfile;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.view.ViewGroup;

public class NavMenuFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>{

	IUserManagerRequestListener serviceListener;
	
	private NavDrawerAdapter navAdapter;
	String userId = "";
	UserProfile user;
	View header;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.nav_list, null);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		if (savedInstanceState == null) {
			header = View.inflate(getActivity(),R.layout.nav_list_header, null);
			this.getListView().addHeaderView(header);
		}
		
		NavDrawerItem[] menu = new NavDrawerItem[] {
                NavMenuSection.create( 100, "F8TH CATEGORY"),
                NavMenuItem.create(101, "Stories", R.drawable.action_home, true, getActivity()),
                NavMenuItem.create(102, "Groups", R.drawable.nav_groups, false, getActivity()), 
                NavMenuItem.create(103, "People", R.drawable.action_people, false, getActivity()), 
                NavMenuSection.create(200, "MY CATEGORY"),
                NavMenuItem.create(201, "Favorite Stories", R.drawable.nav_favorites, false, getActivity()),
                NavMenuItem.create(202, "Joined Groups", R.drawable.nav_groups, false, getActivity()), 
                NavMenuItem.create(203, "Notifications", R.drawable.nav_notifications, false, getActivity())};
		
		navAdapter = new NavDrawerAdapter(getActivity(),R.layout.nav_item_row,menu);
		setListAdapter(navAdapter);
		
		//getLoaderManager().initLoader(0, null, this);
		
	}//end of method onActivityCreated()
		
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			serviceListener = (IUserManagerRequestListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement IUserManagerRequestListener");
		}
		
		userId = serviceListener.onGetUserId();
		if(!userId.equalsIgnoreCase("")){
			getLoaderManager().initLoader(0, null, this);
		}
	}// end of method onAttach()
	
	@Override
	public void onListItemClick(ListView lv, View v, int position, long id) {
		if(position == 0){
			//Toast.makeText(getActivity(), "view my profile fragment", Toast.LENGTH_SHORT).show();
			if(user != null){
				ViewProfileFragment viewProfile = new ViewProfileFragment();
				viewProfile.setUserProfile(user);//serviceListener.onGetCurrentUserRequested());
				switchFragment(viewProfile);
			}else{
				//setUserId(serviceListener.onGetUserId());
				getLoaderManager().restartLoader(0, null, this);
				
			}
			
		}else{
			NavDrawerItem selectedItem = (NavDrawerItem) lv.getItemAtPosition(position);
			int itemId = selectedItem.getId();
			if(itemId == 101){
				//Toast.makeText(getActivity(), "stories fragment", Toast.LENGTH_SHORT).show();
				switchFragment(new StoriesFragment());
				
			}else if(itemId == 102){
				//Toast.makeText(getActivity(), "groups fragment", Toast.LENGTH_SHORT).show();
				switchFragment(new PeopleGroupsFragment());
				
			}else if(itemId == 103){
				//Toast.makeText(getActivity(), "people fragment", Toast.LENGTH_SHORT).show();
				switchFragment(new PeopleProfilesFragment());
				
			}else if(itemId == 201){
				//Toast.makeText(getActivity(), "favorite stories fragment", Toast.LENGTH_SHORT).show();
				switchFragment(new StoriesFavFragment());
				
			}else if(itemId == 202){
				//Toast.makeText(getActivity(), "joined groups fragment", Toast.LENGTH_SHORT).show();
				switchFragment(new PeopleGroupsJoinedFragment());
				
			}else if(itemId == 203){
				//Toast.makeText(getActivity(), "notification fragment", Toast.LENGTH_SHORT).show();
				switchFragment(new ProfileNotifyFragment());
				
			}
		}
		
	}
	
	/*
	public void setUserId(String userId){
		this.userId = userId;
		getLoaderManager().initLoader(0, null, this);
	}*/
	
	// the meat of switching the above fragment
	private void switchFragment(Fragment fragment) {
		if (getActivity() == null)
			return;

		if (getActivity() instanceof F8thActivity) {
			F8thActivity ra = (F8thActivity) getActivity();
			ra.switchContent(fragment);
		}
	}
	
	public void setView(UserProfile userProfile){
		if(userProfile!=null && header != null){
			
			TextView txtNames = (TextView)header.findViewById(R.id.txtUserNames);
			TextView txtDetails = (TextView)header.findViewById(R.id.txtUserDetails);
			TextView txtViews = (TextView)header.findViewById(R.id.txtProfileViews);
			
			txtNames.setText(userProfile.getFname()+" "+userProfile.getLname());
			txtDetails.setText(userProfile.getCountry()+", "+userProfile.getGender());
			
			String views = userProfile.getViews();
			if(views != null){
				txtViews.setText(views+" views");
			}else{
				txtViews.setText("");
			}
			
		}
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// TODO Auto-generated method stub
		String[] projection = { LocalUsersTable.COLUMN_ID, LocalUsersTable.COLUMN_USER_ID,
				LocalUsersTable.COLUMN_AUTOLOGIN, LocalUsersTable.COLUMN_UID,LocalUsersTable.COLUMN_FNAME,
				LocalUsersTable.COLUMN_LNAME, LocalUsersTable.COLUMN_GENDER,
				LocalUsersTable.COLUMN_COUNTRY, LocalUsersTable.COLUMN_FAV_VERSE,
				LocalUsersTable.COLUMN_WHY, LocalUsersTable.COLUMN_DESIRE,LocalUsersTable.COLUMN_VIEWS };

		String userid = serviceListener.onGetUserId();
		
		Uri uri = Uri.parse(F8thContentProvider.CONTENT_URI + "/" + LocalUsersTable.TABLE_LOCAL_USERS);
		String selection = LocalUsersTable.COLUMN_USER_ID + "='" + userid + "'";
		CursorLoader cursorLoader = new CursorLoader(getActivity(), uri,projection, selection, null, null);
		// Log.e("Local database","cursor loader returned");
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		// TODO Auto-generated method stub
		if (cursor != null && cursor.moveToFirst()){
			
			//autoLogin = cursor.getString(cursor.getColumnIndexOrThrow(LocalUsersTable.COLUMN_AUTOLOGIN));
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
			String location = "local";
			
			user = new UserProfile(uid, userId, fname, lname, gender, country,about, why, desire, views, location);
			
			setView(user);
		}else{
			setView(null);
			Toast.makeText(getActivity(), "No Local User Found", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// TODO Auto-generated method stub
		
	}
	
	
}
