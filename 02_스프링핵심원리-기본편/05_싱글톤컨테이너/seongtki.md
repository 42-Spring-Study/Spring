# 05. 싱글톤 컨테이너



웹 어플리케이션 환경에서 싱글톤이 필요한 이유와 자바에서 싱글톤 구현 방법 및 한계점과

스프링이 제공하는 싱글톤 컨테이너 특징에 대해서 학습한다.




## 1) 웹 애플리케이션과 싱글톤

웹에는 여러 고객이 동시에 요청할 수 있다. 이 때마다 객체가 계속 생성되면 메모리관리가 힘들 것이다.

그래서 하나의 객체를 계속 사용하도록 싱글톤 패턴이 등장하였다.



## 2) 싱글톤 패턴

클래스의 인스턴스가 딱 1개만 생성되는 것을 보장하는 디자인 패턴이다.

그래서 객체 인스턴스를 2개 이상 생성하지 못하도록 막아야 한다.

강사님은 싱글톤 패턴 하나만 소개하였는데, 몇가지 더 정리 한 방법을 아래 링크에 정리해 놓았다.

[java singleton 여러방법](https://github.com/seongtaekkim/TIL/blob/master/java/effective-java/docs/02-creating-and-destroying-objects/item03/item03.md)



#### 싱글톤 패턴 문제점
- 싱글톤 패턴을 구현하는 코드 자체가 많이 들어간다.
- 의존관계상 클라이언트가 구체 클래스에 의존한다. DIP를 위반한다.
- 클라이언트가 구체 클래스에 의존해서 OCP 원칙을 위반할 가능성이 높다.
- 테스트하기 어렵다.
- 내부 속성을 변경하거나 초기화 하기 어렵다.
- private 생성자로 자식 클래스를 만들기 어렵다.
- 결론적으로 유연성이 떨어진다.
- 안티패턴으로 불리기도 한다.

문제가 이렇게 많은데 스프링에서 대체 어떻게 싱글톤 개념을 사용할까?



## 3) 싱글톤 컨테이너

스프링 컨테이너는 싱글턴 패턴을 적용하지 않아도, 객체 인스턴스를 싱글톤으로 관리한다.

- 이전에 설명한 컨테이너 생성 과정을 자세히 보자. 컨테이너는 객체를 하나만 생성해서 관리한다.

스프링 컨테이너는 싱글톤 컨테이너 역할을 한다. 이렇게 싱글톤 객체를 생성하고 관리하는 기능을 `싱글톤 레지스트리`라 한다.

스프링 컨테이너의 이런 기능 덕분에 싱글턴 패턴의 모든 단점을 해결하면서 객체를 싱글톤으로 유지할 수 있다.

- 싱글톤 패턴을 위한 지저분한 코드가 들어가지 않아도 된다.
- DIP, OCP, 테스트, private 생성자로 부터 자유롭게 싱글톤을 사용할 수 있다.



##### 한마디로 스프링이 싱글톤을 구현한 싱글톤 레지스트리를 만들었으니 우리는 편하게 사용하면 된다는 말이다..

~~~
@Test
@DisplayName("스프링 컨테이너와 싱글톤")
void SpringContainer() {
    ApplicationContext ac = new
            AnnotationConfigApplicationContext(AppConfig.class);
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
~~~





## 4) 싱글톤 방식의 주의점

싱글톤 패턴이든, 스프링 같은 싱글톤 컨테이너를 사용하든, 객체 인스턴스를 하나만 생성해서 공유하는 싱글톤
방식은 여러 클라이언트가 하나의 같은 객체 인스턴스를 공유하기 때문에 싱글톤 객체는 상태를 유지(stateful)하게 설계하면 안된다.



#### stateful 예제

- 아래와 같은 class가 싱글톤으로 등록되었다고 해보자.

~~~java
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
~~~

아래 테스트코드에서 각각 다른 스레드에서 빈(statefulService)을  A, B이름으로 각각 꺼냈을 때 한쪽이 값을 바꾸면 다른 한쪽도 바뀐다.

공유필드는 항상 조심해야 한다. 스프링 빈은 항상 무상태(stateless)로 설계하자

~~~java
@Test
void statefulServiceSingleton() {
    ApplicationContext ac = new AnnotationConfigApplicationContext(TestConfig.class);
    StatefulService statefulService1 = ac.getBean("statefulService", StatefulService.class);
    statefulService1.order("userA", 10000);
    new Thread(() -> {
        StatefulService statefulService2 = ac.getBean("statefulService", StatefulService.class);
        statefulService2.order("userB", 20000);
    }).start();

    int price = statefulService1.getPrice();
    System.out.println("price = " + price);
    Assertions.assertThat(statefulService1.getPrice()).isEqualTo(20000);
}
~~~



#### stateless로 어떻게 ?

- 특정 클라이언트에 의존적인 필드가 있으면 안된다.
- 특정 클라이언트가 값을 변경할 수 있는 필드가 있으면 안된다
- 가급적 읽기만 가능해야 한다.
- 필드 대신에 자바에서 공유되지 않는, 지역변수, 파라미터, ThreadLocal 등을 사용해야 한다.

~~~java
public class StatefulService {
    public int order(String name, int price) {
        return price;
    }
}
~~~







## 5) @Configuration과 싱글톤



ApplicationContext 는 @Bean 를 이용해 빈을 생성한다고 했다.

아래코드에서 memberRepository() 는 세번 호출된다. 그런데도 MemeryMemberRepository 타입 인스턴스가 싱글톤 형태로 존재할까?

~~~java
@Configuration
public class AppConfig {

    @Bean
    public MemberService memberService() {
        return new MemberServiceImpl(memberRepository());
    }
    @Bean
    public OrderService orderService() {
        return new OrderServiceImpl(memberRepository(), discountPolicy());
    }
    @Bean
    public MemeryMemberRepository memberRepository() {
        return new MemeryMemberRepository();
    }
}
~~~

아래 테스트코드 실행 결과 MemeryMemberRepository는 싱글톤 형태로 존재한다.

~~~java
@Test
void configurationTest() {
    ApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);
    MemberServiceImpl memberService = ac.getBean("memberService", MemberServiceImpl.class);
    OrderServiceImpl orderService = ac.getBean("orderService", OrderServiceImpl.class);
    MemberRepository memberRepository = ac.getBean("memberRepository", MemberRepository.class);
    ...
~~~

#### 바이트코드를 조작하는 CGLIB

스프링에서는 싱글톤 레지스트리라는 개념으로 스프링 빈이 싱글톤이 되도록 보장해준다.
스프링은 자바코드가 몇번 호출되는것과 관계없이 클래스의 바이트코드를 조작하는 ASM(CGLIB)을 사용한다.

이는 @Configuration 를적용한 Type에 한해 적용된다.

아래 테스트를 보면, AppConfig의 빈은` hello.core.AppConfig$$SpringCGLIB$$0` 라는 이름으로 출력된다.

이는 컴파일 시 AppConfig를 상속받는 CGLIB 에서 생성한 바이트코드 기반 클래스가 만들어져 내부에 @Bean을 싱글톤으로 존재할 수 있도록 처리한다.

~~~java
@Test
void configurationDeep() {
    ApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);
    AppConfig bean = ac.getBean(AppConfig.class);

    System.out.println("bean = " + bean.getClass());
    // 출력: bean = class hello.core.AppConfig$$SpringCGLIB$$0
}
~~~

- 강사는 GCLIB 예상코드를 아래와 같이 표현했는데, 시간이 되면 찾아보자.
  - @Bean이 붙은 메서드마다 이미 스프링 빈이 존재하면 존재하는 빈을 반환하고, 스프링 빈이 없으면 생성해서 스 프링 빈으로 등록하고 반환하는 코드가 동적으로 만들어진다.

~~~java
@Bean
public MemberRepository memberRepository() {

 if (memoryMemberRepository가 이미 스프링 컨테이너에 등록되어 있으면?) {
 return 스프링 컨테이너에서 찾아서 반환;
 } else { //스프링 컨테이너에 없으면
 기존 로직을 호출해서 MemoryMemberRepository를 생성하고 스프링 컨테이너에 등록
 return 반환
 }
}
~~~



#### @Configureation 이 없이 @Bean 만 있다면 어떻게 될까

##### AppConfig 빈을 조회하면

~~~
bean = class hello.core.AppConfig$$SpringCGLIB$$0 가 아닌
bean = class hello.core.step3.AppConfig 형태로 조회된다.
~~~

- CGLIB 가 관여하지 않는 코드가 된다.

CGLIB가 바이트코드 조작하지 않으면, 순수자바로 인스턴스를 생성하므로
아래 테스트의 빈은 세개 모두 다르다.

~~~java
@Test
void configurationTest() {
    ApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);
    MemberServiceImpl memberService = ac.getBean("memberService", MemberServiceImpl.class);
    OrderServiceImpl orderService = ac.getBean("orderService", OrderServiceImpl.class);
    MemberRepository memberRepository = ac.getBean("memberRepository", MemberRepository.class);
  ...
~~~









