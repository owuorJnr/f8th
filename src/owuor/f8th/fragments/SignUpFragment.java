package owuor.f8th.fragments;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.EditText;
import org.holoeverywhere.widget.ProgressBar;
import org.holoeverywhere.widget.Spinner;
import org.holoeverywhere.widget.TextView;

import owuor.f8th.R;
import owuor.f8th.adapters.CustomSpinnerAdapter;
import owuor.f8th.interfaces.IRegisterRequestListener;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.ScrollView;

public class SignUpFragment extends Fragment implements OnClickListener, OnFocusChangeListener {

	TextView error, login;
	EditText fname, lname, passwd, passwd2, country, email;
	Spinner gender;
	CustomSpinnerAdapter genderAdapter = null;
	static Button btnSignup;
	static ProgressBar progSignUp;
	ScrollView scroll;
	Runnable MoveUp;
	Boolean internet = true;

	IRegisterRequestListener authListener;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstancestate) {
		View view = inflater.inflate(R.layout.signup, container, false);

		return view;
	}// end of method onCreateView()

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		error = (TextView) getView().findViewById(R.id.txtBlankError);
		login = (TextView) getView().findViewById(R.id.txtLogin);
		email = (EditText) getView().findViewById(R.id.editUsername);
		fname = (EditText) getView().findViewById(R.id.editFirstname);
		lname = (EditText) getView().findViewById(R.id.editLastname);
		passwd = (EditText) getView().findViewById(R.id.editPassword);
		passwd2 = (EditText) getView().findViewById(R.id.editPassword2);
		country = (EditText) getView().findViewById(R.id.editCountry);
		gender = (Spinner) getView().findViewById(R.id.spinnerGender);
		// verified = (CheckBox)getView().findViewById(R.id.chboxUsername);
		btnSignup = (Button) getView().findViewById(R.id.btnRegister);
		progSignUp = (ProgressBar) getView().findViewById(R.id.progressSignUp);
		scroll = (ScrollView) getView().findViewById(R.id.scroll);

		genderAdapter = new CustomSpinnerAdapter(getActivity(), "Gender");
		genderAdapter.setListData(this.getResources().getStringArray(R.array.gender));
		gender.setAdapter(genderAdapter);

		login.setOnClickListener(this);
		btnSignup.setOnClickListener(this);
		email.setOnFocusChangeListener(this);

		MoveUp = new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				scroll.fullScroll(ScrollView.FOCUS_UP);
			}
		};// end of class Runnable()

	}// end of method onActivityCreated()

	public static void unSetProgress() {
		if (progSignUp != null && progSignUp.isShown() && btnSignup != null) {
			progSignUp.setVisibility(View.INVISIBLE);
			btnSignup.setVisibility(View.VISIBLE);
		}
	}// end of method

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

	}// end of method onSaveInstanceState()

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			authListener = (IRegisterRequestListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement IRegisterRequestListener");
		}
	}// end of method onAttach()

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == login) {

			FragmentTransaction ft = getFragmentManager().beginTransaction();

			ft.replace(R.id.main, new SignInFragment(), "new sign in view");
			ft.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
			ft.commit();

		} else if (v == btnSignup) {
			// check save and close
			String strEmail = email.getText().toString();
			String strFname = fname.getText().toString();
			String strLname = lname.getText().toString();
			String strCountry = country.getText().toString();
			String strGender = gender.getSelectedItem().toString();
			String strPasswd = passwd.getText().toString();
			String strPasswd2 = passwd2.getText().toString();

			error.setVisibility(View.VISIBLE);

			if (strEmail.equalsIgnoreCase("") || strFname.equalsIgnoreCase("")
					|| strLname.equalsIgnoreCase("")
					|| strCountry.equalsIgnoreCase("")
					|| strPasswd.equalsIgnoreCase("")
					|| strPasswd2.equalsIgnoreCase("")
					|| gender.getSelectedItemPosition() <= 0) {

				error.setText("some fields empty");
				scroll.post(MoveUp);
				// error.setVisibility(View.VISIBLE);
			} else if (strEmail.contains("|") || strFname.contains("|")
					|| strLname.contains("|")
					|| strCountry.toString().contains("|")) {
				error.setText("remove character '|' from any text");
				scroll.post(MoveUp);
			} else if (!strPasswd.contentEquals(strPasswd2)) {
				// passwords do not match
				error.setText("passwords do not match");
				scroll.post(MoveUp);
				passwd2.setText("");

			} else if (!internet) {
				// no data connection
				error.setText("check your internet settings");
				scroll.post(MoveUp);

			} else {
				// save data in both online and offline databases

				if (strEmail.contains("'") || strEmail.contains(";")
						|| strEmail.contains("\"")) {
					error.setVisibility(View.VISIBLE);
					error.setText("invalid email address");
					scroll.post(MoveUp);

				} else if (strEmail.contains("@") && strEmail.contains(".")) {

					if (!progSignUp.isShown()) {
						progSignUp.setVisibility(View.VISIBLE);
						btnSignup.setVisibility(View.INVISIBLE);
					}

					error.setVisibility(View.INVISIBLE);
					authListener.onSignUpRequested(strEmail, strFname,
							strLname, strGender, strCountry, strPasswd);
					// exit page
					// call detail page which after saving calls login page
				} else {
					error.setVisibility(View.VISIBLE);
					error.setText("invalid email address");
					scroll.post(MoveUp);
				}
			}
		}
	}// end of method onClick()

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		// TODO Auto-generated method stub
		if (v == email && !hasFocus) {
			// verify username from online database
			// if not available
			error.setText("username already in use");
			error.setVisibility(View.VISIBLE);

			error.setVisibility(View.INVISIBLE);
		}
	}// end of method onFocusChange()

}