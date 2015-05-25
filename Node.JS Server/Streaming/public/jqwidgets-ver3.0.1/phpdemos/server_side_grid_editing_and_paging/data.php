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

	if (isset($_GET['update']))
	{
		// UPDATE COMMAND 
		$update_query = "UPDATE `Employees` SET `FirstName`='".$_GET['FirstName']."',
		`LastName`='".$_GET['LastName']."',
		`Title`='".$_GET['Title']."',
		`Address`='".$_GET['Address']."',
		`City`='".$_GET['City']."',
		`Country`='".$_GET['Country']."',
		`Notes`='".$_GET['Notes']."' WHERE `EmployeeID`='".$_GET['EmployeeID']."'";
		 $result = mysql_query($update_query) or die("SQL Error 1: " . mysql_error());
		 echo $result;
	}
	else
	{
		$pagenum = $_GET['pagenum'];
		$pagesize = $_GET['pagesize'];
		$start = $pagenum * $pagesize;
		$query = "SELECT SQL_CALC_FOUND_ROWS * FROM Employees LIMIT $start, $pagesize";

		$result = mysql_query($query) or die("SQL Error 1: " . mysql_error());
		$sql = "SELECT FOUND_ROWS() AS `found_rows`;";
		$rows = mysql_query($sql);
		$rows = mysql_fetch_assoc($rows);
		$total_rows = $rows['found_rows'];
		
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
		 
		$data[] = array(
		   'TotalRows' => $total_rows,
		   'Rows' => $employees
		);
		echo json_encode($data);
	}
?>