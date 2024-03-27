### 학습 내용

- 싱글톤 패턴이란 무엇인가
- 싱글톤 컨테이너의 필요성
- 싱글톤 방식의 주의점 - 무상태
- 스프링 컨테이너에서 어떻게 싱글톤 패턴이 유지되는가?

---

# 싱글톤 패턴의 필요성

스프링 프로젝트 대부분 웹 프로젝트이다. 또한 웹 프로젝트 특성 상 많은 고객이 동시에 요청을 한다. 이러한 요청을 받을 때마다 객체를 생성하고 소멸시키는 것은 심한 메모리 낭비가 될 수 있다.

# 싱글톤 패턴이란?

디자인 패턴 중 하나. 클래스 당 객체 1개 **보장**

→ 인스턴스를 2개 이상 생성하지 못하도록 막아야 한다.

이를 구현하는 방법에는 여러가지가 있지만, 그 중에 가장 간단하고 안전한 private 생성자를 이용해 직접 구현해보자.

## private 생성자를 이용한 방법

가장 간단한 구현 방법이다. new를 이용한 생성을 막는다.

```java
package hello.core.singleton;

public class SingletonService {
 //1. static 영역에 객체를 딱 1개만 생성해둔다.
 private static final SingletonService instance = new SingletonService();
 
 //2. public으로 열어서 객체 인스턴스가 필요하면 이 static 메서드를 통해서만 조회하도록 허용한다.
 public static SingletonService getInstance() {
	 return instance;
 }
 
 //3. 생성자를 private으로 선언해서 외부에서 new 키워드를 사용한 객체 생성을 못하게 막는다.
 private SingletonService() {}
}
```

다음은 테스트 코드이다

```java
//new SingletonService(); //컴파일 에러
SingletonService ss1 = SingletonService.getInstance();
SingletonService ss2 = SingletonService.getInstance();

assertThat(ss1).isSameAs(ss2); //success
```

## 싱글톤 패턴의 문제점

- 패턴 구현 코드가 많이 들어간다
- 클라이언트가 구체 클래스에 의존(=DIP 위반)
    - ex) 다른 곳에서 `getInstance()`로 객체 가져와야 함
- DIP 위반으로 인해, OCP 위반 가능성 높다
- 테스트 어려움
- 내부 속성 변경 or 초기화 여려움
- private 생성자 → 자식 클래스 생성 어려움
- 유연성 떨어짐 (ex, DI 적용 어려움)

⇒ 안티패턴으로 불리기도 함

싱글톤 컨테이너가 이러한 문제점을 모두 해결했다…!

# 싱글톤 컨테이너

싱글톤 컨테이너는 기존 싱글톤 패턴의 문제점 해결하면서, 객체 인스턴스롤 싱글톤으로 관리한다.

스프링 빈이 싱글톤으로 관리되는 빈이다.

## 특징

- 싱글톤 패턴을 따로 적용하지 않아도, 객체를 싱글톤으로 관리한다.
- 스프링 컨테이너는 싱글톤 역할을 한다.
    - 싱글톤 객체를 생성하고 관리하는 기능을 `싱글톤 레지스트리`라고 한다.

⇒ 싱글톤 패턴의 단점이 해결되었다…!

- 싱글톤 패턴 코드(aka, 지저분한 코드) 필요 없다.
- DIP, OCP, test, private 생성자 로부터 자유롭게 싱글톤 사용 가능

## (스프링빈 == 싱글톤) 검증 테스트

```java
@Test
@DisplayName("스프링 컨테이너와 싱글톤")
void springContainer() {
 ApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);
 //1. 조회: 호출할 때 마다 같은 객체를 반환
 MemberService memberService1 = ac.getBean("memberService", MemberService.class);
 //2. 조회: 호출할 때 마다 같은 객체를 반환
 MemberService memberService2 = ac.getBean("memberService", MemberService.class);
 
 //참조값이 같은 것을 확인
 System.out.println("memberService1 = " + memberService1);
 System.out.println("memberService2 = " + memberService2);

 //memberService1 == memberService2
 assertThat(memberService1).isSameAs(memberService2);
}
```

<aside>
💡 스프링의 빈 등록 방식은 기본적으로 싱글톤이지만, 다른 방식으로 변경 가능하다.
(이는 ‘섹션 9. 스코프 빈’을 참조하자)

</aside>

# 싱글톤 방식의 주의점

싱글톤의 장점이자 단점은 객체를 공유한다는 것이다. 따라서 객체는 `무상태` 를 갖도록 설계해야한다. 그렇지 않으면 큰 장애가 발생할 수 있다.

## 무상태 설계란?

- 가급적 읽기만 가능
- 특정 클라이언트에 의존적인 필드가 있으면 x
- 특정 클라이언트가 값을 변경할 수 있는 필드 x
- 가급적 필드 대신, 지역변수, 파라미터, ThreadLocal 등을 사용해야 한다.

### 상태 코드 예시

```java
package hello.core.singleton;
public class StatefulService {
 private int price; //상태를 유지하는 필드
 public void order(String name, int price) {
	 System.out.println("name = " + name + " price = " + price);
	 this.price = price; //여기가 문제!
 }
 public int getPrice() {
	 return price;
 }
}
```

각 요청이 들어올 때마다 새로운 스레드가 생성된다.

각 스레드에서 order() 을 호출할 때마다, price의 값이 바뀌어 getPrice() 호출 시 원하는 값이 나오지 않을 수 있다.

### 무상태 코드 예시

```java
package hello.core.singleton;
public class StatefulService {
	//필드 삭제
	public void order(String name, int price) {
		System.out.println("name = " + name + " price = " + price);
		return price; //바로 리턴해 지역변수에 저장할 수 있게...
	}
}
```

테스트 코드

```java
StatefulService ss = ac.getBean("statefulService", StatefulService.class);
int price = ss.getOrder("userA", 10000);
System.out.println(" price = " + price); //price = 10000
```

# 스프링 컨테이너에서 어떻게 싱글톤 패턴이 유지되는가?

## 기존 AppConfig 코드의 이상한 점…

기존 AppConfig 코드를 살펴보자.

```java
package hello.core;

@Configuration
public class AppConfig {

    @Bean
    public MemberService memberService(){
        System.out.println("call AppConfig.memberService");
        return new MemberServiceImpl(memberRepository());
    }

    @Bean
    public MemberRepository  memberRepository(){
        System.out.println("call AppConfig.memberRepository");
        return new MemoryMemberRepository();
    }

    @Bean
    public OrderService orderService(){
        System.out.println("call AppConfig.orderService");
        return new OrderServiceImpl(memberRepository(), discountPolicy());
    }
	...
}

```

- memberService 빈을 만드는 코드를 보면 memberRepository() 를 호출한다.
    - 이 메서드를 호출하면 new MemoryMemberRepository() 를 호출한다.
- orderService 빈을 만드는 코드도 동일하게 memberRepository() 를 호출한다.
    - 이 메서드를 호출하면 new MemoryMemberRepository() 를 호출한다.

⇒ 서로 다른 2개의 MemberRepository가 생성되는 것처럼 보인다.

하지만, 로그를 확인해보면 각 함수 memberService, memberRepository, orderService가 한 번씩만 호출되는 것을 알 수 있다. 어떻게 된 것일까?

## 바이트코드의 조작

스프링은 @Configuration이 붙은 클래스의 바이트코드를 조작한다.

### 빈에 해당클래스가 등록되지 않는다.

```java
    @Test
    void cofigurationDeep(){
        ApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);
        AppConfig bean = ac.getBean(AppConfig.class); //container에 appconfig도 등록되어 있다.

        System.out.println("bean = " + bean.getClass());
        //bean = class hello.core.AppConfig$$EnhancerBySpringCGLIB$$bd479d70
    }
```

출력 결과 `class hello.core.AppConfig$$EnhancerBySpringCGLIB$...`임을 확인할 수 있다. 이는 CGLIB에 의해 바이트코드가 조작되었다는 것을 의미한다. (순수 클래스라면 `class hello.core.AppConfig`f로 출력되었어야 한다)

### 스프링 빈으로 등록된 건 무엇인가?

CGLIB는 @Configuration이 붙은 클래스를 상속받는 임의의 자식 클래스를 생성해 스프링빈으로 등록하여 싱글톤이 유지되도록 보장해준다

### 싱글톤 유지되는 원리

CGLIB의 내부 기술을 대략적으로 표현하면 다음과 같다.

```java
@Bean
public MemberRepository memberRepository() {
 if (memoryMemberRepository가 이미 스프링 컨테이너에 등록되어 있으면?) {
	 return 스프링 컨테이너에서 찾아서 반환;
 } else { 
 //스프링 컨테이너에 없으면 기존 로직을 호출해서 MemoryMemberRepository를 생성하고 스프링 컨테이너에 등록
	 return 반환
 }
}
```

@Bean이 붙은 메서드마다 이미 스프링빈이 존재하면 해당 Bean을 반환하고, 그렇지 않다면, 새로 생성하여 등록하고 반환하는 코드가 동적으로 생성된다.

## @Configuration이 없다면?

- AppConfig가 순수한 상태(`hello.core.AppConfig`)로 컨테이너에 등록된다.
- MemberRepository가 총 2번 호출되며, 각 인스턴스는 다른 인스턴스이다.

⇒ @Bean은 스프링빈으로 등록하는 역할만 할 뿐, 싱글톤을 보장하지 않는다.

⇒ 스프링 설정 정보는 항상 @Configuration을 사용하자!