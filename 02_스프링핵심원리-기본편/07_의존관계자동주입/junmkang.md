# 섹션 7. 의존관계 자동 주입

## 다양한 의존관계 주입 방법

의존관계 주입 방법

1. 생성자 주입
2. 수정자 주입 (setter 주입)
3. 필드 주입
4. 일반 메서드 주입

### 생성자 주입

```java
private final MemberRepository memberRepository;
private final DiscountPolicy discountPolicy;

@Autowired
public OrderServiceImpl(MemberRepository memberRepository, DiscountPolicy discountPolicy) {
    this.memberRepository = memberRepository;
    this.discountPolicy = discountPolicy;
}
```

기존에 계속 사용을 했던 생성자를 통해서 의존관계를 주입하는 방식을 이야기한다.

해당 방식은 생성자를 통해서만 주입이 되도록 설정을 해두었기 때문에 **불변**과 **필수** 라는 특징을 가지고 있다.

- 불변 : 생성자를 통해서만 의존관계주입이 가능하기때문에 외부에서는 접근이 불가능하다.
- 필수 : 생성자라는 것이 해당 `class`가 실행될때 무조건 적으로 실행이 되는 것이기 때문에 항상 의존관계가 존재할 수 있게된다.

```java
public OrderServiceImpl(MemberRepository memberRepository, DiscountPolicy discountPolicy) {
    this.memberRepository = memberRepository;
    this.discountPolicy = discountPolicy;
}
```

위와 같이 생성자가 하나만 존재하는 경우에는 `@Autowired`를 생략하더라도 의존관계 자동 주입이 가능하다고한다. ( 물론 스프링 빈에만 해당 )

### 수정자 주입

```java
private MemberRepository memberRepository;
private DiscountPolicy discountPolicy;

@Autowired
public void setMemberRepository(MemberRepository memberRepository) {
    this.memberRepository = memberRepository;
}

@Autowired
public void setDiscountPolicy(DiscountPolicy discountPolicy) {
    this.discountPolicy = discountPolicy;
}
```

위에 코드와 같이 `setter`를 사용해서 의존관계를 주입하는 방식을 수정자 주입이라고 이야기한다.

위와 같은 방식의 장점은 상황에 따라 **선택 및 변경 가능성**을 두고 의존관계를 주입할 수 있는 방식이기때문에 `MemberRepository` 을 다른것으로 바꾼다던가 등의 방식에서 활용될 수 있는 방식이라고한다. 

`setter`에 `@Autowired` 를 사용하면 자동으로 의존관계주입하는 방식으로도 사용이 가능하다고한다.

( TODO : `setter`에 `@Autowired` 를 붙여주는 방식을 사용해도 메모리 관리면에서 추후에 따로 의존관계를 주입해준다던가? 방식이 가능한건가..? )

위에 방식은 불변이나 필수의 개념을 가지고있지 않은 방식이기때문에 문제가 발생될 가능성도 있다고한다.
그래서 꼭 필요한 경우가 아니라면 대부분 생성자 주입을 사용한다고한다.

### 필드 주입

```java
@Component
public class OrderServiceImpl implements OrderService {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private DiscountPolicy discountPolicy;
}
```

생성자나 수정자 주입말고 필드 자체에 `@Autowired` 를 걸어서 의존관계를 주입하는 방식을 이야기한다.
해당 방식이 코드상으로 가장 깔끔하게 보이기는 하지만 해당 방식은 생성자랑 수정자 방법과 다르게 외부에서 변경이 불가능하다는 단점이 있어서 사용하는 것이 안좋다고 한다.

```java
// 이와 같은 방식이 불가능하다.
OrderService orderService = new OrderServiceImpl(memberRepository(), discountPolicy());
```

그래서 DI 프레임 워크가 없다면 아무것도 할 수 없기때문에
그냥 java 코드를 사용해서는 해당 로직을 테스트할 수 있는 방식이 없다.

특별한 용도 아니면 사용을 안하고 spring 자체에서도 지향하지않는 방식

테스트에 경우는 필드주입을 사용하는경우가 더러있다.

### 일반 메서드 주입

```java
@Component
public class OrderServiceImpl implements OrderService {
  private MemberRepository memberRepository;
  private DiscountPolicy discountPolicy;
  
	@Autowired
	public void init(MemberRepository memberRepository, DiscountPolicy discountPolicy) {
		this.memberRepository = memberRepository; this.discountPolicy = discountPolicy;
	} 
}
```

위와 같이 다양한 방식 말고도 일반 메서드에 `@Autowired` 를 등록해서 의존관계를 주입하는 방식이 가능하다고한다.

흔히 사용하는 방식이 아니기때문에 숙지만 하고있으면 될거 같은 방식

### 추가 정보

당연하게도 스프링 컨테이너가 관리하는 스프링 빈이어야만 의존관계 자동주입이 가능하다.

## 옵션 처리

코드를 작성하다보면 스프링 빈이 없다면 Null을 넣는다던가, Null임을 확인해서 default 값을 넣어준다던가 등의 동작을 해야하는 경우가 발생이 된다. 그럴경우에 해결하는 여러가지 방법을 제안한다.

- @Autowired(required=false)
- org.springframework.lang.@Nullable
- Optional<>

아래에서 예시코드에서 보여주고있는 `Member`는 스프링 빈이 아니다라는 전제를 가지고 설명을 진행한다.

### `@Autowired(required=false)`

자동 주입할 대상이 없으면 수정자 메서드 자체를 호출 안하게 설정하는 방식

```java
@Autowired(required = false)
public void setNoBean1(Member noBean1) {
    System.out.println("noBean1 = " + noBean1);
}
```

그냥 `@Autowired` 를 사용할 경우에는 `required`를 `true`로 가지고 있는데 이 값을 `false`로 지정을 해줄 수 있다.

위에 함수에 경우에는 코드 내에 실제로 빈을 주입하는 로직이 존재하지않기때문에 해당 로직은 호출되지않는다.

내부에서 빈이 존재하든 안하든 의존성을 주입하는 로직이 존재만 한다면 해당 로직은 돈다.

### `org.springframework.lang.@Nullable`

자동 주입할 대상이 없으면 `null`이 입력이 된다.

```java
@Autowired
public void setNoBean2(@Nullable Member noBean2) {
    System.out.println("noBean2 = " + noBean2);
}
```

원래 `@Autowired` 만 있는 경우에는 의존성주입을 시도할때 주입할 수 있는 빈이 존재하지않는다면 예외처리가 되어 에러가 발생이되는데 `@Nullable` 옵션을 지정한 메서드가 주입할 수 있는 빈이 존재하지않는다고 하더라도 에러를 발생시키지않고, `Null` 을 담는것으로 대신하여 동작하게 만들어준다.

### `Optional<>`

자동으로 주입할 대상이 없으면 `Optional.empty` 가 입력된다.

```java
@Autowired
public void setNoBean3(Optional<Member> noBean3) {
    System.out.println("noBean3 = " + noBean3);
}
```

`@Nullable` 와 비슷한데 `Null`을 넣는것 대신 `Optional` 을 사용했기때문에 대신 `empty` 가 된다고 생각하면 된다.

## 생성자 주입을 선택해라!

해당 파트에서는 왜 생성자 주입을 사용하라고 하는지 그것에 대한 근거를 이야기해 보겠습니다.

- 불변
- 누락
- final 키워드

### 불변

대부분에 경우에는 의존관계에 대한 주입이 일어났다면 해당 애플리케이션의 종료시점까지 의존관계를 변경하는 일이 없다.

그래서 생성자 주입 방식이 해당 조건에 가장 최적인 방식이고, 위에서 이야기했던 수정자 주입의 방식을 사용하면 주입을 할 대상을 public으로 열어두어야하기때문에 누군가 실수로 변경을 할 가능성이 존재하기도 하고, 변경 하면 안되는 메서드를 열어두어야하기때문에 좋은 설계 방식이라고 생각할 수 없다.

### 누락

생성자 주입말고 수정자 주입에 대해서 간단하게 이야기를 해보자.

```java
@Test
	void createOrder() {
		OrderServiceImpl orderService = new OrderServiceImpl();
		orderService.createOrder(1L, "itemA", 10000); 
}
```

위와 같이 어떠한 테스트 코드를 작성한다고 할때 수정자 주입에 경우에는 `new OrderServiceImpl()` 방식으로 생성을 한 이후에 추가적으로 `setter` 를 통해 의존관계를 주입해야하다보니 코드를 작성함에 있어서 누락이 쉽게 발생될 수 있다.

하지만 위와 같은 상황에서 수정자 주입말고 생성자 주입을 사용한다고한다면 컴파일 단계에서 바로 `new OrderServiceImpl();` 가 충분한 인자를 받지 않았다고 컴파일 오류를 발생시키기 때문에 오류가 발생될 가능성이 확연하게 줄어들 수 있다.

### final 키워드

`final` 키워드에 경우에는 해당 로직이 돌아간 이후에는 추가적으로 값을 변경하거나 넣을 수 없도록 제한을 두게 설정을 해줄 수 있다.

그래서 해당 로직이후에 수정이 불가능하다는 점에서 불변이라는 것과 상당히 조합이 좋은데 생성자 주입 방식을 사용하면 

```java
private final MemberRepository memberRepository;
private final DiscountPolicy discountPolicy;

public OrderServiceImpl(MemberRepository memberRepository, DiscountPolicy discountPolicy) {
    this.memberRepository = memberRepository;
    this.discountPolicy = discountPolicy;
}
```

위와 같이 의존관계를 주입받을 멤버 변수에 `final` 키워드를 넣어줄 수 있게 된다.

```java
@Autowired
public OrderServiceImpl(MemberRepository memberRepository, DiscountPolicy discountPolicy) {
    this.memberRepository = memberRepository;
//    this.discountPolicy = discountPolicy;
}
```

그리고 위와 같이 `this.discountPolicy = discountPolicy;` 해당 코드가 누락이 되었다고 가정을 해보고 코드를 돌려보게되면 `private final DiscountPolicy discountPolicy;` 해당 부분에서 값을 설정을 했어야했는데 설정이 되지않았다. 누락이 되었다. 라고 오류가 발생된다고한다. 
그래서 빠르게 오류에 대해 캐치가 가능하다.

### 정리

프레임 워크에 의존하지않고, 순수한 자바 언어의 특징을 잘 살리는 코드 방식이 좋은 방식으로 이야기가 되는데 생성자 주입에 경우에는 순수한 자바언어의 특성을 잘 살릴 수 있는 방식이기때문에 좋은 방식으로 이야기가 된다.

기본으로 생성자 주입을 사용을 하고, 필수 값이 아닌경우에만 수정자 주입방식을 사용하는 형식으로
생성자 주입과 수정자 주입을 동시에 사용할 수 있는 방식이 가능하다고한다.

그래서 결론으로는 **항상 생성자 주입을 최우선적으로 생각하여 사용을 하고, 가끔 추가적인 옵션이 필요하면 수정자 주입을 사용하는 방식의 코드를 구성하는 것이 좋다.**

## 롬복과 최신 트랜드

### 롬복이란?

자바에서 사용하는 라이브러리중에 하나로 getter, setter, 생성자 등등 다양한 로직들을 생략하여 작성할 수 있도록 지원을 해준다.
지금은 사람들이 하두 많이 사용을 하다보니 인텔리제이 2020.3 이후 버전에는 자동으로 lombok이 탑제가 되어있다고 한다.

```java
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class HelloLombok {
    private String name;
    private int age;

    public static void main(String[] args) {
        HelloLombok helloLombok = new HelloLombok();
        helloLombok.setName("test");

//        String name = helloLombok.getName();
//        System.out.println("name = " + name);
        System.out.println("helloLombok = " + helloLombok);
    }
}
```

예시코드는 위와 같다. 기존에는 `name`, `age` 인 멤버변수에 접근을 할려면 `getter`, `setter`등의 함수를 사용해서 접근을 했어야했는데. `lombok`을 사용하게 되면 `@Getter`, `@Setter` 애노테이션만 선언을 해주면 알아서 `getter`, `setter` 메서드들을 사용할 수 있게 지원해준다고한다.

```java
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final MemberRepository memberRepository;
    private final DiscountPolicy discountPolicy;
    ...
}
```

추가적으로 위와 같이 `@RequiredArgsConstructor` 을 사용하게 되면 생성자를 자동으로 생성해줘서 따로 선언을 안해도 되게 해주고, `final` 키워드가 붙은 값들을 자동으로 생성자에 등록해준다고한다.
( 생성자에서 추가적으로 값을 받고싶다면 `@RequiredArgsConstructor` 사용하는 방식말고 따로 생성자 선언필요 )

## 조회 빈이 2개 이상 - 문제

“위에 코드에 경우 `memberRepository`는 인터페이스이고 해당 인터페이스를 상속받은 클래스가 2개가 있을때 그 두 클래스 모두 `@Component`에 등록이 되었다고 해보자. 그럴경우에는 어떤것을 바탕으로 `OrderServiceImpl` 가 동작을 하게 되는가?” 

기존에 위에 개념에 대해서 의문점이 들어서 혼자서 찾아봤었는데 해당 내용의 대한 강의가 따로 준비가 되어있었다..

`DiscountPolicy` 의 하위 타입인 `FixDiscountPolicy` , `RateDiscountPolicy` 이 모두 스프링 빈으로 선언이 되어있을 때 `DiscountPolicy` 을 기준으로 의존 관계를 주입해보면
`NoUniqueBeanDefinitionException` 오류가 발생된다고 한다.

```java
'hello.core.discount.DiscountPolicy' available: expected single matching bean
but found 2: fixDiscountPolicy,rateDiscountPolicy
```

이럴 경우 하위타입으로 직접 지정을 할 수도 있지만 하위타입으로 지정하는 것은 DIP 원칙(의존관계 역전)을 위배하는 행위이기때문에 유연성이 떨어지게된다. 그리고  완전히 똑같은 타입의 스프링 빈이 2개 있을 때 해결이 안된다. 이를 어떻게 해결할 수 있는가?

## @Autowired 필드 명, @Qualifier, @Primary

위에서 이야기한 조회 대상 빈이 2개 이상일 때 해결 방법에는 3가지 정도가 있다고 한다.

1. @Autowired 필드 명 매칭
2. @Qualifier @Qualifier끼리 매칭 빈 이름 매칭
3. @Primary 사용

### @Autowired 필드 명 매칭

`@Autowired`자체가 지원해주는 방식중에 하나가 의존관계를 주입받을 필드명이 빈 이름과 동일하다면 해당 bean에 해당되는 bean을 가져온다고 한다.

```java
private final DiscountPolicy discountPolicy;

private final DiscountPolicy rateDiscountPolicy;
```

위와 같이 bean을 가져올때 bean에 이름과 일치하게 `rateDiscountPolicy` 지정을 하면 그것에 해당하는 bean을 가져온다고한다. 

**@Autowired 매칭 정리**

1. 타입매칭
2. 타입매칭의결과가2개이상일때필드명,파라미터명으로빈이름매칭

### @Qualifier @Qualifier끼리 매칭 빈 이름 매칭

```java
@Component 
@Qualifier("mainDiscountPolicy")
public class RateDiscountPolicy implements DiscountPolicy {}
```

```java
public OrderServiceImpl(MemberRepository memberRepository,
@Qualifier("mainDiscountPolicy") DiscountPolicy
discountPolicy) {
	...
}
```

위와 같이 원하는 bean에 `Qualifier`을 통해 이름을 지정해주고 그 bean을 사용할 메서드에서 
`@Qualifier("mainDiscountPolicy") DiscountPolicy discountPolicy` 이와 같이 지정을 해주면 그 이름에 맞는 bean을 사용한다고 한다.

`@Qualifier`에 맞는 이름을 찾지 못한다면 인자로 넣은 `“mainDiscountPolicy"` 을 찾는다고 하는데 그건 의도해서 사용하기에는 이해하기 어렵기때문에 `@Qualifier` 가 `@Qualifier` 를 찾는 용도로 사용하는 것이 좋다.

**@Qualifier 정리**

1. @Qualifier끼리 매칭
2. 빈이름매칭
3. `NoSuchBeanDefinitionException` 예외 발생

### @Primary 사용

```java
@Component
@Primary
public class RateDiscountPolicy implements DiscountPolicy {
	...
}

@Component
public class FixDiscountPolicy implements DiscountPolicy {
	...
}
```

```java
public OrderServiceImpl(MemberRepository memberRepository, DiscountPolicy discountPolicy) { 
	this.memberRepository = memberRepository;
	this.discountPolicy = discountPolicy; 
}
```

위와 같이 bean으로 등록이 될 class에게 `@Primary` 라는 애노테이션을 넣어서 우선 순위를 지정해줄 수 있다.

그래서 보통은 메인으로 사용할 class에 `@Primary` 를 지정해서 사용하고, 서브로 지정해서 사용할 것에 `@Qualifier` 을 사용해서 명시적으로 회득하는 방식을 사용.

### 우선순위

`@Primary` 와 `@Qualifier` 가 동시에 지정이 되어있다면 우선권은 `@Qualifier` 가 가져가게 된다.

**“스프링은 자동보다는 수동이, 넒은 범위보다는 좁은 범위가 우선순위가 높다.”**

## 애노테이션 직접 만들기

`@Qualifier("mainDiscountPolicy")` 이와 같이 `Qualifier` 순정으로 사용을 하면 문자에 대한 컴파일 타입 체크가 안되어 의도하지않은 동작이 이루어질 수 있게 된다.

```java
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE, ElementType.ANNOTATION_TYPE}) @Retention(RetentionPolicy.RUNTIME)
@Documented
@Qualifier("mainDiscountPolicy") 
public @interface MainDiscountPolicy {
}
// 양식은 Qualifier 형태 그대로 사용
```

```java
@Component
@MainDiscountPolicy
public class RateDiscountPolicy implements DiscountPolicy {}
```

그런데 위와 같이 직접 애노테이션을 만들어서 `@MainDiscountPolicy` 이러한 형식으로 지정을 해주면 오류에 대한 처리를 쉽게 해줄 수 있고, 실제 `Qualifier` 와 똑같이 동작이 이루어지기때문에 위와 같은 방식도 사용한다고 한다.

하지만 무분별한 `Qualifier` 는 유지보수 측면에서 혼란을 줄 수 있다.

## 조회한 빈이 모두 필요할 때, List, Map

실제로 해당 타입의 스프링 빈을 모두 사용하는 간단한 실무 예제를 기준으로 설명을 들었었다.

어느 할인 서비스에서 클라이언트가 할인의 종류 (rate, fix)를 선택해서 할인을 적용시킬 수 있는 로직을 구성한다고한다.

```java
public class AllBeanTest {

    @Test
    void findAllBean() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(AutoAppConfig.class, DiscountService.class);

        DiscountService discountService = ac.getBean(DiscountService.class);
        Member member = new Member(1L, "userA", Grade.VIP);
        int discountPrice = discountService.discount(member, 10000, "fixDiscountPolicy");

        assertThat(discountService).isInstanceOf(DiscountService.class);
        assertThat(discountPrice).isEqualTo(1000);

        int rateDiscountPolicy = discountService.discount(member, 20000, "rateDiscountPolicy");
        assertThat(rateDiscountPolicy).isEqualTo(2000);
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
            System.out.println("discountCode = " + discountCode);
            DiscountPolicy discountPolicy = policyMap.get(discountCode);
            return discountPolicy.discount(member, price);
        }
    }
}
```

그럴경우 위와 같이 코드를 구성할 수 있다. 간단하게 분석을 해보자면

```java
private final Map<String, DiscountPolicy> policyMap;
private final List<DiscountPolicy> policies;

public DiscountService(Map<String, DiscountPolicy> policyMap, List<DiscountPolicy> policies) {
    this.policyMap = policyMap;
    this.policies = policies;
    System.out.println("policyMap = " + policyMap);
    System.out.println("policies = " + policies);
}

ApplicationContext ac = new AnnotationConfigApplicationContext(AutoAppConfig.class, DiscountService.class);
```

위와 같이 생성자를 통해서 모든 `DiscountPolicy`를 `map` 혹은 `List` 형태로 저장을 진행한다.

( 지금의 나는 `fixDiscountPolicy`, `rateDiscountPolicy` 모두 `@Component`로 등록을 해둔 상태라 모두 `Map`이나 `List`에 저장이 된다. )

```java
public int discount(Member member, int price, String discountCode) {
    System.out.println("discountCode = " + discountCode);
    DiscountPolicy discountPolicy = policyMap.get(discountCode);
    return discountPolicy.discount(member, price);
}
```

`DiscountService` 에 `discount` 라는 함수를 선언하여 입력으로 받은 `discountCode` 을 바탕으로 `Map`에서 `discountCode` 를 가져오고, `discountCode`에 해당하는 `discount` 메서드를 실행시킨다.

( `discountCode`로 들어올 값들은  `DiscountPolicy` 라는 `interface` 를 통해서 구현이 되어있기때문에 모두 `discount` 라는 메서드를 가지고있다. )

```java
discountService.discount(member, 10000, "fixDiscountPolicy");
```

위와 같이 `discountService` 에 `discount`를 실행시켜서 3번째 인자인 `discountCode` 에 맞는 메서드를 실행시킨다.

위와 같이 List 혹은 Map을 사용해서 모든 bean들을 대상으로 원하는 로직을 돌리는것이 가능하다.

로직을 추가한다면 `DiscountPolicy discountPolicy = policyMap.get(discountCode);` 에서 값의 유효성검사를 추가적으로 더 해주면 좋을 거 같다.

## 자동, 수동의 올바른 실무 운영 기준

1. 편리한 자동 기능을 기본으로 사용하자
2. 직접 등록하는 기술 지원 객체는 수동 등록
3. 다형성을 적극 활용하는 비즈니스 로직은 수동 등록을 고민해보자

기술지원 객체

애플리케이션은 크게 업무 로직과 기술 지원 로직으로 나눌 수 있다.

- **업무 로직 빈:** 웹을 지원하는 컨트롤러, 핵심 비즈니스 로직이 있는 서비스, 데이터 계층의 로직을 처리하는 리포지토리등이 모두 업무 로직이다. 보통 비즈니스 요구사항을 개발할 때 추가되거나 변경된다.
- **기술 지원 빈:** 기술적인 문제나 공통 관심사(AOP)를 처리할 때 주로 사용된다. 
데이터베이스 연결이나, 공통 로그처리 처럼 업무 로직을 지원하기 위한 하부 기술이나 공통 기술들이다.

### **업무 로직 빈** : 자동 빈 등록

### **기술 지원 빈** : 수동 빈 등록

`TODO: 업무 로직 빈, 기술 지원 빈 등에 대해서 확실한 개념정리가 되지않아서 추가적인 공부 및 정리 필요.`