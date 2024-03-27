# 스프링 핵심 원리 이해2 - 객체 지향 원리 적용

- 새로운 할인 정책 개발
    
    - RateDiscountPolicy 코드 추가
        - public class RateDiscountPolicy implements DiscountPolicy
- 새로운 할인 정책 적용과 문제점
    
    - 할인 정책을 변경하려면 클라이언트인 OrderServiceImpl 코드를 고쳐야 한다
        
        ```java
        public class OrderServiceImpl implements OrderService {
        // private final DiscountPolicy discountPolicy = new FixDiscountPolicy();
         private final DiscountPolicy discountPolicy = new RateDiscountPolicy();
        }
        ```
        
    - 인터페이스에만 의존하도록 설계를 변경하자
        
        ```java
        public class OrderServiceImpl implements OrderService {
         //private final DiscountPolicy discountPolicy = new RateDiscountPolicy();
         private DiscountPolicy discountPolicy;
        }
        ```
        
    - 해결방안
        
        - 이 문제를 해결하려면 누군가가 클라이언트인 OrderServiceImpl 에 DiscountPolicy 의 구현 객체를 대신 생성하고 주입해주어야 한다.
- 관심사의 분리
    
    AppConfig
    
    - 애플리케이션의 전체 동작 방식을 구성(config)하기 위해, 구현 객체를 생성하고, 연결하는 책임을 가지는 별도의 설정 클래스를
        
        ```java
        public class AppConfig {
         public MemberService memberService() {
        	 return new MemberServiceImpl(new MemoryMemberRepository());
         }
         public OrderService orderService() {
        	 return new OrderServiceImpl(
        		 new MemoryMemberRepository(),
        		 new FixDiscountPolicy());
         }
        ```
        
    - AppConfig는 애플리케이션의 실제 동작에 필요한 구현 객체를 생성한다.
        
    - AppConfig는 생성한 객체 인스턴스의 참조(레퍼런스)를 생성자를 통해서 주입(연결)해준다
        
    
    MemberServiceImpl - 생성자 주입
    
    ```java
    public class MemberServiceImpl implements MemberService {
     private final MemberRepository memberRepository;
     public MemberServiceImpl(MemberRepository memberRepository) {
     this.memberRepository = memberRepository;
     }
     public void join(Member member) {
     memberRepository.save(member);
     }
     public Member findMember(Long memberId) {
     return memberRepository.findById(memberId);
     }
    }
    ```
    
    OrderServiceImpl - 생성자 주입
    
    ```java
    public class OrderServiceImpl implements OrderService {
     private final MemberRepository memberRepository;
     private final DiscountPolicy discountPolicy;
     public OrderServiceImpl(MemberRepository memberRepository, DiscountPolicy
    discountPolicy) {
     this.memberRepository = memberRepository;
     this.discountPolicy = discountPolicy;
     }
     @Override
     public Order createOrder(Long memberId, String itemName, int itemPrice) {
     Member member = memberRepository.findById(memberId);
      int discountPrice = discountPolicy.discount(member, itemPrice);
     return new Order(memberId, itemName, itemPrice, discountPrice);
     }
    }
    ```
    
    AppConfig 실행
    
    MemberApp
    
    ```java
    public class MemberApp {
     public static void main(String[] args) {
    	 AppConfig appConfig = new AppConfig();
    	 MemberService memberService = appConfig.memberService();
    	 Member member = new Member(1L, "memberA", Grade.VIP);
    	 memberService.join(member);
    	 Member findMember = memberService.findMember(1L);
    	 System.out.println("new member = " + member.getName());
    	 System.out.println("find Member = " + findMember.getName());
     }
    }
    ```
    
    사용 클래스 - OrderApp
    
    ```java
    public class OrderApp {
     public static void main(String[] args) {
     AppConfig appConfig = new AppConfig();
     MemberService memberService = appConfig.memberService();
     OrderService orderService = appConfig.orderService();
     long memberId = 1L;
     Member member = new Member(memberId, "memberA", Grade.VIP);
     memberService.join(member);
     Order order = orderService.createOrder(memberId, "itemA", 10000);
     System.out.println("order = " + order);
     }
    }
    ```
    
- AppConfig 리팩터링
    
    기존
    
    ```java
    public class AppConfig {
     public MemberService memberService() {
    	 return new MemberServiceImpl(new MemoryMemberRepository());
     }
     public OrderService orderService() {
    	 return new OrderServiceImpl(
    		 new MemoryMemberRepository(),
    		 new FixDiscountPolicy());
     }
    ```
    
    리펙토링
    
    ```java
    public class AppConfig {
     public MemberService memberService() {
    	 return new MemberServiceImpl(memberRepository());
     }
     public OrderService orderService() {
    	 return new OrderServiceImpl(
    	 memberRepository(),
    	 discountPolicy());
     }
     public MemberRepository memberRepository() {
    	 return new MemoryMemberRepository();
     }
     public DiscountPolicy discountPolicy() {
    	 return new FixDiscountPolicy();
    	 }
    }
    ```
    
- 새로운 구조와 할인 정책 적용
    
    ```java
    public class AppConfig {
    ...
    
    	public DiscountPolicy discountPolicy() {
    	// return new FixDiscountPolicy();
    	 return new RateDiscountPolicy();
    	 }
    }
    ```
    
    - 이제 할인 정책을 변경해도, 애플리케이션의 구성 역할을 담당하는 AppConfig만 변경하면 된다.
    - 클라이언트 코드인 OrderServiceImpl 를 포함해서 사용 영역의 어떤 코드도 변경할 필요가 없다
- 좋은 객체 지향 설계의 5가지 원칙의 적용
    
    SRP 단일 책임 원칙
    
    한 클래스는 하나의 책임만 가져야 한다.
    
    - 클라이언트 객체는 직접 구현 객체를 생성하고, 연결하고, 실행하는 다양한 책임을 가지고 있음
    - SRP 단일 책임 원칙을 따르면서 관심사를 분리함
    - 구현 객체를 생성하고 연결하는 책임은 AppConfig가 담당
    - 클라이언트 객체는 실행하는 책임만 담당
    
    DIP 의존관계 역전 원칙
    
    프로그래머는 “추상화에 의존해야지, 구체화에 의존하면 안된다.” 의존성 주입은 이 원칙을 따르는 방법 중 하나다.
    
    - 새로운 할인 정책을 개발하고, 적용하려고 하니 클라이언트 코드도 함께 변경해야 했다. 왜냐하면 기존 클라이언트 코드( OrderServiceImpl )는 DIP를 지키며 DiscountPolicy 추상화 인터페이스에 의존하는 것 같았지만, FixDiscountPolicy 구체화 구현 클래스에도 함께 의존했다.
    - 클라이언트 코드가 DiscountPolicy 추상화 인터페이스에만 의존하도록 코드를 변경했다.
    - 하지만 클라이언트 코드는 인터페이스만으로는 아무것도 실행할 수 없다.
    - AppConfig가 FixDiscountPolicy 객체 인스턴스를 클라이언트 코드 대신 생성해서 클라이언트 코드에 의존관계를 주입했다. 이렇게해서 DIP 원칙을 따르면서 문제도 해결했다.
    
    OCP 소프트웨어 요소는 확장에는 열려 있으나 변경에는 닫혀 있어야 한다
    
    - 다형성 사용하고 클라이언트가 DIP를 지킴
    - 애플리케이션을 사용 영역과 구성 영역으로 나눔
    - AppConfig가 의존관계를 FixDiscountPolicy RateDiscountPolicy 로 변경해서 클라이언트 코드에 주입하므로 클라이언트 코드는 변경하지 않아도 됨
    - 소프트웨어 요소를 새롭게 확장해도 사용 영역의 변경은 닫혀 있다!
- 의존관계 주입 DI(Dependency Injection)
    
    제어의 역전 IoC(Inversion of Control)
    
    - 기존 프로그램은 클라이언트 구현 객체가 스스로 필요한 서버 구현 객체를 생성하고, 연결하고, 실행했다. 한마디로 구현 객체가 프로그램의 제어 흐름을 스스로 조종했다. 개발자 입장에서는 자연스러운 흐름이다.
    - 반면에 AppConfig가 등장한 이후에 구현 객체는 자신의 로직을 실행하는 역할만 담당한다. 프로그램의 제어 흐름은 이제 AppConfig가 가져간다. 예를 들어서 OrderServiceImpl 은 필요한 인터페이스들을 호출하지만 어떤 구현 객체들이 실행될지 모른다.
    - 프로그램에 대한 제어 흐름에 대한 권한은 모두 AppConfig가 가지고 있다. 심지어 OrderServiceImpl 도 AppConfig가 생성한다. 그리고 AppConfig는 OrderServiceImpl 이 아닌 OrderService 인터페이스의 다른 구현 객체를 생성하고 실행할 수 도 있다. 그런 사실도 모른체 OrderServiceImpl 은 묵묵히 자신의 로직을 실행할 뿐이다.
    - 이렇듯 프로그램의 제어 흐름을 직접 제어하는 것이 아니라 외부에서 관리하는 것을 제어의 역전(IoC)이라 한다
    
    의존관계 주입 DI(Dependency Injection)
    
    - 정적인 클래스 의존관계
        - 클래스가 사용하는 import 코드만 보고 의존관계를 쉽게 판단할 수 있다. 정적인 의존관계는 애플리케이션을 실행하지 않아도 분석할 수 있다
    - 동적인 객체 인스턴스 의존 관계
        - 애플리케이션 실행 시점에 실제 생성된 객체 인스턴스의 참조가 연결된 의존 관계다
        - 애플리케이션 실행 시점(런타임)에 외부에서 실제 구현 객체를 생성하고 클라이언트에 전달해서 클라이언트와 서버의 실제 의존관계가 연결 되는 것을 의존관계 주입이라 한다.
        - 객체 인스턴스를 생성하고, 그 참조값을 전달해서 연결된다.
        - 의존관계 주입을 사용하면 클라이언트 코드를 변경하지 않고, 클라이언트가 호출하는 대상의 타입 인스턴스를 변경할 수 있다.
        - 의존관계 주입을 사용하면 정적인 클래스 의존관계를 변경하지 않고, 동적인 객체 인스턴스 의존관계를 쉽게 변경 할 수 있다
- 스프링으로 전환하기
    
    @Configuration
    
    AppConfig에 설정을 구성한다는 뜻
    
    @Bean
    
    스프링 컨테이너에 스프링 빈으로 등록한다
    
    ```java
    ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
    MemberService memberService = applicationContext.getBean("memberService", MemberService.class);
    ```
    
    - ApplicationContext 를 스프링 컨테이너라 한다.