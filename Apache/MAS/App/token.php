<?php
    $url = 'https://aip.baidubce.com/oauth/2.0/token';
    $data = array(
        'grant_type' => 'client_credentials', 
        'client_id' => '7xsxsls3nA1xGwd9zm3qaDMk',
        'client_secret' => 'yKrad6CLWFv3A4yIVDEMGGNevDtm6ZrN'
    );

    // use key 'http' even if you send the request to https://...
    $options = array(
        'http' => array(
            'header'  => "Content-type: application/x-www-form-urlencoded\r\n",
            'method'  => 'POST',
            'content' => http_build_query($data)
        )
    );
    $context  = stream_context_create($options);
    $result = file_get_contents($url, false, $context);
    if ($result === FALSE) { /* Handle error */ }

    $obj = json_decode($result);
    $access_token = $obj->access_token;
    echo $access_token;
?>