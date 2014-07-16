package owuor.f8th.fragments;

import java.util.ArrayList;
import java.util.List;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.EditText;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.TextView;
import org.holoeverywhere.widget.Toast;

import owuor.f8th.R;
import owuor.f8th.ContentProvider.F8thContentProvider;
import owuor.f8th.adapters.SelectGroupsAdapter;
import owuor.f8th.database.GroupsTable;
import owuor.f8th.interfaces.IUserManagerRequestListener;
import owuor.f8th.types.Group;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TableLayout;

public class TellStoryFragment extends Fragment implements OnClickListener,LoaderManager.LoaderCallbacks<Cursor>{

	IUserManagerRequestListener serviceListener;
	
	EditText editCompose;
	ListView listVisible;
	TextView txtEmptyView;
	TableLayout tblContainer;
	Button btnTell;
	
	SelectGroupsAdapter groupsAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstancestate) {
		View view = inflater.inflate(R.layout.story_compose, container,false);

		return view;
	}// end of method onCreateView()
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		editCompose = (EditText)getView().findViewById(R.id.editCompose);
		listVisible = (ListView)getView().findViewById(R.id.listVisibility);
		txtEmptyView = (TextView)getView().findViewById(R.id.txtEmptyView);
		tblContainer = (TableLayout)getView().findViewById(R.id.tblContainer);
		btnTell = (Button)getView().findViewById(R.id.btnTell);
		
		groupsAdapter = new SelectGroupsAdapter(getActivity());
		listVisible.setAdapter(groupsAdapter);
		btnTell.setOnClickListener(this);
		
		getLoaderManager().initLoader(0, null, this);
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
	
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
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
			serviceListener.onTellStoryRequested(strCompose, strVisible);
			this.getActivity().getSupportFragmentManager().popBackStack();
		}
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// TODO Auto-generated method stub
		String[] projection = { GroupsTable.COLUMN_GRP_PHOTO,GroupsTable.COLUMN_GRP_ID,GroupsTable.COLUMN_GRP_NAME};
		String selection = GroupsTable.COLUMN_MID + ">'0'";
		Uri uri = Uri.parse(F8thContentProvider.CONTENT_URI + "/" + GroupsTable.TABLE_GROUPS);
		CursorLoader cursorLoader = new CursorLoader(getActivity(), uri,projection, selection, null, null);
		// Log.e("Local database","cursor loader returned");
		return cursorLoader;
	}
	
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		// TODO Auto-generated method stub

		if (cursor != null && cursor.getCount() > 0) {
			List<Group> groups = new ArrayList<Group>();
			int recordSize = cursor.getCount();
			for(int i=0;i<recordSize;i++){
				cursor.moveToPosition(i);
				String grpName = cursor.getString(cursor.getColumnIndexOrThrow(GroupsTable.COLUMN_GRP_NAME));
				String grpId = cursor.getString(cursor.getColumnIndexOrThrow(GroupsTable.COLUMN_GRP_ID));
				
				Group group = new Group("","", grpId,"","", grpName, "","","", "","");
				groups.add(group);
			}
			groupsAdapter.setGroupList(groups);
			txtEmptyView.setVisibility(View.GONE);
			tblContainer.setVisibility(View.VISIBLE);
		}else{
			txtEmptyView.setVisibility(View.VISIBLE);
			tblContainer.setVisibility(View.GONE);
		}
	}


	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// TODO Auto-generated method stub
		
	}

}
