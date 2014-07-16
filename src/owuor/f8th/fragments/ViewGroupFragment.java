package owuor.f8th.fragments;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.EditText;
import org.holoeverywhere.widget.LinearLayout;
import org.holoeverywhere.widget.Spinner;
import org.holoeverywhere.widget.TextView;
import org.holoeverywhere.widget.Toast;

import owuor.f8th.R;
import owuor.f8th.ContentProvider.F8thContentProvider;
import owuor.f8th.adapters.CustomSpinnerAdapter;
import owuor.f8th.database.GroupsTable;
import owuor.f8th.interfaces.IUserManagerRequestListener;
import owuor.f8th.types.F8th;
import owuor.f8th.types.Group;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

public class ViewGroupFragment extends Fragment implements OnClickListener,LoaderManager.LoaderCallbacks<Cursor> {

	IUserManagerRequestListener serviceListener;
	
	private Group group;
	private String grpId;
	private String userId = "";
	
	Button btnJoin,btnLeave;
	ImageView imgGroup,imgBack,imgEdit,imgDelete;
	TextView txtGrpName,txtGrpType,txtGrpSize,txtGrpLocation,txtGrpOwner,txtEmptyView;
	
	LinearLayout profileView;
	
	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.view_group,container,false);
		
		return view;
	}//end of method onCreateview()
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		getSupportActionBar().setTitle("GROUP");
		
		imgGroup = (ImageView)getView().findViewById(R.id.imgGroup);
		//imgBack = (ImageView)getView().findViewById(R.id.imgBack);

		txtEmptyView = (TextView)getView().findViewById(R.id.txtEmptyView);
		profileView = (LinearLayout)getView().findViewById(R.id.llGroupView);
		
		btnJoin = (Button)getView().findViewById(R.id.btnJoin);
		btnLeave = (Button)getView().findViewById(R.id.btnLeave);
		imgEdit = (ImageView)getView().findViewById(R.id.imgEditGrp);
		imgDelete = (ImageView)getView().findViewById(R.id.imgDeleteGrp);
		txtGrpName = (TextView)getView().findViewById(R.id.txtGrpName);
		txtGrpType = (TextView)getView().findViewById(R.id.txtGrpType);
		txtGrpSize = (TextView)getView().findViewById(R.id.txtGrpSize);
		txtGrpLocation = (TextView)getView().findViewById(R.id.txtGrpLocation);
		txtGrpOwner = (TextView)getView().findViewById(R.id.txtGrpOwner);
		
		if(group != null){
			setView(group);
		}
		
		//imgBack.setOnClickListener(this);
		imgEdit.setOnClickListener(this);
		imgDelete.setOnClickListener(this);
		btnLeave.setOnClickListener(this);
		btnJoin.setOnClickListener(this);
		
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
	
	/*public void setGroup(String userId,Group group){
		this.userId = userId;
		this.group = group;

	}*/
	
	
	public void setGroupId(String userId,String grpId){
		this.grpId = grpId;
		this.userId = userId;
	}
	
	private void setView(Group group){
		
		if(group != null){
			txtEmptyView.setVisibility(View.GONE);
			profileView.setVisibility(View.VISIBLE);
			
			this.group = group;
			this.getSupportActionBar().setSubtitle(group.getName());
			
			if(group.getOwnerId().equalsIgnoreCase(userId)){
				imgEdit.setVisibility(View.VISIBLE);
				imgDelete.setVisibility(View.VISIBLE);
				
			}else{
				imgEdit.setVisibility(View.GONE);
				imgDelete.setVisibility(View.GONE);
			}
				
			if(group.getUserType().equalsIgnoreCase(F8th.GROUP_MEMBER)){
				btnJoin.setVisibility(View.GONE);
				btnLeave.setVisibility(View.VISIBLE);
			}else if(group.getUserType().equalsIgnoreCase(F8th.GROUP_NON_MEMBER)){
				btnJoin.setVisibility(View.VISIBLE);
				btnLeave.setVisibility(View.GONE);
			}
			
			txtGrpName.setText(group.getName());
			txtGrpType.setText(group.getType());
			txtGrpSize.setText(group.getSize()+" members");
			txtGrpLocation.setText(group.getCity()+", "+group.getCountry());
			txtGrpOwner.setText("created by: "+group.getOwnerName());
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
			View view = View.inflate(getActivity(), R.layout.group_create, null);
			final Dialog dialog = new Dialog(getActivity());
			dialog.setContentView(view);
			
			final EditText editGrpName = (EditText)view.findViewById(R.id.editGrpName);
			final EditText editGrpTypeOther = (EditText)view.findViewById(R.id.editGrpOther);
			final EditText editCity = (EditText)view.findViewById(R.id.editGrpCity);
			final EditText editCountry  = (EditText)view.findViewById(R.id.editGrpCountry);
			final Spinner spinnerGrpType = (Spinner)view.findViewById(R.id.spinnerGrpType);
			final Button btnEdit = (Button)view.findViewById(R.id.btnCreate);
			
			btnEdit.setText("Edit");
			editGrpName.setText(group.getName());
			editGrpTypeOther.setText(group.getType());
			editCity.setText(group.getCity());
			editCountry.setText(group.getCountry());
			
			CustomSpinnerAdapter grpTypeAdapter = new CustomSpinnerAdapter(getActivity(),"Group Type");;
			grpTypeAdapter.setListData(getActivity().getResources().getStringArray(R.array.group_type));
			spinnerGrpType.setAdapter(grpTypeAdapter);
			
			btnEdit.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String strGrpName = editGrpName.getText().toString();
					String strGrpType = "";
					String strGrpCity = editCity.getText().toString();
					String strGrpCountry = editCountry.getText().toString();
					
					if(spinnerGrpType.getSelectedItemPosition() > 0){
						strGrpType = spinnerGrpType.getSelectedItem().toString();
					}else{
						strGrpType = editGrpTypeOther.getText().toString();
					}
					
					if(strGrpName.equalsIgnoreCase("") || strGrpType.equalsIgnoreCase("") || strGrpCity.equalsIgnoreCase("") || strGrpCountry.equalsIgnoreCase("")){
						Toast.makeText(getActivity(), "enter group name, select/enter type and group location", Toast.LENGTH_SHORT).show();
					}else{
						serviceListener.onEditGroupRequested(group.getGroupId(), strGrpName, strGrpType,strGrpCity,strGrpCountry);
						dialog.dismiss();
					}
				}
				
			});
			
			WindowManager.LayoutParams wndw = dialog.getWindow().getAttributes();
			wndw.width = ViewGroup.LayoutParams.MATCH_PARENT;
			wndw.height = ViewGroup.LayoutParams.MATCH_PARENT;
			dialog.show();
			
		}else if(v == imgDelete){
			
			
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(F8th.DIALOG_TITLE);
			builder.setMessage("Delete Group: "+group.getName()+" ?");
			builder.setPositiveButton("yes",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							serviceListener.onDeleteGroupRequested(group.getGroupId());
							
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
			
		}else if(v == btnLeave){
			serviceListener.onLeaveGroupRequested(group.getGroupId());
		}else if(v == btnJoin){
			serviceListener.onJoinGroupRequested(group.getGroupId(), F8th.GROUP_MEMBER);
		}else{
			//do nothing
		}
	}
	
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// TODO Auto-generated method stub
		String[] projection = { GroupsTable.COLUMN_ID,
				GroupsTable.COLUMN_GRP_PHOTO, GroupsTable.COLUMN_MID,
				GroupsTable.COLUMN_GID, GroupsTable.COLUMN_GRP_ID,
				GroupsTable.COLUMN_GRP_OWNER_ID,GroupsTable.COLUMN_GRP_OWNER,
				GroupsTable.COLUMN_GRP_NAME,
				GroupsTable.COLUMN_GRP_SIZE,
				GroupsTable.COLUMN_GRP_TYPE,
				GroupsTable.COLUMN_GRP_CITY,
				GroupsTable.COLUMN_GRP_COUNTRY,
				GroupsTable.COLUMN_USER_TYPE };
		
		Uri uri = Uri.parse(F8thContentProvider.CONTENT_URI + "/" + GroupsTable.TABLE_GROUPS);
		String selection = GroupsTable.COLUMN_GRP_ID + "='"+grpId+"'";
		CursorLoader cursorLoader = new CursorLoader(getActivity(), uri,projection, selection, null, null);
		// Log.e("Local database","cursor loader returned");
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// TODO Auto-generated method stub

		if (data != null && data.moveToFirst()){
			
			String mid = data.getString(data.getColumnIndex(GroupsTable.COLUMN_MID));
			String gid = data.getString(data.getColumnIndex(GroupsTable.COLUMN_GID));
			String grpId = data.getString(data.getColumnIndex(GroupsTable.COLUMN_GRP_ID));
			String grpOwnerId = data.getString(data.getColumnIndex(GroupsTable.COLUMN_GRP_OWNER_ID));
			String grpOwner = data.getString(data.getColumnIndex(GroupsTable.COLUMN_GRP_OWNER));
			String grpName = data.getString(data.getColumnIndex(GroupsTable.COLUMN_GRP_NAME));
			String grpType = data.getString(data.getColumnIndex(GroupsTable.COLUMN_GRP_TYPE));
			String grpSize = data.getString(data.getColumnIndex(GroupsTable.COLUMN_GRP_SIZE));
			String grpCity = data.getString(data.getColumnIndex(GroupsTable.COLUMN_GRP_CITY));
			String grpCountry = data.getString(data.getColumnIndex(GroupsTable.COLUMN_GRP_COUNTRY));
			String userType = data.getString(data.getColumnIndex(GroupsTable.COLUMN_USER_TYPE));
			// String grpPhoto =
			// data.getString(data.getColumnIndex(GroupsTable.COLUMN_GRP_PHOTO));

			Group group = new Group(mid,gid, grpId,grpOwnerId,grpOwner, grpName, grpType, grpSize,grpCity,grpCountry,userType);
				// group.setGrpPhoto(grpPhoto);
				this.setView(group);
		}else{
			setView(null);
		}

		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// TODO Auto-generated method stub
		// groupAdapter
	}
	
}
