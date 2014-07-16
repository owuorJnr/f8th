package owuor.f8th.fragments;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.widget.LinearLayout;
import org.holoeverywhere.widget.TextView;

import owuor.f8th.R;
import owuor.f8th.interfaces.IUserManagerRequestListener;
import owuor.f8th.types.F8th;
import owuor.f8th.types.Notification;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ViewNotificationFragment extends Fragment {//implements LoaderManager.LoaderCallbacks<Cursor>{

	IUserManagerRequestListener serviceListener;
	
	Notification notify;
	//String notifyId;
	
	ImageView imgBack;
	TextView txtFrom,txtMessage,txtDate,txtEmptyView;
	
	LinearLayout profileView;
	
	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.view_notification,container,false);
		
		return view;
	}//end of method onCreateview()
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		//automatically mark as read
		this.getSupportActionBar().setTitle("Message");
		
		//imgBack = (ImageView)getView().findViewById(R.id.imgBack);
		txtEmptyView = (TextView)getView().findViewById(R.id.txtEmptyView);
		profileView = (LinearLayout)getView().findViewById(R.id.llNotifyView);

		txtFrom = (TextView)getView().findViewById(R.id.txtFrom);
		txtMessage = (TextView)getView().findViewById(R.id.txtMessage);
		txtDate = (TextView)getView().findViewById(R.id.txtDate);
		
		if(notify != null){
			setView(notify);
		}
		
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
		
	}// end of method onAttach()
	
	public void setNotification(Notification notify){
		this.notify = notify;
	}
	
	/*public void setNotifyId(String notifyId){
		this.notifyId = notifyId;
	}*/
	
	private void setView(Notification notify){
		
		if(notify != null){
			txtEmptyView.setVisibility(View.GONE);
			profileView.setVisibility(View.VISIBLE);
			
			this.notify = notify;
			this.getSupportActionBar().setSubtitle(notify.getSender());
			
			if(notify.getStatus().equalsIgnoreCase(F8th.NOTIFY_UNREAD)){
				txtFrom.setTextColor(Color.RED);
			}
			txtFrom.setText(notify.getSender());
			txtMessage.setText(notify.getMessage());
			txtDate.setText(notify.getDateSent());
			
			serviceListener.onMarkAsReadRequested(notify.getNotifyId());
		}else{
			txtEmptyView.setVisibility(View.VISIBLE);
			profileView.setVisibility(View.GONE);
			this.getSupportActionBar().setSubtitle("");
		}
	}

	/*
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
		String selection = NotificationsTable.COLUMN_NOTIFY_ID + "='" + notifyId + "'";
		CursorLoader cursorLoader = new CursorLoader(getActivity(), uri,projection, selection, null, null);
		// Log.e("Local database","cursor loader returned");
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// TODO Auto-generated method stub
		if (data != null && data.moveToFirst()){
			String nId = data.getString(data.getColumnIndex(NotificationsTable.COLUMN_NID));
			String notifyId = data.getString(data.getColumnIndex(NotificationsTable.COLUMN_NOTIFY_ID));
			String message = data.getString(data.getColumnIndex(NotificationsTable.COLUMN_MESSAGE));
			String senderId = data.getString(data.getColumnIndex(NotificationsTable.COLUMN_SENDER_ID));
			String sender = data.getString(data.getColumnIndex(NotificationsTable.COLUMN_SENDER));
			String sentAt = data.getString(data.getColumnIndex(NotificationsTable.COLUMN_SENT_AT));
			String status = data.getString(data.getColumnIndex(NotificationsTable.COLUMN_STATUS));

			Notification notify = new Notification(nId, notifyId, message,senderId, sender, sentAt, status);
			// notify.getsenderPhoto
			setView(notify);
		}else{
			setView(null);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// TODO Auto-generated method stub
		
	}
	*/
}
