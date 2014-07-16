<?php
/**
 * All Honor and Glory belongs to God Almighty
 * Phil 2:13
 * @since 12th January, 2014
 * @version 2.0
 * 
 */ 
	require_once("group.php");
	
	
	class GROUP_MEMBER extends USER_GROUP
	{
		
	
		public function addMember($grp_id,$user_id,$member_type)
		{
			$time = new DateTime();
			$time = date("Y-m-d h:m:s");
			
			$into = tblGroupMembers;
			$columns = "GROUP_ID,MEMBER_ID,MEMBER_TYPE,CREATED_AT";
			$values = "'$grp_id','$user_id','$member_type','$time'";
			$result = $this->f8h_insert($into,$columns,$values);
			
			if($result !== FALSE){
				return TRUE;
			}else{
				return "Oops, something went wrong.\nUnable to add member to group.";
			}
		}//end of function
		
		public function removeMember($grp_id,$user_id)
		{
			$where = "GROUP_ID = '$grp_id' AND MEMBER_ID = '$user_id'";
			$result = $this->f8h_delete(tblGroupMembers,$where);
			
			if($result !== FALSE){
				return TRUE;
			}else{
				return "Oops, something went wrong.\nUnable to remove member from group.";
			}
		}//end of function
		
		public function getJoinedGroupsId($member_id)
		{
			$id_array = array();
			
			$columns = "GROUP_ID";
			$from = tblGroupMembers;
			$where = "MEMBER_ID = '$member_id'";
			$result = $this->f8h_selectWhere($columns, $from,$where);
			if($result !== FALSE){
				while($row = mysql_fetch_array($result, MYSQL_NUM)){
					$id_array[] = $row[0];
				}
				//print_r($id_array);
				return $id_array;
			}else{
				return FALSE;
			}
		
		}//end of function
		
		public function sendGroupMessage($grp_id,$message)
		{
		
			$columns = "MEMBER_ID";
			$from = tblGroupMembers;
			$where = "GROUP_ID = '$grp_id'";
			$result = $this->f8h_selectWhere($columns, $from,$where);
			if($result !== FALSE){
				while($row = mysql_fetch_assoc($result)){
					$sender_type = SENDER_TYPE_GROUP;
					$sender = $grp_id;
					$recipient = $row['MEMBER_ID'];
					$time = new DateTime();
					$time = date("Y-m-d h:m:s");
					
					$error_found = $this->sendMessage($message,$sender_type,$sender,$recipient,$time);
					if($error_found !== TRUE){
							return $error_found;
					}
				}
				
				return TRUE;
			}else{
				return "No members found";
			}
		}//end of function
		
		public function getJoinedGroupList($user_id,$m_id)
		{
			$list = array("tag" => "get_joined_groups", "success" => 0, "error" => 0);
			
			if($m_id == ""){
				//refresh
				$id = mysql_fetch_assoc(mysql_query("SELECT MAX(M_ID) AS M_ID FROM tblGroupMembers where MEMBER_ID = '$user_id'"));
				$m_id = $id['M_ID'];
				$where = "MEMBER_ID = '$user_id' AND M_ID <= '$m_id'";
			}else{
				//loadmore
				$where = "MEMBER_ID = '$user_id' AND M_ID < '$m_id' ";
			}
			
			$columns = ALL;
			$order_by = "M_ID DESC";
			$limit = "20";
			
			$result = $this->f8h_select($columns, tblGroupMembers,$where,$order_by,$limit);
			if($result !== FALSE){
				$list['success'] = 1;
				$list['error'] = 0;
				while($row = mysql_fetch_assoc($result)){
					$grp_id = $row['GROUP_ID'];
					//$list['joined_groups']['m_id'] = $row['M_ID'];
					$list['joined_groups'][] = $this->getGroupArray($grp_id,$user_id,$row['M_ID']);
				}
				
				return $list;
			}else{
			
				$list['success'] = 0;
				$list['error'] = 1;
				$list['error_msg'] = "You have not joined any group";
				return $list;
			}
		}//end of function
	
	
	}//END OF CLASS
 
 ?>