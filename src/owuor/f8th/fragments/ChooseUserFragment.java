package owuor.f8th.fragments;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.app.ProgressDialog;
import org.holoeverywhere.widget.Button;

import owuor.f8th.R;
import owuor.f8th.ContentProvider.F8thContentProvider;
import owuor.f8th.database.LocalUsersTable;
import owuor.f8th.dialog.F8thDialog;
import owuor.f8th.interfaces.IRegisterRequestListener;
import owuor.f8th.types.F8th;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class ChooseUserFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,  OnClickListener {

	static ProgressDialog autoLoginDialog;
	Button btnSignIn,btnSignUp;

	boolean  connectedToService = false,signingIn = false;


	IRegisterRequestListener authListener;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.choose_user, container, false);

		return view;
	}// end of method onCreateview()

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		//getLoaderManager().initLoader(0, null, this);
		
		btnSignIn = (Button)getView().findViewById(R.id.btnGoSignIn);
		btnSignUp = (Button)getView().findViewById(R.id.btnGoSignUp);

		btnSignIn.setOnClickListener(this);
		btnSignUp.setOnClickListener(this);
		
		autoLoginDialog = new ProgressDialog(getActivity());
		autoLoginDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		autoLoginDialog.setMessage("please wait...");
		autoLoginDialog.show();
	}// end of method onActivityCreated()

	

	
	private BroadcastReceiver myReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			 Log.e("LoginFragment","broadcast received");
			if (intent.getAction().equalsIgnoreCase(F8th.CONNECTED_TO_SERVICE)) {
				connectedToService = true;
				getLoaderManager().initLoader(0, null, ChooseUserFragment.this);
			}
		}
	};// end of class myReceiver()

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			authListener = (IRegisterRequestListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement IRegisterRequestListener");
		}
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(myReceiver, new IntentFilter(F8th.CONNECTED_TO_SERVICE));
		
	}// end of method onAttach()

	public void onDetach() {
		super.onDetach();
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(myReceiver);
		if (autoLoginDialog.isShowing()) {
			autoLoginDialog.dismiss();
		}
	}

	public static void unSetProgress() {

		if (autoLoginDialog.isShowing()) {
			autoLoginDialog.dismiss();
		}
	}

	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		if(v == btnSignIn){
			// go to sign in fragment
			FragmentTransaction ft = getFragmentManager().beginTransaction();

			ft.replace(R.id.main, new SignInFragment(), "new sign in view");
			ft.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
			ft.commit();
			
		}else if(v == btnSignUp){
			// go to sign up fragment
			
			FragmentTransaction ft = getFragmentManager().beginTransaction();

			ft.replace(R.id.main, new SignUpFragment(), "sign up view");
			ft.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
			ft.commit();
		}
		
	}//end of function

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// TODO Auto-generated method stub
		String[] projection = { LocalUsersTable.COLUMN_ID,
				LocalUsersTable.COLUMN_AUTOLOGIN, LocalUsersTable.COLUMN_EMAIL,
				LocalUsersTable.COLUMN_PASSWORD, LocalUsersTable.COLUMN_FNAME,
				LocalUsersTable.COLUMN_LNAME };

		Uri uri = Uri.parse(F8thContentProvider.CONTENT_URI + "/" + LocalUsersTable.TABLE_LOCAL_USERS);
		CursorLoader cursorLoader = new CursorLoader(getActivity(),uri, projection, null, null, null);
		// Log.e("Local database","cursor loader returned");
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// TODO Auto-generated method stub
		// Log.e("Local database","load finished");
		if (data != null && data.getCount() > 0) {

			if (connectedToService && !signingIn) {
				int size = data.getCount();
				if (size > 0) {
					data.moveToPosition(0);
					String email = data.getString(data.getColumnIndex(LocalUsersTable.COLUMN_EMAIL));
					String passwd = data.getString(data.getColumnIndex(LocalUsersTable.COLUMN_PASSWORD));
					authListener.onSignInRequested(email, passwd);
					autoLoginDialog.setMessage("signing in...");
					signingIn = true;
				}
			} 
			
		} else {
			//display welcome dialog
			F8thDialog dialog;
			dialog = new F8thDialog(getActivity());
			String welcome = getResources().getString(R.string.warning_welcome);
			dialog.alertDialog(welcome,Color.GRAY);
		}

		if (!signingIn && autoLoginDialog.isShowing()) {
			autoLoginDialog.dismiss();
		}


	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// TODO Auto-generated method stub
	}

	

}
