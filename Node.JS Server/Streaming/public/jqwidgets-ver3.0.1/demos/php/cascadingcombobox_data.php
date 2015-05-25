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
  
  if (isset($_GET['CustomerID']))
  {
     // get data and store in a json array
     $query = "SELECT * FROM Orders where CustomerID='" .$_GET['CustomerID'] . "'";
     $result = mysql_query($query) or die("SQL Error 1: " . mysql_error());
     while ($row = mysql_fetch_array($result, MYSQL_ASSOC)) {
	       $orders[] = array(
          'CustomerID' => $row['CustomerID'],
          'ShipCity' => $row['ShipCity'],
          'OrderID' => $row['OrderID'],
          'OrderDate' => $row['OrderDate'],
          'ShipName' => $row['ShipName'],
          'ShipAddress' => $row['ShipAddress'],
          'ShipCountry' => $row['ShipCountry']         
	       );
     }
     
     echo json_encode($orders);
     return;
  }
  
  // get data and store in a json array
  $query = "SELECT * FROM Customers";

  $result = mysql_query($query) or die("SQL Error 1: " . mysql_error());
  while ($row = mysql_fetch_array($result, MYSQL_ASSOC)) {
	  $customers[] = array(
          'CustomerID' => $row['CustomerID'],
          'CompanyName' => $row['CompanyName']
	       );
  }

  echo json_encode($customers);
?>