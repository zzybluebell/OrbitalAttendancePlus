<?php

if(isset($_GET['send_notification']) && isset($_GET['Notification']) && isset($_GET['ModuleCode']) && isset($_GET['TeacherIC']) && isset($_GET['Content'])){
    $NotificationId = $_GET['Notification'];
    $ModuleCode = $_GET['ModuleCode'];
    $TeacherIC = $_GET['TeacherIC'];
    $Content = $_GET['Content'];
    
    define('API_ACCESS_KEY', 'AAAAZydAEUE:APA91bEsISTc9OXi9GhQJk-QhU-YTGu4yS7pTWSEe_1mPqYNJ-PcQjigjHlZ1QcKGPAk91IqFK8vSX5SkJ_sqIFa0rpNgGITES8nO7HwpO3O4XeDgPCR9s-W1Hr9F-3j9OaJ1YEHENtgRq3CCE23zLN2SgUjhbDO6Q');
    
    require_once 'dbDetails.php';
    $conn = mysqli_connect(HOST,USER,PASS,DB) or die('Unable to Connect...');
    $select_sql = "SELECT AdminNo FROM module WHERE ModuleCode = '$ModuleCode' && TeacherIC = '$TeacherIC'";
    $select_result = mysqli_query($conn, $select_sql);
    $one_select_result = mysqli_fetch_assoc($select_result);
    $AdminNo = $one_select_result["AdminNo"];
    $AdminNo_arr = explode(",",$AdminNo);
    $New_AdminNo = "";
    
    for($i=0; $i<count($AdminNo_arr); $i++) {
        $New_AdminNo = $New_AdminNo."'".$AdminNo_arr[$i]."',";
    }
    
    $New_AdminNo = substr($New_AdminNo, 0, strlen($New_AdminNo)-1);
    
    $select_sql2 = "SELECT * FROM student WHERE AdminNo in ($New_AdminNo)";
    $select_result2 = mysqli_query($conn, $select_sql2);
        
    if (!$select_result2 || mysqli_num_rows($select_result2) == 0){
                    
    }
    else {
        while ($one_select_result2 = mysqli_fetch_assoc($select_result2)) {
            $FirebaseToken = $one_select_result2["FirebaseToken"];
            $msg = array(
                'body' 	=> $_GET['Content'],
                'title'	=> $_GET['ModuleCode'],
            );
            $fields = array(
                'to' => $FirebaseToken,
                'notification'	=> $msg
            );

            $headers = array(
                'Authorization: key=' . API_ACCESS_KEY,
                'Content-Type: application/json'
            );

            $ch = curl_init();
            curl_setopt( $ch,CURLOPT_URL, 'https://fcm.googleapis.com/fcm/send' );
            curl_setopt( $ch,CURLOPT_POST, true );
            curl_setopt( $ch,CURLOPT_HTTPHEADER, $headers );
            curl_setopt( $ch,CURLOPT_RETURNTRANSFER, true );
            curl_setopt( $ch,CURLOPT_SSL_VERIFYPEER, false );
            curl_setopt( $ch,CURLOPT_POSTFIELDS, json_encode( $fields ) );
            $result = curl_exec($ch);
            echo $result;
            curl_close($ch);
        }
    }
    mysqli_close($conn);
    header("Location:notification.php?Notification=$NotificationId");
}
?>