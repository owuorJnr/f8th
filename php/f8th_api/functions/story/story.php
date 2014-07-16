<?php
/**
 * All Honor and Glory belongs to God Almighty
 * Phil 2:13
 * @since 12th January, 2014
 * @version 2.0
 * 
 */ 
	//require_once("database/db_queries.php");
	require_once("functions/group/group_members.php");
	
	class USER_STORY extends GROUP_MEMBER
	{
	
		public function saveStory($story,$author_id,$visibility)
		{
			$columns = "STORY";
			$from = tblUserStories;
			$where = "STORY = '$story'";
			$result = $this->f8h_selectWhere($columns, $from,$where);
			if($result === FALSE){
				//story does not exist
				$datestart = strtotime('2013-12-14');//you can change it to your timestamp;
				$dateend = strtotime('2043-12-31');//you can change it to your timestamp;
				$daystep = 86400;
				$datebetween = abs(($dateend - $datestart) / $daystep);
				$randomday = rand(0, $datebetween);
				
				$story_id= "stry".$randomday ."". date("Ymd", $datestart + ($randomday * $daystep));
				$time = new DateTime();
				$time = date("Y-m-d h:m:s");
				
				$into = tblUserStories;
				$columns = "STORY_ID,STORY,AUTHOR_ID,CREATED_AT";
				$values = "'$story_id','$story','$author_id','$time'";
				$result = $this->f8h_insert($into,$columns,$values);
				
				if($result !== FALSE){
					//save story visibility
					$grp_array= explode(";",$visibility);
					array_pop($grp_array);
					//print_r($grp_array);
					foreach($grp_array as $val){
						$error_found = $this->saveStoryVisbility($story_id,$val) ;
						if($error_found !== TRUE){
							return $error_found;
						}
					}
					return TRUE;
				}else{
					return "Oops, something went wrong.\nUnable to save story.";
				}
			}else{
				return "story already told";
			}
		}//end of function
		
		
		public function deleteStory($story_id)
		{
			$where = "STORY_ID='$story_id'";
			$result = $this->f8h_delete(tblUserStories,$where);
			if($result !== FALSE){
				$where = "STORY_ID = '$story_id'";
				$result = $this->f8h_delete(tblStoryVisibility,$where);
				return TRUE;
			}else{
				return "Oops, something went wrong.\nUnable to delete story.";
			}
		}//end of function
		
		public function editStory($story_id,$story,$visibility)
		{
			$set = "STORY='$story',UPDATED_AT=NOW()";
			$where = "STORY_ID='$story_id'";
			$result = $this->f8h_update( tblUserStories,$set,$where);
			if($result !== FALSE){
				//save story visibility
				$grp_array= explode(";",$visibility);
				array_pop($grp_array);
				//print_r($grp_array);
				foreach($grp_array as $val){
					$error_found = $this->saveStoryVisbility($story_id,$val) ;
					if($error_found !== TRUE){
						return $error_found;
					}
				}
				return TRUE;
			}else{
				return "Oops, something went wrong.\nUnable to update story.";
			}
		}//end of function
		
		public function getStoryList($user_id,$type,$item_id)
		{
			$list = array("tag" => "get_stories", "success" => 0, "error" => 0);
			
			$groups_id = $this->getJoinedGroupsId($user_id);
			
			if($groups_id !== FALSE){
				$where = "";
				$count = count($groups_id);
				$i = 1;
				foreach($groups_id as $val){
					if($i < $count){
						$where .= " GROUP_ID = '$val' OR ";
					}else{
						$where .= " GROUP_ID = '$val' ";
					}
					$i = $i+1;
				}
				//echo $where;
				
				$columns = "STORY_ID";
				$from = tblStoryVisibility;
				$result = $this->f8h_selectWhere($columns, $from,$where);
				
				if($result !== FALSE){
					$count = mysql_num_rows($result);
					$i = 1;
					$where_arg = "";
					while($row = mysql_fetch_assoc($result)){
						$story_id = $row['STORY_ID'];
						if($i < $count){
							$where_arg .= " STORY_ID = '$story_id' OR ";
						}else{
							$where_arg .= " STORY_ID = '$story_id' ";
						}
						$i = $i+1;
					}
					//echo $where_arg;
					return $this->storyList($where_arg,$user_id,$type,$item_id);
				}else{
				
					$list['success'] = 0;
					$list['error'] = 1;
					$list['error_msg'] = "No stories found in your group";
					return $list;
				}
			}else{
				$list['success'] = 0;
				$list['error'] = 1;
				$list['error_msg'] = "You have not joined any group";
				return $list;
			}
		
		}//end of function
		
		public function storyList($where_arg,$user_id,$type,$item_id)
		{
			$list = array("tag" => "get_stories", "success" => 0, "error" => 0);
			
			if($type === FIRST_TIME){//first time
				$id = mysql_fetch_assoc(mysql_query("SELECT MAX(S_ID) AS S_ID FROM tblUserStories WHERE ".$where_arg));
				$item_id = $id['S_ID'];
				//echo $item_id."<br><br>";
				$where = "S_ID <= $item_id";
				$error = "No stories found";
				
			}else if($type === RECENT){//recent
			//receives last recent item_id
				//$where = "S_ID > $item_id";
				$id = mysql_fetch_assoc(mysql_query("SELECT MAX(S_ID) AS S_ID FROM tblUserStories WHERE ".$where_arg));
				$item_id = $id['S_ID'];
				$where = "S_ID <= $item_id";
				$error = "no new stories";
				
			}else if($type === LOADMORE){//loadMore
			//receives last old item_id
				$where = "S_ID < $item_id";
				$error = "All stories Fetched";
			}else{
				$where = "";
				$error = "Unknown Request";
			}
		
			$columns = "S_ID,STORY_ID,STORY,AUTHOR_ID";
			$from = tblUserStories;
			$where .= " AND (".$where_arg.") ";
			$order_by = "S_ID DESC";
			$limit = "20";
			
			$result = $this->f8h_select($columns,$from,$where,$order_by,$limit);
			
			if ($result !== FALSE) {
				
				$list['success'] = 1;
				$list['error'] = 0;
				while ($row = mysql_fetch_assoc($result)){
					//$story_id = $row['STORY_ID'];
					$list['stories'][] = $this->getStoryDetails($row,$user_id);
					
				}//end of while
			
				return $list;
				
			}else{
				$list['success'] = 0;
				$list['error'] = 1;
				$list['error_msg'] = $error;
				return $list;
			}
		}//end of function
		
		
		public function getStory($story_id,$user_id)
		{
			$story = array("tag" => "get_story", "success" => 0, "error" => 0);
			
			$columns = ALL;
			$from = tblUserStories;
			$where = "STORY_ID = '$story_id'";
			$result = $this->f8h_selectWhere($columns, $from,$where);
			if($result !== FALSE && $row = mysql_fetch_assoc($result)){
				$story['success'] = 1;
				$story['error'] = 0;
				
				$story['story_details']['s_id'] = $row['S_ID'];
				$story['story_details']['story_id'] = $row['STORY_ID'];
				$story['story_details']['story'] = $row['STORY'];
				$story['story_details']['author_id'] = $row['AUTHOR_ID'];
				$story['story_details']['author'] = $this->getFullNames($row['AUTHOR_ID']);
				$story['story_details']['favs'] = $this->getFavSize($row['STORY_ID']);
				$story['story_details']['is_fav'] = $this->isUserFav($row['STORY_ID'],$user_id);
				$story['story_details']['is_owner'] = $this->isOwner($row['AUTHOR_ID'],$user_id);
				$story['story_details']['group'] = $this->getStoryVisibility($row['STORY_ID']);
			
				return $story;
			}else{
				$story['success'] = 0;
				$story['error'] = 1;
				$story['error_msg'] = "Story Not Found";
				return $story;
			}
		}//end of function
		
		
		public function getAuthorPhoto($story_id)
		{
			
		}//end of function
	
	//======================================================================
		public function getStoryDetails($row = array(),$user_id)
		{
			$story = array();
			
			$story['s_id'] = $row['S_ID'];
			$story['story_id'] = $row['STORY_ID'];
			//$post = str_split($row['STORY'],50);
			$story['story'] = $row['STORY'];//$post[0]."...";
			$story['author_id'] = $row['AUTHOR_ID'];
			$story['author'] = $this->getFullNames($row['AUTHOR_ID']);
			$story['favs'] = $this->getFavSize($row['STORY_ID']);
			$story['is_fav'] = $this->isUserFav($row['STORY_ID'],$user_id);
			$story['is_owner'] = $this->isOwner($row['AUTHOR_ID'],$user_id);
			$story['group'] = $this->getStoryVisibility($row['STORY_ID']);
			
			return $story;
			
		}//end of function
		
		public function getStoryArray($story_id,$user_id,$fav_id)
		{
			$story = array();
			
			$columns = ALL;
			$from = tblUserStories;
			$where = "STORY_ID = '$story_id'";
			$result = $this->f8h_selectWhere($columns, $from,$where);
			if($result !== FALSE && $row = mysql_fetch_assoc($result)){
				
				$story['fav_id'] = $fav_id;
				$story['s_id'] = $row['S_ID'];
				$story['story_id'] = $row['STORY_ID'];
				//$post = str_split($row['STORY'],50);
				$story['story'] = $row['STORY'];//$post[0]."...";
				$story['author_id'] = $row['AUTHOR_ID'];
				$story['author'] = $this->getFullNames($row['AUTHOR_ID']);
				$story['favs'] = $this->getFavSize($row['STORY_ID']);
				$story['is_fav'] = $this->isUserFav($row['STORY_ID'],$user_id);
				$story['is_owner'] = $this->isOwner($row['AUTHOR_ID'],$user_id);
				$story['group'] = $this->getStoryVisibility($row['STORY_ID']);
			
				return $story;
			}else{
				
				return $story;
			}
		}//end of function
		
		public function getFavSize($story_id)
		{
			$columns = "COUNT(*) AS FAVS";
			$where = "STORY_ID = '$story_id'";
			
			$favs = mysql_fetch_assoc($this->f8h_selectWhere($columns, tblFavoriteStories,$where));
			
			return $favs['FAVS'];
		}//end of function
		
		public function isUserFav($story_id,$user_id)
		{
			$columns = "USER_ID";
			$where = "STORY_ID = '$story_id'";
			$result = $this->f8h_selectWhere($columns, tblFavoriteStories,$where);
			if($result !== FALSE && $row = mysql_fetch_assoc($result)){
				if($row['USER_ID'] == $user_id){
					return "yes";
				}
			}
			return "no";
		}//end of function
		
		public function isOwner($author_id,$user_id)
		{
		
			if($author_id == $user_id){
				return "yes";
			}else{
				return "no";
			}
		}//end of function
		
		private function saveStoryVisbility($story_id,$grp_id)
		{
			$where = "GROUP_ID = '$grp_id' AND STORY_ID = '$story_id'";
			$result = $this->f8h_delete(tblStoryVisibility,$where);
			
			$time = new DateTime();
			$time = date("Y-m-d h:m:s");
			
			$into = tblStoryVisibility;
			$columns = "STORY_ID,GROUP_ID,CREATED_AT";
			$values = "'$story_id','$grp_id','$time'";
			$result = $this->f8h_insert($into,$columns,$values);
			
			if($result !== FALSE){
				//send notifications
				$message = "New Story on ".$this->getGroupName($grp_id);
				return $this->sendGroupMessage($grp_id,$message);
			}else{
				return FALSE;
			}
		}//end of function
		
		private function getStoryVisibility($story_id)
		{
			$group_details = "";
			
			$columns = "COUNT(*) AS GROUPS";
			$where = "STORY_ID = '$story_id'";
			$result = $this->f8h_selectWhere($columns, tblStoryVisibility,$where);
			if($result !== FALSE && $row = mysql_fetch_assoc($result)){
				$groups = $row['GROUPS'];
				
				if($groups == 1){
					$result1 = $this->f8h_selectWhere("GROUP_ID", tblStoryVisibility,$where);
					if($result1 !== FALSE && $row = mysql_fetch_assoc($result1)){
						$grp_id = $row['GROUP_ID'];
						$group_details = $this->getGroupName($grp_id);
					}
				}else if($groups > 1){
					$group_details = $groups." groups";
				}
				
			}
			
			return $group_details;
		}//end of function
	
	}//END OF CLASS
 
 ?>