<?php
	#Include the connect.php file
	include('connect.php');
	#Connect to the database
	//connection String
	$connect = mysql_connect($hostname, $username, $password)
	or die('Could not connect: ' . mysql_error());
	//Select The database
	$bool = mysql_select_db($database, $connect);
	if ($bool === False){
	   print "can't find $database";
	}
	
	$query = "SELECT * FROM  `Invoices` ORDER BY OrderDate LIMIT 0 , 100";
	$result = mysql_query($query) or die("SQL Error 1: " . mysql_error());

	// get data and store in a json array
	while ($row = mysql_fetch_array($result, MYSQL_ASSOC)) {
		$orders[] = array(
			'OrderDate' => $row['OrderDate'],
			'ProductName' => $row['ProductName'],
			'Quantity' => $row['Quantity']
		  );
	}
  
	echo json_encode($orders);
?>