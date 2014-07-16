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
import owuor.f8th.adapters.NotificationAdapter;
import owuor.f8th.database.NotificationsTable;
import owuor.f8th.interfaces.IUserManagerRequestListener;
import owuor.f8th.types.F8th;
import owuor.f8th.types.Notification;
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


public class ProfileNotifyFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, OnItemClickListener, OnRefreshListener, OnScrollListener {

	public static PullToRefreshListView listNotify;
	public static TextView emptyView;
	ProgressBar listProgress;
	
	private NotificationAdapter notifyAdapter;

	IUserManagerRequestListener serviceListener;
	boolean nextListLoaded = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstancestate) {
		View view = inflater.inflate(R.layout.list_pull_to_refresh, container,false);
		
		return view;
	}// end of method onCreateView()

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getSupportActionBar().setTitle("HOME");
		getSupportActionBar().setSubtitle("notifications");
		
		listNotify = (PullToRefreshListView)getView().findViewById(R.id.pull_to_refresh_listview);
		listProgress = (ProgressBar)getView().findViewById(R.id.progressList);
		
		emptyView = (TextView) getView().findViewById(R.id.empty_list_item);
		emptyView.setText("Unable to load Notifications");
		listNotify.setEmptyView(emptyView);
		
		// initialize adapter and set it
		notifyAdapter = new NotificationAdapter(getActivity());

		getLoaderManager().initLoader(0, null, this);
		
		listNotify.setAdapter(notifyAdapter);
		listNotify.setOnItemClickListener(this);
		listNotify.setOnRefreshListener(this);
		listNotify.setOnScrollListener(this);
		listProgress.setVisibility(View.VISIBLE);
	}


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
		//
		Notification notify = (Notification) listNotify.getItemAtPosition(position+1);
		serviceListener.onViewNotificationRequested(notify);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// TODO Auto-generated method stub

		String[] projection = { NotificationsTable.COLUMN_ID,
				NotificationsTable.COLUMN_NID,
				NotificationsTable.COLUMN_NOTIFY_ID,
				NotificationsTable.COLUMN_MESSAGE,
				NotificationsTable.COLUMN_SENDER_ID,
				NotificationsTable.COLUMN_SENDER,
				NotificationsTable.COLUMN_SENDER_PHOTO,
				NotificationsTable.COLUMN_SENT_AT,
				NotificationsTable.COLUMN_STATUS };

		Uri uri = Uri.parse(F8thContentProvider.CONTENT_URI + "/"+ NotificationsTable.TABLE_NOTIFY);
		CursorLoader cursorLoader = new CursorLoader(getActivity(), uri,projection, null, null, null);
		// Log.e("Local database","cursor loader returned");
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// TODO Auto-generated method stub
		if (data != null && data.getCount() > 0) {
			List<Notification> notifications = new ArrayList<Notification>();
			int size = data.getCount();
			for (int i = 0; i < size; i++) {
				data.moveToPosition(i);
				String nId = data.getString(data.getColumnIndex(NotificationsTable.COLUMN_NID));
				String notifyId = data.getString(data.getColumnIndex(NotificationsTable.COLUMN_NOTIFY_ID));
				String message = data.getString(data.getColumnIndex(NotificationsTable.COLUMN_MESSAGE));
				String senderId = data.getString(data.getColumnIndex(NotificationsTable.COLUMN_SENDER_ID));
				String sender = data.getString(data.getColumnIndex(NotificationsTable.COLUMN_SENDER));
				String sentAt = data.getString(data.getColumnIndex(NotificationsTable.COLUMN_SENT_AT));
				String status = data.getString(data.getColumnIndex(NotificationsTable.COLUMN_STATUS));

				Notification notify = new Notification(nId, notifyId, message,senderId, sender, sentAt, status);
				// notify.getsenderPhoto
				notifications.add(notify);
			}
			notifyAdapter.setNotifyList(notifications);
		} else {
			serviceListener.onNotificationsRequested("");
			
		}
		listProgress.setVisibility(View.GONE);
		nextListLoaded = false;
		if(listNotify.isRefreshing()){
			listNotify.onRefreshComplete();
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		if(visibleItemCount > F8th.LIST_LIMIT && !listNotify.isRefreshing()){
			//screen can handle more than 20 items
			if(!nextListLoaded){
				nextListLoaded = true;
				int itemsCount = listNotify.getCount();
				Log.e("NotifyFragment", String.valueOf(itemsCount));
				Notification lastNotify = (Notification) listNotify.getItemAtPosition(itemsCount-1);
				serviceListener.onFavStoryListRequested(lastNotify.getNID());
			}
		}else if(firstVisibleItem + visibleItemCount >= totalItemCount  && visibleItemCount > F8th.LIST_LIMIT && !listNotify.isRefreshing()){
			//user has scrolled to last item
			if(!nextListLoaded){
				nextListLoaded = true;
				int itemsCount = listNotify.getCount();
				Log.e("NotifyFragment", String.valueOf(itemsCount));
				Notification lastNotify = (Notification) listNotify.getItemAtPosition(itemsCount-1);
				serviceListener.onFavStoryListRequested(lastNotify.getNID());
				
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
		serviceListener.onNotificationsRequested("");
		
	}

}