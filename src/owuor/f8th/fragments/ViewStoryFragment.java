package owuor.f8th.fragments;

import java.util.ArrayList;
import java.util.List;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.EditText;
import org.holoeverywhere.widget.LinearLayout;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.TextView;
import org.holoeverywhere.widget.Toast;

import owuor.f8th.R;
import owuor.f8th.ContentProvider.F8thContentProvider;
import owuor.f8th.adapters.SelectGroupsAdapter;
import owuor.f8th.database.GroupsTable;
import owuor.f8th.database.StoriesTable;
import owuor.f8th.interfaces.IUserManagerRequestListener;
import owuor.f8th.types.F8th;
import owuor.f8th.types.Group;
import owuor.f8th.types.Story;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class ViewStoryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,OnClickListener {

	IUserManagerRequestListener serviceListener;
	Story story;
	String storyId;
	
	ImageView imgBack,imgEdit,imgDelete,imgFav,imgUnFav,imgShare;
	TextView txtAuthor,txtStory,txtFavs,txtVisibility,txtEmptyView;
	
	LinearLayout profileView;
	
	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.view_story,container,false);
		
		return view;
	}//end of method onCreateview()
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);

		this.getSupportActionBar().setTitle("Story");
		//this.getSherlockActivity().getSupportActionBar().setSubtitle("");
		
		//imgBack = (ImageView)getView().findViewById(R.id.imgBack);

		txtEmptyView = (TextView)getView().findViewById(R.id.txtEmptyView);
		profileView = (LinearLayout)getView().findViewById(R.id.llStoryView);
		
		imgEdit = (ImageView)getView().findViewById(R.id.imgEditStory);
		imgDelete = (ImageView)getView().findViewById(R.id.imgDeleteStory);
		imgUnFav = (ImageView)getView().findViewById(R.id.imgFavGold);
		imgFav = (ImageView)getView().findViewById(R.id.imgFavBlack);
		imgShare = (ImageView)getView().findViewById(R.id.imgShareStory);
		txtAuthor = (TextView)getView().findViewById(R.id.txtAuthor);
		txtStory= (TextView)getView().findViewById(R.id.txtStory);
		txtFavs = (TextView)getView().findViewById(R.id.txtFavorite);
		txtVisibility = (TextView)getView().findViewById(R.id.txtVisibility);
		
		if(story != null){
			setView(story);
		}
		
		//imgBack.setOnClickListener(this);
		imgDelete.setOnClickListener(this);
		imgEdit.setOnClickListener(this);
		imgFav.setOnClickListener(this);
		imgUnFav.setOnClickListener(this);
		imgShare.setOnClickListener(this);
		
		getLoaderManager().initLoader(0, null, this);
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
	
	public void setStoryId(String storyId){
		this.storyId = storyId;
	}
	
	/*
	public void setStory(Story story){
		this.story = story;
		if(story != null){
			storyId = story.getStoryId();
		}
	}*/

	
	private void setView(Story story){
		
		if(story != null){
			txtEmptyView.setVisibility(View.GONE);
			profileView.setVisibility(View.VISIBLE);
			
			this.story = story;
			this.getSupportActionBar().setSubtitle(story.getAuthor());
			
			if(story.getIsOwner().equalsIgnoreCase(F8th.STORY_IS_OWNER)){
				imgEdit.setVisibility(View.VISIBLE);
				imgDelete.setVisibility(View.VISIBLE);
			}else if(story.getIsOwner().equalsIgnoreCase(F8th.STORY_IS_NOT_OWNER)){
				imgEdit.setVisibility(View.GONE);
				imgDelete.setVisibility(View.GONE);
			}
			
			if(story.getIsFavorite().equalsIgnoreCase(F8th.STORY_IS_FAV)){
				imgFav.setVisibility(View.GONE);
				imgUnFav.setVisibility(View.VISIBLE);
			}else if(story.getIsFavorite().equalsIgnoreCase(F8th.STORY_IS_NOT_FAV)){
				imgFav.setVisibility(View.VISIBLE);
				imgUnFav.setVisibility(View.GONE);
			}
			
			txtAuthor.setText(story.getAuthor());
			txtStory.setText(story.getStory());
			txtFavs.setText("Favorites: "+story.getFavorite());
			txtVisibility.setText(story.getVisibility());
		}else{
			txtEmptyView.setVisibility(View.VISIBLE);
			profileView.setVisibility(View.GONE);
			this.getSupportActionBar().setSubtitle("");
		}
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v == imgEdit){
			
			//edit
			View view = View.inflate(getActivity(), R.layout.story_compose, null);
			final Dialog dialog = new Dialog(getActivity());
			dialog.getWindow().requestFeature((int) Window.FEATURE_NO_TITLE);
			dialog.setContentView(view);
			
			final EditText editCompose = (EditText)view.findViewById(R.id.editCompose);
			final ListView listVisible = (ListView)view.findViewById(R.id.listVisibility);
			final TextView txtEmptyView = (TextView)view.findViewById(R.id.txtEmptyView);
			final Button btnTell = (Button)view.findViewById(R.id.btnTell);
			
			btnTell.setText("Update");
			editCompose.setText(story.getStory());
			
			SelectGroupsAdapter groupsAdapter = new SelectGroupsAdapter(getActivity());
			
			String[] projection = { GroupsTable.COLUMN_GRP_PHOTO,GroupsTable.COLUMN_GRP_ID,GroupsTable.COLUMN_GRP_NAME};
			String selection = GroupsTable.COLUMN_MID + ">'0'";
			Uri uri = Uri.parse(F8thContentProvider.CONTENT_URI + "/" + GroupsTable.TABLE_GROUPS);
			Cursor cursor = getActivity().getContentResolver().query(uri, projection, selection, null,null);
			if (cursor != null && cursor.getCount() > 0) {
				List<Group> groups = new ArrayList<Group>();
				int recordSize = cursor.getCount();
				for(int i=0;i<recordSize;i++){
					cursor.moveToPosition(i);
					String grpName = cursor.getString(cursor.getColumnIndexOrThrow(GroupsTable.COLUMN_GRP_NAME));
					String grpId = cursor.getString(cursor.getColumnIndexOrThrow(GroupsTable.COLUMN_GRP_ID));
					
					Group group = new Group("","", grpId,"","", grpName, "","","","","");
					groups.add(group);
				}
				cursor.close();
				groupsAdapter.setGroupList(groups);
			}else{
				//no group found
				cursor.close();
				txtEmptyView.setVisibility(View.VISIBLE);
				editCompose.setVisibility(View.GONE);
				btnTell.setVisibility(View.GONE);
				listVisible.setVisibility(View.GONE);
			}

			listVisible.setAdapter(groupsAdapter);
			
			btnTell.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					//update story online
					String strCompose = editCompose.getText().toString();
					String strVisible = "";
					int length = listVisible.getCount();
					Group selectedGroup;
					for(int i = 0;i<length;i++){
						selectedGroup = (Group) listVisible.getItemAtPosition(i);
						if(selectedGroup.isSelected()){
							strVisible = strVisible.concat(selectedGroup.getGroupId()+";");
						}
					}
					//send the story
					if(strCompose.equalsIgnoreCase("") || strVisible.equalsIgnoreCase("")){
						Toast.makeText(getActivity(), "Type story and select atleast one group", Toast.LENGTH_SHORT).show();
					}else{
						serviceListener.onUpdateStoryRequested(story.getStoryId(), strCompose, strVisible);
						dialog.dismiss();
					}
				}
				
			});
			
			WindowManager.LayoutParams wndw = dialog.getWindow().getAttributes();
			wndw.width = ViewGroup.LayoutParams.MATCH_PARENT;
			wndw.height = ViewGroup.LayoutParams.WRAP_CONTENT;
			dialog.show();
			
		}else if(v == imgDelete){
			
			
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(F8th.DIALOG_TITLE);
			builder.setMessage("Delete Story?");
			builder.setPositiveButton("yes",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							serviceListener.onDeleteStoryRequested(story.getStoryId());
						}
			});// end of dialog listener
			builder.setNegativeButton("no",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.dismiss();
						}
			});
			builder.create().show();
			
		}else if(v == imgFav){
			serviceListener.onFavoriteStoryRequested(story.getStoryId());
		}else if(v == imgUnFav){
			serviceListener.onUnFavoriteStoryRequested(story.getStoryId());
		}else if(v == imgShare){
			//serviceListener
			Intent shareIntent;
			shareIntent = new Intent();
			shareIntent.setAction(Intent.ACTION_SEND);
			shareIntent.putExtra(Intent.EXTRA_TEXT, txtStory.getText().toString());
			shareIntent.setType("text/plain");
			this.startActivity(Intent.createChooser(shareIntent,"choose share service"));
			//setShareIntent(shareIntent);
			
		}else{
			//do nothing
		}
		
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// TODO Auto-generated method stub
		String[] projection = { StoriesTable.COLUMN_ID,
				StoriesTable.COLUMN_SID, StoriesTable.COLUMN_favID, StoriesTable.COLUMN_STORY_ID,
				StoriesTable.COLUMN_STORY, StoriesTable.COLUMN_AUTHOR_ID, StoriesTable.COLUMN_AUTHOR,
				StoriesTable.COLUMN_AUTHOR_PHOTO, StoriesTable.COLUMN_FAVS,
				StoriesTable.COLUMN_isFAV,StoriesTable.COLUMN_isOWNER,StoriesTable.COLUMN_GROUP_VISIBILITY};

		Uri uri = Uri.parse(F8thContentProvider.CONTENT_URI + "/"+ StoriesTable.TABLE_STORIES);
		String selection = StoriesTable.COLUMN_STORY_ID + "='"+storyId+"'";
		CursorLoader cursorLoader = new CursorLoader(getActivity(), uri,projection, selection, null, null);
		// Log.e("Local database","cursor loader returned");
		return cursorLoader;
	}
	
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// TODO Auto-generated method stub

		if (data != null && data.moveToFirst()){
			
			String sid = data.getString(data.getColumnIndex(StoriesTable.COLUMN_SID));
			String favId = data.getString(data.getColumnIndex(StoriesTable.COLUMN_favID));
			String storyId = data.getString(data.getColumnIndex(StoriesTable.COLUMN_STORY_ID));
			String detail = data.getString(data.getColumnIndex(StoriesTable.COLUMN_STORY));
			String authorId = data.getString(data.getColumnIndex(StoriesTable.COLUMN_AUTHOR_ID));
			String author = data.getString(data.getColumnIndex(StoriesTable.COLUMN_AUTHOR));
			String favs = data.getString(data.getColumnIndex(StoriesTable.COLUMN_FAVS));
			String isFav = data.getString(data.getColumnIndex(StoriesTable.COLUMN_isFAV));
			String isOwner = data.getString(data.getColumnIndex(StoriesTable.COLUMN_isOWNER));
			String grpVisible = data.getString(data.getColumnIndex(StoriesTable.COLUMN_GROUP_VISIBILITY));
			
			Story story = new Story(favId,sid,storyId,detail,authorId,author,favs,isFav,isOwner,grpVisible);
			//story.AuthorPhoto;
			setView(story);
		}else{
			setView(null);
		}
			
		
	}




	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// TODO Auto-generated method stub
		
	}
	
	
}
