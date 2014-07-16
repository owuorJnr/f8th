package owuor.f8th.fragments;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.EditText;
import org.holoeverywhere.widget.Spinner;
import org.holoeverywhere.widget.Toast;

import owuor.f8th.R;
import owuor.f8th.adapters.CustomSpinnerAdapter;
import owuor.f8th.interfaces.IUserManagerRequestListener;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class AddGroupFragment extends Fragment implements OnClickListener{

	IUserManagerRequestListener serviceListener;
	
	EditText editGrpName,editGrpTypeOther,editCity,editCountry;
	Spinner spinnerGrpType;
	Button btnCreate;
	
	CustomSpinnerAdapter grpTypeAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstancestate) {
		View view = inflater.inflate(R.layout.group_create, container,false);

		return view;
	}// end of method onCreateView()
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getSupportActionBar().setTitle("HOME");
		getSupportActionBar().setSubtitle("add group");
		
		editGrpName = (EditText)getView().findViewById(R.id.editGrpName);
		editGrpTypeOther  = (EditText)getView().findViewById(R.id.editGrpOther);
		editCity = (EditText)getView().findViewById(R.id.editGrpCity);
		editCountry  = (EditText)getView().findViewById(R.id.editGrpCountry);
		spinnerGrpType = (Spinner)getView().findViewById(R.id.spinnerGrpType);
		btnCreate = (Button)getView().findViewById(R.id.btnCreate);
		
		grpTypeAdapter = new CustomSpinnerAdapter(getActivity(),"Group Type");
		grpTypeAdapter.setListData(getActivity().getResources().getStringArray(R.array.group_type));
		spinnerGrpType.setAdapter(grpTypeAdapter);
		
		btnCreate.setOnClickListener(this);
		btnCreate.setText("create");
		
	}//end of method onActivityCreated()


	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			serviceListener = (IUserManagerRequestListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement UserRequestServiceListener");
		}

	}// end of method onAttach()
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v == btnCreate){
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
				serviceListener.onCreateGroupRequested(strGrpName, strGrpType,strGrpCity,strGrpCountry);
				this.getActivity().getSupportFragmentManager().popBackStack();
			}
		}
		
	}
	
}