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
import owuor.f8th.adapters.PeopleGroupAdapter;
import owuor.f8th.database.GroupsTable;
import owuor.f8th.interfaces.IUserManagerRequestListener;
import owuor.f8th.types.F8th;
import owuor.f8th.types.Group;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;


public class PeopleGroupsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, OnItemClickListener, OnRefreshListener, OnScrollListener{

	public static PullToRefreshListView listGroup;
	public static TextView emptyView;
	ProgressBar listProgress;
	
	private PeopleGroupAdapter groupAdapter;
	
	IUserManagerRequestListener serviceListener;
	//boolean connectedToService = false;
	boolean nextListLoaded = false,alreadyDownloaded = false;
	//private static String tag;
	
	//public void setTag(String tag){
	///	this.tag = tag;
	//}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstancestate) {
		View view = inflater.inflate(R.layout.list_pull_to_refresh, container,false);
		
		return view;
	}// end of method onCreateView()
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getSupportActionBar().setTitle("HOME");
		getSupportActionBar().setSubtitle("groups");
		
		listGroup = (PullToRefreshListView)getView().findViewById(R.id.pull_to_refresh_listview);
		listProgress = (ProgressBar)getView().findViewById(R.id.progressList);
		
		emptyView = (TextView) getView().findViewById(R.id.empty_list_item);
		emptyView.setText("Unable to load Groups");
		listGroup.setEmptyView(emptyView);
		
		//this.setEmptyText("Unable to load Groups");
		
		groupAdapter = new PeopleGroupAdapter(getActivity());
		
		getLoaderManager().initLoader(0, null, this);
		
		listGroup.setAdapter(groupAdapter);
		//setListAdapter(groupAdapter);
		listGroup.setOnItemClickListener(this);
		listGroup.setOnRefreshListener(this);
		listGroup.setOnScrollListener(this);
		
		//indicates progress bar initially
		//this.setListShown(false);
		listProgress.setVisibility(View.VISIBLE);
		
	}//end of method onActivityCreated()

	
	private BroadcastReceiver fragmentReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			// Log.e("LoginFragment","broadcast received");
			if (intent.getAction().equalsIgnoreCase(F8th.CONNECTED_TO_SERVICE)) {
				//connectedToService = true;

			}
		}
	};// end of class myReceiver()
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			serviceListener = (IUserManagerRequestListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement IUserManagerRequestListener");
		}
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(fragmentReceiver, new IntentFilter(F8th.CONNECTED_TO_SERVICE));
		
	}// end of method onAttach()

	public void onDetach() {
		super.onDetach();
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(fragmentReceiver);
	}
	
	/*@Override
    public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		Group group = (Group) getListView().getItemAtPosition(position);
		serviceListener.onViewGroupRequested(group);
		
	}*/

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		// TODO Auto-generated method stub
		Group group = (Group) listGroup.getItemAtPosition(position+1);
		serviceListener.onViewGroupRequested(group);
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// TODO Auto-generated method stub
		String[] projection = { GroupsTable.COLUMN_ID,GroupsTable.COLUMN_GRP_PHOTO,
				GroupsTable.COLUMN_GID, GroupsTable.COLUMN_GRP_ID,
				GroupsTable.COLUMN_GRP_OWNER_ID,GroupsTable.COLUMN_GRP_OWNER,
				GroupsTable.COLUMN_GRP_NAME,
				GroupsTable.COLUMN_GRP_SIZE,
				GroupsTable.COLUMN_GRP_TYPE,
				GroupsTable.COLUMN_GRP_CITY,
				GroupsTable.COLUMN_GRP_COUNTRY,
				GroupsTable.COLUMN_USER_TYPE };

		Uri uri = Uri.parse(F8thContentProvider.CONTENT_URI + "/" + GroupsTable.TABLE_GROUPS);
		String selection = GroupsTable.COLUMN_MID + "='0'";
		CursorLoader cursorLoader = new CursorLoader(getActivity(),uri, projection, selection, null, null);
		// Log.e("Local database","cursor loader returned");
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// TODO Auto-generated method stub
		
		if (data != null && data.getCount() > 0) {
			List<Group> groups = new ArrayList<Group>();
			int size = data.getCount();
			for (int i = 0; i < size; i++) {
				data.moveToPosition(i);
				String gid = data.getString(data.getColumnIndex(GroupsTable.COLUMN_GID));
				String grpId = data.getString(data.getColumnIndex(GroupsTable.COLUMN_GRP_ID));
				String grpOwnerId = data.getString(data.getColumnIndex(GroupsTable.COLUMN_GRP_OWNER_ID));
				String grpOwner = data.getString(data.getColumnIndex(GroupsTable.COLUMN_GRP_OWNER));
				String grpName = data.getString(data.getColumnIndex(GroupsTable.COLUMN_GRP_NAME));
				String grpType = data.getString(data.getColumnIndex(GroupsTable.COLUMN_GRP_TYPE));
				String grpSize = data.getString(data.getColumnIndex(GroupsTable.COLUMN_GRP_SIZE));
				String grpCity = data.getString(data.getColumnIndex(GroupsTable.COLUMN_GRP_CITY));
				String grpCountry = data.getString(data.getColumnIndex(GroupsTable.COLUMN_GRP_COUNTRY));
				String userType = data.getString(data.getColumnIndex(GroupsTable.COLUMN_USER_TYPE));
				// String grpPhoto =
				// data.getString(data.getColumnIndex(GroupsTable.COLUMN_GRP_PHOTO));

				Group group = new Group("",gid, grpId,grpOwnerId,grpOwner, grpName, grpType, grpSize,grpCity,grpCountry,userType);
				//group.setGrpPhoto(grpPhoto);
				groups.add(group);
			}
			
			Log.e("GroupFragment", "List Adapter set");
			groupAdapter.setGroupList(groups);
		}else  if(!alreadyDownloaded){
			
			//download list
			//if(connectedToService){
			alreadyDownloaded = true;
			serviceListener.onGroupListRequested(F8th.LIST_FIRST_TIME, "");
			//}
		}else{
			groupAdapter.setGroupList(null);
		}
		//this.setListShown(true);
		listProgress.setVisibility(View.GONE);
		nextListLoaded = false;
		if(listGroup.isRefreshing()){
			listGroup.onRefreshComplete();
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// TODO Auto-generated method stub
		//groupAdapter
	}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		if(visibleItemCount > F8th.LIST_LIMIT && !listGroup.isRefreshing()){
			//screen can handle more than 20 items
			if(!nextListLoaded){
				alreadyDownloaded = false;
				nextListLoaded = true;
				int itemsCount = listGroup.getCount();
				Log.e("GroupFragment", String.valueOf(itemsCount));
				Group lastGroup = (Group) listGroup.getItemAtPosition(itemsCount-1);
				serviceListener.onGroupListRequested(F8th.LIST_LOADMORE, lastGroup.getG_ID());
			}
		}else if(firstVisibleItem + visibleItemCount >= totalItemCount  && visibleItemCount > F8th.LIST_LIMIT && !listGroup.isRefreshing()){
			//user has scrolled to last item
			if(!nextListLoaded){
				alreadyDownloaded = false;
				nextListLoaded = true;
				int itemsCount = listGroup.getCount();
				Log.e("GroupFragment", String.valueOf(itemsCount));
				Group lastGroup = (Group) listGroup.getItemAtPosition(itemsCount-1);
				serviceListener.onGroupListRequested(F8th.LIST_LOADMORE, lastGroup.getG_ID());
				
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
		alreadyDownloaded = false;
		serviceListener.onGroupListRequested(F8th.LIST_REFRESH, "");
		
	}
	
	
}