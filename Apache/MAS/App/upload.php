<?php 
    require_once 'dbDetails.php';

    $upload_path = 'uploads/';

    date_default_timezone_set("Asia/Singapore");
    $timestamp = date('y-m-d H:i:s',time());
    $datetime = date('H:i:s',time());
    $Date = date('w');
 
    if($_SERVER['REQUEST_METHOD']=='POST'){
        
        if(isset($_POST['adminNo']) && !empty($_POST["adminNo"]) && isset($_POST['device']) && !empty($_POST["device"]) && isset($_FILES['image']['name'])){
            $conn = mysqli_connect(HOST,USER,PASS,DB) or die('Unable to Connect...');
            $adminNo = $_POST['adminNo'];
            $BTDevices = $_POST['device'];
            $BTDevices_Count = 0;

            $earlyDateTime = date('H:i:s', strtotime($datetime) + 1800);
            $lateDateTime = date('H:i:s', strtotime($datetime) - 1800);
            
            $select_sql = "SELECT A.SheetId, A.AdminNo, B.MacAddress FROM module AS A INNER JOIN attendance AS B ON A.Id = B.ModuleId WHERE A.AdminNo LIKE '%$adminNo%' && A.BeginTime < '$earlyDateTime' && A.BeginTime > '$lateDateTime' && A.Date = $Date";
            $select_result = mysqli_query($conn, $select_sql);
            
            if (!$select_result || mysqli_num_rows($select_result) == 0){
                echo "errora";
            }
            else {
                while ($one_select_result = mysqli_fetch_assoc($select_result)) {
                    $MacAddress = $one_select_result['MacAddress'];
                    $SheetId = $one_select_result['SheetId'];
                    $BTDevices_arr = explode(",", $BTDevices);
                    for ($i=0; $i<count($BTDevices_arr); $i++) {
                        if($MacAddress == $BTDevices_arr[$i]) {
                            $BTDevices_Count++;
                            break 2;
                        }
                    }
                }
                if($BTDevices_Count == 1) { 
                    $fileinfo = pathinfo($_FILES['image']['name']);
                    $extension = $fileinfo['extension'];
                    $file_path = $upload_path . $adminNo . 'new.' . $extension; 
                    if(move_uploaded_file($_FILES['image']['tmp_name'], $file_path)) {
                        include "faceComparisonV2.php";
                    }
                    else {
                        echo "errorb";
                    }
                }
                else {
                    echo "errorc";
                }
            }
            mysqli_close($conn);
        }
        else{
            echo "errord";
        }
    }
    else {
        echo "errore";
    }
?>