package owuor.f8th.interfaces;

import owuor.f8th.types.Group;
import owuor.f8th.types.Notification;
import owuor.f8th.types.Story;
import owuor.f8th.types.User;
import owuor.f8th.types.UserProfile;

public interface IUserManagerRequestListener {
	
	public UserProfile onGetCurrentUserRequested();
	
	public void onProfileListRequested(String type,String itemId);
	public void onStoryListRequested(String type,String itemId);
	public void onGroupListRequested(String type,String itemId);
	
	public void onFavStoryListRequested(String favId);
	public void onJoinedGroupListRequested(String mId);
	public void onNotificationsRequested(String nId);
	
	public void onViewGroupRequested(Group group);
	public void onViewProfileRequested(User user);
	public void onViewStoryRequested(Story story);
	public void onViewNotificationRequested(Notification notify);
	
	
	//settings
		public String onGetUserId();
		public void onUpdateAutoLogin(boolean state);
		public void onUpdateProfileRequested(String why,String desire,String about);
		public void onUpdateDetailsRequested(String email,String fname,String lname,String gender,String country);
		public void onResetPasswordRequested(String oldPassword,String newPassword);
		public void onLogoutRequested();
		public void onDeregisterRequested();
		
		
		//Story
		public void onTellStoryRequested(String story,String visibility);
		public void onUpdateStoryRequested(String storyId,String story,String visibility);
		public void onDeleteStoryRequested(String storyId);
		public void onFavoriteStoryRequested(String storyId);
		public void onUnFavoriteStoryRequested(String storyId);
			
		//Group
		public void onCreateGroupRequested(String grpName,String grpType,String grpCity,String grpCountry);
		public void onEditGroupRequested(String grpId,String grpName,String grpType,String grpCity,String grpCountry);
		public void onDeleteGroupRequested(String grpId);		
		public void onJoinGroupRequested(String grpId,String memberType);
		public void onLeaveGroupRequested(String grpId);
				
				
		//Notifications
		//public void sendNotification(String message,String recipientId,String sent_at);
		public void onDeleteNotificationRequested(String notifyId);
		public void onMarkAsReadRequested(String notifyId);
		
	
}
