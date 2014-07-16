package owuor.f8th.fragments;

import java.util.ArrayList;
import java.util.List;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.widget.ProgressBar;
import org.holoeverywhere.widget.TextView;

import owuor.f8th.R;
import owuor.f8th.ContentProvider.F8thContentProvider;
import owuor.f8th.adapters.CustomSpinnerAdapter;
import owuor.f8th.adapters.PeopleProfileAdapter;
import owuor.f8th.database.ProfilesTable;
import owuor.f8th.interfaces.IUserManagerRequestListener;
import owuor.f8th.types.F8th;
import owuor.f8th.types.User;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;

public class PeopleProfilesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, OnItemClickListener, OnRefreshListener, OnScrollListener {

	public static PullToRefreshListView listProfile;
	public static TextView emptyView;
	ProgressBar listProgress;

	IUserManagerRequestListener serviceListener;
	//boolean connectedToService = false;
	boolean nextListLoaded = false;

	PeopleProfileAdapter peopleAdapter = null;
	CustomSpinnerAdapter categoryAdapter = null;

	String[] items;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstancestate) {
		View view = inflater.inflate(R.layout.list_pull_to_refresh, container,false);
		
		return view;
	}// end of method onCreateView()

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		/*
		 * listPeople = (ListView)getView().findViewById(R.id.listPeople);
		 * spinnerCategory =
		 * (IcsSpinner)getView().findViewById(R.id.spinnerCategory); editSearch
		 * = (EditText)getView().findViewById(R.id.editSearch); imgSearch =
		 * (ImageView)getView().findViewById(R.id.imgSearch); progSearch =
		 * (ProgressBar)getView().findViewById(R.id.progressSearch);
		 * 
		 * TextView emptyView = new TextView(getActivity());
		 * emptyView.setText("Unable to load User Profiles");
		 * listPeople.setEmptyView(emptyView);
		 * 
		 * categoryAdapter = new
		 * CustomSpinnerAdapter(getActivity(),"Search by");
		 * categoryAdapter.setListData
		 * (this.getResources().getStringArray(R.array.searchCategory));
		 * spinnerCategory.setAdapter(categoryAdapter);
		 */
		getSupportActionBar().setTitle("HOME");
		getSupportActionBar().setSubtitle("people");
		
		listProfile = (PullToRefreshListView)getView().findViewById(R.id.pull_to_refresh_listview);
		listProgress = (ProgressBar)getView().findViewById(R.id.progressList);
		
		emptyView = (TextView) getView().findViewById(R.id.empty_list_item);
		emptyView.setText("Unable to load Profiles");
		listProfile.setEmptyView(emptyView);

		peopleAdapter = new PeopleProfileAdapter(getActivity());

		getLoaderManager().initLoader(0, null, this);

		listProfile.setAdapter(peopleAdapter);
		listProfile.setOnItemClickListener(this);
		listProfile.setOnRefreshListener(this);
		listProfile.setOnScrollListener(this);
		listProgress.setVisibility(View.VISIBLE);

	}// end of method onActivityCreated()


	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			serviceListener = (IUserManagerRequestListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement IUserManagerRequestListener");
		}

	}// end of method onAttach()

	public void onDetach() {
		super.onDetach();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		// TODO Auto-generated method stub
		User user = (User) listProfile.getItemAtPosition(position+1);
		// download user profile
		serviceListener.onViewProfileRequested(user);
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
		CursorLoader cursorLoader = new CursorLoader(getActivity(), uri,projection, null, null, null);
		// Log.e("Local database","cursor loader returned");
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// TODO Auto-generated method stub
		if (data != null && data.getCount() > 0) {
			List<User> users = new ArrayList<User>();
			int size = data.getCount();
			for (int i = 0; i < size; i++) {
				data.moveToPosition(i);
				String uid = data.getString(data.getColumnIndex(ProfilesTable.COLUMN_UID));
				String userId = data.getString(data.getColumnIndex(ProfilesTable.COLUMN_USER_ID));
				String fname = data.getString(data.getColumnIndex(ProfilesTable.COLUMN_FNAME));
				String lname = data.getString(data.getColumnIndex(ProfilesTable.COLUMN_LNAME));
				String gender = data.getString(data.getColumnIndex(ProfilesTable.COLUMN_GENDER));
				String country = data.getString(data.getColumnIndex(ProfilesTable.COLUMN_COUNTRY));

				User user = new User(uid, userId, fname, lname, gender, country);
				// user.setUserPhoto(photo);
				users.add(user);

			}
			peopleAdapter.setUserList(users);
			Log.e("ProfileFragment", "List Adapter set");
		} else {
			serviceListener.onProfileListRequested(F8th.LIST_FIRST_TIME, "");
			
		}
		
		listProgress.setVisibility(View.GONE);
		nextListLoaded = false;
		
		if(listProfile.isRefreshing()){
			listProfile.onRefreshComplete();
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		if(visibleItemCount > F8th.LIST_LIMIT && !listProfile.isRefreshing()){
			//screen can handle more than 20 items
			if(!nextListLoaded){
				nextListLoaded = true;
				int itemsCount = listProfile.getCount();
				Log.e("ProfileFragment", String.valueOf(itemsCount));
				User lastUser = (User) listProfile.getItemAtPosition(itemsCount-1);
				serviceListener.onProfileListRequested(F8th.LIST_LOADMORE, lastUser.getU_ID());
			}
		}else if(firstVisibleItem + visibleItemCount >= totalItemCount  && visibleItemCount > F8th.LIST_LIMIT && !listProfile.isRefreshing()){
			//user has scrolled to last item
			if(!nextListLoaded){
				nextListLoaded = true;
				int itemsCount = listProfile.getCount();
				Log.e("ProfileFragment", String.valueOf(itemsCount));
				User lastUser = (User) listProfile.getItemAtPosition(itemsCount-1);
				serviceListener.onProfileListRequested(F8th.LIST_LOADMORE, lastUser.getU_ID());
				
			}else if(nextListLoaded){
				//Toast.makeText(getActivity(), "all Stories fetched", Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		//do nothing
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		serviceListener.onProfileListRequested(F8th.LIST_REFRESH, "");
		
	}

}
