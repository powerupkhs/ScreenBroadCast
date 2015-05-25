# ScreenBroadCast
This is : 
* Android 스마트폰을 이용한 RealTime Streaming BroadCast
* 자신의 안드로이드 스마트폰 화면을 실시간으로 방송할 수 있는 서비스로 자신의 기기 화면이나 카메라 화면을 음성과 함께 실시간으로 많은 사람에게 웹을 통해 방송할 수 있도록 만들어주는 프로젝트

# Feature
* 안드로이드에서 FFmpeg를 통해 프레임버퍼를 참조하여 자체 화면을 실시간으로 Red5 Streaming Server로 전송하고 Red5 Streaming Server에서 생성된 스트림을 Node.JS Server에서 방마다 할당하여 안드로이드, PC 모두 시청할 수 있는 프로젝트입니다.

# My Part
* 이 프로젝트는 3명의 팀으로 진행하였고 저는 안드로이드에서 FFmepg로 화면추출 및 전송 부분과 Red5 Streaming Server 부분을 담당하였습니다.

# Composition
ScreenBroadCast2
* 자체화면을 방송하고 볼 수 있는 어플리케이션
* 회원 가입 및 로그인 기능
* 안드로이드 자체화면 방송 기능
* 방송 리스트에서 방송을 선택하여 시청할 수 있는 기능
* 즐겨찾는 방송자를 추가하여 그 방송자가 방송을 시작할때 알림을 받는 기능
* 자체화면 외에 카메라를 방송하는 기능
* 플래시 플레이어 간편 설치 기능
* 자체화멱 녹화 파일 저장 기능

Red5 Streaming Server
* FFmpeg로 부터 실시간으로 flv를 전송받아 스트림을 생성해주는 서버

Node.JS Server
* 방송자마다 방을 생성하여 보여주는 서버
* Red5 Streaming Server에서 생성된 스트림을 방마다 연결하여 방송을 보여주는 기능
* 각 방마다 직접 들어가보지않아도 썸네일을 통해 어떤 방송인지 알 수 있게 해주는 기능
* 각 방송별 채팅 기능
* 방송 화면 전환 및 전체화면 기능

# Demonstration Video
* https://www.youtube.com/watch?v=dzQHHqb53mA
