<?php
    require_once 'dbDetails.php';

    session_start();
    
    if (isset($_POST['TeacherIC']) && !empty($_POST["TeacherIC"]) && isset($_POST['TeacherName']) && !empty($_POST["TeacherName"]) && isset($_POST['Password']) && !empty($_POST["Password"]) && isset($_POST['Phone']) && !empty($_POST["Phone"]) && isset($_POST['Email']) && !empty($_POST["Email"]) && !empty($_FILES['iconImage']['tmp_name'])) {
        $TeacherIC = $_POST['TeacherIC'];
        $TeacherName = $_POST['TeacherName'];
        $Password = $_POST['Password'];
        $Phone = $_POST['Phone'];
        $Email = $_POST['Email'];
        
        $allowedType = array("image/jpeg");
        if(in_array($_FILES["iconImage"]["type"], $allowedType)) {    
            if ($_FILES["iconImage"]["size"] < 5000000) { 
                $extension = pathinfo($_FILES['iconImage']['name'], PATHINFO_EXTENSION);
                $target = "teacherImages/".$TeacherIC.".".$extension;
                $filename = "../App/teacherImages/".$TeacherIC.".".$extension;
                $result = move_uploaded_file($_FILES["iconImage"]["tmp_name"], $filename);
                if($result) {
                    $conn = mysqli_connect(HOST,USER,PASS,DB) or die('Unable to Connect...');

                    $count = 0;
                    $search_sql = "SELECT TeacherIC FROM teacher";
                    $search_result = mysqli_query($conn, $search_sql);

                    if (!$search_result || mysqli_num_rows($search_result) == 0){
                        $count++;
                    }
                    else {
                        while ($one_search_result = mysqli_fetch_assoc($search_result)) {
                            if($one_search_result['TeacherIC'] == $TeacherIC) {
                                mysqli_close($conn);
                                header("Location:register.html?Register=duplicate");
                            }
                            else {
                                $count++;
                            }
                        }
                    }
                    
                    if($count != 0) {
                        $insert_sql = "INSERT INTO teacher (TeacherIC, TeacherName, Password, Phone, Email, TeacherImage) VALUES ('$TeacherIC', '$TeacherName', '$Password', '$Phone', '$Email', '$target')";
                        $insert_result = mysqli_query($conn, $insert_sql);
                        mysqli_close($conn);

                        if($insert_result) {
                            header("Location:login.html");
                        }
                        else {
                            header("Location:register.html?Register=failed1");
                        }
                    }
                }
                else {
                    header("Location:register.html?Register=failed2");
                }
            }
            else {
                header("Location:register.html?Register=failed3");
            }
        }
        else {
            header("Location:register.html?Register=failed4");
        }

        

             

    }
    else {
        header("Location:register.html?Register=failed");
    }
?>