<?php
      
// receive image as POST Parameter

$image = str_replace('data:image/png;base64,', '', $_POST['image']);
$image = str_replace(' ', '+', $image);
// Decode the Base64 encoded Image
$data = base64_decode($image);
// Create Image path with Image name and Extension
$file = 'images/' . "MyImage" . '.jpg';
// Save Image in the Image Directory
$success = file_put_contents($file, $data);