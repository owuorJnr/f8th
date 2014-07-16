<?php
/**
 * All Honor and Glory belongs to God Almighty
 * Phil 2:13
 * @since 12th January, 2014
 * @version 2.0
 * 
 */ 
	require_once("story.php");
	
	class USER_FAVORITE_STORY extends USER_STORY
	{
	
		public function addFavorite($story_id,$user_id)
		{
			$time = new DateTime();
			$time = date("Y-m-d h:m:s");
			
			$into = tblFavoriteStories;
			$columns = "STORY_ID,USER_ID,CREATED_AT";
			$values = "'$story_id','$user_id','$time'";
			$result = $this->f8h_insert($into,$columns,$values);
			
			if($result !== FALSE){
				return TRUE;
			}else{
				return "Oops, something went wrong.\nUnable to add favorite.";
			}
		}//end of function
		
		
		public function removeFavorite($story_id,$user_id)
		{
			$where = "STORY_ID = '$story_id' AND USER_ID = '$user_id'";
			$result = $this->f8h_delete(tblFavoriteStories,$where);
			
			if($result !== FALSE){
				return TRUE;
			}else{
				return "Oops, something went wrong.\nUnable to remove favorite.";
			}
		}//end of function
		
		public function getFavStoryList($user_id,$fav_id)
		{
			$list = array("tag" => "get_fav_stories", "success" => 0, "error" => 0);
			
			if($fav_id == ""){
				//refresh
				$fav_id = 0;
				$id = mysql_fetch_assoc(mysql_query("SELECT MAX(FAV_ID) AS FAV_ID FROM tblFavoriteStories where USER_ID = '$user_id'"));
				$fav_id = $id['FAV_ID'];
				$where = "USER_ID = '$user_id' AND FAV_ID <= '$fav_id'";
			}else{
				//loadmore
				$where = "USER_ID = '$user_id' AND FAV_ID < '$fav_id' ";
			}
			
			$columns = ALL;
			$from = tblFavoriteStories;
			$order_by = "FAV_ID DESC";
			$limit = "20";
			
			$result = $this->f8h_select($columns,$from,$where,$order_by,$limit);
			if($result !== FALSE){
				$list['success'] = 1;
				$list['error'] = 0;
				while($row = mysql_fetch_assoc($result)){
					$story_id = $row['STORY_ID'];
					//$list['fav_stories']['fav_id'] = $row['FAV_ID'];
					$list['fav_stories'][] = $this->getStoryArray($story_id,$user_id,$row['FAV_ID']);
				}
				
				return $list;
			}else{
			
				$list['success'] = 0;
				$list['error'] = 1;
				$list['error_msg'] = "You have no favorite stories";
				return $list;
			}
		}//end of function
	
	
	}//END OF CLASS
 
 ?>