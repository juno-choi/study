# IP

* Internet Protocol (인터넷 프로토콜)

    - 복잡한 인터넷 망에서 client가 지정한 IP 주소(IP Address)로 데이터를 전달하는 역할

    - 패킷(Packet)이라는 통신 단위로 데이터 전달

        `packet` [출발지IP][목적지IP][기타][전송데이터] 의 규칙으로 전송


* IP 프로토콜의 한계

    - `비연결성`

        패킷을 받을 대상이 없거나 받을 수 없는 서비스 불능 상태여도 패킷은 일방적으로 전송돼고 전송한 client는 전송이 성공한 것만 확인할 수 있을 뿐 상대방이 성공적으로 전송한 내용을 받았는지를 확인할 수 없다.

    - `비신뢰성`

        위 비연결성, 물리적인 이유로 인한 중간의 패킷의 손실 가능성

        패킷의 전송 속도차나 환경차이에 의한 순서의 확실성이 불확실함

    - `프로그램 구분`

        하나의 IP에서 여러개의 어플리케이션이 동작하고 있을때 client가 원하는 어플리케이션에 데이터를 전송할 수 없음

# 인터넷 프로토콜 스택의 4계층

<img src="https://blog.kakaocdn.net/dn/nOzzX/btq3mdwSmrr/4cIU4VRzFpqLv0LwaKL5m1/img.png" alt="protocol"></img>

`애플리케이션 계층`

1. Application Layer(응용 계층), Presentation Layer(표현 계층), Session Layer(세션 계층)으로 구분할 수도 있다.
2. 해당 계층은 사용자가 실제로 사용하면서 직접 서비스를 경험하거나 os나 protocol이 간접적으로 서비스를 사용하는 계층

`전송 계층`

1. IP의 문제점을 보완하기 위해 전송에 필요한 계층
2. 네트워크 프로토콜 내에서 송신자와 수시잔의 연결하는 통신 서비스를 제공하는 계층

`인터넷 계층`

1. 네트워크 패킷을 IP Address를 통해 지정된 목적지로 전송하기위한 계층

`네트워크 인터페이스 계층`

1. 네트워크의 하드웨어를 제어하는 계층

<img src="https://blog.kakaocdn.net/dn/chAO9U/btq3kkJ0hd2/v1GGDkhScqXCnZXaEkd46K/img.png" alt="protocol"></img>

위 그림으로 다시 설명하자면 Hello, world라는 메세지를 애플리케이션 계층에 해당하는 chrome application에서 socket library를 통하여 OS에 해당하는 전송 계층의 TCP로 전달하여 TCP 정보를 생성하고 다시 인터넷 계층의 IP로 정보가 전달되어 IP 패킷에 정보를 담아 트워크 인터페이스 계층으로 전달되어져 LAN을 통해 데이터가 전송되어진다.


# TCP
<img src="https://blog.kakaocdn.net/dn/xvO9P/btq3mpjxJXn/tLb8gOFeTr3O57ymSK8zc0/img.png" alt="protocol"></img>
* Transmission Control Protocol (전송 제어 프로토콜)

    - `연결지향`

        3 way handshake를 통해 client와 데이터를 전송 받을 server의 상태를 확인하여 데이터를 전송

        실제로 물리적으로 연결된 상태는 아니며 논리적인 연결 상태

    - `데이터 전달 보증`

        데이터를 전송했을 때 데이터를 받았다는 server의 응답을 반환받을 수 있다.

    - `순서 보장`

        데이터의 패킷의 순서가 달라졌을 때 순서가 틀린 부분부터 데이터를 모두 버리고 재전송을 요청


`3 way handshake`

1. client가 데이터를 전송할때 목적지 server가 현재 데이터를 받을 수 있는 상태인지 먼저 확인
2. 목적지 server에서 데이터를 받을 수 있는 상태일 경우 client로 데이터를 전송해도 좋다고 전송, 또한 목적지 server에서도 client로 데이터를 주고 받을 수 있는 상태인지 확인
3. client도 server에 데이터를 주고 받을 수 있는 상태인 것을 확인하여 전송
4. client에서 목적지 server로 데이터를 전송

<img src="https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FqD6vv%2Fbtq3mvYbGWR%2FIJsh9N20Q9p2DTtr0R9tHk%2Fimg.png" alt="3 way handshake"></img>

`SYN` 접속 요청
`ACK` 요청 수락

# UDP

* User Datagram Protocol (사용자 데이터그램 프로토콜)

    - IP와 거의 같고 PORT와 체크썸만 추가
    - 데이터 전달 및 순서가 보장되지 않지만, 단순하고 빠름
    - TCP는 검증하는 프로세스가 많아 UDP가 훨씬 빠름
    - 기능이 거의 없지만 사용자가 application에서 수정하여 사용할 수 있어 TCP보다 유연하게 사용 가능

# PORT

* 하나의 client가 여러 server와 동시에 통신

    - IP를 집주소라고 생각한다면 port는 해당 집에서 살고 있는 사람으로 생각하면 좋을거 같다. IP 주소를 통해 집을 찾고 port번호를 통해 해당 사람을 찾아서 데이터를 전송해주는 
    - 0 ~ 65535 까지 사용이 가능하지만 0 ~ 1023은 잘 알려진 port로 사용하지 않는 것이 좋다.

# DNS

* Dmain Name System (도메인 네임 시스템)
    - ip의 단점을 보완
        1. ip는 기억하기 어렵다
        2. ip는 변경될 수 있다.
    - goole.com -> 192.168.1.2로 DNS server에 등록해놓으면 도메인명으로만 ip 주소로 접근할 수 있다.
    
# URI
<img src="https://blog.kakaocdn.net/dn/uyjWy/btq3mhTCpOy/dBYTP97rxsBMc8qS5hxqF0/img.png" alt="3 way handshake"></img>
* Uniform Resource Identifier

    - `Uniform` 리소스 식별하는 통일된 방식
    - `Resource` 자원, URI로 식별할 수 있는 모든 것
    - `Identifier` 다른 항목과 구분하는데 필요한 정보

    
* URL
  
    - `Locator` 리소스가 있는 위치를 지정
    - 대부분 우리가 웹 브라우저에서 사용하는 방식
    - ex) http://www.example.com/serch?idx=12&page=1
    - scheme://[userinfo@]host[:port][/path][?query][#fragment]
        - `scheme`
            1. 주로 프로토콜 사용
            2. 어떤 방식으로 자원에 접근할 것인가 하는 규칙 (http, https, ftp)
            3. http (80), https (443) 포트는 생략 가능
        - `userinfo`
            1. url에 사용자 정보를 포함해서 인증
            2. 거의 사용하지 않음
        - host
            1. 도메인명 또는 IP 주소
        - `port`
            1. 접속 포트
            2. 생략 가능
        - `path`
            1. 리소스 경로
            2. 대부분 계층적 구조를 가짐 ex) /order/item/book
        - `query`
            1. key=value 형태
            2. ?로 시작 &으로 추가
            3. query parameter 혹은 query string으로 불림
        - `fragment`
            1. html 내부 북마크 등에 사용
            2. server와 통신하는 정보는 아니고 html 내부에서 사용
* URN
    
    - `Name` 리소스에 이름을 부여
    - ex) book:isbn:72989
    
# 웹 브라우저 요청 흐름

* https://www.google.com:443/search?q=hello&hl=ko 의 요청메세지
    
        GET /search?q=hello&hl=ko
        HOST: www.google.com

* 요청에 대한 응답 메세지
        
        HTTP/1.1 200 OK
        Content-Type: text/html;charset=UTF-8
        Content-Length: 3423

        <html>
            <body> ... </body>
        </html>

# HTTP란

* HyperText Transfer Protocol

    - 최초에는 HTML과 TEXT를 전송하기 위해 고안됨
    - 현재는 image, 음성, 영상, 파일, json 등 거의 모든 형태의 데이터를 전송
    
    
* HTTP 역사
    - HTTP/0.9 1991년 : GET만 지원, HTTP 헤더 지원하지 않음
    - HTTP/1.0 1996년 : 메서드, 헤더 추가
    - HTTP/1.1 1997년 : 가장 많이 사용되는 버전
    - HTTP/2   2015년 : 성능 개선
    - HTTP/3   진행중  : TCP 대신 UDP 사용 및 성능 개선  
    

# HTTP 특징
* 클라이언트 서버 구조
    1. request response 구조
    2. client와 server를 서로 분리한 `독립적` 구조이며 client는 요청을 보내고 server는 응답을 대기하면서 요청에 대한 결과를 만들어 응답한다.
    

* Stateful, Stateless
    - Stateful 상태유지 프로토콜
        
        - client의 요청을 server 측면에서 항상 같은 server에서 요청을 받아 응답해야한다.
        - server가 중간에 장애가 나면 client를 다시 처음부터 다른 server에 요청을 해야한다.
       
    - Stateless 무상태 프로토콜
        
        - client의 요청을 server 측면에서 아무 server나 요청을 받아 응답한다.
        - server가 중간에 장애가 나도 client는 다른 server를 사용하여 요청을 진행할 수 있다.
        - 무상태로 설계하는 측면이 서비스 확장에 유리 
        - 하지만 무상태로만 설계할 수 없는 경우가 존재
            1. 무상태 ex) 로그인이 필요 없는 단순한 서비스 소개 화면
            2. 상태 유지 ex) 로그인
                + 일반적으로 브라우저 쿠키와 서버 세션등을 사용하여 상태를 유지
                + 상태유지는 최소한만으로 사용
        - 필요한 값이 많아질 수록 전송해야하는 데이터도 많아짐
    
* connectionless (비 연결성)

        장점
  
        1. HTTP는 기본이 연결을 유지하지 않는 모델
        2. 1시간 동안 수천명이 서비스를 사용해도 실제 서버로 요청을 보내는 요청은 그 수에 비해 현저히 작다
        3. HTTP에서는 요청이 들어왔을 때 결과 값을 반환하고 연결을 끊는다.
        4. 자원의 소모가 적다.
    

        단점

        1. TCP/IP 연결을 새로 맺어야함 - 3 way handshake를 다시 해야함
        2. HTML 뿐만 아니라 javascirpt, css, image 등 수 많은 자료를 연결될 때마다 다운로드
        3. HTTP 지속 연결 (Persistent Connections)로 문제 해결
        4. HTTP2/HTTP3 에서 개선

* HTTP 메세지

<img src="https://blog.kakaocdn.net/dn/tWPRN/btq3ncYz5Cr/cf4pEkDQmybrtKztmWhixK/img.png" alt="3 way handshake"></img>
       
1. 요청 메세지 - 시작라인

    - start-line = `request-line`
    - `request-line` 'http method' + '(공백)' + '/' + '요청대상' + '(공백)' + '/' + 'version' 의 구조
            
        - `http method` 
            
            종류 : get, post, put, delete
            
            get - 조회
          
            post - 요청

        - `요청대상`
          
            절대경로(/)?query
        
        - `version`
          
            HTTP의 version
    

2. 응답 메세지 - 시작라인
    
    - start-line = `status-line`
    - `status-line` 'version' + '/' + status-code + '/' + 'reason-phrase'
    
        - `version`
          
            HTTP version
            
        - `status-code`
            
            200 : 성공
          
            400 : 클라이언트 요청 오류
          
            500 : 서버 내부 오류
          
        - `reason=phrase`
            
            사람이 이해할 수 있는 짧은 설명


3. HTTP HEADER

    - header-field - field-name":"`OWS`field-value`OWS`    (OWS:띄어쓰기 허용)
    - filed-name은 대소문자 구분이 없음
    - 용도
        - HTTP 전송에 필요한 모든 부가정보
        - 표준 헤더가 존재함
        - 필요시 임의의 헤더 추가 가능
    

4. HTTP 메세지 바디
    
    - 실제 전송할 데이터
    - HTML, image, 음성, 파일 등
    

5. HTTP는 매우 단순하며 확장도 가능함


# HTTP 메서드

* HTTP 설계
    - <span style="color:red">회원</span> 목록 `조회`  
       `get` <span style="color:red">/members</span>
    - <span style="color:red">회원</span> `조회`  
       `get` <span style="color:red">/members</span>/{id}   
    - <span style="color:red">회원</span> `등록`  
       `post`  <span style="color:red">/members</span>/{id}   
    - <span style="color:red">회원</span> `수정`  
       `put` <span style="color:red">/members</span>/{id}
    - <span style="color:red">회원</span> `삭제`  
       `delete` <span style="color:red">/members</span>/{id}
    

* 요구사항에 따른 URI 설계
    - API URI는 resource를 먼저 식별하고 resource를 가지고 설계해야함
    - 요구사항에 따른 resource는 <span style="color:red">회원</span>이 resource가 됨 
    - resource와 행동을 분리하여 리소스는 URI가 되고 행위(`조회`,`등록`,`수정`,`삭제`)는 메서드가 됨
    

* 메서드의 종류
    
    - 주로 사용되는 메서드

        `GET`   조회
        
        - 서버에 전달하고 싶은 데이터는 QUERY를 통해 전달
        - 메세지 바디를 사용하여 데이터를 전달할 수 있지만 권장하지 않음
      
        `POST`  요청, 등록
    
        - 메세지 바디를 통해 서버로 데이터 전달
        - 주로 데이터 신규 등록, 프로세스에 사용
        - 프로세스는 예로 주문에서 결제완료 -> 배달시작 처럼 값변경을 넘어 프로세스의 상태가 변경되는 경우에 사용됨
        
        `PUT`   대체
    
        - 리소스를 완전히 대체 (기존 리소스의 중복되는 내용일지라도 모든 필드값을 그대로 입력해야함)
        - 없으면 생성
        - `POST`와 차이점은 클라이언트가 리소스 위치를 알고 URI 지정하여 사용
    
        `PATCH` 부분 변경
        
        - 리소스를 부분적으로 변경 (`put`과 다르게 부분적인 필드값만 보내면 해당 필드만 수정됨)
    
        `DELETE`    삭제
        
        - 리소스 제거

    - 기타 메서드

        `HEAD`  GET과 동일하지만 상태줄과 헤더만 반환
        
        `OPTIONS`   대상 리소스에 통신 가능 옵션을 설명
    
        `CONNECT`   대상 자원으로 식별되는 서버에 대한 터널 설정
        
        `TRACE` 리소스 경로를 따라 메세지 루프백 테스트 수행


* HTTP 메서드 속성

    - Safe Method `안전`
    
        - 호출해도 리소스를 변경하지 않는다.
        - `GET` , `HEAD`
    
    - Idempotent Method `멱등`
    
        - 한번 호출하든 두번 호출하든 100번 호출하든 결과가 똑같다.
        - `GET` , `PUT` , `DELETE`
        - PUT은 리소스 자체를 완전히 대체하기 때문에 같은 요청을 계속해서 반복하면 같은 리소스가 대체되는 것이기때문에 동일하다.
        - `멱등`이 필요한 이유는 `멱등`한 메서드는 몇번을 호출해도 결과가 동일하고 리소스에 미치는 영향이 동일하기 때문에 서버 내부의 이유로 정상 응답을 못 주었을 때, 클라이언트가 같은 요청을 동일하게 반복해도 되는지에 대한 판단 근거가 될 수 있다.
    
    - cacheable Method `캐시가능`
    
        - 웹 브라우저에 똑같은 리소스를 또 다운로드 하지 않고 웹 브라우저 자체에 캐시로 저장하여 사용
        - `GET` , `HEAD` , `POST` , `PATCH` 
        - 실제로는 `GET` , `HEAD` 정도만 캐시로 사용
            1. `POST` , `PATCH`  는 본문 내용까지 캐시 키로 고려해야하는데 구현이 쉽지 않다.


# HTTP 메서드 활용

* 클라이언트 -> server 데이터 전송
    - 데이터 전송 방법
        
            1. 쿼리 파라미터를 통한 데이터 전송
                - GET
                - 정렬 필터(검색어)
            
            2. 메세지 바디를 통한 데이터 전송
                - POST, PUT, PATCH
                - 회원가입, 상품주문, 등록, 변경
    
    - 데이터 전송 상황
    
            1. 정적 데이터 조회  ex) /static/start.jpg
                - 쿼리 파라미터 없이 리소스 경로로 단순하게 조회
                - GET 사용
       
            2. 동적 데이터 조회  ex) /search?q=hello&hl=ko
                - GET 사용
                - 쿼리 파라미터를 사용해서 데이터 전달
                - 주로 검색, 게시판 필터로 사용
    
            3. HTML Form 데이터 전송
                - GET, POST 전송가능
                - GET = 쿼리 파라미터로 전송됨
                - POST = 메세지 바디로 전송됨
                - multipart/form-data 파일을 전송하기 위한 Content-Type
       
            4. HTTP API 데이터 전송
                - 서버와 서버간의 백엔드 시스템 통신
                - ajax 통신
                - 주로 Content-Type: application/json 을 사용

* HTTP API 설계 예시
    `참고` https://restfulapi.net/resource-naming
    - `POST` 기반
        
        회원 목록 `GET` /members

        회원 등록 `POST` /members

        회원 조회 `GET` /members/{id}

        회원 수정 `PATCH` `PUT` `POST` /members/{id}

        회원 삭제 `DELETE` /members/{id}

            1. 클라이언트의 요청 POST /members
    
            2. server에서 새로 등록된 리소스에 대한 URI값을 반환해준다. 
                응답 데이터 예시
       
                HTTP/1.1 201 Created
                Content-Type: application/json
                Content_Length: 33
                Location: /members/100
       
                {
                    "username": "choi",
                    "age": 28
                }
    
            3. 이러한 형식을 컬렉션(Collection)이라 부른다.
                서버가 관리하는 리소스 디렉토리
                서버가 리소스 URI를 생성하고 관리

    - `PUT` 기반 (파일이 존재한다면 지우고 새로 생성하는게 맞기 때문에 파일 예시가 제일 적당)
    
        파일 목록 `GET` /files
    
        파일 조회 `GET` /files/{filename}
    
        파일 등록 `PUT` /files/{filename}
    
        파일 삭제 `DELETE` /files/{filename}
    
        파일 대량 등록 `POST` /files
        
            1. 클라이언트가 리소스 URI를 알고 있어야한다.
                POST와는 다르게 등록시에도 클라이언트에서 리소스의 URI를 직접 지정해서 등록하게된다.
       
            2. 이러한 형식을 스토어(Store)이라 부른다.
                클라이언트가 관리하는 리소스 저장소
                클라이언트가 리소스의 URI를 알고 관리함

    - HTML Form 기반 (`GET` `POST`만 지원)
    
        회원 목록 `GET` /members
        
        회원 등록 폼 `GET` /members/new
    
        회원 등록 `POST` /members/new

        회원 조회 `GET` /members/{id}
    
        회원 수정 폼 `GET` /members/{id}/edit
       
        회원 수정 `POST` /members/{id}/edit
    
        회원 삭제 `POST` /members/{id}/delete

            1. HTML Form은 GET, POST만 지원
            2. GET, POST만 지원하기 때문에 여러 제약이 있고 이 제약을 해결하기 위해 컨트롤 URI를 사용
                - /new, /edit, /delete가 컨트롤 URI
            3. HTTP 메서드로 해결하기 애매한 경우 컨트롤 URI를 사용
 
   
# HTTP 상태코드

* 1XX (Informational)
    - 요청이 수신되어 처리중
    - 거의 사용하지 않음
  
  
* 2XX (Successful)
    - `200` Ok
        - 정상 처리
    - `201` Created
        - 요청 성공해서 새로운 리소스 생성
    - `202` Accepted
        - 요청이 접수되었으나 처리 프로세스는 진행되지 않았음 (추후에 프로세스 진행됨)
    - `204` No Content
        - 서버가 요청을 성공적으로 수행했지만, 응답할 데이터가 없음
        - ex) 웹 문서 편집기에 save버튼
            1. 문서 편집 중 save를 누름
            2. 저장에 성공했다면 204 상태코드를 return함
            3. 하지만 화면상에 변화가 생기거나 데이터상에 변화가 생기는건 없음
            4. 저장에 실패했다면 실패 상태코드를 return하여 사용자에게 알림
    
    
* 3XX (Redirection)
    - 요청을 완료하기 위해 유저 client에 추가 조치 필요
    - 웹 브라우저는 3xx 응답에 location 값이 존재하면 location uri로 리다이렉션
    - 영구적 리다이렉션
        - ex) 사용하던 페이지의 url이 변경되었을때 변경 전 url 입력시 301 상태코드를 반환해주고 location 값에 새로운 url로 반환한다.
        
                301 Moved Permanently 
                - 리다이렉트 요청시 요청 메서드가 GET으로 변경되고 본문이 제거될 수 있음
          
                308 Pernanent Redirect (영구 리다이렉션)
                - 301 상태코드와 기능은 동일
                - 리아이렉트 요청시 요청 메스드와 본문 유지

    - 일시적인 리다이렉션
        - ex) 결제 완료시 주문 완료 화면으로 이동
        
                302 Found
                - 리다이렉트시 요청 메서드가 GET으로 변하고 본문이 제거될 수 있음
          
                307 Temporary Redirect
                - 302와 기능 같음
                - 리다이렉트시 요청 메서드와 본문 유지
          
                303 See Other
                - 302와 기능 같음
                - 레다이렉트시 요청 메서드가 GET으로 변경

        - PRG : Post / Redirect / Get
            1. POST 요청상태의 url에서 새로고침을 하면 POST가 중복되어 요청되는 상태
            2. 주문을 POST 요청으로 완료 후 redirect를 주문 완료의 GET 페이지로 보내서 해결
            3. url이 이미 POST -> GET으로 리다이렉트 되었기 때문에 새로고침을 잘못 눌러도 GET 페이지로 연결
    
    - 기타 리다이렉션
        
            300 Multiple Choices
            - 잘 안씀
      
            304 Not Modified
            - 캐시를 목적으로 사용
            - 클라이언트의 리소스가 수정되지 않았음을 알려주고 클라이언트는 로컬 PC에 저장된 캐시를 재사용한다.
            - 캐시를 재사용하므로 네트워크 다운로드 사용량이 줄어듦
            - 304 응답은 메세지 바디를 포함하면 안된다. (로컬 캐시를 사용하므로)
        

* 4XX (Client Error)
    - 클라이언트의 잘못된 요청
    - 오류 원인이 클라이언트
    - 오류의 원인이 클라이언트에서 잘못된 요청, 데이터이기 때문에 재시도를 요청해도 실패함
    
            400 Bad Request
            - 요청 내용이 오류
            - 클라이언트는 요청 내용을 수정해서 다시 보내야함

            401 Unauthorized
            - 클라이언트가 해당 리소스에 대한 인증이 필요
            - 401 오류 발생시 응답에 WWW-Authenticate 헤더와 함께 인증 방법을 설명
            
            403 Forbidden
            - 서버가 요청을 이해했지만 승인을 거부함
            - 접근 권한이 없는 경우
    
            404 Not Found
            - 요청 리소스 서버가 존재하지 않음
            - 또는 권한이 없는 클라이언트에게 서버단에서 해당 리소스를 숨기고 싶을 때
    

* 5XX (Server Error)
    - 서버 문제로 오류 발생
    - 서버 문제이기 때문에 재시도 하면 성공할 수도 있음
    - 5XX 에러는 서버에서 문제가 발생했을 때 사용하는 에러이므로 서버의 로직상 에러 상태 코드를 만들어내야할때는 4XX이나 2XX으로 에러를 내야한다.
    
            500 Internal Server Error
            - 서버 내부 문제 오류
            - 애매한 서버 오류는 모두 500
    
            503 Service Unavailable
            - 서비스 이용불가
            - 서버 터졌음
            - 혹은 서버를 일부러 다운시켜놓은 경우
            - Retry-After 헤더 필드로 얼마뒤에 server가 복구될지 알려줄 수 있음
    
            
# HTTP HEADER

* 표현
    - Content-Type : 표현 데이터의 형식
        - 미디어 타입, 문자 인코딩
    - Content-Encoding : 표현 데이터의 압축 방식
        - 표현 데이터를 압축하기 위해 사용
        - 데이터를 전송하기 전 압축 후 인코딩 헤더 추가하여 정보 전달
    - Content-Language : 표현 데이터의 자연 언어
        - 표현 데이터의 잔연 언어 표현 ex) ko, en, en-US
    - Content-Length : 표현 데이터의 길이
        - Transfer-Encoding(전송 코딩)을 사용할땐 사용하면 안됨
    

* 콘텐츠 협상 (Content negotiation)
    - Accept : 클라이언트가 선호하는 미디어 타입 전달
    - Accept-Charset : 클라이언트가 선호하는 문자 인코딩
    - Accept-Encoding : 클라이언트가 선호하는 압축 인코딩
    - Accept-Language : 클라이언트가 선호하는 자연 언어
        
    - 협상 헤더는 요청시에만 사용됨
    - 우선순위가 필요할땐 Quaility Values(q) 값 사용
        - Quaility Values(q) 사용 예시1
            1. ex) Accept-Language : ko-KR, ko;q=0.9, en-US;q=0.8,en;q=0.7
            2. q값이 생략되면 1
            3. q는 0~1까지의 숫자이고 클수록 우선순위가 높음
        - Quaility Values(q) 사용 예시2
            1. ex) Accept : text/*, text/plain, text/plain;format=flowed, */*
            2. 위 예시일 경우 우선순위는
                1. text/plain;format=flowed
                2. text/plain
                3. text/*
                4. \*/*

        
* 전송 방식
    - Content-Length 단순전송
    - Content-Encoding 압축 전송
        - Content-Encoding값을 추가하여 클라이언트가 압축을 풀때 사용
    - Transfer-Encoding 분할 전송
        - chunked
            1. 용량이 너무 클때 데이터를 쪼개서 계속 보냄
            2. \\r\n이 종료라는 표시
    - Range, Content-Range
        - 범위를 지정해서 범위만큼 데이터를 전송하고 받음
    

* 일반 정보
    - From
        - 유저 에이전트의 이메일 정보
        - 잘 사용되진 않음
        - 검색 엔진 같은 곳에서 주로 사용
        - 요청에서 사용
    - Referer
        - 현재 요청된 url에 접속하기 전 사이트의 url
        - 접속 경로를 분석할 수 있음
        - 요청에서 사용
    - User-Agent
        - 클라이언트의 애플리케이션 정보(웹 브라우저 정보 등)
        - 특정 브라우저에서 버그가 생기는걸 확인할 수 있음
        - 브라우저 통계를 낼때 사용 
        - 요청에서 사용
    - Server
        - web server의 origin server의 정보
        - ex) Apache, Nginx
        - 응답에서 사용
    - Date
        - 메세지가 발생한 날짜와 시간
        - 응답에서 사용


* 특별한 정보
    - Host
        - 진짜 중요한 필수 값
        - 하나의 IP 주소에 여러 도메인이 적용되어 있을 때 구분하기 위한 값
        - 요청에서 사용
    - Location
        - 페이지 리다이렉션 값
        - 3XX 응답 결과에 Location 헤더가 있으면 Location 값으로 자동 이동
        - 201 응답 결과에 Location 헤더가 있으면 요청에 의해 생성된 리소스 URI 값
    - Allow
        - 허용 가능한 HTTP 메서드
        - 405 (Method Not Allowed) 응답에 포함해야함
        - 클라이언트가 잘못된 HTTP 메서드를 전송했을 때 해당 URL에서 사용가능한 HTTP 메서드를 알려줌
    - Retry-After
        - 503 응답 결과에서는 서비스가 언제까지 불능인지 알려줄 수 있음
        - 날짜나 초단위로 표시할 수 있음
    

* 인증
    - Authorization
        - 클라이언트 인증 정보를 서버에 전달
        - Authorization: Basic xxxxxxxxxxxx
        - 인증 메커니즘에 따라 헤더의 value 값이 달라진다.
    - WWW-Authenticate
        - 리로스 접근시 필요한 인증 방법 정의
        - 401 Unauthorized 응답 결과와 함께 사용
    

* 쿠키
    - Set-Cookie
        - 서버에서 클라이언트로 쿠키 전달(응답)
        - 서버에서 전달받은 클라이언트 웹브라우저의 쿠키 저장소에 쿠키로 저장됨
    - Cookie
        - 클라이언트가 서버에서 받은 쿠키를 저장하고, HTTP 요청시 서버로 전달
    - 모든 요청에 쿠기를 자동 포함
        - 네트워크 추가적 트래픽 유발
        - 최소한의 정보만 사용하는걸 추천함 (세션 id, 인증 토큰)
        - 서버에 전송하지 않고 웹 브라우저 내부에 데이터를 저장해서 사용하고 싶으면 web storage 사용
    - 사용처
        - 사용자 로그인 세션관리
        - 광고 정보 트래킹
    - 보안에 민감한 데이터는 저장하면 안됨!
    - Expires, max-age 쿠키의 생명주기
        - expires : 만료일이 되면 쿠키 삭제
        - max-age : 초단위로 시간이 지나 0이 되거나 음수로 지정하면 쿠키 삭제
        - 세션 쿠키 : 만료 날짜를 생략하면 브라우저 종료시 자동 삭제
        - 영속 쿠키 : 만료 날짜를 입력하면 해당 날짜까지 유지
    - 쿠키의 domain
        - domain값을 명시하면 명시한 도메인과 서브 도메인을 포함하여 쿠키에 접근이 가능함
        - domain값을 생략 시 하위 도메인에서는 접근이 불가능하고 쿠기가 생성된 해당 도메인에서만 쿠키에 접근이 가능함
    - 쿠키의 path
        - 경로를 포함한 하위 경로만 쿠키 접근 가능
    - 쿠키의 보안
        - Secure
            1. 원래 쿠키는 http, https 모두 접근이 가능한데 Secure 적용시 https에서만 접근 가능
        - httpOnly
            1. XSS 공격 방지
               - XXS 공격이란 url자체에 악성 script를 주입하여 javascript로 쿠키에 접근하여 데이터를 조작하는 공격
            2. javascript에서 쿠키에 접근 불가
        - SameSite
            1. CSRF 공격 방지
               - CSRF 공격이란 A 도메인에서 B 도메인의 등록 URL를 사용하여 무작위로 등록시키거나 다른 계정을 도용하여 등록시키는 방법으로 공격  
            2. 요청하는 도메인과 쿠키에 설정된 도메인이 같은 경우에만 쿠키를 전송
    
    
# 캐시

* 캐시란
    - 데이터가 변경되지 않을때 데이터를 한번 다운받아서 웹 브라우저에 저장해둔다.
    - 네트워크 사용량을 줄일 수 있다.
    - 브라우저 로딩 속도가 매우 빨라짐
    - 빠른 사용자 경험이 가능해짐
    - 캐시 유효 시간이 초가되면 서버를 통해 데이터를 다시 조회하고 캐시를 갱신한다.


* 검증 헤더와 조건부 요청 헤더
    - Last-Modified, If-Modified-Since
        - 캐시 데이터와 서버 데이터가 같은지 검증하는 데이터
        - 캐시 유효시간이 초과해서 서버에 다시 요청할 경우 다음과 같은 2가지 경우가 존재
            - 서버에서 기존 데이터를 변경해서 캐시에 저장되는 데이터가 변경되었을 경우
            - 서버에서 기존 데이터를 변경하지 않아서 캐시에 저장되는 데이터가 동일할 경우
        - 위 같은 경우에 캐시를 검증하는 방법
            - 헤더에 Last-Modified 헤더를 추가하여 서버에서 응답
            - 캐시에 Last-Modified값을 갖고 http 요청을 server로 보낼때 if-modified-since 값을 헤더에 추가하여 보냄
            - server에서 해당 소스의 수정일과 client의 http 요청 header의 if-modified-since 값을 비교하여 수정이 안됐을 경우 304 Not Modified 상태코드를 보내주며 HTTP Body 값을 제외하고 응답 값을 보낸다.
    - ETag, If-None-Match
        - Entity Tag
        - 캐시용 데이터에 임의의 고유 버전 이름을 달아줌
        - 데이터가 변경 되면 태그를 변경
        - ETag 값만 비교해서 데이터를 서버로 부터 받을지 캐시를 사용할지 결정
        - 데이터가 변경된게 없다면 위 방법과 같게 304 Not Modified 상태코드 반환과 HTTP Body가 없이 응답을 해준다.
        - 캐시 제어 로직을 서버에서 완전하게 관리가 가능함
            - 예시로 서버에서 배포하고 싶은 시기에 ETag값을 변경하여 변경된 값을 한번에 배포가 가능
    

* 캐시와 조건부 요청 헤더
    - Cache-Control 캐시 제어
        - Cache-Control: max-age : 캐시 유효 시간, 초단위
        - Cache-Control: no-cache : 데이터는 캐시해도 되지만 항상 서버에 검증하고 사용
        - Cache-Control: no-store : 데이터가 민감한 정보이므로 저장하면 안됨
    - Pragma 캐시 제어 (하위 호환)
        - HTTP 1.0 하위 호환
        - 거의 사용하지 않음
    - Expires 캐시 만료일
        - 캐시 만료일을 날짜로 지정
        - HTTP 1.0 부터 사용
        - Expires 보다 유연한 Cache-Control:max-age 사용을 권장
        - 만약 Expires과 Cache-Control:max-age 함께 사용시 Cache-Control:max-age가 우선됨


* 프록시 캐시
    - 원 서버(Origin Server)로 부터 데이터를 응답 받는데 있어서 물리적인 거리가 멀어서 시간이 오래 걸릴 경우 사용자와 서버 사이에 프록시 서버를 두어 public 캐시로 저장한 뒤 사용자들이 원 서버에 접근하여 데이터를 가져오는 것이 아닌 프록시 서버로부터 데이터를 가져오게끔 해줌
    - Cache-Control: public 캐시 : 다른 사용자들이 캐시를 확인 할 수 있음, public 캐시에 저장
    - Cache-Control: private 캐시 : 해당 사용자만을 위한 캐시(기본값), private 캐시에 저장
    - Cache-Control: s-maxage : 프록시 캐시에만 적용되는 max-age
    - Age: (s) : 원 서버에서 응답 후 프록시 캐시 내에 머문 시간


* 캐시 무효화
    - 민감한 정보이거나 데이터가 계속해서 바뀌는 데이터의 경우
    - 웹 브라우저에 캐시가 되지 않도록 해주는 방법
        
            Cache-Control: no-cache, no-store, must-revalidate
            Pragma: no-cache
        - Cache-Control: no-cache
          
            1. 데이터는 캐시하도 되지만 항상 서버에 검증하고 사용
        - Cache-Control: no-store
            
            1. 데이터에 민감한 정보가 있으므로 저장하면 안됨
        - Cache-Control: must-revalidate
            
            1. 캐시 만료 후 최초 조회시 원서버에 검증해야함

            2. 원 서버 접근 실패시 반드시 오류가 발생해야함 : 504(Gateway Timeout)
    
            3. 캐시 유효 시간이라면 캐시를 사용함
    
            4. 서버 설정에 따라 원서버와 네트워크가 단절되었을 때 예전 데이터로 응답해버릴 경우가 있어서 원서버와 데이터가 단절일 경우 504 상태코드를 반환하게 한다.
    
            5. 돈과 관련된 업무거나 항상 정확해야하는 데이터일 경우
          
        - Pragma: no-cache
            
            1. HTTP 1.0 하위 버전으로 요청할 경우 대비
    
`출처` https://www.inflearn.com/course/http-%EC%9B%B9-%EB%84%A4%ED%8A%B8%EC%9B%8C%ED%81%AC/dashboard