<?php
    require_once 'dbDetails.php';

    session_start();
    
    if (isset($_POST['ModuleCode']) && !empty($_POST["ModuleCode"]) && isset($_POST['ModuleName']) && !empty($_POST["ModuleName"]) && isset($_POST['Date']) && !empty($_POST["Date"]) && isset($_POST['BeginTime']) && !empty($_POST["BeginTime"]) && isset($_POST['EndTime']) && !empty($_POST["EndTime"]) && isset($_POST['AdminNo']) && !empty($_POST["AdminNo"])) {
        $ModuleCode = $_POST['ModuleCode'];
        $ModuleName = $_POST['ModuleName'];
        $Date = $_POST['Date'];
        $BeginTime = $_POST['BeginTime'];
        $EndTime = $_POST['EndTime'];
        $AdminNo = $_POST['AdminNo'];
        $TeacherIC = $_SESSION['TeacherIC'];
        
        $conn = mysqli_connect(HOST,USER,PASS,DB) or die('Unable to Connect...');
        
        if(isset($_POST['Update']) && !empty($_POST["Update"])) {
            $UpdateId = $_POST['Update'];
            
            $conn = mysqli_connect(HOST,USER,PASS,DB) or die('Unable to Connect...');
            $select_sql = "SELECT SheetId FROM module WHERE Id = '$UpdateId'";
            $select_result = mysqli_query($conn, $select_sql);
            $one_select_result = mysqli_fetch_assoc($select_result);
            $SheetId = $one_select_result['SheetId'];
            
            $scriptURL = "https://script.google.com/macros/s/AKfycbz8SFhii5HnDo_vJId0_zQd2H1yO-e5KtG4O7k0l-w/dev?action=updatesheet&SheetId=".$SheetId."&AdminNo=".$AdminNo;
            
            echo $scriptURL;

            $sheetIdJson = file_get_contents($scriptURL);
            $sheetIdJson = json_decode($sheetIdJson, true);
            
            if($sheetIdJson["result"] == "succeeded") {
                $update_sql = "UPDATE module SET ModuleCode = '$ModuleCode', ModuleName = '$ModuleName', Date = $Date, BeginTime = '$BeginTime', EndTime = '$EndTime', AdminNo = '$AdminNo' WHERE Id = '$UpdateId'";
                $update_result = mysqli_query($conn, $update_sql);
                mysqli_close($conn);
                if($update_result) {
                    header("Location:teacherInfo.php");
                }
                else {
                    header("Location:moduleInfo.php?Update=failed");
                }
            }
            else {
                header("Location:moduleInfo.php?Update=failed");
            }
        }
        else {
            $scriptURL = "https://script.google.com/macros/s/AKfycbz8SFhii5HnDo_vJId0_zQd2H1yO-e5KtG4O7k0l-w/dev?action=newsheet&AdminNo=".$AdminNo;

            $newSheetUrlJson = file_get_contents($scriptURL);
            $newSheetUrlJson = json_decode($newSheetUrlJson, true);
            $newSheetId = $newSheetUrlJson['result'];
            
            $insert_sql = "INSERT INTO module (ModuleCode, ModuleName, Date, BeginTime, EndTime, TeacherIC, AdminNo, SheetId) VALUES ('$ModuleCode', '$ModuleName', $Date, '$BeginTime', '$EndTime', '$TeacherIC', '$AdminNo', '$newSheetId')";
            $insert_result = mysqli_query($conn, $insert_sql);
            
            mysqli_close($conn);
            if($insert_result) {
                header("Location:teacherInfo.php");
            }
            else {
                header("Location:moduleInfo.php?Register=failed");
            }
        }

    }
    else {
        header("Location:moduleInfo.php?Login=failed");
    }
?>