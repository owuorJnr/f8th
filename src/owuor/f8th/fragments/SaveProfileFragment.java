package owuor.f8th.fragments;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.EditText;
import org.holoeverywhere.widget.ProgressBar;
import org.holoeverywhere.widget.TextView;

import owuor.f8th.R;
import owuor.f8th.interfaces.IRegisterRequestListener;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ScrollView;

public class SaveProfileFragment extends Fragment implements OnClickListener{

	TextView error;
	EditText about, why, desire;
	static Button btnSave;
	static ProgressBar progSave;
	ScrollView scroll;
	Runnable MoveUp;
	Boolean internet = true;

	IRegisterRequestListener authListener;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstancestate) {
		View view = inflater.inflate(R.layout.settings_profile, container, false);

		return view;
	}// end of method onCreateView()
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		error = (TextView) getView().findViewById(R.id.txtBlankError);
		about = (EditText) getView().findViewById(R.id.editAbout);
		why = (EditText) getView().findViewById(R.id.editWhy);
		desire = (EditText) getView().findViewById(R.id.editDesire);
		btnSave = (Button) getView().findViewById(R.id.btnSave);
		progSave = (ProgressBar) getView().findViewById(R.id.progressSave);
		scroll = (ScrollView) getView().findViewById(R.id.scroll);


		btnSave.setOnClickListener(this);

		MoveUp = new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				scroll.fullScroll(ScrollView.FOCUS_UP);
			}
		};// end of class Runnable()

	}// end of method onActivityCreated()
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			authListener = (IRegisterRequestListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement IRegisterRequestListener");
		}
	}// end of method onAttach()
	
	public static void unSetProgress() {
		if (progSave != null && progSave.isShown() && btnSave != null) {
			progSave.setVisibility(View.INVISIBLE);
			btnSave.setVisibility(View.VISIBLE);
		}
	}// end of method
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v == btnSave){
			String strAbout = about.getText().toString();
			String strWhy = why.getText().toString();
			String strDesire = desire.getText().toString();
			
			error.setVisibility(View.VISIBLE);
			
			if(strAbout.equalsIgnoreCase("") || strWhy.equalsIgnoreCase("") || strDesire.equalsIgnoreCase("")){
				error.setText("some fields empty");
				scroll.post(MoveUp);
			}else if (strAbout.contains("|") || strWhy.contains("|") || strDesire.contains("|")){
				error.setText("remove character '|' from any text");
				scroll.post(MoveUp);
			}else{
				if (!progSave.isShown()) {
					progSave.setVisibility(View.VISIBLE);
					btnSave.setVisibility(View.INVISIBLE);
				}

				error.setVisibility(View.INVISIBLE);
				authListener.onSaveProfileRequested(strAbout, strWhy, strDesire);
			}
		}
		
	}//end of onClick

}//END OF CLASS SaveProfileFragment
