package owuor.f8th.activities;

import java.util.List;

import org.holoeverywhere.app.Fragment;

import owuor.f8th.R;
import owuor.f8th.dialog.F8thDialog;
import owuor.f8th.fragments.AddGroupFragment;
import owuor.f8th.fragments.NavMenuFragment;
import owuor.f8th.fragments.PeopleGroupsFragment;
import owuor.f8th.fragments.PeopleGroupsJoinedFragment;
import owuor.f8th.fragments.PeopleProfilesFragment;
import owuor.f8th.fragments.ProfileNotifyFragment;
import owuor.f8th.fragments.SettingsFragment;
import owuor.f8th.fragments.StoriesFavFragment;
import owuor.f8th.fragments.StoriesFragment;
import owuor.f8th.fragments.TellStoryFragment;
import owuor.f8th.fragments.ViewGroupFragment;
import owuor.f8th.fragments.ViewNotificationFragment;
import owuor.f8th.fragments.ViewProfileFragment;
import owuor.f8th.fragments.ViewStoryFragment;
import owuor.f8th.interfaces.IUserManager;
import owuor.f8th.interfaces.IUserManagerRequestListener;
import owuor.f8th.service.UserManagerService;
import owuor.f8th.types.F8th;
import owuor.f8th.types.Group;
import owuor.f8th.types.Notification;
import owuor.f8th.types.Story;
import owuor.f8th.types.User;
import owuor.f8th.types.UserProfile;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.CanvasTransformer;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

public class F8thActivity extends SlidingFragmentActivity implements IUserManagerRequestListener, OnMenuItemClickListener{

	private IUserManager userManagerService = null;
	private boolean mBound = false;//service connected
	private int popBack = 0;
	
	private CanvasTransformer mTransformer;
	
	Thread thread;
	F8thDialog dialog;
	
	MenuItem status,settings,story,group;
	
	SettingsFragment settingsFragment;
	NavMenuFragment navMenu;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_f8th);
		
		settingsFragment = new SettingsFragment();
		dialog = new F8thDialog(this);

		// check if the content frame contains the menu frame
		if (findViewById(R.id.menu_frame) == null) {
			setBehindContentView(R.layout.nav_frame);
			getSlidingMenu().setSlidingEnabled(true);
			getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
			// show home as up so we can toggle
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		} else {
			// add a dummy view
			View v = new View(this);
			setBehindContentView(v);
			getSlidingMenu().setSlidingEnabled(false);
			getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		}
		
		
		// set the Above View Fragment
		if(savedInstanceState == null){
			getSupportFragmentManager()
			.beginTransaction()
			.replace(R.id.content_frame, new StoriesFragment())
			.commit();
		}

		// set the Behind View Fragment
		navMenu = new NavMenuFragment();
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.menu_frame, navMenu)
		.commit();

		//Customise the SlidingMenu
		//zoom animation
		mTransformer = new CanvasTransformer() {
			@Override
			public void transformCanvas(Canvas canvas, float percentOpen) {
				float scale = (float) (percentOpen*0.25 + 0.75);
				canvas.scale(scale, scale, canvas.getWidth()/2, canvas.getHeight()/2);
			}
		};
		
		SlidingMenu sm = getSlidingMenu();
		setSlidingActionBarEnabled(true);
		sm.setBehindScrollScale(0.0f);
		sm.setBehindCanvasTransformer(mTransformer);
		
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setBehindScrollScale(0.25f);
		sm.setFadeDegree(0.25f);
		
	}//end of onCreate

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			toggle();
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	@Override
	public void onBackPressed(){
		
		if(getSupportFragmentManager().getBackStackEntryCount() > 0){
			
			getSupportFragmentManager().popBackStack();
			
			if(popBack > 0){
				popBack--;
			}else{
				popBack = 0;
			}
		/*	
		}else if(getSupportFragmentManager().getBackStackEntryCount() > 0){
			
			getSupportFragmentManager().popBackStack();
		*/	
		}else{	
			if (mBound){
				unbindService(serviceConnection);
				mBound = false;
				this.stopService(new Intent(F8thActivity.this,UserManagerService.class));
				// Unregister since the activity is not visible
				LocalBroadcastManager.getInstance(this).unregisterReceiver(f8thReceiver);
				Log.e("main activity", "back pressed: main finished");
	
				this.finish();
			}
		}
		//super.onBackPressed();
	}//end of onBackPressed()
	
	@Override
	public void onStart(){
		super.onStart();
		// Bind to LocalService
        Intent intent = new Intent(this, UserManagerService.class);
        if(!mBound){
        	bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        	mBound = true;
        	Log.e("Main activity","start binded to service");
        	
        	// Register mMessageReceiver to receive messages.
        	LocalBroadcastManager.getInstance(this).registerReceiver(f8thReceiver,new IntentFilter(F8th.INTERNET_NOT_AVAILABLE));
        	LocalBroadcastManager.getInstance(this).registerReceiver(f8thReceiver,new IntentFilter(F8th.INTERNET_AVAILABLE));
        	}
     }//end of onStart()
	
	@Override
	public void onPause(){
		super.onPause();
		//Unregister since the activity is not visible
	    LocalBroadcastManager.getInstance(this).unregisterReceiver(f8thReceiver);
	    getSupportLoaderManager().destroyLoader(0);
	    
		// Unbind from the service
        if (mBound) {
            unbindService(serviceConnection);
            mBound = false;
            Log.e("Main activity","pause unbinded from service");
        }
     
	}//end of onPause()
	
	@Override
	public void onResume() {
	  super.onResume();
	  // Register mMessageReceiver to receive messages.
	  LocalBroadcastManager.getInstance(this).registerReceiver(f8thReceiver,new IntentFilter(F8th.INTERNET_NOT_AVAILABLE));
	  LocalBroadcastManager.getInstance(this).registerReceiver(f8thReceiver,new IntentFilter(F8th.INTERNET_AVAILABLE));
	  if(!mBound){
		  bindService(new Intent(this, UserManagerService.class), serviceConnection, Context.BIND_AUTO_CREATE);
		  mBound = true;
		  Log.e("Main activity","resume binded to service");
	  }
	}//end of onResume
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		
		if(mBound){
			unbindService(serviceConnection);
			mBound = false;
			Log.e("Main activity","destroy unbinded from service");
		}
		Log.e("main activity","main finished");
	}//end of onDestroy()
	
	private ServiceConnection serviceConnection = new ServiceConnection(){

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			userManagerService = ((UserManagerService.LocalBinder)service).getService();
			mBound = true;
			//connectedToService = true;
			Log.e("service","service connected at main");
			LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(F8th.CONNECTED_TO_SERVICE));
			
			//do something
			
			userManagerService.setErrorMsg();
			userManagerService.setSuccessMsg();
			
			userManagerService.startTimerTask();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			mBound = false;
			Log.e("service","service disconnected at log in");
		}
		
	};//end of class ServiceConnection
	
	private BroadcastReceiver f8thReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			
			if(intent.getAction().equalsIgnoreCase(F8th.INTERNET_NOT_AVAILABLE) && status!=null){
				status.setIcon(R.drawable.presence_offline);
				
			}else if(intent.getAction().equalsIgnoreCase(F8th.INTERNET_AVAILABLE) && status!=null){
				status.setIcon(R.drawable.presence_online);
				
			}
		}
		
		
	};//end of class f8thReceiver()
	
	
//---------------------------------------------------------------------------------------------------------------------------
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.f8th_menu, menu);
		
		status = menu.findItem(R.id.status);
		settings = menu.findItem(R.id.settings);
		story = menu.findItem(R.id.newStory);
		group = menu.findItem(R.id.newGroup);
		
		story.setOnMenuItemClickListener(this);
		group.setOnMenuItemClickListener(this);
		settings.setOnMenuItemClickListener(this);
		
		status.setIcon(R.drawable.presence_offline);
		
		// Initilization
		//getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
				
		return true;
	}//end of method onCreateoptionsMenu()
	
	@Override
	public boolean onMenuItemClick(MenuItem item) {
		// TODO Auto-generated method stub
		
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		if(popBack < 2){
			ft.addToBackStack(null);
			popBack++;
		}else{
			ft.addToBackStack(null);
		}
		
		if(item == settings){
			
			if(getSupportFragmentManager().findFragmentById(R.id.content_frame) != settingsFragment){
				
				if(mBound && userManagerService != null){
					//settingsFragment.setUserId(userManagerService.getUserId());
					
					ft.replace(R.id.content_frame, settingsFragment, "settings notification");
					ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
					ft.commit();
				}else{
					Toast.makeText(this, "Service Not Running", Toast.LENGTH_SHORT).show();
				}
				
				
			}
			return true;
		}else if(item == group){
			
			ft.replace(R.id.content_frame, new AddGroupFragment(), "settings notification");
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			ft.commit();
			
			return true;
		}else if(item == story){
			
			ft.replace(R.id.content_frame, new TellStoryFragment(), "settings notification");
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			ft.commit();
			
			return true;
		}else{
			return false;
		}
	}
	
//======================================================================================================================
	
	public void switchContent(final Fragment fragment) {
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.content_frame, fragment)
		.commit();
		
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			public void run() {
				getSlidingMenu().showContent();
			}
		}, 50);
	}	
//==============================================================================================================================
	
	@Override
	public void onProfileListRequested(final String type, final String itemId) {
		// TODO Auto-generated method stub
		if (mBound && userManagerService!=null && userManagerService!=null && !userManagerService.isNetworkConnected()) {

			String error = "No internet conection!\nplease check your settings.";
			Toast.makeText(getApplicationContext(),error,Toast.LENGTH_SHORT).show();
			Log.e("F8thActivity", "no internet");
		} else if (mBound && userManagerService!=null && userManagerService!=null && userManagerService.isNetworkConnected()) {

			thread = new Thread() {
				private Handler handler = new Handler();

				@Override
				public void run() {
					final List<User> profileList = userManagerService.profileList(type, itemId);
					if (profileList == null) {
						handler.post(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								PeopleProfilesFragment.emptyView.setText(userManagerService.getErrorMsg());
								Toast.makeText(getApplicationContext(),userManagerService.getErrorMsg(),Toast.LENGTH_SHORT).show();
								
								userManagerService.setErrorMsg();
								//do not change local database and fragment adapter
								if(PeopleProfilesFragment.listProfile.isRefreshing()){
									PeopleProfilesFragment.listProfile.onRefreshComplete();
								}
							}

						});
					} else if (profileList != null) {
						handler.post(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Toast.makeText(getApplicationContext(),userManagerService.getSuccessMsg(),Toast.LENGTH_SHORT).show();
								//dialog.dismissDialog();
								
							}

						});
					}
				}// end of thread run()
			};
			thread.start();

		}
	}


	@Override
	public void onStoryListRequested(final String type, final String itemId) {
		// TODO Auto-generated method stub
		if (mBound && userManagerService!=null && !userManagerService.isNetworkConnected()) {
			
			String error = "No internet conection!\nplease check your settings.";
			Toast.makeText(getApplicationContext(),error,Toast.LENGTH_SHORT).show();
			//dialog.alertDialog("No internet conection!\nplease check your settings.");
			Log.e("F8thActivity", "no internet");
		} else if (mBound && userManagerService!=null && userManagerService.isNetworkConnected()) {

			thread = new Thread() {
				private Handler handler = new Handler();

				@Override
				public void run() {
					final List<Story> storyList = userManagerService.storyList(type, itemId);
					if (storyList == null) {
						handler.post(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								StoriesFragment.emptyView.setText(userManagerService.getErrorMsg());
								Toast.makeText(getApplicationContext(),userManagerService.getErrorMsg(),Toast.LENGTH_SHORT).show();
								
								userManagerService.setErrorMsg();
								
								//do not change local database and fragment adapter
								if(StoriesFragment.listStories.isRefreshing()){
									StoriesFragment.listStories.onRefreshComplete();
								}
							}

						});
					} else if (storyList != null) {
						handler.post(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Toast.makeText(getApplicationContext(),userManagerService.getSuccessMsg(),Toast.LENGTH_SHORT).show();
								//dialog.dismissDialog();
								
								
							}

						});
					}
				}// end of thread run()
			};
			thread.start();

		}
	}


	@Override
	public void onGroupListRequested(final String type, final String itemId) {
		// TODO Auto-generated method stub
		if (mBound && userManagerService!=null && !userManagerService.isNetworkConnected()) {
			
			String error = "No internet conection!\nplease check your settings.";
			Toast.makeText(getApplicationContext(),error,Toast.LENGTH_SHORT).show();
			//dialog.alertDialog("No internet conection!\nplease check your settings.");
			Log.e("F8thActivity", "no internet");
		} else if (mBound && userManagerService!=null && userManagerService.isNetworkConnected()) {

			thread = new Thread() {
				private Handler handler = new Handler();

				@Override
				public void run() {
					final List<Group> groupList = userManagerService.groupList(type, itemId);
					if (groupList == null) {
						handler.post(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								PeopleGroupsFragment.emptyView.setText(userManagerService.getErrorMsg());
								Toast.makeText(getApplicationContext(),userManagerService.getErrorMsg(),Toast.LENGTH_SHORT).show();
								
								
								userManagerService.setErrorMsg();
								//do not change local database and fragment adapter
								if(PeopleGroupsFragment.listGroup.isRefreshing()){
									PeopleGroupsFragment.listGroup.onRefreshComplete();
								}
							}

						});
					} else if (groupList != null) {
						handler.post(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Toast.makeText(getApplicationContext(),userManagerService.getSuccessMsg(),Toast.LENGTH_SHORT).show();
								//dialog.dismissDialog();
								
							}

						});
					}
				}// end of thread run()
			};
			thread.start();

		}
	}


	@Override
	public void onFavStoryListRequested(final String favId) {
		// TODO Auto-generated method stub
		if (mBound && userManagerService!=null && !userManagerService.isNetworkConnected()) {
			
			String error = "No internet conection!\nplease check your settings.";
			Toast.makeText(getApplicationContext(),error,Toast.LENGTH_SHORT).show();
			//dialog.alertDialog("No internet conection!\nplease check your settings.");
			Log.e("F8thActivity", "no internet");
		} else if (mBound && userManagerService!=null && userManagerService.isNetworkConnected()) {

			thread = new Thread() {
				private Handler handler = new Handler();

				@Override
				public void run() {
					final List<Story> storyList = userManagerService.favStoryList(favId);
					if (storyList == null) {
						handler.post(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								StoriesFavFragment.emptyView.setText(userManagerService.getErrorMsg());
								Toast.makeText(getApplicationContext(),userManagerService.getErrorMsg(),Toast.LENGTH_SHORT).show();
								
								
								userManagerService.setErrorMsg();
								//do not change local database and fragment adapter
								if(StoriesFavFragment.listStories.isRefreshing()){
									StoriesFavFragment.listStories.onRefreshComplete();
								}
							
							}

						});
					} else if (storyList != null) {
						handler.post(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Toast.makeText(getApplicationContext(),userManagerService.getSuccessMsg(),Toast.LENGTH_SHORT).show();
								//dialog.dismissDialog();
								
							}

						});
					}
				}// end of thread run()
			};
			thread.start();

		}
	}

	@Override
	public void onJoinedGroupListRequested(final String mId) {
		// TODO Auto-generated method stub
		if (mBound && userManagerService!=null && !userManagerService.isNetworkConnected()) {
			
			String error = "No internet conection!\nplease check your settings.";
			Toast.makeText(getApplicationContext(),error,Toast.LENGTH_SHORT).show();
			//dialog.alertDialog("No internet conection!\nplease check your settings.");
			Log.e("F8thActivity", "no internet");
		} else if (mBound && userManagerService!=null && userManagerService.isNetworkConnected()) {

			thread = new Thread() {
				private Handler handler = new Handler();

				@Override
				public void run() {
					final List<Group> groupList = userManagerService.joinedGroupList(mId);
					if (groupList == null) {
						handler.post(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								PeopleGroupsJoinedFragment.emptyView.setText(userManagerService.getErrorMsg());
								Toast.makeText(getApplicationContext(),userManagerService.getErrorMsg(),Toast.LENGTH_SHORT).show();
								
							
								userManagerService.setErrorMsg();
								//do not change local database and fragment adapter
								if(PeopleGroupsJoinedFragment.listGroup.isRefreshing()){
									PeopleGroupsJoinedFragment.listGroup.onRefreshComplete();
								}
								
							}

						});
					} else if (groupList != null) {
						handler.post(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Toast.makeText(getApplicationContext(),userManagerService.getSuccessMsg(),Toast.LENGTH_SHORT).show();
								//dialog.dismissDialog();
								
							}

						});
					}
				}// end of thread run()
			};
			thread.start();

		}
	}

	
	@Override
	public void onNotificationsRequested(final String nId) {
		// TODO Auto-generated method stub
		if (mBound && userManagerService!=null && !userManagerService.isNetworkConnected()) {
			
			String error = "No internet conection!\nplease check your settings.";
			Toast.makeText(getApplicationContext(),error,Toast.LENGTH_SHORT).show();
			//dialog.alertDialog("No internet conection!\nplease check your settings.");
			Log.e("F8thActivity", "no internet");
		} else if (mBound && userManagerService!=null && userManagerService.isNetworkConnected()) {

			thread = new Thread() {
				private Handler handler = new Handler();

				@Override
				public void run() {
					final List<Notification> notifyList = userManagerService.notifyList(nId);
					if (notifyList == null) {
						handler.post(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								ProfileNotifyFragment.emptyView.setText(userManagerService.getErrorMsg());
								Toast.makeText(getApplicationContext(),userManagerService.getErrorMsg(),Toast.LENGTH_SHORT).show();
								
			
								userManagerService.setErrorMsg();
								//do not change local database and fragment adapter
								if(ProfileNotifyFragment.listNotify.isRefreshing()){
									ProfileNotifyFragment.listNotify.onRefreshComplete();
								}
								
							}

						});
					} else if (notifyList != null) {
						handler.post(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Toast.makeText(getApplicationContext(),userManagerService.getSuccessMsg(),Toast.LENGTH_SHORT).show();
								//dialog.dismissDialog();
								
							}

						});
					}
				}// end of thread run()
			};
			thread.start();

		}
	}
	
	@Override
	public void onViewGroupRequested(final Group group) {
		// TODO Auto-generated method stub
		//download new group, update saved group
		//if unable use passed group
		
		if (mBound && userManagerService!=null && !userManagerService.isNetworkConnected()) {
			
			String error = "No internet conection!\nplease check your settings.";
			Toast.makeText(getApplicationContext(),error,Toast.LENGTH_SHORT).show();
			Log.e("F8thActivity", "no internet");
		} else if (mBound && userManagerService!=null && userManagerService.isNetworkConnected()) {
			thread = new Thread() {
				private Handler handler = new Handler();

				@Override
				public void run() {
					final Group newGroup= userManagerService.downloadGroup(group.getGroupId());
					final ViewGroupFragment viewGroupFragment = new ViewGroupFragment();
					
					if (newGroup == null) {
						handler.post(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								PeopleGroupsFragment.emptyView.setText(userManagerService.getErrorMsg());
								Toast.makeText(getApplicationContext(),userManagerService.getErrorMsg(),Toast.LENGTH_SHORT).show();
								userManagerService.setErrorMsg();
								
								//use passed group
								viewGroupFragment.setGroupId(userManagerService.getUserId(),group.getGroupId());
							}

						});
					} else if (newGroup != null) {
						handler.post(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								viewGroupFragment.setGroupId(userManagerService.getUserId(),newGroup.getGroupId());
								
							}

						});
					}
					
					getSupportFragmentManager()
					.beginTransaction()
					.addToBackStack("list")
					.replace(R.id.content_frame, viewGroupFragment, "view group")
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
					.commit();
					
					popBack++;
				}// end of thread run()
			};
			thread.start();
		}
		
	}

	@Override
	public void onViewProfileRequested(final User user) {
		// TODO Auto-generated method stub
		
		if (mBound && userManagerService!=null && userManagerService!=null && !userManagerService.isNetworkConnected()) {
			
			String error = "No internet conection!\nplease check your settings.";
			Toast.makeText(getApplicationContext(),error,Toast.LENGTH_SHORT).show();
			Log.e("F8thActivity", "no internet");
		} else if (mBound && userManagerService!=null && userManagerService!=null && userManagerService.isNetworkConnected()) {
			//main.removeAllViews();
			thread = new Thread() {
				private Handler handler = new Handler();

				@Override
				public void run() {
					final UserProfile newUser = userManagerService.downloadUserProfile(user.getUserId());
					final ViewProfileFragment viewProfileFragment = new ViewProfileFragment();
					
					if (newUser == null) {
						handler.post(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								PeopleProfilesFragment.emptyView.setText(userManagerService.getErrorMsg());
								Toast.makeText(getApplicationContext(),userManagerService.getErrorMsg(),Toast.LENGTH_SHORT).show();
								userManagerService.setErrorMsg();
								viewProfileFragment.setUserId(user.getUserId());
							}

						});
					} else if (newUser != null) {
						handler.post(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								viewProfileFragment.setUserId(newUser.getUserId());
								
							}

						});
					}
					
					getSupportFragmentManager()
					.beginTransaction()
					.addToBackStack("list")
					.replace(R.id.content_frame, viewProfileFragment, "view profile")
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
					.commit();
					
					popBack++;
				}// end of thread run()
			};
			thread.start();
		}
		
	}

	@Override
	public void onViewStoryRequested(final Story story) {
		// TODO Auto-generated method stub
		if (mBound && userManagerService!=null && !userManagerService.isNetworkConnected()) {
			
			String error = "No internet conection!\nplease check your settings.";
			Toast.makeText(getApplicationContext(),error,Toast.LENGTH_SHORT).show();
			Log.e("F8thActivity", "no internet");
		} else if (mBound && userManagerService!=null && userManagerService.isNetworkConnected()) {
			//main.removeAllViews();
			thread = new Thread() {
				private Handler handler = new Handler();

				@Override
				public void run() {
					
					final Story newStory = userManagerService.downloadStory(story.getStoryId());
					final ViewStoryFragment viewStoryFragment = new ViewStoryFragment();
					
					if (newStory == null) {
						handler.post(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								StoriesFragment.emptyView.setText(userManagerService.getErrorMsg());
								Toast.makeText(getApplicationContext(),userManagerService.getErrorMsg(),Toast.LENGTH_SHORT).show();
								userManagerService.setErrorMsg();
								viewStoryFragment.setStoryId(story.getStoryId());
							}

						});
					} else if (newStory != null) {
						handler.post(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								viewStoryFragment.setStoryId(newStory.getStoryId());
							}

						});
					}
					
					FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
					ft.addToBackStack("list");
					ft.replace(R.id.content_frame, viewStoryFragment, "view story");
					ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
					ft.commit();
					
					popBack++;
				}// end of thread run()
			};
			thread.start();
			//unSetActionBar("Story",story.getAuthor());
		}
		
	}

	@Override
	public void onViewNotificationRequested(Notification notify) {
		// TODO Auto-generated method stub
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		
		ViewNotificationFragment viewNotificationFragment = new ViewNotificationFragment();
		viewNotificationFragment.setNotification(notify);
		
		ft.replace(R.id.content_frame, viewNotificationFragment, "view notification");
		ft.addToBackStack("list");
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		ft.commit();
		
		popBack++;
	}

	@Override
	public UserProfile onGetCurrentUserRequested() {
		// TODO Auto-generated method stub
		if (mBound && userManagerService!=null){
			return userManagerService.getCurrentUser();
		}else{
			return null;
		}
	}

	@Override
	public void onTellStoryRequested(final String story, final String visibility) {
		// TODO Auto-generated method stub
		if (mBound && userManagerService!=null && !userManagerService.isNetworkConnected()) {
			
			String error = "No internet conection!\nplease check your settings.";
			Toast.makeText(getApplicationContext(),error,Toast.LENGTH_SHORT).show();
			Log.e("F8thActivity", "no internet");
		} else if (mBound && userManagerService!=null && userManagerService.isNetworkConnected()) {

			thread = new Thread() {
				private Handler handler = new Handler();

				@Override
				public void run() {
					boolean result = userManagerService.tellStory(story, visibility);
					if(!result){
						handler.post(new Runnable(){
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Toast.makeText(getApplicationContext(),userManagerService.getErrorMsg(),Toast.LENGTH_SHORT).show();
								userManagerService.setErrorMsg();
								
							}
							
						});
					}else if(result){
						handler.post(new Runnable(){
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Toast.makeText(getApplicationContext(), userManagerService.getSuccessMsg(), Toast.LENGTH_SHORT).show();
								userManagerService.setSuccessMsg();
							}
							
						});
					}
				}// end of thread run()
			};
			thread.start();

		}
	}

	@Override
	public void onCreateGroupRequested(final String grpName, final String grpType,final String grpCity,final String grpCountry) {
		// TODO Auto-generated method stub
		if (mBound && userManagerService!=null && !userManagerService.isNetworkConnected()) {
			
			String error = "No internet conection!\nplease check your settings.";
			Toast.makeText(getApplicationContext(),error,Toast.LENGTH_SHORT).show();
			Log.e("F8thActivity", "no internet");
		} else if (mBound && userManagerService!=null && userManagerService.isNetworkConnected()) {

			thread = new Thread() {
				private Handler handler = new Handler();

				@Override
				public void run() {
					boolean result = userManagerService.createGroup(grpName, grpType,grpCity,grpCountry);
					if(!result){
						handler.post(new Runnable(){
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Toast.makeText(getApplicationContext(),userManagerService.getErrorMsg(),Toast.LENGTH_SHORT).show();
								userManagerService.setErrorMsg();
								
							}
							
						});
					}else if(result){
						handler.post(new Runnable(){
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Toast.makeText(getApplicationContext(), userManagerService.getSuccessMsg(), Toast.LENGTH_SHORT).show();
								userManagerService.setSuccessMsg();
							}
							
						});
					}
				}// end of thread run()
			};
			thread.start();

		}
	}
//----------------------------------------------------------------------------------------------------------------------------	
	
	@Override
	public String onGetUserId(){
		if(mBound && userManagerService!=null){
			return userManagerService.getUserId();
		}else{
			return "";
		}	
	}
	
	@Override
	public void onUpdateAutoLogin(final boolean state) {
		// TODO Auto-generated method stub
		if(mBound && userManagerService!=null && !userManagerService.isNetworkConnected()){
			
			String error = "No internet conection!\nplease check your settings.";
			Toast.makeText(getApplicationContext(),error,Toast.LENGTH_SHORT).show();
			Log.e("F8thActivity", "no internet");
			if(settingsFragment!=null){
				settingsFragment.unSetProgress();
			}
			Log.e("MainActivity","no internet");
		}else if(mBound && userManagerService!=null  && userManagerService.isNetworkConnected()){
			thread = new Thread(){
				private Handler handler = new Handler();
				@Override
				public void run(){
					boolean result =  userManagerService.updateAutoLogin(state);
					if(!result){
						handler.post(new Runnable(){
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Toast.makeText(getApplicationContext(),userManagerService.getErrorMsg(),Toast.LENGTH_SHORT).show();
									userManagerService.setErrorMsg();
									if(settingsFragment!=null){
										settingsFragment.unSetProgress();
									}
								
							}
							
						});
					}else if(result){
						handler.post(new Runnable(){
							@Override
							public void run() {
								// TODO Auto-generated method stub
								if(settingsFragment!=null){
									settingsFragment.unSetProgress();
								}
								
							}
							
						});
					}
				}//end of thread run()
			};
			thread.start();
		}
	}
	
	@Override
	public void onUpdateProfileRequested(final String why, final String desire, final String about) {
		// TODO Auto-generated method stub
		if(mBound && userManagerService!=null && !userManagerService.isNetworkConnected()){
			
			String error = "No internet conection!\nplease check your settings.";
			Toast.makeText(getApplicationContext(),error,Toast.LENGTH_SHORT).show();
			Log.e("F8thActivity", "no internet");
		}else if(mBound && userManagerService!=null  && userManagerService.isNetworkConnected()){
			thread = new Thread(){
				private Handler handler = new Handler();
				@Override
				public void run(){
					boolean result = userManagerService.updateProfile(about, why,desire);
					if(!result){
						handler.post(new Runnable(){
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Toast.makeText(getApplicationContext(),userManagerService.getErrorMsg(),Toast.LENGTH_SHORT).show();
								userManagerService.setErrorMsg();
								settingsFragment.dialog.dismiss();//1st dialog
								
							}
							
						});
					}else if(result){
						handler.post(new Runnable(){
							@Override
							public void run() {
								// TODO Auto-generated method stub
								settingsFragment.dialog.dismiss();//1st dialog
								Toast.makeText(getApplicationContext(), userManagerService.getSuccessMsg(), Toast.LENGTH_SHORT).show();
								userManagerService.setSuccessMsg();
								//}
							}
							
						});
					}
				}//end of thread run()
			};
			thread.start();
		}
	
	}

	@Override
	public void onUpdateDetailsRequested(final String email, final String fname,
			final String lname, final String gender, final String country) {
		// TODO Auto-generated method stub
		if(mBound && userManagerService!=null && !userManagerService.isNetworkConnected()){
			
			String error = "No internet conection!\nplease check your settings.";
			Toast.makeText(getApplicationContext(),error,Toast.LENGTH_SHORT).show();
			Log.e("F8thActivity", "no internet");
		}else if(mBound && userManagerService!=null  && userManagerService.isNetworkConnected()){
			thread = new Thread(){
				private Handler handler = new Handler();
				@Override
				public void run(){
					boolean result =  userManagerService.updateUser(email, fname, lname, gender, country);
					if(!result){
						handler.post(new Runnable(){
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Toast.makeText(getApplicationContext(),userManagerService.getErrorMsg(),Toast.LENGTH_SHORT).show();
								userManagerService.setErrorMsg();
								settingsFragment.dialog.dismiss();//1st dialog
							}
							
						});
					}else if(result){
						handler.post(new Runnable(){
							@Override
							public void run() {
								// TODO Auto-generated method stub
								userManagerService.setSuccessMsg();
								Toast.makeText(getApplicationContext(), userManagerService.getSuccessMsg(), Toast.LENGTH_SHORT).show();
								
							}
							
						});
					}
				}//end of thread run()
			};
			thread.start();
		}
	}

	
	@Override
	public void onResetPasswordRequested(String oldPassword, final String newPassword) {
		// TODO Auto-generated method stub
		if(mBound && !userManagerService.isNetworkConnected()){
			
			dialog.alertDialog("No internet conection!\nplease check your settings.");
			settingsFragment.unSetProgress();
			Log.e("F8thActivity","no internet");
		}else if(mBound && userManagerService.isNetworkConnected()){
			//dialog.setupDialog("updating story");
			if(userManagerService.getPassword().equals(oldPassword)){
			//passwords match
				thread = new Thread(){
					private Handler handler = new Handler();
					@Override
					public void run(){
						boolean result = userManagerService.resetPassword(newPassword);
						if(!result){
							handler.post(new Runnable(){
								@Override
								public void run() {
									// TODO Auto-generated method stub
									if(dialog.alertDialog(userManagerService.getErrorMsg())){
										userManagerService.setErrorMsg();
										settingsFragment.dialog.dismiss();//1st dialog
									}
								}
								
							});
						}else if(result){
							handler.post(new Runnable(){
								@Override
								public void run() {
									// TODO Auto-generated method stub
									
									settingsFragment.dialog.dismiss();//1st dialog
									startActivity(new Intent(F8thActivity.this,LoginActivity.class));
									dialog.dismissDialog();
									Toast.makeText(getApplicationContext(), "password reset\nplease login with your new password.", Toast.LENGTH_SHORT).show();
									F8thActivity.this.finish();
								}
								
							});
						}
					}//end of thread run()
				};
				thread.start();
			}else{
				settingsFragment.dialog.dismiss();//1st dialog
				dialog.alertDialog("current password incorrect");
			}
		}
		
	}

	@Override
	public void onLogoutRequested() {
		// TODO Auto-generated method stub
		this.onBackPressed();
	}

	
	@Override
	public void onDeregisterRequested() {
		// TODO Auto-generated method stub
		if(mBound && userManagerService!=null && !userManagerService.isNetworkConnected()){
			
			String error = "No internet conection!\nplease check your settings.";
			Toast.makeText(getApplicationContext(),error,Toast.LENGTH_SHORT).show();
			Log.e("F8thActivity", "no internet");
		}else if(mBound && userManagerService!=null  && userManagerService.isNetworkConnected()){
			thread = new Thread(){
				private Handler handler = new Handler();
				@Override
				public void run(){
					boolean result = userManagerService.deregisterUser();
					if(!result){
						handler.post(new Runnable(){
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Toast.makeText(getApplicationContext(),userManagerService.getErrorMsg(),Toast.LENGTH_SHORT).show();
								userManagerService.setErrorMsg();
									if(settingsFragment!=null){
										settingsFragment.unSetProgress();
									}
								
							}
							
						});
					}else if(result){
						handler.post(new Runnable(){
							@Override
							public void run() {
								// TODO Auto-generated method stub
								if(settingsFragment!=null){
									settingsFragment.unSetProgress();
								}
								Toast.makeText(getApplicationContext(),userManagerService.getSuccessMsg(), Toast.LENGTH_SHORT).show();
								F8thActivity.this.onBackPressed();
							}
							
						});
					}
				}//end of thread run()
			};
			thread.start();
		}
	}

	
	
//-----------------------------------------------------------------------------------------------------------------

	
	@Override
	public void onUpdateStoryRequested(final String storyId, final String story,final String visibility) {
		// TODO Auto-generated method stub
		if (mBound && userManagerService!=null && !userManagerService.isNetworkConnected()) {
			
			String error = "No internet conection!\nplease check your settings.";
			Toast.makeText(getApplicationContext(),error,Toast.LENGTH_SHORT).show();
			Log.e("F8thActivity", "no internet");
		} else if (mBound && userManagerService!=null && userManagerService.isNetworkConnected()) {

			thread = new Thread() {
				private Handler handler = new Handler();

				@Override
				public void run() {
					boolean result = userManagerService.updateStory(storyId, story, visibility);
					if(!result){
						handler.post(new Runnable(){
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Toast.makeText(getApplicationContext(),userManagerService.getErrorMsg(),Toast.LENGTH_SHORT).show();
								userManagerService.setErrorMsg();
								
							}
							
						});
					}else if(result){
						handler.post(new Runnable(){
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Toast.makeText(getApplicationContext(), userManagerService.getSuccessMsg(), Toast.LENGTH_SHORT).show();
								userManagerService.setSuccessMsg();
							}
							
						});
					}
				}// end of thread run()
			};
			thread.start();

		}
	}


	@Override
	public void onDeleteStoryRequested(final String storyId) {
		// TODO Auto-generated method stub
		if (mBound && userManagerService!=null && !userManagerService.isNetworkConnected()) {
			
			String error = "No internet conection!\nplease check your settings.";
			Toast.makeText(getApplicationContext(),error,Toast.LENGTH_SHORT).show();
			Log.e("F8thActivity", "no internet");
		} else if (mBound && userManagerService!=null && userManagerService.isNetworkConnected()) {

			thread = new Thread() {
				private Handler handler = new Handler();

				@Override
				public void run() {
					boolean result = userManagerService.deleteStory(storyId);
					if(!result){
						handler.post(new Runnable(){
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Toast.makeText(getApplicationContext(),userManagerService.getErrorMsg(),Toast.LENGTH_SHORT).show();
								userManagerService.setErrorMsg();
								
							}
							
						});
					}else if(result){
						handler.post(new Runnable(){
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Toast.makeText(getApplicationContext(), userManagerService.getSuccessMsg(), Toast.LENGTH_SHORT).show();
								userManagerService.setSuccessMsg();
								//F8thActivity.this.finish();
							}
							
						});
					}
				}// end of thread run()
			};
			thread.start();

		}
	}


	@Override
	public void onFavoriteStoryRequested(final String storyId) {
		// TODO Auto-generated method stub
		if (mBound && userManagerService!=null && !userManagerService.isNetworkConnected()) {
			
			String error = "No internet conection!\nplease check your settings.";
			Toast.makeText(getApplicationContext(),error,Toast.LENGTH_SHORT).show();
			Log.e("F8thActivity", "no internet");
		} else if (mBound && userManagerService!=null && userManagerService.isNetworkConnected()) {

			thread = new Thread() {
				private Handler handler = new Handler();

				@Override
				public void run() {
					boolean result = userManagerService.favoriteStory(storyId);
					if(!result){
						handler.post(new Runnable(){
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Toast.makeText(getApplicationContext(),userManagerService.getErrorMsg(),Toast.LENGTH_SHORT).show();
								userManagerService.setErrorMsg();
								
							}
							
						});
					}else if(result){
						handler.post(new Runnable(){
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Toast.makeText(getApplicationContext(), userManagerService.getSuccessMsg(), Toast.LENGTH_SHORT).show();
								userManagerService.setSuccessMsg();
							}
							
						});
					}
				}// end of thread run()
			};
			thread.start();

		}
	}


	@Override
	public void onUnFavoriteStoryRequested(final String storyId) {
		// TODO Auto-generated method stub
		if (mBound && userManagerService!=null && !userManagerService.isNetworkConnected()) {
			
			String error = "No internet conection!\nplease check your settings.";
			Toast.makeText(getApplicationContext(),error,Toast.LENGTH_SHORT).show();
			Log.e("F8thActivity", "no internet");
		} else if (mBound && userManagerService!=null && userManagerService.isNetworkConnected()) {

			thread = new Thread() {
				private Handler handler = new Handler();

				@Override
				public void run() {
					boolean result = userManagerService.unFavoriteStory(storyId);
					if(!result){
						handler.post(new Runnable(){
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Toast.makeText(getApplicationContext(),userManagerService.getErrorMsg(),Toast.LENGTH_SHORT).show();
								userManagerService.setErrorMsg();
								
							}
							
						});
					}else if(result){
						handler.post(new Runnable(){
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Toast.makeText(getApplicationContext(), userManagerService.getSuccessMsg(), Toast.LENGTH_SHORT).show();
								userManagerService.setSuccessMsg();
							}
							
						});
					}
				}// end of thread run()
			};
			thread.start();

		}
	}

//----------------------------------------------------------------------------------------------------------------------------

	@Override
	public void onEditGroupRequested(final String grpId, final String grpName,final String grpType,final String grpCity,final String grpCountry) {
		// TODO Auto-generated method stub
		if (mBound && userManagerService!=null && !userManagerService.isNetworkConnected()) {
			
			String error = "No internet conection!\nplease check your settings.";
			Toast.makeText(getApplicationContext(),error,Toast.LENGTH_SHORT).show();
			Log.e("F8thActivity", "no internet");
		} else if (mBound && userManagerService!=null && userManagerService.isNetworkConnected()) {

			thread = new Thread() {
				private Handler handler = new Handler();

				@Override
				public void run() {
					boolean result = userManagerService.editGroup(grpId, grpName, grpType,grpCity,grpCountry);
					if(!result){
						handler.post(new Runnable(){
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Toast.makeText(getApplicationContext(),userManagerService.getErrorMsg(),Toast.LENGTH_SHORT).show();
								userManagerService.setErrorMsg();
								
							}
							
						});
					}else if(result){
						handler.post(new Runnable(){
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Toast.makeText(getApplicationContext(), userManagerService.getSuccessMsg(), Toast.LENGTH_SHORT).show();
								userManagerService.setSuccessMsg();
							}
							
						});
					}
				}// end of thread run()
			};
			thread.start();

		}
	}


	@Override
	public void onDeleteGroupRequested(final String grpId) {
		// TODO Auto-generated method stub
		if (mBound && userManagerService!=null && !userManagerService.isNetworkConnected()) {
			
			String error = "No internet conection!\nplease check your settings.";
			Toast.makeText(getApplicationContext(),error,Toast.LENGTH_SHORT).show();
			Log.e("F8thActivity", "no internet");
		} else if (mBound && userManagerService!=null && userManagerService.isNetworkConnected()) {

			thread = new Thread() {
				private Handler handler = new Handler();

				@Override
				public void run() {
					boolean result = userManagerService.deleteGroup(grpId);
					if(!result){
						handler.post(new Runnable(){
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Toast.makeText(getApplicationContext(),userManagerService.getErrorMsg(),Toast.LENGTH_SHORT).show();
								userManagerService.setErrorMsg();
								
							}
							
						});
					}else if(result){
						handler.post(new Runnable(){
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Toast.makeText(getApplicationContext(), userManagerService.getSuccessMsg(), Toast.LENGTH_SHORT).show();
								userManagerService.setSuccessMsg();
							}
							
						});
					}
				}// end of thread run()
			};
			thread.start();

		}
	}


	@Override
	public void onJoinGroupRequested(final String grpId, final String memberType) {
		// TODO Auto-generated method stub
		if (mBound && userManagerService!=null && !userManagerService.isNetworkConnected()) {
			
			String error = "No internet conection!\nplease check your settings.";
			Toast.makeText(getApplicationContext(),error,Toast.LENGTH_SHORT).show();
			Log.e("F8thActivity", "no internet");
		} else if (mBound && userManagerService!=null && userManagerService.isNetworkConnected()) {

			thread = new Thread() {
				private Handler handler = new Handler();

				@Override
				public void run() {
					boolean result = userManagerService.joinGroup(grpId, memberType);
					if(!result){
						handler.post(new Runnable(){
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Toast.makeText(getApplicationContext(),userManagerService.getErrorMsg(),Toast.LENGTH_SHORT).show();
								userManagerService.setErrorMsg();
								
							}
							
						});
					}else if(result){
						handler.post(new Runnable(){
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Toast.makeText(getApplicationContext(), userManagerService.getSuccessMsg(), Toast.LENGTH_SHORT).show();
								userManagerService.setSuccessMsg();
							}
							
						});
					}
				}// end of thread run()
			};
			thread.start();

		}
	}


	@Override
	public void onLeaveGroupRequested(final String grpId) {
		// TODO Auto-generated method stub
		if (mBound && userManagerService!=null && !userManagerService.isNetworkConnected()) {
			
			String error = "No internet conection!\nplease check your settings.";
			Toast.makeText(getApplicationContext(),error,Toast.LENGTH_SHORT).show();
			Log.e("F8thActivity", "no internet");
		} else if (mBound && userManagerService!=null && userManagerService.isNetworkConnected()) {

			thread = new Thread() {
				private Handler handler = new Handler();

				@Override
				public void run() {
					boolean result = userManagerService.leaveGroup(grpId);
					if(!result){
						handler.post(new Runnable(){
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Toast.makeText(getApplicationContext(),userManagerService.getErrorMsg(),Toast.LENGTH_SHORT).show();
								userManagerService.setErrorMsg();
								
							}
							
						});
					}else if(result){
						handler.post(new Runnable(){
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Toast.makeText(getApplicationContext(), userManagerService.getSuccessMsg(), Toast.LENGTH_SHORT).show();
								userManagerService.setSuccessMsg();
							}
							
						});
					}
				}// end of thread run()
			};
			thread.start();

		}
	}


	@Override
	public void onDeleteNotificationRequested(final String notifyId) {
		// TODO Auto-generated method stub
		if (mBound && userManagerService!=null && !userManagerService.isNetworkConnected()) {
			
			String error = "No internet conection!\nplease check your settings.";
			Toast.makeText(getApplicationContext(),error,Toast.LENGTH_SHORT).show();
			Log.e("F8thActivity", "no internet");
		} else if (mBound && userManagerService!=null && userManagerService.isNetworkConnected()) {

			thread = new Thread() {
				private Handler handler = new Handler();

				@Override
				public void run() {
					boolean result = userManagerService.deleteNotification(notifyId);
					if(!result){
						handler.post(new Runnable(){
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Toast.makeText(getApplicationContext(),userManagerService.getErrorMsg(),Toast.LENGTH_SHORT).show();
								userManagerService.setErrorMsg();
								//close this fragment
							}
							
						});
					}else if(result){
						handler.post(new Runnable(){
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Toast.makeText(getApplicationContext(), userManagerService.getSuccessMsg(), Toast.LENGTH_SHORT).show();
								userManagerService.setSuccessMsg();
							}
							
						});
					}
				}// end of thread run()
			};
			thread.start();

		}
	}
	
	
//--------------------------------------------------------------------------------------------------------------
	
	@Override
	public void onMarkAsReadRequested(final String notifyId) {
		// TODO Auto-generated method stub
		if (mBound && userManagerService!=null && !userManagerService.isNetworkConnected()) {
			
			String error = "No internet conection!\nplease check your settings.";
			Toast.makeText(getApplicationContext(),error,Toast.LENGTH_SHORT).show();
			Log.e("F8thActivity", "no internet");
		} else if (mBound && userManagerService!=null && userManagerService.isNetworkConnected()) {

			thread = new Thread() {
				private Handler handler = new Handler();

				@Override
				public void run() {
					boolean result = userManagerService.markAsRead(notifyId);
					if(!result){
						handler.post(new Runnable(){
							@Override
							public void run() {
								// TODO Auto-generated method stub
								//Toast.makeText(getApplicationContext(),userManagerService.getErrorMsg(),Toast.LENGTH_SHORT).show();
								userManagerService.setErrorMsg();
								
							}
							
						});
					}else if(result){
						handler.post(new Runnable(){
							@Override
							public void run() {
								// TODO Auto-generated method stub
								//Toast.makeText(getApplicationContext(), userManagerService.getSuccessMsg(), Toast.LENGTH_SHORT).show();
								userManagerService.setSuccessMsg();
							}
							
						});
					}
				}// end of thread run()
			};
			thread.start();

		}
	}

//====================================================================================================================
	
}//END OF CLASS F8thActivity
