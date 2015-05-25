
/**
 * Module dependencies.
 */

var express = require('express')
  , routes = require('./routes')
  , http = require('http')
  , path = require('path')
  , socketio = require('socket.io');

var app = express();

var gcm = require('./gcm');

process.setMaxListeners(0);

// all environments
app.set('port', process.env.PORT || 3000);
app.set('ip', process.env.IP);
app.set('views', __dirname + '/views');
app.set('view engine', 'ejs');
app.use(express.favicon());
app.use(express.logger('dev'));
app.use(express.bodyParser({ keepExtensions: true, uploadDir: __dirname + "/public/uploads" }));                     
app.use(express.methodOverride());
app.use(express.static(path.join(__dirname, 'public')));
app.use(express.static(path.join(__dirname, '')));
app.use(express.cookieParser());
app.use(express.session({secret: 'svtabyrki4q786as37c785ta8vi56aiw4i8w467acv'}));
app.use(app.router);

// development only
if ('development' == app.get('env')) {
  app.use(express.errorHandler());
}

app.post('/register', gcm.regist);
app.post('/unregister', gcm.unregist);
app.post('/send', gcm.send_push);

//get action
app.get('/', routes.index);
app.get('/broadcast', routes.broadcast);
app.get('/mobile_broadcast', routes.mobile_broadcast);
app.get('/recommend', routes.recommend);
app.get('/fullScreen', routes.mobile_broadcast);
app.get('/joinMember', routes.joinMember);
app.get('/favorites', routes.favorites);

//post action
app.post('/login', routes.login);
app.post('/logout', routes.logout);
app.post('/uploadThumbnail', function(reqest,response,next){process.emit('uploadThumbnail', reqest.body.room); next();}, routes.uploadThumbnail);  


//지수 요청
app.post('/appLogin', routes.appLogin);   //로그인
app.post('/appJoin', routes.appJoin);    //회원가입
app.post('/appRoomList', routes.appRoomList);    //방목록
app.post('/appDeleteRoom',function(reqest,response,next){process.emit('makeRoom'); process.emit('changeSWF', reqest.body.number); next();}, routes.appDeleteRoom);    //방삭제
app.post('/appSearchRoom', routes.appSearchRoom);    //방찾기
app.post('/appMakeRoom',function(reqest,response,next){process.emit('makeRoom');next();}, routes.appMakeRoom);    //방만들기
app.post('/appCheckRecommend', routes.appCheckRecommend);    //추천 여부체크
app.post('/appIncreaseRecommend', routes.appIncreaseRecommend);    //추천수 +1
app.post('/appInsertFavorites', routes.appInsertFavorites);    //즐겨찾기 등록
app.post('/appFavoritesList', routes.appFavoritesList);    //즐겨찾기 목록

var server = http.createServer(app);

var roomCount = new Array();


var io = socketio.listen(server);
io.set('log level', 2);

io.sockets.on('connection', function(socket){    
    socket.on('message', function(data){
        console.log(data);
        if(data != "connect"){
            socket.get('room', function(error, room){
                io.sockets.in(room).emit('message', data);
            });
        } 
    });
    socket.on('join', function(data){
        socket.join(data);
        socket.set('room', data);
        if(typeof roomCount[data] == 'undefined'){
            roomCount[data] = 0;
        }
        roomCount[data]++;
        socket.get('room', function(error, room){
            io.sockets.in(room).emit('roomCount', roomCount[data]);
        });
    });
    socket.on('disconnect', function(){
        socket.get('room', function(error, room){
            if(roomCount[room] <= 0){
                roomCount[room] = 0;
            }
            else{
                roomCount[room]--;
            }
            io.sockets.in(room).emit('roomCount', roomCount[room]);
        });
    });
    process.on('makeRoom', function(){
        io.sockets.emit('makeRoom');
    });
    process.on('changeSWF', function(data){
        io.sockets.emit('changeSWF', data);
    });
    process.on('uploadThumbnail', function(data){
        io.sockets.emit('uploadThumbnail', data);
    });
});

server.listen(app.get('port'), app.get('ip'), function(){
    console.log('Express server listening on port ' + app.get('port'));
});