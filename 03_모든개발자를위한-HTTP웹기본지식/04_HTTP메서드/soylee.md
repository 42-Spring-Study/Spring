# API 설계

URI + HTTP method

- 리소스를 식별할 수 있는 URI가 좋은 URI 이다.
- 리소스와 행위(메소드)를 분리하여 설계해야 한다

### 예제

회원 조회: /members + GET

회원 등록: /members + POST

회원 수정: /members/{id} + PATCH

회원 삭제: /members/{id} + DELETE

# HTTP method

메시지의 동작 정의

## GET

리소스 조회 시 사용한다.

- 바디 원칙적으로 사용 금지
- 쿼리문 작성 가능
- html form 가능

## POST

요청 데이터 처리하며, 주로 신규 리소스 등록, 프로세스 처리에 사용한다.

- 응답 바디에 요청한 데이터 실린다.
- 보통 클라이언트가 데이터 위치 모를 때
- 혹은 애매할 때 사용
- html form 가능

## PUT

데이터 대체(완전 대체) 시 사용

- 클라이언트가 리소스를 식별한다(정확한 URI 알고 있다)

## PATCH

데이터 일부 수정 시 사용

## DELETE

리소스 삭제 시 사용

## 기타 메서드

### HEAD

get과 동일하지만, 상태줄과 헤더만 반환한다.

### OPTIONS

대상 리소스에 대한 통신 가능 옵션(메서드)를 설명

- 주로 CORS에서 사용

### CONNECT

대상 리소스로 식별되는 서버에 대한 터널을 지정

### TRACE

대상 리소스에 대한 경로를 따라 루프백 테스트를 수행

# HTTP method 속성

## 안전(Safe Methods)

호출 시 리소스 변경 없으며, get, head 등이 이에 해당한다

- 해당 리소스에 대한 안전만 고려한다.
    - 반복 호출로 인한 로그 → 장애 는 고려하지 않는다

## 멱등성(Idempotent Methods)

반복 호출해도 항상 같은 결과가 발생하며, GET, PUT, DELETE 등이 이에 해당한다.

- POST는 멱등하지 않다.
- 멱등한 메소드는 반복 호출이 가능하므로 자동 복구 메커니즘에 사용될 수 있다.

## 캐시 가능(Cacheable Methods)

응답 결과로 받은 리소스를 캐시해서 상용해도 된다. GET, HEAD, POST, PATH 등이 이에 해당

- 실제로는 GET, HEAD 정도만 캐시로 사용한다.
    - POST, PATCH는 본문 내용까지 캐시 키로 고려해야 하기 때문