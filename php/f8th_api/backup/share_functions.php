<?php
/**
 * All Honor and Glory belongs to God Almighty
 * Phil 2:13
 * @since 12th January, 2014
 * @version 1.0
 * 
 */ 


	require_once("db_queries.php");
	
class SHARE_FUNCTIONS extends DB_QUERIES
{//inherits all the methods/functions and fields/varibles in class DB_QUERIES
	function test($test)
	{
		//return "You entered: ".$test;
		return $this->sf_test("sharefaith function, ".fname." ".$test);
	}//end of function
	//===============================================
    
    /**
     * user functions
     */
    public function authenticateUser($email,$password){
     $result = mysql_query("SELECT * FROM users WHERE email = '$email'") or die(mysql_error());
        // check for result 
        $no_of_rows = mysql_num_rows($result);
        if ($no_of_rows > 0) {
            $result = mysql_fetch_array($result);
            $salt = $result['salt'];
            $encrypted_password = $result['encrypted_password'];
            $hash = $this->checkhashSSHA($salt, $password);
            // check for password equality
            if ($encrypted_password == $hash) {
                // user authentication details are correct
                return true;
            }else{
            	return false;
            }
        }
    }//end of function 
    
    public function isListUpdated($last_update_time,$uid){
    	
    if($uid == ""){
    	$result = mysql_query("SELECT * FROM stories");
    	$uid = mysql_num_rows($result);
    }
    	$limit = 50;
	$result = mysql_query("SELECT updated_at FROM  stories WHERE sid>=$uid  ORDER BY  stories.created_at DESC LIMIT $limit") or die(mysql_error());
	//check for result
	$no_of_rows = mysql_num_rows($result);
	if ($no_of_rows > 0) {
		while ($row = mysql_fetch_assoc($result)){
		  if(strtotime($row["updated_at"]) > strtotime($last_update_time)){
			return "updated";
		  }//end of inner if
		}//end of while
		
	     //no changes detected
		return "not_updated";
	}else{
	//error getting list
            return false;
	}//end of first if-else

    }//end of function


    public function getMyStory($uuid){
    	$result = mysql_query("SELECT users.email,users.fname,users.lname,users.gender,users.country,stories.about,stories.why,stories.desire,stories.updated_at,stories.sid,stories.unique_id
			FROM stories LEFT JOIN users ON stories.unique_id=users.unique_id WHERE stories.unique_id='$uuid'") or die(mysql_error());
    		
        //checks if user exists
        $no_of_rows = mysql_num_rows($result);
        if ($no_of_rows > 0) {
        	$person = array("tag" => "get_mystory", "success" => 1, "error" => 0);
        	
			while ($row = mysql_fetch_assoc($result)){
			$person['story'] = $row;
			
			}//end of while
        	return $person;
        }else{
        	return false;
        }
    }//end of function
    
    public function  getRawStoryList($uid,$direction,$limit){
    	
	$list = array("tag" => "get_stories", "success" => 0, "error" => 1,"error_msg"=>"unknown error","update_time" =>"","story_id" => "");
	
    	if ($limit == ""){
    		$limit = 15;
    	}
    	
	if($uid == ""){//default, first page
		$result = mysql_query("SELECT sid FROM stories ORDER BY sid DESC LIMIT 1");
		if ($row = mysql_fetch_assoc($result)){
			$uid_start = $row['sid'];//upper limit
			$uid = $uid_start;

			$result1 = mysql_query("SELECT sid FROM stories WHERE sid<=$uid ORDER BY sid DESC LIMIT $limit");
			while ($row1 = mysql_fetch_assoc($result1)){
				$uid_end = $row1['sid'];//lower limit
			}
		
			$result = mysql_query("SELECT users.email,users.fname,users.lname,users.gender,users.country,stories.about,stories.why,stories.desire,stories.updated_at,stories.sid,stories.unique_id
			FROM stories INNER JOIN users ON stories.unique_id=users.unique_id WHERE stories.sid<=$uid_start AND stories.sid>=$uid_end ORDER BY stories.updated_at DESC") or die(mysql_error());
		}else{
			$list['success'] = 0;
			$list['error'] = 1;
			$list['error_msg'] = "no stories in the database";
			return $list;
		}
		
	}else if ($direction == "next"){//fetch 15 lower rows below uid, next page and refresh page
	
		$result = mysql_query("SELECT sid FROM stories WHERE sid<=$uid ORDER BY sid DESC LIMIT $limit");
		while ($row = mysql_fetch_assoc($result)){
			$uid_end = $row['sid'];//lower limit
		}
		
		if($uid == $uid_end){//no more records to fetch, next has reached last page
			$list['success'] = 0;
			$list['error'] = 1;
			$list['error_msg'] = "last page";
			return $list;
		}else{
			$uid_start = $uid;//upper limit
		}
		
    		$result = mysql_query("SELECT users.email,users.fname,users.lname,users.gender,users.country,stories.about,stories.why,stories.desire,stories.updated_at,stories.sid,stories.unique_id
			FROM stories INNER JOIN users ON stories.unique_id=users.unique_id WHERE stories.sid<=$uid_start AND stories.sid>=$uid_end ORDER BY stories.updated_at DESC") or die(mysql_error());
    		
    	}else if ($direction == "previous"){//fetch 15 upper rows above uid,previous page
	
		$result = mysql_query("SELECT sid FROM stories WHERE sid>=$uid ORDER BY sid ASC LIMIT $limit");
		while ($row = mysql_fetch_assoc($result)){
			$uid_start= $row['sid'];//upper limit
		}
		
		if($uid_start == $uid){//no more records,previous has reached first page
			$list['success'] = 0;
			$list['error'] = 1;
			$list['error_msg'] = "first page";
			return $list;
		}else{
			$uid_end = $uid;
		}
			
    		$result = mysql_query("SELECT users.email,users.fname,users.lname,users.gender,users.country,stories.about,stories.why,stories.desire,stories.updated_at,stories.sid,stories.unique_id
			FROM stories INNER JOIN users ON stories.unique_id=users.unique_id WHERE stories.sid<=$uid_start AND stories.sid>=$uid_end ORDER BY stories.updated_at DESC") or die(mysql_error());
    		
	}else{
		$list['success'] = 0;
		$list['error'] = 1;
		$list['error_msg'] = "unknown list direction request";
		$list['story_id'] = "";
		return $list;
    	}//end of first if-else-if

	//check for result
	$no_of_rows = mysql_num_rows($result);
	if ($no_of_rows > 0) {
		$array_uid = array();
		$array_time = array();
		
		$list['success'] = 1;
		while ($row = mysql_fetch_assoc($result)){
		$list['stories'][] = $row;
			
		$array_uid[] = $row['sid'];
		$array_time[] = $row['updated_at'];
			
		}//end of while
		
		if($no_of_rows == 1){
			$list['update_time'] = $array_time[0];
		}else{
			$list['update_time'] = max($array_time);
		}
			
		if($direction == "previous"){
			$list['story_id'] = $uid_start;//max($array_uid);
		}else{
			$list['story_id'] = $uid_end;//min($array_uid);
		}
	
		return $list;
		//return json_encode($list);
	}else{
		$list['success'] = 0;
		$list['error'] = 1;
		$list['error_msg'] = "unable to match records";
		$list['story_id'] = "";
		return $list;
	}//end of second if-else
	
    }//end of function 
    
	public function searchStories($category,$searchKey){
		$list = array("tag" => "search_stories", "success" => 0, "error" => 1,"error_msg"=>"unknown error");
		
		if($category == "Name"){
		
			$names = explode(",",$searchKey);
			$fname = $names[0];
			$lname = $names[1];
			
			if($lname == ""){
				$result = mysql_query("SELECT users.email,users.fname,users.lname,users.gender,users.country,stories.about,stories.why,stories.desire,stories.updated_at,stories.sid,stories.unique_id
				FROM users INNER JOIN stories ON users.unique_id=stories.unique_id WHERE users.fname='$fname' OR users.lname='$fname'") or die(mysql_error());
			}else{
				$result = mysql_query("SELECT users.email,users.fname,users.lname,users.gender,users.country,stories.about,stories.why,stories.desire,stories.updated_at,stories.sid,stories.unique_id
				FROM users INNER JOIN stories ON users.unique_id=stories.unique_id WHERE users.fname='$fname' OR users.fname='$lname' OR users.lname='$fname' OR users.lname='$lname'") or die(mysql_error());
			}
	
		}else if($category == "Country"){
			$result = mysql_query("SELECT users.email,users.fname,users.lname,users.gender,users.country,stories.about,stories.why,stories.desire,stories.updated_at,stories.sid,stories.unique_id
			FROM users INNER JOIN stories ON users.unique_id=stories.unique_id WHERE users.country = '$searchKey'") or die(mysql_error());
		}else{
			$list['success'] = 0;
			$list['error'] = 1;
			$list['error_msg'] = "unknown search category: ".$category;
			return $list;
		}//end of first if-else-if
		
		//check for result
		$no_of_rows = mysql_num_rows($result);
		if ($no_of_rows > 0) {
		
			$list['success'] = 1;
			while ($row = mysql_fetch_assoc($result)){
			$list['stories'][] = $row;
		
			}//end of while
		
			return $list;
		}else{
			$list['success'] = 0;
			$list['error'] = 1;
			$list['error_msg'] = "no results found,\nchange 'Search By'";
			return $list;
		}//end of second if-else
		
	}//end of method
    
	public function signUpUser($email,$fname,$lname,$gender,$country,$password){
	  	$uuid = uniqid('', true);
        $hash = $this->hashSSHA($password);
        $encrypted_password = $hash["encrypted"]; // encrypted password
        $salt = $hash["salt"]; // salt
        $time = new DateTime();
	$time = date("Y-m-d h:m:s");
        $result = mysql_query("INSERT INTO users(unique_id,email,fname,lname,gender,country,encrypted_password,salt,created_at) VALUES ('$uuid','$email','$fname','$lname','$gender','$country', '$encrypted_password','$salt', '$time')");
        // check for successful store
        if (mysql_affected_rows() > 0) {
	
		$to = $email;
		$subject = "Welcome to sharefaith";
		$message = "Hi ".$fname.",\n\nWe are eager to hear your story.\n\n\nRegards,\nAdministrator";
		$from = "admin@sharemiale.info.ke";
		$headers = "From:" . $from;
		mail($to,$subject,$message,$headers);
	
		return true;
        } else {
		return false;
        }
	}//end of function 
	
	public function updateDetails($email,$new_email,$fname,$lname,$gender,$country){
		$result = mysql_query("SELECT * FROM users WHERE email = '$email'") or die(mysql_error());
        // check for result
        $no_of_rows = mysql_num_rows($result);
        if ($no_of_rows > 0) {
            $result = mysql_fetch_array($result);
	    	$uuid = $result['unique_id'];
            
	    	$time = new DateTime();
	   		$time = date("Y-m-d h:m:s");
	    
            //update details
        	$result1 = mysql_query("UPDATE users SET email='$new_email',fname='$fname', lname='$lname',gender='$gender',country='$country', updated_at='$time' WHERE unique_id='$uuid'");
       		// check for successful update
			if (mysql_affected_rows() > 0) {
				
           		$result2 = mysql_query("SELECT * FROM users WHERE unique_id = $uuid");
           		//return mysql_fetch_array($result2);
				return true;
   			} else {
           		return false;
        	}
        }
        
	}//end of function
	
	public function saveStory($email,$uuid,$why,$desire,$about){
	    $time = new DateTime();
	    $time = date("Y-m-d h:m:s");
		
		//save new story
	  $result = mysql_query("INSERT INTO stories(unique_id,about,why,desire,created_at,updated_at) VALUES ('$uuid','$about','$why','$desire','$time','$time')") or die(mysql_error());
        // check for successful store
        if (mysql_affected_rows() > 0) {
            // get user details
		
		$to = $email;
		$subject = "We got your story";
		$message = "Hi ".$this->getUserFname($email).",\n\nThank you for sharing your story.\nWe hope it will inspire someone :-)\n\n\nRegards,\nAdministrator";
		$from = "admin@sharemiale.info.ke";
		$headers = "From:" . $from;
		mail($to,$subject,$message,$headers);
		
		return true;
        }else {
		return false;
        }

	}//end of function 
	
	public function  updateStory($uuid,$why,$desire,$about){
	    //update story
	    $result = mysql_query("UPDATE stories SET about='$about',why='$why',desire='$desire',updated_at=NOW() WHERE unique_id='$uuid'") or die(mysql_error());	
        // check for successful update
	if (mysql_affected_rows() > 0) {	
            return true;
       	} else {
           	return false;
        }  
	}//end of function 
	
	public function resetPassword($email,$newPassword){
       //reset password
		$hash = $this->hashSSHA($newPassword);
   		$encrypted_password= $hash["encrypted"]; // encrypted password
       	$salt = $hash["salt"]; // salt
        $result = mysql_query("UPDATE users SET encrypted_password='$encrypted_password', salt='$salt', updated_at=NOW() WHERE email='$email'");
        // check for successful update
	if (mysql_affected_rows() > 0) {
	
		$to = $email;
		$subject = "sharefaith: Your password has been reset";
		$message = "Hi ".$this->getUserFname($email).",\n\nYour password has been changed.\n\n\nRegards,\nAdministrator";
		$from = "admin@sharemiale.info.ke";
		$headers = "From:" . $from;
		mail($to,$subject,$message,$headers);
		
		return true;
       	} else {    			
       		return false;
        }
		
	}//end of function 
	
	public function forgotPassword($email){
		$result = mysql_query("SELECT email from users WHERE email = '$email'");
        $no_of_rows = mysql_num_rows($result);
        if ($no_of_rows > 0) {
	
		$datestart = strtotime('2013-12-14');//you can change it to your timestamp;
		$dateend = strtotime('2023-12-31');//you can change it to your timestamp;
		$daystep = 86400;
		$datebetween = abs(($dateend - $datestart) / $daystep);
		$randomday = rand(0, $datebetween);
	
		$newPassword = "pass".$result['fname']."".$randomday ."". date("Ymd", $datestart + ($randomday * $daystep));
		//reset password
		$hash1 = $this->hashSSHA($newPassword);
		$encrypted_password1 = $hash1["encrypted"]; // encrypted password
		$salt1 = $hash1["salt"]; // salt
		$result1 = mysql_query("UPDATE users SET encrypted_password='$encrypted_password1', salt='$salt1', updated_at=NOW() WHERE email='$email'");
		// check for successful update
		if (mysql_affected_rows() > 0) {
           
			$to = $email;
			$subject = "sharefaith: forgot password";
			$message = "Hi ".$this->getUserFname($email).",\n\nYour new password: \n".$newPassword."\n\n\nRegards,\nAdministrator.";
			$from = "admin@sharemiale.info.ke";
			$headers = "From:" . $from;
			mail($to,$subject,$message,$headers);
			//echo "Mail Sent.";
			return true;
		} else {
			return false;
        	}
        }
	
	}//end of function 
	
	public function getAnimationContent($item){
		
		//get animation articles
		
	}//end of function 
	
	public function deregister($uuid){
        //delete record
		$result = mysql_query("DELETE FROM users WHERE unique_id='$uuid'");
		if(mysql_affected_rows() > 0){
			//delete successful
			$result = mysql_query("DELETE FROM stories WHERE unique_id='$uuid'");
			if(mysql_affected_rows() > 0){
				return true;
			}
		}else{
			//delete error
			return false;
		}
        
	}//end of function
	
	/**
	 * end of user functions
	 */
 
	public function isUserAlreadyExisting($email){
	 $result = mysql_query("SELECT email from users WHERE email = '$email'");
        $no_of_rows = mysql_num_rows($result);
        if ($no_of_rows > 0) {
            // user existed 
            return true;
        } else {
            // user not existed
            return false;
        }
	}//end of function isUserAlreadyExisting()
	
	 public function isUserAuthenticated($email,$password){
     $result = mysql_query("SELECT * FROM users WHERE email = '$email'") or die(mysql_error());
        // check for result 
        $no_of_rows = mysql_num_rows($result);
        if ($no_of_rows > 0) {
            $result = mysql_fetch_array($result);
            $salt = $result['salt'];
            $encrypted_password = $result['encrypted_password'];
            $hash = $this->checkhashSSHA($salt, $password);
            // check for password equality
            if ($encrypted_password == $hash) {
                // user authentication details are correct
                return "correct";
            }else{
            	return "wrong";
            }
        } else {
            // user not found/incorrect email
            return false;
        }
    }//end of function 
    
    
    public function getUserFname($email){
	$name  = "";
	$result = mysql_query("SELECT * FROM users WHERE email = '$email'") or die(mysql_error());
        //checks if user exists
        $no_of_rows = mysql_num_rows($result);
        if ($no_of_rows > 0) {
		if($row = mysql_fetch_assoc($result)){
	    		$name = $row['fname'];
		}
	}
	
	return $name;
    }//end of function
    
    public function checkStory($email){
    	$result = mysql_query("SELECT * FROM users WHERE email = '$email'") or die(mysql_error());
        //checks if user exists
        $no_of_rows = mysql_num_rows($result);
        if ($no_of_rows > 0) {
        	//user exits
            while($row = mysql_fetch_assoc($result)){
	    		$uuid = $row['unique_id'];
            }
	    	
	    	//check if user has ever saved story
	    	$result1 = mysql_query("SELECT * FROM stories WHERE unique_id = '$uuid'") or die(mysql_error());
	    	$no_of_rows = mysql_num_rows($result1);
	    	if($no_of_rows > 0) {
	    	//return true with unique id, to update story
	    		$exist = array("exist"=>true,"unique_id"=>$uuid);
	    		return $exist;
	    	}else{
	    	//return false with unique id, to insert new story
	    		$exist = array("exist"=>false,"unique_id"=>$uuid);
	    		return $exist;
	    	}//end of second if-else
	    	
        }else{
        	//user does not exist/incorrect email
        	return false;
        }
    }//end of function
	
  /**
     * Encrypting password
     * @param password
     * returns salt and encrypted password
     */
    public function hashSSHA($password) {
 
        $salt = sha1(rand());
        $salt = substr($salt, 0, 10);
        $encrypted = base64_encode(sha1($password . $salt, true) . $salt);
        $hash = array("salt" => $salt, "encrypted" => $encrypted);
        return $hash;
    }
 
    /**
     * Decrypting password
     * @param salt, password
     * returns hash string
     */
    public function checkhashSSHA($salt, $password) {
 
        $hash = base64_encode(sha1($password . $salt, true) . $salt);
 
        return $hash;
    }
	
} 

?>
