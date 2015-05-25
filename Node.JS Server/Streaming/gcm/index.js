var gcm = require('node-gcm');
var mysql = require('mysql');
var client = mysql.createConnection({
    host : '211.189.127.226',
    port : '3306',
    database : 'streaming',
    user : 'swssm',
    password : 'swssm'
});

exports.regist = function (req, res) {
    var regId = req.body.regId;
    var userId = req.body.userId;
    console.log(regId);
    console.log(userId);
    client.query('UPDATE member SET regId=? WHERE id=?', [regId, userId], function(error, result, fields){
        if(error) {
            console.log("MySQL Failure.");
            console.log(error);
        }
    });
    res.end(); 
};

exports.unregist = function (req, res) {
    console.log ('unregister success');
    res.end(); 
};

exports.send_push = function(req, res) {
    var id = req.body.id;
    var title = req.body.title;
    var message = new gcm.Message();
	var sender = new gcm.Sender('AIzaSyCCfbvVLDKgQXPsa2pNTL-BU9E_mzE7EFU');
	var registrationIds = [];

	// Optional
	message.addData('id', id);
	message.addData('title',title);
	message.collapseKey = 'demo';
	message.delayWhileIdle = true;
	message.timeToLive = 3;


    client.query('SELECT * FROM member INNER JOIN favorites ON member.Id=favorites.MyId WHERE favorites.BjId=?', [id], function(error, result, fields){
        if(error) {
            console.log("MySQL Failure.");
            console.log(error);
        } 
        // At least one required
        for(var i=0; i<result.length; i++) {
            if(result[i].regId != null){
                registrationIds.push(result[i].regId + '');
            }
        }
        /**
            * Parameters: message-literal, registrationIds-array, No. of retries, callback-function
        */
        
        
        //sender.sendNoRetry(message, registrationIds, function (err, result) {
        //    console.log(result);
        //}); // retry 없이 보내는 부분입니다. 보통은 이것을 더 많이 애용할 것 같습니다.
        sender.send(message, registrationIds, 4, function (err, result) {
            console.log(result);
        });
    });
    
};

//keeped connection
function keepalive() {
  client.query('select 1', [], function(err, result) {
    if(err) return console.log(err);
    // Successul keepalive
  });
}
setInterval(keepalive, 1000*60*5);