# 섹션 2. 서블릿

## 서블릿

클라이언트의 요청에 대해 동적으로 응답을 생성하는 웹 애플리케이션 컴포넌트

http통신을 진행할때 편리하게 할 수 있도록 http 요청메시지와 응답메시지의 스펙을 편리하게 할 수 있도록 도와주는것을 이야기한다.

### Hello 서블릿

```java
@ServletComponentScan // 서블릿 자동 등록
@SpringBootApplication
public class ServletApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServletApplication.class, args);
	}

}
```

`ServletApplication` 전체적으로 컨테이너들을 관리하고 실행을 담당하는 가장 main에 파일에서는
`@ServletComponentScan` 애노테이션을 같이 적어주어서 서블릿들을 main에서 찾아서 등록할 수 있도록 설정해줄 수 있다.

```java
@WebServlet(name = "helloServlet", urlPatterns = "/hello")
public class HelloServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("HelloServlet.service");
        System.out.println("request = " + request);
        System.out.println("response = " + response);

        String username = request.getParameter("username");
        System.out.println("username = " + username);

        response.setContentType("text/plain");
        response.setCharacterEncoding("utf-8");
        response.getWriter().write("hello " + username);
    }
}
```

기본적으로 실행되기위한 코드는 위와같이 작성이 될 수 있다.

분석을 해보자면 

- `@WebServlet`
    - `name`: 서블릿 이름
    서블릿 컨테이너 내에서 서블릿을 구별하는데 사용된다.
    - `urlPatterns`: URL 매핑 
    실제로 접속할 url을 설정할 파트 `<IP>:<PORT>/<urlPatterns>`

해당 값들은 중복으로 들어가서는 안된다.

- `protected void service(HttpServletRequest request, HttpServletResponse response)`
    - `service()` : 클라이언트로 부터 오는 모든 유형의 HTTP 요청을 받는다.
    GET, POST, PUT, DELETE 등 을 모두 처리한다.
    예를 들어 클라이언트가 보낸 하나에 메소드 처리하고 싶다면 `doGet` 이러한 형식으로 처리할 수 있다.
    - `request` : 클라이언트를 통해 받은 데이터
     클라이언트 측에서 `?username=123` 이와 같이 데이터를 보내왔다면
    `request.getParameter("username")` 이러한 형식으로 클라이언트가 보낸 데이터를 확인할 수 있다.
    - `response` : 클라이언트에게 반환을 해줄 변수
    클라이언트에게 최종적으로 반환을 해줄 데이터를 이야기한다. 여기에 어떠한 값을 담느냐에 따라서 클라이언트에게 전달되는 값이 달라진다.

- `response.setContentType("text/plain");
response.setCharacterEncoding("utf-8");
response.getWriter().write("hello " + username);`
    - 클라이언트에게 데이터를 보낼때 어떠한 값을 헤더에 추가할건지 바디는 어떻게 적어서 보낼건지 추가적인 설정을 해줄 수 있다.

## **HttpServletRequest**

http 요청 메세지를 개발자가 직접 파싱에서 사용하면 복잡하고 불편하기때문에 해당 과정을 쉽게 할수록 서블릿이라는 것이 http요청 메시지를 편리하게 사용할 수 있도록 대신 해준다.

- **START LINE**
    - HTTP 메소드
    - URL
    - 쿼리 스트링
    - 스키마, 프로토콜
- **헤더**
    - 헤더 조회
- **바디**
    - form 파라미터 형식 조회
    - message body 데이터 직접 조회

### **임시 저장소 기능**

해당 HTTP 요청이 시작부터 끝날 때 까지 유지되는 임시 저장소 기능

- 저장: `request.setAttribute(name, value)`
- 조회: `request.getAttribute(name)`

### **세션 관리 기능**

- `request.getSession(create: true)`

위와 같은 서블릿을 사용하기전에 당연히 http에 대한 기본지식이 있어야한다.

**HTTP 스펙이 제공하는 요청, 응답 메시지 자체를 이해**해야 한다.

### 헤더 조회

```java
@WebServlet(name = "requestHeaderServlet", urlPatterns = "/request-header")
public class RequestHeaderServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        printStartLine(request);
    }

    private static void printStartLine(HttpServletRequest request) {
        System.out.println("--- REQUEST-LINE - start ---");
        System.out.println("request.getMethod() = " + request.getMethod()); //GET
        System.out.println("request.getProtocol() = " + request.getProtocol()); // HTTP/1.1
        System.out.println("request.getScheme() = " + request.getScheme()); //http // http://localhost:8080/request-header System.out.println("request.getRequestURL() = " + request.getRequestURL());
        // /request-header
        System.out.println("request.getRequestURI() = " + request.getRequestURI()); //username=hi
        System.out.println("request.getQueryString() = " + request.getQueryString());
        System.out.println("request.isSecure() = " + request.isSecure()); //https 사용 유무
        System.out.println("--- REQUEST-LINE - end ---");
        System.out.println();
    }
}
```

```java
--- REQUEST-LINE - start ---
request.getMethod() = GET
request.getProtocol() = HTTP/1.1
request.getScheme() = http
request.getRequestURI() = /request-header
request.getQueryString() = null
request.isSecure() = false
--- REQUEST-LINE - end ---
```

위와 같은 코드를 실행해서 테스트작업을 해보면 아래와 같은 테스트 결과가 나오게 된다.
위와 같이 request 내부에서 지원해주는 메소드를 사용하여 클라이언트가 보내온 정보들을 확인하는 것이 가능하다.

대부분 조회를 할때 위와 같은 뉘양스로 코드를 작성하므로 필요한 헤더가 있다면 찾아보기.

## http 요청 데이터

http 요청 메시지를 통해서 클라이언트에서 서버로 데이터를 전달하는 방식에는 다양한 방식들이 존재하는데
아래와 같이 크게 3가지로 나누어진다고한다.

### 방법

- **GET - 쿼리 파라미터**
    - /url**?username=hello&age=20**
    - 메시지 바디 없이, URL의 쿼리 파라미터에 데이터를 포함해서 전달
    - 예) 검색, 필터, 페이징등에서 많이 사용하는 방식
- **POST - HTML Form**
    - content-type: application/x-www-form-urlencoded
    - 메시지 바디에 쿼리 파리미터 형식으로 전달 username=hello&age=20
    - 예) 회원 가입, 상품 주문, HTML Form 사용
- **HTTP message body**에 데이터를 직접 담아서 요청
    - HTTP API에서 주로 사용, JSON, XML, TEXT
- 데이터 형식은 주로 JSON 사용
    - POST, PUT, PATCH
    

### GET - 쿼리 파라미터

주로 검색을 할때 사용되는 방식

클라이언트측에서는 서버에게 GET 메소드를 사용해서 데이터를 요청할때
`http://localhost:8080/request-param?username=hello&age=20`

위와 같이 쿼리 파라미터를 사용하는 경우가 종종 있다. 그럴때 서버 측에서는 클라이언트가 보낸 쿼리 파라미터의 값을 어떻게 받을 수 있는가에 대한 다양한 예제를 다뤄보자.

**전체 파라미터 조회**

```java
System.out.println("[전체 파라미터 조회] - start");
request.getParameterNames().asIterator()
                .forEachRemaining(paramName -> System.out.println(paramName + "= " + request.getParameter(paramName)));
System.out.println("[전체 파라미터 조회] - end");
```

위에서는 클라이언트가 보낸 파라미터들을 key값 상관 없이 모두 다 조회를 진행한다.

`getParameterNames` 라는 키워드가 이제 핵심 키워드인데 해당 키워드는 클라이언트가 보낸 쿼리 파라미터를 `Enumeration<String>` 타입으로 변환을 시켜주는 역할을 한다.

그러고 가져온 값들을 `asIterator` 를 통해 `Iterator` ****타입으로 변환시키고,
`forEachRemaining` 를 사용해서 람다식을 이용해 값들을 뽑아 출력해주는 역할을 한다.

**단일 파라미터 조회**

```java
System.out.println("[단일 파라미터 조회] - start");
String username = request.getParameter("username");
String age = request.getParameter("age");
System.out.println("username = " + username);
System.out.println("age = " + age);
System.out.println("[단일 파라미터 조회] - end");
```

클라이언트가 보낸 값들 중에 내가 원하는 key값을 사용해서 특정 값만 조회할려면 위와 같은 방법을 사용할 수 있다.

`getParameter()` 방식으로 알고있는 key값을 넣어 그것에 해당하는 value를 가져오는 방식

key에 해당하는 값이 존재하지않는다면 `null` 로 출력이 이루어진다.

**복수 파라미터 조회**

```java
System.out.println("[이름이 같은 복수 파라미터 조회] - start");
String[] usernames = request.getParameterValues("username");
for (String name : usernames) {
    System.out.println("username = " + name);
}
System.out.println("[이름이 같은 복수 파라미터 조회] - end");
```

프로그램을 설계할때 

`http://localhost:8080/request-param?username=hello&username=world`

위와 같이 파라미터를 중복으로 사용하는 경우도 간혹 있다고 한다. 그럴경우 서버에서 중복되는 파라미터의 값을 모두 가져올려면 위와 같은 방식을 사용할 수 있다고한다.

`getParameterValues()` 방식으로 알고 있는 key값을 넣어 그것에 해당되는 값을 배열로 가져오고,
for문을 통해 전체를 조회하는 방식

### HTTP 요청 데이터 - POST HTML Form

주로 회원가입, 상품 주문 등에서 사용되는 방식

**`application/x-www-form-urlencoded`**

```java
content-type: application/x-www-form-urlencoded
message body: username=hello&age=20
```

형식으로 POST 메소드 정보를 서버에게 보낼 경우에는 위와 같이 받는 데이터의 형식이 GET으로 받았던 쿼리파라미터랑 형식이 똑같기 때문에 위에서 GET을 테스트하기위해서 작성한 쿼리파라미터의 코드를 그대로 사용해도 똑같이 동작이 이루어진다.

```java
[전체 파라미터 조회] - start
username= hello
age= 20
[전체 파라미터 조회] - end

[단일 파라미터 조회] - start
username = hello
age = 20
[단일 파라미터 조회] - end

[이름이 같은 복수 파라미터 조회] - start
username = hello
[이름이 같은 복수 파라미터 조회] - end
```

### HTTP 요청데이터 - API 메시지 바디 ( 단순 텍스트 )

클라이언트 측에서 POST를 사용하여 body에 데이터를 보내올때 서버측에서는 아래와 같은 방식을 사용해서 
데이터를 조회할 수 있다. ( 잘 사용하지는 않음 )

```java
...
ServletInputStream inputStream = request.getInputStream();
String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
System.out.println("messageBody = " + messageBody);
...
```

- `request.getInputStream();`

클라이언트가 보낸 body의 데이터를 읽어오는 메서드이다.
위와 같이 해당 메서드를 호출하면 요청 바디에 담긴 모든 데이터를 가져올 수 있다.

- `StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);`

위에서 `request.getInputStream()` 통해 받아온 데이터를 실제로 확인을 할려면 `StreamUtils.copyToString`을 사용해서 데이터를 확인할 수 있는데 값을 꺼내어 가져올때에는 인코딩 방식을 선택하여 `StandardCharsets.UTF_8` 같은 형식으로 지정을 해줘야 String 형식으로 꺼내오는것이 가능하다. ( 가장 많이 사용되는 방식이 `UTF_8` )

### HTTP 요청데이터 - API 메시지 바디 ( JSON )

가장 핵심이 되고 가장 많이 사용되는 데이터 통신 방식

```java
content-type: application/json
message body: {"username": "hello", "age": 20}
```

```java
...
ServletInputStream inputStream = request.getInputStream();
String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
System.out.println("messageBody = " + messageBody);
...
```

```java
messageBody = {"username": "hello", "age": 20}
```

json형식이라고해서 데이터를 가져오는 방식이 크게 차이가 나는것은 아니다.
json에 어찌되었든 text 형식이기때문에 위에서 사용했던 “API 메시지 바디 ( 단순 텍스트 )” 방식을 사용하면 위와 같이 json 데이터를 가져올 수 있다.

그치만 값을 가져오는 거 자체는 위와 같이 간단하게 가져오는 것이 가능한데 실제로 사용하기 위해서는 Jackson, Gson 같은 JSON 변환 라이브러리를 추가해서 사용해야한다.

```java
@Getter @Setter
public class HelloData {

    private String username;
    private int age;
}
```

```java
    ...	
		private final ObjectMapper objectMapper = new ObjectMapper();

		protected void service(...) {
        ...	
        HelloData helloData = objectMapper.readValue(messageBody, HelloData.class);

        System.out.println("helloData.username = " + helloData.getUsername());
        System.out.println("helloData.age = " + helloData.getAge());
        ...
    }
```

위의 방식은 spring boot에 기본으로 내장이 되어있는 Jackson을 사용한 방식으로 json에 해당하는 데이터는 위와 같이 사용할 수 있다.

`objectMapper.readValue(messageBody, HelloData.class);`

위에서 가져온 `messageBody` 값과 대입을 시킬 `class(HelloData.class)` 를  넣어주게 되면 `readValue` 에서 알아서 `setter` 함수를 찾고 알맞게 대입을 시켜준다.

그래서 `helloData.getUsername()` 이러한 코드를 사용해서 데이터를 뽑는게 가능하다.

## **HttpServletResponse**

### 역할

**HTTP 응답 메시지 생성**

- HTTP 응답코드 지정
- 헤더 생성
- 바디 생성

**편의 기능 제공**

- Content-Type, 쿠키, Redirect

### 옵션 설정

```java
...
// [status-line]
response.setStatus(HttpServletResponse.SC_OK);

// [response-headers]
response.setHeader("Content-Type", "text/plain;charset=utf-8");
response.setHeader("Cache-Control", "no-cache no-store, must-revalidate");
response.setHeader("Pragma", "no-cache");
response.setHeader("my-header", "hello");
...
```

클라이언트에게 데이터를 보낼때 status는 무엇을 할건지, header에는 어떠한 정보를 담을건지 설정을 해줄 수 있는데 위와 같은 방식으로 설정해주는것이 가능하다.

`**response.setStatus(HttpServletResponse.SC_OK)**`

클라이언트에게 결과물을 보낼때 해당 결과가 200인지, 400인지 등 status code값을 같이 보내줘야하는데

위와 같이 값을 보내줄 수 있다. 직접 `response.setStatus(200)` 같이 데이터를 적어줘도 되지만 

`HttpServletResponse` 안에 내장되어있는 `status code`에 해당되는 값을 사용하는 것을 추천한다.

`**response.setHeader(key, value)**`

클라이언트에게 데이터를 보낼때 어떠한 헤더를 같이 보낼건지 설정을 해서 넣어줄 수 있다.

위와 같은 방식을 사용해서 헤더를 추가가능.

하지만 위와같이 직접 `**setHeader**`를 설정하는 방식은 잘 사용하지않고

```java
//response.setHeader("Content-Type", "text/plain;charset=utf-8");
response.setContentType("text/plain");
response.setCharacterEncoding("utf-8");
```

위와 같이 그것에 맞게 해당되는 header함수를 사용해서 값을 넣어주는 방식을 더 많이 사용한다고한다.

### HTTP 응답데이터 - 단순 텍스트, HTML

```java
protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    // Content-type: text/html;charset=utf-8
    response.setContentType("text/html");
    response.setCharacterEncoding("utf-8");

    PrintWriter writer = response.getWriter();
    writer.println("<html>");
    writer.println("<body>");
    writer.println("  <div>안녕?</div>");
    writer.println("</body>");
    writer.println("</html>");
}
```

위와 같은 방식을 사용해서 응답 데이터를 직접 넣어서 클라이언트에게 전달할 수 있다.

HTTP 응답으로 HTML을 반환할 때는 `content-type`을 `text/html`로 지정해야 한다.

### HTTP 응답데이터 - API JSON

```java
	...
	private ObjectMapper objectMapper = new ObjectMapper();
	...
		protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		    // Content-Type: application/json
		    response.setContentType("application/json");
		    response.setCharacterEncoding("utf-8");
		
		    HelloData helloData = new HelloData();
		    helloData.setUsername("hello");
		    helloData.setAge(20);
		
		    // {"username": "hello", "age":20}
		    String result = objectMapper.writeValueAsString(helloData);
		    response.getWriter().write(result);
		}
```

받은 데이터를 json형식으로 바꾸어 가공해서 반환을 해주는 방식이다.

HTTP 응답으로 JSON을 반환할 때는 `content-type`을 `application/json` 로 지정해야 한다.

`objectMapper.writeValueAsString(helloData);` 

`objectMapper`안에 있는 `writeValueAsString()`을 사용해서 class를 json형식으로 반환을 하는것이 가능하다.