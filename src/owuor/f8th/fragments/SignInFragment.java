package owuor.f8th.fragments;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.EditText;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.ProgressBar;
import org.holoeverywhere.widget.TextView;

import owuor.f8th.R;
import owuor.f8th.interfaces.IRegisterRequestListener;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class SignInFragment extends Fragment implements OnClickListener {

	TextView error, signup, forgotPasswd;
	EditText email, passwd;
	static Button btnLogin;
	static ProgressBar progLogin;
	ListView listEmail;

	boolean localUsers = false, connectedToService = false;
	Dialog dialog;

	IRegisterRequestListener authListener;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstancestate) {
		View view = inflater.inflate(R.layout.signin, container, false);

		return view;
	}// end of method onCreateView()

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		error = (TextView) getView().findViewById(R.id.txtError);
		signup = (TextView) getView().findViewById(R.id.txtSignup);
		forgotPasswd = (TextView) getView().findViewById(R.id.txtForgotPassword);
		email = (EditText) getView().findViewById(R.id.loginEmail);
		passwd = (EditText) getView().findViewById(R.id.loginPass);
		btnLogin = (Button) getView().findViewById(R.id.btnLogin);
		progLogin = (ProgressBar) getView().findViewById(R.id.progressSignIn);

		email.clearFocus();
		passwd.clearFocus();
		btnLogin.requestFocus();

		signup.setOnClickListener(this);
		forgotPasswd.setOnClickListener(this);
		btnLogin.setOnClickListener(this);

	}// end of method onActivityCreated()

	public static void unSetProgress() {
		if (progLogin != null && progLogin.isShown() && btnLogin != null) {
			progLogin.setVisibility(View.INVISIBLE);
			btnLogin.setVisibility(View.VISIBLE);
		}
	}// end of method

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			authListener = (IRegisterRequestListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement IRegisterRequestListener");
		}

	}// end of method onAttach()

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == signup) {
			// go to sign up fragment
			FragmentTransaction ft = getFragmentManager().beginTransaction();

			ft.replace(R.id.main, new SignUpFragment(), "sign up view");
			ft.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
			ft.commit();

		} else if (v == forgotPasswd) {
			// go to forgot password fragment
			FragmentTransaction ft = getFragmentManager().beginTransaction();

			ft.replace(R.id.main, new ForgotPasswordFragment(),"forgot password view");
			ft.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
			ft.commit();

		} else if (v == btnLogin) {
			// check and login user
			// check save and close
			error.setVisibility(View.VISIBLE);

			String strEmail = email.getText().toString();
			String strPasswd = passwd.getText().toString();

			if (strEmail.equalsIgnoreCase("") || strPasswd.equalsIgnoreCase("")) {

				error.setText("enter username and password");

			} else {
				// login user both offline and online
				if (strEmail.contains("'") || strEmail.contains(";") || strEmail.contains("\"")) {
					error.setVisibility(View.VISIBLE);
					error.setText("invalid email address");

				} else if (strEmail.contains("@") && strEmail.contains(".")) {
					// authenticate user
					if (!progLogin.isShown()) {
						progLogin.setVisibility(View.VISIBLE);
						btnLogin.setVisibility(View.INVISIBLE);
					}
					error.setVisibility(View.INVISIBLE);
					authListener.onSignInRequested(strEmail, strPasswd);
				} else {
					error.setVisibility(View.VISIBLE);
					error.setText("invalid email address");
				}
			}
		}
	}// end of method onClick()

}