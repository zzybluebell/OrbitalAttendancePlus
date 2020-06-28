<?php
    require_once 'dbDetails.php';
    date_default_timezone_set("Asia/Singapore");
    $datetime = date('H:i:s',time());
    $date = date('w');

    if (isset($_POST['AdminNo']) && !empty($_POST["AdminNo"])) {
        $AdminNo = $_POST['AdminNo'];
        
        $conn = mysqli_connect(HOST,USER,PASS,DB) or die('Unable to Connect...');
        $select_sql = "SELECT A.ModuleName, B.TeacherName, A.Date, A.BeginTime, A.EndTime FROM module AS A INNER JOIN teacher AS B ON A.TeacherIC = B.TeacherIC WHERE A.AdminNo LIKE '%$AdminNo%' && Date = $date";
        $select_result = mysqli_query($conn, $select_sql);

        $ModuleName = "";
        $TeacherName = "";
        $BeginTime = "";
        $EndTime = "";
        $status = "";
        
        if (!$select_result || mysqli_num_rows($select_result) == 0){
            echo "error1";
        }
        else {
            while ($one_select_result = mysqli_fetch_assoc($select_result)) {
                $ModuleName_New = $one_select_result['ModuleName'];
                $ModuleName = $ModuleName. ",".$ModuleName_New;
                
                $TeacherName_New = $one_select_result['TeacherName'];
                $TeacherName = $TeacherName. ",".$TeacherName_New;
                
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
            $ModuleName = substr($ModuleName, 1);
            $TeacherName = substr($TeacherName, 1);
            $BeginTime = substr($BeginTime, 1);
            $EndTime = substr($EndTime, 1);
            $status = substr($status, 1);
            
            echo $ModuleName."<br>";
            echo $TeacherName."<br>";
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