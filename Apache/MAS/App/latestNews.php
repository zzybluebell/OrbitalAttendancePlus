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
        
        $select_sql = "SELECT B.ModuleName, A.Content, A.TimeStamp FROM notification AS A INNER JOIN module AS B ON A.ModuleCode = B.ModuleCode && A.TeacherIC = B.TeacherIC WHERE B.AdminNo LIKE '%$AdminNo%'";
        
        $select_result = mysqli_query($conn, $select_sql);
        
        $ModuleName = "";
        $Content = "";
        $TimeStamp = "";
        
        if (!$select_result || mysqli_num_rows($select_result) == 0){
            $response["status"] = "NoRecord";
        }
        else {
            $i = 0;
            $response["status"] = "OK";
            while ($one_select_result = mysqli_fetch_assoc($select_result)) {
                $response[$i]["ModuleName"] = $one_select_result["ModuleName"];
                $response[$i]["Content"] = $one_select_result["Content"];
                $response[$i]["TimeStamp"] = $one_select_result["TimeStamp"];
                $i++;
            }
        }
        mysqli_close($conn);
    }
    else {
        $response["status"] = "Error";
    }
    echo json_encode($response);
?>