<?php
    require_once 'dbDetails.php';
    date_default_timezone_set("Asia/Singapore");
    $datetime = date('H:i:s',time());
    $date = date('w');

    if(isset($_POST["TeacherIC"]) && !empty($_POST['TeacherIC'])) {
        $TeacherIC = $_POST["TeacherIC"];
        
        $conn = mysqli_connect(HOST,USER,PASS,DB) or die('Unable to Connect...');
        $select_sql = "SELECT * FROM module WHERE TeacherIC = '$TeacherIC' && Date = $date";
        $select_result = mysqli_query($conn, $select_sql);
        
        $Id = "";
        $ModuleCode = "";
        $ModuleName = "";
        $BeginTime = "";
        $EndTime = "";
        $status = "";
        
        if (!$select_result || mysqli_num_rows($select_result) == 0){
            echo "error1";
        }
        else {
            while ($one_select_result = mysqli_fetch_assoc($select_result)) {
                $Id_New = $one_select_result['Id'];
                $Id = $Id. ",".$Id_New;
                
                $ModuleCode_New = $one_select_result['ModuleCode'];
                $ModuleCode = $ModuleCode. ",".$ModuleCode_New;
                
                $ModuleName_New = $one_select_result['ModuleName'];
                $ModuleName = $ModuleName. ",".$ModuleName_New;
                
                $BeginTime_New = $one_select_result['BeginTime'];
                $BeginTime = $BeginTime. ",".$BeginTime_New;
                
                $EndTime_New = $one_select_result['EndTime'];
                $EndTime = $EndTime. ",".$EndTime_New;
                
                $earlyDateTime = date('H:i:s', strtotime($datetime) + 1800);
                $lateDateTime = date('H:i:s', strtotime($datetime) - 1800);
                
                if(strtotime($BeginTime_New)<strtotime($earlyDateTime) && strtotime($BeginTime_New)>strtotime($lateDateTime)) {
                    $status_New = "Available";
                    $status = $status. ",".$status_New;
                }
                else {
                    $status_New = "Unavailable";
                    $status = $status. ",".$status_New;
                }
            }
            $Id = substr($Id, 1);
            $ModuleCode = substr($ModuleCode, 1);
            $ModuleName = substr($ModuleName, 1);
            $BeginTime = substr($BeginTime, 1);
            $EndTime = substr($EndTime, 1);
            $status = substr($status, 1);
            
            echo $Id."<br>";
            echo $ModuleCode."<br>";
            echo $ModuleName."<br>";
            echo $BeginTime."<br>";
            echo $EndTime."<br>";
            echo $status;
        }
        mysqli_close($conn);
    }
    else {
        echo "error2";
    }
?>