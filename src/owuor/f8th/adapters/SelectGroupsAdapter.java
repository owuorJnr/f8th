package owuor.f8th.adapters;

import java.util.List;

import owuor.f8th.R;
import owuor.f8th.types.Group;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

public class SelectGroupsAdapter extends ArrayAdapter<Group> {
	private LayoutInflater layoutInflater;

	public SelectGroupsAdapter(Context context) {
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
		final ViewHolder holder;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.select_group_row, null);
			holder = new ViewHolder();
			
			holder.groupName = (TextView)convertView.findViewById(R.id.txt_grp_name);
			holder.cbxGroup = (CheckBox)convertView.findViewById(R.id.cbxGroup);
			holder.cbxGroup.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
					Group group = (Group)holder.cbxGroup.getTag();
					group.setSelected(buttonView.isChecked());
				}
			});
			
			convertView.setTag(holder);
			holder.cbxGroup.setTag(getItem(position));
		} else {
			holder = (ViewHolder) convertView.getTag();
			holder.cbxGroup.setTag(getItem(position));
		}

		Group group = getItem(position);
		holder.groupName.setText(group.getName());
		holder.cbxGroup.setChecked(getItem(position).isSelected());
		return convertView;
	}// end of method getView()

	static class ViewHolder {
		CheckBox cbxGroup;
		TextView groupName,title,item;
	}

}// END OF CLASS SelectGroupsAdapter