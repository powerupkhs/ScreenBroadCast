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
// get data and store in a json array
$query = "SELECT * FROM employees";

if (isset($_GET['insert']))
{
	// INSERT COMMAND 
	$insert_query = "INSERT INTO employees(`FirstName`, `LastName`, `Title`, `Address`, `City`, `Country`, `Notes`) VALUES ('".$_GET['FirstName']."','".$_GET['LastName']."','".$_GET['Title']."','".$_GET['Address']."','".$_GET['City']."','".$_GET['Country']."','".$_GET['Notes']."')";
	
    $result = mysql_query($insert_query) or die("SQL Error 1: " . mysql_error());
    mysql_close($connect);  
    echo $result;
}
else if (isset($_GET['update']))
{
	// UPDATE COMMAND 
	// disable foreign key checks.	
	 mysql_query("SET foreign_key_checks = 0");	
	$update_query = "UPDATE employees SET `FirstName`='".$_GET['FirstName']."',
	`LastName`='".$_GET['LastName']."',
	`Title`='".$_GET['Title']."',
	`Address`='".$_GET['Address']."',
	`City`='".$_GET['City']."',
	`Country`='".$_GET['Country']."',
	`Notes`='".$_GET['Notes']."' WHERE EmployeeID='".$_GET['EmployeeID']."'";
	 $result = mysql_query($update_query) or die("SQL Error 1: " . mysql_error());
	// enable foreign key checks.	
	 mysql_query("SET foreign_key_checks = 1");	
     mysql_close($connect);
     echo $result;
}
else if (isset($_GET['delete']))
{
	// DELETE COMMAND 
	$delete_query = "DELETE FROM employees WHERE `EmployeeID`='".$_GET['EmployeeID']."'";	
	$result = mysql_query($delete_query) or die("SQL Error 1: " . mysql_error());
    mysql_close($connect);
    echo $result;
}
else
{
    // SELECT COMMAND
	$result = mysql_query($query) or die("SQL Error 1: " . mysql_error());
	while ($row = mysql_fetch_array($result, MYSQL_ASSOC)) {
		$employees[] = array(
			'EmployeeID' => $row['EmployeeID'],
			'FirstName' => $row['FirstName'],
			'LastName' => $row['LastName'],
			'Title' => $row['Title'],
			'Address' => $row['Address'],
			'City' => $row['City'],
			'Country' => $row['Country'],
			'Notes' => $row['Notes']
		  );
	}
	 
	echo json_encode($employees);
}
?>