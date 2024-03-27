# 07. 의존관계자동주입



여러가지 의존관계자동주입에 대해 장단점을 학습,

의존관계자동주입 시 타입을 기준으로 빈을 조회할 때 검색된 빈이 여러개일 때 선택하기 위한 여러 방법을 알아본다.

이후 의존관계자동주입과 수동주입을 상황에 맞게 사용하는 방법을 소개한다.



## 1) 다양한 의존관계 주입 방법



### 1] 생성자 주입

- 생성자 호출시점에 딱 1번만 호출되는 것이 보장된다.
- 불변, 필수 의존관계에 사용 (대부분 DI 후 변경할 일이 없어 불변으로 설정을 권장)
- 필요한 의존객체는 final로 선언하여 컴파일타임에 생성자세팅이 강제된다.

~~~java
@Test
void createOrder() {
    MemeryMemberRepository repository = new MemeryMemberRepository();
    repository.save(new Member(1L, "seongtki", Grade.VIP));
    OrderServiceImpl orderService = new OrderServiceImpl(repository, new FixDiscountPolicy());
    orderService.createOrder(1L, "itemA", 10000);
}
~~~



### 2] 수정자 주입(setter 주입)

- setter 를 통해서 의존관계를 주입하는 방법이다.
- 선택, 변경 가능성이 있는 의존관계에 사용
- 가변객체가 되어 3자가 인스턴스를 변경할 수 있어 특별한 경우가 아니라면 권장되지 않는다.



### 3] 필드 주입

- 필드에 바로 주입하는 방법이다.
- 애플리케이션의 실제 코드와 관계 없는 테스트 코드 혹은
  스프링 설정을 목적으로 하는 @Configuration 같은 곳에서만 특별한 용도로 사용
- 일반 자바에서는 의존주입이 불가해 사용할 수 없다는 단점이 있음.
- 아래 예제는 이를 해결하기 위해 수정자를 추가하였다. (가변객체가 되기 때문에 역시 권장하지 않는다.)

~~~java
@Test
void fieldInjectTest() {
    OrderServiceImpl orderService = new OrderServiceImpl();
    orderService.setMemberRepository(new MemeryMemberRepository());
    orderService.setDiscountPolicy(new FixDiscountPolicy());
    orderService.createOrder(1L, "itemA", 10000);
}
~~~





### 4] 일반 메서드 주입

- 일반 메서드를 통해서 주입 받을 수 있다.
- 일반적으로 잘 사용하지 않는다.





## 2) 의존주입 옵션 처리

주입할 스프링 빈이 없어도 동작해야 할 때가 있다. 그런데 @Autowired 만 사용하면 required 옵션의 기본값이 true 로 되어 있어서 자동 주입 대상이 없으면 오류 가 발생한다.



##### 자동 주입 대상을 옵션으로 처리하는 방법은 다음과 같다.

- @Autowired(required=false) : 자동 주입할 대상이 없으면 수정자 메서드 자체가 호출 안됨
- org.springframework.lang.@Nullable : 자동 주입할 대상이 없으면 null이 입력된다.
- Optional<> : 자동 주입할 대상이 없으면 Optional.empty 가 입력된다.

~~~java
//호출 안됨
@Autowired(required = false)
public void setNoBean1(Member member) {
 System.out.println("setNoBean1 = " + member);
}
//null 호출
@Autowired
public void setNoBean2(@Nullable Member member) {
 System.out.println("setNoBean2 = " + member);
}
//Optional.empty 호출
@Autowired(required = false)
public void setNoBean3(Optional<Member> member) {
 System.out.println("setNoBean3 = " + member);
}
~~~





## 3) 타입하나에서 빈이 2개이상 조회되는 케이스



~~~java
@Component
public class RateDiscountPolicy implements DiscountPolicy {}
@Component
public class FixDiscountPolicy implements DiscountPolicy {}
~~~



~~~java
ApplicationContext context = new AnnotationConfigApplicationContext(AutoAppConfig.class);
        context.getBean(DiscountPolicy.class);
~~~



![스크린샷 2024-03-27 오전 11.02.29](../img/seongtki_215.png)

- `getBean` 을 하위 타입으로 지정할 수 도 있지만, 하위 타입으로 지정하는 것은 DIP를 위배하고 유연성이 떨어진다. 
  스프링 빈을 수동 등록해서 문제를 해결해도 되지만, `의존 관계 자동 주입에서 해결하는 여러 방법`이 있다.



### @Autowired 필드 명, @Qualifier, @Primary



##### @Autowired 필드 명 매칭

~~~java
@Autowired private DiscountPolicy rateDiscountPolicy;
~~~

필드 명 매칭은 먼저 타입 매칭을 시도 하고 그 결과에 여러 빈이 있을 때 추가로 동작하는 기능이다.



##### @Qualifier

~~~java
@Component
@Qualifier("mainDiscountPolicy")
public class RateDiscountPolicy implements DiscountPolicy {}
@Component
@Qualifier("fixDiscountPolicy")
public class FixDiscountPolicy implements DiscountPolicy {} 
~~~

~~~java
@Autowired
public OrderServiceImpl(MemberRepository memberRepository,
 @Qualifier("mainDiscountPolicy") DiscountPolicy
discountPolicy) {
 this.memberRepository = memberRepository;
 this.discountPolicy = discountPolicy;
}
~~~



##### @Primary

~~~java
@Component
@Primary
public class RateDiscountPolicy implements DiscountPolicy {}
@Component
public class FixDiscountPolicy implements DiscountPolicy {}
~~~

~~~java
//생성자
@Autowired
public OrderServiceImpl(MemberRepository memberRepository,
 DiscountPolicy discountPolicy) {
 this.memberRepository = memberRepository;
 this.discountPolicy = discountPolicy;
}
~~~



##### @Qualifier vs @Primary

- 코드에서 자주 사용하는 메인 데이터베이스의 커넥션을 획득하는 스프링 빈이 있고, 코드에서 특별한 기능으로 가끔 사 용하는 서브 데이터베이스의 커넥션을 획득하는 스프링 빈이 있다고 생각해보자.
- 메인 데이터베이스의 커넥션을 획득하 는 스프링 빈은 @Primary 를 적용해서 조회하는 곳에서 @Qualifier 지정 없이 편리하게 조회하고, 서브 데이터베 이스 커넥션 빈을 획득할 때는 @Qualifier 를 지정해서 명시적으로 획득 하는 방식으로 사용하면 코드를 깔끔하게 유지할 수 있다.
- 물론 이때 메인 데이터베이스의 스프링 빈을 등록할 때 @Qualifier 를 지정해주는 것은 상관없다.

##### 우선순위

- @Primary 는 기본값 처럼 동작하는 것이고, @Qualifier 는 매우 상세하게 동작한다.
- 수동으로 조작하는 @Qualifier가 우선권이 높다.



### @Qualifier 사용자실수 런타임에러 방지

- @Qualifier("mainDiscountPolicy") 이렇게 문자를 적으면 컴파일시 타입 체크가 안된다. 
- 아래처럼 `Qualifier`를 래핑한 애노테이션을 정의하면, 사용자실수를 예방할 수 있다.

~~~java
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER,
        ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Qualifier("mainDiscountPolicy")
public @interface MainDiscountPolicy {}
~~~

~~~java
@Component
@MainDiscountPolicy
public class RateDiscountPolicy implements DiscountPolicy {}
~~~

~~~java
//생성자 자동 주입
@Autowired
public OrderServiceImpl(MemberRepository memberRepository,
 @MainDiscountPolicy DiscountPolicy discountPolicy) {
 this.memberRepository = memberRepository;
 this.discountPolicy = discountPolicy;
}
~~~









## 4) 조회한 빈이 모두 필요할 때, List, Map



~~~java

public class AllBeanTest {
    @Test
    void findAllBean() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(AutoAppConfig.class, DiscountService.class);
        DiscountService discountService = ac.getBean(DiscountService.class);
        Member member = new Member(1L, "userA", Grade.VIP);
        int discountPrice = discountService.discount(member, 10000,"fixDiscountPolicy");
        assertThat(discountService).isInstanceOf(DiscountService.class);
        assertThat(discountPrice).isEqualTo(1000);
    }
    static class DiscountService {
        private final Map<String, DiscountPolicy> policyMap;
        private final List<DiscountPolicy> policies;
        public DiscountService(Map<String, DiscountPolicy> policyMap, List<DiscountPolicy> policies) {
            this.policyMap = policyMap;
            this.policies = policies;
            System.out.println("policyMap = " + policyMap);
            System.out.println("policies = " + policies);
        }
        public int discount(Member member, int price, String discountCode) {
            DiscountPolicy discountPolicy = policyMap.get(discountCode);
            System.out.println("discountCode = " + discountCode);
            System.out.println("discountPolicy = " + discountPolicy);
            return discountPolicy.discount(member, price);
        }
    }
}
~~~









## 5) 자동, 수동의 올바른 실무 운영 기준



그러면 어떤 경우에 컴포넌트 스캔과 자동 주입을 사용하고, 어떤 경우에 설정 정보를 통해서 수동으로 빈을 등록하고, 의존관계도 수동으로 주입해야 할까?



@Component, @Controller , @Service , @Repository 이 편한데

@Configuration , @Bean 을 이용해서 수동으로 빈을 등록 해야 할까?



**업무 로직 빈:** 웹을 지원하는 컨트롤러, 핵심 비즈니스 로직이 있는 서비스, 데이터 계층의 로직을 처리하는 리포 지토리등이 모두 업무 로직이다. 보통 비즈니스 요구사항을 개발할 때 추가되거나 변경된다. 

- 비지니스로직은 유형이 비슷하고 개수도 많다. 컴포넌트 스캔 해도, 오류 시 원인을 빨리 찾을 수 있다.
- 수동등록은 개수가 너무 많아져 관리가 힘들다.

**기술 지원 빈:** 기술적인 문제나 공통 관심사(AOP)를 처리할 때 주로 사용된다. 데이터베이스 연결이나, 공통 로그 처리 처럼 업무 로직을 지원하기 위한 하부 기술이나 공통 기술들이다.

- 애플리케이션에 광범위하게 영향을 미치는 기술 지원 객체는 수동 빈으로 등록해서 설정 정보에 바로 나타나게 하는 것이 유지보수 하기 좋다.

  (다만 스프링이 이미자동으로 등록한 빈은 그대로 둔다. 스프링의도를 잘 파악하고 사용하는게 중요함.)

  

##### 다형성 관련 빈

- DiscountPolicy 타입 빈이 여러개다.
- @Qualifier, @Primary 등 상세설정이 필요한데, 빈설정을 한군데에서 관리하는 게 유지보수상 더 좋다.

~~~java
@Configuration
public class DiscountPolicyConfig {
 @Bean
 public DiscountPolicy rateDiscountPolicy() {
 return new RateDiscountPolicy();
 }
 @Bean
 public DiscountPolicy fixDiscountPolicy() {
 return new FixDiscountPolicy();
 }
}
~~~



















































