<?php
 /**
 * All Honor and Glory belongs to God Almighty
 * Phil 2:13
 * @since 12th January, 2014
 * @version 1.0
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
if (isset($_GET['tag']) && $_GET['tag'] != '') {
    // get tag
    $tag = $_GET['tag'];
 
    // include db handler
    require_once("share_functions.php");
    $sf = new SHARE_FUNCTIONS();
    
 
    // response Array
   $response = array("tag" => $tag, "success" => 0, "error" => 0);
 
    // check for tag type
    if($tag == "test"){
	echo $sf->test($tag);
	
    }else if($tag == "authenticate"){
    	$email = $_GET['email'];
    	$password = $_GET['password'];
    	$check = $sf->isUserAuthenticated($email, $password);
    	
    	if($check){
    		if($check == "correct"){
    		//check user
    		$user = $sf->authenticateUser($email, $password);
	    	if($user){
				// user found
			    // echo json with success = 1
			    $response["success"] = 1;
			    
			    $story = $sf->checkStory($email);
			    if($story['exist'] == true){
				$response["success_msg"] = "authentication successful";
			    }else if($story['exist'] == false){
				$response["success_msg"] = "authentication successful\nplease update your story";
			    }
			    echo json_encode($response);
			}else{
				// user not found
			    // echo json with error = 1
			    $response["error"] = 1;
			    $response["error_msg"] = "server error: unable to authenticate details\ntry again";
			    echo json_encode($response);
			}
    		}else{
    			// user authentication failed
	            $response["error"] = 1;
	            $response["error_msg"] = "authentication error: incorrect password";
	            echo json_encode($response);
    		}
    	}else{
    		// user authentication failed
            $response["error"] = 2;
            $response["error_msg"] = "authentication error: incorrect email\nor user does not exist";
            echo json_encode($response);
    	}
    	
    }else if($tag == "signup"){
    	$email = $_GET['email'];
    	$fname = $_GET['fname'];
    	$lname = $_GET['lname'];
    	$gender = $_GET['gender'];
    	$country = $_GET['country'];
    	$password = $_GET['password'];
    	
    	//check if user is already registered
    	if($sf->isUserAlreadyExisting($email)){
    		// user is already existed - error response
            $response["error"] = 2;
            $response["error_msg"] = "User already existing";
            echo json_encode($response);
    	}else{
    		//save user details in database
    		$user = $sf->signUpUser($email, $fname, $lname, $gender, $country, $password);
    		if($user){
    			// user stored successfully
                $response["success"] = 1;
                $response["success_msg"] = "Registration successful\nplease sign in";
                echo json_encode($response);
    		}else{
    			// user failed to store
                $response["error"] = 1;
                $response["error_msg"] = "Error occured in Registartion\nremove any apostrophes in your email\nand try again";
                echo json_encode($response);
    		}
    	}
    	
    	
    }else if($tag == "detect_changes"){
	$last_update_time = $_GET['last_update_time'];
	$uid = $_GET['uid'];

	//check for any changes(updates) in the retrieved list
	$check = $sf->isListUpdated($last_update_time,$uid);
	if($check){

	  if($check == "updated"){
	    //changes detected
	    $response["success"] = 1;
        $response["success_msg"] = "changes detected";
        echo json_encode($response);
	  }else if($check == "not_updated"){
	    //no changes detected
	    $response["success"] = 2;
        $response["success_msg"] = "no changes detected";
        echo json_encode($response);
	  }

	}else{
	  
	  $response["error"] = 1;
      $response["error_msg"] = "server error: unable to detect changes";
      echo json_encode($response);
	}

    }else if($tag == "get_stories"){
	$uid = $_GET['uid'];
	$direction = $_GET['direction'];
	$limit = $_GET['limit'];
	
   	$stories = array();
	$stories = $sf->getRawStoryList($uid,$direction,$limit);
	if($stories){
		echo json_encode($stories);
	}else{
	    // error retrieving list
            $response["error"] = 1;
            $response["error_msg"] = "server error: unable to get stories";
            echo json_encode($response);
	}

     }else if($tag == "search_stories"){
	$category = $_GET['category'];
	$searchKey = $_GET['searchKey'];
	
	$stories = array();
	$stories = $sf->searchStories($category,$searchKey);
	if($stories){
		echo json_encode($stories);
	}else{
	    // error retrieving list
            $response["error"] = 1;
            $response["error_msg"] = "server error: unable obtain search results";
            echo json_encode($response);
	}


    }else if($tag == "get_mystory"){
    	$email = $_GET['email'];
    	$password = $_GET['password'];
    	$authenticated = $sf->isUserAuthenticated($email, $password);
    	
    if($authenticated){
    		if($authenticated == "correct"){
    			//check if user already saves story
    			$check = $sf->checkStory($email);
    			if($check['exist'] == true){
    				$uuid = $check['unique_id'];
    				$story = array();
    				$story = $sf->getMyStory($uuid);
	    			if($story){
		    			// user story updated successfully
		                echo json_encode($story);
		    		}else{
		    			//error updating story
		                $response["error"] = 1;
		                $response["error_msg"] = "server error: unable to retrieve story\ntry again";
		                echo json_encode($response);
		    		}
    				
    			}else if($check['exist'] == false){
				    //error updating story
		            $response["error"] = 1;		                
		            $response["error_msg"] = "null error: please update your story";
				    echo json_encode($response);
    				
    			}else{
    				//user does not exist/incorrect email, will be sorted below
    			}
	    	}else{
    			// user authentication failed
	            $response["error"] = 1;
	            $response["error_msg"] = "authentication error: incorrect password";
	            echo json_encode($response);
    		}
    	}else{
    		// user authentication failed
            $response["error"] = 2;
            $response["error_msg"] = "authentication error: incorrect email\nor user does not exist";
            echo json_encode($response);
    	}

    }else if($tag == "reset_password"){
    	$email = $_GET['email'];
    	$currentPassword = $_GET['currentPassword'];
    	$newPassword = $_GET['newPassword'];
    	//authenticate user
    	$check = $sf->isUserAuthenticated($email, $currentPassword);
    	
    	if($check){
    		if($check == "correct"){
	    		$user = $sf->resetPassword($email, $newPassword);
	    		if($user){
	    			// password reset
	            	$response["success"] = 1;
	            	$response["success_msg"] = "password changed";
	            	$response["password"] = $newPassword;
	           		echo json_encode($response);
	    		}else{
	    			// error resetting password
	            	$response["error"] = 1;
	            	$response["error_msg"] = "server error: unable to reset password\ntry again";
	            	echo json_encode($response);
	    		}
    		}else{
    			// user authentication failed
	            $response["error"] = 1;
	            $response["error_msg"] = "authentication error: incorrect password";
	            echo json_encode($response);
    		}
    	}else{
    		// user authentication failed
            $response["error"] = 2;
            $response["error_msg"] = "authentication error: incorrect email\nor user does not exist";
            echo json_encode($response);
    	}
    	
    	
    }else if($tag == "forgot_password"){
    	$email = $_GET['email'];
    	if($sf->isUserAlreadyExisting($email)){
    		$user = $sf->forgotPassword($email);
    		if($user){
    			// password reset and sent to email
            	$response["success"] = 1;
            	$response["success_msg"] = "new password sent to: ".$email;
           		echo json_encode($response);
    		}else{
    			// error resetting and sending password
            	$response["error"] = 1;
            	$response["error_msg"] = "server error: unable to reset password\ntry again";
            	echo json_encode($response);
    		}
    		
    	}else{
    		// user not existing
            $response["error"] = 2;
            $response["error_msg"] = "email not found in records.\nretype email";
            echo json_encode($response);
    	}
    	
    }else if($tag == "update_details"){
    	$email = $_GET['email'];
    	$new_email = $_GET['new_email'];
    	$fname = $_GET['fname'];
    	$lname = $_GET['lname'];
    	$gender = $_GET['gender'];
    	$country = $_GET['country'];
    	$password = $_GET['password'];
    	$check = $sf->isUserAuthenticated($email, $password);
    	
    	if($check){
    		if($check == "correct"){
	    		$user = $sf->updateDetails($email,$new_email, $fname, $lname, $gender, $country);
	    		if($user){
	    			// user details updated successfully
	                $response["success"] = 1;
	                $response["success_msg"] = "details updated successfully";
	                $response["email"] = $new_email;
	                echo json_encode($response);
	    		}else{
	    			// error updating details
	                $response["error"] = 1;
	                $response["error_msg"] = "server error: unable to update details\nremove any apostrophes in your email\nand try again";
	                echo json_encode($response);
	    		}
    		}else{
    			// user authentication failed
	            $response["error"] = 1;
	            $response["error_msg"] = "authentication error: incorrect password";
	            echo json_encode($response);
    		}
    	}else{
    		// user authentication failed
            $response["error"] = 2;
            $response["error_msg"] = "authentication error: incorrect email\nor user does not exist";
            echo json_encode($response);
    	}
    	
    	
	}else if($tag == "update_story"){
    	$email = $_GET['email'];
    	$password = $_GET['password'];
    	$why = $_GET['why'];
    	$desire = $_GET['desire'];
    	$about = $_GET['about'];
    	$authenticated = $sf->isUserAuthenticated($email, $password);
    	
    	if($authenticated){
    		if($authenticated == "correct"){
    			//check if user already saves story
    			$check = $sf->checkStory($email);
    			if($check['exist'] == true){
    				$uuid = $check['unique_id'];
    				$result = $sf->updateStory($uuid, $why, $desire,$about);
	    			if($result){
		    			// user story updated successfully
		                $response["success"] = 1;
		                $response["success_msg"] = "story updated successfully";
		                echo json_encode($response);
		    		}else{
		    			//error updating story
		                $response["error"] = 1;
		                $response["error_msg"] = "server error: unable to update story\ntry again";
		                echo json_encode($response);
		    		}
    				
    			}else if($check['exist'] == false){
    				$uuid = $check['unique_id'];
    				$result = $sf->saveStory($email,$uuid, $why, $desire,$about);
	    			if($result){
			    			// user story saved successfully
			                $response["success"] = 1;
			                $response["success_msg"] = "story saved successfully";
			                echo json_encode($response);
			    		}else{
			    			//error saving story
			                $response["error"] = 1;
			                $response["error_msg"] = "server error: unable to save story\ntry again";
			                echo json_encode($response);
			    		}
    				
    			}else{
    				//user does not exist/incorrect email, will be sorted below
    			}
	    	}else{
    			// user authentication failed
	            $response["error"] = 1;
	            $response["error_msg"] = "authentication error: incorrect password";
	            echo json_encode($response);
    		}
    	}else{
    		// user authentication failed
            $response["error"] = 2;
            $response["error_msg"] = "authentication error: incorrect email\nor user does not exist";
            echo json_encode($response);
    	}
    	
    }else if($tag == "get_animation"){
    	
    	//get animation articles

    }else if($tag == "deregister"){
    	$email = $_GET['email'];
    	$password = $_GET['password'];
    	$authenticated = $sf->isUserAuthenticated($email, $password);
    	
    	if($authenticated){
    		if($authenticated == "correct"){
    			$check = $sf->checkStory($email);
    			if($check){
    				$uuid = $check['unique_id'];
    			}else{
    				$uuid = 0;
    			}
    			
			$name = $sf->getUserFname($email);
	    		$user = $sf->deregister($uuid);
	    		if($user){
	    			// user record deleted
				$to = $email;
				$subject = "Goodbye, from sharefaith";
				$message = "Hi ".$name.",\n\nWe will miss you. We would like to hear from you,\nplease reply to this email if you would like to tell us the cause of your departure.\n\n\nRegards,\nAdministrator";
				$from = "admin@sharemiale.info.ke";
				$headers = "From:" . $from;
				mail($to,$subject,$message,$headers);
				
				$response["success"] = 1;
				$response["success_msg"] = "deregister successful";
	           		echo json_encode($response);
	    		}else{
	    			// error deleting recoed
	            	$response["error"] = 1;
	            	$response["error_msg"] = "server error: unable to deregister\ntry again";
	            	echo json_encode($response);
	    		}
    		}else{
    			// user authentication failed
	            $response["error"] = 1;
	            $response["error_msg"] = "authentication error: incorrect password";
	            echo json_encode($response);
    		}
    	}else{
    		// user authentication failed
            $response["error"] = 2;
            $response["error_msg"] = "authentication error: incorrect email\nor user does not exist";
            echo json_encode($response);
    	}
    	
	}else{
    	echo "Invalid Request";
    }
       
}else {
	
	echo "Access Denied";
	
}//end of first if-else

?>
