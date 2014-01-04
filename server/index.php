<?php
require 'Slim/Slim.php';

date_default_timezone_set("Asia/Shanghai");

$app = new Slim ();
// $app->contentType('text/html; charset=utf-8');
// $app->get('/wines', 'getWines');
$app->get ( '/getEp/:id', 'getEp' );
$app->post ( '/getEpsByIds', 'getEpsByIds' );
$app->get ( '/getEpsByIdRange/:idStart/:idEnd', 'getEpsByIdRange' );
$app->get ( '/getEpTimestamps', 'getEpTimestamps' );
$app->get ( '/getAllPodcasters', 'getAllPodcasters' );
$app->post ( '/getPodcastersByIds', 'getPodcastersByIds' );
$app->get ( '/getPodcasterTimestamps', 'getPodcasterTimestamps' );
$app->post ( '/iLikePodcaster/:id', 'iLikePodcaster' );
$app->post ( '/iDontLikePodcaster/:id', 'iDontLikePodcaster' );
$app->post ( '/downloadEp/:id', 'downloadEp' );
$app->post ( '/activeUser', 'activeUser' );
$app->post ( '/feedback', 'feedback' );
// $app->get('/wines/search/:query', 'findByName');
// $app->post('/wines', 'addWine');
// $app->put('/wines/:id', 'updateWine');
// $app->delete('/wines/:id', 'deleteWine');

$app->run ();

//------------------------------------------------------------------------------
//						EPs
//------------------------------------------------------------------------------

function getEp($id) {
	$sql = "SELECT *, unix_timestamp(publishDate) as `publishDate` FROM eps WHERE id=:id";
	try {
		$db = getConnection ();
		$stmt = $db->prepare ( $sql );
		$stmt->bindParam ( "id", $id );
		$stmt->execute ();
		$ep = $stmt->fetchObject ();
		$db = null;
		echo json_encode ( $ep );
	} catch ( PDOException $e ) {
		echo '{"error":{"text":' . $e->getMessage () . '}}';
	}
}

function getEpsByIds() {
	$ids = json_decode ( Slim::getInstance ()->request ()->getBody () );
	$ids = $ids->ids;
	
	$result = array ();
	try {
		$db = getConnection ();
		
		foreach ( $ids as $id ) {
			$sql = "SELECT *, unix_timestamp(publishDate) as `publishDate` FROM eps WHERE id=:id";
			$stmt = $db->prepare ( $sql );
			$stmt->bindParam ( "id", $id );
			$stmt->execute ();
			array_push ( $result, $stmt->fetchObject () );
		}
		$db = null;
		echo '{"eps": ' . json_encode ( $result ) . '}';
	} catch ( PDOException $e ) {
		echo '{"error":{"text":' . $e->getMessage () . '}}';
	}
}

function getEpsByIdRange($idStart, $idEnd) {
	try {
		$db = getConnection ();
		$sql = "SELECT *, unix_timestamp(publishDate) as `publishDate` FROM eps WHERE id>=$idStart AND id<=$idEnd ORDER BY id DESC";
		$stmt = $db->prepare ( $sql );
		$stmt = $db->query ( $sql );
		$eps = $stmt->fetchAll ( PDO::FETCH_OBJ );
		$db = null;
		echo '{"eps": ' . json_encode ( $eps ) . '}';
	} catch ( PDOException $e ) {
		echo '{"error":{"text":' . $e->getMessage () . '}}';
	}
}

function getEpTimestamps() {
	$sql = "SELECT id, unix_timestamp(publishDate) as `publishDate` FROM eps ORDER BY id";
	try {
		$db = getConnection ();
		$stmt = $db->query ( $sql );
		$epTimestamps = $stmt->fetchAll ( PDO::FETCH_OBJ );
		$db = null;
		echo '{"epTimestamps": ' . json_encode ( $epTimestamps ) . '}';
	} catch ( PDOException $e ) {
		echo '{"error":{"text":' . $e->getMessage () . '}}';
	}
}

function downloadEp($id) {
	$sql = "UPDATE eps SET downloadCount=downloadCount+1 WHERE id=$id";
	try {
		$db = getConnection ();
		$stmt = $db->prepare ( $sql );
		if ($stmt->execute ()) {
			echo '{"result": "true"}';
		} else {
			echo '{"result": "false"}';
		}
		$db = null;
	} catch ( PDOException $e ) {
		echo '{"error":{"text":' . $e->getMessage () . '}}';
	}
}

function activeUser() {
	$user = json_decode ( Slim::getInstance ()->request ()->getBody () );
	
	try {
		$db = getConnection ();
		
		$sql = "SELECT id FROM users WHERE imei=:imei";
		$stmt = $db->prepare ( $sql );
		$stmt->bindParam ( "imei", $user->imei );
		$stmt->execute ();
		$result = $stmt->fetchObject ();
		
		if ($result != null) {
			$sql = "UPDATE users SET model=:model, category=:category, sysVersion=:sysVersion, appVersion=:appVersion, manufacturer=:manufacturer, activeTime=:activeTime WHERE imei=:imei";
			$stmt = $db->prepare ( $sql );
			$stmt->bindParam ( "model", $user->model );
			$stmt->bindParam ( "imei", $user->imei );
			$stmt->bindParam ( "category", $user->category );
			$stmt->bindParam ( "sysVersion", $user->sysVersion );
			if(isset($user->appVersion))
			{
				$stmt->bindParam ( "appVersion", $user->appVersion );
			}
			else
			{
				$appVersion = "1.0";
				$stmt->bindParam ( "appVersion", $appVersion );
			}
			$stmt->bindParam ( "manufacturer", $user->manufacturer );
			$now = date("Y-m-d H:i:s");
			$stmt->bindParam ( "activeTime", $now);
			$stmt->execute ();
			
			$user->id = $result->id;
		} else {
			$sql = "INSERT INTO users(model, imei, category, sysVersion, appVersion, manufacturer, activeTime) VALUES (:model, :imei, :category, :sysVersion, :appVersion, :manufacturer, :activeTime)";
			$stmt = $db->prepare ( $sql );
			$stmt->bindParam ( "model", $user->model );
			$stmt->bindParam ( "imei", $user->imei );
			$stmt->bindParam ( "category", $user->category );
			$stmt->bindParam ( "sysVersion", $user->sysVersion );
			if(isset($user->appVersion))
			{
				$stmt->bindParam ( "appVersion", $user->appVersion );
			}
			else
			{
				$appVersion = "1.0";
				$stmt->bindParam ( "appVersion", $appVersion );
			}
			$stmt->bindParam ( "manufacturer", $user->manufacturer );
			$now = date("Y-m-d H:i:s");
			$stmt->bindParam ( "activeTime", $now);
			$stmt->execute ();
			$user->id = $db->lastInsertId ();
		}
		$db = null;
		echo '{"userActive": {"id": "' . $user->id . '"}}';
	} catch ( PDOException $e ) {
		echo '{"error":{"text":' . $e->getMessage () . '}}';
	}
}

function feedback() {
	$feedback = json_decode ( Slim::getInstance ()->request ()->getBody () );
	
	try {
		$db = getConnection ();
		
		$sql = "INSERT INTO feedback(userId, nickname, content, submitTime) VALUES (:userId, :nickname, :content, NOW())";
		$stmt = $db->prepare ( $sql );
		$stmt->bindParam ( "userId", $feedback->userId );
		$stmt->bindParam ( "nickname", $feedback->nickname );
		$stmt->bindParam ( "content", $feedback->content );
		$stmt->execute ();
		$feedback->id = $db->lastInsertId ();
		$db = null;
		echo '{"feedback": {"id": "' . $feedback->id . '"}}';
	} catch ( PDOException $e ) {
		echo '{"error":{"text":' . $e->getMessage () . '}}';
	}
}

//------------------------------------------------------------------------------
//						Podcasters
//------------------------------------------------------------------------------

function getAllPodcasters()
{
	try {
		$db = getConnection ();
		$sql = "SELECT *, unix_timestamp(updateTime) as `updateTime` FROM podcasters ORDER BY id";
		$stmt = $db->prepare ( $sql );
		$stmt = $db->query ( $sql );
		$podcasters = $stmt->fetchAll ( PDO::FETCH_OBJ );
		$db = null;
		echo '{"podcasters": ' . json_encode ( $podcasters ) . '}';
	} catch ( PDOException $e ) {
		echo '{"error":{"text":' . $e->getMessage () . '}}';
	}
}

function getPodcastersByIds()
{
	$ids = json_decode ( Slim::getInstance ()->request ()->getBody () );
	$ids = $ids->ids;
	
	$result = array ();
	try {
		$db = getConnection ();
	
		foreach ( $ids as $id ) {
			$sql = "SELECT *, unix_timestamp(updateTime) as `updateTime` FROM podcasters WHERE id=:id";
			$stmt = $db->prepare ( $sql );
			$stmt->bindParam ( "id", $id );
			$stmt->execute ();
			array_push ( $result, $stmt->fetchObject () );
		}
		$db = null;
		echo '{"podcasters": ' . json_encode ( $result ) . '}';
	} catch ( PDOException $e ) {
		echo '{"error":{"text":' . $e->getMessage () . '}}';
	}
}

function getPodcasterTimestamps()
{
	$sql = "SELECT id, unix_timestamp(updateTime) as `updateTime` FROM podcasters ORDER BY id";
	try {
		$db = getConnection ();
		$stmt = $db->query ( $sql );
		$podcasterTimestamps = $stmt->fetchAll ( PDO::FETCH_OBJ );
		$db = null;
		echo '{"podcasterTimestamps": ' . json_encode ( $podcasterTimestamps ) . '}';
	} catch ( PDOException $e ) {
		echo '{"error":{"text":' . $e->getMessage () . '}}';
	}
}

function iLikePodcaster($id)
{
	try {
		$db = getConnection ();
	
		$sql = "UPDATE podcasters SET heart=heart+1, updateTime=NOW() WHERE id=:id";
		$stmt = $db->prepare ( $sql );
		$stmt->bindParam ( "id", $id );
		$stmt->execute ();
		
		$sql = "SELECT heart FROM podcasters WHERE id=:id";
		$stmt = $db->prepare ( $sql );
		$stmt->bindParam ( "id", $id );
		$stmt->execute ();
		$result = $stmt->fetchObject ();
		
		$db = null;
		echo '{"iLikePodcaster": {"id": "' . $id . '", "heart": "'.$result->heart.'"}}';
	} catch ( PDOException $e ) {
		echo '{"error":{"text":' . $e->getMessage () . '}}';
	}
}

function iDontLikePodcaster($id)
{
	try {
		$db = getConnection ();
	
		$sql = "UPDATE podcasters SET heart=heart-1, updateTime=NOW() WHERE id=:id";
		$stmt = $db->prepare ( $sql );
		$stmt->bindParam ( "id", $id );
		$stmt->execute ();
		
		$sql = "SELECT heart FROM podcasters WHERE id=:id";
		$stmt = $db->prepare ( $sql );
		$stmt->bindParam ( "id", $id );
		$stmt->execute ();
		$result = $stmt->fetchObject ();
		
		$db = null;
		echo '{"iDontLikePodcaster": {"id": "' . $id . '", "heart": "'.$result->heart.'"}}';
	} catch ( PDOException $e ) {
		echo '{"error":{"text":' . $e->getMessage () . '}}';
	}
}

function getConnection() {
	$dbhost = "localhost";
	$dbuser = "root";
	$dbpass = "12345";
	$dbname = "api_jywave_com";
	$dbh = new PDO ( "mysql:host=$dbhost;dbname=$dbname", $dbuser, $dbpass );
	$dbh->setAttribute ( PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION );
	$dbh->exec ( "SET NAMES 'utf8'" );
	return $dbh;
}

?>
