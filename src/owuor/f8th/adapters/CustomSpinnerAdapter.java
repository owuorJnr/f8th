package owuor.f8th.adapters;

import owuor.f8th.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomSpinnerAdapter extends ArrayAdapter<CharSequence> {

	LayoutInflater inflater;
	String header;

	public CustomSpinnerAdapter(Context context, String header) {
		super(context, android.R.layout.simple_list_item_2);
		this.header = header;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setListData(CharSequence[] data) {
		clear();
		if (data != null) {
			for (CharSequence appEntry : data) {
				add(appEntry);
			}
			notifyDataSetChanged();
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.spinner_list_row, null);
			holder = new ViewHolder();
			holder.title = (TextView) convertView.findViewById(R.id.txtHeading);
			holder.item = (TextView) convertView.findViewById(R.id.txtItem);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		CharSequence cs = getItem(position);
		holder.item.setText(cs);
		holder.title.setText(header);

		return convertView;
	}// end of method getView()

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.spinner_dropdown_row, null);
			holder = new ViewHolder();
			holder.item = (TextView) convertView.findViewById(R.id.txtItem);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		CharSequence cs = getItem(position);
		holder.item.setText(cs);

		return convertView;
	}

	static class ViewHolder {
		TextView item, title;
	}

}