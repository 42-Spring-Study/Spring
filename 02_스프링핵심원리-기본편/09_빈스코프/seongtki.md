# 09. 빈 스코프

`Scope`는 빈이 존재할 수 있는 범위를 의미한다. 이번 장에서 종류와 사용예제 등을 알아보자.

##### 코드참조

~~~
코드:  hello.core.sec09
테스트: hello.core.sec09_빈스코프/
~~~



#### 스프링이 지원하는 스코프 

- 싱글톤: 기본 스코프, 스프링 컨테이너의 시작과 종료까지 유지되는 가장 넓은 범위의 스코프이다.
- 프로토타입: 스프링 컨테이너는 프로토타입 빈의 생성과 의존관계 주입까지만 관여하고 더는 관리하지 않는 매우
  짧은 범위의 스코프이다.
- request: 웹 요청이 들어오고 나갈때 까지 유지되는 스코프이다.
- session: 웹 세션이 생성되고 종료될 때 까지 유지되는 스코프이다.
- application: 웹의 서블릿 컨텍스트와 같은 범위로 유지되는 스코프이다.







## 1) 싱글톤, 프로토타입 스코프

싱글톤 스코프의 빈을 조회하면 스프링 컨테이너는 항상 같은 인스턴스의 스프링 빈을 반환한다. 
반면에 프로토타입 스 코프를 스프링 컨테이너에 조회하면 스프링 컨테이너는 항상 새로운 인스턴스를 생성해서 반환한다.



#### 싱글톤

~~~
1. 싱글톤 스코프의 빈을 스프링 컨테이너에 요청한다.
2. 스프링 컨테이너는 본인이 관리하는 스프링 빈을 반환한다.
3. 이후에 스프링 컨테이너에 같은 요청이 와도 같은 객체 인스턴스의 스프링 빈을 반환한다.
~~~

##### 예제

`@PostConstruct`는 처음 빈 생성시에만 수행된다. `@PreDestroy`는 빈 컨테이너 종료시 수행된다.

~~~java
@Scope("singleton")
static class SingletonBean {
    @PostConstruct
    public void init() {
        System.out.println("SingletonBean.init");
    }
    @PreDestroy
    public void destroy() {
        System.out.println("SingletonBean.destroy");
    }
}
~~~



#### 프로토타입

~~~
1. 프로토타입 스코프의 빈을 스프링 컨테이너에 요청한다.
2. 스프링 컨테이너는 이 시점에 프로토타입 빈을 생성하고, 필요한 의존관계를 주입한다.
3. 스프링 컨테이너는 생성한 프로토타입 빈을 클라이언트에 반환한다.
4. 이후에 스프링 컨테이너에 같은 요청이 오면 항상 새로운 프로토타입 빈을 생성해서 반환한다.
~~~

##### 예제

`@PreDestroy` 는 동작하지 않는다.

- 빈 생성 이후의 인스턴스 생성주기 권한이 호출자로 넘어간다.

~~~java
@Scope("prototype")
static class PrototypeBean {
    @PostConstruct
    public void init() {
        System.out.println("PrototypeBean.init");
    }
    @PreDestroy
    public void destroy() {
        System.out.println("PrototypeBean.destroy");
    }
}
~~~







## 2) 싱글톤 빈이 프로토타입 빈을 관리할 때 이슈



싱글톤인 `ClientBean` 클래스에서 프로토타입인 `PrototypeBean`를 관리한다고 해보자.

직관적으로 PrototypeBean 클래스는 프로토타입으로 동작한다고 생각되지만, 실제로는 `ClientBean` 빈 최초 생성 이후에는

빈을 가져올 때 클래스 내부는 초기화하지 않아서 `PrototypeBean` 빈을 새로 생성하지 않는다.

`ClientBean` 빈을 가져올 때마다 `PrototypeBean`를 생성하려면 아래 세가지 방법 중 하나를 선택하면 된다.



~~~java
@Scope("prototype")
static class PrototypeBean {
...
~~~

#### 해결1) ApplicationContext

- ApplicationContext getBean()을 하면 `Prototype` Scope이기 때문에 인스턴스를 생성해서 리턴한다.

~~~java
@Scope("singleton")
static class ClientBean {
    @Autowired
    private ApplicationContext ac;
    public int logic() {
        PrototypeBean prototypeBean = ac.getBean(PrototypeBean.class);
        prototypeBean.addCount();
        int count = prototypeBean.getCount();
        return count;
    }
}
~~~

#### 해결2) ObjectFactory, ObjectProvider

- ObjectFactory: 기능이 단순, 별도의 라이브러리 필요 없음, 스프링에 의존
- ObjectProvider: ObjectFactory 상속, 옵션, 스트림 처리등 편의 기능이 많고, 별도의 라이브러리 필요 없음, 스프링에 의존

~~~java
@Scope("singleton")
static class ClientBean2 {
    @Autowired
//        private ObjectProvider<PrototypeBean> prototypeBeanProvider;
    private ObjectFactory<PrototypeBean> prototypeBeanProvider;
    public int logic() {
        PrototypeBean prototypeBean = prototypeBeanProvider.getObject();
        prototypeBean.addCount();
        int count = prototypeBean.getCount();
        return count;
    }
}
~~~

#### 해결3) jakarta.inject.Provider (JSR330)

- `jakarta.inject:jakarta.inject-api:2.0.1` 라이브러리 추가 (자바 표준)
- 실행해보면 provider.get() 을 통해서 항상 새로운 프로토타입 빈이 생성되는 것을 확인할 수 있다.
- provider 의 get() 을 호출하면 내부에서는 스프링 컨테이너를 통해 해당 빈을 찾아서 반환한다. (DL)

~~~java
@Scope("singleton")
static class ClientBean3 {
    @Autowired
    private Provider<PrototypeBean> provider;
    public int logic() {
        PrototypeBean prototypeBean = provider.get();
        prototypeBean.addCount();
        int count = prototypeBean.getCount();
        return count;
    }
}
~~~









## 3) 웹 스코프

#### 웹 스코프의 특징

웹 스코프는 웹 환경에서만 동작한다.
웹 스코프는 프로토타입과 다르게 스프링이 해당 스코프의 종료시점까지 관리한다. 따라서 종료 메서드가 호출된다.

#### 웹 스코프 종류

- request: HTTP 요청 하나가 들어오고 나갈 때 까지 유지되는 스코프, 각각의 HTTP 요청마다 별도의 빈 인스턴
  스가 생성되고, 관리된다.
- session: HTTP Session과 동일한 생명주기를 가지는 스코프
- application: 서블릿 컨텍스트( ServletContext )와 동일한 생명주기를 가지는 스코프
- websocket: 웹 소켓과 동일한 생명주기를 가지는 스코프



## 4) request 스코프 예제 만들기



url 요청마다 아래와 같은 형태의 로그를 console에 출력하는 서비스를 만들어보자.

~~~sh
[d06b992f...] request scope bean create
[d06b992f...][http://localhost:8080/log-demo] controller test
[d06b992f...][http://localhost:8080/log-demo] service id = testId
[d06b992f...] request scope bean close
~~~

url 요청이 기준이다. 해당 범위에 같은 uuid를 사용한다는걸 생각해서 빈 범위를 request scope로 설정한다.

~~~sql
@Component
@Scope("request")
public class MyLogger {
    private String uuid;
    private String requestURL;
    public void setRequestURL(String requestURL) {
        this.requestURL = requestURL;
    }
    public void log(String message) {
        System.out.println("[" + uuid + "]" + "[" + requestURL + "] " + message);
    }
    @PostConstruct
    public void init() {
        uuid = UUID.randomUUID().toString();
        System.out.println("[" + uuid + "] request scope bean create:" + this);
    }
    @PreDestroy
    public void close() {
        System.out.println("[" + uuid + "] request scope bean close:" + this);
    }
}
~~~



#### 주의사항: DI - 실행오류

~~~java
@Autowired private MyLogger myLogger;
~~~

- MyLogger 를 사용하기 위해  DI를 하면 아래와 같이 에러가 발생한다.
  ~~~
  Error creating bean with name 'myLogger': Scope 'request' is not active for the
  current thread; consider defining a scoped proxy for this bean if you intend to
  refer to it from a singleton;
  ~~~

- MyLogger는 request 범위기 때문에 url 요청중일 때에만 인스턴스가 존재하므로, 프로그램 실행시엔 빈이 생성되지 않는다.



#### 해결방법1) ObjectProvider 사용

- ObjectProvider를 사용하면 런타임에 실제 요청 시(getObject() 호출) 빈을생성하고 주입받아 사용하게 된다.
- 일단 빈이 생성되면 요청이 끝나기 전까지 같은 빈을 주입받아 사용할 수 있다.

~~~java
// Contorller
private final ObjectProvider<MyLogger> myLoggerProvider;

@Autowired
public LogDemoController(LogDemoService logDemoService, ObjectProvider<MyLogger> myLoggerProvider) {
    this.logDemoService = logDemoService;
    this.myLoggerProvider = myLoggerProvider;
}

@RequestMapping("log-demo")
@ResponseBody
public String logDemo(HttpServletRequest request) {
    String requestURL = request.getRequestURL().toString();
    MyLogger myLogger = myLoggerProvider.getObject();
    myLogger.setRequestURL(requestURL);
    myLogger.log("controller test");
    System.out.println("myLogger = " + myLogger.getClass());
    logDemoService.logic("testId");
    return "OK";
}
~~~

~~~sql
@Service
public class LogDemoService {

    private final MyLogger myLogger;
    private final ObjectProvider<MyLogger> myLoggerProvider;

    @Autowired
    public LogDemoService(ObjectProvider<MyLogger> myLoggerProvider) {
        this.myLoggerProvider = myLoggerProvider;
    }

    public void logic(String id) {
        myLoggerProvider.getObject().log("service id = " + id);
    }
}
~~~



#### 해결방법2) 프록시 적용

- proxyMode를 설정하면, ASM에 의해 필요한 시점에 프록시 객체(빈)를 생성하여 관리한다.
- 마찬가지로 request 범위에서 같은 빈을 사용할 수 있다.

~~~java
// 적용대상이 인터페이스라면 ScopedProxyMode.INTERFACES 로 설정
@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class MyLogger {
...
~~~

##### 스프링의 프록시 패턴은 AOP에서 주로 사용한다. 동작원리 등은 스프링핵심원리-고급편에서 다룰 예정























