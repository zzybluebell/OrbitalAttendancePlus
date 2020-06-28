<?php
    require_once 'dbDetails.php';

    if(isset($_POST["FirebaseToken"]) && !empty($_POST['FirebaseToken']) && isset($_POST["AdminNo"]) && !empty($_POST['AdminNo'])) {
        $FirebaseToken = $_POST["FirebaseToken"];
        $AdminNo = $_POST["AdminNo"];
            
        $conn = mysqli_connect(HOST,USER,PASS,DB) or die('Unable to Connect...');
        $count = 0;
        $search_sql = "SELECT * FROM student WHERE AdminNo = '$AdminNo'";
        $search_result = mysqli_query($conn, $search_sql);
        
        if (!$search_result || mysqli_num_rows($search_result) == 0){
            $count++;
        }

        if($count != 0) {
            $insert_sql = "INSERT INTO student (AdminNo, FirebaseToken) VALUES ('$AdminNo', '$FirebaseToken')";
            $insert_result = mysqli_query($conn, $insert_sql);
            mysqli_close($conn);

            if($insert_result) {
                echo "succeeded";
            }
            else {
                echo "error";
            }
        }
        else {
            $update_sql = "UPDATE student SET FirebaseToken = '$FirebaseToken' WHERE AdminNo = '$AdminNo'";
            $update_result = mysqli_query($conn, $update_sql);
            mysqli_close($conn);

            if($update_result) {
                echo "succeeded";
            }
            else {
                echo "error";
            }
        }
    }
    else {
        echo "error";
    }
?>