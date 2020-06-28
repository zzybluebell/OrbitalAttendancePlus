<?php
    require_once 'dbDetails.php';

    session_start();
    
    if (isset($_POST['TeacherIC']) && !empty($_POST["TeacherIC"]) && isset($_POST['Password']) && !empty($_POST["Password"])) {
        $TeacherIC = $_POST['TeacherIC'];
        $Password = $_POST['Password'];
        
        $conn = mysqli_connect(HOST,USER,PASS,DB) or die('Unable to Connect...');
        $select_sql = "SELECT * FROM teacher WHERE TeacherIC = '$TeacherIC' and Password ='$Password'";
        $select_result = mysqli_query($conn, $select_sql);
        $result_found = mysqli_num_rows($select_result);
        mysqli_close($conn);
        
        if($result_found >= 1) {
            $_SESSION['TeacherIC'] = $TeacherIC;
            header("Location:teacherInfo.php");
        }
        else {
            header("Location:login.html?Login=failed");
        }
    }
    else {
        header("Location:login.html?Login=failed");
    }
?>