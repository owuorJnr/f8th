/**
 * All Honor and Glory belongs to God Almighty
 * Phil 2:13
 *@author Dickson Owuor <dickytea@gmail.com>
 *@version 1.1
 *@since 2014-1-03
 *@see http://javatechig.com/android/json-feed-reader-in-android/
 *
 *<p></p>
 */

package owuor.f8th.adapters;

import java.util.List;

import owuor.f8th.R;
import owuor.f8th.types.User;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PeopleProfileAdapter extends ArrayAdapter<User> {

	private LayoutInflater layoutInflater;

	public PeopleProfileAdapter(Context context) {
		super(context, android.R.layout.simple_list_item_2);
		layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}// end of constructor

	public void setUserList(List<User> data) {
		clear();
		if (data != null) {
			for (User appEntry : data) {
				add(appEntry);
			}
			notifyDataSetChanged();
		}
	}
	
	public void setUserPhoto(byte[] photo){
		
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.list_people_search_row, null);
			holder = new ViewHolder();
			
			holder.photoView = (ImageView)convertView.findViewById(R.id.imgProfile);
			holder.nameView = (TextView) convertView.findViewById(R.id.txtNames);
			holder.genderView = (TextView) convertView.findViewById(R.id.txtGender);
			holder.countryView = (TextView) convertView.findViewById(R.id.txtCountry);
			//holder.views = (TextView) convertView.findViewById(R.id.txtViews);
			convertView.setTag(holder);
			
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		User user = getItem(position);
		holder.nameView.setText(user.getFname() + " " + user.getLname());
		holder.genderView.setText(user.getGender());
		holder.countryView.setText(user.getCountry());
		//holder.views.setText(user.getViews());

		return convertView;
	}// end of method getView()

	static class ViewHolder {
		ImageView photoView;
		TextView nameView, genderView, countryView;//,views;
	}

}// END OF CLASS PeopleProfileAdapter