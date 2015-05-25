var mysql = require('mysql')
, fs = require('fs')
, util = require('util');

var client = mysql.createConnection({
    host : '211.189.127.226',
    port : '3306',
    database : 'streaming',
    user : 'swssm',
    password : 'swssm'
});

/*
 * GET home page.
 */
exports.index = function(req, res){
    var room;
    var favorites;
    if(typeof req.session.user !== 'undefined') {
        client.query('SELECT * FROM favorites INNER JOIN room ON favorites.BjId=room.id WHERE favorites.MyId=?', [req.session.id], function(error, result, fields){
            if(error) {
                console.log("MySQL Failure.");
                console.log(error);
            }
            favorites = result;
            client.query('SELECT * FROM room ORDER BY recommend DESC, number ASC', function(error, result2, fields) {
                if(error) {
                    console.log("MySQL Failure.");
                    console.log(error);
                } 
                room = result2;
                res.render('page/index', { title: '메인화면::::', whoami: req.session.user, room: room, favorites: favorites });
            });
        });
    }
    else {
        client.query('SELECT * FROM room ORDER BY recommend DESC, number ASC', function(error, result, fields) {
            if(error) {
                console.log("MySQL Failure.");
                console.log(error);
            } 
            room = result;
            res.render('page/index', { title: '메인화면::::', whoami: req.session.user, room: room });
        });
    }
};

exports.login = function(req, res){
    var id = req.body.id;
    var password = req.body.password;
    client.query('SELECT * FROM member WHERE id=? AND password=?', [id, password] , function(error, result, fields) {
        if(error) {
            console.log("MySQL Failure.");
            console.log(error);
        } 
        if(result.length === 0) {
            console.log("쿼리 결과 없음");
            res.render('page/failed_login', { title: '메인화면::::', whoami: req.session.user });
        }
        else {
            req.session.userId = result[0].id;
            req.session.user = result[0].name;
            
            
            var room;
            var favorites;
            client.query('SELECT * FROM favorites INNER JOIN room ON favorites.BjId=room.id WHERE favorites.MyId=?', [req.session.userId], function(error, result, fields){
                if(error) {
                    console.log("MySQL Failure.");
                    console.log(error);
                }
                favorites = result;
                client.query('SELECT * FROM room ORDER BY recommend DESC, number ASC', function(error, result2, fields) {
                    if(error) {
                        console.log("MySQL Failure.");
                        console.log(error);
                    } 
                    room = result2;
                    res.render('page/index', { title: '메인화면::::', whoami: req.session.user, room: room, favorites: favorites });
                });
            });
        }
    });
};

exports.logout = function(req, res){
    delete req.session.user;
    client.query('SELECT * FROM room ORDER BY recommend DESC, number ASC', function(error, result, fields) {
        if(error) {
            console.log("MySQL Failure.");
            console.log(error);
        } 
        var room = result;
        res.render('page/index', { title: '메인화면::::', whoami: req.session.user, room: room });
    });
};

exports.joinMember = function(req, res){
        res.render('page/join_member', { title: '방송화면::::', whoami: req.session.user, userid: req.session.userId });
};

exports.broadcast = function(req, res){
    if(typeof req.session.user == 'undefined') {
        res.render('page/request_login', { title: '로그인이 필요합니다::::', whoami: req.session.user });
    }
    else {
        var roomName = req.query.room;
        var roomNumber = req.query.number;
        
        client.query('SELECT * FROM room WHERE number=?', [roomNumber], function(error, result, fields) {
            if(error) {
                console.log("MySQL Failure.");
                console.log(error);
            } 
            var room = result;
            res.render('page/broadcast', { title: '방송화면::::', whoami: req.session.user, userid: req.session.userId, roomName: roomName, roomNumber: roomNumber, room: room });
        });
    }
};

exports.recommend = function(req, res){
    if(typeof req.session.user == 'undefined') {
        res.render('page/request_login', { title: '로그인이 필요합니다::::', whoami: req.session.user });
    }
    else {
        
        client.query('SELECT * FROM recommend WHERE room=? and id=?', [parseInt(req.query.room), req.query.id], function(error, result, fields) {
            if(error) {
                console.log("MySQL Failure.SELECT");
                console.log(error);
            } 
            if(result.length === 0) {
                client.query("INSERT INTO recommend (room, id) VALUES (?, ?)", [parseInt(req.query.room), req.query.id], function(error, result2, fields) { 
                    if(error) {
                        console.log("MySQL Failure.INSERT");
                        console.log(error);
                    }
                    else {
                        var update = true;
                        client.query("UPDATE room SET recommend=recommend+1 WHERE number=?", [parseInt(req.query.room)], function(error, result3, fields) {
                            if(error) {
                                update = false;
                                console.log("MySQL Failure.UPDATE");
                                console.log(error);
                            }
                            else {
                                update = true;
                                res.render('result/success_recommend', { title: '방송화면::::', whoami: req.session.user, userid: req.session.userId });
                            }
                        });
                        if(update === false) {
                            res.render('result/fail_recommend', { title: '방송화면::::', whoami: req.session.user, userid: req.session.userId });  
                        }
                    }
                });
            }
            else {
                res.render('result/aready_recommend', { title: '방송화면::::', whoami: req.session.user, userid: req.session.userId });  
            }
        });
    }
};

exports.favorites = function(req, res){
    if(typeof req.session.user == 'undefined') {
        res.render('page/request_login', { title: '로그인이 필요합니다::::', whoami: req.session.user });
    }
    else {
        
        client.query('SELECT * FROM favorites WHERE BjId=? and MyId=?', [req.query.BJ, req.query.id], function(error, result, fields) {
            if(error) {
                console.log("MySQL Failure.SELECT");
                console.log(error);
            } 
            if(result.length === 0) {
                client.query("INSERT INTO favorites (BjId, MyId) VALUES (?, ?)", [req.query.BJ, req.query.id], function(error, result2, fields) { 
                    if(error) {
                        console.log("MySQL Failure.INSERT");
                        console.log(error);
                    }
                    res.render('result/success_favorites', { title: '방송화면::::', whoami: req.session.user, userid: req.session.userId });
                });
            }
            else {
                res.render('result/aready_favorites', { title: '방송화면::::', whoami: req.session.user, userid: req.session.userId });  
            }
        });
    }
};

exports.mobile_broadcast = function(req, res){
    var roomNumber = req.query.roomNumber;
    res.render('mobile/broadcast', { title: '방송화면::::',  roomNumber: roomNumber });
};

exports.uploadThumbnail = function(req, res){    
    var image = req.body.image;
    var roomNumber = req.body.room;
            
    fs.writeFile(roomNumber + ".jpg", new Buffer(image, "base64"), function(err) {});
    console.log("post success");
    console.log("roomNumber :" + roomNumber);
    res.end();
    //res.Flush();
};
 



/* android function */
exports.appLogin = function(req, res){
    res.set('Content-Type', 'text/html');
    console.log(req.body.id);
    var id = req.body.id;
    var password = req.body.password;
    client.query("SELECT password FROM member WHERE id='" + id + "'", function(error, result, fields) {
        if(error) {
            console.log("MySQL Failure.");
            console.log(error);
        } 
        console.log("id = " + id);
        console.log("password = " + password);
        var check;
        if(result.length == 0) {
            console.log("result.length=0");
            check = false;
            res.render('DB/appLogin', { title: '방송화면::::', check: check });
        }
        else {
            var user_password = result[0].password;
            if(password == user_password) {
                check = true;
                res.render('DB/appLogin', { title: '방송화면::::', check: check });
            }
            else {
                console.log("password No match");
                check = false;
                res.render('DB/appLogin', { title: '방송화면::::', check: check });
            }
        }
    });
};
exports.appJoin = function(req, res){
    res.set('Content-Type', 'text/html');
    var id = req.body.id;
    var password = req.body.password;
    var name = req.body.name;
    client.query("INSERT INTO member(id, password, name) VALUES (?, ?, ?)", [id, password, name], function(error, result, fields) {
        var check;
        
        check = false;
        if(error) {
            console.log("MySQL Failure.");
            console.log(error);
        } 
        else {
            check = true;
            res.render('DB/appJoin', { title: '방송화면::::', check: check });
        }
        res.render('DB/appJoin', { title: '방송화면::::', check: check });
    });
};
exports.appRoomList = function(req, res){
    res.set('Content-Type', 'application/json');
    client.query("SELECT * FROM room ORDER BY recommend DESC, number ASC", function(error, result, fields) {
        if(error) {
            console.log("MySQL Failure.");
            console.log(error);
        } 
        
        var check;
        if(result.length === 0) {
            console.log("result.length=0");
            check = false;
            res.render('DB/appRoomList', { title: '방송화면::::', check: check });
        }
        else {
            //var objMap = {"JObject" : result}; 
            //var json = JSON.stringify(objMap);
            res.render('DB/appRoomList', { title: '방송화면::::', check: JSON.stringify(result) });
        }
    });
};
exports.appMakeRoom = function(req, res){
    res.set('Content-Type', 'text/html');
    var name = req.body.name;
    var isPublic = req.body.isPublic;
    var password = req.body.password;
    var id = req.body.id;
    var date = req.body.date;
    client.query("INSERT INTO room(name, is_public, recommend, date, id, password) VALUES (?, ?, 0, ?, ?, ?)", [name, isPublic, date, id, password] , function(error, result, fields) {
        var check;
        
        check = false;
        if(error) {
            console.log("MySQL Failure.");
            console.log(error);
        } 
        else {
            check = true;
            client.query("SELECT * FROM room WHERE name=? AND is_public=? AND recommend=0 AND date=? AND id=? AND password=?", [name, isPublic, date, id, password], function(error, result, fields) {
                if(error) {
                    console.log("MySQL Failure.");
                    console.log(error);
                } 
                res.render('DB/appMakeRoom', { title: '방송화면::::', check: check, room: result[0].number });
            });
        }
        if(check == false){
            res.render('DB/appMakeRoom', { title: '방송화면::::', check: check });
        }
    });
};
exports.appDeleteRoom = function(req, res){
    res.set('Content-Type', 'application/json');
    var roomNumber = req.body.number;
    client.query("DELETE FROM room WHERE number=?", [roomNumber], function(error, result, fields) {
        var check;
        if(error) {
            console.log("MySQL Failure.");
            console.log(error);
        } 
        else {
            check = true;
            res.render('DB/appDeleteRoom', { title: '방송화면::::', check: check });
        }
        if(check == false){
            res.render('DB/appDeleteRoom', { title: '방송화면::::', check: check });
        }
    });
};
exports.appCheckRecommend = function(req, res){
    res.set('Content-Type', 'application/json');
    var number = req.body.number;
    var id = req.body.id;
    console.log(number);
    console.log(id);
    client.query('SELECT * FROM recommend WHERE room=? and id=?', [number, id], function(error, result, fields) {
        var check = true;
        if(error) {
            console.log("MySQL Failure.SELECT");
            console.log(error);
            check = false;
            res.render('DB/appCheckRecommend', { title: '방송화면::::', check: check });
        }
        else {
            if(result.length === 0) {
                console.log("result.length=0");
                check = false;
                client.query("INSERT INTO recommend (room, id) VALUES (?, ?)", [number, id], function(error, result, fields) { 
                    if(error) {
                        console.log("MySQL Failure.INSERT");
                        console.log(error);
                        check = true;
                        res.render('DB/appCheckRecommend', { title: '방송화면::::', check: check });
                    }
                    else {
                        check = false;
                        res.render('DB/appCheckRecommend', { title: '방송화면::::', check: check });
                    }
                });
            }
            else {
                check = true;
                res.render('DB/appCheckRecommend', { title: '방송화면::::', check: check });
            }
        }
    });
};
exports.appIncreaseRecommend = function(req, res){
    res.set('Content-Type', 'application/json');
    var number = req.body.number;
    var id = req.body.id;
    client.query("UPDATE room SET recommend=recommend+1 WHERE number=?", [number, id], function(error, result, fields) {
        var update = true;
        if(error) {
            update = false;
            console.log("MySQL Failure.UPDATE");
            console.log(error);
            res.render('DB/appIncreaseRecommend', { title: '방송화면::::', check: update });
        }
        else {
            update = true;
            res.render('DB/appIncreaseRecommend', { title: '방송화면::::', check: update });
        }
        });
};

exports.appSearchRoom = function(req, res){
    res.set('Content-Type', 'application/json');
    var keyword = req.body.keyword;
    client.query("SELECT * FROM room WHERE name like '%" + keyword + "%'", function(error, result, fields) {
        if(error) {
            console.log("MySQL Failure.");
            console.log(error);
        } 
        
        var check;
        if(result.length === 0) {
            console.log("result.length=0");
            check = false;
            res.render('DB/appSearchRoom', { title: '방송화면::::', check: check });
        }
        else {
            //var objMap = {"JObject" : result}; 
            //var json = JSON.stringify(objMap);
            res.render('DB/appSearchRoom', { title: '방송화면::::', check: JSON.stringify(result) });
        }
    });
};

exports.appInsertFavorites = function(req, res){
    res.set('Content-Type', 'application/json');
    var BjId = req.body.BjId;
    var UserId = req.body.UserId;
    client.query('SELECT * FROM favorites WHERE BjId=? and MyId=?', [BjId, UserId], function(error, result, fields) {
        var check = true;
        if(error) {
            console.log("MySQL Failure.SELECT");
            console.log(error);
        } 
        if(result.length === 0) {
            client.query("INSERT INTO favorites (BjId, MyId) VALUES (?, ?)", [BjId, UserId], function(error, result2, fields) { 
                if(error) {
                    console.log("MySQL Failure.INSERT");
                    console.log(error);
                }
                res.render('DB/appInsertFavorites', { title: '방송화면::::', check: check });
            });
        }
        else {
            check = false;
            res.render('DB/appInsertFavorites', { title: '방송화면::::', check: check });  
        }
    });
};

exports.appFavoritesList = function(req, res){
    res.set('Content-Type', 'application/json');
    var UserId = req.body.UserId;
    client.query('SELECT * FROM favorites INNER JOIN room ON favorites.BjId=room.id WHERE favorites.MyId=?', [UserId], function(error, result, fields){
        if(error) {
            console.log("MySQL Failure.");
            console.log(error);
        }
        res.render('DB/appInsertFavorites', { title: '방송화면::::', check: JSON.stringify(result) });  
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
