#!/usr/bin/php
<?php
require_once('DB.php');
require_once 'Mail/mimeDecode.php';

$db_dsn = 'pgsql://artificial_spool:'+$ENV{'ARTIFICIAL_SPOOL_PASSWORD'}+'@db:5432/artificial';


#read in the message itself
$fd = fopen("php://stdin", "r");
$email = "";
while (!feof($fd)) {
    $email .= fread($fd, 1024);
}
fclose($fd);

#figure out the command line parameters
$client_addr = $argv[1];
$sasl_user = $argv[2];
$sender = $argv[3];


$recipients = array_slice($argv,4);

#DEBUG ONLY, Store off the recipients as a pretty printed string;
#ob_start();
#var_dump($recipients);
#$recipients_str = ob_get_flush();
#DEBUG ONLY DONE


#find the Message ID and Subject lines

$seek = 0;
$lastseek = 0;
while($seek = strpos($email, "\n",$seek+1)){
        #check if this subset is the one we're looking for:

        if(substr_compare($email,"Message-Id:",$seek+1,11,true) == 0){
                $message_id = substr($email,$seek+13,strpos($email,"\n",$seek+13)-$seek-13);
        }else if(substr_compare($email,"Subject:",$seek+1,8,true) == 0){
                $subject = substr($email,$seek+9,strpos($email,"\n",$seek+9)-$seek-9);
        }
        if($lastseek+1 == $seek){
                #we hit the data section, stop looking
                break;
        }
        $lastseek = $seek;

}


//connect to the DB
        GLOBAL $db_dsn;
        $db =& DB::connect($db_dsn);
        if(PEAR::isError($db)){
                print($db->getMessage());
                exit(75);
        }

#lookup the account ID to be used
        #select * from accounts where username = null OR (null is null and '127.0.0.2' = ANY(allowed_addr));
        $res =& $db->getAll("select account_id from accounts_spool where username = ? OR username = ? OR (? = '' and ? = ANY(allowed_addr))",array($sasl_user,$recipients[0],$sasl_user,$client_addr),DB_FETCHMODE_ASSOC);
        if(PEAR::isError($res)){
                print($res->getMessage());
                exit(75);
        }

        if(count($res) == 0){
                print("No account found");
                exit(75);
        }

#lookup the additional headers to pull
        $headerres =& $db->getAll("select name,ordinal from account_headers where account_id = ? order by ordinal",array($res[0]['account_id']),DB_FETCHMODE_ASSOC);
        if(PEAR::isError($headerres)){
                print($headerres->getMessage());
                exit(75);
        }

        $insertValues = array($res[0]['account_id'],$sender,$client_addr,$message_id,$email,$subject,strlen($email),implode('@@@',$recipients));
        $headerSQL = '';
        if(count($headerres) > 0){
                $mail = new Mail_mimeDecode($email);
                $params['decode_headers'] = true;
                $mo = $mail->decode($params);

                $highestOrdinal = -1;
                $hArray = array();
                foreach($headerres as $r){
                        if($r['ordinal'] > $highestOrdinal){
                                $highestOrdinal = $r['ordinal'];
                        }
                        $hArray[$r['ordinal']] = $mo->headers[strtolower($r['name'])];
                }


                for($i=0;$i<=$highestOrdinal;$i++){
                        $insertValues[] = $hArray[$i];
                        if($i == 0){
                                $headerSQL = '?';
                        }else{
                                $headerSQL = $headerSQL.',?';
                        }
                }

        } 

        $stmt =& $db->prepare('insert into messages(account_id,from_addr,remote_ip,smtp_message_id,message,received,subject,length,recipients,headers) values (?,?,?,?,?,now(),?,?,string_to_array(?,\'@@@\'),ARRAY['.$headerSQL.']::text[])');
        if(PEAR::isError($stmt)){
                print("Prepare: ".$stmt->getMessage());
                exit(75);
        }
        $res =& $db->execute($stmt,$insertValues);
        if(PEAR::isError($res)){
                print("Exec: ".$res->getMessage());
                exit(75);
        }

        $db->disconnect();



#$fh = fopen('/tmp/poststore.txt','a');
#ob_start();
#var_dump($argv);
#fwrite($fh, ob_get_flush()."\n");
#fwrite($fh, $email."\n\n");
#fclose($fh);
?>

