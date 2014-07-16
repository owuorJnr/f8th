/**
* All Honor and Glory belongs to God Almighty
* Phil 2:13
*@author Dickson Owuor <dickytea@gmail.com>
*@version 1.1
*@since 2014-1-03
*@see
*
*<p>This is the main service.</p>
*/

package owuor.f8th.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import owuor.f8th.ContentProvider.F8thContentProvider;
import owuor.f8th.communication.NetworkOperator;
import owuor.f8th.database.GroupsTable;
import owuor.f8th.database.LocalUsersTable;
import owuor.f8th.database.NotificationsTable;
import owuor.f8th.database.ProfilesTable;
import owuor.f8th.database.StoriesTable;
import owuor.f8th.interfaces.INetworkOperator;
import owuor.f8th.interfaces.IUserManager;
import owuor.f8th.parser.JSONParser;
import owuor.f8th.types.F8th;
import owuor.f8th.types.Group;
import owuor.f8th.types.Notification;
import owuor.f8th.types.Story;
import owuor.f8th.types.User;
import owuor.f8th.types.UserProfile;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class UserManagerService extends Service implements IUserManager {

	private Timer timer;
	private boolean mTimer = false;
	
	INetworkOperator<NameValuePair> networkOperator;
	ConnectivityManager connection = null;
	//NotificationManager notificationManager;

	private final IBinder mBinder = new LocalBinder();

	// userProfile defined variables
	private UserProfile userProfile;

	// string variables
	private String userid = "";
	private String email = "";
	private String password = "";
	private String errorMsg = "";
	private String successMsg = "";
	
	//private String autoLogin = "";

	// boolean variables
	private boolean userAuthenticated = false;
	private boolean otherUsersAutoLoginUnset = false;

//===============================================================================================
	
	public class LocalBinder extends Binder {
		public UserManagerService getService() {

			return UserManagerService.this;
		}
	}// end of CLASS LocalBinder

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return mBinder;
	}// end of method onBind()

	@Override
	public void onCreate() {
		networkOperator = new NetworkOperator();
		connection = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		
		this.setSuccessMsg();
		this.setErrorMsg();
		
		timer = new Timer();
		//NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE); 

	}// end of method onCreate()

	public void onDestroy() {
		super.onDestroy();
		Log.e("UserManagerService", "service finished");
	}// end of onDestroy()

	// =====================================================================================================

	@Override
	public boolean loginUser(String email, String password) {
		this.email = email;
		this.password = password;
		userAuthenticated = true;

		return true;
	}

	@Override
	public boolean logoutUser() {
		// TODO Auto-generated method stub
		this.userid = null;
		this.email = null;
		this.password = null;
		userAuthenticated = false;
		return true;// null;
	}
	
	@Override
	public boolean isNetworkConnected(){
		// method for testing Internet connection
		// TODO Auto-generated method stub
		if (connection != null) {
			NetworkInfo[] info = connection.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						//Log.i("Network Operator", "connection available");
						return connection.getActiveNetworkInfo().isConnected();
					}
				}// end of for-loop
			}
		}
		Log.e("Network Operator", "no connection");
		return false;
	}

	@Override
	public String getUserId(){
		return userid;
	}
	
	@Override
	public String getEmail() {
		return email;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getErrorMsg() {
		return errorMsg;
	}

	@Override
	public String getSuccessMsg() {
		return successMsg;
	}

	@Override
	public UserProfile getCurrentUser(){
		return userProfile;
	}
	
	@Override
	public void setErrorMsg() {
		// TODO Auto-generated method stub
		this.errorMsg = "unknown error: please logout\ntry again later...";
	}

	@Override
	public void setSuccessMsg() {
		// TODO Auto-generated method stub
		this.successMsg = "successfully completed";
	}
	
	@Override
	public void exit() {
		// TODO Auto-generated method stub
		if(mTimer){
			timer.cancel();
		}
		this.stopSelf();
		Log.e("UserManagerService","service finished");
	}

	// --------------------------------------------------------------------------------------------------------

	@Override
	public boolean isUserAuthenticated() {
		// method for authenticating userProfile - implemented through interface
		// TODO Auto-generated method stub
		return userAuthenticated;
	}

	@Override
	public boolean authenticateUser(String email, String password) {
		// TODO Auto-generated method stub
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", F8th.signin_tag));
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("password", password));

		Log.e("UserManagerService", "sending authentication data");
		JSONObject json = networkOperator.sendHttpRequest(params);

		try {
			if (json != null && json.getString(F8th.KEY_SUCCESS) != null) {
				String result = json.getString(F8th.KEY_SUCCESS);
				if (Integer.parseInt(result) == 1) {
					loginUser(email,password);
					this.userid = json.getString("userid");
					
					ContentValues values = new ContentValues();
					values.put(LocalUsersTable.COLUMN_EMAIL, email);
					values.put(LocalUsersTable.COLUMN_PASSWORD, password);

					Uri uri = Uri.parse(F8thContentProvider.CONTENT_URI + "/" + LocalUsersTable.TABLE_LOCAL_USERS);
					String selection = LocalUsersTable.COLUMN_USER_ID + "='" + userid + "'";
					getContentResolver().update(uri, values, selection, null);
					
					successMsg = json.getString(F8th.KEY_SUCCESS_MSG);
					Log.e("UserManagerService", "userProfile authenticated");
					
					return true;
				} else if (json.getString(F8th.KEY_ERROR) != null) {
					result = json.getString(F8th.KEY_ERROR);
					if (Integer.parseInt(result) == 1
							|| Integer.parseInt(result) == 2
							|| Integer.parseInt(result) == 3) {
						userAuthenticated = false;
						Log.e("UserManagerService", "userProfile not authenticated");
						this.errorMsg = json.getString(F8th.KEY_ERROR_MSG);
						return false;
					}
				}
			} else {
				Log.e("UserManagerService", "unknown error: login failed");
				this.errorMsg = "unknown error: sign in failed\ntry again later...";
				return false;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			userAuthenticated = false;
			Log.e("UserManagerService", "caught: sign up failed");
			return false;
			// return e.getMessage();
		
		}
		userAuthenticated = false;
		Log.e("UserManagerService", "null: sign up failed");
		return false;
	}// end of method authenticateUser()

	@Override
	public boolean signUpUser(String email, String fname, String lname,
			String gender, String country, String password) {
		// method for saving record in both online and offline databases
		// TODO Auto-generated method stub

		// save to online server
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", F8th.signup_tag));
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("fname", codeApostrophe(fname)));
		params.add(new BasicNameValuePair("lname", codeApostrophe(lname)));
		params.add(new BasicNameValuePair("gender", gender));
		params.add(new BasicNameValuePair("country", codeApostrophe(country)));
		params.add(new BasicNameValuePair("password", password));

		JSONObject json = networkOperator.sendHttpRequest(params);

		try {
			if (json != null && json.getString(F8th.KEY_SUCCESS) != null) {
				String result = json.getString(F8th.KEY_SUCCESS);
				if (result != null && Integer.parseInt(result) == 1) {
					loginUser(email,password);
					this.userid = json.getString("userid");
					
					// save offline
					ContentValues values = new ContentValues();
					values.put(LocalUsersTable.COLUMN_AUTOLOGIN, F8th.AUTO_LOGIN_UNSET);
					values.put(LocalUsersTable.COLUMN_EMAIL, email);
					values.put(LocalUsersTable.COLUMN_PASSWORD, password);
					values.put(LocalUsersTable.COLUMN_FNAME, fname);
					values.put(LocalUsersTable.COLUMN_LNAME, lname);
					values.put(LocalUsersTable.COLUMN_GENDER, gender);
					values.put(LocalUsersTable.COLUMN_COUNTRY, country);

					Uri uri = Uri.parse(F8thContentProvider.CONTENT_URI + "/" + LocalUsersTable.TABLE_LOCAL_USERS);
					this.getContentResolver().insert(uri, values);

					successMsg = json.getString(F8th.KEY_SUCCESS_MSG);
					Log.e("UserManagerService", "sign up successful");
					return true;
				} else if (json.getString(F8th.KEY_ERROR) != null) {
					result = json.getString(F8th.KEY_ERROR);
					if (Integer.parseInt(result) == 1
							|| Integer.parseInt(result) == 2
							|| Integer.parseInt(result) == 3) {
						// return json.getString(KEY_ERROR_MSG);
						this.errorMsg = json.getString(F8th.KEY_ERROR_MSG);
						Log.e("UserManagerService", "error: sign up failed");
						return false;
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("UserManagerService", "caught: sign up failed");
			this.errorMsg = e.getMessage();
			return false;// e.getMessage();
		}
		Log.e("UserManagerService", "nothing: sign up failed");
		this.errorMsg = "nothing: sign up failed";
		return false;// null;
	}// end of function

	@Override
	public boolean updateUser(String newEmail, String fname, String lname,
			String gender, String country) {
		// TODO Auto-generated method stub

		// update online
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", F8th.update_user_tag));
		params.add(new BasicNameValuePair("user-id", userid));
		params.add(new BasicNameValuePair("password", password));
		params.add(new BasicNameValuePair("email", newEmail));
		params.add(new BasicNameValuePair("fname", codeApostrophe(fname)));
		params.add(new BasicNameValuePair("lname", codeApostrophe(lname)));
		params.add(new BasicNameValuePair("gender", gender));
		params.add(new BasicNameValuePair("country", codeApostrophe(country)));

		JSONObject json = networkOperator.sendHttpRequest(params);

		try {
			if (json != null && json.getString(F8th.KEY_SUCCESS) != null) {
				String result = json.getString(F8th.KEY_SUCCESS);
				if (Integer.parseInt(result) == 1) {

					// update offline
					ContentValues values = new ContentValues();
					values.put(LocalUsersTable.COLUMN_EMAIL, newEmail);
					values.put(LocalUsersTable.COLUMN_FNAME, fname);
					values.put(LocalUsersTable.COLUMN_LNAME, lname);
					values.put(LocalUsersTable.COLUMN_GENDER, gender);
					values.put(LocalUsersTable.COLUMN_COUNTRY, country);

					Uri uri = Uri.parse(F8thContentProvider.CONTENT_URI + "/" + LocalUsersTable.TABLE_LOCAL_USERS);
					String selection = LocalUsersTable.COLUMN_USER_ID + "='" + userid + "'";
					this.getContentResolver().update(uri, values, selection, null);

					successMsg = json.getString(F8th.KEY_SUCCESS_MSG);
					this.email = newEmail;//json.getString("email");
					Log.e("UserManagerService", "details updated");
					this.userProfile = downloadUserProfile(userid);
					return true;
				} else if (json.getString(F8th.KEY_ERROR) != null) {
					result = json.getString(F8th.KEY_ERROR);
					if (Integer.parseInt(result) == 1
							|| Integer.parseInt(result) == 2
							|| Integer.parseInt(result) == 3) {
						this.errorMsg = json.getString(F8th.KEY_ERROR_MSG);
						Log.e("UserManagerService",
								"error: update details failed");
						return false;
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("UserManagerService", "caught: update details failed");
			this.errorMsg = e.getMessage();
			return false;// e.getMessage();
		}
		Log.e("UserManagerService", "nothing: update details failed");
		return false;// null;
	}// end of method updateDetails()

	@Override
	public boolean updateProfile(String favVerse, String why, String desire) {
		// method for updating info in both online and offline databases
		// TODO Auto-generated method stub

		// update online server
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", F8th.update_profile_tag));
		params.add(new BasicNameValuePair("user-id", userid));
		params.add(new BasicNameValuePair("password", password));
		params.add(new BasicNameValuePair("fav-verse", codeApostrophe(favVerse)));
		params.add(new BasicNameValuePair("why", codeApostrophe(why)));
		params.add(new BasicNameValuePair("desire", codeApostrophe(desire)));

		JSONObject json = networkOperator.sendHttpRequest(params);

		try {
			if (json != null && json.getString(F8th.KEY_SUCCESS) != null) {
				String result = json.getString(F8th.KEY_SUCCESS);
				if (Integer.parseInt(result) == 1) {

					// update offline database
					ContentValues values = new ContentValues();
					values.put(LocalUsersTable.COLUMN_FAV_VERSE, favVerse);
					values.put(LocalUsersTable.COLUMN_WHY, why);
					values.put(LocalUsersTable.COLUMN_DESIRE, desire);

					Uri uri = Uri.parse(F8thContentProvider.CONTENT_URI + "/" + LocalUsersTable.TABLE_LOCAL_USERS);
					String selection = LocalUsersTable.COLUMN_USER_ID + "='" + userid + "'";
					this.getContentResolver().update(uri, values, selection, null);

					successMsg = json.getString(F8th.KEY_SUCCESS_MSG);
					Log.e("UserManagerService", "story updated");
					this.userProfile = downloadUserProfile(userid);
					return true;
				} else if (json.getString(F8th.KEY_ERROR) != null) {
					result = json.getString(F8th.KEY_ERROR);
					if (Integer.parseInt(result) == 1
							|| Integer.parseInt(result) == 2
							|| Integer.parseInt(result) == 3) {
						this.errorMsg = json.getString(F8th.KEY_ERROR_MSG);
						Log.e("UserManagerService",
								"error: update story failed");
						return false;
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("UserManagerService", "caught: update story failed");
			this.errorMsg = e.getMessage();
			return false;// e.getMessage();
		}
		Log.e("UserManagerService", "nothing: update story failed");
		return false;// null;
	}

	@Override
	public boolean resetPassword(String newPass) {
		// TODO Auto-generated method stub
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", F8th.reset_passwd_tag));
		params.add(new BasicNameValuePair("user-id", userid));
		params.add(new BasicNameValuePair("current-password", password));
		params.add(new BasicNameValuePair("new-password", newPass));

		JSONObject json = networkOperator.sendHttpRequest(params);

		try {
			if (json != null && json.getString(F8th.KEY_SUCCESS) != null) {
				String result = json.getString(F8th.KEY_SUCCESS);
				if (Integer.parseInt(result) == 1) {

					// update offline database
					ContentValues values = new ContentValues();
					values.put(LocalUsersTable.COLUMN_PASSWORD, newPass);

					Uri uri = Uri.parse(F8thContentProvider.CONTENT_URI + "/" + LocalUsersTable.TABLE_LOCAL_USERS);
					String selection = LocalUsersTable.COLUMN_USER_ID + "='" + userid + "'";
					this.getContentResolver().update(uri, values, selection, null);

					successMsg = json.getString(F8th.KEY_SUCCESS_MSG);
					this.password = newPass;//json.getString("password");
					return true;
				} else if (json.getString(F8th.KEY_ERROR) != null) {
					result = json.getString(F8th.KEY_ERROR);
					if (Integer.parseInt(result) == 1
							|| Integer.parseInt(result) == 2
							|| Integer.parseInt(result) == 3) {
						this.errorMsg = json.getString(F8th.KEY_ERROR_MSG);
						Log.e("UserManagerService",
								"error: unable to reset password");
						return false;
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("UserManagerService", "caught: reset password failed");
			this.errorMsg = e.getMessage();
			return false;// e.getMessage();
		}
		Log.e("UserManagerService", "nothing: reset password failed");
		return false;// null;
	}

	@Override
	public boolean forgotPassword(String email) {
		// TODO Auto-generated method stub
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", F8th.forgot_passwd_tag));
		params.add(new BasicNameValuePair("email", email));

		JSONObject json = networkOperator.sendHttpRequest(params);

		try {
			if (json != null && json.getString(F8th.KEY_SUCCESS) != null) {
				String result = json.getString(F8th.KEY_SUCCESS);
				if (Integer.parseInt(result) == 1) {
					successMsg = json.getString(F8th.KEY_SUCCESS_MSG);
					Log.e("UserManagerService", "password sent to email");
					return true;
				} else if (Integer.parseInt(result) == 0
						&& json.getString(F8th.KEY_ERROR) != null) {
					result = json.getString(F8th.KEY_ERROR);
					if (Integer.parseInt(result) == 1
							|| Integer.parseInt(result) == 2
							|| Integer.parseInt(result) == 3) {
						Log.e("UserManagerService",
								"error: resetting password failed");
						this.errorMsg = json.getString(F8th.KEY_ERROR_MSG);
						return false;
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("UserManagerService", "caught: resetting password failed");
			this.errorMsg = e.getMessage();
			return false;// e.getMessage();
		}
		Log.e("UserManagerService", "null: resetting password failed");
		return false;// null;
	}// end of function

	@Override
	public boolean deregisterUser() {
		// TODO Auto-generated method stub

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", F8th.deregister_tag));
		params.add(new BasicNameValuePair("user-id", userid));
		params.add(new BasicNameValuePair("password", password));

		JSONObject json = networkOperator.sendHttpRequest(params);

		try {
			if (json != null && json.getString(F8th.KEY_SUCCESS) != null) {
				String result = json.getString(F8th.KEY_SUCCESS);
				if (Integer.parseInt(result) == 1) {

					Uri uri = Uri.parse(F8thContentProvider.CONTENT_URI + "/" + LocalUsersTable.TABLE_LOCAL_USERS);
					String selection = LocalUsersTable.COLUMN_USER_ID + "='" + userid + "'";
					getContentResolver().delete(uri, selection, null);

					successMsg = json.getString(F8th.KEY_SUCCESS_MSG);
					Log.e("UserManagerService", "userProfile deregistered");
					return true;
				} else if (Integer.parseInt(result) == 0
						&& json.getString(F8th.KEY_ERROR) != null) {
					result = json.getString(F8th.KEY_ERROR);
					if (Integer.parseInt(result) == 1
							|| Integer.parseInt(result) == 2
							|| Integer.parseInt(result) == 3) {
						// return json.getString(KEY_ERROR_MSG);
						Log.e("UserManagerService",
								"error: userProfile deregister failed");
						this.errorMsg = json.getString(F8th.KEY_ERROR_MSG);
						return false;
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("UserManagerService", "caught: userProfile deregister failed");
			this.errorMsg = e.getMessage();
			return false;// e.getMessage();
		}
		Log.e("UserManagerService", "null: userProfile deregister failed");
		return false;

	}// end of method deregister()
	
//----------------------------------------------Get Functions------------------------------------------
	
	@Override
	public UserProfile downloadUserProfile(String profileId) {
		// TODO Auto-generated method stub
		//get userProfile's story alone
		JSONParser jsonParser = new JSONParser();
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
	        params.add(new BasicNameValuePair("tag", F8th.get_profile_tag));
	        params.add(new BasicNameValuePair("profile-id", profileId));
	        params.add(new BasicNameValuePair("viewer-id", userid));
			
	        JSONObject json = networkOperator.sendHttpRequest(params);
	        
	        try {
	        	
	        	if(json!=null && json.getString(F8th.KEY_SUCCESS) != null){
					String result = json.getString(F8th.KEY_SUCCESS);
					if(Integer.parseInt(result) == 1){
						Log.e("UserManagerService","UserProfile success: retrieved");
						this.successMsg = "User Details retrieved";
						UserProfile userProfile = jsonParser.parseUserProfile(json);	
						
						if(userProfile != null){

							ContentValues values = new ContentValues();
							values.put(ProfilesTable.COLUMN_UID, userProfile.getU_ID());
							values.put(ProfilesTable.COLUMN_USER_ID, userProfile.getUserId());
							values.put(ProfilesTable.COLUMN_FNAME, userProfile.getFname());
							values.put(ProfilesTable.COLUMN_LNAME, userProfile.getLname());
							values.put(ProfilesTable.COLUMN_GENDER, userProfile.getGender());
							values.put(ProfilesTable.COLUMN_COUNTRY, userProfile.getCountry());
							values.put(ProfilesTable.COLUMN_FAV_VERSE, userProfile.getFavVerse());
							values.put(ProfilesTable.COLUMN_WHY, userProfile.getWhy());
							values.put(ProfilesTable.COLUMN_DESIRE, userProfile.getDesire());
							values.put(ProfilesTable.COLUMN_VIEWS, userProfile.getViews());

							Uri uri = Uri.parse(F8thContentProvider.CONTENT_URI + "/" + ProfilesTable.TABLE_USERS);
							//Uri uri1 = Uri.parse(F8thContentProvider.CONTENT_URI + "/" + LocalUsersTable.TABLE_LOCAL_USERS);
							String selection = ProfilesTable.COLUMN_USER_ID + "='" + profileId + "'";
							getContentResolver().update(uri, values, selection, null);
							//getContentResolver().update(uri1, values, selection, null);
						}
						
						return userProfile;
						
					}else if(json.getString(F8th.KEY_ERROR)!= null){
						result = json.getString(F8th.KEY_ERROR);
						if(Integer.parseInt(result) == 1 || Integer.parseInt(result) == 2 || Integer.parseInt(result) == 3){
							Log.e("UserManagerService", "error: retrieving UserProfile failed");
							this.errorMsg = json.getString(F8th.KEY_ERROR_MSG);
							userProfile = null;
						}	
					}
					return null;
	        	}else{
					Log.e("UserManagerService", "unknown error: retrieving UserProfile failed");
					this.errorMsg = "unknown error: retrieving UserProfile failed";
					return null;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("UserManagerService","UserProfile: "+e.getMessage());
				return null;
			}
		
	}//end of method downloadUserProfile

	@Override
	public Story downloadStory(String storyId) {
		// TODO Auto-generated method stub
		JSONParser jsonParser = new JSONParser();

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", F8th.get_story_tag));
		params.add(new BasicNameValuePair("story-id", storyId));
		params.add(new BasicNameValuePair("user-id", userid));

		JSONObject json = networkOperator.sendHttpRequest(params);

		try {

			if (json != null && json.getString(F8th.KEY_SUCCESS) != null) {
				String result = json.getString(F8th.KEY_SUCCESS);
				if (Integer.parseInt(result) == 1) {
					Log.e("UsermanagerService","story success: retrieved");
					this.successMsg = "Story Details retrieved";
					
					Story story = jsonParser.parseStory(json);
					
					if(story != null){

						ContentValues values = new ContentValues();
						values.put(StoriesTable.COLUMN_SID, story.getSID());
						values.put(StoriesTable.COLUMN_STORY_ID, story.getStoryId());
						values.put(StoriesTable.COLUMN_STORY, story.getStory());
						values.put(StoriesTable.COLUMN_AUTHOR_ID, story.getAuthorId());
						values.put(StoriesTable.COLUMN_AUTHOR, story.getAuthor());
						values.put(StoriesTable.COLUMN_FAVS, story.getFavorite());
						values.put(StoriesTable.COLUMN_isFAV, story.getIsFavorite());
						values.put(StoriesTable.COLUMN_isOWNER, story.getIsOwner());
						values.put(StoriesTable.COLUMN_GROUP_VISIBILITY, story.getVisibility());

						Uri uri = Uri.parse(F8thContentProvider.CONTENT_URI + "/" + StoriesTable.TABLE_STORIES);
						String selection = StoriesTable.COLUMN_STORY_ID + "='" + storyId + "'";
						getContentResolver().update(uri, values, selection, null);
					}
					
					return story;

				} else if (json.getString(F8th.KEY_ERROR) != null) {
					result = json.getString(F8th.KEY_ERROR);
					if (Integer.parseInt(result) == 1 || Integer.parseInt(result) == 2 || Integer.parseInt(result) == 3) {
						this.errorMsg = json.getString(F8th.KEY_ERROR_MSG);
						Log.e("UserManagerService","error: "+errorMsg);
					}
				}
				return null;
			} else {
				this.errorMsg = "unknown error: retrieving story failed";
				Log.e("UserManagerService",errorMsg);
				return null;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("UserManagerService","story: " + e.getMessage());
			return null;
		}
	}

	@Override
	public Group downloadGroup(String groupId) {
		// TODO Auto-generated method stub
		JSONParser jsonParser = new JSONParser();

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", F8th.get_group_tag));
		params.add(new BasicNameValuePair("grp-id", groupId));
		params.add(new BasicNameValuePair("user-id", userid));

		JSONObject json = networkOperator.sendHttpRequest(params);

		try {

			if (json != null && json.getString(F8th.KEY_SUCCESS) != null) {
				String result = json.getString(F8th.KEY_SUCCESS);
				if (Integer.parseInt(result) == 1) {
					Log.e("UsermanagerService","group success: retrieved");
					this.successMsg = "Group details retrieved";
					
					Group group = jsonParser.parseGroup(json);

					if(group != null){
						// save offline
						ContentValues values = new ContentValues();
						values.put(GroupsTable.COLUMN_GID, group.getG_ID());
						values.put(GroupsTable.COLUMN_GRP_ID, group.getGroupId());
						values.put(GroupsTable.COLUMN_GRP_OWNER_ID, group.getOwnerId());
						values.put(GroupsTable.COLUMN_GRP_OWNER, group.getOwnerName());
						values.put(GroupsTable.COLUMN_GRP_NAME, group.getName());
						values.put(GroupsTable.COLUMN_GRP_TYPE, group.getType());
						values.put(GroupsTable.COLUMN_GRP_SIZE, group.getSize());
						values.put(GroupsTable.COLUMN_GRP_CITY, group.getCity());
						values.put(GroupsTable.COLUMN_GRP_COUNTRY, group.getCountry());
						values.put(GroupsTable.COLUMN_USER_TYPE, group.getUserType());
						
						Uri uri = Uri.parse(F8thContentProvider.CONTENT_URI + "/" + GroupsTable.TABLE_GROUPS);
						String selection = GroupsTable.COLUMN_GRP_ID + "='" + groupId + "'";
						getContentResolver().update(uri, values, selection, null);
					}
					
					return group;
					
				} else if (json.getString(F8th.KEY_ERROR) != null) {
					result = json.getString(F8th.KEY_ERROR);
					if (Integer.parseInt(result) == 1 || Integer.parseInt(result) == 2 || Integer.parseInt(result) == 3) {
						this.errorMsg = json.getString(F8th.KEY_ERROR_MSG);
						Log.e("UserManagerService","error: "+errorMsg);
					}
				}
				return null;
			} else {
				this.errorMsg = "unknown error: retrieving group failed";
				Log.e("UserManagerService",errorMsg);
				return null;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("UserManagerService","group: " + e.getMessage());
			return null;
		}
	}

	
//------------------Story-------------------------------------------------------------------

	@Override
	public boolean tellStory(String story, String visibility) {
		// TODO Auto-generated method stub

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", F8th.tell_story_tag));
		params.add(new BasicNameValuePair("user-id", userid));
		params.add(new BasicNameValuePair("story", codeApostrophe(story)));
		params.add(new BasicNameValuePair("visibility", visibility));

		JSONObject json = networkOperator.sendHttpRequest(params);

		try {
			if (json != null && json.getString(F8th.KEY_SUCCESS) != null) {
				String result = json.getString(F8th.KEY_SUCCESS);
				if (Integer.parseInt(result) == 1) {

					successMsg = json.getString(F8th.KEY_SUCCESS_MSG);
					Log.e("UserManagerService", successMsg);
					return true;
				} else if (Integer.parseInt(result) == 0 && json.getString(F8th.KEY_ERROR) != null) {
					result = json.getString(F8th.KEY_ERROR);
					if (Integer.parseInt(result) == 1 || Integer.parseInt(result) == 2 || Integer.parseInt(result) == 3) {
						this.errorMsg = json.getString(F8th.KEY_ERROR_MSG);
						Log.e("UserManagerService","error: "+errorMsg);
						
						return false;
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("UserManagerService", "save story caught: "+e.getMessage());
			this.errorMsg = e.getMessage();
			return false;// e.getMessage();
		}
		Log.e("UserManagerService", "save story null");
		return false;
	}

	@Override
	public boolean updateStory(String storyId, String story, String visibility) {
		// TODO Auto-generated method stub
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", F8th.update_story_tag));
		params.add(new BasicNameValuePair("story-id", storyId));
		params.add(new BasicNameValuePair("story", codeApostrophe(story)));
		params.add(new BasicNameValuePair("visibility", visibility));

		JSONObject json = networkOperator.sendHttpRequest(params);

		try {
			if (json != null && json.getString(F8th.KEY_SUCCESS) != null) {
				String result = json.getString(F8th.KEY_SUCCESS);
				if (Integer.parseInt(result) == 1) {

					ContentValues values = new ContentValues();
					values.put(StoriesTable.COLUMN_STORY, story);

					Uri uri = Uri.parse(F8thContentProvider.CONTENT_URI + "/" + StoriesTable.TABLE_STORIES);
					String selection = StoriesTable.COLUMN_STORY_ID + "='" + storyId + "'";
					this.getContentResolver().update(uri, values, selection, null);
					
					successMsg = json.getString(F8th.KEY_SUCCESS_MSG);
					Log.e("UserManagerService", successMsg);
					return true;
				} else if (Integer.parseInt(result) == 0 && json.getString(F8th.KEY_ERROR) != null) {
					result = json.getString(F8th.KEY_ERROR);
					if (Integer.parseInt(result) == 1 || Integer.parseInt(result) == 2 || Integer.parseInt(result) == 3) {
						this.errorMsg = json.getString(F8th.KEY_ERROR_MSG);
						Log.e("UserManagerService","error: "+errorMsg);
						
						return false;
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("UserManagerService", "update story caught: "+e.getMessage());
			this.errorMsg = e.getMessage();
			return false;// e.getMessage();
		}
		Log.e("UserManagerService", "update story null");
		return false;
	}

	@Override
	public boolean deleteStory(String storyId) {
		// TODO Auto-generated method stub
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", F8th.delete_story_tag));
		params.add(new BasicNameValuePair("story-id", storyId));

		JSONObject json = networkOperator.sendHttpRequest(params);

		try {
			if (json != null && json.getString(F8th.KEY_SUCCESS) != null) {
				String result = json.getString(F8th.KEY_SUCCESS);
				if (Integer.parseInt(result) == 1) {

					Uri uri = Uri.parse(F8thContentProvider.CONTENT_URI + "/" + StoriesTable.TABLE_STORIES);
					String selection = StoriesTable.COLUMN_STORY_ID + "='" + storyId + "'";
					this.getContentResolver().delete(uri, selection, null);
					
					successMsg = json.getString(F8th.KEY_SUCCESS_MSG);
					Log.e("UserManagerService", successMsg);
					return true;
				} else if (Integer.parseInt(result) == 0 && json.getString(F8th.KEY_ERROR) != null) {
					result = json.getString(F8th.KEY_ERROR);
					if (Integer.parseInt(result) == 1 || Integer.parseInt(result) == 2 || Integer.parseInt(result) == 3) {
						this.errorMsg = json.getString(F8th.KEY_ERROR_MSG);
						Log.e("UserManagerService","error: "+errorMsg);
						
						return false;
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("UserManagerService", "delete story caught: "+e.getMessage());
			this.errorMsg = e.getMessage();
			return false;// e.getMessage();
		}
		Log.e("UserManagerService", "delete story null");
		return false;
	}

	@Override
	public boolean favoriteStory(String storyId) {
		// TODO Auto-generated method stub
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", F8th.fav_story_tag));
		params.add(new BasicNameValuePair("user-id", userid));
		params.add(new BasicNameValuePair("story-id", storyId));

		JSONObject json = networkOperator.sendHttpRequest(params);

		try {
			if (json != null && json.getString(F8th.KEY_SUCCESS) != null) {
				String result = json.getString(F8th.KEY_SUCCESS);
				if (Integer.parseInt(result) == 1) {

					ContentValues values = new ContentValues();
					values.put(StoriesTable.COLUMN_isFAV, F8th.STORY_IS_FAV);

					Uri uri = Uri.parse(F8thContentProvider.CONTENT_URI + "/" + StoriesTable.TABLE_STORIES);
					//Uri uri1 = Uri.parse(F8thContentProvider.CONTENT_URI + "/" + StoriesTable.TABLE_STORIES);
					String selection = StoriesTable.COLUMN_STORY_ID + "='" + storyId + "'";
					this.getContentResolver().update(uri, values, selection, null);
					//this.getContentResolver().insert(uri1, values);
					
					successMsg = json.getString(F8th.KEY_SUCCESS_MSG);
					Log.e("UserManagerService", successMsg);
					return true;
				} else if (Integer.parseInt(result) == 0 && json.getString(F8th.KEY_ERROR) != null) {
					result = json.getString(F8th.KEY_ERROR);
					if (Integer.parseInt(result) == 1 || Integer.parseInt(result) == 2 || Integer.parseInt(result) == 3) {
						this.errorMsg = json.getString(F8th.KEY_ERROR_MSG);
						Log.e("UserManagerService","error: "+errorMsg);
						
						return false;
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("UserManagerService", "favorite story caught: "+e.getMessage());
			this.errorMsg = e.getMessage();
			return false;// e.getMessage();
		}
		Log.e("UserManagerService", "favorite story null");
		return false;
	}

	@Override
	public boolean unFavoriteStory(String storyId) {
		// TODO Auto-generated method stub
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", F8th.unfav_story_tag));
		params.add(new BasicNameValuePair("user-id", userid));
		params.add(new BasicNameValuePair("story-id", storyId));

		JSONObject json = networkOperator.sendHttpRequest(params);

		try {
			if (json != null && json.getString(F8th.KEY_SUCCESS) != null) {
				String result = json.getString(F8th.KEY_SUCCESS);
				if (Integer.parseInt(result) == 1) {

					ContentValues values = new ContentValues();
					values.put(StoriesTable.COLUMN_isFAV, F8th.STORY_IS_NOT_FAV);

					Uri uri = Uri.parse(F8thContentProvider.CONTENT_URI + "/" + StoriesTable.TABLE_STORIES);
					//Uri uri1 = Uri.parse(F8thContentProvider.CONTENT_URI + "/" + StoriesTable.TABLE_STORIES);
					String selection = StoriesTable.COLUMN_STORY_ID + "='" + storyId + "'";
					this.getContentResolver().update(uri, values, selection, null);
					//this.getContentResolver().insert(uri1, values);
					
					successMsg = json.getString(F8th.KEY_SUCCESS_MSG);
					Log.e("UserManagerService", successMsg);
					return true;
				} else if (Integer.parseInt(result) == 0 && json.getString(F8th.KEY_ERROR) != null) {
					result = json.getString(F8th.KEY_ERROR);
					if (Integer.parseInt(result) == 1 || Integer.parseInt(result) == 2 || Integer.parseInt(result) == 3) {
						this.errorMsg = json.getString(F8th.KEY_ERROR_MSG);
						Log.e("UserManagerService","error: "+errorMsg);
						
						return false;
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("UserManagerService", "unfavorite story caught: "+e.getMessage());
			this.errorMsg = e.getMessage();
			return false;// e.getMessage();
		}
		Log.e("UserManagerService", "unfavorite story null");
		return false;
	}

	
//--------------------------------------------Group------------------------------------------------------------
	
	@Override
	public boolean createGroup(String grpName, String grpType,String grpCity,String grpCountry) {
		// TODO Auto-generated method stub
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", F8th.create_group_tag));
		params.add(new BasicNameValuePair("user-id", userid));
		params.add(new BasicNameValuePair("grp-name", codeApostrophe(grpName)));
		params.add(new BasicNameValuePair("grp-type", codeApostrophe(grpType)));
		params.add(new BasicNameValuePair("grp-city", codeApostrophe(grpCity)));
		params.add(new BasicNameValuePair("grp-country", codeApostrophe(grpCountry)));

		JSONObject json = networkOperator.sendHttpRequest(params);

		try {
			if (json != null && json.getString(F8th.KEY_SUCCESS) != null) {
				String result = json.getString(F8th.KEY_SUCCESS);
				if (Integer.parseInt(result) == 1) {

					successMsg = json.getString(F8th.KEY_SUCCESS_MSG);
					Log.e("UserManagerService", successMsg);
					return true;
				} else if (Integer.parseInt(result) == 0 && json.getString(F8th.KEY_ERROR) != null) {
					result = json.getString(F8th.KEY_ERROR);
					if (Integer.parseInt(result) == 1 || Integer.parseInt(result) == 2 || Integer.parseInt(result) == 3) {
						this.errorMsg = json.getString(F8th.KEY_ERROR_MSG);
						Log.e("UserManagerService","error: "+errorMsg);
						
						return false;
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("UserManagerService", "create group caught: "+e.getMessage());
			this.errorMsg = e.getMessage();
			return false;// e.getMessage();
		}
		Log.e("UserManagerService", "create group null");
		return false;
	}

	@Override
	public boolean editGroup(String grpId, String grpName, String grpType,String grpCity,String grpCountry) {
		// TODO Auto-generated method stub
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", F8th.edit_group_tag));
		params.add(new BasicNameValuePair("grp-id", grpId));
		params.add(new BasicNameValuePair("grp-name", codeApostrophe(grpName)));
		params.add(new BasicNameValuePair("grp-type", codeApostrophe(grpType)));
		params.add(new BasicNameValuePair("grp-city", codeApostrophe(grpCity)));
		params.add(new BasicNameValuePair("grp-country", codeApostrophe(grpCountry)));

		JSONObject json = networkOperator.sendHttpRequest(params);

		try {
			if (json != null && json.getString(F8th.KEY_SUCCESS) != null) {
				String result = json.getString(F8th.KEY_SUCCESS);
				if (Integer.parseInt(result) == 1) {

					ContentValues values = new ContentValues();
					values.put(GroupsTable.COLUMN_GRP_NAME, grpName);
					values.put(GroupsTable.COLUMN_GRP_TYPE, grpType);
					values.put(GroupsTable.COLUMN_GRP_CITY, grpCity);
					values.put(GroupsTable.COLUMN_GRP_COUNTRY, grpCountry);
					
					Uri uri = Uri.parse(F8thContentProvider.CONTENT_URI + "/" + GroupsTable.TABLE_GROUPS);
					String selection = GroupsTable.COLUMN_GRP_ID + "='" + grpId + "'";
					this.getContentResolver().update(uri, values, selection, null);
					
					successMsg = json.getString(F8th.KEY_SUCCESS_MSG);
					Log.e("UserManagerService", successMsg);
					return true;
				} else if (Integer.parseInt(result) == 0 && json.getString(F8th.KEY_ERROR) != null) {
					result = json.getString(F8th.KEY_ERROR);
					if (Integer.parseInt(result) == 1 || Integer.parseInt(result) == 2 || Integer.parseInt(result) == 3) {
						this.errorMsg = json.getString(F8th.KEY_ERROR_MSG);
						Log.e("UserManagerService","error: "+errorMsg);
						
						return false;
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("UserManagerService", "edit group caught: "+e.getMessage());
			this.errorMsg = e.getMessage();
			return false;// e.getMessage();
		}
		Log.e("UserManagerService", "edit group null");
		return false;
	}

	@Override
	public boolean deleteGroup(String grpId) {
		// TODO Auto-generated method stub
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", F8th.delete_group_tag));
		params.add(new BasicNameValuePair("grp-id", grpId));

		JSONObject json = networkOperator.sendHttpRequest(params);

		try {
			if (json != null && json.getString(F8th.KEY_SUCCESS) != null) {
				String result = json.getString(F8th.KEY_SUCCESS);
				if (Integer.parseInt(result) == 1) {

					Uri uri = Uri.parse(F8thContentProvider.CONTENT_URI + "/" + GroupsTable.TABLE_GROUPS);
					String selection = GroupsTable.COLUMN_GRP_ID + "='" + grpId + "'";
					this.getContentResolver().delete(uri, selection, null);
					
					successMsg = json.getString(F8th.KEY_SUCCESS_MSG);
					Log.e("UserManagerService", successMsg);
					return true;
				} else if (Integer.parseInt(result) == 0 && json.getString(F8th.KEY_ERROR) != null) {
					result = json.getString(F8th.KEY_ERROR);
					if (Integer.parseInt(result) == 1 || Integer.parseInt(result) == 2 || Integer.parseInt(result) == 3) {
						this.errorMsg = json.getString(F8th.KEY_ERROR_MSG);
						Log.e("UserManagerService","error: "+errorMsg);
						
						return false;
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("UserManagerService", "delete group caught: "+e.getMessage());
			this.errorMsg = e.getMessage();
			return false;// e.getMessage();
		}
		Log.e("UserManagerService", "delete group null");
		return false;
	}

	@Override
	public boolean joinGroup(String grpId, String memberType) {
		// TODO Auto-generated method stub
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", F8th.join_group_tag));
		params.add(new BasicNameValuePair("user-id", userid));
		params.add(new BasicNameValuePair("grp-id", grpId));
		params.add(new BasicNameValuePair("member-type", memberType));

		JSONObject json = networkOperator.sendHttpRequest(params);

		try {
			if (json != null && json.getString(F8th.KEY_SUCCESS) != null) {
				String result = json.getString(F8th.KEY_SUCCESS);
				if (Integer.parseInt(result) == 1) {

					ContentValues values = new ContentValues();
					values.put(GroupsTable.COLUMN_USER_TYPE, memberType);
					
					Uri uri = Uri.parse(F8thContentProvider.CONTENT_URI + "/" + GroupsTable.TABLE_GROUPS);
					//Uri uri1 = Uri.parse(F8thContentProvider.CONTENT_URI + "/" + GroupsTable.TABLE_GROUPS);
					String selection = GroupsTable.COLUMN_GRP_ID + "='" + grpId + "'";
					this.getContentResolver().update(uri, values, selection, null);
					//this.getContentResolver().update(uri1, values, selection, null);
					
					successMsg = json.getString(F8th.KEY_SUCCESS_MSG);
					Log.e("UserManagerService", successMsg);
					return true;
				} else if (Integer.parseInt(result) == 0 && json.getString(F8th.KEY_ERROR) != null) {
					result = json.getString(F8th.KEY_ERROR);
					if (Integer.parseInt(result) == 1 || Integer.parseInt(result) == 2 || Integer.parseInt(result) == 3) {
						this.errorMsg = json.getString(F8th.KEY_ERROR_MSG);
						Log.e("UserManagerService","error: "+errorMsg);
						
						return false;
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("UserManagerService", "join group caught: "+e.getMessage());
			this.errorMsg = e.getMessage();
			return false;// e.getMessage();
		}
		Log.e("UserManagerService", "join group null");
		return false;
	}

	@Override
	public boolean leaveGroup(String grpId) {
		// TODO Auto-generated method stub
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", F8th.leave_group_tag));
		params.add(new BasicNameValuePair("user-id", userid));
		params.add(new BasicNameValuePair("grp-id", grpId));

		JSONObject json = networkOperator.sendHttpRequest(params);

		try {
			if (json != null && json.getString(F8th.KEY_SUCCESS) != null) {
				String result = json.getString(F8th.KEY_SUCCESS);
				if (Integer.parseInt(result) == 1) {

					ContentValues values = new ContentValues();
					values.put(GroupsTable.COLUMN_USER_TYPE, F8th.GROUP_NON_MEMBER);
					
					Uri uri = Uri.parse(F8thContentProvider.CONTENT_URI + "/" + GroupsTable.TABLE_GROUPS);
					//Uri uri1 = Uri.parse(F8thContentProvider.CONTENT_URI + "/" + GroupsTable.TABLE_GROUPS);
					String selection = GroupsTable.COLUMN_GRP_ID + "='" + grpId + "'";
					//this.getContentResolver().delete(uri, selection, null);
					this.getContentResolver().update(uri, values, selection, null);
					
					successMsg = json.getString(F8th.KEY_SUCCESS_MSG);
					Log.e("UserManagerService", successMsg);
					return true;
				} else if (Integer.parseInt(result) == 0 && json.getString(F8th.KEY_ERROR) != null) {
					result = json.getString(F8th.KEY_ERROR);
					if (Integer.parseInt(result) == 1 || Integer.parseInt(result) == 2 || Integer.parseInt(result) == 3) {
						this.errorMsg = json.getString(F8th.KEY_ERROR_MSG);
						Log.e("UserManagerService","error: "+errorMsg);
						
						return false;
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("UserManagerService", "leave group caught: "+e.getMessage());
			this.errorMsg = e.getMessage();
			return false;// e.getMessage();
		}
		Log.e("UserManagerService", "leave group null");
		return false;
	}

//-------------------------------------Notify--------------------------------------------------------------------
	
	@Override
	public boolean deleteNotification(String notifyId) {
		// TODO Auto-generated method stub
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", F8th.delete_notify_tag));
		params.add(new BasicNameValuePair("notify-id", notifyId));

		JSONObject json = networkOperator.sendHttpRequest(params);

		try {
			if (json != null && json.getString(F8th.KEY_SUCCESS) != null) {
				String result = json.getString(F8th.KEY_SUCCESS);
				if (Integer.parseInt(result) == 1) {

					successMsg = json.getString(F8th.KEY_SUCCESS_MSG);
					Log.e("UserManagerService", successMsg);
					return true;
				} else if (Integer.parseInt(result) == 0 && json.getString(F8th.KEY_ERROR) != null) {
					result = json.getString(F8th.KEY_ERROR);
					if (Integer.parseInt(result) == 1 || Integer.parseInt(result) == 2 || Integer.parseInt(result) == 3) {
						this.errorMsg = json.getString(F8th.KEY_ERROR_MSG);
						Log.e("UserManagerService","error: "+errorMsg);
						
						return false;
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("UserManagerService", "delete notification caught: "+e.getMessage());
			this.errorMsg = e.getMessage();
			return false;// e.getMessage();
		}
		Log.e("UserManagerService", "delete notification null");
		return false;
	}

	@Override
	public boolean markAsRead(String notifyId) {
		// TODO Auto-generated method stub
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", F8th.markAsRead_tag));
		params.add(new BasicNameValuePair("notify-id", notifyId));

		JSONObject json = networkOperator.sendHttpRequest(params);

		try {
			if (json != null && json.getString(F8th.KEY_SUCCESS) != null) {
				String result = json.getString(F8th.KEY_SUCCESS);
				if (Integer.parseInt(result) == 1) {

					ContentValues values = new ContentValues();
					values.put(NotificationsTable.COLUMN_STATUS, F8th.NOTIFY_READ);
					
					Uri uri = Uri.parse(F8thContentProvider.CONTENT_URI + "/" + NotificationsTable.TABLE_NOTIFY);
					String selection = NotificationsTable.COLUMN_NOTIFY_ID + "='" + notifyId + "'";
					this.getContentResolver().update(uri, values, selection, null);
					
					successMsg = json.getString(F8th.KEY_SUCCESS_MSG);
					Log.e("UserManagerService", successMsg);
					
					deleteNotification(notifyId);
					
					return true;
				} else if (Integer.parseInt(result) == 0 && json.getString(F8th.KEY_ERROR) != null) {
					result = json.getString(F8th.KEY_ERROR);
					if (Integer.parseInt(result) == 1 || Integer.parseInt(result) == 2 || Integer.parseInt(result) == 3) {
						this.errorMsg = json.getString(F8th.KEY_ERROR_MSG);
						Log.e("UserManagerService","error: "+errorMsg);
						
						return false;
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("UserManagerService", "mark notification caught: "+e.getMessage());
			this.errorMsg = e.getMessage();
			return false;// e.getMessage();
		}
		Log.e("UserManagerService", "mark notification null");
		return false;
	}

	
//-------------------------------------------------------Lists--------------------------------------------------------
	
	@Override
	public List<User> profileList(String type,String itemId) {
		// TODO Auto-generated method stub
		JSONParser jsonParser = new JSONParser();

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", F8th.profile_list_tag));
		params.add(new BasicNameValuePair("user-id", userid));
		params.add(new BasicNameValuePair("type", type));
		params.add(new BasicNameValuePair("item-id", itemId));

		JSONObject json = networkOperator.sendHttpRequest(params);

		try {

			if (json != null && json.getString(F8th.KEY_SUCCESS) != null) {
				String result = json.getString(F8th.KEY_SUCCESS);
				if (Integer.parseInt(result) == 1) {
					Log.e("UserManagerService","List UserProfile success: retrieved");
					this.successMsg = "user profiles retrieved";
					List<User>profileList = jsonParser.parseUserList(json);
					
					if(profileList != null){
						if(type.equalsIgnoreCase(F8th.LIST_REFRESH)){
							Uri uri = Uri.parse(F8thContentProvider.CONTENT_URI + "/" + ProfilesTable.TABLE_USERS);
							//String selection = "*";
							UserManagerService.this.getContentResolver().delete(uri, null, null);
						}
						
						int size = profileList.size();
						for(int i=0;i<size;i++){
							// save offline
							User user = profileList.get(i);
							ContentValues values = new ContentValues();
							values.put(ProfilesTable.COLUMN_UID, user.getU_ID());
							values.put(ProfilesTable.COLUMN_USER_ID, user.getUserId());
							values.put(ProfilesTable.COLUMN_FNAME, user.getFname());
							values.put(ProfilesTable.COLUMN_LNAME, user.getLname());
							values.put(ProfilesTable.COLUMN_GENDER, user.getGender());
							values.put(ProfilesTable.COLUMN_COUNTRY, user.getCountry());
	
							Uri uri = Uri.parse(F8thContentProvider.CONTENT_URI + "/" + ProfilesTable.TABLE_USERS);
							UserManagerService.this.getContentResolver().insert(uri, values);
						}
					}
					
					return profileList;

				} else if (json.getString(F8th.KEY_ERROR) != null) {
					result = json.getString(F8th.KEY_ERROR);
					if (Integer.parseInt(result) == 1
							|| Integer.parseInt(result) == 2
							|| Integer.parseInt(result) == 3) {
						this.errorMsg = json.getString(F8th.KEY_ERROR_MSG);
						Log.e("UserManagerService", "userList error: "
								+ errorMsg);
					}
				}
				return null;
			} else {
				this.errorMsg = "unknown error: retrieving List UserProfile failed";
				Log.e("UserManagerService", "userList error: " + errorMsg);
				return null;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("UserManagerService", "List UserProfile: " + e.getMessage());
			return null;
		}
	}

	@Override
	public List<Story> storyList(String type,String itemId) {
		// TODO Auto-generated method stub
		JSONParser jsonParser = new JSONParser();

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", F8th.story_list_tag));
		params.add(new BasicNameValuePair("user-id", userid));
		params.add(new BasicNameValuePair("type", type));
		params.add(new BasicNameValuePair("item-id", itemId));

		JSONObject json = networkOperator.sendHttpRequest(params);

		try {

			if (json != null && json.getString(F8th.KEY_SUCCESS) != null) {
				String result = json.getString(F8th.KEY_SUCCESS);
				if (Integer.parseInt(result) == 1) {
					Log.e("UserManagerService","storyList success: retrieved");
					this.successMsg = "stories retrieved";
					List<Story> storyList = jsonParser.parseStoryList(json);
					
					if(storyList != null){

						if(type.equalsIgnoreCase(F8th.LIST_REFRESH)){
							Uri uri = Uri.parse(F8thContentProvider.CONTENT_URI + "/" + StoriesTable.TABLE_STORIES);
							String selection = StoriesTable.COLUMN_favID + "='0'";
							getContentResolver().delete(uri, selection, null);
						}
						
						int size = storyList.size();
						for(int i=0;i<size;i++){
							// save offline
							Story story = storyList.get(i);
							ContentValues values = new ContentValues();
							values.put(StoriesTable.COLUMN_favID, "0");
							values.put(StoriesTable.COLUMN_SID, story.getSID());
							values.put(StoriesTable.COLUMN_STORY_ID, story.getStoryId());
							values.put(StoriesTable.COLUMN_STORY, story.getStory());
							values.put(StoriesTable.COLUMN_AUTHOR_ID, story.getAuthorId());
							values.put(StoriesTable.COLUMN_AUTHOR, story.getAuthor());
							values.put(StoriesTable.COLUMN_FAVS, story.getFavorite());
							values.put(StoriesTable.COLUMN_isFAV, story.getIsFavorite());
							values.put(StoriesTable.COLUMN_isOWNER, story.getIsOwner());
							values.put(StoriesTable.COLUMN_GROUP_VISIBILITY, story.getVisibility());

							Uri uri = Uri.parse(F8thContentProvider.CONTENT_URI + "/" + StoriesTable.TABLE_STORIES);
							getContentResolver().insert(uri, values);
						}
					}
					
					return storyList;

				} else if (json.getString(F8th.KEY_ERROR) != null) {
					result = json.getString(F8th.KEY_ERROR);
					if (Integer.parseInt(result) == 1 || Integer.parseInt(result) == 2 || Integer.parseInt(result) == 3) {
						this.errorMsg = json.getString(F8th.KEY_ERROR_MSG);
						Log.e("UserManagerService", "storyList error: "+ errorMsg);
					}
				}
				return null;
			} else {
				this.errorMsg = "unknown error: retrieving storyList failed";
				Log.e("UserManagerService", "storyList error: " + errorMsg);
				return null;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("UserManagerService", "storyList: " + e.getMessage());
			return null;
		}
	}

	@Override
	public List<Group> groupList(String type,String itemId) {
		// TODO Auto-generated method stub
		JSONParser jsonParser = new JSONParser();

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", F8th.group_list_tag));
		params.add(new BasicNameValuePair("user-id", userid));
		params.add(new BasicNameValuePair("type", type));
		params.add(new BasicNameValuePair("item-id", itemId));

		JSONObject json = networkOperator.sendHttpRequest(params);

		try {

			if (json != null && json.getString(F8th.KEY_SUCCESS) != null) {
				String result = json.getString(F8th.KEY_SUCCESS);
				if (Integer.parseInt(result) == 1) {
					Log.e("UserManagerService","groupList success: retrieved");
					this.successMsg = "groups retrieved";
					List<Group> groupList = jsonParser.parseGroupList(json);
					
					if(groupList != null){

						if(type.equalsIgnoreCase(F8th.LIST_REFRESH)){
							Uri uri = Uri.parse(F8thContentProvider.CONTENT_URI + "/" + GroupsTable.TABLE_GROUPS);
							String selection = GroupsTable.COLUMN_MID + "='0'";
							getContentResolver().delete(uri, selection, null);
						}
						
						int size = groupList.size();
						for(int i=0;i<size;i++){
							// save offline
							Group group = groupList.get(i);
							ContentValues values = new ContentValues();
							values.put(GroupsTable.COLUMN_MID, "0");
							values.put(GroupsTable.COLUMN_GID, group.getG_ID());
							values.put(GroupsTable.COLUMN_GRP_ID, group.getGroupId());
							values.put(GroupsTable.COLUMN_GRP_OWNER_ID, group.getOwnerId());
							values.put(GroupsTable.COLUMN_GRP_OWNER, group.getOwnerName());
							values.put(GroupsTable.COLUMN_GRP_NAME, group.getName());
							values.put(GroupsTable.COLUMN_GRP_TYPE, group.getType());
							values.put(GroupsTable.COLUMN_GRP_SIZE, group.getSize());
							values.put(GroupsTable.COLUMN_GRP_CITY, group.getCity());
							values.put(GroupsTable.COLUMN_GRP_COUNTRY, group.getCountry());
							values.put(GroupsTable.COLUMN_USER_TYPE, group.getUserType());

							Uri uri = Uri.parse(F8thContentProvider.CONTENT_URI + "/" + GroupsTable.TABLE_GROUPS);
							getContentResolver().insert(uri, values);
						}
					}
					
					return groupList;

				} else if (json.getString(F8th.KEY_ERROR) != null) {
					result = json.getString(F8th.KEY_ERROR);
					if (Integer.parseInt(result) == 1 || Integer.parseInt(result) == 2 || Integer.parseInt(result) == 3) {
						this.errorMsg = json.getString(F8th.KEY_ERROR_MSG);
						Log.e("UserManagerService", "groupList error: "+ errorMsg);
					}
				}
				return null;
			} else {
				this.errorMsg = "unknown error: retrieving groupList failed";
				Log.e("UserManagerService", "groupList error: " + errorMsg);
				return null;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("UserManagerService", "groupList: " + e.getMessage());
			return null;
		}
	}

	@Override
	public List<Notification> notifyList(String nId) {
		// TODO Auto-generated method stub
		JSONParser jsonParser = new JSONParser();

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", F8th.notify_list_tag));
		params.add(new BasicNameValuePair("user-id", userid));
		params.add(new BasicNameValuePair("n-id", nId));

		JSONObject json = networkOperator.sendHttpRequest(params);

		try {

			if (json != null && json.getString(F8th.KEY_SUCCESS) != null) {
				String result = json.getString(F8th.KEY_SUCCESS);
				if (Integer.parseInt(result) == 1) {
					Log.e("UserManagerService","notifyList success: retrieved");
					this.successMsg = "notifications retrieved";
					
					List<Notification> notifyList = jsonParser.parseNotificationList(json);
					
					if(notifyList != null){
						if(nId.equalsIgnoreCase("")){
							Uri uri = Uri.parse(F8thContentProvider.CONTENT_URI + "/" + NotificationsTable.TABLE_NOTIFY);
							getContentResolver().delete(uri, null, null);
						}
						
						int size = notifyList.size();
						for(int i=0;i<size;i++){
							// save offline
							Notification notify = notifyList.get(i);
							ContentValues values = new ContentValues();
							values.put(NotificationsTable.COLUMN_NID, notify.getNID());
							values.put(NotificationsTable.COLUMN_NOTIFY_ID, notify.getNotifyId());
							values.put(NotificationsTable.COLUMN_MESSAGE, notify.getMessage());
							values.put(NotificationsTable.COLUMN_SENDER_ID, notify.getSenderId());
							values.put(NotificationsTable.COLUMN_SENDER,  notify.getSender());
							values.put(NotificationsTable.COLUMN_SENT_AT, notify.getDateSent());
							values.put(NotificationsTable.COLUMN_STATUS, notify.getStatus());

							Uri uri = Uri.parse(F8thContentProvider.CONTENT_URI + "/" + NotificationsTable.TABLE_NOTIFY);
							getContentResolver().insert(uri, values);
						}
					}
					
					return notifyList;

				} else if (json.getString(F8th.KEY_ERROR) != null) {
					result = json.getString(F8th.KEY_ERROR);
					if (Integer.parseInt(result) == 1 || Integer.parseInt(result) == 2 || Integer.parseInt(result) == 3) {
						this.errorMsg = json.getString(F8th.KEY_ERROR_MSG);
						Log.e("UserManagerService", "notifyList error: "+ errorMsg);
					}
				}
				return null;
			} else {
				this.errorMsg = "unknown error: retrieving notifyList failed";
				Log.e("UserManagerService", "notifyList error: " + errorMsg);
				return null;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("UserManagerService", "notifyList: " + e.getMessage());
			return null;
		}
	}
	
	@Override
	public List<Group> joinedGroupList(String mId) {
		// TODO Auto-generated method stub
		JSONParser jsonParser = new JSONParser();

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", F8th.joinedGroup_list_tag));
		params.add(new BasicNameValuePair("user-id", userid));
		params.add(new BasicNameValuePair("m-id", mId));

		JSONObject json = networkOperator.sendHttpRequest(params);

		try {

			if (json != null && json.getString(F8th.KEY_SUCCESS) != null) {
				String result = json.getString(F8th.KEY_SUCCESS);
				if (Integer.parseInt(result) == 1) {
					Log.e("UserManagerService","joined groupList success: retrieved");
					this.successMsg = "joined group retrieved";
					
					List<Group> groupList = jsonParser.parseJoinedGroupList(json);
					
					if(groupList != null){

						if(mId.equalsIgnoreCase("")){
							Uri uri = Uri.parse(F8thContentProvider.CONTENT_URI + "/" + GroupsTable.TABLE_GROUPS);
							String selection = GroupsTable.COLUMN_MID + ">'0'";
							getContentResolver().delete(uri, selection, null);
						}
						
						int size = groupList.size();
						for(int i=0;i<size;i++){
							// save offline
							Group group = groupList.get(i);
							ContentValues values = new ContentValues();
							values.put(GroupsTable.COLUMN_MID, group.getM_ID());
							values.put(GroupsTable.COLUMN_GID, group.getG_ID());
							values.put(GroupsTable.COLUMN_GRP_ID, group.getGroupId());
							values.put(GroupsTable.COLUMN_GRP_OWNER_ID, group.getOwnerId());
							values.put(GroupsTable.COLUMN_GRP_OWNER, group.getOwnerName());
							values.put(GroupsTable.COLUMN_GRP_NAME, group.getName());
							values.put(GroupsTable.COLUMN_GRP_TYPE, group.getType());
							values.put(GroupsTable.COLUMN_GRP_SIZE, group.getSize());
							values.put(GroupsTable.COLUMN_GRP_CITY, group.getCity());
							values.put(GroupsTable.COLUMN_GRP_COUNTRY, group.getCountry());
							values.put(GroupsTable.COLUMN_USER_TYPE, group.getUserType());

							Uri uri = Uri.parse(F8thContentProvider.CONTENT_URI + "/" + GroupsTable.TABLE_GROUPS);
							getContentResolver().insert(uri, values);
						}
					}
					
					return groupList;

				} else if (json.getString(F8th.KEY_ERROR) != null) {
					result = json.getString(F8th.KEY_ERROR);
					if (Integer.parseInt(result) == 1 || Integer.parseInt(result) == 2 || Integer.parseInt(result) == 3) {
						this.errorMsg = json.getString(F8th.KEY_ERROR_MSG);
						Log.e("UserManagerService", "joined groupList error: "+ errorMsg);
					}
				}
				return null;
			} else {
				this.errorMsg = "unknown error: retrieving joined groupList failed";
				Log.e("UserManagerService", errorMsg);
				return null;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("UserManagerService", "joined groupList: " + e.getMessage());
			return null;
		}
	}
	
	@Override
	public List<Story> favStoryList(String favId) {
		// TODO Auto-generated method stub
		JSONParser jsonParser = new JSONParser();

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", F8th.favStory_list_tag));
		params.add(new BasicNameValuePair("user-id", userid));
		params.add(new BasicNameValuePair("fav-id", favId));

		JSONObject json = networkOperator.sendHttpRequest(params);

		try {

			if (json != null && json.getString(F8th.KEY_SUCCESS) != null) {
				String result = json.getString(F8th.KEY_SUCCESS);
				if (Integer.parseInt(result) == 1) {
					Log.e("UserManagerService","fav storyList success: retrieved");
					this.successMsg = "favorite stories retrieved";
					
					List<Story> storyList = jsonParser.parseFavStoryList(json);

					if(storyList != null){

						if(favId.equalsIgnoreCase("")){
							Uri uri = Uri.parse(F8thContentProvider.CONTENT_URI + "/" + StoriesTable.TABLE_STORIES);
							String selection = StoriesTable.COLUMN_favID + ">'0'";
							getContentResolver().delete(uri, selection, null);
						}
						
						int size = storyList.size();
						for(int i=0;i<size;i++){
							// save offline
							Story story = storyList.get(i);
							ContentValues values = new ContentValues();
							values.put(StoriesTable.COLUMN_SID, story.getSID());
							values.put(StoriesTable.COLUMN_favID, story.getFavId());
							values.put(StoriesTable.COLUMN_STORY_ID, story.getStoryId());
							values.put(StoriesTable.COLUMN_STORY, story.getStory());
							values.put(StoriesTable.COLUMN_AUTHOR_ID, story.getAuthorId());
							values.put(StoriesTable.COLUMN_AUTHOR, story.getAuthor());
							values.put(StoriesTable.COLUMN_FAVS, story.getFavorite());
							values.put(StoriesTable.COLUMN_isFAV, story.getIsFavorite());
							values.put(StoriesTable.COLUMN_isOWNER, story.getIsOwner());
							values.put(StoriesTable.COLUMN_GROUP_VISIBILITY, story.getVisibility());

							Uri uri = Uri.parse(F8thContentProvider.CONTENT_URI + "/" + StoriesTable.TABLE_STORIES);
							getContentResolver().insert(uri, values);
						}
					}
					
					return storyList;
					
				} else if (json.getString(F8th.KEY_ERROR) != null) {
					result = json.getString(F8th.KEY_ERROR);
					if (Integer.parseInt(result) == 1 || Integer.parseInt(result) == 2 || Integer.parseInt(result) == 3) {
						this.errorMsg = json.getString(F8th.KEY_ERROR_MSG);
						Log.e("UserManagerService", "fav storyList error: "+ errorMsg);
					}
				}
				return null;
			} else {
				this.errorMsg = "unknown error: retrieving storyList failed";
				Log.e("UserManagerService", "fav storyList error: " + errorMsg);
				return null;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("UserManagerService", "fav storyList: " + e.getMessage());
			return null;
		}
	}
	
//------------------------------------------------------Photos----------------------------------------------
	
	@Override
	public boolean uploadProfilePhoto(String profileId,byte[] photo) {
		// TODO Auto-generated method stub
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", F8th.upload_profile_photo_tag));
		params.add(new BasicNameValuePair("grp-id", profileId));
		//params.add(new BasicNameValuePair("photo", photo));

		JSONObject json = networkOperator.sendHttpRequest(params);

		try {
			if (json != null && json.getString(F8th.KEY_SUCCESS) != null) {
				String result = json.getString(F8th.KEY_SUCCESS);
				if (Integer.parseInt(result) == 1) {

					successMsg = json.getString(F8th.KEY_SUCCESS_MSG);
					Log.e("UserManagerService", successMsg);
					return true;
				} else if (Integer.parseInt(result) == 0 && json.getString(F8th.KEY_ERROR) != null) {
					result = json.getString(F8th.KEY_ERROR);
					if (Integer.parseInt(result) == 1 || Integer.parseInt(result) == 2 || Integer.parseInt(result) == 3) {
						this.errorMsg = json.getString(F8th.KEY_ERROR_MSG);
						Log.e("UserManagerService","error: "+errorMsg);
						
						return false;
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("UserManagerService", "upload profile photo caught: "+e.getMessage());
			this.errorMsg = e.getMessage();
			return false;// e.getMessage();
		}
		Log.e("UserManagerService", "upload profile photo null");
		return false;
	}

	@Override
	public byte[] downloadProfilePhoto(String profileId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean uploadGroupPhoto(String grpId,byte[] photo) {
		// TODO Auto-generated method stub
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", F8th.upload_group_photo_tag));
		params.add(new BasicNameValuePair("grp-id", grpId));
		//params.add(new BasicNameValuePair("photo", photo));

		JSONObject json = networkOperator.sendHttpRequest(params);

		try {
			if (json != null && json.getString(F8th.KEY_SUCCESS) != null) {
				String result = json.getString(F8th.KEY_SUCCESS);
				if (Integer.parseInt(result) == 1) {

					successMsg = json.getString(F8th.KEY_SUCCESS_MSG);
					Log.e("UserManagerService", successMsg);
					return true;
				} else if (Integer.parseInt(result) == 0 && json.getString(F8th.KEY_ERROR) != null) {
					result = json.getString(F8th.KEY_ERROR);
					if (Integer.parseInt(result) == 1 || Integer.parseInt(result) == 2 || Integer.parseInt(result) == 3) {
						this.errorMsg = json.getString(F8th.KEY_ERROR_MSG);
						Log.e("UserManagerService","error: "+errorMsg);
						
						return false;
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("UserManagerService", "upload group photo caught: "+e.getMessage());
			this.errorMsg = e.getMessage();
			return false;// e.getMessage();
		}
		Log.e("UserManagerService", "upload group photo null");
		return false;
	}

	@Override
	public byte[] downloadGroupPhoto(String groupId) {
		// TODO Auto-generated method stub
		return null;
	}
	

//---------------------------------------------------------------------------------------------------
	
	@Override
	public boolean loadUser(){
		
		userProfile = downloadUserProfile(userid);
		if(userProfile != null){
			if(saveLocalUser(userProfile)){
				return true;
			}
		}else{
			//load local user
			userProfile = getLocalUserProfile();
			if(userProfile != null){
				return true;
			}
		}
		
		return false;
	}//end of function
	
	@Override
	public void startTimerTask(){
		if(!mTimer){
			mTimer = true;
			Log.i("timer", "timer started");
			timer.schedule(new TimerTask(){
				
				//check Internet connection after 10s
				@Override
				public void run() {
					// TODO Auto-generated method stub
					
					if(!isNetworkConnected()){
						//send broadcast for lost Internet connection
						LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(F8th.INTERNET_NOT_AVAILABLE));
						//Log.i("broadcast", "no internet broadcast sent");
					}else{
						//send broadcast for lost Internet connection
						LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(F8th.INTERNET_AVAILABLE));
						//Log.i("broadcast", "internet broadcast sent");
					}
					
					/*List<Notification> notices = UserManagerService.this.notifyList("");
					
					if(notices != null){
						int newsItems = notices.size();
						Intent intent = new Intent(UserManagerService.this, LoginActivity.class);
						PendingIntent pIntent = PendingIntent.getActivity(UserManagerService.this, 0, intent, 0);
						
						Notification n  = new Notification.Builder(this)
				        .setContentTitle(newsItems+" new stories")
				        .setContentText("F8th")
				        .setSmallIcon(R.drawable.ic_launcher)
				        .setContentIntent(pIntent)
				        .setAutoCancel(true).build();
					}*/
				}
				
			}, 1000, F8th.TIMER_INTERVAL);
		}
	}//end of method startTimerThread()
	
	@Override
	public void stopTimerTask(){
		if(mTimer){
			timer.cancel();
			mTimer = false;
			Log.i("timer", "timer stopped");
		}
	}//end of method stopTimerThread()

	@Override
	public boolean updateAutoLogin(boolean state) {
		String set;
		if (state) {
			set = F8th.AUTO_LOGIN_SET;
		} else {
			set = F8th.AUTO_LOGIN_UNSET;
		}

		// uncheck all
		unsetOtherAutoLogins();

		// update offline
		ContentValues values = new ContentValues();
		values.put(LocalUsersTable.COLUMN_AUTOLOGIN, set);

		Uri uri = Uri.parse(F8thContentProvider.CONTENT_URI + "/" + LocalUsersTable.TABLE_LOCAL_USERS);
		String selection = LocalUsersTable.COLUMN_USER_ID + "='" + userid + "'";
		this.getContentResolver().update(uri, values, selection, null);

		//localUserProfile = getLocalUserProfile();
		return true;
	}

	@Override
	public boolean deleteLocalUser() {
		Uri uri = Uri.parse(F8thContentProvider.CONTENT_URI + "/" + LocalUsersTable.TABLE_LOCAL_USERS);
		String selection = LocalUsersTable.COLUMN_USER_ID + "='" + userid + "'";
		getContentResolver().delete(uri, selection, null);
		//this.localUserProfile = getLocalUserProfile();
		return true;
	}// end of function

	//@Override
	public boolean saveLocalUser(UserProfile onlineProfile) {
		if (onlineProfile != null) {
			// check if userProfile exists
			String[] projection = { LocalUsersTable.COLUMN_ID,LocalUsersTable.COLUMN_USER_ID };

			Uri uri = Uri.parse(F8thContentProvider.CONTENT_URI + "/" + LocalUsersTable.TABLE_LOCAL_USERS);
			String selection = LocalUsersTable.COLUMN_USER_ID + "='" + userid + "'";
			Cursor cursor = getContentResolver().query(uri, projection, selection,null, null);

			if (cursor.moveToFirst() && cursor != null) {
				cursor.close();
				
				//update offline
				ContentValues values = new ContentValues();
				values.put(LocalUsersTable.COLUMN_FNAME, onlineProfile.getFname());
				values.put(LocalUsersTable.COLUMN_LNAME, onlineProfile.getLname());
				values.put(LocalUsersTable.COLUMN_GENDER, onlineProfile.getGender());
				values.put(LocalUsersTable.COLUMN_COUNTRY, onlineProfile.getCountry());
				values.put(LocalUsersTable.COLUMN_FAV_VERSE, onlineProfile.getFavVerse());
				values.put(LocalUsersTable.COLUMN_WHY, onlineProfile.getWhy());
				values.put(LocalUsersTable.COLUMN_DESIRE, onlineProfile.getDesire());
				values.put(LocalUsersTable.COLUMN_VIEWS, onlineProfile.getViews());
				
				this.getContentResolver().update(uri, values,selection,null);
				
				return true;
			} else {
				cursor.close();
				// save offline
				ContentValues values = new ContentValues();
				values.put(LocalUsersTable.COLUMN_AUTOLOGIN, F8th.AUTO_LOGIN_UNSET);
				values.put(LocalUsersTable.COLUMN_USER_ID, userid);
				values.put(LocalUsersTable.COLUMN_EMAIL, email);
				values.put(LocalUsersTable.COLUMN_PASSWORD, password);
				values.put(LocalUsersTable.COLUMN_FNAME, onlineProfile.getFname());
				values.put(LocalUsersTable.COLUMN_LNAME, onlineProfile.getLname());
				values.put(LocalUsersTable.COLUMN_GENDER, onlineProfile.getGender());
				values.put(LocalUsersTable.COLUMN_COUNTRY, onlineProfile.getCountry());
				values.put(LocalUsersTable.COLUMN_FAV_VERSE, onlineProfile.getFavVerse());
				values.put(LocalUsersTable.COLUMN_WHY, onlineProfile.getWhy());
				values.put(LocalUsersTable.COLUMN_DESIRE, onlineProfile.getDesire());
				values.put(LocalUsersTable.COLUMN_VIEWS, onlineProfile.getViews());

				this.getContentResolver().insert(uri, values);

				return true;
			}
		} else {
			return false;
		}
	}// end of method

//==================================================================================================
	public void unsetOtherAutoLogins() {

		if (!otherUsersAutoLoginUnset) {

			String[] projection = { LocalUsersTable.COLUMN_ID,LocalUsersTable.COLUMN_USER_ID };

			Uri url = Uri.parse(F8thContentProvider.CONTENT_URI + "/" + LocalUsersTable.TABLE_LOCAL_USERS);
			Cursor cursor = getContentResolver().query(url, projection, null, null,null);
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				String user_id = cursor.getString(cursor.getColumnIndex(LocalUsersTable.COLUMN_USER_ID));
				ContentValues values = new ContentValues();
				values.put(LocalUsersTable.COLUMN_AUTOLOGIN, F8th.AUTO_LOGIN_UNSET);

				String selection = LocalUsersTable.COLUMN_USER_ID + "='" + user_id + "'";
				Uri uri = Uri.parse(F8thContentProvider.CONTENT_URI + "/"+ LocalUsersTable.TABLE_LOCAL_USERS);
				this.getContentResolver().update(uri, values, selection, null);

				cursor.moveToNext();
				// return false;
			}
			cursor.close();
			otherUsersAutoLoginUnset = true;
		}
	}// end of method

	public String codeApostrophe(String text) {
		// replaces all apostrophes in the text with special characters
		String codedText;
		codedText = text.replace("'", "&%");
		codedText = codedText.replace(";", "&#");
		codedText = codedText.replace("*", "&@");
		return codedText.replace('"', '|');
	}

	private UserProfile getLocalUserProfile() {

		String[] projection = { LocalUsersTable.COLUMN_ID, LocalUsersTable.COLUMN_USER_ID,
				LocalUsersTable.COLUMN_AUTOLOGIN, LocalUsersTable.COLUMN_UID,LocalUsersTable.COLUMN_FNAME,
				LocalUsersTable.COLUMN_LNAME, LocalUsersTable.COLUMN_GENDER,
				LocalUsersTable.COLUMN_COUNTRY, LocalUsersTable.COLUMN_FAV_VERSE,
				LocalUsersTable.COLUMN_WHY, LocalUsersTable.COLUMN_DESIRE,LocalUsersTable.COLUMN_VIEWS };

		Uri uri = Uri.parse(F8thContentProvider.CONTENT_URI + "/" + LocalUsersTable.TABLE_LOCAL_USERS);
		String selection = LocalUsersTable.COLUMN_USER_ID + "='" + userid + "'";
		Cursor cursor = getContentResolver().query(uri, projection, selection, null,null);

		if (cursor.moveToFirst() && cursor != null) {


			//autoLogin = cursor.getString(cursor.getColumnIndexOrThrow(LocalUsersTable.COLUMN_AUTOLOGIN));
			String uid = cursor.getString(cursor.getColumnIndexOrThrow(LocalUsersTable.COLUMN_UID));
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
			cursor.close();
			return new UserProfile(uid, userId, fname, lname, gender, country,about, why, desire, views, location);
		} else {
			cursor.close();
			return null;
		}
	}

}// END OF CLASS UserManagerService
