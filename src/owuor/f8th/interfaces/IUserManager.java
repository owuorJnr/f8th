package owuor.f8th.interfaces;


import java.util.List;

import owuor.f8th.types.Group;
import owuor.f8th.types.Notification;
import owuor.f8th.types.Story;
import owuor.f8th.types.User;
import owuor.f8th.types.UserProfile;

public interface IUserManager {

	public String getUserId();
	public String getEmail();
	public String getPassword();
	public String getErrorMsg();
	public String getSuccessMsg();
	
	public UserProfile getCurrentUser();
	
	public void setErrorMsg();
	public void setSuccessMsg();
	
	//deals with local database
	public boolean updateAutoLogin(boolean state);
	public boolean loadUser();
	//public boolean saveLocalUser(UserProfile onlineUser);
	public boolean deleteLocalUser();
	
	//Online User Profile
	public boolean isUserAuthenticated();
	public boolean authenticateUser(String email,String password);
	public boolean signUpUser(String email,String fname,String lname,String gender,String country,String password);
	public boolean updateProfile(String why,String desire,String about);
	public boolean updateUser(String email,String fname,String lname,String gender,String country);
	public boolean resetPassword(String newPassword);
	public boolean forgotPassword(String email);
	public boolean deregisterUser();
	
	//Get
	public UserProfile downloadUserProfile(String userId);//called by authenticateUser/updateUser/updateProfile
	public Story downloadStory(String storyId);
	public Group downloadGroup(String groupId);
	
	
	//Story
	public boolean tellStory(String story,String visibility);
	public boolean updateStory(String storyId,String story,String visibility);
	public boolean deleteStory(String storyId);
	public boolean favoriteStory(String storyId);
	public boolean unFavoriteStory(String storyId);
	
	
	//Group
	public boolean createGroup(String grpName,String grpType,String grpCity,String grpCountry);
	public boolean editGroup(String grpId,String grpName,String grpType,String grpCity,String grpCountry);
	public boolean deleteGroup(String grpId);
	public boolean joinGroup(String grpId,String memberType);
	public boolean leaveGroup(String grpId);
	
	
	//Notifications
	//public boolean sendNotification(String message,String recipientId,String sent_at);
	public boolean deleteNotification(String notifyId);
	public boolean markAsRead(String notifyId);
	
	//Automatic Lists
	public List<User> profileList(String type,String itemId);
	public List<Story> storyList(String type,String itemId);
	public List<Group> groupList(String type,String itemId);
	public List<Notification> notifyList(String nId);
	
	public List<Group> joinedGroupList(String mId);
	public List<Story> favStoryList(String favId);
	
	
	//photos
	public boolean uploadProfilePhoto(String profileId,byte[] photo);
	public byte[] downloadProfilePhoto(String profileId);
	public boolean uploadGroupPhoto(String grpId,byte[] photo);
	public byte[] downloadGroupPhoto(String groupId);
	
	
	//other functions
	public boolean loginUser(String email,String password);
	public boolean logoutUser();
	public boolean isNetworkConnected();
	public void startTimerTask();
	public void stopTimerTask();
	public void exit();
}
