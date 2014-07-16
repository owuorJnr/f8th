<?php
/**
 * All Honor and Glory belongs to God Almighty
 * Phil 2:13
 * @since 12th January, 2014
 * @version 2.0
 * 
 */ 
	require_once("database/db_queries.php");
	//require_once("functions/group/group_members.php");
	
	class PROFILE extends DB_QUERIES
	{//inherits all the methods/functions and fields/varibles in class DB_QUERIES

		public function getProfile($viewer_id,$profile_id){
    
			$columns = "u_id,".tblUsers.".".user_id.",fname,lname,gender,country,fav_verse,why,desire";
			$from = tblUsers;
			$leftJoin = tblUserProfiles;
			$on = tblUsers.".".user_id."=".tblUserProfiles.".".user_id;
			$where = tblUsers.".".user_id."='$profile_id'";
			$orderBy = "";
			
			$result = $this->f8h_select_leftJoin($columns,$from,$leftJoin,$on,$where,$orderBy);
			
			if ($result !== FALSE) {
				$person = array("tag" => "get_profile", "success" => 1, "error" => 0);
				
				if ($row = mysql_fetch_assoc($result)){
					$person['user_profile'] = $row;
					
				}//end of while
				
				$views = $this->getProfileViews($profile_id);
				$person['user_profile']['views'] = $views;
				return $person;
			}else{
				return FALSE;
			}
		}//end of function
		
		public function saveProfilePhoto($profile_id,$imgData)
		{
			
			$filename= $profile_id;
			
			$path = "photos/profile_photos/".$filename;
			$fp = fopen($path,'w');
			
			//str_replace(' ','+',$imgData);
			$img = base64_decode($imgData);
			
			$chk = fwrite($fp,$img);
			fclose($fp);
			
			if($chk !== FALSE){
			
				$set = "PHOTO_PATH='$path',updated_at=NOW()";
				$where = "USER_ID='$profile_id'";
				$result = $this->f8h_update(tblUsers,$set,$where);
				if($result === TRUE){
					return TRUE;
				}
			}
			
			return "Oops, something went wrong.\nError uploading photo";
			
		}//end of function
		
		public function getProfilePhoto($profile_id)
		{
			$columns = "PHOTO_PATH";
			$where = "USER_ID = '$profile_id'";
			$result = $this->f8h_selectWhere($columns,tblUsers,$where);
			if($result !== FALSE && $row = mysql_fetch_assoc($result)){
				$path = $row['PHOTO_PATH'];
				$imgStr = "";
				$imgStr .= base64_encode(file_get_content($path));
				
				return $imgStr;
			}
			
			return FALSE;
			
		}//end of function
		
		
		public function saveProfile($profile_id,$why,$desire,$fav_verse){
			$time = new DateTime();
			$time = date("Y-m-d h:m:s");
			
			//save new story
			$into = tblUserProfiles;
			$columns = "USER_ID,FAV_VERSE,WHY,DESIRE,CREATED_AT,UPDATED_AT";
			$values = "'$profile_id','$fav_verse','$why','$desire','$time','$time'";
			
			$result = $this->f8h_insert($into,$columns,$values);
			// check for successful store
			if ($result !== FALSE) {
				// get user details
				$email = $this->getUserEmail($profile_id);
				$to = $email;
				$subject = "F8th: Profile Update";
				$message = "Hello ".$this->getUserFname($email).",\n\n
							Thank you for sharing your story.\n
							We hope it will inspire someone :-)\n\n\n
							Regards,\nAdministrator";
							
				$from = "admin@sharemiale.info.ke";
				$headers = "From:" . $from;
				mail($to,$subject,$message,$headers);
				
				return TRUE;
			}else {
				//echo "Not saved<br>";
				return FALSE;
			}
			
		}//end of function 
		
		
		public function  updateProfile($profile_id,$why,$desire,$fav_verse){
			//update story
			
			$set = "FAV_VERSE='$fav_verse',WHY='$why',DESIRE='$desire',UPDATED_AT=NOW()";
			$where = "USER_ID='$profile_id'";
			$result = $this->f8h_update(tblUserProfiles,$set,$where);
			// check for successful update
			//echo "updated<br>";
			return $result; 
		}//end of function 
		
		public function deleteProfile($profile_id){
			
			$where = "USER_ID='$profile_id'";
			$result = $this->f8h_delete(tblUserProfiles,$where);
			
			return $result;
		}//end of function
		 
		
		public function getProfileList($type,$item_id){
			
			$list = array("tag" => "get_profiles", "success" => 0, "error" => 0);
			
			if($type === FIRST_TIME){//first time
				$id = mysql_fetch_assoc(mysql_query("SELECT MAX(U_ID) AS U_ID FROM tblUsers"));
				$item_id = $id['U_ID'];
				
				$where = uid." <= $item_id";
				$error = "no new profiles";
				
			}else if($type === RECENT){//recent
			//receives last recent item_id
				//$where = uid." > $item_id";
				
				$id = mysql_fetch_assoc(mysql_query("SELECT MAX(U_ID) AS U_ID FROM tblUsers"));
				$item_id = $id['U_ID'];
				$where = uid." <= $item_id";
				$error = "No new groups";
				
			}else if($type === LOADMORE){//loadMore
			//receives last old item_id
				$where = uid." < $item_id";
				$error = "All profiles Fetched";
			}else{
				$where = "";
				$error = "Unknown Request";
			}
		
			$columns = uid.",".user_id.",".fname.",".lname.",".gender.",".country;
			$from = tblUsers;
			$order_by = "U_ID DESC";
			$limit = "20";
			
			$result = $this->f8h_select($columns,$from,$where,$order_by,$limit);
			
			if ($result !== FALSE) {
				
				$list['success'] = 1;
				$list['error'] = 0;
				while ($row = mysql_fetch_assoc($result)){
					$list['profiles'][] = $row;
					
				}//end of while
			
				return $list;
				
			}else{
				$list['success'] = 0;
				$list['error'] = 1;
				$list['error_msg'] = $error;
				return $list;
			}
		}//end of function
		
		public function searchProfiles($category,$searchKey){
			$list = array("tag" => "search_stories", "success" => 0, "error" => 1,"error_msg"=>"unknown error");
				
			if($category == "Name"){
				
				$names = explode(",",$searchKey);
				$fname = $names[0];
				$lname = $names[1];
				
				if($lname == ""){
						
					$columns = email.",".fname.",".lname.",".gender.",".country.",".tblUserProfiles.".".
							updated_at.",".P_ID.",".tblUserProfiles.".".user_id;
					$from = tblUsers;
					$innerJoin = tblUserProfiles;
					$on = tblUserProfiles.".".user_id."=".tblUsers.".".user_id;
					$where = fname."='$fname' OR ".lname."='$fname'";
					$orderBy = "";
							
					
					
				}else{
					
					$columns = email.",".fname.",".lname.",".gender.",".country.",".tblUserProfiles.".".
							updated_at.",".P_ID.",".tblUserProfiles.".".user_id;
					$from = tblUsers;
					$innerJoin = tblUserProfiles;
					$on = tblUserProfiles.".".user_id."=".tblUsers.".".user_id;
					$where = fname."='$fname' OR ".fname."='$lname' OR ".lname."='$fname' OR ".lname."='$lname'";
					$orderBy = "";
				}
				
			}else if($category == "Country"){
					
				$columns = email.",".fname.",".lname.",".gender.",".country.",".tblUserProfiles.".".
					updated_at.",".P_ID.",".tblUserProfiles.".".user_id;
				$from = tblUsers;
				$innerJoin = tblUserProfiles;
				$on = tblUserProfiles.".".user_id."=".tblUsers.".".user_id;
				$where = country."= '$searchKey'";
				$orderBy = "";
					
			}else{
				$list['success'] = 0;
				$list['error'] = 1;
				$list['error_msg'] = "unknown search category: ".$category;
				return $list;
			}//end of first if-else-if
				
			$result = $this->f8h_select_innerJoin($columns,$from,$innerJoin,$on,$where,$orderBy);
			//check for result
			if ($result !== FALSE) {
			
				$list['success'] = 1;
				while ($row = mysql_fetch_assoc($result)){
					$list['profiles'][] = $row;
			
				}//end of while
			
				return $list;
			}else{
				$list['success'] = 0;
				$list['error'] = 1;
				$list['error_msg'] = "no results found,\nchange 'Search By'";
				return $list;
			}//end of second if-else
				
		}//end of method
	
	//=====================================================================================================
		
		public function checkProfile($email){
		
			$columns = ALL;
			$where = "email= '$email'";
			$result = $this->f8h_selectWhere($columns,tblUsers,$where);
			if($result !== FALSE){
				//user exits
				if($row = mysql_fetch_assoc($result)){
					$profile_id = $row['USER_ID'];
				}
				
				//check if user has ever saved story
				$result1 = $this->profileSaved($profile_id);
				if($result1 !== FALSE){
				//return TRUE with unique id, to update story
					$exist = array("msg"=>"","USER_ID"=>$profile_id);
					return $exist;
				}else{
				//return FALSE with unique id, to insert new story
					$exist = array("msg"=>"Please update your Profile","USER_ID"=>$profile_id);
					return $exist;
				}//end of second if-else
				
			}else{
				//user does not exist/incorrect email
				return FALSE;
			}
		}//end of function
		
		public function profileSaved($profile_id){
			//check if user has ever saved story
			$columns = ALL;
			$where = "USER_ID = '$profile_id'";
			$result = $this->f8h_selectWhere($columns,tblUserProfiles,$where);
			if($result !== FALSE){
			
				return TRUE;
			}else{
			
				return FALSE;
			}
			
		}//end of function
		
		public function addProfileView($profile_id)
		{
			$time = new DateTime();
			$time = date("Y-m-d h:m:s");
			
			$views = $this->getProfileViews($profile_id);
			$views = $views + 1;
			
			if($views == 1){
				//save first
				$into = tblProfileViews;
				$columns = "USER_ID,VIEWS,CREATED_AT";
				$values = "'$profile_id','$views','$time'";
				$result = $this->f8h_insert($into,$columns,$values);
				
				return $result;
			}else{	
				//update
				$set = "VIEWS='$views',UPDATED_AT=NOW()";
				$where = "USER_ID='$profile_id'";
				$result = $this->f8h_update(tblProfileViews,$set,$where);
				
				return $result;
			}
		}//end of function
		
		private function getProfileViews($profile_id)
		{
			$views = 0;
			$columns = ALL;
			$where = "USER_ID = '$profile_id'";
			$result = $this->f8h_selectWhere($columns,tblProfileViews,$where);
			if($result !== FALSE){
				if($row = mysql_fetch_assoc($result)){
					$cViews = $row['VIEWS'];
					if($cViews > 0){
						$views = $cViews;
					}
				}
			}
			return $views;
		}//end of function
	}	

?>