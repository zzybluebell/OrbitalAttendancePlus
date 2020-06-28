<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="pragma" content="no-cache"> 
        <meta http-equiv="Cache-Control" content="no-cache, must-revalidate"> 
        <meta http-equiv="expires" content="0">
        <?php
            require_once 'dbDetails.php';

            session_start();

            if (isset($_GET['Notification']) && !empty($_GET["Notification"])) {
                $NotificationId = $_GET['Notification'];

                $conn = mysqli_connect(HOST,USER,PASS,DB) or die('Unable to Connect...');
                $select_sql = "SELECT * FROM module WHERE Id = $NotificationId";
                $select_result = mysqli_query($conn, $select_sql);
            }
        ?> 
    </head>
    <body>
        <?php 
            $one_select_result = mysqli_fetch_assoc($select_result);
            $ModuleCode = $one_select_result['ModuleCode'];
            $TeacherIC = $one_select_result['TeacherIC'];
        ?>
        <h2>Module code: <?php echo $ModuleCode; ?></h2>
        <br><br><hr>
        <?php
            $select_sql2 = "SELECT * FROM notification WHERE ModuleCode = '$ModuleCode' && TeacherIC = '$TeacherIC'";
            $select_result2 = mysqli_query($conn, $select_sql2);
            if (!$select_result2 || mysqli_num_rows($select_result2) == 0){
                    
            }
            else {
                while ($one_select_result2 = mysqli_fetch_assoc($select_result2)) {
                ?>
                    <h3>Content:</h3>
                    <p><?php echo $one_select_result2['Content']; ?></p>
                    <p><?php echo $one_select_result2['TimeStamp']; ?></p>
                    <hr><br>
                <?php
                }
            }
            mysqli_close($conn);
        ?>
        <h3>Add Content:</h3>
        <div>
            <form method="post" action="addNotification.php">
                <input type="hidden" name="NotificationId" value="<?php echo $NotificationId; ?>"/>
                <input type="hidden" name="ModuleCode" value="<?php echo $ModuleCode; ?>"/>
                <input type="hidden" name="TeacherIC" value="<?php echo $TeacherIC; ?>"/>
                <textarea style="width:800px; height:100px;" name="Content"></textarea><br>
                <input type="submit" value="Submit">
            </form>
        </div>
        <hr>
        <a href="teacherInfo.php"><button>Back</button></a>
    </body>
</html>