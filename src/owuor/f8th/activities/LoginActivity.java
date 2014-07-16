/**
* All Honor and Glory belongs to God Almighty
* Phil 2:13
*@author Dickson Owuor <dickytea@gmail.com>
*@version 1.1
*@since 2014-1-03
*@see
*
*<p>This activity allows user to sign up, sign in or send a recovery password to the user's email
*incase they forget their current password.</p>
*/

package owuor.f8th.activities;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.widget.Toast;

import owuor.f8th.R;
import owuor.f8th.dialog.F8thDialog;
import owuor.f8th.fragments.ChooseUserFragment;
import owuor.f8th.fragments.ForgotPasswordFragment;
import owuor.f8th.fragments.SaveProfileFragment;
import owuor.f8th.fragments.SignInFragment;
import owuor.f8th.fragments.SignUpFragment;
import owuor.f8th.interfaces.IRegisterRequestListener;
import owuor.f8th.interfaces.IUserManager;
import owuor.f8th.service.UserManagerService;
import owuor.f8th.types.F8th;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class LoginActivity extends Activity implements IRegisterRequestListener {

	private IUserManager userManagerService;

	boolean mBound = false;
	boolean internet = true;

	SignInFragment signInFragment;
	ChooseUserFragment chooseUserFragment;

	Thread thread;
	F8thDialog dialog;

	// use threads and handler to access user manager functions
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_login);
		getSupportActionBar().hide();
		dialog = new F8thDialog(this);

		if (savedInstanceState == null) {
			//implemented for the very first time only
			this.startService(new Intent(LoginActivity.this,UserManagerService.class));
			Log.e("Log in activity", "1st service started");
			
			chooseUserFragment = new ChooseUserFragment();
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			ft.add(R.id.main, chooseUserFragment, "login view");
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			ft.commit();
		}// end of if()

	}// end of method onCreate()

	private ServiceConnection serviceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			userManagerService = ((UserManagerService.LocalBinder) service).getService();
			mBound = true;
			Log.e("log in connector", "service connected at log in");

			LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(F8th.CONNECTED_TO_SERVICE));

		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			mBound = false;
			Log.e("service", "service disconnected at log in");
		}

	};// end of class ServiceConnection

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if (mBound) {
			unbindService(serviceConnection);
			mBound = false;
			this.stopService(new Intent(LoginActivity.this,UserManagerService.class));
			Log.e("login activity", "back pressed: login finished");
			this.finish();
		}
	}// end of onBackPressed()

	@Override
	public void onStart() {
		super.onStart();
		// Bind to LocalService
		Intent intent = new Intent(this, UserManagerService.class);
		if (!mBound) {
			bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
			mBound = true;
			Log.e("Log in activity", "start binded to service");
		}

	}// end of onStart()

	@Override
	public void onPause() {
		super.onPause();
		if (dialog != null) {
			dialog.dismissDialog();
		}
		// Unbind from the service
		if (mBound) {
			unbindService(serviceConnection);
			mBound = false;
			Log.e("Log in activity", "pause unbinded from service");
		}

	}// end of onPause()

	@Override
	public void onStop() {
		super.onStop();
		Log.e("Log in activity", "activity stopped");
	}// end of onStop()

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (dialog != null) {
			dialog.dismissDialog();
		}

		if (mBound) {
			unbindService(serviceConnection);
			mBound = false;
			Log.e("Log in activity", "destroy unbinded from service");
		}
		Log.e("Log in activity", "activity finished");
	}// end method onDestroy()

	@Override
	public void onResume() {
		super.onResume();
		if (!mBound) {
			bindService(new Intent(this, UserManagerService.class),
					serviceConnection, Context.BIND_AUTO_CREATE);
			mBound = true;
			Log.e("Log in activity", "resume binded to service");
		}
	}// end of onResume

	@Override
	public void onSignInRequested(final String email, final String password) {
		// TODO Auto-generated method stub
		if (mBound && !userManagerService.isNetworkConnected()) {
			internet = false;
			dialog.alertDialog("No internet conection!\nplease check your settings.");
			SignInFragment.unSetProgress();
			ChooseUserFragment.unSetProgress();
			Log.e("Log in Activity", "no internet");
		} else if (mBound && userManagerService.isNetworkConnected()) {

			thread = new Thread() {
				private Handler handler = new Handler();

				@Override
				public void run() {
					boolean result = userManagerService.authenticateUser(email,password);
					if (!result) {
						handler.post(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								if (dialog.alertDialog(userManagerService.getErrorMsg())) {
									userManagerService.setErrorMsg();
									SignInFragment.unSetProgress();
									ChooseUserFragment.unSetProgress();
								}
							}

						});
					} else if (result) {
						
						boolean userLoaded = userManagerService.loadUser();
						
						if(userLoaded){
							
							handler.post(new Runnable() {
								@Override
								public void run() {
									// TODO Auto-generated method stub
									
									startActivity(new Intent(LoginActivity.this,F8thActivity.class));
									dialog.dismissDialog();
									LoginActivity.this.finish();
								}

							});
							
						}else{
							dialog.alertDialog("Unable to retrieve your profile");
						}
						
					}
				}// end of thread run()
			};
			thread.start();

		}
	}// end of method onLoginRequested()

	@Override
	public void onSignUpRequested(final String email, final String fname,
			final String lname, final String gender, final String country,
			final String password) {
		// TODO Auto-generated method stub
		if (mBound && !userManagerService.isNetworkConnected()) {
			internet = false;
			dialog.alertDialog("No internet conection!\nplease check your settings.");
			SignUpFragment.unSetProgress();
			Log.e("Log in Activity", "no internet");
		} else if (mBound && userManagerService.isNetworkConnected()) {

			thread = new Thread() {
				private Handler handler = new Handler();

				@Override
				public void run() {
					boolean result = userManagerService.signUpUser(email,fname, lname, gender, country, password);
					if (!result) {
						handler.post(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								if (dialog.alertDialog(userManagerService.getErrorMsg())) {
									userManagerService.setErrorMsg();
									SignUpFragment.unSetProgress();
								}
							}

						});
					} else if (result) {
						handler.post(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Toast.makeText(getApplicationContext(),userManagerService.getSuccessMsg(),Toast.LENGTH_SHORT).show();
								dialog.dismissDialog();

								FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
								ft.replace(R.id.main, new SaveProfileFragment(),"save profile view");
								ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
								ft.commit();
							}

						});
					}
				}// end of thread run()
			};
			thread.start();

		}
	}// end of method onSignupRequested()
	
	
	@Override
	public void onSaveProfileRequested(final String about, final String why,final String desire) {
		// TODO Auto-generated method stub
		if (mBound && !userManagerService.isNetworkConnected()) {
			internet = false;
			dialog.alertDialog("No internet conection!\nplease check your settings.");
			SaveProfileFragment.unSetProgress();
			Log.e("Log in Activity", "no internet");
		} else if (mBound && userManagerService.isNetworkConnected()) {

			thread = new Thread() {
				private Handler handler = new Handler();

				@Override
				public void run() {
					boolean result = userManagerService.updateProfile(about,why,desire);
					if (!result) {
						handler.post(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								if (dialog.alertDialog(userManagerService.getErrorMsg())) {
									userManagerService.setErrorMsg();
									SaveProfileFragment.unSetProgress();
								}
							}

						});
					} else if (result) {
						handler.post(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Toast.makeText(getApplicationContext(),userManagerService.getSuccessMsg(),Toast.LENGTH_SHORT).show();
								dialog.dismissDialog();
								signInFragment = new SignInFragment();
								FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
								ft.replace(R.id.main,signInFragment,"login view");
								ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
								ft.commit();
							}

						});
					}
				}// end of thread run()
			};
			thread.start();

		}
	}// end of method onSaveProfileRequested()
	

	@Override
	public void onForgotpasswordRequested(final String email) {
		// TODO Auto-generated method stub
		if (mBound && !userManagerService.isNetworkConnected()) {
			internet = false;
			dialog.alertDialog("No internet conection!\nplease check your settings.");
			ForgotPasswordFragment.unSetProgress();
			Log.e("Log in Activity", "no internet");
		} else if (mBound && userManagerService.isNetworkConnected()) {
			thread = new Thread() {
				private Handler handler = new Handler();

				@Override
				public void run() {
					boolean result = userManagerService.forgotPassword(email);
					if (!result) {
						handler.post(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								if (dialog.alertDialog(userManagerService.getErrorMsg())) {
									userManagerService.setErrorMsg();
									ForgotPasswordFragment.unSetProgress();
								}
							}

						});
					} else if (result) {
						handler.post(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Toast.makeText(getApplicationContext(),userManagerService.getSuccessMsg(),Toast.LENGTH_SHORT).show();
								dialog.dismissDialog();
								signInFragment = new SignInFragment();
								FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
								ft.replace(R.id.main, signInFragment,"login view");
								ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
								ft.commit();
							}

						});
					}
				}// end of thread run()
			};
			thread.start();
		}
	}// end of method onForgotpasswordRequested()

}// END OF CLASS LoginActivity()
