### 학습 내용

- 빈의 라이프 사이클
- 생성자 - 초기화 콜백 분리 필요성
- 초기화, 소멸자 사용 등록 방법 3가지

---

# 빈의 라이플 사이클

객체 인스턴스 생성 → 의존관계 주입 → 초기화 콜백 → 사용 → 소멸자 콜백 → 스프링 종료

# 생성자 초기화 분리 필요성

- 미리 생성 해 놓고 사용하기 직전에 초기화 할 수 있는 **지연** 가능

# 초기화 - 소멸자 등록 방법

## 초기 방법 - 인터페이스 사용

스프링 의존적 설계 → 부담이다.

현재는 많이 사용하지 않음

## @Bean

@Bean 에 인자로 함수명

destroy → 추론 기능, 즉 외부라이브러리의 close, shutdown 포함한 메소드 있으면 같이 해준다.

## 권장 방법

자바 진영에서 공식적으로 지원하는 기능

추론 기능 제공하지 않음