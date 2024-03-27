# 섹션 6. 컴포넌트 스캔

## 컴포넌트 스캔과 의존관계 자동 주입 시작하기

### 컴포넌트 스캔이란?

컨테이너가 자동으로 애플리케이션의 클래스 경로를 스캔하고, 특정 패키지나 애노테이션에 기반하여 `bean`으로 등록하는 프로세스를 이야기한다.

```java
@Configuration
@ComponentScan(
        excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Configuration.class)
)
public class AutoAppConfig {
}
```

그래서 위와 같이 `@ComponetScan` 이라는 애노테이션을 명시해주고 `AppConfig`를 생성해주게되면 해당 `@ComponetScan`은 `@Component`이라는 키워드가 붙은 애노테이션을 자동으로 찾아서 `bean`으로 등록해준다고한다.

`excludeFilters`에 경우에는 어떠한 것들을 제외하고 `@ComponetScan` 을 사용할건지 지정해줄 수 있다.

```java
@Configuration
public class AppConfig {

    @Bean
    public MemberService memberService() {
        return new MemberServiceImpl(memberRepository());
    }

    @Bean
    public MemoryMemberRepository memberRepository() {
        return new MemoryMemberRepository();
    }

    @Bean
    public OrderService orderService() {
        return new OrderServiceImpl(memberRepository(), discountPolicy());
    }

    @Bean
    public DiscountPolicy discountPolicy() {
        return new RateDiscountPolicy();
    }
}
```

그러면 의문점이 하나 더 생기게 되는데 기존에는 수정으로 `Bean`을 등록해주고 의존관계를 주입해주는 코드를 사용하여 서로간의 의존관계가 주입이 되었었는데. 위와 같이 `@ComponetScan`을 사용하는 방식으로는 “어떻게 의존관계를 주입해줄 수 있는가?” 에 대한 의문점이 들게된다.

```java
@Autowired
public OrderServiceImpl(MemberRepository memberRepository, DiscountPolicy discountPolicy) {
    this.memberRepository = memberRepository;
    this.discountPolicy = discountPolicy;
}
```

위와 같이 의존관계를 설정하는 생성자에다가 `@Autowired`라는 애노테이션을 넣어주게 되면 생성자에서 여러 의존 관계도 한번에 주입받을 수 있다고한다.

스프링 컨테이너가 해당하는 스프링 `Bean`을 찾는 방식은
스프링 컨테이너가 자동으로 타입이 같은 `Bean`을 찾아서 주입하는 방식이라고한다.

### 의문점

위에 코드에 경우 `memberRepository`는 인터페이스이고 해당 인터페이스를 상속받은 클래스가 2개가 있을때
그 두 클래스 모두 `@Component`에 등록이 되었다고 해보자. 그럴경우에는 어떤것을 바탕으로 `OrderServiceImpl` 가 동작을 하게 되는가라는 의문점이 들어 해당 내용에 대해서 찾아봤는데.

다른 추가적인 설정을 하지않았다면 스프링에 동작순서에 따라 먼저 등록된 빈을 선택으로 동작하게 되고,

```java
@Component
@Primary
public class ARepository implements MemberRepository {
    ...
}
```

위와 같이 `@Primary` 라는 애노테이션을 추가하여 `bean`에 우선순위를 줄 수 있다고 한다.
( 2개의 빈 모두 `@Primary`를 가지고 있는 경우에는 의존성 주입에 실패하여 에러가 난다고 한다. )

## 탐색 위치와 기본 스캔 대상

```java
@ComponentScan(
        basePackages = "hello.core.member"
        ...
)
```

위와 같이 `basePackages`를 지정해주게 되면 컴포넌트 스캔의 범위를 지정해줄 수 있다.
( 즉 위에 코드는 `"hello.core.member"` 디렉토리 안에 있는 `@Component` 만 등록을 하겠다는 의미 )

`basePackages = {"hello.core", "hello.service"}` 이와 같이 사용도 가능

### 권장하는 방법

`basePackages` 를 따로 지정하지않고 `AppConfig` 파일의 위치를 지금 진행하고 있는 나의 프로젝트 최상단에 두는 방식을 권장한다고한다.

```java

`com.hello` // 최상단
`com.hello.serivce` 
`com.hello.repository`
```

나의 파일구조가 위와같이 있을때 
**프로젝트의 최상단은** `com.hello`이므로 해당 위치에 `AppConfig` 라는 생성한다. 그러면 하위는 모두 자동으로 컴포넌트 스캔의 대상이 된다.

**위와 같은 방법을 권장.**

```java
package hello.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoreApplication.class, args);
	}

}
```

이러한 내용을 가진 파일이 있고, `@SpringBootApplication,` 안에 `@ComponentScan` 이 있으므로 `@ComponentScan`을 따로 사용할 필요가 없다고 하던데. 왜 그런건지..?

`@ComponentScan` 에 경우 `@Component` 를 상속하고 있는 애노테이션들도 스캔대상에 포함시킨다고 한다.

- `@Component` : 컴포넌트 스캔에서 사용
- `@Controller` : 스프링 MVC 컨트롤러에서 사용
- `@Service` : 스프링 비즈니스 로직에서 사용
- `@Repository` : 스프링 데이터 접근 계층에서 사용
- `@Configuration` : 스프링 설정 정보에서 사용
- `@Controller` : 스프링 MVC 컨트롤러로 인식
- `@Repository` : 스프링 데이터 접근 계층으로 인식하고, 데이터 계층의 예외를 스프링 예외로 변환해준다.
- `@Configuration` : 앞서 보았듯이 스프링 설정 정보로 인식하고, 스프링 빈이 싱글톤을 유지하도록 추가 처리를 한다.
- `@Service` : 사실 `@Service` 는 특별한 처리를 하지 않는다. 대신 개발자들이 핵심 비즈니스 로직이 여기에 있겠구나 라고 비즈니스 계층을 인식하는데 도움이 된다.

그래서 위와 같은 것들도 스캔 대상에 포함이된다. ( 애노테이션끼리 상속관계라는 것은 존재하지않기때문에 자바언어가 지원하는 기능은 아니고 스프링에서 지원하는 기능이라고한다. )

## 필터

### 필터란?

컴포넌트 스캔 대상을 추가로 지정하거나 컴포넌트 스캔에서 제외할 대상을 지정하는 것을 이야기한다.

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyExcludeComponent {
}

@MyIncludeComponent
public class BeanA {
	...
}
```

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyIncludeComponent {
}

@MyExcludeComponent
public class BeanB {
	...
}
```

```java
@Configuration
@ComponentScan(
        includeFilters = @Filter(type = FilterType.ANNOTATION, classes = MyIncludeComponent.class),
        excludeFilters = @Filter(type = FilterType.ANNOTATION, classes = MyExcludeComponent.class)
)
static class ComponentFilterAppConfig {
	...
}
```

간단한 사용 예제는 위와 같다. `ComponentFilterAppConfig` 에서 `includeFilters` 와 `excludeFilters` 를 사용해서 애노테이션에 따라서 등록을 진행할 애노테이션과 등록에서 제외할 애노테이션을 지정해줄 수 있다.

```java
@Test
void filterScan() {
    ApplicationContext ac = new AnnotationConfigApplicationContext(ComponentFilterAppConfig.class);
    BeanA beanA = ac.getBean("beanA", BeanA.class);
    assertThat(beanA).isNotNull();

    org.junit.jupiter.api.Assertions.assertThrows(
            NoSuchBeanDefinitionException.class,
            () -> ac.getBean("beanB", BeanB.class)
    );
}
```

그래서 실제 테스트 코드는 위와 같이 작성하여 확인 할 수 있는데 

`beanA` 에 경우에는 `@MyIncludeComponent` 애노테이션을 바탕으로 선언이 된 `class`이므로 정상적으로 Config에서 인식을 하여 찾을 수 있었고,

`beanB` 에 경우에는 `@MyExcludeComponent` 애노테이션을 바탕으로 선언이 된 `class` 이므로 등록이 되지않아 비어있는 것을 확인할 수 있었다.

실제로는 `@Component` 면 웬만한 문제들은 다 해결이 되기때문에 `includeFilters` 까지 사용하는 일은 거의 없다고 한다.

`excludeFilters` 만 가끔가다 사용하는 편.

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
```

- `@Target(ElementType.TYPE)`
클래스 레벨에 애노테이션이 적용될 수 있음을 의미
다양한 옵션들이 존재하지만 `TYPE` 에 경우에는 클래스, 인터페이스, Enum 등의 타입에 적용할 수 있음을 의미한다고 한다.
- `@Retention(RetentionPolicy.RUNTIME)`
런타임 시에도 해당 애노테이션이 유지되는 설정
- `@Documented`
해당 요소에 대한 문서가 자동으로 생성되고 API 문서에 포함된다고한다.

## 중복 등록과 충돌

컴포넌트 스캔에서 같은 빈 이름을 등록한 경우를 의미한다.

크게

1. 자동 빈 등록 vs 자동 빈 등록
2. 수동 빈 등록 vs 자동 빈 등록

으로 2가지 상황이 있을 수 있다고 한다.

### 자동 빈 등록 vs 자동 빈 등록

`@ComponentScan` 에 의해서 자동으로 `spring bean`이 등록이 될때 그 이름이 같은 경우에는 
`ConflictingBeanDefinitionException` 이라는 예외가 발생된다고한다. 그래서 빌드자체가 안된다.

### 수동 빈 등록 vs 자동 빈 등록

**수동 빈 등록**

```java
@Bean(name = "memoryMemberRepository")
public MemberRepository memberRepository() {
   return new MemoryMemberRepository();
}
```

**자동 빈 등록**

```java
@Component
public class MemoryMemberRepository implements MemberRepository {
	...
}
```

**수동 bean이 자동 bean 보다 우선순위가 높아서 자동 bean을 오버라이딩 하는 형식으로 동작을 한다고한다.**

최근 스프링 부트에서는 수동 빈 등록과 자동 빈 등록이 충돌나면 오류가 발생하도록 기본 값을 바꾸었다.
( 의도한 동작이 아닌경우 경우가 많고, 애매한 우선순위를 기준으로 동작하기때문에 그러한 결정 )