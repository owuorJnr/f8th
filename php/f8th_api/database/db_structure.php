<?php
/**
 * All Honor and Glory belongs to God Almighty
 * Psalm 27:13-14
 * @since 12th January, 2014
 * @version 2.0
 * 
 */ 
	//DATABASE TABLES
	define("tblUsers","tblUsers");
	define("tblUserProfiles","tblUserProfiles");
	define("tblProfileViews","tblProfileViews");
	define("tblUserNotifications","tblUserNotifications");
	define("tblUserStories","tblUserStories");
	define("tblFavoriteStories","tblFavoriteStories");
	define("tblStoryVisibility","tblStoryVisibility");
	define("tblUserGroups","tblUserGroups");
	define("tblGroupMembers","tblGroupMembers");
	define("tblGroupEvents","tblGroupEvents");
	
	//SQL CONSTANTS
	define("ALL","*");
	define("FIRST_TIME","");
	define("RECENT","recent");
	define("LOADMORE","load-more");
	define("READ","read");
	define("UNREAD","not-read");
	define("JOINED_GROUPS","joined");
	define("FAVORITE_STORIES","favs");
	define("SENDER_TYPE_GROUP","group");
	define("SENDER_TYPE_USER","user");
	define("SIGNUP_STATUS","registered");

	//TABLE COLUMNS
	//tblUsers
	define("uid",tblUsers.".u_id");
	define("user_id","user_id");
	define("email",tblUsers.".email");
	define("uname",tblUsers.".uname");
	define("fname",tblUsers.".fname");
	define("lname",tblUsers.".lname");
	define("gender",tblUsers.".gender");
	define("country",tblUsers.".country");
	define("password",tblUsers.".encrypted_password");
	define("salt",tblUsers.".salt");
	define("created_at","created_at");
	define("updated_at","updated_at");
	
	//tblUserProfiles
	define("P_ID",tblUserProfiles.".p_id");
	define("fav_verse",tblUserProfiles.".fav_verse");
	define("why",tblUserProfiles.".why");
	define("desire",tblUserProfiles.".desire");
	//define("user","tblUserProfiles.unique_id");
	//define("","tblUserProfiles.created_at");
	//define("","tblUserProfiles.updated_at");
	
	//tblUserNotify
	
	
	//tblUserStories
	
	
	//tblUserFavorites
	
	
	//tblUserGroups
	
	
	//tblGroupMembers
	
	
	//tblGroupEvents
	
	
?>