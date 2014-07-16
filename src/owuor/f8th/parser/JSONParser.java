package owuor.f8th.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import owuor.f8th.types.F8th;
import owuor.f8th.types.Group;
import owuor.f8th.types.Notification;
import owuor.f8th.types.Story;
import owuor.f8th.types.User;
import owuor.f8th.types.UserProfile;

public class JSONParser {
	

	public JSONParser() {
		
	}// end of constructor

	
	public UserProfile parseUserProfile(JSONObject json) throws JSONException {
		UserProfile userProfile;

		if (json.getString(F8th.KEY_SUCCESS) != null) {
			// if successful
			try {
				// Getting User Profile
				JSONObject s = json.getJSONObject(F8th.TAG_USER_PROFILE);

				// Storing each json item in variable
				String uid = s.getString(F8th.TAG_UID);
				String userId = s.getString(F8th.TAG_USER_ID);
				//String email = "";//s.getString(F8th.TAG_EMAIL);
				String fname = decodeApostrophe(s.getString(F8th.TAG_FNAME));
				String lname = decodeApostrophe(s.getString(F8th.TAG_LNAME));
				String gender = s.getString(F8th.TAG_GENDER);
				String country = decodeApostrophe(s.getString(F8th.TAG_COUNTRY));
				String about = decodeApostrophe(s.getString(F8th.TAG_FAV_VERSE));
				String why = decodeApostrophe(s.getString(F8th.TAG_WHY));
				String desire = decodeApostrophe(s.getString(F8th.TAG_DESIRE));
				String views = s.getString(F8th.TAG_VIEWS);
				

				Log.i("jsonparser", "raw userProfile fetched");
				userProfile = new UserProfile(uid,userId, fname, lname, gender, country,  about, why, desire,views);
				return userProfile;

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("jsonparser", "error fetching userProfile");
				return null;
			}

		} else {
			Log.e("jsonparser", "null error fetching userProfile");
			return null;
		}

	}// end of method parseUserProfile()
	
	public Story parseStory(JSONObject json) throws JSONException {
		Story story;
		
		if (json.getString(F8th.KEY_SUCCESS) != null) {
			// if successful
			try {
				// Getting User Profile
				JSONObject s = json.getJSONObject(F8th.TAG_STORY_DETAILS);
				
				// Storing each json item in variable
				String sid = s.getString(F8th.TAG_SID);
				String storyId = s.getString(F8th.TAG_STORY_ID);
				String strStory = decodeApostrophe(s.getString(F8th.TAG_STORY));
				String authorId = s.getString(F8th.TAG_STORY_AUTHOR_ID);
				String author = decodeApostrophe(s.getString(F8th.TAG_STORY_AUTHOR));
				String favs = s.getString(F8th.TAG_STORY_FAVS);
				String isFav = s.getString(F8th.TAG_STORY_IS_FAV);
				String isOwner = s.getString(F8th.TAG_STORY_IS_OWNER);
				String grpVisible = s.getString(F8th.TAG_STORY_VISIBILITY);
				
				Log.i("jsonparser", "raw story fetched");
				story = new Story("",sid,storyId,strStory,authorId, author, favs, isFav, isOwner,grpVisible);
				return story;

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("jsonparser", "error fetching story");
				return null;
			}

		} else {
			Log.e("jsonparser", "null error fetching story");
			return null;
		}
	}//end of parseStory
	
	public Group parseGroup(JSONObject json) throws JSONException {
		Group group;
		
		if (json.getString(F8th.KEY_SUCCESS) != null) {
			// if successful
			try {
				// Getting User Profile
				JSONObject s = json.getJSONObject(F8th.TAG_GRP_DETAILS);
		
				String gid = s.getString(F8th.TAG_GID);
				String grpId = s.getString(F8th.TAG_GRP_ID);
				String grpOwnerId = s.getString(F8th.TAG_GRP_OWNER_ID);
				String grpOwnerName = decodeApostrophe(s.getString(F8th.TAG_GRP_OWNER));
				String grpName = decodeApostrophe(s.getString(F8th.TAG_GRP_NAME));
				String grpType = decodeApostrophe(s.getString(F8th.TAG_GRP_TYPE));
				String grpSize = s.getString(F8th.TAG_GRP_SIZE);
				String grpCity = decodeApostrophe(s.getString(F8th.TAG_GRP_CITY));
				String grpCountry = decodeApostrophe(s.getString(F8th.TAG_GRP_COUNTRY));
				String userType = s.getString(F8th.TAG_GRP_USER_TYPE);
				

				group = new Group("",gid,grpId,grpOwnerId,grpOwnerName,grpName, grpType, grpSize,grpCity,grpCountry,userType);
				
				return group;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("jsonparser", "error fetching group");
				return null;
			}

		} else {
			Log.e("jsonparser", "null error fetching group");
			return null;
		}
	}//end of parseGroup
	
//===========================================================================================================
	
	// method to get list
	public List<User> parseUserList(JSONObject json) throws JSONException {
		
		if (json.getString(F8th.KEY_SUCCESS) != null) {
			// if successful
			JSONArray users = new JSONArray();
			List<HashMap<String, String>> rawList = new ArrayList<HashMap<String, String>>();

			try {
				// Getting Array of users
				users = json.getJSONArray(F8th.TAG_USERS);

				// looping through All users
				for (int i = 0; i < users.length(); i++) {
					JSONObject s = users.getJSONObject(i);

					// Storing each json item in variable
					String uid = s.getString(F8th.TAG_UID);
					String userId = s.getString(F8th.TAG_USER_ID);
					//String email = "";//s.getString(F8th.TAG_EMAIL);
					String fname = decodeApostrophe(s.getString(F8th.TAG_FNAME));
					String lname = decodeApostrophe(s.getString(F8th.TAG_LNAME));
					String gender = s.getString(F8th.TAG_GENDER);
					String country = decodeApostrophe(s.getString(F8th.TAG_COUNTRY));
					

					// creating new HashMap
					HashMap<String, String> map = new HashMap<String, String>();

					// adding each child node to HashMap key => value
					map.put(F8th.TAG_UID, uid);
					map.put(F8th.TAG_USER_ID, userId);
					//map.put(F8th.TAG_EMAIL, email);
					map.put(F8th.TAG_FNAME, fname);
					map.put(F8th.TAG_LNAME, lname);
					map.put(F8th.TAG_GENDER, gender);
					map.put(F8th.TAG_COUNTRY, country);

					// adding HashList to ArrayList
					rawList.add(map);
				}// end of for-loop
				Log.i("jsonparser", "raw list fetched");

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("jsonparser", "error fetching list");
			}

			return getUserList(rawList);

		} else {
			Log.e("jsonparser", "null error fetching list");
			return null;
		}
	}// end of method parseList()


	public List<Story> parseStoryList(JSONObject json) throws JSONException {
		
		if (json.getString(F8th.KEY_SUCCESS) != null) {
			// if successful
			JSONArray stories = new JSONArray();
			List<HashMap<String, String>> rawList = new ArrayList<HashMap<String, String>>();

			try {
				// Getting Array of stories
				stories = json.getJSONArray(F8th.TAG_STORIES);

				// looping through All stories
				for (int i = 0; i < stories.length(); i++) {
					JSONObject s = stories.getJSONObject(i);

					// Storing each json item in variable
					String sid = s.getString(F8th.TAG_SID);
					String storyId = s.getString(F8th.TAG_STORY_ID);
					String strStory = decodeApostrophe(s.getString(F8th.TAG_STORY));
					String authorId = s.getString(F8th.TAG_STORY_AUTHOR_ID);
					String author = decodeApostrophe(s.getString(F8th.TAG_STORY_AUTHOR));
					String favs = s.getString(F8th.TAG_STORY_FAVS);
					String isFav = s.getString(F8th.TAG_STORY_IS_FAV);
					String isOwner = s.getString(F8th.TAG_STORY_IS_OWNER);
					String grpVisible = s.getString(F8th.TAG_STORY_VISIBILITY);

					// creating new HashMap
					HashMap<String, String> map = new HashMap<String, String>();

					// adding each child node to HashMap key => value
					map.put(F8th.TAG_SID, sid);
					map.put(F8th.TAG_STORY_ID, storyId);
					map.put(F8th.TAG_STORY, strStory);
					map.put(F8th.TAG_STORY_AUTHOR_ID, authorId);
					map.put(F8th.TAG_STORY_AUTHOR, author);
					map.put(F8th.TAG_STORY_FAVS, favs);
					map.put(F8th.TAG_STORY_IS_FAV, isFav);
					map.put(F8th.TAG_STORY_IS_OWNER, isOwner);
					map.put(F8th.TAG_STORY_VISIBILITY, grpVisible);

					// adding HashList to ArrayList
					rawList.add(map);
				}// end of for-loop
				Log.i("jsonparser", "raw list fetched");

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("jsonparser", "error fetching list");
			}

			return getStoriesList(rawList);

		} else {
			Log.e("jsonparser", "null error fetching list");
			return null;
		}
	}//end of method parseStories()
	
	
	public List<Story> parseFavStoryList(JSONObject json) throws JSONException {
		
		if (json.getString(F8th.KEY_SUCCESS) != null) {
			// if successful
			JSONArray stories = new JSONArray();
			List<HashMap<String, String>> rawList = new ArrayList<HashMap<String, String>>();

			try {
				// Getting Array of stories
				stories = json.getJSONArray(F8th.TAG_FAV_STORIES);

				// looping through All stories
				for (int i = 0; i < stories.length(); i++) {
					JSONObject s = stories.getJSONObject(i);

					// Storing each json item in variable
					String favId = s.getString(F8th.TAG_favID);
					String sid = s.getString(F8th.TAG_SID);
					String storyId = s.getString(F8th.TAG_STORY_ID);
					String strStory = decodeApostrophe(s.getString(F8th.TAG_STORY));
					String authorId = s.getString(F8th.TAG_STORY_AUTHOR_ID);
					String author = decodeApostrophe(s.getString(F8th.TAG_STORY_AUTHOR));
					String favs = s.getString(F8th.TAG_STORY_FAVS);
					String isFav = s.getString(F8th.TAG_STORY_IS_FAV);
					String isOwner = s.getString(F8th.TAG_STORY_IS_OWNER);
					String grpVisible = s.getString(F8th.TAG_STORY_VISIBILITY);
					

					// creating new HashMap
					HashMap<String, String> map = new HashMap<String, String>();

					// adding each child node to HashMap key => value
					map.put(F8th.TAG_favID, favId);
					map.put(F8th.TAG_SID, sid);
					map.put(F8th.TAG_STORY_ID, storyId);
					map.put(F8th.TAG_STORY, strStory);
					map.put(F8th.TAG_STORY_AUTHOR_ID, authorId);
					map.put(F8th.TAG_STORY_AUTHOR, author);
					map.put(F8th.TAG_STORY_FAVS, favs);
					map.put(F8th.TAG_STORY_IS_FAV, isFav);
					map.put(F8th.TAG_STORY_IS_OWNER, isOwner);
					map.put(F8th.TAG_STORY_VISIBILITY, grpVisible);

					// adding HashList to ArrayList
					rawList.add(map);
				}// end of for-loop
				Log.i("jsonparser", "raw list fetched");

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("jsonparser", "error fetching list");
			}

			return getStoriesList(rawList);

		} else {
			Log.e("jsonparser", "null error fetching list");
			return null;
		}
	}//end of method parseStories()
	
	public List<Group> parseGroupList(JSONObject json) throws JSONException {
		
		if (json.getString(F8th.KEY_SUCCESS) != null) {
			// if successful
			JSONArray groups = new JSONArray();
			List<HashMap<String, String>> rawList = new ArrayList<HashMap<String, String>>();

			try {
				// Getting Array of groups
				groups = json.getJSONArray(F8th.TAG_GROUPS);

				// looping through All groups
				for (int i = 0; i < groups.length(); i++) {
					JSONObject s = groups.getJSONObject(i);

					// Storing each json item in variable
					String gid = s.getString(F8th.TAG_GID);
					String grpId = s.getString(F8th.TAG_GRP_ID);
					String grpOwnerId = s.getString(F8th.TAG_GRP_OWNER_ID);
					String grpOwner = decodeApostrophe(s.getString(F8th.TAG_GRP_OWNER));
					String grpName = decodeApostrophe(s.getString(F8th.TAG_GRP_NAME));
					String grpType = decodeApostrophe(s.getString(F8th.TAG_GRP_TYPE));
					String grpSize = s.getString(F8th.TAG_GRP_SIZE);
					String grpCity = decodeApostrophe(s.getString(F8th.TAG_GRP_CITY));
					String grpCountry = decodeApostrophe(s.getString(F8th.TAG_GRP_COUNTRY));
					String userType = s.getString(F8th.TAG_GRP_USER_TYPE);
					

					// creating new HashMap
					HashMap<String, String> map = new HashMap<String, String>();

					// adding each child node to HashMap key => value
					map.put(F8th.TAG_GID, gid);
					map.put(F8th.TAG_GRP_ID, grpId);
					map.put(F8th.TAG_GRP_OWNER_ID, grpOwnerId);
					map.put(F8th.TAG_GRP_OWNER, grpOwner);
					map.put(F8th.TAG_GRP_NAME, grpName);
					map.put(F8th.TAG_GRP_TYPE, grpType);
					map.put(F8th.TAG_GRP_SIZE, grpSize);
					map.put(F8th.TAG_GRP_CITY, grpCity);
					map.put(F8th.TAG_GRP_COUNTRY, grpCountry);
					map.put(F8th.TAG_GRP_USER_TYPE, userType);

					// adding HashList to ArrayList
					rawList.add(map);
				}// end of for-loop
				Log.i("jsonparser", "raw list fetched");

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("jsonparser", "error fetching list");
			}

			return getGroupsList(rawList);

		} else {
			Log.e("jsonparser", "null error fetching list");
			return null;
		}
	}//end of method parseGroups()
	
	
	public List<Group> parseJoinedGroupList(JSONObject json) throws JSONException {
		
		if (json.getString(F8th.KEY_SUCCESS) != null) {
			// if successful
			JSONArray groups = new JSONArray();
			List<HashMap<String, String>> rawList = new ArrayList<HashMap<String, String>>();

			try {
				// Getting Array of groups
				groups = json.getJSONArray(F8th.TAG_JOINED_GROUPS);

				// looping through All groups
				for (int i = 0; i < groups.length(); i++) {
					JSONObject s = groups.getJSONObject(i);

					// Storing each json item in variable
					String mid = s.getString(F8th.TAG_MID);
					String gid = s.getString(F8th.TAG_GID);
					String grpId = s.getString(F8th.TAG_GRP_ID);
					String grpOwnerId = s.getString(F8th.TAG_GRP_OWNER_ID);
					String grpOwner = decodeApostrophe(s.getString(F8th.TAG_GRP_OWNER));
					String grpName = decodeApostrophe(s.getString(F8th.TAG_GRP_NAME));
					String grpType = decodeApostrophe(s.getString(F8th.TAG_GRP_TYPE));
					String grpSize = s.getString(F8th.TAG_GRP_SIZE);
					String grpCity = decodeApostrophe(s.getString(F8th.TAG_GRP_CITY));
					String grpCountry = decodeApostrophe(s.getString(F8th.TAG_GRP_COUNTRY));
					String userType = s.getString(F8th.TAG_GRP_USER_TYPE);
					

					// creating new HashMap
					HashMap<String, String> map = new HashMap<String, String>();

					// adding each child node to HashMap key => value
					map.put(F8th.TAG_MID, mid);
					map.put(F8th.TAG_GID, gid);
					map.put(F8th.TAG_GRP_ID, grpId);
					map.put(F8th.TAG_GRP_OWNER_ID, grpOwnerId);
					map.put(F8th.TAG_GRP_OWNER, grpOwner);
					map.put(F8th.TAG_GRP_NAME, grpName);
					map.put(F8th.TAG_GRP_TYPE, grpType);
					map.put(F8th.TAG_GRP_SIZE, grpSize);
					map.put(F8th.TAG_GRP_CITY, grpCity);
					map.put(F8th.TAG_GRP_COUNTRY, grpCountry);
					map.put(F8th.TAG_GRP_USER_TYPE, userType);

					// adding HashList to ArrayList
					rawList.add(map);
				}// end of for-loop
				Log.i("jsonparser", "raw list fetched");

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("jsonparser", "error fetching list");
			}

			return getGroupsList(rawList);

		} else {
			Log.e("jsonparser", "null error fetching list");
			return null;
		}
	}//end of method parseGroups()
	
	
	public List<Notification> parseNotificationList(JSONObject json) throws JSONException {

		if (json.getString(F8th.KEY_SUCCESS) != null) {
			// if successful
			JSONArray notify = new JSONArray();
			List<HashMap<String, String>> rawList = new ArrayList<HashMap<String, String>>();

			try {
				// Getting Array of notifications
				notify = json.getJSONArray(F8th.TAG_NOTIFICATIONS);

				// looping through All notifications
				for (int i = 0; i < notify.length(); i++) {
					JSONObject s = notify.getJSONObject(i);

					// Storing each json item in variable
					String nid = s.getString(F8th.TAG_NID);
					String notifyId = s.getString(F8th.TAG_NF_ID);
					String message = decodeApostrophe(s.getString(F8th.TAG_NF_MESSAGE));
					String senderId = s.getString(F8th.TAG_NF_SENDER_ID);
					String sender = decodeApostrophe(s.getString(F8th.TAG_NF_SENDER));
					String date = s.getString(F8th.TAG_NF_DATE);
					String status = s.getString(F8th.TAG_NF_STATUS);
					

					// creating new HashMap
					HashMap<String, String> map = new HashMap<String, String>();

					// adding each child node to HashMap key => value
					map.put(F8th.TAG_NID, nid);
					map.put(F8th.TAG_NF_ID, notifyId);
					map.put(F8th.TAG_NF_MESSAGE, message);
					map.put(F8th.TAG_NF_SENDER_ID, senderId);
					map.put(F8th.TAG_NF_SENDER, sender);
					map.put(F8th.TAG_NF_DATE, date);
					map.put(F8th.TAG_NF_STATUS, status);

					// adding HashList to ArrayList
					rawList.add(map);
				}// end of for-loop
				Log.i("jsonparser", "raw list fetched");

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("jsonparser", "error fetching list");
			}

			return getNotificationsList(rawList);

		} else {
			Log.e("jsonparser", "null error fetching list");
			return null;
		}
	}//end of method parseNotifications()
	
//======================================================================================================================================
	
	private String decodeApostrophe(String codedText) {
		String text = codedText.replace('"', '|');
		text = text.replace("&@", "*");
		text = text.replace("&#", ";");

		return text.replace("&%", "'");
	}
	
	// method for success
		private List<User> getUserList(List<HashMap<String, String>> rawPeople) {
			int size = rawPeople.size();
			List<User> people = new ArrayList<User>();
			User person;

			for (int i = 0; i < size; i++) {
				HashMap<String, String> rawPerson = rawPeople.get(i);
				String uid = (String) rawPerson.get(F8th.TAG_UID);
				String userId = (String) rawPerson.get(F8th.TAG_USER_ID);
				//String email = (String) rawPerson.get(F8th.TAG_EMAIL);
				String fname = (String) rawPerson.get(F8th.TAG_FNAME);
				String lname = (String) rawPerson.get(F8th.TAG_LNAME);
				String gender = (String) rawPerson.get(F8th.TAG_GENDER);
				String country = (String) rawPerson.get(F8th.TAG_COUNTRY);
				

				person = new User(uid,userId,fname, lname, gender, country);
				people.add(person);
			}
			Log.i("PeopleProfileList", "people added");
			
			return people;
		}// end of method setUserList()
		
		private List<Story> getStoriesList(List<HashMap<String, String>> rawStories) {
			int size = rawStories.size();
			List<Story> stories = new ArrayList<Story>();
			Story story;

			for (int i = 0; i < size; i++) {
				HashMap<String, String> rawStory = rawStories.get(i);
				String favId = (String) rawStory.get(F8th.TAG_favID);
				String sid = (String) rawStory.get(F8th.TAG_SID);
				String storyId = (String) rawStory.get(F8th.TAG_STORY_ID);
				String strStory = (String) rawStory.get(F8th.TAG_STORY);
				String authorId = (String) rawStory.get(F8th.TAG_STORY_AUTHOR_ID);
				String author = (String) rawStory.get(F8th.TAG_STORY_AUTHOR);
				String favs = (String) rawStory.get(F8th.TAG_STORY_FAVS);
				String isFav = (String) rawStory.get(F8th.TAG_STORY_IS_FAV);
				String isOwner = (String) rawStory.get(F8th.TAG_STORY_IS_OWNER);
				String grpVisible = (String) rawStory.get(F8th.TAG_STORY_VISIBILITY);
				
				if(favId == null){
					favId = "";
				}

				story = new Story(favId,sid,storyId,strStory,authorId, author, favs, isFav, isOwner,grpVisible);
				stories.add(story);
			}
			Log.i("PeopleProfileList", "people added");
			return stories;
		}// end of method setUserList()
		
		private List<Group> getGroupsList(List<HashMap<String, String>> rawGroups) {
			int size = rawGroups.size();
			List<Group> groups = new ArrayList<Group>();
			Group group;

			for (int i = 0; i < size; i++) {
				HashMap<String, String> rawGroup = rawGroups.get(i);
				String mid = (String) rawGroup.get(F8th.TAG_MID);
				String gid = (String) rawGroup.get(F8th.TAG_GID);
				String grpId = (String) rawGroup.get(F8th.TAG_GRP_ID);
				String grpOwnerId = (String) rawGroup.get(F8th.TAG_GRP_OWNER_ID);
				String grpOwner = (String) rawGroup.get(F8th.TAG_GRP_OWNER);
				String grpName = (String) rawGroup.get(F8th.TAG_GRP_NAME);
				String grpType = (String) rawGroup.get(F8th.TAG_GRP_TYPE);
				String grpSize = (String) rawGroup.get(F8th.TAG_GRP_SIZE);
				String grpCity = (String) rawGroup.get(F8th.TAG_GRP_CITY);
				String grpCountry = (String) rawGroup.get(F8th.TAG_GRP_COUNTRY);
				String userType = (String) rawGroup.get(F8th.TAG_GRP_USER_TYPE);
				
				if(mid == null){
					mid = "";
				}

				group = new Group(mid,gid,grpId,grpOwnerId,grpOwner,grpName, grpType, grpSize,grpCity,grpCountry,userType);
				groups.add(group);
			}
			Log.i("PeopleGroupList", "groups added");
			return groups;
		}// end of method setUserList()
		
		private List<Notification>  getNotificationsList(List<HashMap<String, String>> rawNotifications) {
			int size = rawNotifications.size();
			List<Notification> notifications = new ArrayList<Notification>();
			Notification notify;

			for (int i = 0; i < size; i++) {
				HashMap<String, String> rawNotify = rawNotifications.get(i);
				String nid = (String) rawNotify.get(F8th.TAG_NID);
				String notifyId = (String) rawNotify.get(F8th.TAG_NF_ID);
				String message = (String) rawNotify.get(F8th.TAG_NF_MESSAGE);
				String senderId = (String) rawNotify.get(F8th.TAG_NF_SENDER_ID);
				String sender = (String) rawNotify.get(F8th.TAG_NF_SENDER);
				String date = (String) rawNotify.get(F8th.TAG_NF_DATE);
				String status = (String) rawNotify.get(F8th.TAG_NF_STATUS);
				

				notify = new Notification(nid,notifyId,message,senderId,sender,date, status);
				notifications.add(notify);
			}
			Log.i("ProfileNotificationList", "notifications added");
			return notifications;
		}// end of method setUserList()

}// END OF CLASS JSONParser