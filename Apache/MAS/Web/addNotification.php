<?php
    require_once 'dbDetails.php';

    session_start();
    
    if (isset($_POST['NotificationId']) && !empty($_POST["NotificationId"])) {
        $NotificationId = $_POST['NotificationId'];
        
        if(isset($_POST['TeacherIC']) && !empty($_POST["TeacherIC"]) && isset($_POST['ModuleCode']) && !empty($_POST["ModuleCode"]) && isset($_POST['Content']) && !empty($_POST["Content"])){
        
            $TeacherIC = $_POST['TeacherIC'];
            $ModuleCode = $_POST['ModuleCode'];
            $Content = $_POST['Content'];

            $conn = mysqli_connect(HOST,USER,PASS,DB) or die('Unable to Connect...');
            $count = 0;
            $search_sql = "SELECT * FROM notification WHERE ModuleCode = '$ModuleCode' && TeacherIC = '$TeacherIC'";
            $search_result = mysqli_query($conn, $search_sql);

            if (!$search_result || mysqli_num_rows($search_result) == 0){
                $count++;
            }

            if($count != 0) {
                $insert_sql = "INSERT INTO notification (ModuleCode, TeacherIC, Content) VALUES ('$ModuleCode', '$TeacherIC', '$Content')";
                $insert_result = mysqli_query($conn, $insert_sql);
                mysqli_close($conn);

                if($insert_result) {
                    header("Location:api.php?send_notification&Notification=$NotificationId&ModuleCode=$ModuleCode&TeacherIC=$TeacherIC&Content=$Content");
                }
                else {
                    header("Location:notification.php?Notification=$NotificationId&&status=failed");
                }
            }
            else {
                $update_sql = "UPDATE notification SET Content = '$Content' WHERE ModuleCode = '$ModuleCode' && TeacherIC = '$TeacherIC'";
                $update_result = mysqli_query($conn, $update_sql);
                mysqli_close($conn);

                if($update_result) {
                    header("Location:api.php?send_notification&Notification=$NotificationId&ModuleCode=$ModuleCode&TeacherIC=$TeacherIC&Content=$Content");
                }
                else {
                    header("Location:notification.php?Notification=$NotificationId&&status=failed");
                }
            }
        }
        else {
            header("Location:notification.php?Notification=$NotificationId&&status=failed");
        }
    }
    else {
        header("Location:login.html");
    }
?>