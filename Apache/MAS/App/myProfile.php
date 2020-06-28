<?php 
    require_once 'dbDetails.php';

    $upload_path = 'originals/';

    date_default_timezone_set("Asia/Singapore");
    $timestamp = date('y-m-d H:i:s',time());
 
    if($_SERVER['REQUEST_METHOD']=='POST'){
        if(isset($_POST['adminNo']) && !empty($_POST["adminNo"])){
             
            $adminNo = $_POST['adminNo'];
            $conn = mysqli_connect(HOST,USER,PASS,DB) or die('Unable to Connect...');
            $select_sql = "SELECT Image, TimeStamp FROM student WHERE AdminNo ='$adminNo'";
            $select_result = mysqli_query($conn, $select_sql);
            $one_select_result = mysqli_fetch_assoc($select_result);
            $Image = $one_select_result['Image'];
            $timestamp_old = $one_select_result['TimeStamp'];
            
            if(isset($_POST['name']) && !empty($_POST["name"])){
                $name = $_POST['name'];
                $update_sql1 = "UPDATE student SET name = '$name' WHERE AdminNo = '$adminNo'";
                $update_result1 = mysqli_query($conn, $update_sql1);
                if($update_result1) {
                    echo "succeeded1";
                }
                else {
                    echo "error";
                }
            }

            if(isset($_POST['SCG']) && !empty($_POST["SCG"])){
                $SCG = $_POST['SCG'];
                $update_sql2 = "UPDATE student SET SCG = '$SCG' WHERE AdminNo = '$adminNo'";
                $update_result2 = mysqli_query($conn, $update_sql2);
                if($update_result2) {
                    echo "succeeded2";
                }
                else {
                    echo "error";
                }
            }
            
            if(isset($_FILES['image']['name'])){
                if($Image == null || (strtotime($timestamp)-strtotime($timestamp_old))/(3600*24)>7){
                    $fileinfo = pathinfo($_FILES['image']['name']);
                    $extension = $fileinfo['extension'];
                    $file_path = $upload_path . $adminNo . '.' . $extension;
                    if(move_uploaded_file($_FILES['image']['tmp_name'], $file_path)) {  
                        $update_sql3 = "UPDATE student SET TimeStamp = '$timestamp', Image = '$file_path' WHERE AdminNo = '$adminNo'";
                        $update_result3 = mysqli_query($conn, $update_sql3);
                        if($update_result3) {
                            echo "succeeded";
                        }
                        else {
                            echo "error";
                        }
                    }
                    else {
                        echo "error";
                    }
                }
                else {
                    echo "errorc";
                }
            }
            mysqli_close($conn);
        }
        else{
            echo "error";
        }  
    }
    else {
        echo "error";
    }
?>