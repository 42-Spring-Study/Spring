# C → S: 데이터 전달 방식

## 쿼리 파라미터 이용

GET 메소드, 주로 정렬 필터로 사용한다

## 메시지 바디 이용

POST, PUT, PATCH 등 리소스 등록 혹은 변경에 사용한다

# C → S: 데이터 전달 상황

## 정적 데이터 조회

쿼리 파라미터 없는 GET 요청 → 리소스 경로로 단순 조회 가능

## 동적 데이터 조회

쿼리 파라미터 + GET 요청

- 조회 조건을 줄여주는 필터, 혹은 조회 결과를 정렬하는 정렬 조건에 주로 사용한다.

## HTML Form을 통한 데이터 전송

GET, POST 만 가능하다.

- MIME:
    - 인코딩 o: `application/x-www-form-urlencoded`
    - 인코딩 x(주로 바이너리 데이터): `multipart/form-data` (여러 종류 데이터 한 번에 전송 가능)

## HTTP API를 통한 데이터 전송

- MIME:  `application/json`을 주로 사용 (사실상 표준)

# URI 설계 개념