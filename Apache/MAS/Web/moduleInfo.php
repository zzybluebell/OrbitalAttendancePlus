<!DOCTYPE html>
<html>
    <?php
        require_once 'dbDetails.php';

        session_start();
    
        if (isset($_POST['Update']) && !empty($_POST["Update"])) {
            $UpdateId = $_POST['Update'];
            
            $conn = mysqli_connect(HOST,USER,PASS,DB) or die('Unable to Connect...');
            $select_sql = "SELECT * FROM module WHERE Id = '$UpdateId'";
            $select_result = mysqli_query($conn, $select_sql);
        }
    ?>
    <head>
        <meta charset="utf-8" />
        <title>Module Information</title>
        <style>
            .name {
                display: inline-block;
                width: 200px;
                vertical-align: top; 
            }
            .name label {
                display: inline-block;
                width: 200px;
                height: 30px;
            }
            .value {
                display: inline-block;
                width: 400px;
                vertical-align: top; 
            }
            .value label {
                display: inline-block;
                width: 400px;
                height: 30px;
            }
            .value textarea {
                height: 150px;
                width: 300px; 
            }
        </style>
    </head>
    <body>
        <form method="post" action="check_moduleInfo.php">
            <div class="name">
                <label>Module Code:</label>
                <label>Module Name:</label>
                <label>Module Date:</label>
                <label>Module Time:</label>
                <label>Student AdminNo:</label>
            </div>
            <div class="value">
            <?php
                if(isset($select_result) && !empty($select_result)) {
                    if (!$select_result || mysqli_num_rows($select_result) == 0){
                    ?>
                        <label><input type="text" name="ModuleCode" required/></label>
                        <label><input type="text" name="ModuleName" required/></label>
                        <label>
                            <select name="Date">
                                <option value="1" selected>Monday</option>
                                <option value="2">Tuesday</option>
                                <option value="3">Wednesday</option>
                                <option value="4">Thursday</option>
                                <option value="5">Friday</option>
                            </select>
                        </label>
                        <label>Begin: <input type="time" name="BeginTime" required/> End: <input type="time" name="EndTime" required/></label>
                        <label><textarea type="text" name="AdminNo" required></textarea></label>
                        <label><input type="submit" value="Submit" /> <input type="reset" value="Reset" /></label>
                    <?php
                    }
                    else {
                        while ($one_select_result = mysqli_fetch_assoc($select_result)) {
                        ?>
                            <label><input type="text" name="ModuleCode" value="<?php echo $one_select_result['ModuleCode']; ?>" required/></label>
                            <label><input type="text" name="ModuleName" value="<?php echo $one_select_result['ModuleName']; ?>" required/></label>
                            <label>
                                <select name="Date">
                                <?php
                                    if($one_select_result['Date'] == 1) {
                                    ?>
                                        <option value="1" selected>Monday</option>
                                <?php
                                    }
                                    else {
                                ?>
                                    <option value="1">Monday</option>]
                                <?php
                                    }
                                    if($one_select_result['Date'] == 2) {
                                ?>
                                    <option value="2" selected>Tuesday</option>
                                <?php
                                    }
                                    else {
                                ?>
                                        <option value="2">Tuesday</option>
                                <?php
                                    }
                                    if($one_select_result['Date'] == 3) {
                                ?>
                                        <option value="3" selected>Wednesday</option>
                                <?php
                                    }
                                    else {
                                ?>
                                        <option value="3">Wednesday</option>
                                <?php
                                    }
                                    if($one_select_result['Date'] == 4) {
                                ?>
                                        <option value="4" selected>Thursday</option>
                                <?php
                                    }
                                    else {
                                ?>
                                        <option value="4">Thursday</option>
                                <?php
                                    }
                                    if($one_select_result['Date'] == 5) {
                                ?>
                                        <option value="5" selected>Friday</option>
                                <?php
                                    }
                                    else {
                                ?>
                                        <option value="5">Friday</option>
                                <?php
                                    }
                                ?>
                                </select>
                            </label>
                            <label>Begin: <input type="time" name="BeginTime" value="<?php echo $one_select_result['BeginTime']; ?>" required/> End: <input type="time" name="EndTime" value="<?php echo $one_select_result['EndTime']; ?>" required/></label>
                            <label><textarea type="text" name="AdminNo" required><?php echo $one_select_result['AdminNo']; ?></textarea></label>
                            <label><input type="hidden" name="Update" value="<?php echo $one_select_result['Id']; ?>"/> <input type="submit" value="Submit" /> <input type="reset" value="Reset" /></label>
                        <?php
                        }
                    }
                }
                else {
                ?>
                    <label><input type="text" name="ModuleCode" required/></label>
                    <label><input type="text" name="ModuleName" required/></label>
                    <label>
                        <select name="Date">
                            <option value="1" selected>Monday</option>
                            <option value="2">Tuesday</option>
                            <option value="3">Wednesday</option>
                            <option value="4">Thursday</option>
                            <option value="5">Friday</option>
                        </select>
                    </label>
                    <label>Begin: <input type="time" name="BeginTime" required/> End: <input type="time" name="EndTime" required/></label>
                    <label><textarea type="text" name="AdminNo" required></textarea></label>
                    <label><input type="submit" value="Submit" /> <input type="reset" value="Reset" /></label>
                <?php
                }
            ?>
            </div>
        </form>
    </body>
</html>