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
$query = "SELECT * FROM customers";
if (isset($_GET['customerid']))
{
	$pagenum = $_GET['pagenum'];
	$pagesize = $_GET['pagesize'];
	$start = $pagenum * $pagesize;
	$query = "SELECT SQL_CALC_FOUND_ROWS * FROM Orders WHERE CustomerID='" .$_GET['customerid'] . "'";
	$query .= " LIMIT $start, $pagesize";
	$result = mysql_query($query) or die("SQL Error 1: " . mysql_error());
	$sql = "SELECT FOUND_ROWS() AS `found_rows`;";
	$rows = mysql_query($sql);
	$rows = mysql_fetch_assoc($rows);
	$total_rows = $rows['found_rows'];	
	if (isset($_GET['sortdatafield']))
	{
		$sortfield = $_GET['sortdatafield'];
		$sortorder = $_GET['sortorder'];
		
		if ($sortfield != NULL)
		{		
			if ($sortorder == "desc")
			{
				$query = "SELECT * FROM Orders WHERE CustomerID='" .$_GET['customerid'] . "' ORDER BY" . " " . $sortfield . " DESC LIMIT $start, $pagesize";
			}
			else if ($sortorder == "asc")
			{
				$query = "SELECT * FROM Orders WHERE CustomerID='" .$_GET['customerid'] . "' ORDER BY" . " " . $sortfield . " ASC LIMIT $start, $pagesize";
			}			
			$result = mysql_query($query) or die("SQL Error 1: " . mysql_error());
		}
	}
	// get data and store in a json array
	while ($row = mysql_fetch_array($result, MYSQL_ASSOC)) {
		$orders[] = array(
			'OrderDate' => $row['OrderDate'],
			'ShippedDate' => $row['ShippedDate'],
			'ShipName' => $row['ShipName'],
			'ShipAddress' => $row['ShipAddress'],
			'ShipCity' => $row['ShipCity'],
			'ShipCountry' => $row['ShipCountry']
		  );
	}
    $data[] = array(
       'TotalRows' => $total_rows,
	   'Rows' => $orders
	);
	echo json_encode($data);    	
}
else
{
	$pagenum = $_GET['pagenum'];
	$pagesize = $_GET['pagesize'];
	$start = $pagenum * $pagesize;
	$query = "SELECT SQL_CALC_FOUND_ROWS * FROM Customers LIMIT $start, $pagesize";
	$result = mysql_query($query) or die("SQL Error 1: " . mysql_error());
	$sql = "SELECT FOUND_ROWS() AS `found_rows`;";
	$rows = mysql_query($sql);
	$rows = mysql_fetch_assoc($rows);
	$total_rows = $rows['found_rows'];
	if (isset($_GET['sortdatafield']))
	{
		$sortfield = $_GET['sortdatafield'];
		$sortorder = $_GET['sortorder'];
		
		if ($sortfield != NULL)
		{		
			if ($sortorder == "desc")
			{
				$query = "SELECT * FROM Customers ORDER BY" . " " . $sortfield . " DESC LIMIT $start, $pagesize";
			}
			else if ($sortorder == "asc")
			{
				$query = "SELECT * FROM Customers ORDER BY" . " " . $sortfield . " ASC LIMIT $start, $pagesize";
			}			
			$result = mysql_query($query) or die("SQL Error 1: " . mysql_error());
		}
	}
	while ($row = mysql_fetch_array($result, MYSQL_ASSOC)) {
		$customers[] = array(
			'CustomerID' => $row['CustomerID'],
			'CompanyName' => $row['CompanyName'],
			'ContactName' => $row['ContactName'],
			'ContactTitle' => $row['ContactTitle'],
			'Address' => $row['Address'],
			'City' => $row['City'],
			'Country' => $row['Country']
		  );
	}
    $data[] = array(
       'TotalRows' => $total_rows,
	   'Rows' => $customers
	);
	echo json_encode($data);
}
?>