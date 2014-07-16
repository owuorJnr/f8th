package owuor.f8th.adapters;

import java.util.List;

import owuor.f8th.R;
import owuor.f8th.ContentProvider.F8thContentProvider;
import owuor.f8th.database.NotificationsTable;
import owuor.f8th.types.F8th;
import owuor.f8th.types.Notification;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NotificationAdapter extends ArrayAdapter<Notification> {


	private LayoutInflater layoutInflater;
	private Context context;

	public NotificationAdapter(Context context) {
		super(context, android.R.layout.simple_list_item_2);
		this.context = context;
		layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}// end of constructor

	public void setNotifyList(List<Notification> data) {
		clear();
		if (data != null) {
			for (Notification appEntry : data) {
				add(appEntry);
			}
			notifyDataSetChanged();
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.list_profile_notify_row, null);
			holder = new ViewHolder();
			
			holder.photoView = (ImageView)convertView.findViewById(R.id.imgSender);
			holder.delete = (ImageView)convertView.findViewById(R.id.imgDeleteNotify);
			holder.sender = (TextView) convertView.findViewById(R.id.txtFrom);
			holder.message = (TextView) convertView.findViewById(R.id.txtMessage);
			holder.date = (TextView) convertView.findViewById(R.id.txtDate);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final Notification notification = getItem(position);

		if(notification.getStatus().equalsIgnoreCase(F8th.NOTIFY_UNREAD)){
			holder.sender.setTextColor(Color.RED);
		}
		
		holder.sender.setText(notification.getSender());
		holder.message.setText(notification.getMessage());
		holder.date.setText(notification.getDateSent());
		
		holder.delete.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Uri uri = Uri.parse(F8thContentProvider.CONTENT_URI + "/" + NotificationsTable.TABLE_NOTIFY);
				String selection = NotificationsTable.COLUMN_NOTIFY_ID + "='" + notification.getNotifyId() + "'";
				context.getContentResolver().delete(uri, selection, null);

				NotificationAdapter.this.remove(notification);
				notifyDataSetChanged();
			}
		});

		return convertView;
	}// end of method getView()

	static class ViewHolder {
		ImageView photoView,delete;
		TextView sender, message, date;
	}

}// END OF CLASS NotificationAdapter