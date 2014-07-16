/**
 * All Honor and Glory belongs to God Almighty
 * Phil 2:13
 *@author Dickson Owuor <dickytea@gmail.com>
 *@version 1.1
 *@since 2014-1-03
 *@see http://tausiq.wordpress.com/2012/12/12/android-custom-adapter-listview-with-listfragment-and-loadermanager-inside-fragmentactivity/
 *
 *<p></p>
 */

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
import owuor.f8th.adapters.StoriesAdapter;
import owuor.f8th.database.StoriesTable;
import owuor.f8th.interfaces.IUserManagerRequestListener;
import owuor.f8th.types.F8th;
import owuor.f8th.types.Story;
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

public class StoriesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, OnItemClickListener, OnRefreshListener, OnScrollListener{

	public static PullToRefreshListView listStories;
	public static TextView emptyView;
	ProgressBar listProgress;
	
	private StoriesAdapter storiesAdapter;
	
	IUserManagerRequestListener serviceListener;
	boolean nextListLoaded = false,alreadyDownloaded = false;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstancestate) {
		View view = inflater.inflate(R.layout.list_pull_to_refresh, container,false);
		
		return view;
	}// end of method onCreateView()
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		getSupportActionBar().setTitle("HOME");
		getSupportActionBar().setSubtitle("stories");
		
		listStories = (PullToRefreshListView)getView().findViewById(R.id.pull_to_refresh_listview);
		listProgress = (ProgressBar)getView().findViewById(R.id.progressList);
		
		emptyView = (TextView) getView().findViewById(R.id.empty_list_item);
		emptyView.setText("Unable to load Stories");
		listStories.setEmptyView(emptyView);
		
		//initialize adapter and set it
		storiesAdapter = new StoriesAdapter(getActivity());
		
		getLoaderManager().initLoader(0, null, this);
		
		listStories.setAdapter(storiesAdapter);
		listStories.setOnItemClickListener(this);
		listStories.setOnRefreshListener(this);
		listStories.setOnScrollListener(this);
		listProgress.setVisibility(View.VISIBLE);

	}
	
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			serviceListener = (IUserManagerRequestListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement IUserManagerRequestListener");
		}
		
	}// end of method onAttach()

	public void onDetach() {
		super.onDetach();
	}
	
	
	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        //
		Story story = (Story) listStories.getItemAtPosition(position+1);
		serviceListener.onViewStoryRequested(story);
		
    }
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// TODO Auto-generated method stub
		String[] projection = { StoriesTable.COLUMN_ID,
				StoriesTable.COLUMN_SID, StoriesTable.COLUMN_STORY_ID,
				StoriesTable.COLUMN_STORY, StoriesTable.COLUMN_AUTHOR_ID, StoriesTable.COLUMN_AUTHOR,
				StoriesTable.COLUMN_AUTHOR_PHOTO, StoriesTable.COLUMN_FAVS,
				StoriesTable.COLUMN_isFAV,StoriesTable.COLUMN_isOWNER,StoriesTable.COLUMN_GROUP_VISIBILITY};

		Uri uri = Uri.parse(F8thContentProvider.CONTENT_URI + "/" + StoriesTable.TABLE_STORIES);
		String selection = StoriesTable.COLUMN_favID + "='0'";
		CursorLoader cursorLoader = new CursorLoader(getActivity(), uri,projection, selection, null, null);
		// Log.e("Local database","cursor loader returned");
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// TODO Auto-generated method stub
		if (data != null && data.getCount() > 0) {
			List<Story> stories = new ArrayList<Story>();
			int size = data.getCount();
			for (int i = 0; i < size; i++) {
				data.moveToPosition(i);
				String sid = data.getString(data.getColumnIndex(StoriesTable.COLUMN_SID));
				String storyId = data.getString(data.getColumnIndex(StoriesTable.COLUMN_STORY_ID));
				String detail = data.getString(data.getColumnIndex(StoriesTable.COLUMN_STORY));
				String authorId = data.getString(data.getColumnIndex(StoriesTable.COLUMN_AUTHOR_ID));
				String author = data.getString(data.getColumnIndex(StoriesTable.COLUMN_AUTHOR));
				String favs = data.getString(data.getColumnIndex(StoriesTable.COLUMN_FAVS));
				String isFav = data.getString(data.getColumnIndex(StoriesTable.COLUMN_isFAV));
				String isOwner = data.getString(data.getColumnIndex(StoriesTable.COLUMN_isOWNER));
				String grpVisible = data.getString(data.getColumnIndex(StoriesTable.COLUMN_GROUP_VISIBILITY));
				
				Story story = new Story("",sid,storyId,detail,authorId,author,favs,isFav,isOwner,grpVisible);
				//story.AuthorPhoto;
				stories.add(story);
			}
			storiesAdapter.setStoryList(stories);
			Log.e("StoriesFragment", "List Adapter set");
		}else if(!alreadyDownloaded){
			alreadyDownloaded = true;
			serviceListener.onStoryListRequested(F8th.LIST_FIRST_TIME, "");
		}else{
			//do nothing
			storiesAdapter.setStoryList(null);
		}
		
		listProgress.setVisibility(View.GONE);
		nextListLoaded = false;
		if(listStories.isRefreshing()){
			listStories.onRefreshComplete();
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		if(visibleItemCount > F8th.LIST_LIMIT && !listStories.isRefreshing()){
			//screen can handle more than 20 items
			if(!nextListLoaded){
				alreadyDownloaded = false;
				nextListLoaded = true;
				int itemsCount = listStories.getCount();
				Log.e("StoriesFragment", String.valueOf(itemsCount));
				Story lastStory = (Story) listStories.getItemAtPosition(itemsCount-1);
				serviceListener.onStoryListRequested(F8th.LIST_LOADMORE, lastStory.getSID());
			}
		}else if(firstVisibleItem + visibleItemCount >= totalItemCount  && visibleItemCount > F8th.LIST_LIMIT && !listStories.isRefreshing()){
			//user has scrolled to last item
			if(!nextListLoaded){
				alreadyDownloaded = false;
				nextListLoaded = true;
				int itemsCount = listStories.getCount();
				Log.e("StoriesFragment", String.valueOf(itemsCount));
				Story lastStory = (Story) listStories.getItemAtPosition(itemsCount-1);
				serviceListener.onStoryListRequested(F8th.LIST_LOADMORE, lastStory.getSID());
				
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
		serviceListener.onStoryListRequested(F8th.LIST_REFRESH, "");
	}
	
}
