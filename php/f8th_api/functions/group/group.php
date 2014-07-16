<?php
/**
 * All Honor and Glory belongs to God Almighty
 * Phil 2:13
 * @since 12th January, 2014
 * @version 2.0
 * 
 */ 
	//require_once("database/db_queries.php");
	require_once("functions/user/notification.php");
	
	class USER_GROUP extends USER_NOTIFICATION
	{
		
	
		public function createGroup($user_id,$grp_name,$grp_type,$city,$country)
		{
			$columns = ALL;
			$where = "GROUP_NAME = '$grp_name'";
			$result = $this->f8h_selectWhere($columns, tblUserGroups,$where);
			if($result === FALSE){
				
				$columns = ALL;
				$where = "GROUP_OWNER_ID = '$user_id'";
				$result = $this->f8h_selectWhere($columns, tblUserGroups,$where);
				
				if($result === FALSE){
				
					$datestart = strtotime('2013-12-14');//you can change it to your timestamp;
					$dateend = strtotime('2043-12-31');//you can change it to your timestamp;
					$daystep = 86400;
					$datebetween = abs(($dateend - $datestart) / $daystep);
					$randomday = rand(0, $datebetween);
					
					$grp_id= "grp".$randomday ."". date("Ymd", $datestart + ($randomday * $daystep));
					$time = new DateTime();
					$time = date("Y-m-d h:m:s");
					
					$into = tblUserGroups;
					$columns = "GROUP_ID,GROUP_OWNER_ID,GROUP_NAME,GROUP_TYPE,GROUP_CITY,GROUP_COUNTRY,CREATED_AT";
					$values = "'$grp_id','$user_id','$grp_name','$grp_type','$city','$country','$time'";
					$result = $this->f8h_insert($into,$columns,$values);
					
					if($result !== FALSE){
						return TRUE;
					}else{
						return "Oops, something went wrong.\nUnable to create ".$grp_name;
					}
				}else{
					
					return "You can only create one group at a time.";
				}
			}else{
				return "group already created";
			}
		}//end of function
		
		public function deleteGroup($grp_id)
		{
			$where = "GROUP_ID = '$grp_id'";
			$result = $this->f8h_delete(tblUserGroups,$where);
			if($result === TRUE){
			
				$where = "GROUP_ID = '$grp_id'";
				$this->f8h_delete(tblGroupMembers,$where);
				
				return TRUE;
			}else{
				return "Oops, something went wrong.\nUnable to delete group.";
			}
		}//end of function
		
		public function editGroup($grp_id,$grp_name,$grp_type,$city,$country)
		{
			$columns = ALL;
			$where = "GROUP_NAME = '$grp_name'";
			$result = $this->f8h_selectWhere($columns, tblUserGroups,$where);
			if($result !== FALSE && $row = mysql_fetch_assoc($result)){
			
				if($row['GROUP_NAME'] == $grp_name){
			
					$set = "GROUP_NAME='$grp_name',GROUP_TYPE='$grp_type',GROUP_CITY='$city',GROUP_COUNTRY='$country',UPDATED_AT=NOW()";
					$where = "GROUP_ID = '$grp_id'";
					$result = $this->f8h_update( tblUserGroups,$set,$where);
					if($result === TRUE){
						return TRUE;
					}else{
						return "Oops, something went wrong.\nUnable to update group ".$grp_name;
					}
				
				}else{
					return "group name already in use";
				}
			}else{
			
				$set = "GROUP_NAME='$grp_name',GROUP_TYPE='$grp_type',UPDATED_AT=NOW()";
				$where = "GROUP_ID = '$grp_id'";
				$result = $this->f8h_update( tblUserGroups,$set,$where);
				if($result === TRUE){
					return TRUE;
				}else{
					return "Oops, something went wrong.\nUnable to update group ".$grp_name;
				}
			}
		}//end of function
		
		public function getGroupList($user_id,$type,$item_id)
		{
			$list = array("tag" => "get_groups", "success" => 0, "error" => 0);
			
			if($type === FIRST_TIME){//first time
				$id = mysql_fetch_assoc(mysql_query("SELECT MAX(G_ID) AS G_ID FROM tblUserGroups"));
				$item_id = $id['G_ID'];
				
				$where = "G_ID <= $item_id";
				$error = "No groups found";
				
			}else if($type === RECENT){//recent
			//receives last recent item_id
				//$where = "G_ID > $item_id";
				$id = mysql_fetch_assoc(mysql_query("SELECT MAX(G_ID) AS G_ID FROM tblUserGroups"));
				$item_id = $id['G_ID'];
				
				$where = "G_ID <= $item_id";
				$error = "no new groups";
				
			}else if($type === LOADMORE){//loadMore
			//receives last old item_id
				$where = "G_ID < $item_id";
				$error = "All groups Fetched";
			}else{
				$where = "";
				$error = "Unknown Request";
			}
		
			$columns = ALL;
			$from = tblUserGroups;
			$order_by = "G_ID DESC";
			$limit = "20";
			
			$result = $this->f8h_select($columns,$from,$where,$order_by,$limit);
			
			if ($result !== FALSE) {
				
				$list['success'] = 1;
				$list['error'] = 0;
				while ($row = mysql_fetch_assoc($result)){
					//$grp_id = $row['GROUP_ID'];
					$list['groups'][] = $this->getGroupDetails($row,$user_id);
					
				}//end of while
			
				return $list;
				
			}else{
				$list['success'] = 0;
				$list['error'] = 1;
				$list['error_msg'] = $error;
				return $list;
			}
		}//end of function
		
		public function getGroup($grp_id,$user_id)
		{
			$group = array("tag" => "get_group", "success" => 0, "error" => 0);
			
			$columns = ALL;
			$from = tblUserGroups;
			$where = "GROUP_ID = '$grp_id'";
			$result = $this->f8h_selectWhere($columns, $from,$where);
			if($row = mysql_fetch_assoc($result)){
				$group['success'] = 1;
				$group['error'] = 0;
				
				$group['group_details']['g_id'] = $row['G_ID'];
				$group['group_details']['group_id'] = $row['GROUP_ID'];
				$group['group_details']['owner_id'] = $row['GROUP_OWNER_ID'];
				$group['group_details']['owner'] = $this->getFullNames($row['GROUP_OWNER_ID']);
				$group['group_details']['group_name'] = $row['GROUP_NAME'];
				$group['group_details']['group_type'] = $row['GROUP_TYPE'];
				$group['group_details']['group_city'] = $row['GROUP_CITY'];
				$group['group_details']['group_country'] = $row['GROUP_COUNTRY'];
				$group['group_details']['group_size'] = $this->getGroupSize($row['GROUP_ID']);
				$group['group_details']['user_type'] = $this->getUserType($row['GROUP_ID'],$user_id);
			
				return $group;
			}else{
				$group['success'] = 0;
				$group['error'] = 1;
				$group['error_msg'] = "Group Not Found";
				return $group;
			}
		}//end of function
		
		public function saveGroupPhoto($grp_id,$imgData)
		{
			$filename= $grp_id;
			
			$path = "photos/group_photos/".$filename;
			$fp = fopen($path,'w');
			
			//str_replace(' ','+',$imgData);
			$img = base64_decode($imgData);
			
			$chk = fwrite($fp,$img);
			fclose($fp);
			
			if($chk !== FALSE){
			
				$set = "PHOTO_PATH='$path',updated_at=NOW()";
				$where = "GROUP_ID='$grp_id'";
				$result = $this->f8h_update(tblUserGroups,$set,$where);
				if($result === TRUE){
					return TRUE;
				}
			}
			
			return "Oops, something went wrong.\nError uploading photo";
		}//end of function
		
		public function getGroupPhoto($grp_id)
		{
			$columns = "PHOTO_PATH";
			$where = "GROUP_ID = '$grp_id'";
			$result = $this->f8h_selectWhere($columns,tblUserGroups,$where);
			if($result !== FALSE && $row = mysql_fetch_assoc($result)){
				$path = $row['PHOTO_PATH'];
				$imgStr = "";
				$imgStr .= base64_encode(file_get_content($path));
				
				return $imgStr;
			}
			
			return FALSE;
		}//end of function
			
	//=====================================================================================
	
		private function getGroupDetails($row = array(),$user_id)
		{
			$group = array();	
			
			$group['g_id'] = $row['G_ID'];
			$group['group_id'] = $row['GROUP_ID'];
			$group['owner_id'] = $row['GROUP_OWNER_ID'];
			$group['owner'] = $this->getFullNames($row['GROUP_OWNER_ID']);
			$group['group_name'] = $row['GROUP_NAME'];
			$group['group_type'] = $row['GROUP_TYPE'];
			$group['group_city'] = $row['GROUP_CITY'];
			$group['group_country'] = $row['GROUP_COUNTRY'];
			$group['group_size'] = $this->getGroupSize($row['GROUP_ID']);
			$group['user_type'] = $this->getUserType($row['GROUP_ID'],$user_id);
			
			return $group;
		}//end of function
		
		public function getGroupArray($grp_id,$user_id,$m_id)
		{
			$group = array();
			
			$columns = ALL;
			$from = tblUserGroups;
			$where = "GROUP_ID = '$grp_id'";
			$result = $this->f8h_selectWhere($columns, $from,$where);
			if($row = mysql_fetch_assoc($result)){
				
				$group['m_id'] = $m_id;
				$group['g_id'] = $row['G_ID'];
				$group['group_id'] = $row['GROUP_ID'];
				$group['owner_id'] = $row['GROUP_OWNER_ID'];
				$group['owner'] = $this->getFullNames($row['GROUP_OWNER_ID']);
				$group['group_name'] = $row['GROUP_NAME'];
				$group['group_type'] = $row['GROUP_TYPE'];
				$group['group_city'] = $row['GROUP_CITY'];
				$group['group_country'] = $row['GROUP_COUNTRY'];
				$group['group_size'] = $this->getGroupSize($row['GROUP_ID']);
				$group['user_type'] = $this->getUserType($row['GROUP_ID'],$user_id);
			
				return $group;
			}else{
				
				return $group;
			}
		}//end of function
		
		public function getGroupSize($grp_id)
		{
			$columns = "COUNT(*) AS SIZE";
			$where = "GROUP_ID = '$grp_id'";
			$result = $this->f8h_selectWhere($columns, tblGroupMembers,$where);
			if($result !== FALSE && $row =  mysql_fetch_assoc($result)){
				return $row['SIZE'];
			}
			return "";
		}//end of function
		
		public function getGroupName($grp_id)
		{
		
			$columns = "GROUP_NAME";
			$where = "GROUP_ID = '$grp_id'";
			$result = $this->f8h_selectWhere($columns, tblUserGroups,$where);
			if($result !== FALSE && $row =  mysql_fetch_assoc($result)){
				return $row['GROUP_NAME'];
			}
			return "";
		}
		
		public function getUserType($grp_id,$user_id)
		{
			$columns = "MEMBER_TYPE";
			$where = "GROUP_ID = '$grp_id' AND MEMBER_ID = '$user_id'";
			$result = $this->f8h_selectWhere($columns, tblGroupMembers,$where);
			
			if($result !== FALSE && $row = mysql_fetch_assoc($result)){
				return $row['MEMBER_TYPE'];
			}
			
			return "";
		}//end of function
	}//END OF CLASS
 
 ?>