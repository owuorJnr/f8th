<?php

public function  getProfileList($uid,$direction,$limit){
    	
			$list = array("tag" => "get_profiles", "success" => 0, "error" => 1,"error_msg"=>"unknown error","update_time" =>"","profile_id" => "");
			
			$id = mysql_fetch_assoc(mysql_query("SELECT MAX(P_ID) AS P_ID FROM tblUserProfiles"));
			echo $id['P_ID'];
			return $id['P_ID'];
			
			if ($limit == ""){
				$limit = 15;
			}
			
			if($uid == ""){//default, first page
				
				$columns = "P_ID";
				$from = tblUserProfiles;
				$where = "";
				$orderBy = "P_ID DESC ";
				$limit = "1";
					
				$result = $this->sf_select($columns,$from,$where,$orderBy,$limit);
					
				if ($row = mysql_fetch_assoc($result)){
					$uid_start = $row['P_ID'];//upper limit
					$uid = $uid_start;
					
					$columns = "P_ID";
					$from = tblUserProfiles;
					$where = "P_ID<=$uid";
					$orderBy = "P_ID ASC ";
					$limit = "1";
					
					$result1 = $this->sf_select($columns,$from,$where,$orderBy,$limit);
					while ($row1 = mysql_fetch_assoc($result1)){
						$uid_end = $row1['P_ID'];//lower limit
					}
					
				}else{
					$list['success'] = 0;
					$list['error'] = 1;
					$list['error_msg'] = "no user profiles found in the database";
					return $list;
				}
				
			}else if ($direction == "next"){//fetch 15 lower rows below uid, next page and refresh page
			
				$columns = "P_ID";
				$from = tblUserProfiles;
				$where = "P_ID<=$uid";
				$orderBy = "P_ID DESC ";
				$limit = "1";
					
				$result = $this->sf_select($columns,$from,$where,$orderBy,$limit);
				
				while ($row = mysql_fetch_assoc($result)){
					$uid_end = $row['P_ID'];//lower limit
				}
				
				if($uid == $uid_end){//no more records to fetch, next has reached last page
					$list['success'] = 0;
					$list['error'] = 1;
					$list['error_msg'] = "last page";
					return $list;
				}else{
					$uid_start = $uid;//upper limit
				}
				
				
			}else if ($direction == "previous"){//fetch 15 upper rows above uid,previous page
			
				$columns = "P_ID";
				$from = tblUserProfiles;
				$where = "P_ID>=$uid";
				$orderBy = "P_ID ASC ";
				$limit = "1";
					
				$result = $this->sf_select($columns,$from,$where,$orderBy,$limit);
				
				while ($row = mysql_fetch_assoc($result)){
					$uid_start= $row['P_ID'];//upper limit
				}
				
				if($uid_start == $uid){//no more records,previous has reached first page
					$list['success'] = 0;
					$list['error'] = 1;
					$list['error_msg'] = "first page";
					return $list;
				}else{
					$uid_end = $uid;
				}
					
			}else{
				$list['success'] = 0;
				$list['error'] = 1;
				$list['error_msg'] = "unknown list direction request";
				$list['profile_id'] = "";
				return $list;
			}//end of first if-else-if

			$columns = email.",".fname.",".lname.",".gender.",".country.",".tblUserProfiles.".".
						updated_at.",".pid.",".tblUserProfiles.".".user_id;
			$from = tblUserProfiles;
			$innerJoin = tblUsers;
			$on = tblUserProfiles.".".user_id."=".tblUsers.".".user_id;
			$where = pid."<=$uid_start AND ".pid.">=$uid_end";
			$orderBy = tblUserProfiles.".".updated_at." DESC";
					
			$result = $this->sf_select_innerJoin($columns,$from,$innerJoin,$on,$where,$orderBy);
			$no_of_rows = mysql_affected_rows();
			//check for result
			if ($result !== FALSE) {
				$array_uid = array();
				$array_time = array();
				
				$list['success'] = 1;
				while ($row = mysql_fetch_assoc($result)){
				$list['profiles'][] = $row;
					
				$array_uid[] = $row['P_ID'];
				$array_time[] = $row['updated_at'];
					
				}//end of while
				
				if($no_of_rows == 1){
					$list['update_time'] = $array_time[0];
				}else{
					$list['update_time'] = max($array_time);
				}
					
				if($direction == "previous"){
					$list['profile_id'] = $uid_start;//max($array_uid);
				}else{
					$list['profile_id'] = $uid_end;//min($array_uid);
				}
			
				return $list;
				//return json_encode($list);
			}else{
				$list['success'] = 0;
				$list['error'] = 1;
				$list['error_msg'] = "unable to match records";
				$list['profile_id'] = "";
				return $list;
			}//end of second if-else
			
		    }//end of function 

  public function isListUpdated($last_update_time,$uid){
    	
    if($uid == ""){
    	//$result = mysql_query("SELECT * FROM stories");
	$result = $this->sf_selectAll(tblUserProfiles);
	if($result !== FALSE){
		$uid = mysql_num_rows($result);
	}
    }
    	$limit = 50;
	$where = sid.">=$uid";
	$orderBy = tblUserProfiles.created_at." DESC";
	$result = $this->sf_select(updated_at,tblUserProfiles,$where,$orderBy,$limit);
	//check for result
	//$no_of_rows = mysql_num_rows($result);
	if ($result !== FALSE) {
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


    
    
    public function  getRawProfileList($uid,$direction,$limit){
    	
	$list = array("tag" => "get_stories", "success" => 0, "error" => 1,"error_msg"=>"unknown error","update_time" =>"","story_id" => "");
	
    	if ($limit == ""){
    		$limit = 15;
    	}
    	
	if($uid == ""){//default, first page
		//$result = mysql_query("SELECT sid FROM stories ORDER BY sid DESC LIMIT 1");
		
		$columns = "sid";
		$from = tblUserProfiles;
		$where = "";
		$orderBy = "sid DESC ";
		$limit = "1";
			
		$result = $this->sf_select($columns,$from,$where,$orderBy,$limit);
			
		if ($row = mysql_fetch_assoc($result)){
			$uid_start = $row['sid'];//upper limit
			$uid = $uid_start;

			//$result1 = mysql_query("SELECT sid FROM stories WHERE sid<=$uid ORDER BY sid DESC LIMIT $limit");
			
			$columns = "sid";
			$from = tblUserProfiles;
			$where = "sid<=$uid";
			$orderBy = "sid DESC ";
			$limit = "1";
			
			$result1 = $this->sf_select($columns,$from,$where,$orderBy,$limit);
			while ($row1 = mysql_fetch_assoc($result1)){
				$uid_end = $row1['sid'];//lower limit
			}
		
			/*$result = mysql_query("SELECT users.email,users.fname,users.lname,users.gender,users.country,stories.about,stories.why,stories.desire,stories.updated_at,stories.sid,stories.unique_id
			//FROM stories INNER JOIN users ON stories.unique_id=users.unique_id WHERE stories.sid<=$uid_start AND stories.sid>=$uid_end ORDER BY stories.updated_at DESC") or die(mysql_error());
			
			$columns = email.",".",".fname.",".lname.",".gender.",".country.",".tblUserProfiles.".".
						updated_at.",".tblUserProfiles.".".sid.",".tblUserProfiles.".".unique_id;
			$from = tblUserProfiles;
			$innerJoin = tblUsers;
			$on = tblUsers.".".user_id."=".tblUserProfiles.".".user_id;
			$where = tblUserProfiles.".".sid."<='$uid_start' AND ".tblUserProfiles.".".sid.">='$uid_end'";
			$orderBy = tblUserProfiles.".".updated_at." DESC";
			
			$result = $this->sf_select_innerJoin($columns,$from,$leftJoin,$on,$where,$orderBy);*/
			
			
		}else{
			$list['success'] = 0;
			$list['error'] = 1;
			$list['error_msg'] = "no stories in the database";
			return $list;
		}
		
	}else if ($direction == "next"){//fetch 15 lower rows below uid, next page and refresh page
	
		//$result = mysql_query("SELECT sid FROM stories WHERE sid<=$uid ORDER BY sid DESC LIMIT $limit");
		$columns = "sid";
		$from = tblUserProfiles;
		$where = "sid<=$uid";
		$orderBy = "sid DESC ";
		$limit = "1";
			
		$result = $this->sf_select($columns,$from,$where,$orderBy,$limit);
		
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
		
    		/*$result = mysql_query("SELECT users.email,users.fname,users.lname,users.gender,users.country,stories.about,stories.why,stories.desire,stories.updated_at,stories.sid,stories.unique_id
			//FROM stories INNER JOIN users ON stories.unique_id=users.unique_id WHERE stories.sid<=$uid_start AND stories.sid>=$uid_end ORDER BY stories.updated_at DESC") or die(mysql_error());
			
		$columns = email.",".",".fname.",".lname.",".gender.",".country.",".tblUserProfiles.".".
						updated_at.",".tblUserProfiles.".".sid.",".tblUserProfiles.".".unique_id;
		$from = tblUserProfiles;
		$innerJoin = tblUsers;
		$on = tblUserProfiles.".".user_id."=".tblUsers.".".user_id;
		$where = tblUserProfiles.".".sid."<=$uid_start AND ".tblUserProfiles.".".sid.">=$uid_end";
		$orderBy = tblUserProfiles.".".updated_at." DESC";
			
		$result = $this->sf_select_innerJoin($columns,$from,$leftJoin,$on,$where,$orderBy);*/
    		
    	}else if ($direction == "previous"){//fetch 15 upper rows above uid,previous page
	
		//$result = mysql_query("SELECT sid FROM stories WHERE sid>=$uid ORDER BY sid ASC LIMIT $limit");
		$columns = "sid";
		$from = tblUserProfiles;
		$where = "sid>=$uid";
		$orderBy = "sid ASC ";
		$limit = "1";
			
		$result = $this->sf_select($columns,$from,$where,$orderBy,$limit);
		
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
			
    		/*$result = mysql_query("SELECT users.email,users.fname,users.lname,users.gender,users.country,stories.about,stories.why,stories.desire,stories.updated_at,stories.sid,stories.unique_id
			//FROM stories INNER JOIN users ON stories.unique_id=users.unique_id WHERE stories.sid<=$uid_start AND stories.sid>=$uid_end ORDER BY stories.updated_at DESC") or die(mysql_error());
    		
		$columns = email.",".",".fname.",".lname.",".gender.",".country.",".tblUserProfiles.".".
						updated_at.",".tblUserProfiles.".".sid.",".tblUserProfiles.".".unique_id;
		$from = tblUserProfiles;
		$innerJoin = tblUsers;
		$on = tblUserProfiles.".".user_id."=".tblUsers.".".user_id;
		$where = tblUserProfiles.".".sid."<=$uid_start AND ".tblUserProfiles.".".sid.">=$uid_end";
		$orderBy = tblUserProfiles.".".updated_at." DESC";
			
		$result = $this->sf_select_innerJoin($columns,$from,$leftJoin,$on,$where,$orderBy);*/
	}else{
		$list['success'] = 0;
		$list['error'] = 1;
		$list['error_msg'] = "unknown list direction request";
		$list['story_id'] = "";
		return $list;
    	}//end of first if-else-if

	$columns = email.",".",".fname.",".lname.",".gender.",".country.",".tblUserProfiles.".".
						updated_at.",".tblUserProfiles.".".sid.",".tblUserProfiles.".".unique_id;
	$from = tblUserProfiles;
	$innerJoin = tblUsers;
	$on = tblUserProfiles.".".user_id."=".tblUsers.".".user_id;
	$where = tblUserProfiles.".".sid."<=$uid_start AND ".tblUserProfiles.".".sid.">=$uid_end";
	$orderBy = tblUserProfiles.".".updated_at." DESC";
			
	$result = $this->sf_select_innerJoin($columns,$from,$leftJoin,$on,$where,$orderBy);
	//check for result
	if ($result !== FALSE) {
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
				//$result = mysql_query("SELECT users.email,users.fname,users.lname,users.gender,users.country,stories.about,stories.why,stories.desire,stories.updated_at,stories.sid,stories.unique_id
				//FROM users INNER JOIN stories ON users.unique_id=stories.unique_id WHERE users.fname='$fname' OR users.lname='$fname'") or die(mysql_error());
				
				$columns = email.",".",".fname.",".lname.",".gender.",".country.",".tblUserProfiles.".".
						updated_at.",".tblUserProfiles.".".sid.",".tblUserProfiles.".".unique_id;
				$from = tblUsers;
				$innerJoin = tblUserProfiles;
				$on = tblUserProfiles.".".user_id."=".tblUsers.".".user_id;
				$where = fname."='$fname' OR ".lname."='$fname'";
				$orderBy = "";
						
				
				
			}else{
				$result = mysql_query("SELECT users.email,users.fname,users.lname,users.gender,users.country,stories.about,stories.why,stories.desire,stories.updated_at,stories.sid,stories.unique_id
				FROM users INNER JOIN stories ON users.unique_id=stories.unique_id WHERE users.fname='$fname' OR users.fname='$lname' OR users.lname='$fname' OR users.lname='$lname'") or die(mysql_error());
				
				$columns = email.",".",".fname.",".lname.",".gender.",".country.",".tblUserProfiles.".".
						updated_at.",".tblUserProfiles.".".sid.",".tblUserProfiles.".".unique_id;
				$from = tblUsers;
				$innerJoin = tblUserProfiles;
				$on = tblUserProfiles.".".user_id."=".tblUsers.".".user_id;
				$where = fname."='$fname' OR ".fname=."'$lname' OR ".lname."='$fname' OR ".lname."='$lname'";
				$orderBy = "";
			}
	
		}else if($category == "Country"){
			$result = mysql_query("SELECT users.email,users.fname,users.lname,users.gender,users.country,stories.about,stories.why,stories.desire,stories.updated_at,stories.sid,stories.unique_id
			FROM users INNER JOIN stories ON users.unique_id=stories.unique_id WHERE users.country = '$searchKey'") or die(mysql_error());
			
			$columns = email.",".",".fname.",".lname.",".gender.",".country.",".tblUserProfiles.".".
						updated_at.",".tblUserProfiles.".".sid.",".tblUserProfiles.".".unique_id;
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
		
		$result = $this->sf_select_innerJoin($columns,$from,$leftJoin,$on,$where,$orderBy);
		//check for result
		if ($result !== FALSE) {
		
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
    

?>