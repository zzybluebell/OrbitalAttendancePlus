<?php
    include "token.php";

    function request_post_photo($url = '', $param = '') {
        if (empty($url) || empty($param)) {
            return false;
        }

        $postUrl = $url;
        $curlPost = $param;
        // 初始化curl
        $curl = curl_init();
        curl_setopt($curl, CURLOPT_URL, $postUrl);
        curl_setopt($curl, CURLOPT_HEADER, 0);
        // 要求结果为字符串且输出到屏幕上
        curl_setopt($curl, CURLOPT_RETURNTRANSFER, 1);
        curl_setopt($curl, CURLOPT_SSL_VERIFYPEER, false);
        // post提交方式
        curl_setopt($curl, CURLOPT_POST, 1);
        curl_setopt($curl, CURLOPT_POSTFIELDS, $curlPost);
        // 运行curl
        $data = curl_exec($curl);
        curl_close($curl);
        
        return $data;
    }

    $url = 'https://aip.baidubce.com/rest/2.0/face/v2/match?access_token='.$access_token;

    $img = file_get_contents('originals/160015Q.jpg');
    $img = base64_encode($img);
    $img1 = file_get_contents('uploads/160015Qnew.jpg');
    $img1 = base64_encode($img1);

    $bodys = array(
        'images' => implode(',', array($img, $img1)),
        'ext_fields' => "qualities",
        'image_liveness' => "faceliveness,faceliveness",
        'types' => "7,7"
    );
    $res = request_post_photo($url, $bodys);
?>

<!doctype html>
<html lang="en"><head><Meta http-equiv="Content-Type" Content="pplication/x-www-form-urlencoded" /></head><body><br/><br/><?php
    $result_arr = json_decode($res, true);
    if($result_arr != NULL) {
        if(count($result_arr["result"]) == 0) {
            // 没有人像
            echo "error1";
        }
        else {
            if($result_arr["result"][0]["score"] > 80) {
                $faceliveness_arr = explode(',', $result_arr["ext_info"]["faceliveness"]);
                if ($faceliveness_arr[0] > 0.393241 && $faceliveness_arr[1] > 0.393241) {
                    
                    $scriptURL = "https://script.google.com/macros/s/AKfycbz8SFhii5HnDo_vJId0_zQd2H1yO-e5KtG4O7k0l-w/dev?action=markattendance&SheetId=".$SheetId."&AdminNo=".$adminNo;

                    $markAttendanceJson = file_get_contents($scriptURL);
                    $markAttendanceJson = json_decode($markAttendanceJson, true);
                    $markAttendance = $markAttendanceJson['result'];

                    if($markAttendance == "succeeded") {
                        echo "succeeded";
                    }
                    else {
                        // google sheet error
                        echo "error5";
                    }
                }
                else {
                    // 非活体
                    echo "error2";
                }
            }
            else {
                // 不是同一人
                echo "error3";
            }
        }
    }
    else {
        // 请求失败
        echo "error4";
    }?></body></html>
