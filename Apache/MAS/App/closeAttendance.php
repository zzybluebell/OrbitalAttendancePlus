<?php
    require_once 'dbDetails.php';

    date_default_timezone_set("Asia/Singapore");
    $timestamp = date('y-m-d H:i:s',time());

    if (isset($_POST['ModuleId']) && !empty($_POST["ModuleId"]) && isset($_POST['MacAddress']) && !empty($_POST["MacAddress"])) {
        $ModuleId = $_POST['ModuleId'];
        $MacAddress = $_POST['MacAddress'];
        
        $conn = mysqli_connect(HOST,USER,PASS,DB) or die('Unable to Connect...');
        
        $delete_sql = "DELETE FROM attendance WHERE ModuleId = '$ModuleId' && MacAddress = '$MacAddress'";
        $delete_result = mysqli_query($conn, $delete_sql);
        mysqli_close($conn);
        
        echo "succeeded";
    }
    else {
        echo "error";
    }
?>