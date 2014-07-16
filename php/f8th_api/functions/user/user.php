<?php
/**
 * All Honor and Glory belongs to God Almighty
 * Phil 2:13
 * @since 12th January, 2014
 * @version 2.0
 * 
 */ 
	require_once("profile.php");
	
	class USER_PROFILE extends PROFILE
	{//inherits all the methods/functions and fields/varibles in class DB_QUERIES
	
	
	//====================================
		
		/**
		* user functions
		*/
		public function signInUser($email,$password){
		
			$where = email."='$email'";
			$result = $this->f8h_selectWhere(ALL,tblUsers,$where);
			
			if ($result !== FALSE) {
				$result = mysql_fetch_array($result);
				$salt = $result['SALT'];
				$encrypted_password = $result['ENCRYPTED_PASSWORD'];
				$hash = $this->checkhashSSHA($salt, $password);
				// check for password equality
				if ($encrypted_password == $hash) {
					// user authentication details are correct
					return TRUE;
				}else{
					return "Oops, something went wrong.\nWrong Password.";
				}
			}else{
				return "Oops, something went wrong.\nWrong Email: ".$email."\nor user not registered";
			}
		}//end of function 
		
		public function signUpUser($email,$fname,$lname,$gender,$country,$password){
	
			$user_id = uniqid('', TRUE);
			$hash = $this->hashSSHA($password);
			$encrypted_password = $hash["encrypted"]; // encrypted password
			$salt = $hash["salt"]; // salt
			$status = SIGNUP_STATUS;
			$time = new DateTime();
			$time = date("Y-m-d h:m:s");
			
			$into = tblUsers;
			$columns = user_id.",".email.",".fname.",".lname.",".gender.",".country.",".
					password.",".salt.",STATUS,".created_at;
			$values = "'$user_id','$email','$fname','$lname','$gender','$country', '$encrypted_password','$salt','$status', '$time'";
			
			$result = $this->f8h_insert($into,$columns,$values);
			// check for successful store
			if ($result !== FALSE) {
				
				
				$grp_id = "grp001";
				$member_type = "member";
				
				$into = tblGroupMembers;
				$columns = "GROUP_ID,MEMBER_ID,MEMBER_TYPE,CREATED_AT";
				$values = "'$grp_id','$user_id','$member_type','$time'";
				$result1 = $this->f8h_insert($into,$columns,$values);
				
				$to = $email;
				$subject = "F8th: welcome";
				$message = "Hello ".$fname.",\n\nWelcome to F8th, let's exchange ideas on how to make the world a better place.\n\n\nRegards,\nAdministrator";
				$from = "admin@sharemiale.info.ke";
				$headers = "From:" . $from;
				mail($to,$subject,$message,$headers);
			
				return "Welcome ".$fname.",\nsign up successful";
			} else {
				return "Oops, something went wrong.\nSign up failed.\nremove any apostrophes in your email\nTry Again";
			}
		}//end of function 
		
		public function updateUser($user_id,$new_email,$fname,$lname,$gender,$country){
	
			//$email = $this->getUserEmail($user_id);
			$where = user_id."= '$user_id'";
			$result = $this->f8h_selectWhere(ALL,tblUsers,$where);
			// check for result
			if ($result !== FALSE) {
				$result = mysql_fetch_array($result);
				$user_id = $result['USER_ID'];
			    
				$time = new DateTime();
				$time = date("Y-m-d h:m:s");
			    
				//update details
				$set = "EMAIL='$new_email',FNAME='$fname', LNAME='$lname',
						GENDER='$gender',COUNTRY='$country', UPDATED_AT='$time'";
				$where = "USER_ID='$user_id'";
				$result1 = $this->f8h_update(tblUsers,$set,$where);
				// check for successful update
				if ($result1 !== FALSE) {
					return TRUE;
						
				} else {
					return "Oops, something went wrong.\nProfile not updated.\nTry again";
				}
			}else{
				return "Oops, something went wrong.\nUser not found\nTry again";
			}
        
		}//end of function
		
		
		public function resetPassword($user_id,$newPassword){
			//reset password
			$email = $this->getUserEmail($user_id);
			
			$hash = $this->hashSSHA($newPassword);
			$encrypted_password= $hash["encrypted"]; // encrypted password
			$salt = $hash["salt"]; // salt
			
			
			$set = "ENCRYPTED_PASSWORD='$encrypted_password', SALT='$salt', UPDATED_AT=NOW()";
			$where = "USER_ID='$user_id'";
			$result = $this->f8h_update(tblUsers,$set,$where);
			// check for successful update
			if ($result !== FALSE) {
			
				$to = $email;
				$subject = "F8th: Your password has been reset";
				$message = "Hello ".$this->getUserFname($email).",\n\n
							Your password was changed on".date("Y-m-d h:m:s",time()).".\n\n\n
							Regards,\nAdministrator";
				$from = "admin@sharemiale.info.ke";
				$headers = "From:" . $from;
				mail($to,$subject,$message,$headers);
				
				return "Password Reset Successfully";
			} else {    			
				return "Oops, something went wrong.\nPassword Not Reset.\nTry again";
			}
		
		}//end of function 
		
		
		public function forgotPassword($email){
			$columns = ALL;
			$where = email."= '$email'";
			$result = $this->f8h_selectWhere($columns,tblUsers,$where);
			if($result !== FALSE){
			
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
					
					
				$set = "ENCRYPTED_PASSWORD='$encrypted_password1', SALT='$salt1', UPDATED_AT=NOW()";
				$where = "email='$email'";
				$result1 = $this->f8h_update(tblUsers,$set,$where);
				// check for successful update
				if ($result1 !== FALSE) {
				   
					$to = $email;
					$subject = "F8th: forgot password";
					$message = "Hello ".$this->getUserFname($email).",\n\n
								Your new password: \n".$newPassword."\n\n\n
								Regards,\nAdministrator.";
					$from = "admin@sharemiale.info.ke";
					$headers = "From:" . $from;
					mail($to,$subject,$message,$headers);
					//echo "Mail Sent.";
					return TRUE;
				} else {
					return "Oops, something went wrong.\nPassword Not Reset.\nTry again";
				}
			}else{
				return "Oops, something went wrong.\nIncorrect Email: ".$email."\nTry again";
			}
		}//end of function 
		
		public function deleteUser($user_id){
			//delete user
			$where = "USER_ID='$user_id'";
			$result = $this->f8h_delete(tblUsers,$where);
			if ($result !== FALSE) {
				//delete successful, delete profile
				$result = $this->deleteProfile($user_id);
				if($result === TRUE){
					$email = $this->getUserEmail($user_id);
					$fname = $this->getUserFname($email);
					$to = $email;
					$subject = "F8th: Goodbye";
					$message = "Hello ".$fname.",\n\nWe will miss you. Come back soon.\n\n\nRegards,\nAdministrator";
					$from = "admin@sharemiale.info.ke";
					$headers = "From:" . $from;
					mail($to,$subject,$message,$headers);
					
					return TRUE;
				}else{
					return "Profile Not Deleted";
				}
			}else{
				//delete error
				return "Oops, something went wrong.\nUser Not Deleted.\nTry again";
			}
        
		}//end of function
		
	//=============================================================
		
		public function isUserAlreadyExisting($email){
		
			$columns = "EMAIL";
			$where = email."= '$email'";
			$result = $this->f8h_selectWhere($columns,tblUsers,$where);
			if($result !== FALSE){
				return TRUE;
			}else{	
				return FALSE;
			}
		}//end of function isUserAlreadyExisting()
		
		
		public function isUserAuthenticated($user_id,$password){
			$columns = ALL;
			$where = user_id."= '$user_id'";
			$result = $this->f8h_selectWhere($columns,tblUsers,$where);
			if($result !== FALSE){
				$result = mysql_fetch_array($result);
				$salt = $result['SALT'];
				$encrypted_password = $result['ENCRYPTED_PASSWORD'];
				$hash = $this->checkhashSSHA($salt, $password);
				// check for password equality
				if ($encrypted_password == $hash) {
					// user authentication details are correct
					return "correct";
				}else{
					return "wrong";
				}
			} else {
			    // user not found
			    return FALSE;
			}
		}//end of function

		
		public function getUserFname($email){
			$name  = "";
			$columns = ALL;
			$where = email."= '$email'";
			$result = $this->f8h_selectWhere($columns,tblUsers,$where);
			if($result !== FALSE){
				if($row = mysql_fetch_assoc($result)){
					$name = $row['FNAME'];
				}
			}
			
			return $name;
		}//end of function
		
		public function getUserEmail($user_id)
		{
			$email  = "";
			$columns = ALL;
			$where = user_id."= '$user_id'";
			$result = $this->f8h_selectWhere($columns,tblUsers,$where);
			if($result !== FALSE){
				if($row = mysql_fetch_assoc($result)){
					$email = $row['EMAIL'];
				}
			}
			
			return $email;
		}//end of function
		
		public function getFullNames($user_id)
		{
			$names  = "";
			$columns = ALL;
			$where = user_id."= '$user_id'";
			$result = $this->f8h_selectWhere($columns,tblUsers,$where);
			if($result !== FALSE){
				if($row = mysql_fetch_assoc($result)){
					$names = $row['FNAME']." ".$row['LNAME'];;
				}
			}
			
			return $names;
		}//end of function
		
		/**
		* Encrypting password
		* @param password
		 * returns salt and encrypted password
		 */
		public function hashSSHA($password) {
		 
			$salt = sha1(rand());
			$salt = substr($salt, 0, 10);
			$encrypted = base64_encode(sha1($password . $salt, TRUE) . $salt);
			$hash = array("salt" => $salt, "encrypted" => $encrypted);
			return $hash;
		}//end
		 
		/**
		* Decrypting password
		* @param salt, password
		* returns hash string
		*/
		public function checkhashSSHA($salt, $password) {
		 
			$hash = base64_encode(sha1($password . $salt, TRUE) . $salt);
		 
			return $hash;
		}//end
	
	//===============================================================
	
	}//END OF CLASS
 
 ?>