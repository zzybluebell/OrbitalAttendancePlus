<?php
    require_once 'dbDetails.php';
    session_start();
    if (isset($_SESSION['TeacherIC']) && !empty($_SESSION["TeacherIC"])) {
        $TeacherIC = $_SESSION['TeacherIC'];
        echo $TeacherIC;
        echo $_FILES['iconImage']['tmp_name'];
        if (!empty($_FILES['iconImage']['tmp_name'])) {
            $allowedType = array("image/jpeg");
            if (in_array($_FILES["iconImage"]["type"], $allowedType)) {    
                if ($_FILES["iconImage"]["size"] < 5000000) { 
                    $extension = pathinfo($_FILES['iconImage']['name'], PATHINFO_EXTENSION);
                    $target = "teacherImages/".$TeacherIC.".".$extension;
                    $filename = "../App/teacherImages/".$TeacherIC.".".$extension;
                    $result = move_uploaded_file($_FILES["iconImage"]["tmp_name"], $filename);
                    if($result) {
                        header("Location:teacherInfo.php");
                    }
                    else {
                        header("Location:teacherInfo.php?changeicon=failed");
                    }
                }
                else {
                    header("Location:teacherInfo.php?changeicon=failed");
                }
            }
            else {
                header("Location:teacherInfo.php?changeicon=failed");
            }
        }
        else {
            header("Location:teacherInfo.php?changeicon=failed");
        }
    }
    else {
        header("Location:login.html");
    }
?>