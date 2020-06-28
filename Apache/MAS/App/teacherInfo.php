<?php
    require_once 'dbDetails.php';
    $response = array();
    $input = file_get_contents("php://input");
    $jsonObj = json_decode($input, true);
    $id = $jsonObj['id'];
    date_default_timezone_set("Asia/Singapore");
    $datetime = date('H:i:s',time());
    $date = date('w');

    if (isset($_POST['AdminNo']) && !empty($_POST["AdminNo"])) {
        $AdminNo = $_POST['AdminNo'];
        
        $conn = mysqli_connect(HOST,USER,PASS,DB) or die('Unable to Connect...');
        
        if (isset($_POST['SearchText']) && !empty($_POST["SearchText"])) {
            $SearchText = $_POST['SearchText'];
            $select_sql = "SELECT A.ModuleName, B.TeacherIC, B.TeacherName, B.Phone, B.Email, B.TeacherImage FROM module AS A INNER JOIN teacher AS B ON A.TeacherIC = B.TeacherIC WHERE B.TeacherName LIKE '%$SearchText%' && A.AdminNo LIKE '%$AdminNo%' Group BY B.TeacherIC";
        }
        else {
            $select_sql = "SELECT A.ModuleName, B.TeacherIC, B.TeacherName, B.Phone, B.Email, B.TeacherImage FROM module AS A INNER JOIN teacher AS B ON A.TeacherIC = B.TeacherIC WHERE A.AdminNo LIKE '%$AdminNo%' Group BY B.TeacherIC";
        }
        
        $select_result = mysqli_query($conn, $select_sql);
        
        $ModuleName = "";
        $TeacherName = "";
        $Phone = "";
        $Email = "";
        $TeacherImage = "";
        
        if (!$select_result || mysqli_num_rows($select_result) == 0){
            $response["status"] = "NoRecord";
            
        }
        else {
            $i = 0;
            $response["status"] = "OK";
            while ($one_select_result = mysqli_fetch_assoc($select_result)) {
                $response[$i]["ModuleName"] = $one_select_result["ModuleName"];
                $response[$i]["TeacherName"] = $one_select_result["TeacherName"];
                $response[$i]["Phone"] = $one_select_result["Phone"];
                $response[$i]["Email"] = $one_select_result["Email"];
                $response[$i]["TeacherImage"] = fileToBase64($one_select_result["TeacherImage"]);
                $i++;
            }
        }
        mysqli_close($conn);
    }
    else {
        $response["status"] = "Error";
    }
    echo json_encode($response);

    function fileToBase64($file){
        $base64_file = '';
        if(file_exists($file)){
            $mime_type= mime_content_type($file);
            $base64_data = base64_encode(file_get_contents($file));
            $base64_file = 'data:'.$mime_type.';base64,'.$base64_data;
        }
        return $base64_file;
    }
?>