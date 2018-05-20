<?php
require_once __DIR__ . '/vendor/autoload.php';
// Use the youtube-dl-php wrapper
use YoutubeDl\YoutubeDl;
define("DOWNLOAD_FOLDER", "C:/Users/Jelly/Desktop/WinNMP/WWW/project2/files/");
define("DOWNLOAD_FOLDER_PUBLIC", "http://project2.duckdns.org:1234/files/");
header("Content-Type: application/json");
if(isset($_GET["link"]) && !empty($_GET["link"]))
{
	// get link from request and parses it for information
	$link = $_GET["link"];
	parse_str(parse_url($link, PHP_URL_QUERY), $queryvars);
	$id = $queryvars["v"]; // EgT_us6AsDg of https://www.youtube.com/watch?v=EgT_us6AsDg
	$file = DOWNLOAD_FOLDER.$id.".mp3"; // download location with appending .mp3
	$exists = file_exists($file); // file_exists checks if the file exists, returns a bool
	// youtube-dl options
	if($exists){
		// if the file already exists then skip the download
		$options = [
			'skip-download' => true
		];
	}
	else{
		// downloads the audio and converts to mp3
		$options = [
			'extract-audio' => true,
			'audio-format' => 'mp3',
			'audio-quality' => 0, 
			// output as "id of url".mp3
			'output' => '%(id)s.%(ext)s' // https://www.youtube.com/watch?v=EgT_us6AsDg becomes EgT_us6AsDg.mp3
		];
	}
	$dl = new YoutubeDl($options); // creates a new youtube-dl object with options
	$dl->setBinPath('C:/MPV/youtube-dl'); // location of youtube-dl
	$dl->setDownloadPath(DOWNLOAD_FOLDER);
	try {
		// starts the download process
		$audio = $dl->download($link); // if skip-download is true then it doesn't download anything
		$file = DOWNLOAD_FOLDER_PUBLIC.$id.".mp3";
		
		echo json_encode(array("error" => false, "title" => $audio->getTitle(), "duration" => $audio->getDuration(), "file" => $file));
	} catch (Exception $e) {
		echo json_encode(array("error" => true, "message" => $e->getMessage()));
	}
}
else
	echo json_encode(array("error" => true, "message" => "Invalid request"));
?>