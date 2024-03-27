### 학습 내용

- 의존관계 주입 방법 → 생성자 방식 권장
- 롬복 라이브러리 소개
- 같은 타입인 빈이 여러 개 → 매칭 방법?
- 자동, 수동 운영 방법

---

# 의존 관계 주입 방법

의존관계 주입 방법에는 4가지가 있다.

1. 생성자 주입 방법: 필수, 불변 의존 관계에 사용
    - 딱 1번만 호출되는 것이 보장된다.
2. 수정자 주입 방법: 선택, 변경 가능성 있는 의존 관계에 사용
3. 필드 주입 방법: 코드가 간결해진다.
    - 외부 변경 어려움 & DI 프레임워크가 없으면 아무 것도 할 수 없기 때문에 테스트가 어렵다.
    - @Configuration 이나 @Test 같은 곳에서만 특별한 용도로 사용
4. 일반 메서드 주입: 보통 사용 X

<aside>
💡 순수한 자바 테스트 코드에서는 @Autowired가 동작하지 않는다.
@SpringBootTest는 스프링 컨테이너를 테스트에 통합했기 때문에 사용 가능하다.

</aside>

## 주입할 스프링 빈이 없을 때… (옵션 처리)

1. @Autowired(required=false): 수정자 호출 X
2. @Nullable: null 입력
3. Optional<>: Optional.empty 입력

⇒ Nullable과 Optional은 다른 주입 방식에도 사용 가능하다

## 생성자 주입을 해야하는 이유

### 불변

- 대부분 의존관계는 앱 종료시점까지 변경되어선 안된다.
- 수정자 주입의 경우 public으로 설정해야 하고, 이는 다른 누군가에 의해 변경될 수 있음을 의미한다.
변경하면 안되는 것을 메서드로 열어두는 것은 좋은 방법이 아니다.
- 생성자는 객체 생성 시점에 딱 1번만 호출되므로 불변을 보장할 수 있다.

### 누락

- 순수한 자바 코드로 단위 테스트 하는 경우, 의존관계 없이 실행해도 컴파일 에러가 발생하지 않는다. 
런타임에 NPE 발생

### final 키워드

- 생성자에서 값이 설정되지 않는 오류를 막아준다.
- 오직 생성자 주입 방식만 final 키워드 사용할 수 있다.

# 롬복 라이브러리

롬복 라이브러리를 통해 조금 더 편리하게 개발할 수 있다.

특정 어노테이션을 붙이면, 해당 코드를 컴파일 타임에 만들어준다

## 설정법

```groovy
...
//lombok 설정 추가 시작
configurations {
 compileOnly {
 extendsFrom annotationProcessor
 }
}
//lombok 설정 추가 끝
...
dependencies {
 ...
 //lombok 라이브러리 추가 시작
 compileOnly 'org.projectlombok:lombok'
 annotationProcessor 'org.projectlombok:lombok'
 testCompileOnly 'org.projectlombok:lombok'
 testAnnotationProcessor 'org.projectlombok:lombok'
 //lombok 라이브러리 추가 끝
 ...
 }
}
...
```

### @Getter, @Setter

필드 getter, setter 생성해준다.

### @RequiredArgsConstructor

final 필드를 인자로 갖는 생성자 생성

---

# 같은 타입의 빈이 여러 개일 때?

@Autowired는 빈의 타입을 기준으로 주입을 한다. 만약 스프링 빈에 같은 타입의 빈이 여러 개 있다면 `NoUniqueBeanDefinitionException` 오류가 발생한다.

의존관계 설정 시, 하위 타입으로 지정하는 방법도 있지만 이는 인터페이스가 아닌 구현체에 의존하게 되므로 DIP 를 위반하게 된다.

## 해결 방법

### @Autowired 필드명 매칭

타입으로 매칭하고, 같은 타입이 2개 이상이라면 필드명(혹은 파라미터 이름)과 같은 이름을 가진 스프링 빈을 주입한다

```groovy
public OrderService(DiscountPolicy fixedDiscountPolicy, DiscountPolicy rateDiscountPolicy){}
```

### @Qualifier

추가 구분자를 붙여주는 방법이다.

```java
@Component
@Qualifier("mainDiscountPolicy")
public class RateDiscountPolicy implements DiscountPolicy {}
```

```java
@Component
@Qualifier("fixDiscountPolicy")
public class FixDiscountPolicy implements DiscountPolicy {}
```

주입 시 주입할 빈의 구분자를 적어준다

```java
@Autowired
public OrderServiceImpl(MemberRepository memberRepository,
 @Qualifier("mainDiscountPolicy") DiscountPolicy discountPolicy) {
 this.memberRepository = memberRepository;
 this.discountPolicy = discountPolicy;
}
```

모든 코드에 적어주어야 한다는 단점이 있다.

### @Primary

해당 빈을 우선적으로 주입한다.

```java
@Component
@Primary
public class RateDiscountPolicy implements DiscountPolicy {}
```

```java
@Component
public class FixDiscountPolicy implements DiscountPolicy {}
```

Qualifier를 붙일 필요가 없다.

### Qualifier vs Primary

주로 사용하는 빈과, 가끔 사용하는 빈이 있다고 가정해보자

주로 사용하는 빈은 @Primary로 지정해 조회하는 곳에서 @Qualifier 없이 편하게 조회하고, 가끔 사용하는 빈을 사용할 때는 @Qualifier를 지정해 명시적으로 획득하는 방식으로 구현하면 된다

단, 우선순위는 @Qualifier가 높다.

## Qualifier 런타임 문제 해결

Qualifier 인자가 문자열이므로 컴파일 타임에 에러를 잡을 수 없다. 이를 해결하기 위해 어노테이션을 생성해 사용하는 방법이 있다.

```java
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER,
ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Qualifier("mainDiscountPolicy")
public @interface MainDiscountPolicy {
}
```

@MainDiscountPolicy를 @Qualifier("mainDiscountPolicy") 대신 사용하면 된다.

## 조회한 빈이 모두 필요할 때

할인 서비스를 제공하는데, 클라이언트가 할인의 종류를 선택할 수 있는 상황에서는 모든 종류의 할인 정책을 서비스가 가지고 있어야 한다.

### 사용 예제

```java
static class DiscountService {
 private final Map<String, DiscountPolicy> policyMap;
 private final List<DiscountPolicy> policies;
 public DiscountService(Map<String, DiscountPolicy> policyMap,
List<DiscountPolicy> policies) {
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
```

---

의존관계 주입 시 타입을 List나 Map으로 지정하면 해당 타입의 빈을 모두 받을 수 있다.

# 자동, 수동 운영 기준

설정 정보를 유지하는 것이 번거롭고, 자동만으롤도 OCP, DIP를 지킬 수 있으므로 자동을 선호하는 추세이지만, 아직 수동으로 주입해야 하는 경우들이 있다.

## 어플리케이션의 두 로직

- 업무 로직 빈: MVC의 C, 핵심 비즈니스 로직 존재하는 서비스, 데이터 계층의 로직을 처리하는 리포지토리 모두 해당한다.
    - 판단기준: 비즈니스 요구사항을 개발할 때, 추가 혹은 변경된다.
- 기술 지원 빈: 데이터베이스 연결, 공통 로그 처리 등 업무 로직 지원하기 위한 하부 기술 또는 공통 기술
    - 판단기준: 기술적인 문제, 공통 관심사(AOP) 처리 시 주로 사용

## 기술 지원 빈은 수동으로 관리하는 것이 좋다!!!