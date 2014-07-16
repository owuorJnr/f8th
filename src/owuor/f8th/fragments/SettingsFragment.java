package owuor.f8th.fragments;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.CheckBox;
import org.holoeverywhere.widget.EditText;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.ProgressBar;
import org.holoeverywhere.widget.Spinner;
import org.holoeverywhere.widget.Toast;

import owuor.f8th.R;
import owuor.f8th.ContentProvider.F8thContentProvider;
import owuor.f8th.adapters.CustomSpinnerAdapter;
import owuor.f8th.adapters.SettingsAdapter;
import owuor.f8th.database.LocalUsersTable;
import owuor.f8th.interfaces.IUserManagerRequestListener;
import owuor.f8th.types.F8th;
import owuor.f8th.types.UserProfile;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class SettingsFragment extends Fragment implements OnItemClickListener, OnCheckedChangeListener,LoaderManager.LoaderCallbacks<Cursor> {

	UserProfile userProfile;
	String userId = "";
	String email = "";
	ListView listSettings;
	CheckBox cbxPushNotification;
	ProgressBar progressPushNotification;
	SettingsAdapter settingsAdapter;

	IUserManagerRequestListener serviceListener;
	public Dialog dialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstancestate) {
		View view = inflater.inflate(R.layout.list_profile_settings, container,false);

		return view;
	}// end of method onCreateView()

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getSupportActionBar().setTitle("HOME");
		getSupportActionBar().setSubtitle("settings");
		
		userId = serviceListener.onGetUserId();
		
		dialog = new Dialog(getActivity());
		dialog.getWindow().requestFeature((int) Window.FEATURE_NO_TITLE);

		listSettings = (ListView) getView().findViewById(R.id.listUserAcc);

		if (savedInstanceState == null) {
			View header = View.inflate(getActivity(),R.layout.settings_list_header, null);
			listSettings.addHeaderView(header);


			progressPushNotification = (ProgressBar) getView().findViewById(R.id.progressPushNotify);
			cbxPushNotification = (CheckBox) getView().findViewById(R.id.cbPushNotify);
			//setAutoLogin();
			cbxPushNotification.setOnCheckedChangeListener(this);

			settingsAdapter = new SettingsAdapter(getActivity());
			String[] items = this.getResources().getStringArray(R.array.settings);
			settingsAdapter.setListData(items);

			listSettings.setAdapter(settingsAdapter);
			
		}

		listSettings.setOnItemClickListener(this);
		
	}// end of method onActivityCreated()

	/*public void setUserId(String userId) {
		this.userId = userId;
		
	}*/


	@Override
	public void onPause() {
		super.onPause();
		if (dialog.isShowing()) {
			dialog.dismiss();
		}
	}// end of method onPause()

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}// end of method onSaveInstanceState()

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			serviceListener = (IUserManagerRequestListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement IUserManagerRequestListener");
		}
		getLoaderManager().initLoader(0, null, this);
	}// end of method onAttach()

	public void unSetProgress() {
		if (listSettings != null) {
			int size = listSettings.getCount();
			for (int i = 0; i < size; i++) {
				if (listSettings.getChildAt(i) != null) {
					ProgressBar progress = (ProgressBar) listSettings.getChildAt(i).findViewById(R.id.progressSettings);
					if (progress != null && progress.isShown()) {
						progress.setVisibility(View.INVISIBLE);
					}
				}
			}
			
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		unSetProgress();
		final ProgressBar progress = (ProgressBar) view.findViewById(R.id.progressSettings);

		if (listSettings.getItemAtPosition(position).toString().equalsIgnoreCase(F8th.SIGN_OUT)) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(F8th.DIALOG_TITLE);
			builder.setMessage("are you signing out?");
			builder.setPositiveButton("yes",new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							if (!progress.isShown()) {
								progress.setVisibility(View.VISIBLE);
							}
							serviceListener.onLogoutRequested();
							dialog.dismiss();
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

		} else if (listSettings.getItemAtPosition(position).toString().equalsIgnoreCase(F8th.DEREGISTER)) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(F8th.DIALOG_TITLE);
			builder.setMessage("All your details and stories will be deleted permanently.\nDo you really want to de-register?");
			builder.setPositiveButton("yes",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							if (!progress.isShown()) {
								progress.setVisibility(View.VISIBLE);
							}
							serviceListener.onDeregisterRequested();
							dialog.dismiss();
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

		} else if (listSettings.getItemAtPosition(position).toString().equalsIgnoreCase(F8th.UPDATE_PROFILE)) {

			// dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
			View v = View.inflate(getActivity(), R.layout.settings_profile, null);
			dialog.setContentView(v);
			final EditText why = (EditText) v.findViewById(R.id.editWhy);
			final EditText desire = (EditText) v.findViewById(R.id.editDesire);
			final EditText about = (EditText) v.findViewById(R.id.editAbout);
			final Button btnSave = (Button) v.findViewById(R.id.btnSave);
			final ProgressBar progSave = (ProgressBar) v.findViewById(R.id.progressSave);

			if (userProfile != null) {
				why.setText(userProfile.getWhy());
				desire.setText(userProfile.getDesire());
				about.setText(userProfile.getFavVerse());
			}

			btnSave.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (why.getText().toString().equalsIgnoreCase("")
							|| desire.getText().toString().equalsIgnoreCase("")
							|| about.getText().toString().equalsIgnoreCase("")) {
						Toast.makeText(getActivity(), "some fields empty",Toast.LENGTH_SHORT).show();
					} else if (why.getText().toString().contains("|")
							|| desire.getText().toString().contains("|")
							|| about.getText().toString().contains("|")) {
						Toast.makeText(getActivity(),"remove character '|' from any text",Toast.LENGTH_SHORT).show();
					} else {
						if (!progSave.isShown()) {
							progSave.setVisibility(View.VISIBLE);
							btnSave.setVisibility(View.INVISIBLE);
						}
						serviceListener.onUpdateProfileRequested(why.getText().toString(), desire.getText().toString(), about.getText().toString());
						// dialog.dismiss();
					}
				}
			});
			WindowManager.LayoutParams wndw = dialog.getWindow().getAttributes();
			wndw.width = ViewGroup.LayoutParams.MATCH_PARENT;
			wndw.height = ViewGroup.LayoutParams.WRAP_CONTENT;
			dialog.show();

		/*} else if (listSettings.getItemAtPosition(position).toString().equalsIgnoreCase("view story")) {
			if (!progress.isShown()) {
				progress.setVisibility(View.VISIBLE);
			}
			serviceListener.onViewMyInfoRequested();*/

		} else if (listSettings.getItemAtPosition(position).toString().equalsIgnoreCase(F8th.EDIT_USER)) {

			View v = View.inflate(getActivity(), R.layout.settings_edit_user, null);
			dialog.setContentView(v);
			final EditText editEmail = (EditText) v.findViewById(R.id.editEmail);
			final EditText fname = (EditText) v.findViewById(R.id.editFname);
			final EditText lname = (EditText) v.findViewById(R.id.editLname);
			final Spinner gender = (Spinner) v.findViewById(R.id.spinnerSex);
			final EditText country = (EditText) v.findViewById(R.id.editNation);

			CustomSpinnerAdapter genderAdapter = new CustomSpinnerAdapter(getActivity(), "Gender");
			genderAdapter.setListData(this.getResources().getStringArray(R.array.gender));
			gender.setAdapter(genderAdapter);

			if (userProfile != null) {
				editEmail.setText(email);
				fname.setText(userProfile.getFname());
				lname.setText(userProfile.getLname());
				country.setText(userProfile.getCountry());
				if (userProfile.getGender().equalsIgnoreCase("Female")) {
					gender.setSelection(1);
				} else if (userProfile.getGender().equalsIgnoreCase("Male")) {
					gender.setSelection(2);
				}
			}

			final Button btnUpdate = (Button) v.findViewById(R.id.btnUpdate);
			final ProgressBar progUpdate = (ProgressBar) v.findViewById(R.id.progressUpdate);

			btnUpdate.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String strEmail = editEmail.getText().toString();
					String strFname = fname.getText().toString();
					String strLname = lname.getText().toString();
					String strCountry = country.getText().toString();
					String strGender = gender.getSelectedItem().toString();

					if (strEmail.equalsIgnoreCase("")
							|| strFname.equalsIgnoreCase("")
							|| strLname.equalsIgnoreCase("")
							|| strCountry.equalsIgnoreCase("")
							|| gender.getSelectedItemPosition() == 0) {
						Toast.makeText(getActivity(), "some fields empty",Toast.LENGTH_SHORT).show();

					} else if (strEmail.contains("|") || strFname.contains("|")
							|| strLname.contains("|")
							|| strCountry.toString().contains("|")) {
						Toast.makeText(getActivity(),"remove character '|' from any text",Toast.LENGTH_SHORT).show();

					} else if (strEmail.contains("'") || strEmail.contains(";")
							|| strEmail.contains("\"")) {
						Toast.makeText(getActivity(), "invalid email address",Toast.LENGTH_SHORT).show();

					} else {

						String address = editEmail.getText().toString();
						if (address.contains("@") && address.contains(".")) {
							if (!progUpdate.isShown()) {
								progUpdate.setVisibility(View.VISIBLE);
								btnUpdate.setVisibility(View.INVISIBLE);
							}
							serviceListener.onUpdateDetailsRequested(strEmail,strFname, strLname, strGender, strCountry);
							// dialog.dismiss();
						} else {
							Toast.makeText(getActivity(),"invalid email address", Toast.LENGTH_SHORT).show();
						}

					}
				}
			});
			WindowManager.LayoutParams wndw = dialog.getWindow().getAttributes();
			wndw.width = ViewGroup.LayoutParams.MATCH_PARENT;
			wndw.height = ViewGroup.LayoutParams.WRAP_CONTENT;
			dialog.show();

		} else if (listSettings.getItemAtPosition(position).toString()
				.equalsIgnoreCase(F8th.CHANGE_PASSWORD)) {
			View v = View.inflate(getActivity(), R.layout.settings_reset_password, null);
			dialog.setContentView(v);
			final EditText cPass = (EditText) v.findViewById(R.id.editCurrent);
			final EditText nPass = (EditText) v.findViewById(R.id.editNew);
			final EditText n1Pass = (EditText) v.findViewById(R.id.editConfirm);
			final Button btnReset = (Button) v.findViewById(R.id.btnReset);
			final ProgressBar progReset = (ProgressBar) v.findViewById(R.id.progressReset);

			btnReset.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (cPass.getText().toString().equalsIgnoreCase("")
							|| nPass.getText().toString().equalsIgnoreCase("")) {
						Toast.makeText(getActivity(), "some fields empty",Toast.LENGTH_SHORT).show();
					} else if (!nPass.getText().toString()
							.equals(n1Pass.getText().toString())) {
						Toast.makeText(getActivity(),"new passwords do not match",Toast.LENGTH_SHORT).show();
						n1Pass.setText("");
					} else {
						if (!progReset.isShown()) {
							progReset.setVisibility(View.VISIBLE);
							btnReset.setVisibility(View.INVISIBLE);
						}
						serviceListener.onResetPasswordRequested(cPass.getText().toString(), nPass.getText().toString());
						// dialog.dismiss();

					}
				}
			});
			WindowManager.LayoutParams wndw = dialog.getWindow().getAttributes();
			wndw.width = ViewGroup.LayoutParams.MATCH_PARENT;
			wndw.height = ViewGroup.LayoutParams.WRAP_CONTENT;
			dialog.show();

		} else if (listSettings.getItemAtPosition(position).toString().equalsIgnoreCase(F8th.REPORT_ISSUE)) {

			View v = View.inflate(getActivity(), R.layout.settings_report, null);
			dialog.setContentView(v);
			final EditText suggestion = (EditText) v.findViewById(R.id.editSuggestion);
			Button btnSend = (Button) v.findViewById(R.id.btnSend);
			btnSend.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					// serviceListener.onSendsuggestionRequested(suggestion.getText().toString());
					if (!suggestion.getText().toString().equalsIgnoreCase("")) {
						if (!progress.isShown()) {
							progress.setVisibility(View.VISIBLE);
						}

						Intent mail = new Intent(Intent.ACTION_SEND);
						mail.putExtra(Intent.EXTRA_EMAIL, F8th.to);
						mail.putExtra(Intent.EXTRA_SUBJECT, F8th.subject);
						mail.putExtra(Intent.EXTRA_TEXT, suggestion.getText().toString());
						mail.setType("message/rfc822");
						getActivity().startActivity(Intent.createChooser(mail,"choose email service"));

						dialog.dismiss();
					} else {
						Toast.makeText(getActivity(), "type your report/suggestion",Toast.LENGTH_SHORT).show();
					}
				}
			});
			WindowManager.LayoutParams wndw = dialog.getWindow().getAttributes();
			wndw.width = ViewGroup.LayoutParams.MATCH_PARENT;
			wndw.height = ViewGroup.LayoutParams.WRAP_CONTENT;
			dialog.show();

		} else if (listSettings.getItemAtPosition(position).toString()
				.equalsIgnoreCase(F8th.ABOUT_F8TH)) {
			View v = View.inflate(getActivity(), R.layout.settings_about, null);
			dialog.setContentView(v);
			Button btnOk = (Button) v.findViewById(R.id.btnAboutOk);
			btnOk.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					dialog.dismiss();
				}
			});
			WindowManager.LayoutParams wndw = dialog.getWindow()
					.getAttributes();
			wndw.width = ViewGroup.LayoutParams.MATCH_PARENT;
			wndw.height = ViewGroup.LayoutParams.WRAP_CONTENT;
			dialog.show();
		}// end of if-else
	}// end of onItemClick

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub

		/*if (isChecked && autoLogin.equalsIgnoreCase(F8th.AUTO_LOGIN_UNSET)) {
			if (!progressAutologin.isShown()) {
				progressAutologin.setVisibility(View.VISIBLE);
			}
			serviceListener.onUpdateAutoLogin(true);
		} else if (!isChecked && autoLogin.equalsIgnoreCase(F8th.AUTO_LOGIN_SET)) {
			if (!progressAutologin.isShown()) {
				progressAutologin.setVisibility(View.VISIBLE);
			}
			serviceListener.onUpdateAutoLogin(false);
		}*/

	}// end of method

	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// TODO Auto-generated method stub
		String[] projection = { LocalUsersTable.COLUMN_EMAIL, LocalUsersTable.COLUMN_USER_ID,
				LocalUsersTable.COLUMN_AUTOLOGIN, LocalUsersTable.COLUMN_UID,LocalUsersTable.COLUMN_FNAME,
				LocalUsersTable.COLUMN_LNAME, LocalUsersTable.COLUMN_GENDER,
				LocalUsersTable.COLUMN_COUNTRY, LocalUsersTable.COLUMN_FAV_VERSE,
				LocalUsersTable.COLUMN_WHY, LocalUsersTable.COLUMN_DESIRE,LocalUsersTable.COLUMN_VIEWS };

		String userid = serviceListener.onGetUserId();
		
		Uri uri = Uri.parse(F8thContentProvider.CONTENT_URI + "/" + LocalUsersTable.TABLE_LOCAL_USERS);
		String selection = LocalUsersTable.COLUMN_USER_ID + "='" + userid + "'";
		CursorLoader cursorLoader = new CursorLoader(getActivity(), uri,projection, selection, null, null);
		// Log.e("Local database","cursor loader returned");
		return cursorLoader;
	}
	

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		// TODO Auto-generated method stub
		if (cursor.moveToFirst() && cursor != null) {


			//autoLogin = cursor.getString(cursor.getColumnIndexOrThrow(LocalUsersTable.COLUMN_AUTOLOGIN));
			email = cursor.getString(cursor.getColumnIndexOrThrow(LocalUsersTable.COLUMN_EMAIL));
			String userId = cursor.getString(cursor.getColumnIndexOrThrow(LocalUsersTable.COLUMN_USER_ID));
			String fname = cursor.getString(cursor.getColumnIndexOrThrow(LocalUsersTable.COLUMN_FNAME));
			String lname = cursor.getString(cursor.getColumnIndexOrThrow(LocalUsersTable.COLUMN_LNAME));
			String gender = cursor.getString(cursor.getColumnIndexOrThrow(LocalUsersTable.COLUMN_GENDER));
			String country = cursor.getString(cursor.getColumnIndexOrThrow(LocalUsersTable.COLUMN_COUNTRY));
			String about = cursor.getString(cursor.getColumnIndexOrThrow(LocalUsersTable.COLUMN_FAV_VERSE));
			String why = cursor.getString(cursor.getColumnIndexOrThrow(LocalUsersTable.COLUMN_WHY));
			String desire = cursor.getString(cursor.getColumnIndexOrThrow(LocalUsersTable.COLUMN_DESIRE));
			String views = cursor.getString(cursor.getColumnIndexOrThrow(LocalUsersTable.COLUMN_VIEWS));
			String location = "local";
			String uid = "";

			userProfile = new UserProfile(uid, userId, fname, lname, gender, country,about, why, desire, views, location);
			//Log.e("SettingsFragment",autoLogin);
		} 
	}

	
	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// TODO Auto-generated method stub
		//loader.reset();
	}

}// END OF CLASS SettingsFragment