### 학습 내용

- 컴포넌트 스캔 & 자동 주입 사용
- 탐색 위치 & 대상 지정
- 필터 사용
- 중복 등록과 충돌

---

# 컴포넌트 스캔 & 자동 주입 사용

## 배경

등록해야하는 스프링 빈이 수십, 수백 개가 되면, 설정 정보도 커지고, 누락하는 문제가 발생할 수 있다.

⇒ 자동으로 등록해주는 컴포넌트 스캔 기능 제공

⇒ 의존 관계 자동 주입 기능 제공

## 컴포넌트 스캔

@Component 어노테이션이 붙은 클래스를 스캔해 스프링 빈으로 등록한다

```java
@Configuration
@ComponentScan
public class AutoAppConfig {
}
```

- @ComponentScan를 부착하여 사용할 수 있다.
- @Configuration 안에도 @Component가 부착되어 있다
    
    → AutoAppConfig도 등록된다!
    

## 자동 주입

기존에는 @Bean으로 직접 설정 정보 및 의존관계 명시했지만, 컴포넌트 스캔에는 존재하지 않는다.

→ 의존관계는 @Autowired로 자동으로 주입된다.

## 동작 과정

1. 컴포넌트 스캔
    - @ComponetScan은 @Component가 붙은 모든 클래스를 스프링 빈으로 등록한다.
    - 스프링빈 명:
        - 기본 값: 클래스명 + 맨 앞글자 소문자 (class Bean → bean)
        - 직접 지정: @Component(”빈 이름”)
2. 의존관계 자동 주입
    - 생성자에 @Autowired 지정 → 스프링 컨테이너가 자동으로 모든 의존관계 찾아 주입한다.
    - 조회 전략:
        - 기본: 타입이 같은 빈을 찾아서 주입한다.
        - `@Autowired == getBean(클래스명.class)`

# 탐색 위치 & 대상 지정

외부 라이브러리에 대해서도 컴포넌트 스캔을 진행할 수 있다. 즉, 탐색위치를 너무 광범위하게 잡으면 스캔에 굉장히 오랜 시간이 걸릴 수 있으므로, 잘 지정하는 것이 중요하다.

## 기본값

설정파일이 존재하는 패키지와 그 하위 패키지를 탐색한다.

## 변경 하는 법

```java
@Configuration
@ComponentScan(
        excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Configuration.class),
        basePackages = "hello.core.member",
        basePackageClasses = AutoAppConfig.class
)
public class AutoAppConfig {
}
```

## 이상적 - 스프링부트

프로젝트 가장 상단에 설정파일을 두어, 프로젝트를 스캔할 수 있도록 하는 것이 가장 이상적이다.

스프링부트는 그렇게 되도록 구현되어 있다.

→ 프로젝트 최상단에 존재하는 @SpringBootApplication안에 @ComponentScan 존재한다.

## 탐색 대상 및 부가 기능

- `Componet`: 컴포넌트 스캔에 사용
- `Controller` : 스프링 MVC 컨트롤러에서 사용
    - 스프링 MVC 컨트롤러로 인식
- `Service` : 스프링 비즈니스 로직에서 사용
    - 특별한 처리를 하지 않는다. 대신 개발자들이 핵심 비즈니스 로직이 여기에 있
    겠구나 라고 비즈니스 계층을 인식하는데 도움이 된다
- `Repository` : 스프링 데이터 접근 계층에서 사용
    - 스프링 데이터 접근 계층으로 인식하고, 데이터 계층의 예외를 스프링 예외로 변환해줌
- `Configuration` : 스프링 설정 정보에서 사용
    - 스프링 설정 정보로 인식하고, 스프링 빈이 싱글톤을 유지하도록 추가 처리
    를 한다

Component를 제외한 다른 어노테이션은 Component를 포함하고 있다.

<aside>
💡 애노테이션 상속 관계는 자바가 아닌, 스프링이 제공하는 기능이다.

</aside>

<aside>
💡 useDefaultFilters 옵션 → false 처리 시, 위에 명시된 기본 스캔 대상들이 스캔에서 제외된다.

</aside>

# 필터 사용

- includeFilters : 컴포넌트 스캔 대상을 추가로 지정한다.
- excludeFilters : 컴포넌트 스캔에서 제외할 대상을 지정한다.

## 예제

등록에 포함할 때, 포함하지 않을 때 붙일 어노테이션을 만들어보자

```java
package hello.core.scan.filter;

import java.lang.annotation.*;

@Target({ElementType.TYPE}) //TYPE -> 클래스 레벨에 붙는 것
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyIncludeComponent {
}
```

```java
package hello.core.scan.filter;

import java.lang.annotation.*;

@Target({ElementType.TYPE}) //TYPE -> 클래스 레벨에 붙는 것
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyExcludeComponent {
}
```

각 어노테이션을 빈 클래스 BeanA, BeanB에 붙인 후 테스트 진행하였다.

```java
public class ComponentFilterAppConfigTest {
    @Test
    void filterScan(){
        ApplicationContext ac = new AnnotationConfigApplicationContext(ComponetFilterAppConfig.class);
        BeanA beanA = ac.getBean("beanA", BeanA.class);
        assertThat(beanA).isNotNull();
    }

    @Configuration
    @ComponentScan(
            includeFilters = @Filter(type = FilterType.ANNOTATION, classes = MyIncludeComponent.class),
            excludeFilters = @Filter(type = FilterType.ANNOTATION, classes = MyExcludeComponent.class)
    )
    static class ComponetFilterAppConfig{
    }
}
```

- type = FilterType.ANNOTATION: 어노테이션에 대해 필터를 설정하겠다.
    - 기본값이다. 생략 가능
- ComponentScan 시, @MyIncludeComponent 는 등록되고 MyExcludeComponent는 등록되지 않는다.

## FilterType 옵션

- ANNOTATION: 기본값, 애노테이션을 인식해서 동작한다.
    - ex) org.example.SomeAnnotation
- ASSIGNABLE_TYPE: 지정한 타입과 자식 타입을 인식해서 동작한다.
    - ex) org.example.SomeClass
- ASPECTJ: AspectJ 패턴 사용
    - ex) org.example..*Service+*
- *REGEX: 정규 표현식*
    - *ex) org\.example\.Default.*
- CUSTOM: TypeFilter 이라는 인터페이스를 구현해서 처리
    - ex) org.example.MyTypeFilter

<aside>
💡 최근 스프링부트(@SpringBootApplication)은 컴포넌트 스캔을 기본으로 제공하는데, 최대한 이 기본옵션에 맞추어 사용하는 것을 권장한다.

</aside>

# 중복 등록과 충돌

스프링 빈의 이름 혹은 타입이 같은 경우 충돌이 발생한다. 충돌은 다음과 같이 크게 2가지로 나눌 수 있다.

- 자동 등록 vs 자동 등록
- 자동 등록 vs 수동 등록

## 자동 등록 vs 자동 등록

`ConflictingBeanDefinitionException` 예외 발생

## 자동 등록 vs 수동 등록

수동 등록이 우선권을 가진다.

```java
@Component
public class MemoryMemberRepository implements MemberRepository {}
```

```java
@Configuration
@ComponentScan(
 excludeFilters = @Filter(type = FilterType.ANNOTATION, classes =
Configuration.class)
)
public class AutoAppConfig {
 @Bean(name = "memoryMemberRepository")
 public MemberRepository memberRepository() {
 return new MemoryMemberRepository();
 }
}
```

<aside>
💡 다음과 같은 로그가 남는다.

Overriding bean definition for bean 'memoryMemberRepository' with a different
definition: replacing

</aside>

### 문제점

애매하다. 의도하지 않은 결과일 수 있다. ⇒ 잡기 어려운 버그이다.

### 스프링 부트

이 경우에도 에러가 발생하도록 처리하였다.

`Consider renaming one of the beans or enabling overriding by setting
spring.main.allow-bean-definition-overriding=true`