package owuor.f8th.types;

public class F8th {
	
	//-----------------NetworkOperator-----------------------
		//public static final String AUTHENTIFICATION_SERVER_ADDRESS = "http://10.0.2.2/f8th/f8th.php";
		public static final String AUTHENTIFICATION_SERVER_ADDRESS = "http://sharemiale.info.ke/f8th_api/f8th.php";
		
		public static final String F8TH_LINK = "https://play.google.com/store/apps/details?id=owuor.f8th";
		//strathmore server
		//public static final String AUTHENTIFICATION_SERVER_ADDRESS = "http://192.168.184.39/sharefaith/index.php";
		//public static final String AUTHENTIFICATION_SERVER_ADDRESS = "http://41.89.6.181/sharefaith/index.php";
		//public static final String HTTP_REQUEST_FAILED = null;
	
	//-------------------F8thDialog--------------------------
		public static final String DIALOG_TITLE = "F8th Alert";
		
	//-------------------F8thActivity--------------------------	
		
	//-------------------LoginActivity--------------------------
		public static String CONNECTED_TO_SERVICE = "connected";
		
	//-------------------ChooseUserFragment--------------------------
		public static final String ANOTHER_USER = "manually sign in ";
		
	//-------------------UserSettingsFragment--------------------------
		public static String[] to = {"admin@sharemiale.info.ke"};
		public static String subject = "my suggestion";
		
	//-------------------F8thActivity && JSONParser--------------------------	
		public static final String TAG_USERS = "profiles";
		public static final String TAG_USER_PROFILE = "user_profile";
		public static final String TAG_UID = "u_id";
	    public static final String TAG_USER_ID = "user_id";
	    public static final String TAG_FNAME = "fname";
	    public static final String TAG_LNAME = "lname";
	    public static final String TAG_GENDER = "gender";
	    public static final String TAG_COUNTRY = "country";
	    public static final String TAG_FAV_VERSE = "fav_verse";
	    public static final String TAG_WHY = "why";
	    public static final String TAG_DESIRE = "desire";
	    public static final String TAG_VIEWS = "views";
	    
	//---------------------------------------------------------------------------
	    public static final String TAG_STORIES = "stories";
	    public static final String TAG_FAV_STORIES = "fav_stories";
	    public static final String TAG_STORY_DETAILS = "story_details";
	    public static final String TAG_favID = "fav_id";
	    public static final String TAG_SID = "s_id";
	    public static final String TAG_STORY_ID = "story_id";
	    public static final String TAG_STORY = "story";
	    public static final String TAG_STORY_AUTHOR_ID = "author_id";
	    public static final String TAG_STORY_AUTHOR = "author";
	    //public static final String TAG_STORY_DATE = "created_at";
	    public static final String TAG_STORY_FAVS = "favs";
	    public static final String TAG_STORY_IS_FAV = "is_fav";
	    public static final String TAG_STORY_IS_OWNER = "is_owner";
	    public static final String TAG_STORY_VISIBILITY = "group";
	    
	    
	    public static final String TAG_GROUPS = "groups";
	    public static final String TAG_JOINED_GROUPS = "joined_groups";
	    public static final String TAG_GRP_DETAILS = "group_details";
	    public static final String TAG_GID = "g_id";
	    public static final String TAG_MID = "m_id";
	    public static final String TAG_GRP_ID = "group_id";
	    public static final String TAG_GRP_OWNER_ID = "owner_id";
	    public static final String TAG_GRP_OWNER = "owner";
	    public static final String TAG_GRP_NAME = "group_name";
	    public static final String TAG_GRP_TYPE = "group_type";
	    //public static final String TAG_GRP_DATE = "created_at";
	    public static final String TAG_GRP_SIZE = "group_size";
	    public static final String TAG_GRP_CITY = "group_city";
	    public static final String TAG_GRP_COUNTRY = "group_country";
	    public static final String TAG_GRP_USER_TYPE = "user_type";
	    
	    public static final String TAG_NOTIFICATIONS = "notifications";
	    public static final String TAG_NID = "n_id";
	    public static final String TAG_NF_ID = "notify_id";
	    public static final String TAG_NF_MESSAGE = "message";
	    public static final String TAG_NF_SENDER_ID = "sender_id";
	    public static final String TAG_NF_SENDER = "sender";
	    public static final String TAG_NF_DATE = "sent_at";
	    public static final String TAG_NF_STATUS = "status";
	    
		
	//-------------------UserManagerService--------------------
		public static final int TIMER_INTERVAL = 1000;
		
		
		public static final String signin_tag = "signin";
		public static final String signup_tag = "signup";
		public static final String update_user_tag = "update_user";
		public static final String update_profile_tag = "update_profile";
		public static final String reset_passwd_tag = "reset_password";
		public static final String forgot_passwd_tag = "forgot_password";
		public static final String deregister_tag = "de-register";
		
		public static final String get_profile_tag = "get_profile";
		public static final String get_story_tag = "get_story";
		public static final String get_group_tag = "get_group";
		
		public static final String tell_story_tag = "save_story";
		public static final String update_story_tag = "update_story";
		public static final String delete_story_tag = "delete_story";
		public static final String fav_story_tag = "favorite_story";
		public static final String unfav_story_tag = "unfavorite_story";
		
		public static final String create_group_tag = "create_group";
		public static final String edit_group_tag = "edit_group";
		public static final String delete_group_tag = "delete_group";
		public static final String join_group_tag = "join_group";
		public static final String leave_group_tag = "leave_group";
		
		public static final String delete_notify_tag = "delete_notification";
		public static final String markAsRead_tag = "notify_mark_as_read";
		
		public static final String profile_list_tag = "get_profile_list";
		public static final String story_list_tag = "get_story_list";
		public static final String group_list_tag = "get_group_list";
		public static final String notify_list_tag = "get_notification_list";
		
		public static final String joinedGroup_list_tag = "get_joined_group_list";
		public static final String favStory_list_tag = "get_fav_story_list";
		
		public static final String upload_profile_photo_tag = "upload_profile_photo";
		public static final String upload_group_photo_tag = "upload_group_photo";
		public static final String download_profile_photo_tag = "get_profile_photo";
		public static final String download_group_photo_tag = "get_group_photo";
		
		public static final String INTERNET_NOT_AVAILABLE = "no_internet";
		public static final String INTERNET_AVAILABLE = "internet";
		public static final String LIST_REFRESH = "recent";
		public static final String LIST_LOADMORE = "load-more";
		public static final String LIST_FIRST_TIME = "";
		
		public static final String AUTO_LOGIN_SET = "YES";
		public static final String AUTO_LOGIN_UNSET = "NO";
		
		public static final String KEY_SUCCESS = "success";
	    public static final String KEY_SUCCESS_MSG = "success_msg";
	    public static final String KEY_ERROR = "error";
	    public static final String KEY_ERROR_MSG = "error_msg";
	    
//--------------------------------SettingsFragment--------------------------------------------------------
	    public static final String SIGN_OUT = "Sign Out";
	    public static final String DEREGISTER = "De-Register";
	    public static final String UPDATE_PROFILE = "Update Profile";
	    public static final String EDIT_USER = "Edit User";
	    public static final String CHANGE_PASSWORD = "Change Password";
	    public static final String REPORT_ISSUE = "Report Issue";
	    public static final String ABOUT_F8TH = "About";
	    
//---------------------------Main...Fragments---------------------------------------------------------------
	    public static final String PEOPLE_FRAGMENT = "people";
	    public static final String HOME_FRAGMENT = "stories";
	    public static final String PROFILE_FRAGMENT = "profile";
	    
//----------------------------View...Fragment-------------------------------------------------------------
	    public static final String GROUP_OWNER = "owner";
	    public static final String GROUP_MANAGER = "member";
	    public static final String GROUP_MEMBER = "member";
	    public static final String GROUP_NON_MEMBER = "";
	    
	    public static final String NOTIFY_READ = "read";
	    public static final String NOTIFY_UNREAD = "unread";
	    
	    public static final String STORY_IS_OWNER = "yes";
	    public static final String STORY_IS_NOT_OWNER  = "no";
	    public static final String STORY_IS_FAV  = "yes";
	    public static final String STORY_IS_NOT_FAV  = "no";
	    
	    public static final String VIEW  = "view";
	    public static final String VIEW_ID  = "viewId";
	    public static final String VIEW_SETTINGS  = "settings";
	    public static final String VIEW_STORY  = "story_view";
	    public static final String VIEW_PROFILE  = "profile_view";
	    public static final String VIEW_GROUP  = "group_view";
	    public static final String VIEW_NOTIFICATION  = "notify_view"; 
	   
	    
	    public static final int LIST_LIMIT = 20;
	    
	}
