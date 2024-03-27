# URI(Unified Resource Identifier)

통합된 자원 식별자

## 종류

- URL(Unified Resource Locator): 리소스 위치 지정
- URN(Unified Resource Name): 리소스 이름 지정
    - 위치가 변하더라도 그대로 사용 가능
    - 구현이 어려워 보편화 x

### URL 문법
![](../03_%EB%AA%A8%EB%93%A0%EA%B0%9C%EB%B0%9C%EC%9E%90%EB%A5%BC%EC%9C%84%ED%95%9C-HTTP%EC%9B%B9%EA%B8%B0%EB%B3%B8%EC%A7%80%EC%8B%9D/img/soylee_301.png)
- scheme: 프로토콜
- userinfo: 사용자 정보 포함 →  인증
- host: 호스트명, 도메인명, ip 주소
- port: 생략 가능
- path: 경로, 계층적 구조를 가진다
- query: 웹서버에 제공하는 파라미터, 문자 형태
- fragment: html 내부 북마크에 사용

# 웹 브라우저의 요청 흐름

1. 클라이언트가 서버에 요청(요청 메시지 전송)

2. 서버가 요청 메시지 처리 및 응답 메시지 생성

3. 서버가 클라이언트에 응답 메시지 전달