# **싱글톤 컨테이너**

- **웹 애플리케이션과 싱글톤**
    
    고객이 요청한때마다 인스턴스를 생성하면 메모리가 낭비된다.
    
    하나의 인스턴스만을 생성하고 공유해서 하면 메모리 절약 가능
    
- **싱글톤 패턴**
    
    - 클래스의 인스턴스가 딱 1개만 생성되는 것을 보장하는 디자인 패턴이다.
    - 그래서 객체 인스턴스를 2개 이상 생성하지 못하도록 막아야 한다.
    - private 생성자를 사용해서 외부에서 임의로 new 키워드를 사용하지 못하도록 막아야 한다.
    
    싱글톤 예제
    
    ```jsx
    public class SingletonService {
    //1. static 영역에 객체를 딱 1개만 생성해둔다.
    private static final SingletonService instance = new SingletonService();
    //2. public으로 열어서 객체 인스턴스가 필요하면 이 static 메서드를 통해서만 조회하도록 허용한 다.
         public static SingletonService getInstance() {
             return instance;
    }
    //3. 생성자를 private으로 선언해서 외부에서 new 키워드를 사용한 객체 생성을 못하게 막는다. private SingletonService() {
    }
    public void logic() { System.out.println("싱글톤 객체 로직 호출");
    } }
    ```
    
    **싱글톤 패턴 문제점**
    
    - 싱글톤 패턴을 구현하는 코드 자체가 많이 들어간다.
    - 의존관계상 클라이언트가 구체 클래스에 의존한다. DIP를 위반한다.
    - 클라이언트가 구체 클래스에 의존해서 OCP 원칙을 위반할 가능성이 높다. 테스트하기 어렵다.
    - 내부 속성을 변경하거나 초기화 하기 어렵다.
    - private 생성자로 자식 클래스를 만들기 어렵다.
    - 결론적으로 유연성이 떨어진다.
    - 안티패턴으로 불리기도 한다.
- 싱글톤 컨테이너
    
    **싱글톤 컨테이너**
    
    - 스프링 컨테이너는 싱글턴 패턴을 적용하지 않아도, 객체 인스턴스를 싱글톤으로 관리한다.
    - 이전에 설명한 컨테이너 생성 과정을 자세히 보자. 컨테이너는 객체를 하나만 생성해서 관리한다.
    - 스프링 컨테이너는 싱글톤 컨테이너 역할을 한다. 이렇게 싱글톤 객체를 생성하고 관리하는 기능을 싱글톤 레지스트리라 한다.
    - 스프링 컨테이너의 이런 기능 덕분에 싱글턴 패턴의 모든 단점을 해결하면서 객체를 싱글톤으로 유지할 수 있다.
    - 싱글톤 패턴을 위한 지저분한 코드가 들어가지 않아도 된다.
    - DIP, OCP, 테스트, private 생성자로 부터 자유롭게 싱글톤을 사용할 수 있다.
    
    필요시 요청때마다 새로운 객체를 생성해서 반환하는 기능도 제공한다.
    
- 싱글톤 방식의 주의점
    
    - 싱글톤으로 만든 객체에 결재금액 정보가 공유되고 있다면 다른 사람이 구매하였을때도 값이 증가하게되어 큰 문제가 된다.
        
    - 싱글톤 패턴이든, 스프링 같은 싱글톤 컨테이너를 사용하든, 객체 인스턴스를 하나만 생성해서 공유하는 싱글톤 방식은 여러 클라이언트가 하나의 같은 객체 인스턴스를 공유하기 때문에 싱글톤 객체는 상태를 유지(stateful)하게 설계하면 안된다.
        
        - 상태유지란?
            - **상태 유지(Stateful) 객체**는 이전의 연산이나 이벤트의 결과를 내부적으로 저장하고, 이러한 내부 상태에 기반하여 향후의 연산 결과가 달라질 수 있는 객체입니다. 예를 들어, 계산기 객체가 마지막 연산의 결과를 내부에 저장하고 있는 경우, 이는 상태를 유지하는 것입니다.
    - 무상태(stateless)로 설계해야 한다!
        
        - 특정 클라이언트에 의존적인 필드가 있으면 안된다.
        - 특정 클라이언트가 값을 변경할 수 있는 필드가 있으면 안된다!
        - 가급적 읽기만 가능해야 한다.
        - 필드 대신에 자바에서 공유되지 않는, 지역변수, 파라미터, ThreadLocal 등을 사용해야 한다.
    - chatgpt 답변
        
        ### **상태 유지(Stateful) vs 상태 비유지(Stateless)**
        
        - **상태 유지(Stateful) 객체**는 이전의 연산이나 이벤트의 결과를 내부적으로 저장하고, 이러한 내부 상태에 기반하여 향후의 연산 결과가 달라질 수 있는 객체입니다. 예를 들어, 계산기 객체가 마지막 연산의 결과를 내부에 저장하고 있는 경우, 이는 상태를 유지하는 것입니다.
        - **상태 비유지(Stateless) 객체**는 내부 상태를 저장하지 않으며, 동일한 입력에 대해 항상 동일한 출력을 반환합니다. 이러한 객체의 동작은 이전에 수행된 작업에 의존하지 않습니다. 예를 들어, 순수 함수는 주어진 입력에 대해서만 결과를 계산하고, 내부 상태에 의존하지 않으므로 상태 비유지입니다.
        
        ### **싱글톤과 상태 비유지**
        
        싱글톤 패턴을 사용하는 객체가 상태를 유지하면, 애플리케이션의 다른 부분에서 이 객체를 사용할 때 예상치 못한 결과를 초래할 수 있습니다. 싱글톤 인스턴스의 상태가 변경되면, 그 변경사항이 애플리케이션 전반에 영향을 미칩니다. 이는 디버깅을 어렵게 만들고, 오류를 추적하기 힘들게 만듭니다.
        
        따라서 싱글톤 객체를 설계할 때는 가능한 상태를 내부에 저장하지 않는 방식으로 구현하는 것이 좋습니다. 이를 통해 객체의 재사용성과 예측 가능성을 높일 수 있으며, 부작용의 가능성을 최소화할 수 있습니다.
        
- @Configuration과 싱글톤
    
    예제코드
    
    ```java
    @Configuration
    public class AppConfig {
     @Bean
     public MemberService memberService() {
     return new MemberServiceImpl(memberRepository());
     }
     @Bean
     public OrderService orderService() {
     return new OrderServiceImpl(
     memberRepository(),
     discountPolicy());
     }
     @Bean
     public MemberRepository memberRepository() {
     return new MemoryMemberRepository();
     }
     ...
    }
    ```
    
    memberService와 orderService 모두
    
    MemoryMemberRepository를 생성해준다
    
    하지만 실제로는 MemoryMemberRepository는 한개반 생성된다.
    
    스프링 컨테이너는 어떻게 이런 상황에서도 싱글톤을 보장해 주는걸까?
    
    soutm 하면 자동으로 println 생성
    
- @Configuration과 바이트코드 조작의 마법
    
    ```java
    void configurationDeep() {
     ApplicationContext ac = new
    AnnotationConfigApplicationContext(AppConfig.class);
     //AppConfig도 스프링 빈으로 등록된다.
     AppConfig bean = ac.getBean(AppConfig.class);
     
     System.out.println("bean = " + bean.getClass());
     //출력: bean = class hello.core.AppConfig$$EnhancerBySpringCGLIB$$bd479d70
    }
    ```
    
    - AnnotationConfigApplicationContext 에 파라미터로 넘긴 값은 스프링 빈으로 등록된다.
        
    - AppConfig.class의 빈을 조회해보면 와 같이 CGLIB가 붙은 빈이 생성된다.
        
    - 출력: bean = class hello.core.AppConfig$$EnhancerBySpringCGLIB$$bd479d70
        
    
    CGLIB는 바이트 코드를 조작할수있는 라이브러리 이다.
    
    @Configuration 을 붙이면 바이트코드를 조작하는 CGLIB 기술을 사용해서 싱글톤을 보장하지만
    
    스프링 설정파일에 @Configuration 를 붙이지 않고 @Bean 만을 사용해도 스프링 빈에 등록은 되지만 싱글톤은 보장하지 않는다.
    
    AppConfig@CGLIB 예상 코드
    
    ```java
    @Bean
    public MemberRepository memberRepository() {
     
     if (memoryMemberRepository가 이미 스프링 컨테이너에 등록되어 있으면?) {
     return 스프링 컨테이너에서 찾아서 반환;
     } else { //스프링 컨테이너에 없으면
     기존 로직을 호출해서 MemoryMemberRepository를 생성하고 스프링 컨테이너에 등록
     return 반환
     }
    }
    ```
    
    chatGPT 답변 : CGLIB
    
    CGLIB(Code Generation Library)은 Java 클래스의 바이트 코드를 런타임에 동적으로 생성하고 조작하기 위한 강력한 고성능 라이브러리입니다. 이 라이브러리는 프록시, 메소드 인터셉션, 그리고 클래스와 메소드의 런타임 생성을 포함한 여러 기능을 제공합니다. CGLIB는 리플렉션을 사용하는 대신 직접 바이트 코드를 조작함으로써, 더 높은 성능을 제공할 수 있습니다. 주로 프록시 생성이나 AOP(Aspect-Oriented Programming) 프레임워크에서 많이 사용됩니다.
    
    ### **CGLIB의 주요 사용 사례**
    
    - **프록시 생성**: 대상 객체에 대한 프록시를 생성하여, 해당 객체의 메소드 호출을 가로채거나, 사전/사후 처리를 추가할 수 있습니다. 이는 AOP의 구현이나 트랜잭션 관리 등에 유용하게 사용됩니다.
    - **런타임에 클래스나 메소드 추가**: 기존 클래스에 새로운 메소드나 필드를 런타임에 추가하거나, 완전히 새로운 클래스를 동적으로 생성할 수 있습니다.
    
    ### **CGLIB vs 리플렉션**
    
    리플렉션도 런타임에 클래스 정보를 조사하고 객체를 조작할 수 있지만, CGLIB는 다음과 같은 장점을 가집니다:
    
    - **성능**: CGLIB는 바이트 코드 조작을 통해 리플렉션에 비해 더 높은 성능을 제공합니다.
    - **유연성**: 리플렉션은 기존에 존재하는 클래스와 메소드에 대해서만 작업할 수 있지만, CGLIB는 새로운 클래스나 메소드를 런타임에 생성할 수 있는 더 큰 유연성을 제공합니다.
    
    ### **CGLIB와 자바 프록시**
    
    자바의 기본 프록시 메커니즘은 인터페이스 기반 프록시만을 지원합니다. 즉, 대상 객체가 최소한 하나의 인터페이스를 구현해야 합니다. 반면, CGLIB는 인터페이스가 없는 클래스에 대해서도 프록시를 생성할 수 있습니다. 이는 CGLIB가 클래스 자체를 상속받아 새로운 자식 클래스를 생성하기 때문에 가능합니다.
    
    ### **주의사항**
    
    CGLIB를 사용할 때는 몇 가지 주의사항이 있습니다:
    
    - **파이널 메소드와 클래스**: CGLIB는 파이널 메소드나 클래스를 오버라이드할 수 없으므로, 이러한 메소드나 클래스에 대해서는 프록시 생성이 불가능합니다.
    - **복잡성**: 바이트 코드 조작은 복잡할 수 있으므로, CGLIB를 사용할 때는 추가적인 주의가 필요합니다.
    
    CGLIB는 Spring 프레임워크에서 AOP 구현이나 프록시 객체 생성 등에 내부적으로 사용되는 대표적인 예입니다.