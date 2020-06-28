<?php
    require_once 'dbDetails.php';

    date_default_timezone_set("Asia/Singapore");
    $timestamp = date('y-m-d H:i:s',time());

    if (isset($_POST['ModuleId']) && !empty($_POST["ModuleId"]) && isset($_POST['MacAddress']) && !empty($_POST["MacAddress"])) {
        $ModuleId = $_POST['ModuleId'];
        $MacAddress = $_POST['MacAddress'];
        
        $conn = mysqli_connect(HOST,USER,PASS,DB) or die('Unable to Connect...');
        
        $search_date_sql = "SELECT A.BeginTime, A.EndTime, B.Date FROM module AS A INNER JOIN attendance AS B ON A.Id = B.ModuleId WHERE B.ModuleId = $ModuleId";
        $search_date_result = mysqli_query($conn, $search_date_sql);

        $count = 0;
        
        if (!$search_date_result || mysqli_num_rows($search_date_result) == 0){
            $count = 0;
        }
        else {
            while ($one_search_date_result = mysqli_fetch_assoc($search_date_result)) {
                $count++;
            }
        }
        
        if($count == 0) {
            $search_sql = "SELECT SheetId FROM module WHERE Id = $ModuleId";
            $search_result = mysqli_query($conn, $search_sql);
            $one_search_result = mysqli_fetch_assoc($search_result);

            $SheetId = $one_search_result["SheetId"];

            $scriptURL = "https://script.google.com/macros/s/AKfycbz8SFhii5HnDo_vJId0_zQd2H1yO-e5KtG4O7k0l-w/dev?action=beginattendance&SheetId=".$SheetId;

            $beginAttendanceJson = file_get_contents($scriptURL);
            $beginAttendanceJson = json_decode($beginAttendanceJson, true);
            $beginAttendance = $beginAttendanceJson['result'];
            
            if($beginAttendance == "succeeded") {
                $select_sql = "SELECT MacAddress FROM attendance WHERE Id = $ModuleId";
                $select_result = mysqli_query($conn, $select_sql);
                $one_select_result = mysqli_fetch_assoc($select_result);
                
                if (!$select_result || mysqli_num_rows($select_result) == 0){
                    $insert_sql = "INSERT INTO attendance (ModuleId, Date, MacAddress) VALUES ($ModuleId, '$timestamp', '$MacAddress')";
                    $insert_result = mysqli_query($conn, $insert_sql);
                    if($insert_result) {
                        echo "succeeded";
                    }
                    else {
                        echo "error1";
                    }
                }
                else {
                    echo "error2";
                }
            }
            else {
                echo "error3";
            }
            mysqli_close($conn);
        }
        else {
            echo "error4";
        }
    }
    else {
        echo "error5";
    }
?>