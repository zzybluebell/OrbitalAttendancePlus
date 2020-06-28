<?php
    if(isset($_GET["status"]) && !empty($_GET['status']) && $_GET["status"] == "login"){
        $ch1 = curl_init();
        curl_setopt($ch1, CURLOPT_URL, 'http://myportal.nyp.edu.sg/portal/pls/portal/NYP_PORTAL.wwsec_app_priv.login?p_requested_url=NYP_PORTAL.home&p_cancel_url=NYP_PORTAL.home');
        curl_setopt($ch1, CURLOPT_HEADER, true);
        curl_setopt($ch1, CURLOPT_FOLLOWLOCATION, false);
        curl_setopt($ch1, CURLOPT_RETURNTRANSFER, true);
        $result = curl_exec($ch1);
        if (preg_match('~Location: (.*)~i', $result, $match)) {
            $url = trim($match[1]);
        }
        curl_close($ch1);

        // 初始化
        $ch2 = curl_init();
        // 设置要抓取的页面地址
        curl_setopt($ch2, CURLOPT_URL, $url);       
        // 抓取结果直接返回（如果为0，则直接输出内容到页面）
        curl_setopt($ch2, CURLOPT_RETURNTRANSFER, 1);
        // 不需要页面的HTTP头
        curl_setopt($ch2, CURLOPT_HEADER, 0);
        // 执行并获取HTML文档内容，可用echo输出内容
        $contents = curl_exec($ch2);
        // 释放句柄
        curl_close($ch2);

        $key_word = "name=\"request_id\"";
        $position = strpos($contents, $key_word);
        $sub_contents = substr($contents, $position, 100);

        $left_position = strpos($sub_contents, "value=\"");
        $right_position = strpos($sub_contents, "\">");

        $request_id = substr($sub_contents, $left_position+7, $right_position-($left_position+7));

        echo $request_id ."_split_". $url;
    }
?>