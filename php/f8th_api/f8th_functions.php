<?php
/**
 * All Honor and Glory belongs to God Almighty
 * Phil 2:13
 * @since 12th January, 2014
 * @version 2.0
 * 
 */ 
	
class F8TH_FUNCTIONS
{
	private $user,$story,$notification,$group;
	private $response;
	
	public function __construct($tag)
	{
		// response Array
		$this->response = array("tag" => $tag, "success" => 0, "error" => 0);
	}
		
	public function __destruct()
	{
	
	}
	
	//====================================

	function test($test)
	{
		$this->createAll();
		$test = "dickytea@gmail.com";
		//return "You entered: ".$test;
		return $this->user->searchProfiles("Country","kenya");
	}//end of function
	
	function signIn($email,$password){
		$this->createUser();
		$result = $this->user->signInUser($email,$password);
		//return $result;
		if($result === TRUE){
		
			$profile = $this->user->checkProfile($email);
			$user_id = "";
			$message = "Sign In Successful\n";
			if($profile !== FALSE){
				$message .= $profile["msg"];
				$user_id = $profile["USER_ID"];
			}
		
			$this->response["success"] = 1;
			$this->response["userid"] = $user_id;
			$this->response["success_msg"] = $message;
			
			return json_encode($this->response);
		}else{
			$this->response["error"] = 1;
			$this->response["error_msg"] = $result;
			return json_encode($this->response);
		}
	}//end of function
	
	function signUp($email,$fname,$lname,$gender,$country,$password){
		
		$this->createUser();
		$alreadyExits = $this->user->isUserAlreadyExisting($email);
		//return $result;
		if($alreadyExits === FALSE){
			
			$result = $this->user->signUpUser($email,$fname,$lname,$gender,$country,$password);
			$user_id = "";
			
			if($result !== FALSE){
				$profile = $this->user->checkProfile($email);
				if($profile !== FALSE){
					$user_id = $profile["USER_ID"];
				}
			}
			
			
			$this->response["success"] = 1;
			$this->response["userid"] = $user_id;
			$this->response["success_msg"] = $result;
			
			return json_encode($this->response);
		}else{
		
			$this->response["error"] = 1;
			$this->response["error_msg"] = "User already registered!";
			return json_encode($this->response);
		}
		
	}//end of function
	
	function forgotPass($email){
		$this->createUser();
		$result = $this->user->forgotPassword($email);
		//return $result;
		if($result === TRUE){
		
			$this->response["success"] = 1;
			$this->response["success_msg"] = "New Password sent to ".$email;
			
			return json_encode($this->response);
		}else{
		
			$this->response["error"] = 1;
			$this->response["error_msg"] = $result;
			return json_encode($this->response);
		}
		
	}//end of function
	
	private function authenticateUser($user_id,$password)
	{
		$this->createUser();
		$checkUser = $this->user-> isUserAuthenticated($user_id,$password);
		
		if($checkUser == "correct"){
			return TRUE;
		}else if($checkUser == "wrong"){
			return "authentication error: incorrect password";
		}else if($checkUser === FALSE){
			return "authentication error: user does not exist";
		}
	}//end of function
	
	function updateUser($user_id,$new_email,$fname,$lname,$gender,$country,$password){
		//$this->createUser();
		$checkUser = $this->authenticateUser($user_id,$password);
		
		if($checkUser === TRUE){
			$result = $this->user->updateUser($user_id,$new_email,$fname,$lname,$gender,$country);
			//return $result;
			if($result === TRUE){
				
				$this->response["success"] = 1;
				$this->response["success_msg"] = "Thank you ".$fname.", for updating your details.";
				
				return json_encode($this->response);
			}else{
			
				$this->response["error"] = 1;
				$this->response["error_msg"] = $result;
					
				return json_encode($this->response);
			}
		}else{
			$this->response["error"] = 2;
			$this->response["error_msg"] = $checkUser;
			return json_encode($this->response);
		}
		
	}//end of function
	
	function updateProfile($user_id,$password,$why,$desire,$about){
		//$this->createUser();
		$checkUser = $this->authenticateUser($user_id,$password);
		
		if($checkUser === TRUE){
			$result = $this->user->profileSaved($user_id);
			//return $result;
			if($result === TRUE){
				//update
				$result1 = $this->user->updateProfile($user_id,$why,$desire,$about);
				
			}else if($result === FALSE){
				//save
				$result1 = $this->user-> saveProfile($user_id,$why,$desire,$about);
			}
			
			if($result1 === TRUE){
				$this->response["success"] = 1;
				$this->response["success_msg"] = "Profile Updated";
				
				return json_encode($this->response);
			}else{
			
				$this->response["error"] = 1;
				$this->response["error_msg"] = "Profile Not Updated";
					
				return json_encode($this->response);
			}
		}else{
			$this->response["error"] = 2;
			$this->response["error_msg"] = $checkUser;
			return json_encode($this->response);
		}
	}//end of function
	
	function uploadProfilePhoto($profile_id,$imgData)
	{
		$this->createUser();
		$result = $this->user->saveProfilePhoto($profile_id,$imgData);
		if($result === TRUE){
		
			$message = "Profile Photo Uploaded";
		
			$this->response["success"] = 1;
			$this->response["success_msg"] = $message;
			
			return json_encode($this->response);
		}else{
			$this->response["error"] = 1;
			$this->response["error_msg"] = $result;
			return json_encode($this->response);
		}
	}//end of function
	
	function downloadProfilePhoto($profile_id)
	{
		$this->createUser();
		$result = $this->user->getProfilePhoto($profile_id);
		if($result !== FALSE && $result != ""){
		
			return $result;
		}else{
			$this->response["error"] = 1;
			$this->response["error_msg"] = "Oops, something went wrong.\nError Downloading Photo";
			return json_encode($this->response);
		}
	}//end of function
	
	function resetPass($user_id,$currentPassword,$newPassword){
		//$this->createUser();
		$checkUser = $this->authenticateUser($user_id,$currentPassword);
		
		if($checkUser === TRUE){
			$result = $this->user->resetPassword($user_id,$newPassword);
			//return $result;
			if($result === TRUE){
				
				$this->response["success"] = 1;
				$this->response["success_msg"] = "Password Reset Successfully";
				
				return json_encode($this->response);
			}else{
			
				$this->response["error"] = 1;
				$this->response["error_msg"] = $result;
					
				return json_encode($this->response);
			}
		}else{
			$this->response["error"] = 2;
			$this->response["error_msg"] = $checkUser;
			return json_encode($this->response);
		}
	}//end of function
	
	function deregister($user_id,$password){
		//$this->createUser();
		$checkUser = $this->authenticateUser($user_id,$currentPassword);
		
		if($checkUser === TRUE){
			$result = $this->user->deleteUser($user_id);
			//return $result;
			if($result === TRUE){
				
				$this->response["success"] = 1;
				$this->response["success_msg"] = "User De-registered";
				
				return json_encode($this->response);
			}else{
			
				$this->response["error"] = 1;
				$this->response["error_msg"] = $result;
					
				return json_encode($this->response);
			}
		}else{
			$this->response["error"] = 2;
			$this->response["error_msg"] = $checkUser;
			return json_encode($this->response);
		}
	}//end of function
	
	function getProfile($viewer_id,$profile_id){
		$this->createUser();
		if($viewer_id != $profile_id){
			$this->user->addProfileView($profile_id);
		}
			
		$result = $this->user->getProfile($viewer_id,$profile_id);
		
		if($result !== FALSE){
			
			return json_encode($result);
		}else{
		
			$this->response["error"] = 1;
			$this->response["error_msg"] = "User Profile Not Found!";
			return json_encode($this->response);
		}
		
	}//end of function
	
	function getProfileList($type,$item_id){
		$this->createUser();
		return json_encode($this->user->getProfileList($type,$item_id));
	}//end of function
	
	function searchProfile($category,$searchKey){
		$this->createUser();
		return json_encode($this->user->searchProfiles($category,$searchKey));
	}//end of function
	
	function saveStory($story,$author_id,$visibility){
		$this->createStory();
		$result = $this->story->saveStory($story,$author_id,$visibility);
		if($result === TRUE){
		
			$message = "Story Told";
		
			$this->response["success"] = 1;
			$this->response["success_msg"] = $message;
			
			return json_encode($this->response);
		}else{
			$this->response["error"] = 1;
			$this->response["error_msg"] = $result;
			return json_encode($this->response);
		}
		
	}//end of function
	
	function updateStory($story_id,$story,$visibility){
		$this->createStory();
		$result = $this->story->editStory($story_id,$story,$visibility);
		if($result === TRUE){
		
			$message = "Story Updated";
		
			$this->response["success"] = 1;
			$this->response["success_msg"] = $message;
			
			return json_encode($this->response);
		}else{
			$this->response["error"] = 1;
			$this->response["error_msg"] = $result;
			return json_encode($this->response);
		}
	}//end of function
	
	function deleteStory($story_id){
		$this->createStory();
		$result = $this->story->deleteStory($story_id);
		if($result === TRUE){
		
			$message = "Story Deleted";
		
			$this->response["success"] = 1;
			$this->response["success_msg"] = $message;
			
			return json_encode($this->response);
		}else{
			$this->response["error"] = 1;
			$this->response["error_msg"] = $result;
			return json_encode($this->response);
		}
	}//end of function
	
	function favStory($story_id,$user_id){
		$this->createStory();
		$result = $this->story->addFavorite($story_id,$user_id);
		if($result === TRUE){
		
			$message = "Favorite Added";
		
			$this->response["success"] = 1;
			$this->response["success_msg"] = $message;
			
			return json_encode($this->response);
		}else{
			$this->response["error"] = 1;
			$this->response["error_msg"] = $result;
			return json_encode($this->response);
		}
	}//end of function
	
	function unFavStory($story_id,$user_id){
		$this->createStory();
		$result = $this->story->removeFavorite($story_id,$user_id);
		if($result === TRUE){
		
			$message = "Favorite Removed";
		
			$this->response["success"] = 1;
			$this->response["success_msg"] = $message;
			
			return json_encode($this->response);
		}else{
			$this->response["error"] = 1;
			$this->response["error_msg"] = $result;
			return json_encode($this->response);
		}
	}//end of function
	
	function getStoryList($user_id,$type,$item_id){
		$this->createStory();
		return json_encode($this->story->getStoryList($user_id,$type,$item_id));
	}//end of function
	
	function getFavStoryList($user_id,$fav_id){
		$this->createStory();
		return json_encode($this->story->getFavStoryList($user_id,$fav_id));
	}//end of function
	
	function getStory($story_id,$user_id){
		$this->createStory();
		return json_encode($this->story->getStory($story_id,$user_id));
	}//end of function
	
	function saveGroup($user_id,$grp_name,$grp_type,$city,$country){
		$this->createGroup();
		$result = $this->group->createGroup($user_id,$grp_name,$grp_type,$city,$country);
		if($result === TRUE){
		
			$message = "Group Created";
		
			$this->response["success"] = 1;
			$this->response["success_msg"] = $message;
			
			return json_encode($this->response);
		}else{
			$this->response["error"] = 1;
			$this->response["error_msg"] = $result;
			return json_encode($this->response);
		}
	}//end of function
	
	function uploadGroupPhoto($grp_id,$imgData)
	{
		$this->createGroup();
		$result = $this->group->saveGroupPhoto($grp_id,$imgData);
		if($result === TRUE){
		
			$message = "Group Photo Uploaded";
		
			$this->response["success"] = 1;
			$this->response["success_msg"] = $message;
			
			return json_encode($this->response);
		}else{
			$this->response["error"] = 1;
			$this->response["error_msg"] = $result;
			return json_encode($this->response);
		}
	}//end of function
	
	function downloadGroupPhoto($grp_id)
	{
		$this->createGroup();
		$result = $this->group->getGroupPhoto($grp_id);
		if($result !== FALSE && $result != ""){
		
			return $result;
		}else{
			$this->response["error"] = 1;
			$this->response["error_msg"] = "Oops, something went wrong.\nError Downloading Photo";
			return json_encode($this->response);
		}
	}//end of function
	
	function editGroup($grp_id,$grp_name,$grp_type,$city,$country){
		$this->createGroup();
		$result = $this->group->editGroup($grp_id,$grp_name,$grp_type,$city,$country);
		if($result === TRUE){
		
			$message = "Group Updated";
		
			$this->response["success"] = 1;
			$this->response["success_msg"] = $message;
			
			return json_encode($this->response);
		}else{
			$this->response["error"] = 1;
			$this->response["error_msg"] = $result;
			return json_encode($this->response);
		}
	}//end of function
	
	function deleteGroup($grp_id){
		$this->createGroup();
		$result = $this->group->deleteGroup($grp_id);
		if($result === TRUE){
		
			$message = "Group Deleted";
		
			$this->response["success"] = 1;
			$this->response["success_msg"] = $message;
			
			return json_encode($this->response);
		}else{
			$this->response["error"] = 1;
			$this->response["error_msg"] = $result;
			return json_encode($this->response);
		}
	}//end of function
	
	function getGroupList($user_id,$type,$item_id){
		$this->createGroup();
		return json_encode($this->group->getGroupList($user_id,$type,$item_id));
	}//end of function
	
	function getJoinedGroupList($user_id,$m_id){
		$this->createGroup();
		return json_encode($this->group->getJoinedGroupList($user_id,$m_id));
	}//end of function
	
	function getGroup($grp_id,$user_id){
		$this->createGroup();
		return json_encode($this->group->getGroup($grp_id,$user_id));
	}//end of function
	
	function joinGroup($grp_id,$user_id,$memberType){
		$this->createGroup();
		$result = $this->group->addMember($grp_id,$user_id,$memberType);
		if($result === TRUE){
		
			$message = "Member Added";
		
			$this->response["success"] = 1;
			$this->response["success_msg"] = $message;
			
			return json_encode($this->response);
		}else{
			$this->response["error"] = 1;
			$this->response["error_msg"] = $result;
			return json_encode($this->response);
		}
	}//end of function
	
	function leaveGroup($grp_id,$user_id){
		$this->createGroup();
		$result = $this->group->removeMember($grp_id,$user_id);
		if($result === TRUE){
		
			$message = "Member Removed";
		
			$this->response["success"] = 1;
			$this->response["success_msg"] = $message;
			
			return json_encode($this->response);
		}else{
			$this->response["error"] = 1;
			$this->response["error_msg"] = $result;
			return json_encode($this->response);
		}
	}//end of function
	
	function getNoficationList($user_id,$n_id){
		$this->createNotify();
		return json_encode($this->notification->getNoficationList($user_id,$n_id));
	}//end of function
	
	function sendNotification($message,$sender,$recipient,$date){
		$this->createNotify();
		$result = $this->notification->sendMessage($message,$sender,$recipient,$date);
		if($result === TRUE){
		
			$message = "Message Sent";
		
			$this->response["success"] = 1;
			$this->response["success_msg"] = $message;
			
			return json_encode($this->response);
		}else{
			$this->response["error"] = 1;
			$this->response["error_msg"] = $result;
			return json_encode($this->response);
		}
	}//end of function
	
	function deleteNofication($notify_id){
		$this->createNotify();
		$result = $this->notification->deleteNofication($notify_id);
		if($result === TRUE){
		
			$message = "Message Deleted";
		
			$this->response["success"] = 1;
			$this->response["success_msg"] = $message;
			
			return json_encode($this->response);
		}else{
			$this->response["error"] = 1;
			$this->response["error_msg"] = $result;
			return json_encode($this->response);
		}
	}//end of function
	
	function markAsRead($notify_id){
		$this->createNotify();
		$result = $this->notification->markAsRead($notify_id);
		if($result === TRUE){
		
			$message = "Message Marked As Read";
		
			$this->response["success"] = 1;
			$this->response["success_msg"] = $message;
			
			return json_encode($this->response);
		}else{
			$this->response["error"] = 1;
			$this->response["error_msg"] = $result;
			return json_encode($this->response);
		}
	}//end of function
	
	//===============================================
    
	private function createAll()
	{
		$this->createUser();
		$this->createStory();
		$this->createGroup();
		$this->createNotify();
		$this->createNotify();
	}//end of function

	private function createUser(){
		require_once("functions/user/user.php");
		$this->user = new USER_PROFILE();
	}//end of function
	
	private function createStory(){
		//require_once("functions/story/story.php");
		require_once("functions/story/favorite.php");
		$this->story = new USER_FAVORITE_STORY();
	}//end of function
	
	private function createGroup(){
		//require_once("functions/group/group.php");
		require_once("functions/group/group_members.php");
		//$this->group = new USER_GROUP();
		$this->group = new GROUP_MEMBER;
	}//end of function
	
	private function createNotify(){
		require_once("functions/user/notification.php");
		$this->notification = new USER_NOTIFICATION();
		
	}//end of function
}

?>
