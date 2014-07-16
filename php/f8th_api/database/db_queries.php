<?php
/**
 * All Honor and Glory belongs to God Almighty
 * Psalm 27:13-14
 * @since 12th January, 2014
 * @version 2.0
 * 
 */ 
	class DB_QUERIES {
	
		private $db;
		
		function __construct()
		{//call function to connect to the database
			require_once("db_connect.php");
			require_once("db_structure.php");
			
			$this->db = new DB_CONNECT();
			$this->db->connect();
		}//end of constructor
		
		function __destruct()
		{//call function to disconnect from database
			$this->db->disconnect();
		}//end of destructor
	
	//================================================
	
		function f8h_test($test)
		{
			return "Database Query: ".$test;
		}
		
		function f8h_select($columns,$from,$where,$order_by,$limit)
		{
			if($where == ""){
				$query = "SELECT $columns 
					FROM $from 
					ORDER BY $order_by 
					LIMIT $limit";
			}else{
				$query = "SELECT $columns 
					FROM $from 
					WHERE $where 
					ORDER BY $order_by 
					LIMIT $limit";
			}
			
			//echo $query."<br><br>";
			$result = mysql_query($query) or die(mysql_error());
			if(mysql_affected_rows() > 0)
			{
				return $result;
			}else
			{
				return FALSE;
			}
		}//end of function
		
		function f8h_selectAll($from)
		{
			$query = "SELECT * 
					FROM $from";
				
			$result = mysql_query($query) or die(mysql_error());
			if(mysql_affected_rows() > 0)
			{
				return $result;
			}else
			{
				return FALSE;
			}
		}//end of function
		
		function f8h_selectWhere($columns,$from,$where)
		{
			$query = "SELECT $columns 
					FROM $from 
					WHERE $where";
				
			$result = mysql_query($query) or die(mysql_error());
			if(mysql_affected_rows() > 0)
			{
				return $result;
			}else
			{
				return FALSE;
			}
		}//end of function
		
		function f8h_select_leftJoin($columns,$from,$join,$on,$where,$order_by)
		{
			if($order_by === ""){
				$query = "SELECT $columns 
					FROM $from 
					LEFT JOIN $join 
					ON $on 
					WHERE $where";
			}else{
				$query = "SELECT $columns 
					FROM $from 
					LEFT JOIN $join 
					ON $on 
					WHERE $where 
					ORDER BY $order_by";
			}
			
				
			//echo $query."<br><br>";
			$result = mysql_query($query) or die(mysql_error());
			if(mysql_affected_rows() > 0)
			{
				return $result;
			}else
			{
				return FALSE;
			}
		}//end of function
		
		function f8h_select_innerJoin($columns,$from,$join,$on,$where,$order_by)
		{
			if($order_by === ""){
				$query = "SELECT $columns 
					FROM $from 
					INNER JOIN $join 
					ON $on 
					WHERE $where";
			}else{
				$query = "SELECT $columns 
					FROM $from 
					INNER JOIN $join 
					ON $on 
					WHERE $where 
					ORDER BY $order_by";
			}
			
			//echo $query."<br><br>";
			$result = mysql_query($query) or die(mysql_error());
			if(mysql_affected_rows() > 0)
			{
				return $result;
			}else
			{
				return FALSE;
			}
		}//end of function
		
		function f8h_insert($into,$columns,$values)
		{
			$query = "INSERT 
					INTO $into($columns) 
					VALUES($values)";
			
			//echo $query;
			$result = mysql_query($query) or die(mysql_error());
			if(mysql_affected_rows() > 0)
			{
				return mysql_insert_id();
			}else
			{
				return FALSE;
			}
		}
		
		function f8h_update($table,$set,$where)
		{
			$query = "UPDATE $table 
					SET $set 
					WHERE $where";
			
			//echo $query;
			$result = mysql_query($query) or die(mysql_error());
			if(mysql_affected_rows() > 0)
			{
				return TRUE;
			}else
			{
				return FALSE;
			}
		}
		
		function f8h_delete($from,$where)
		{
			$query = "DELETE 
					FROM $from 
					WHERE $where";
			
			//echo $query;
			$result = mysql_query($query) or die(mysql_error());
			if(mysql_affected_rows() > 0)
			{
				return TRUE;
			}else
			{
				return FALSE;
			}
		}
	
	}
?>