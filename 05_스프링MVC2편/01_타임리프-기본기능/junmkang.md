# 1. 타임리프 - 기본 기능

[https://start.spring.io/](https://start.spring.io/)

- 프로젝트 선택
    - Project: **Gradle - Groovy** Project
    - Language: Java
    - Spring Boot: 3.x.x
- Project Metadata
    - Group: hello
    - Artifact: thymeleaf-basic
    - Name: thymeleaf-basic
    - Package name: **hello.thymeleaf**
    - Packaging: **Jar**
    - Java: 17
- Dependencies: **Spring Web**, **Lombok** , **Thymeleaf**

## 타임리프 소개

- 서버 사이드 HTML 렌더링  (SSR)
- 네츄럴 템플릿
- 스프링 통합 지원

### 서버 사이드 HTML 렌더링  (SSR)

백엔드 서버에서 HTML을 동적으로 렌더링 하는 용도

### 네츄럴 템플릿

순수 타임리프는 순수 HTML을 최대한 유지하는 특징이 있다.
타임리프로 작성한 파일은 HTML을 유지하기 때문에 웹 브라우저에서 파일을 직접 열어도 내용을 확인할 수 있고, 서버를 통해 뷰 템플릿을 거치면 동적으로 변경된 결과를 확인할 수 있다.
JSP를 포함한 다른 뷰 템플릿들은 해당 파일을 열면, 예를 들어서 JSP 파일 자체를 그대로 웹 브라우저에서 열어보면 JSP 소스코드와 HTML이 뒤죽박죽 섞여서 웹 브라우저에서 정상적인 HTML 결과를 확인할 수 없다. 오직 서버를 통해서 JSP가 렌더링 되고 HTML 응답 결과를 받아야 화면을 확인할 수 있다.
반면에 타임리프로 작성된 파일은 해당 파일을 그대로 웹 브라우저에서 열어도 정상적인 HTML 결과를 확인할 수 있다. 물론 이 경우 동적으로 결과가 렌더링 되지는 않는다. 하지만 HTML 마크업 결과가 어떻게 되는지 파일만 열어도 바로 확인할 수 있다.
이렇게 **순수 HTML을 그대로 유지하면서 뷰 템플릿도 사용할 수 있는 타임리프의 특징을 네츄럴 템플릿**(natural
templates)이라 한다.

### 스프링 통합 지원

타임리프는 스프링과 자연스럽게 통합되고, 스프링의 다양한 기능을 편리하게 사용할 수 있게 지원한다.

**타임리프 사용 선언**

`<html xmlns:th="http://www.thymeleaf.org">`

```java
• 간단한 표현:
	◦ 변수 표현식: ${...}
	◦ 선택 변수 표현식: *{...}
	◦ 메시지 표현식: #{...}
	◦ 링크 URL 표현식: @{...}
	◦ 조각 표현식: ~{...}
• 리터럴
	◦ 텍스트: 'one text', 'Another one!',...
	◦ 숫자: 0, 34, 3.0, 12.3,...
	◦ 불린: true, false
	◦ 널: null
	◦ 리터럴 토큰: one, sometext, main,...
• 문자 연산:
	◦ 문자합치기:+
	◦ 리터럴 대체: |The name is ${name}|
• 산술 연산:
	◦ Binary operators: +, -, *, /, %
	◦ Minus sign (unary operator): -
• 불린 연산:
  ◦ Binary operators: and, or
	◦ Boolean negation (unary operator): !, not 
• 비교와 동등:
	◦ 비교:>,<,>=,<=(gt,lt,ge,le)
	◦ 동등 연산: ==, != (eq, ne) 
• 조건 연산:
	◦ If-then: (if) ? (then)
	◦ If-then-else: (if) ? (then) : (else)
	◦ Default: (value) ?: (defaultvalue)
• 특별한 토큰:
	◦ No-Operation: _
```

## 텍스트 - text, utext

### Escape

```html
<ul>
    <li>th:text 사용 <span th:text="${data}"></span></li>
    <li>컨첸츠 안에서 직접 출력하기 = [[${data}]]</li>
</ul>
```

`<` → `&lt;`

`>` → `&gt;`

`<, >` 문자를 출력하기위해서 위와 같이 escape처리를 하여 문자를 변환하는 방식을 사용한다.

그래서  `"Hello <b>Spring!</b>"` 이라는 문자를 출력해보면  문자 그대로 `"Hello <b>Spring!</b>"` 가 나오는 것을 확인해 볼 수 있다.

### Unescape

`th:text` → `th:utext`

`[[...]]` → `[(...)]`

escape를 허용하지않을려면 위와 같이 unescape 문법을 사용하면 허용시킬 수 있다.
 `"Hello <b>Spring!</b>"` →  `"Hello **Spring!**"` 형식으로 출력값이 나오게 된다.

## 변수 - SpringEL

변수 표현식 : `${...}`

- **Object**
`user.username` : user의 username을 프로퍼티 접근 `user.getUsername()`
`user['username']` : 위와 같음 `user.getUsername()`
`user.getUsername()` : user의 `getUsername()` 을 직접 호출
- **List**
`users[0].username` : List에서 첫 번째 회원을 찾고 username 프로퍼티 접근
`list.get(0).getUsername()`
`users[0]['username']` : 위와 같음
`users[0].getUsername()` : List에서 첫 번째 회원을 찾고 메서드 직접 호출
- **Map**
`userMap['userA'].username` : Map에서 userA를 찾고, username 프로퍼티 접근
`map.get("userA").getUsername()`
`userMap['userA']['username']` : 위와 같음
`userMap['userA'].getUsername()` : Map에서 userA를 찾고 메서드 직접 호출

### 지역변수

```html
<h1>지역 변수 - (th:with)</h1>
<div th:with="first=${users[0]}">
    <p>처음 사람의 이름은 <span th:text="${first.username}"></span></p>
</div>
```

위와 같이 지역변수를 사용해서 선언하는 방식도 가능 
지역 변수는 선언한 테그 안에서만 사용할 수 있다.

## 기본 객체들

```html
...
<body>

<h1>식 기본 객체 (Expression Basic Objects)</h1>
<ul>
    <li>request = <span th:text="${request}"></span></li>
    <li>response = <span th:text="${response}"></span></li>
    <li>session = <span th:text="${session}"></span></li>
    <li>servletContext = <span th:text="${servletContext}"></span></li>
    <li>locale = <span th:text="${#locale}"></span></li>
</ul>

<h1>편의 객체</h1>
<ul>
    <li>Request Parameter = <span th:text="${param.paramData}"></span></li>
    <li>session = <span th:text="${session.sessionData}"></span></li>
    <li>spring bean = <span th:text="${@helloBean.hello('Spring!')}"></span></li>
</ul>

</body>
...
```

이런 점을 해결하기 위해 편의 객체도 제공한다.

- HTTP 요청 파라미터 접근: `param`
예) `${param.paramData}`
- HTTP 세션 접근: `session`
예) `${session.sessionData}`
- 스프링 빈 접근: `@`
예) `${@helloBean.hello('Spring!')}`

## 유틸리티 객체와 날짜

**타임리프 유틸리티 객체들**

- `#message` : 메시지, 국제화 처리
- `#uris` : URI 이스케이프 지원
- `#dates` : `java.util.Date` 서식 지원
- `#calendars` : `java.util.Calendar` 서식 지원
- `#temporals` : 자바8 날짜 서식 지원
- `#numbers` : 숫자 서식 지원
- `#strings` : 문자 관련 편의 기능
- `#objects` : 객체 관련 기능 제공
- `#bools` : boolean 관련 기능 제공
- `#arrays` : 배열 관련 기능 제공
- `#lists` , `#sets` , `#maps` : 컬렉션 관련 기능 제공
- `#ids` : 아이디 처리 관련 기능 제공, 뒤에서 설명

**타임리프 유틸리티 객체**

[https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html#expression-utility-objects](https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html#expression-utility-objects)

**유틸리티 객체 예시**

[https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html#expression-utility-objects](https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html#expression-utility-objects)

```html
...
<h1>LocalDateTime</h1>
<ul>
    <li>default = <span th:text="${localDateTime}"></span></li>
    <li>yyyy-MM-dd HH:mm:ss = <span th:text="${#temporals.format(localDateTime, 'yyyy-MM-dd HH:mm:ss')}"></span></li>
</ul>
...
```

## URL 링크

```html
...
<!-- {param1: data1, param2: data2} 정의 -->
<!-- /hello  -->
<li><a th:href="@{/hello}">basic url</a></li>
<!-- /hello?param1=data1&param2=data2 -->
<li><a th:href="@{/hello(param1=${param1}, param2=${param2})}">hello query param</a></li>
<!-- /hello/data1/data2 -->
<li><a th:href="@{/hello/{param1}/{param2}(param1=${param1}, param2=${param2})}">path variable</a></li>
<!-- /hello/data1?param2=data2 -->
<li><a th:href="@{/hello/{param1}(param1=${param1}, param2=${param2})}">path variable + query parameter</a></li>
...
```

**단순한 URL**

- `@{/hello}`
    - → `/hello`

**쿼리 파라미터**

- `@{/hello(param1=${param1}, param2=${param2})}`
    - → `/hello?param1=data1&param2=data2`
    - `()` 에 있는 부분은 쿼리 파라미터로 처리된다.

**경로 변수**

- `@{/hello/{param1}/{param2}(param1=${param1}, param2=${param2})}`
    - → `/hello/data1/data2`
    - URL 경로상에 변수가 있으면 `()` 부분은 경로 변수로 처리된다.

**경로 변수 + 쿼리 파라미터**

- `@{/hello/{param1}(param1=${param1}, param2=${param2})}`
    - → `/hello/data1?param2=data2`
    - 경로 변수와 쿼리 파라미터를 함께 사용할 수 있다.

## 리터럴

리터럴은 소스 코드상에 고정된 값을 말하는 용어이다.
예를 들어서 다음 코드에서 `"Hello"` 는 문자 리터럴, `10` , `20` 는 숫자 리터럴이다.

```
String a = "Hello" 
int a = 10 * 20
```

- 문자: `'hello'`
- 숫자: `10`
- 불린: `true` , `false`
- null: `null`

타임리프에서 문자 리터럴은 항상 `'` (작은 따옴표)로 감싸야 한다.
`<span th:text="'hello'">`

그런데문자를항상 `'` 로감싸는것은너무귀찮은일이다.
공백없이쭉이어진다면하나의의미있는토큰으로인지해서 다음과 같이 작은 따옴표를 생략할 수 있다.
룰: `A-Z` , `a-z` , `0-9` , `[]` , `.` , `-` , `_`

`<span th:text="hello">`

**오류**
`<span th:text="hello world!"></span>`

문자 리터럴은 원칙상 `'` 로 감싸야 한다. 중간에 공백이 있어서 하나의 **의미있는 토큰으로도 인식되지 않는다.**

**수정**
`<span th:text="'hello world!'"></span>`

이렇게 `'` 로 감싸면 정상 동작한다.

### 예시

```html
...
<ul>
    <!--주의! 다음 주석을 풀면 예외가 발생함-->
    <!--    <li>"hello world!" = <span th:text="hello world!"></span></li>-->
    <li>'hello' + ' world!' = <span th:text="'hello' + ' world!'"></span></li>
    <li>'hello world!' = <span th:text="'hello world!'"></span></li>
    <li>'hello ' + ${data} = <span th:text="'hello ' + ${data}"></span></li>
    <li>리터럴 대체 |hello ${data}| = <span th:text="|hello ${data}|"></span></li>
</ul>
...
```

## 연산

- **비교연산**: HTML 엔티티를 사용해야 하는 부분을 주의하자,
`>` (gt), `<` (lt), `>=` (ge), `<=` (le), `!` (not), `==` (eq), `!=` (neq, ne)
- **조건식**: 자바의 조건식과 유사하다.
- **Elvis 연산자**: 조건식의 편의 버전
- **No-Operation**: `_` 인 경우 마치 타임리프가 실행되지 않는 것 처럼 동작한다. 
이것을 잘 사용하면 HTML의 내용 그대로 활용할 수 있다. 마지막 예를 보면 “데이터가 없습니다.” 부분이 그대로 출력된다.

## 속성 값 설정

```html
<h1>속성 설정</h1>
<input type="text" name="mock" th:name="userA" />

<h1>속성 추가</h1>
- th:attrappend = <input type="text" class="text" th:attrappend="class=' large'" /><br/>
- th:attrprepend = <input type="text" class="text" th:attrprepend="class='large '" /><br/>
- th:classappend = <input type="text" class="text" th:classappend="large" /><br/>

<h1>checked 처리</h1>
- checked o <input type="checkbox" name="active" th:checked="true" /><br/>
- checked x <input type="checkbox" name="active" th:checked="false" /><br/>
- checked=false <input type="checkbox" name="active" checked="false" /><br/>
```

**속성 설정**
`th:*` 속성을 지정하면 타임리프는 기존 속성을 `th:*` 로 지정한 속성으로 대체한다. 

기존 속성이 없다면 새로 만든다.
`<input type="text" name="mock" th:name="userA" />`

→ 타임리프 렌더링 후 `<input type="text" name="userA" />`

**속성 추가**

- `th:attrappend` : 속성 값의 뒤에 값을 추가한다.
- `th:attrprepend` : 속성 값의 앞에 값을 추가한다.
- `th:classappend` : class 속성에 자연스럽게 추가한다.

**checked 처리**

HTML에서는 `<input type="checkbox" name="active" checked="false" />`
→ 이 경우에도 checked 속성이 있기 때문에 checked 처리가 되어버린다.

HTML에서 `checked` 속성은 `checked` 속성의 값과 상관없이 `checked` 라는 속성만 있어도 체크가 된다. 이런 부분이 `true` , `false` 값을 주로 사용하는 개발자 입장에서는 불편하다.

타임리프의 `th:checked` 는 값이 `false` 인 경우 `checked` 속성 자체를 제거한다.

`<input type="checkbox" name="active" th:checked="false" />`
→ 타임리프 렌더링 후: `<input type="checkbox" name="active" />`

## 반복

타임리프에서 반복은 `th:each` 를 사용한다. 추가로 반복에서 사용할 수 있는 여러 상태 값을 지원한다.

```html
    ...
  <tr th:each="user, userStat : ${users}">
    <td th:text="${userStat.count}">username</td>
    <td th:text="${user.username}">username</td>
    <td th:text="${user.age}">0</td>
    <td>
      index = <span th:text="${userStat.index}"></span>
      count = <span th:text="${userStat.count}"></span>
      size = <span th:text="${userStat.size}"></span>
      even? = <span th:text="${userStat.even}"></span>
      odd? = <span th:text="${userStat.odd}"></span>
      first? = <span th:text="${userStat.first}"></span>
      last? = <span th:text="${userStat.last}"></span>
      current = <span th:text="${userStat.current}"></span>
    </td>
    ...
```

**반복 기능**
`<tr th:each="user : ${users}">`

반복시 오른쪽 컬렉션( `${users}` )의 값을 하나씩 꺼내서 왼쪽 변수( `user` )에 담아서 태그를 반복 실행합니다.
`th:each` 는 `List` 뿐만 아니라 배열, `java.util.Iterable` , `java.util.Enumeration` 을 구현한 모든 객체를 반복에 사용할 수 있습니다. `Map` 도 사용할 수 있는데 이 경우 변수에 담기는 값은 `Map.Entry`입니다.

**반복 상태 유지**
`<tr th:each="user, userStat : ${users}">`

반복의 두번째 파라미터를 설정해서 반복의 상태를 확인 할 수 있습니다.
두번째 파라미터는 생략 가능한데, 생략하면 지정한 변수명( `user` ) + `Stat` 가 됩니다.
여기서는 `user` + `Stat` = `userStat` 이므로 생략 가능합니다.

**반복 상태 유지 기능**

- `index` : 0부터 시작하는 값
- `count` : 1부터 시작하는 값
- `size` : 전체 사이즈
- `even` , `odd` : 홀수, 짝수 여부( `boolean` )
- `first` , `last` :처음, 마지막 여부( `boolean` )
- `current` : 현재 객체
`BasicController.User(username=UserC, age=30)`

python에 반복문 문법이랑 상당히 흡사하다.

## 조건부 평가

타임리프의 조건식
`if` , `unless` ( `if` 의 반대)

### `if`, `unless`

```html
...
<span th:text="'미성년자'" th:if="${user.age lt 20}"></span>
<span th:text="'미성년자'" th:unless="${user.age ge 20}"></span>
...
```

- ***if, unless***
타임리프는 해당 조건이 맞지 않으면 태그 자체를 렌더링하지 않는다.
만약 다음 조건이 `false` 인 경우 `<span>...<span>` 부분 자체가 렌더링 되지 않고 사라진다. 
`<span th:text="'미성년자'" th:if="${user.age lt 20}"></span>`

### `switch`

```html
...
<td th:switch="${user.age}">
    <span th:case="10">10살</span>
    <span th:case="20">20살</span>
    <span th:case="*">기타</span>
</td>
...
```

- ***switch***
`*` 은 만족하는 조건이 없을 때 사용하는 디폴트이다.

## 주석

주석을 표현하는 다양한 방법 및 예시

### 출력

```html
<h1>예시</h1>
<span th:text="${data}">html data</span>

<h1>1. 표준 HTML 주석</h1>
<!--
<span th:text="${data}">html data</span>
-->

<h1>2. 타임리프 파서 주석</h1>
<!--/* [[${data}]] */-->

<!--/*-->
<span th:text="${data}">html data</span>
<!--*/-->

<h1>3. 타임리프 프로토타입 주석</h1>
<!--/*/
<span th:text="${data}">html data</span>
/*/-->
```

### 결과

```html
<h1>예시</h1> 
<span>Spring!</span>

<h1>1. 표준 HTML 주석</h1>
<!--
<span th:text="${data}">html data</span> 
-->

<h1>2. 타임리프 파서 주석</h1> 

<h1>3. 타임리프 프로토타입 주석</h1>
<span>Spring!</span>
```

1. **표준 HTML 주석**
자바스크립트의 표준 HTML 주석은 타임리프가 렌더링 하지 않고, 그대로 남겨둔다.
2. **타임리프 파서 주석**
타임리프 파서 주석은 타임리프의 진짜 주석이다. 렌더링에서 주석 부분을 제거한다.
3. **타임리프 프로토타입 주석**
타임리프 프로토타입은 약간 특이한데, HTML 주석에 약간의 구문을 더했다.
**HTML 파일**을 웹 브라우저에서 그대로 열어보면 HTML 주석이기 때문에 이 부분이 웹 브라우저가 렌더링하지 않는다.
**타임리프 렌더링**을 거치면 이 부분이 정상 렌더링 된다.
쉽게 이야기해서 HTML 파일을 그대로 열어보면 주석처리가 되지만, 타임리프를 렌더링 한 경우에만 보이는 기능이다.

## 블록

 `<th:block>` 은 HTML 태그가 아닌 타임리프의 유일한 자체 태그다.

```html
...
<th:block th:each="user : ${users}">
    <div>
        사용자 이름1 <span th:text="${user.username}"></span>
        사용자 나이1 <span th:text="${user.age}"></span>
    </div>
    <div>
        요약 <span th:text="${user.username} + ' / ' + ${user.age}"></span>
    </div>
</th:block>
...
```

model만을 반복하는것이 아니라 block안에 있는 태그 자체를 반복하여 출력해준다.

타임리프의 특성상 HTML 태그안에 속성으로 기능을 정의해서 사용하는데, 위 예처럼 이렇게 사용하기 애매한 경우에 사용하면 된다. `<th:block>` 은 렌더링시 제거된다.

## 자바스크립트 인라인

타임리프는 자바스크립트에서 타임리프를 편리하게 사용할 수 있는 자바스크립트 인라인 기능을 제공한다. 자바스크립트 인라인 기능은 다음과 같이 적용하면 된다.
`<script th:inline="javascript">`

### 인라인 사용 전

```html
<!-- 자바스크립트 인라인 사용 전 -->
<script>

    var username = [[${user.username}]];
    var age = [[${user.age}]];

    //자바스크립트 내추럴 템플릿
    var username2 = /*[[${user.username}]]*/ "test username";

    //객체
    var user = [[${user}]];
</script>
```

### 결과

```html
<script>
var username = userA; var age = 10;

//자바스크립트 내추럴 템플릿
var username2 = /*userA*/ "test username";

//객체
var user = BasicController.User(username=userA, age=10);
```

### 인라인 사용 후

```html
<!-- 자바스크립트 인라인 사용 후 -->
<script th:inline="javascript">
    var username = [[${user.username}]];
    var age = [[${user.age}]];

    //자바스크립트 내추럴 템플릿
    var username2 = /*[[${user.username}]]*/ "test username";

    //객체
    var user = [[${user}]];
</script>
```

### 결과

```html
자바스크립트 인라인 사용 후
<script>
var username = "userA"; 
var age = 10;

//자바스크립트 내추럴 템플릿 
var username2 = "userA";

//객체
var user = {"username":"userA","age":10};

</script>
```

**텍스트 렌더링**

`var username = [[${user.username}]];`

- 인라인 사용 전 `var username = userA;`
- 인라인 사용 후 `var username = "userA";`

**자바스크립트 내추럴 템플릿**
타임리프는 HTML 파일을 직접 열어도 동작하는 내추럴 템플릿 기능을 제공한다. 자바스크립트 인라인 기능을 사용하 면 주석을 활용해서 이 기능을 사용할 수 있다.

`var username2 = /*[[${user.username}]]*/ "test username";` 

- 인라인 사용 전 `var username2 = /*userA*/ "test username";`
- 인라인 사용 후 `var username2 = "userA";`

**객체**
타임리프의 자바스크립트 인라인 기능을 사용하면 객체를 JSON으로 자동으로 변환해준다.

`var user = [[${user}]];`

- 인라인 사용 전 `var user = BasicController.User(username=userA, age=10);`
- 인라인 사용 후 `var user = {"username":"userA","age":10};`
- 인라인 사용 전은 객체의 toString()이 호출된 값이다.
- 인라인 사용 후는 객체를 JSON으로 변환해준다.

### **자바스크립트 인라인** each

```html
<!-- 자바스크립트 인라인 each -->
<script th:inline="javascript">

    [# th:each="user, stat : ${users}"]
    var user[[${stat.count}]] = [[${user}]];
    [/]

</script>
```

### 결과

```html
<script>
var user1 = {"username":"userA","age":10}; 
var user2 = {"username":"userB","age":20}; 
var user3 = {"username":"userC","age":30};
</script>
```

## 템플릿 조각

반복되는 html 코드들을 사용하기 쉽게 템플릿화해서 가져다가 사용할 수 있는 기능을 지원해준다.

### 템플릿 조각

```html
<body>

<footer th:fragment="copy">
    푸터 자리 입니다.
</footer>

<footer th:fragment="copyParam (param1, param2)">
    <p>파라미터 자리 입니다.</p>
    <p th:text="${param1}"></p>
    <p th:text="${param2}"></p>
</footer>

</body>
```

### 실 사용

```html
<h2>부분 포함 insert</h2>
<div th:insert="~{template/fragment/footer :: copy}"></div>

<h2>부분 포함 replace</h2>
<div th:replace="~{template/fragment/footer :: copy}"></div>

<h2>부분 포함 단순 표현식</h2>
<div th:replace="template/fragment/footer :: copy"></div>

<h1>파라미터 사용</h1>
<div th:replace="~{template/fragment/footer :: copyParam ('데이터1', '데이터2')}"></div>
```

**부분 포함 insert**
`<div th:insert="~{template/fragment/footer :: copy}"></div>`

```html
<h2>부분 포함 insert</h2>
<div>
<footer>
푸터 자리 입니다.
</footer>
</div>
```

`th:insert` 를 사용하면 현재 태그( `div` ) 내부에 추가한다.

**부분 포함 replace**
`<div th:replace="~{template/fragment/footer :: copy}"></div>`

```html
<h2>부분 포함 replace</h2>
<footer>
푸터 자리 입니다.
</footer>
```

`th:replace` 를 사용하면 현재 태그( `div` )를 대체한다.

**부분 포함 단순 표현식**
`<div th:replace="template/fragment/footer :: copy"></div>`

```html
<h2>부분 포함 단순 표현식</h2>
<footer>
푸터 자리 입니다.
</footer>
```

 `~{...}` 를 사용하는 것이 원칙이지만 템플릿 조각을 사용하는 코드가 단순하면 이 부분을 생략할 수 있다.

**파라미터 사용**
다음과 같이 파라미터를 전달해서 동적으로 조각을 렌더링 할 수도 있다.

`<div th:replace="~{template/fragment/footer :: copyParam ('데이터1', '데이터2')}"></div>`

```html
<h1>파라미터 사용</h1>
<footer>
<p>파라미터 자리 입니다.</p>
<p>데이터1</p>
<p>데이터2</p>
</footer>
```

`footer.html` 의 `copyParam`부분

```html
<footer th:fragment="copyParam (param1, param2)">
<p>파라미터 자리 입니다.</p>
<p th:text="${param1}"></p>
<p th:text="${param2}"></p>
</footer>
```

## 템플릿 레이아웃1

이전에는 일부 코드 조각을 가지고와서 사용했다면, 이번에는 개념을 더 확장해서 코드 조각을 레이아웃에 넘겨서 사용 하는 방법에 대해서 알아보자.

### layout

```html
<head th:fragment="common_header(title,links)">

    <title th:replace="${title}">레이아웃 타이틀</title>

    <!-- 공통 -->
    <link rel="stylesheet" type="text/css" media="all" th:href="@{/css/awesomeapp.css}">
    <link rel="shortcut icon" th:href="@{/images/favicon.ico}">
    <script type="text/javascript" th:src="@{/sh/scripts/codebase.js}"></script>

    <!-- 추가 -->
    <th:block th:replace="${links}" />

</head>
```

### 실 사용

```html
...
<head th:replace="template/layout/base :: common_header(~{::title},~{::link})">
    <title>메인 타이틀</title>
    <link rel="stylesheet" th:href="@{/css/bootstrap.min.css}">
    <link rel="stylesheet" th:href="@{/themes/smoothness/jquery-ui.css}">
</head>
...
```

`common_header(~{::title},~{::link})` 이 부분이 핵심이다.
`::title` 은 현재 페이지의 title 태그들을 전달한다.
`::link` 는 현재 페이지의 link 태그들을 전달한다.

**결과를 보자.**

- 메인 타이틀이 전달한 부분으로 교체되었다.
- 공통 부분은 그대로 유지되고, 추가 부분에 전달한 `<link>` 들이 포함된 것을 확인할 수 있다.

layout의 기본 형태는 그대로 유지한 상태로 내가 설명을 바꾸고 싶은 부분만 위와 같이 변경하는 것이 가능하다.
훨씬 더 유연하게 사용하는 방식.

`common_header(~{::title},~{::link})` 방식을 사용하면 설정한 범위안에서 `title` 이라는 태그랑 `link` 라는 이름의 태그를 알아서 찾아서 전달해준다.
위에 코드를 보면 `title`은 기존폼에서 치환를 하는 방식으로 설계가 되어있고,
`link` 에 경우에는 `th:block` 으로 되어있기때문에 `link`의 개수만큼 `block`생성을 진행한다.

## 템플릿 레이아웃2

조금 더 확장에서 아래 코드와 같이 html 자체를 layout으로 만들어서 사용하는 것이 가능하다고한다.

### layout

```html
<!DOCTYPE html>
<html th:fragment="layout (title, content)" xmlns:th="http://www.thymeleaf.org">
<head>
    <title th:replace="${title}">레이아웃 타이틀</title>
</head>
<body>
<h1>레이아웃 H1</h1>
<div th:replace="${content}">
    <p>레이아웃 컨텐츠</p>
</div>
<footer>
    레이아웃 푸터
</footer>
</body>
</html>
```

### 실 사용

```html
<!DOCTYPE html>
<html th:replace="~{template/layoutExtend/layoutFile :: layout(~{::title}, ~{::section})}"
      xmlns:th="http://www.thymeleaf.org">
<head>
    <title>메인 페이지 타이틀</title>
</head>
<body>
<section>
    <p>메인 페이지 컨텐츠</p>
    <div>메인 페이지 포함 내용</div>
</section>
</body>
</html>
```

`fragment방식이랑 layout이랑 같이 사용하는 것도 가능할까? 테스트해보기`