<?php
    require_once 'dbDetails.php';
    $response = array();
    $input = file_get_contents("php://input");
    $jsonObj = json_decode($input, true);
    $id = $jsonObj['id'];
    date_default_timezone_set("Asia/Singapore");
    $datetime = date('H:i:s',time());
    $date = date('w');

    if (isset($_POST['AdminNo']) && !empty($_POST["AdminNo"])) {
        $AdminNo = $_POST['AdminNo'];
        
        $conn = mysqli_connect(HOST,USER,PASS,DB) or die('Unable to Connect...');
        
        $select_sql = "SELECT * FROM student WHERE AdminNo = '$AdminNo'";
        $select_result = mysqli_query($conn, $select_sql);
        
        $AdminNo = "";
        $Name = "";
        $SCG = "";
        $Image = "";
        
        $response["status"] = "OK";
        if (!$select_result || mysqli_num_rows($select_result) == 0){
        }
        else {
            $one_select_result = mysqli_fetch_assoc($select_result);
            $response[0]["AdminNo"] = $one_select_result["AdminNo"];
            $response[0]["Name"] = $one_select_result["Name"];
            $response[0]["SCG"] = $one_select_result["SCG"];
            $response[0]["Image"] = fileToBase64($one_select_result["Image"]);
        }
        mysqli_close($conn);
    }
    else {
        $response["status"] = "Error";
    }
    echo json_encode($response);

    function fileToBase64($file){
        $base64_file = '';
        if(file_exists($file)){
            $mime_type= mime_content_type($file);
            $base64_data = base64_encode(file_get_contents($file));
            $base64_file = 'data:'.$mime_type.';base64,'.$base64_data;
        }
        return $base64_file;
    }
?>