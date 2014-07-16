package owuor.f8th.adapters;

import java.util.List;

import owuor.f8th.R;
import owuor.f8th.types.Group;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PeopleGroupAdapter extends ArrayAdapter<Group> {

	private LayoutInflater layoutInflater;

	public PeopleGroupAdapter(Context context) {
		super(context, android.R.layout.simple_list_item_2);
		layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}// end of constructor

	public void setGroupList(List<Group> data) {
		clear();
		if (data != null) {
			for (Group appEntry : data) {
				add(appEntry);
			}
			notifyDataSetChanged();
		}
	}
	
	public void setGroupPhoto(byte[] photo){
		
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.list_people_groups_row, null);
			holder = new ViewHolder();
			
			holder.photoView = (ImageView)convertView.findViewById(R.id.imgGroup);
			holder.name = (TextView) convertView.findViewById(R.id.txtGrpName);
			holder.type = (TextView) convertView.findViewById(R.id.txtGrpType);
			holder.size = (TextView) convertView.findViewById(R.id.txtGrpSize);
			convertView.setTag(holder);
			
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Group group = getItem(position);
		holder.name.setText(group.getName());
		holder.type.setText(group.getType());
		holder.size.setText(group.getSize()+" members");

		return convertView;
	}// end of method getView()

	static class ViewHolder {
		ImageView photoView;
		TextView name, type, size;
	}

}// END OF CLASS GroupAdapter