# 🔴 프로젝트 세팅

## 🟠 Spring Initializr

![](https://velog.velcdn.com/images/ililil9482/post/34d96e3b-50b3-49b7-8791-87a110c344c1/image.png)

프로젝트 자체가 가볍게 해볼 내용으로 db와 web 정도만 세팅해서 진행하려고 한다.

### 🟢 테스트용 db h2를 사용하지 않는 이유

참고로 이번에는 h2를 사용하지 않고 spring에서 지원하는 docker compose support를 사용하여 개발 환경에서는 docker로 mysql 서버를 띄워서 테스트 db로 사용해보려고 한다.

h2가 가벼워서 좋지만 docker를 사용할 줄 안다면 docker compose support로 세팅해서 여러가지를 테스트용도로 쓰는것이 redis나 kafka등 다른 시스템을 추가하기도 좋을거 같아서다.

## 🟠 프로젝트 실행

![](https://velog.velcdn.com/images/ililil9482/post/f4fd71f7-916a-4fe7-b4df-47c1e5f307a2/image.png)


프로젝트를 실행시키고 해당 페이지로 접근했다면 정상적으로 실행 완료

### 🟢 docker compose support 주의

docker compose support를 의존성 추가하였다면 실행시 로컬 컴퓨터에 docker가 실행된 상태여야만 정상적으로 실행된다. 이점만 주의하면 크게 문제 될건 없다.

## 🟠 프로젝트를 진행하는 목표

### 🟢 현실적인 목표

이번 프로젝트를 통해 부모님께서 판매하고 계시는 사과들을 전단지처럼 간단하게 판매 홍보 사이트를 만들어보려고 한다.  
전단지와 다른 기능이라면 주문까지 들어올 수 있게 해보려고 한다.

그냥 네이버 스마트 스토어를 쓰면 되지 왜 전단지 사이트를 만들어야 할까? 라는 생각이 드실수도 있지만 스마트 스토어를 안써본게 아니다. 스마트 스토어도 굉장히 좋은 사이트이지만 부모님께서 사용하시기에는 너무 기능이 많았고 복잡했다. 부모님께서 원하시는건 그냥 간단하게 사과를 판매하기 위한 정보가 노출되는 사이트와 주문만 들어올 수 있는 정도의 기능을 원하셨기 때문이다.

노션으로 해당 부분을 만들어도 되지만 위 기능들은 내가 직접 해볼수 있을거 같아 추석 전까지 사이트를 만들어보려고 한다.

### 🟢 개발자로써 목표

이번 프로젝트가 정말 간단하게 만들 프로젝트이기 때문에 기능을 많이 넣는다는 느낌보다는 SOLID 원칙과 Clean Code 지향, 테스트 코드, DDD 개념 적용, 디자인패턴 등 기능보단 단단하고 읽기 좋은 코드를 만들어보는 프로젝트가 목표다.