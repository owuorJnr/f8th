<?php
/**
 * All Honor and Glory belongs to God Almighty
 * Phil 2:13
 * @since 12th January, 2014
 * @version 2.0
 * 
 */ 
	//require_once("database/db_queries.php");
	require_once("functions/user/user.php");
	
	class USER_NOTIFICATION extends USER_PROFILE
	{
		
	
		public function sendMessage($message,$sender_type,$sender,$recipient,$date)
		{
			$datestart = strtotime('2013-12-14');//you can change it to your timestamp;
			$dateend = strtotime('2043-12-31');//you can change it to your timestamp;
			$daystep = 86400;
			$datebetween = abs(($dateend - $datestart) / $daystep);
			$randomday = rand(0, $datebetween);
			
			$noti_id= "noti".$randomday ."". date("Ymd", $datestart + ($randomday * $daystep));
			$time = new DateTime();
			$time = date("Y-m-d h:m:s");
			
			$into = tblUserNotifications;
			$columns = "NOTIFICATION_ID,SENDER_TYPE,SENDER_ID,RECIPIENT_ID,MESSAGE,STATUS,SENT_AT,CREATED_AT";
			$values = "'$noti_id','$sender_type','$sender','$recipient','$message','".UNREAD."','$date','$time'";
			$result = $this->f8h_insert($into,$columns,$values);
			
			if($result !== FALSE){
				return TRUE;
			}else{
				return "Oops, something went wrong.\nUnable to send message.";
			}
		}//end of function
		
		public function markAsRead($notify_id)
		{
			$set = "STATUS='".READ."',UPDATED_AT=NOW()";
			$where = "NOTIFICATION_ID='$notify_id'";
			$result = $this->f8h_update( tblUserNotifications,$set,$where);
			if($result === TRUE){
				return TRUE;
			}else{
				return "Oops, something went wrong.\nUnable to mark message as read.";
			}
		}//end of function
		
		public function getNoficationList($user_id,$n_id)
		{
			$list = array("tag" => "get_notifications", "success" => 0, "error" => 0);
			
			if($n_id == ""){
				//refresh
				$id = mysql_fetch_assoc(mysql_query("SELECT MAX(N_ID) AS N_ID FROM tblUserNotifications where RECIPIENT_ID = '$user_id'"));
				$n_id = $id['N_ID'];
				$where = "RECIPIENT_ID = '$user_id' AND N_ID <= '$n_id' ";
				/*}else{
					$where = "RECIPIENT_ID = '$user_id'";
				}*/
			}else{
				//loadmore
				$where = "RECIPIENT_ID = '$user_id' AND N_ID < '$n_id' ";
			}
			
			$columns = ALL;
			$from = tblUserNotifications;
			//$where = "RECIPIENT_ID = '$user_id' AND STATUS='".UNREAD."'";
			$order_by = "N_ID DESC";
			$limit = "10";
			
			$result = $this->f8h_select($columns,$from,$where,$order_by,$limit);
			
			if ($result !== FALSE) {
				
				$list['success'] = 1;
				$list['error'] = 0;
				while ($row = mysql_fetch_assoc($result)){
					$notify_id = $row['NOTIFICATION_ID'];
					$list['notifications'][] = $this->getNotifyDetails($row);
					
					
				}//end of while
			
				return $list;
				
			}else{
				$list['success'] = 0;
				$list['error'] = 1;
				$list['error_msg'] = "You are up to date.";
				return $list;
			}
		}//end of function
		
		public function getSenderPhoto($notify_id)
		{
		
		}//end of function
		
		public function deleteNofication($notify_id)
		{
			$where = "NOTIFICATION_ID='$notify_id'";
			$result = $this->f8h_delete(tblUserNotifications,$where);
			if($result === TRUE){
				return TRUE;
			}else{
				return "Oops, something went wrong.\nUnable to delete notification.";
			}
		}//end of function
		
		
	//==============================================================================================
		public function getNotifyDetails($row = array())
		{
			$notification = array();
			
			/*$columns = ALL;
			$from = tblUserNotifications;
			$where = "NOTIFICATION_ID='$notify_id'";
			$result = $this->f8h_selectWhere($columns, $from,$where);
			if($row = mysql_fetch_assoc($result)){
				*/
				$notification['n_id'] = $row['N_ID'];
				$notification['notify_id'] = $row['NOTIFICATION_ID'];
				$notification['message'] = $row['MESSAGE'];
				$notification['sender_id'] = $row['SENDER_ID'];
				$notification['sender'] = $this->getFullNames($row['SENDER_ID']);
				$notification['recipient_id'] = $row['RECIPIENT_ID'];
				$notification['status'] = $row['STATUS'];
				$notification['sent_at'] = $row['SENT_AT'];
			
				return $notification;
			/*}else{
				
				return $notification;
			}*/
		}//end of function
	}//END OF CLASS
 
 ?>