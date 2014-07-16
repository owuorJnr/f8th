<?php
 /**
 * All Honor and Glory belongs to God Almighty
 * Phil 2:13
 * @since 12th January, 2014
 * @version 2.0
 * 
 */ 
 
/**
 * File to handle all API requests
 * Accepts GET and POST
 * 
 * Each request will be identified by TAG
 * Response will be JSON data
 
  /**
 * check for POST request 
 */
if (isset($_POST['tag']) && $_POST['tag'] != "") {
	// get tag
	$tag = filter_input(INPUT_POST, 'tag', FILTER_SANITIZE_STRING);
 
	// include db handler
	require_once("f8th_functions.php");
	$f8h = new F8TH_FUNCTIONS($tag);
	 
	// check for tag type
	if($tag == "test"){
		print_r($f8h->test($tag));
		
	}else if($tag == "signin"){
		$email = filter_input(INPUT_POST, 'email', FILTER_SANITIZE_EMAIL);
		$password = $_POST['password'];
		echo $f8h->signIn($email,$password);
		
	}else if($tag == "signup"){
		$email = filter_input(INPUT_POST, 'email', FILTER_SANITIZE_EMAIL);
		$fname = filter_input(INPUT_POST, 'fname', FILTER_SANITIZE_STRING);
		$lname = filter_input(INPUT_POST, 'lname', FILTER_SANITIZE_STRING);
		$gender = filter_input(INPUT_POST, 'gender', FILTER_SANITIZE_STRING);
		$country = filter_input(INPUT_POST, 'country', FILTER_SANITIZE_STRING);
		$password = $_POST['password'];
		echo $f8h->signUp($email,$fname,$lname,$gender,$country,$password);
		
	}else if($tag == "forgot_password"){
		$email = filter_input(INPUT_POST, 'email', FILTER_SANITIZE_EMAIL);
		echo $f8h->forgotPass($email);
		
	}else if($tag == "update_user"){
		$user_id = $_POST['user-id'];
		$new_email = filter_input(INPUT_POST, 'email', FILTER_SANITIZE_EMAIL);
		$fname = filter_input(INPUT_POST, 'fname', FILTER_SANITIZE_STRING);
		$lname = filter_input(INPUT_POST, 'lname', FILTER_SANITIZE_STRING);
		$gender = filter_input(INPUT_POST, 'gender', FILTER_SANITIZE_STRING);
		$country = filter_input(INPUT_POST, 'country', FILTER_SANITIZE_STRING);
		$password = $_POST['password'];
		echo $f8h->updateUser($user_id,$new_email,$fname,$lname,$gender,$country,$password);
		
	}else if($tag == "update_profile"){
		$user_id = $_POST['user-id'];
		$password = $_POST['password'];
		$about = filter_input(INPUT_POST, 'fav-verse', FILTER_SANITIZE_STRING);
		$why = filter_input(INPUT_POST, 'why', FILTER_SANITIZE_STRING);
		$desire = filter_input(INPUT_POST, 'desire', FILTER_SANITIZE_STRING);
		echo $f8h->updateProfile($user_id,$password,$why,$desire,$about);
		
	}else if($tag == "upload_profile_photo"){
		$imgData = $_POST['photo'];
		$profile_id = $_POST['profile-id'];
		echo $f8h->uploadProfilePhoto($profile_id,$imgData);
		
	}else if($tag == "get_profile_photo"){	
		$profile_id = $_POST['profile-id'];
		echo $f8h->downloadProfilePhoto($profile_id);
		
	}else if($tag == "reset_password"){
		$user_id = $_POST['user-id'];
		$currentPassword = $_POST['current-password'];
		$newPassword = $_POST['new-password'];
		echo $f8h->resetPass($user_id,$currentPassword,$newPassword);
		
	}else if($tag == "de-register"){
		$user_id = $_POST['user-id'];
		$password = $_POST['password'];
		echo $f8h->deregister($user_id,$password);
		
	}else if($tag == "get_profile"){
		$viewer_id = $_POST['viewer-id'];
		$profile_id = $_POST['profile-id'];
		echo $f8h->getProfile($viewer_id,$profile_id);
		
	}else if($tag == "get_profile_list"){
		$type = $_POST['type'];
		$item_id = $_POST['item-id'];
		echo $f8h->getProfileList($type,$item_id);
		
	}else if($tag == "search_profile"){
		$category = $_POST['category'];
		$searchKey = $_POST['search-key'];
		echo $f8h->searchProfile($category,$searchKey);
		
	}else if($tag == "save_story"){
		$user_id = $_POST['user-id'];
		$story = $_POST['story'];
		$visibility = $_POST['visibility'];
		echo $f8h->saveStory($story,$user_id,$visibility);
		
	}else if($tag == "update_story"){
		$story_id = $_POST['story-id'];
		$story = $_POST['story'];
		$visibility = $_POST['visibility'];
		echo $f8h->updateStory($story_id,$story,$visibility);
		
	}else if($tag == "delete_story"){
		$story_id = $_POST['story-id'];
		echo $f8h->deleteStory($story_id);
		
	}else if($tag == "favorite_story"){
		$story_id = $_POST['story-id'];
		$user_id = $_POST['user-id'];
		echo $f8h->favStory($story_id,$user_id);
		
	}else if($tag == "unfavorite_story"){
		$story_id = $_POST['story-id'];
		$user_id = $_POST['user-id'];
		echo $f8h->unFavStory($story_id,$user_id);
		
	}else if($tag == "get_story_list"){
		$user_id = $_POST['user-id'];
		$type = $_POST['type'];
		$item_id = $_POST['item-id'];
		echo $f8h->getStoryList($user_id,$type,$item_id);
		
	}else if($tag == "get_fav_story_list"){
		$user_id = $_POST['user-id'];
		$fav_id = $_POST['fav-id'];
		echo $f8h->getFavStoryList($user_id,$fav_id);
		
	}else if($tag == "get_story"){
		$story_id = $_POST['story-id'];
		$user_id = $_POST['user-id'];
		echo $f8h->getStory($story_id,$user_id);
		
	}else if($tag == "create_group"){
		$user_id = $_POST['user-id'];
		$grp_name = filter_input(INPUT_POST, 'grp-name', FILTER_SANITIZE_STRING);
		$grp_type = filter_input(INPUT_POST, 'grp-type', FILTER_SANITIZE_STRING);
		$grp_city = filter_input(INPUT_POST, 'grp-city', FILTER_SANITIZE_STRING);
		$grp_country = filter_input(INPUT_POST, 'grp-country', FILTER_SANITIZE_STRING);
		echo $f8h->saveGroup($user_id,$grp_name,$grp_type,$grp_city,$grp_country);
		
	}else if($tag == "upload_group_photo"){
		$imgData = $_POST['photo'];
		$grp_id = $_POST['grp-id'];
		echo $f8h->uploadGroupPhoto($grp_id,$imgData);

	}else if($tag == "get_group_photo"){	
		$grp_id = $_POST['grp-id'];
		echo $f8h->downloadGroupPhoto($grp_id);
	
	}else if($tag == "edit_group"){
		$grp_id = $_POST['grp-id'];
		$grp_name = filter_input(INPUT_POST, 'grp-name', FILTER_SANITIZE_STRING);
		$grp_type = filter_input(INPUT_POST, 'grp-type', FILTER_SANITIZE_STRING);
		$grp_city = filter_input(INPUT_POST, 'grp-city', FILTER_SANITIZE_STRING);
		$grp_country = filter_input(INPUT_POST, 'grp-country', FILTER_SANITIZE_STRING);
		echo $f8h->editGroup($grp_id,$grp_name,$grp_type,$grp_city,$grp_country);
		
	}else if($tag == "delete_group"){
		$grp_id = $_POST['grp-id'];
		echo $f8h->deleteGroup($grp_id);
		
	}else if($tag == "get_group_list"){
		$user_id = $_POST['user-id'];
		$type = $_POST['type'];
		$item_id = $_POST['item-id'];
		echo $f8h->getGroupList($user_id,$type,$item_id);
	
	}else if($tag == "get_joined_group_list"){
		$user_id = $_POST['user-id'];
		$m_id = $_POST['m-id'];
		echo $f8h->getJoinedGroupList($user_id,$m_id);
		
	}else if($tag == "get_group"){
		$user_id = $_POST['user-id'];
		$grp_id = $_POST['grp-id'];
		echo $f8h->getGroup($grp_id,$user_id);
		
	}else if($tag == "join_group"){
		$user_id = $_POST['user-id'];
		$grp_id = $_POST['grp-id'];
		$memberType = $_POST['member-type'];
		echo $f8h->joinGroup($grp_id,$user_id,$memberType);
		
	}else if($tag == "leave_group"){
		$user_id = $_POST['user-id'];
		$grp_id = $_POST['grp-id'];
		echo $f8h->leaveGroup($grp_id,$user_id);
		
	}else if($tag == "send_notification"){
		$message = filter_input(INPUT_POST, 'msg', FILTER_SANITIZE_STRING);
		$user_id = $_POST['user-id'];
		$recipient_id = $_POST['recipient-id'];
		$date = filter_input(INPUT_POST, 'sent-at', FILTER_SANITIZE_STRING);
		echo $f8h->sendNotification($message,$user_id,$recipient_id,$date);
		
	}else if($tag == "get_notification_list"){
		$user_id = $_POST['user-id'];
		$n_id = $_POST['n-id'];
		echo $f8h->getNoficationList($user_id,$n_id);
		
	}else if($tag == "delete_notification"){
		$notify_id = $_POST['notify-id'];
		echo $f8h->deleteNofication($notify_id);
		
	}else if($tag == "notify_mark_as_read"){
		$notify_id = $_POST['notify-id'];
		echo $f8h->markAsRead($notify_id);
		
	/*}else if($tag == ""){*/
	
	}else{
		echo "Invalid Request";
	}
	
}else {
	
	echo "Access Denied";
	
}//end of first if-else

?>