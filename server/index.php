<?php
	require 'Slim/Slim.php';
	
	$app = new Slim();
	//$app->contentType('text/html; charset=utf-8');
	//$app->get('/wines', 'getWines');
	$app->get('/getEp/:id',  'getEp');
	$app->post('/getEpsByIds',  'getEpsByIds');
	$app->get('/getEpsByIdRange/:idStart/:idEnd',  'getEpsByIdRange');
	$app->get('/getEpTimestamps', 'getEpTimestamps');
	$app->put('/downloadEp/:id',  'downloadEp');
// 	$app->get('/wines/search/:query', 'findByName');
// 	$app->post('/wines', 'addWine');
// 	$app->put('/wines/:id', 'updateWine');
// 	$app->delete('/wines/:id',   'deleteWine');
	
	$app->run();
	
	function getEp($id) {
		$sql = "SELECT *, unix_timestamp(publishDate) as `publishDate` FROM eps WHERE id=:id";
		try {
			$db = getConnection();
			$stmt = $db->prepare($sql);
			$stmt->bindParam("id", $id);
			$stmt->execute();
			$ep = $stmt->fetchObject();
			$db = null;
			echo json_encode($ep);
		} catch(PDOException $e) {
			echo '{"error":{"text":'. $e->getMessage() .'}}';
		}
	}
	
	function getEpsByIds()
	{
		$ids = json_decode(Slim::getInstance()->request()->getBody());
		$ids = $ids->ids;
				
		$result = array();
		try {
			$db = getConnection();
			
			foreach($ids as $id)
			{
				$sql = "SELECT *, unix_timestamp(publishDate) as `publishDate` FROM eps WHERE id=:id";
				$stmt = $db->prepare($sql);
				$stmt->bindParam("id", $id);
				$stmt->execute();
				array_push($result, $stmt->fetchObject());
			}
			$db = null;
			echo '{"eps": ' . json_encode($result) . '}';
		} catch(PDOException $e) {
			echo '{"error":{"text":'. $e->getMessage() .'}}';
		}
	}
	
	function getEpsByIdRange($idStart, $idEnd)
	{
		try {
			$db = getConnection();
			$sql = "SELECT *, unix_timestamp(publishDate) as `publishDate` FROM eps WHERE id>=$idStart AND id<=$idEnd ORDER BY id DESC";
			$stmt = $db->prepare($sql);
			$stmt = $db->query($sql);
			$eps = $stmt->fetchAll(PDO::FETCH_OBJ);
			$db = null;
			echo '{"eps": ' . json_encode($eps) . '}';
		} catch(PDOException $e) {
			echo '{"error":{"text":'. $e->getMessage() .'}}';
		}
	}
	
	
	function getEpTimestamps()
	{
		$sql = "SELECT id, unix_timestamp(publishDate) as `publishDate` FROM eps ORDER BY id";
		try {
			$db = getConnection();
			$stmt = $db->query($sql);
			$epTimestamps = $stmt->fetchAll(PDO::FETCH_OBJ);
			$db = null;
			echo '{"epTimestamps": ' . json_encode($epTimestamps) . '}';
		} catch(PDOException $e) {
			echo '{"error":{"text":'. $e->getMessage() .'}}';
		}
	}
	
	function downloadEp($id)
	{
		$sql = "UPDATE eps SET downloadCount=downloadCount+1 WHERE id=$id";
		try {
			$db = getConnection();
			$stmt = $db->prepare($sql);
			if($stmt->execute())
			{
				echo '{"result": "true"}';
			}
			else
			{
				echo '{"result": "false"}';
			}
			$db = null;
		} catch(PDOException $e) {
			echo '{"error":{"text":'. $e->getMessage() .'}}';
		}
	}
	
	function getConnection() {
		$dbhost="localhost";
		$dbuser="root";
		$dbpass="12345";
		$dbname="api_jywave_com";
		$dbh = new PDO("mysql:host=$dbhost;dbname=$dbname", $dbuser, $dbpass);
		$dbh->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
		$dbh->exec("SET NAMES 'utf8'");
		return $dbh;
	}
?>
