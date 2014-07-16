<?php
 /**
 * All Honor and Glory belongs to God Almighty
 * Phil 2:13
 * @since 12th January, 2014
 * @version 2.0
 * 
 */ 
 
 
class DB_CONNECT {
 
 
	public function __construct()
	{
		require_once("config.php");
	}
	
	
    // Connecting to database
    public function connect() {
        
        // connecting to mysql
        $con = mysql_connect(DB_HOST, DB_USER, DB_PASSWORD);
        // selecting database
        mysql_select_db(DB_DATABASE);
 
        // return database handler
        return $con;
    }
 
    // Closing database connection
    public function disconnect() {
        mysql_close() or die(mysql_error());
    }
 
}
 
?>