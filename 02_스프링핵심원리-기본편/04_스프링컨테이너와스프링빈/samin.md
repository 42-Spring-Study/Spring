스프링 빈에 등록한 인스턴스들을 조회하는 다양한 방법에 대해 알아봄

- 스프링 컨테이너 생성
    
    스프링 컨테이너의 생성과정
    
    1. 스프링 컨테이너 생성
    2. 스프링 빈 등록
        1. 빈 이름은 메서드 이름을 사용한다.
        2. 빈이름을 직접 부여할수도 있다
            1. 이름중복이 되면 안된다.
    3. 스프링 빈 의존관계 설정 - 준비
    4. 스프링 빈 의존관계 설정 - 완료
- 컨테이너에 등록된 모든 빈 조회
    
    ```java
AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);
    ```
    
    ac.getBeanDefinitionNames() 로 스프링에 등록된 모든 빈 이름 조회가능
    
    ac.getBean() 빈 이름으로 빈 객체(인스턴스)를 조회한다.
    
- **스프링 빈 조회** - **기본**
    
    스프링 컨테이너에서 스프링 빈을 찾는 가장 기본적인 조회 방
    
    ac.getBean(빈이름, 타입) ac.getBean(타입)
    
    조회 대상 스프링 빈이 없으면 예외 발생
    
- 스프링 빈 조회 - 동일한 타입이 둘 이상
    
    타입으로 조회시 같은 타입의 스프링 빈이 둘 이상이면 오류가 발생한다.
    
    ```jsx
    MemberRepository memberRepository = ac.getBean("memberRepository1", MemberRepository.class);
    ```
    
    이때는 빈 이름을 지정하자.
    
    `ac.getBeansOfType()` 을 사용하면 해당 타입의 모든 빈을 조회할 수 있다.
    
- **스프링 빈 조회** - **상속 관계**
    
    - 부모 타입으로 조회하면, 자식 타입도 함께 조회한다.
    - 그래서 모든 자바 객체의 최고 부모인 `Object` 타입으로 조회하면, 모든 스프링 빈을 조회한다
- BeanFactory**와** ApplicationContext
    
    - **BeanFactory**
        
        - 스프링 빈을 관리하고 조회하는 역할을 담당한다.
    - **ApplicationContext**
        
        - BeanFactory 기능을 모두 상속받아서 제공한다.
        - 애플리케이션을 개발할 때는 빈을 관리하고 조회하는 기능은 물론이고, 수 많은 부가기능이 필요하다.
    - **ApplicatonContext가 제공하는 부가기능**
        
        - **메시지소스를 활용한 국제화 기능**
            
        - **환경변수** 로컬, 개발, 운영등을 구분해서 처리
            
        - **애플리케이션 이벤트**
            
            발행하고 구독하는 모델을 편리하게 지원
            
        - **편리한 리소스 조회**
            
            파일, 클래스패스, 외부 등에서 리소스를 편리하게 조회
            
- **다양한 설정 형식 지원** - **자바 코드**, XML
    
    현재는 어노테이션 기반을 주로 사용하지만
    
    과거에는 설정을할때 xml기반을 많이 사용하였다.
    
    xml을 사용하면 컴파일 없이 빈 설정정보를 변경할수 있는 장점이 있다
    
    GenericXmlApplicationContext를 사용한다
    
    ```jsx
     ApplicationContext ac = new GenericXmlApplicationContext("appConfig.xml");
    ```
    
    xml형식
    
    ```jsx
    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="<http://www.springframework.org/schema/beans>"
          xmlns:xsi="<http://www.w3.org/2001/XMLSchema-instance>"
          xsi:schemaLocation="<http://www.springframework.org/schema/beans> http://
    www.springframework.org/schema/beans/spring-beans.xsd">
    
    	<bean id="memberService" class="hello.core.member.MemberServiceImpl">
    	   <constructor-arg name="memberRepository" ref="memberRepository" />
    	</bean>
    	     
    	<bean id="memberRepository" class="hello.core.member.MemoryMemberRepository" />
    	 
    	     <bean id="orderService" class="hello.core.order.OrderServiceImpl">
    	         <constructor-arg name="memberRepository" ref="memberRepository" />
    	         <constructor-arg name="discountPolicy" ref="discountPolicy" />
    	     </bean>
    	     <bean id="discountPolicy" class="hello.core.discount.RateDiscountPolicy" />
    	</beans>
    ```
    
- **스프링 빈 설정 메타 정보** - BeanDefinition BeanDefinition?
    
    BeanDefinition 을 빈 설정 메타정보라 한다.
    
    - AnnotationConfigApplicationContext는 AnnotatedBeanDefinitionReader 를 사용해서 AppConfig.class 를 읽고 BeanDefinition 을 생성한다.
    - GenericXmlApplicationContext 는 XmlBeanDefinitionReader 를 사용해서 appConfig.xml 설정 정보를 읽고 BeanDefinition 을 생성한다.
    - 새로운 형식의 설정 정보가 추가되면, XxxBeanDefinitionReader를 만들어서 BeanDefinition 을 생성하면 된다.
    - **BeanDefinition 정보**
        - BeanClassName: 생성할 빈의 클래스 명(자바 설정 처럼 팩토리 역할의 빈을 사용하면 없음)
        - factoryBeanName: 팩토리 역할의 빈을 사용할 경우 이름, 예) appConfigfactoryMethodName: 빈을 생성할 팩토리 메서드 지정, 예) memberService
        - Scope: 싱글톤(기본값)
        - lazyInit: 스프링 컨테이너를 생성할 때 빈을 생성하는 것이 아니라, 실제 빈을 사용할 때 까지 최대한 생성을 지연처리 하는지 여부
        - InitMethodName: 빈을 생성하고, 의존관계를 적용한 뒤에 호출되는 초기화 메서드 명
        - DestroyMethodName: 빈의 생명주기가 끝나서 제거하기 직전에 호출되는 메서드 명
        - Constructor arguments, Properties: 의존관계 주입에서 사용한다. (자바 설정 처럼 팩토리 역할의 빈을 사용 하면 없음)