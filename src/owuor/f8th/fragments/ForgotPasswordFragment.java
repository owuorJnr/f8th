package owuor.f8th.fragments;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.EditText;
import org.holoeverywhere.widget.ProgressBar;
import org.holoeverywhere.widget.TextView;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import owuor.f8th.R;
import owuor.f8th.interfaces.IRegisterRequestListener;


public class ForgotPasswordFragment extends Fragment implements OnClickListener {

	TextView error, signin;
	EditText email;
	static Button btnResetPasswd;
	static ProgressBar progResetPasswd;

	Boolean internet = true;

	IRegisterRequestListener authListener;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.forgot_password, container, false);

		return view;
	}// end of method onCreateview()

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		error = (TextView) getView().findViewById(R.id.txtForgotError);
		signin = (TextView) getView().findViewById(R.id.txtForgotSignin);
		email = (EditText) getView().findViewById(R.id.editForgotEmail);
		btnResetPasswd = (Button) getView().findViewById(R.id.btnResetPasswd);
		progResetPasswd = (ProgressBar) getView().findViewById(R.id.progressResetPasswd);

		signin.setOnClickListener(this);
		btnResetPasswd.setOnClickListener(this);

	}// end of method onActivityCreated()

	public static void unSetProgress() {
		if (progResetPasswd != null && progResetPasswd.isShown() && btnResetPasswd != null) {
			progResetPasswd.setVisibility(View.INVISIBLE);
			btnResetPasswd.setVisibility(View.VISIBLE);
		}
	}// end of method

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
		if (v == signin) {

			FragmentTransaction ft = getFragmentManager().beginTransaction();

			ft.replace(R.id.main, new SignInFragment(), "new sign in view");
			ft.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
			ft.commit();

		} else if (v == btnResetPasswd) {
			// check save and close
			error.setVisibility(View.VISIBLE);

			if (email.getText().toString().equalsIgnoreCase("")) {

				error.setText("enter email address");
			} else if (!internet) {
				error.setText("check your internet settings");

			} else {
				// save data in both online and offline databases
				String address = email.getText().toString();
				if (address.contains("@") && address.contains(".")) {
					if (!progResetPasswd.isShown()) {
						progResetPasswd.setVisibility(View.VISIBLE);
						btnResetPasswd.setVisibility(View.INVISIBLE);
					}

					error.setVisibility(View.INVISIBLE);
					authListener.onForgotpasswordRequested(email.getText()
							.toString());
					// exit page
					// call detail page which after saving calls login page
				} else {
					error.setText("invalid email address");
				}
			}
		}
	}// end of method onClick()

}// END OF CLASS ForgotPasswordFragment