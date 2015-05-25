<?php
  #Include the connect.php file
  include('connect.php');
  #Connect to the database
  //connection String
  $connect = mysql_connect($hostname, $username, $password)
  or die('Could not connect: ' . mysql_error());
  //select database
  mysql_select_db($database, $connect);
  //Select The database
  $bool = mysql_select_db($database, $connect);
  if ($bool === False){
	  print "can't find $database";
  }
  // get data and store in a json array
  $query = "SELECT * FROM Employees";

  $result = mysql_query($query) or die("SQL Error 1: " . mysql_error());
  while ($row = mysql_fetch_array($result, MYSQL_ASSOC)) {
	  $employees[] = array(
          'EmployeeID' => $row['EmployeeID'],
          'FirstName' => $row['FirstName'],
	        'LastName' => $row['LastName'],
	        'Name' => $row['FirstName'] . " " . $row['LastName']
        );
  }

  echo json_encode($employees);
?>