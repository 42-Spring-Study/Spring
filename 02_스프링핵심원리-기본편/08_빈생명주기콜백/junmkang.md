# 섹션 8. 빈 생명주기 콜백

## 빈 생명주기 콜백 시작

### 생명주기란?

일반적으로 어떤 객체나 프로세스가 생성되고 활성화되며 사용되고 소멸되는 과정을 이야기한다.

### 생명주기가 왜 필요한가?

그럼 java에서 이야기하는 생명주기는 어떠한 경우를 이야기하냐면 

```java
@Configuration
			static class LifeCycleConfig {
         @Bean
         public NetworkClient networkClient() {
             NetworkClient networkClient = new NetworkClient();
             networkClient.setUrl("http://hello-spring.dev");
             return networkClient;
         }
			} 
}
```

위와 같이 객체를 생성 한 이후에 외부에서 `setUrl`을 사용해서 url을 지정해주는 로직을 실행해보면

```java
생성자 호출, url = null 
connect: null
call: null message = 초기화 연결 메시지
```

생성될때에 경우 위와 같이 url에 해당되는 부분이 `null`로 나오는것을 확인할 수 있다.
당연히 객체를 생성하는 단계 `NetworkClient networkClient = new NetworkClient();` 에서는 따로 url을 들고있지않기때문에 `networkClient.setUrl("http://hello-spring.dev");` 해당 로직전에 출력이 되는 부분에서는 `null`이라는 값이 나올 수 밖에 없다.

그런데 생명주기를 통해 콜백을 선언하면 스프링 빈이 스프링 컨테이너에 등록된 후 
( 즉 위에서 해당 파트는 `public NetworkClient networkClient()` 이 종료된 직후 ) 
에 콜백을 걸어서 원하는 동작을 추가로 할 수 있게된다.

스프링 빈을 사용할때 스프링 빈의 라이프 사이클은 아래와 같다.

```java
**객체 생성 → 의존관계 주입**
```

그렇다보니 실제로 스프링 빈에 접근을 할려면 위와 같이 객체생성과 의존관계 주입이 다 끝난 뒤에 해당 빈을 호출을 해야한다. 그러면 개발자는 그 시점을 알아 차려야 **“내가 지금 빈에 접근할 수 있구나”**를 알게되는데 그것을 어떻게 알 수 있는가? 에 대한 의문점이 생기게 되는데

**스프링 자체에서 의존관계 주입이 완료되면 스프링 빈에게 콜백 메서드를 통해서 초기화 시점을 알려주는 기능을 제공**하고, 스프링은 스프링 컨테이너가 종료되기 직전에 소명 콜백을 주어 안전하게 스프링컨테이너의 종료 작업을 진행할 수 있도록 알려준다고한다.

그래서 콜백을 적용시킨 스프링 빈의 이벤트의 라이프 사이클은 아래와 같이 구성할 수 있다.

```java
**스프링 컨테이너 생성 → 스프링 빈 생성 → 의존관계 주입 → 초기화 콜백 → 사용 → 소멸전 콜백 → 스프링 종료**
```

- **초기화 콜백**: 빈이 생성되고, 빈의 의존관계 주입이 완료된 후 호출
- **소멸전 콜백**: 빈이 소멸되기 직전에 호출

그렇다보니 스프링 빈을 생성하고 의존관계가 주입이 된 이후에 초기화 콜백이라는 것을 사용하여 빈에 접근을 하고 모두 다 사용되고 난 이후에는 소멸전 콜백을 사용하여 데이터들을 정리하는 것이 필요하다고 한다.

그래서 그러한 콜백들을 받을 수 있는 방법이 크게 3가지가 있다고 한다.

1. **인터페이스 InitializingBean, DisposableBean**
2. **빈 등록 초기화, 소멸 메서드 지정**
3. **애노테이션 @PostConstruct, @PreDestroy**

### **객체의 생성과 초기화는 분리하자.**

생성자: 필수정보를 받고 메모리를 할당해서 객체를 생성하는 책임

초기화: 생성된 값들을 활용하여 외부 커넥션을 연결하는 등 무거운 동작 수행

으로 나누어 지게 된다.

그것을 보고 “초기화라는 과정과 객체를 생성하는 과정을 하나로 합치면 깔끔하지않을까?”라는 생각이 들수도 있는데. 생성자안에서 초기화 작업까지 한번에 하게되면 생성자가 너무 많은 부담을 가지고 무거운 초기화 작업까지 하는것은 유지보수 측면에서도 힘들고 **SRC**에 위반되는 행위이므로 분리를 하는것이 좋다고한다.

## 인터페이스 InitializingBean, DisposableBean

```java
public class NetworkClient implements InitializingBean, DisposableBean {
	
	...
	
	@Override
	public void afterPropertiesSet() throws Exception {
		connect();
		call("초기화 연결 메시지"); 
	}
	
	@Override
	public void destroy() throws Exception {
		disConnect();
	}
}
```

```java
생성자 호출, url = null
NetworkClient.afterPropertiesSet
connect: http://hello-spring.dev
call: http://hello-spring.dev message = 초기화 연결 메시지
```

위와 같은 `InitializingBean, DisposableBean` 인터페이스들이 존재한다 해당 방식을 사용하여 

`InitializingBean` - `afterPropertiesSet` : 초기화 지원

`DisposableBean` - `destroy` : 소멸 지원이 가능하다.

### **초기화, 소멸 인터페이스 단점**

- 이 인터페이스는 스프링 전용 인터페이스다. 해당 코드가 스프링 전용 인터페이스에 의존한다.
- 초기화, 소멸 메서드의 이름을 변경할 수 없다.
- 내가 코드를 고칠 수 없는 외부 라이브러리에 적용할 수 없다.

**지금은 더 좋은 방법들이 있어서 사용 ❌**

## 빈 등록 초기화, 소멸 메서드 지정

```java
public class NetworkClient {
	
	...
	

	public void init() { 
		System.out.println("NetworkClient.init"); 
		connect();
		call("초기화 연결 메시지");
	}
	
	public void close() {
		System.out.println("NetworkClient.close");
		disConnect();
	}
}
```

```java
@Bean(initMethod = "init", destroyMethod = "close")
public NetworkClient networkClient() {
         ...
}
```

빈에 직접 `initMethod`, `destroyMethod`을 통해 초기화 콜백과 소멸 콜백으로 사용할 메서드를 지정해줄 수 있다.

**설정 정보 사용 특징**

- 메서드 이름을 자유롭게 줄 수 있다.
- 스프링 빈이 스프링 코드에 의존하지 않는다.
- 코드가 아니라 설정 정보를 사용하기 때문에 코드를 고칠 수 없는 외부 라이브러리에도 초기화, 종료 메서드를 적용할 수 있다.

`destroyMethod`에 경우에는 특이하게 `inferred` 이라는 기능이 있어서 종료 메서드의 이름이 `close`혹은 `shutdown` 인 메서드가 있다면 `destroyMethod` 통해 지정하지않아도 해당 메서드를 자동으로 호출해준다고한다.

이 방법도 외부 라이브러리의 초기화, 종료 외에는 잘 사용하지는 않는다고 한다.

## 애노테이션 @PostConstruct, @PreDestroy

```java
public class NetworkClient {
	
	...
	
	@PostConstruct
	public void init() { 
		System.out.println("NetworkClient.init"); 
		connect();
		call("초기화 연결 메시지");
	}
	
	@PreDestroy
	public void close() {
		System.out.println("NetworkClient.close");
		disConnect();
	}
}
```

```java
@Bean
 public NetworkClient networkClient() {
     NetworkClient networkClient = new NetworkClient();
     networkClient.setUrl("http://hello-spring.dev");
     return networkClient;
}
```

초기화, 종료로 사용할 메서드에 `@PostConstruct` 와 `@PreDestroy` 애노테이션을 지정만 해주면 바로 초기화 콜백과 소멸전 콜백으로 사용할 수 있게 된다.

**@PostConstruct, @PreDestroy 애노테이션 특징**

- 최신 스프링에서 가장 권장하는 방법이다.
- 애노테이션 하나만 붙이면 되므로 매우 편리하다.
- 패키지를 잘 보면 ``javax.annotation.PostConstruct`` 이다. 스프링에 종속적인 기술이 아니라 `JSR-250`
라는 자바 표준이다. 따라서 스프링이 아닌 다른 컨테이너에서도 동작한다.
- 컴포넌트 스캔과 잘 어울린다.
- 유일한 단점은 외부 라이브러리에는 적용하지 못한다는 것이다. 외부 라이브러리를 초기화, 종료 해야 하면
@Bean의 기능을 사용하자.

## 결론

**@PostConstruct, @PreDestroy 애노테이션을 사용하자**

코드를 고칠 수 없는 외부 라이브러리를 초기화, 종료해야 하면 `@Bean` 의 `initMethod` , `destroyMethod` 를 사용하자.