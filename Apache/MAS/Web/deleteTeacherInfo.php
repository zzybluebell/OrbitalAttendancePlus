<?php
    require_once 'dbDetails.php';

    session_start();
    
    if (isset($_POST['Delete']) && !empty($_POST["Delete"])) {
        $DeleteId = $_POST['Delete'];
        
        $conn = mysqli_connect(HOST,USER,PASS,DB) or die('Unable to Connect...');
        $search_sql = "SELECT SheetId FROM module WHERE Id = $DeleteId";
        $search_result = mysqli_query($conn, $search_sql);
        $one_search_result = mysqli_fetch_assoc($search_result);
        $SheetId = $one_search_result["SheetId"];
        
        $scriptURL = "https://script.google.com/macros/s/AKfycbz8SFhii5HnDo_vJId0_zQd2H1yO-e5KtG4O7k0l-w/dev?action=deletesheet&SheetId=".$SheetId;
        
        $deleteSheetJson = file_get_contents($scriptURL);
        $deleteSheetJson = json_decode($deleteSheetJson, true);
        $deleteSheet = $deleteSheetJson['result'];
        
        if($deleteSheet == "succeeded") {
            $delete_sql = "DELETE FROM module WHERE Id = '$DeleteId'";
            $delete_result = mysqli_query($conn, $delete_sql);
            mysqli_close($conn);
            header("Location:teacherInfo.php"); 
        }
        else {
            mysqli_close($conn);
            header("Location:teacherInfo.php?Delete=failed");
        }
    }
    else {
        header("Location:teacherInfo.php?Delete=failed");
    }
?>