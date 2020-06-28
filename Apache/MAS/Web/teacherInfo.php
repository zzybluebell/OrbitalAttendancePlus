<!DOCTYPE html>
<html>
    <?php
        require_once 'dbDetails.php';
        session_start();
        if (isset($_SESSION['TeacherIC']) && !empty($_SESSION["TeacherIC"])) {
            $TeacherIC = $_SESSION['TeacherIC'];
            
            $conn = mysqli_connect(HOST,USER,PASS,DB) or die('Unable to Connect...');
            $select_sql = "SELECT * FROM module WHERE TeacherIC = '$TeacherIC'";
            $select_result = mysqli_query($conn, $select_sql);
            
            $select_sql2 = "SELECT * FROM teacher WHERE TeacherIC = '$TeacherIC'";
            $select_result2 = mysqli_query($conn, $select_sql2);
        }
    ?>
    <head>
        <meta charset="utf-8" />
        <meta http-equiv="pragma" content="no-cache"> 
        <meta http-equiv="Cache-Control" content="no-cache, must-revalidate"> 
        <meta http-equiv="expires" content="0">
        <title>Teacher Info</title>
        <style>
            h2 {
                display: inline-block;
                width: 200px;
                position: relative;
                top: -40px;
            }
            
            img {
                display: inline-block;
                width: 100px;
                margin-right: 20px;
            }
            
            .iconBTN {
                display: inline-block;
                width: 100px;
            }
            
            .iconBTN input {
                width: 70px;
                position: relative;
                top: -30px;
            }
            
            #imgbox {
                display: inline-block;
            }
            
            table {
                margin-bottom: 20px;
            }
        </style>
    </head>
    <body>
        <h2>Hi, <?php echo $TeacherIC; ?></h2>
        <div id="imgbox">
            <img id="imgIcon" src="../App/<?php $one_select_result2 = mysqli_fetch_assoc($select_result2); echo $one_select_result2['TeacherImage']; ?>?t=<?php echo date('y-m-d h:i:s',time())?>" height="100" width="100">
        </div>
        <div class="iconBTN">
            <form method="post" action="changeIcon.php" enctype="multipart/form-data">
                <input type="file" name="iconImage" onchange="Picture_Show(this);" required="required">
                <input type="submit" value="Submit" >
            </form>
        </div>
        <table border="1" width="100%">
            <tr>
                <th>Module Code</th>
                <th>Module Name</th>
                <th>Date</th>
                <th>Begin Time</th>
                <th>End Time</th>
                <th>AdminNo</th>
                <th>URL</th>
                <th>Update</th>
                <th>Delete</th>
                <th>Notification</th>
            </tr>
            <?php
                if (!$select_result || mysqli_num_rows($select_result) == 0){
                    
                }
                else {
                    while ($one_select_result = mysqli_fetch_assoc($select_result)) {
                        ?>
                        <tr>
                            <th><?php echo $one_select_result['ModuleCode']; ?></th>
                            <th><?php echo $one_select_result['ModuleName']; ?></th>
                            <th>
                                <?php
                                    if($one_select_result['Date'] == 1){
                                        echo "Monday";
                                    }
                                    else if($one_select_result['Date'] == 2){
                                        echo "Tuesday";
                                    }
                                    else if($one_select_result['Date'] == 3){
                                        echo "Wednesday";
                                    }
                                    else if($one_select_result['Date'] == 4){
                                        echo "Thursday";
                                    }
                                    else if($one_select_result['Date'] == 5){
                                        echo "Friday";
                                    }
                                    else{
                                        echo "Sunday";
                                    }
                                ?>
                            </th>
                            <th><?php echo $one_select_result['BeginTime']; ?></th>
                            <th><?php echo $one_select_result['EndTime']; ?></th>
                            <th style="word-break:break-all; width:300px; overflow:auto;"><?php echo $one_select_result['AdminNo']; ?></th>
                            <th><a href="https://docs.google.com/spreadsheets/d/<?php echo $one_select_result['SheetId']; ?>/edit#gid=0" target="_blank">Attendance Table Link</a></th>
                            <th>
                                <form method="post" action="moduleInfo.php">
                                    <input type="hidden" name="Update" value="<?php echo $one_select_result['Id']; ?>"/>
                                    <input type="submit" value="Update" />
                                </form>
                            </th>
                            <th>
                                <form method="post" action="deleteTeacherInfo.php" onsubmit="return confirmDelete()">
                                    <input type="hidden" name="Delete" value="<?php echo $one_select_result['Id']; ?>"/>
                                    <input type="submit" value="Delete" />
                                </form>
                            </th>
                            <th>
                                <form method="get" action="notification.php">
                                    <input type="hidden" name="Notification" value="<?php echo $one_select_result['Id']; ?>"/>
                                    <input type="submit" value="Notification" />
                                </form>
                            </th>
                        </tr>
                        <?php
                    }
                }
                mysqli_close($conn);
            ?>
        </table>
        <label><input type="button" value="Add Module" onclick="GoToModuleInfo();" /> <input type="button" value="Logout" onclick="GoToLogout();" /></label>
        
        <script language="javascript">
            function confirmDelete() {
                var r = confirm("Are you sure?");
                if (r == true) {
                    return true;
                } 
                else {
                    return false;
                }
            }
            function GoToModuleInfo() {
                window.location.href = "moduleInfo.php";
            }
            function GoToLogout() {
                window.location.href = "logout.php";
            }   
            function Picture_Show(obj){
                var len = obj.files.length;
                for (var i =0 ; i < len ; i++){
                    showimg(obj.files[i]);
                }
            }
            function showimg(img){
                var a = new FileReader();
                a.readAsDataURL(img);
                a.onload=function(){
                    var img = new Image();
                    img.src=a.result;
                    img.style.width = "100px";
                    img.style.height = "100px";
                    document.getElementById('imgbox').appendChild(img);
                    document.getElementById('imgIcon').style.display = "none";
                }
            }
        </script>
    </body>
</html>