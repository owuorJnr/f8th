package owuor.f8th.adapters;

import owuor.f8th.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SettingsAdapter extends ArrayAdapter<String>{

	private LayoutInflater layoutInflater;
	
	public SettingsAdapter(Context context) {
		super(context, android.R.layout.simple_list_item_2);
		// TODO Auto-generated constructor stub
		layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public void setListData(String[] data){
		 clear();
	        if (data != null) {
	            for (String appEntry : data) {
	                add(appEntry);
	            }
	            notifyDataSetChanged();
	        }
	}
	
	@Override
	public View getView(int position,View convertView,ViewGroup parent){
		
		if(convertView == null){
			convertView = layoutInflater.inflate(R.layout.list_profile_settings_row, null);
		}
		TextView text = (TextView)convertView.findViewById(R.id.txtItem);
		//final ProgressBar progress = (ProgressBar)convertView.findViewById(R.id.progressChild);
		
		String item = getItem(position);
		text.setText(item);
		
		return convertView;
	}

}