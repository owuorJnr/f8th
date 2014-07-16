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
import owuor.f8th.types.Story;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class StoriesAdapter extends ArrayAdapter<Story> {


	private LayoutInflater layoutInflater;

	public StoriesAdapter(Context context) {
		super(context, android.R.layout.simple_list_item_2);
		layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}// end of constructor

	public void setStoryList(List<Story> data) {
		clear();
		if (data != null) {
			for (Story appEntry : data) {
				add(appEntry);
			}
			notifyDataSetChanged();
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.list_story_row, null);
			holder = new ViewHolder();
			
			holder.photoView = (ImageView)convertView.findViewById(R.id.imgAuthor);
			holder.author = (TextView) convertView.findViewById(R.id.txtAuthor);
			holder.story = (TextView) convertView.findViewById(R.id.txtStory);
			holder.favorite = (TextView) convertView.findViewById(R.id.txtFavorite);
			holder.visibility = (TextView) convertView.findViewById(R.id.txtVisibility);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Story story = getItem(position);
		holder.author.setText(story.getAuthor());
		holder.story.setText(story.getStory());
		holder.favorite.setText("Favorites: "+story.getFavorite());
		holder.visibility.setText(story.getVisibility());

		//String fav = story.getIsFavorite();
		//check if favorite or not
		
		return convertView;
	}// end of method getView()

	static class ViewHolder {
		ImageView photoView;
		TextView author, story, favorite,visibility;
	}

}// END OF CLASS StoryAdapter