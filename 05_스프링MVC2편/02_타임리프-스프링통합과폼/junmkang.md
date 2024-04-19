# 2. 타임리프 - 스프링 통합과 폼

## 타임리프 스프링 통합

**스프링 통합으로 추가되는 기능들**

- 스프링의 SpringEL 문법 통합
- `${@myBean.doSomething()}` 처럼 스프링 빈 호출 지원
- 편리한 폼 관리를 위한 추가 속성
    - `th:object` (기능 강화, 폼 커맨드 객체 선택)
    - `th:field` , `th:errors` , `th:errorclass`
- 폼 컴포넌트 기능
    - checkbox, radio button, List 등을 편리하게 사용할 수 있는 기능 지원
- 스프링의 메시지, 국제화 기능의 편리한 통합
- 스프링의 검증, 오류 처리 통합
- 스프링의 변환 서비스 통합(ConversionService)

## 입력 폼 처리

```java
@GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "form/addForm";
}
```

```html
    <form action="item.html" th:action th:object="${item}" method="post">
        <div>
            <label for="itemName">상품명</label>
            <input type="text" id="itemName" th:field="*{itemName}" class="form-control" placeholder="이름을 입력하세요">
        </div>
        <div>
            <label for="price">가격</label>
            <input type="text" id="price" th:field="*{price}" class="form-control" placeholder="가격을 입력하세요">
        </div>
        <div>
            <label for="quantity">수량</label>
            <input type="text" id="quantity" th:field="*{quantity}" class="form-control" placeholder="수량을 입력하세요">
        </div>
			...
		</form>
```

타임 리프에서 제공해주는 문법을 사용하면 위와 같은 구조 형식으로 변경이 가능하다.

- `th:object` : 커맨드 객체를 지정한다.
- `*{...}` : 선택 변수 식이라고 한다. `th:object` 에서 선택한 객체에 접근한다.
    - 원래는 GET을 통해서 모델을 전달할때 받은 객체에서 해당 값을 조회할려면`th:field="${item.quantity}”` 이와 같이 접근을 해야하는데. 타임리프에서 지원해주는 문법을 사용하면 `*{...}` 형식으로 조금 더 편하게 해당 객체에 접근하는 것이 가능해진다.
- `th:field`
    - HTML 태그의 `id` , `name` , `value` 속성을 자동으로 처리해준다.
    ( `id`, `name`, `value`들을 생략하여도 `th:field`를 통해 자동으로 값을 넣어준다. )

### 리팩토링 전

```html
<form action="item.html" th:action method="post">
    <div>
        <label for="id">상품 ID</label>
        <input type="text" id="id" name="id" class="form-control" value="1" th:value="${item.id}" readonly>
    </div>
    
    ...
    
</form>
```

### 리팩토링 후

```html
<form action="item.html" th:action th:object="${item}" method="post">
    <div>
        <label for="id">상품 ID</label>
        <input type="text" id="id" class="form-control" th:field="*{id}" readonly>
    </div>
    
	  ...
	  
</form>
```

## 요구사항 추가

- 판매 여부
    - 판매 오픈 여부
    - 체크 박스로 선택할 수 있다.
- 등록 지역
    - 서울, 부산, 제주
    - 체크 박스로 다중 선택할 수 있다.
- 상품 종류
    - 도서, 식품, 기타
    - 라디오 버튼으로 하나만 선택할 수 있다.
- 배송 방식
    - 빠른 배송
    - 일반 배송
    - 느린 배송
    - 셀렉트 박스로 하나만 선택할 수 있다.

## 체크 박스 - 단일1

```html
<form action="item.html" th:action th:object="${item}" method="post">
	...
	<div class="form-check">
	    <input type="checkbox" id="open" name="open" class="form-check-input">
	    <label for="open" class="form-check-label">판매 오픈</label>
	</div>
	...
</form>
```

위와 같은 코드에 대해서 이야기를 해보자면 위에 코드는 간단한 체크박스를 만드는 html 코드이다.
저렇게 작성한 경우 클라이언트가 데이터를 보낼때 체크박스에 상태에 따라 체크박스에 해당하는 데이터를 같이 보내게 되는데.

```html
FormItemController : item.open=true //체크 박스를 선택하는 경우 
FormItemController : item.open=null //체크 박스를 선택하지 않는 경우
```

클라이언트가 체크박스를 체크한 후에 form을 보내면 `&open=true` 이와 같이 데이터를 같이 보내게 되고,
클라이언트가 체크박스를 체크안하고 form을 보내면 아무런 데이터를 보내지않는다. 
아무런 데이터도 보내지않으면 서버측에 `open` 필드에는 `NULL` 이라는 값이 담기게 되는데 이부분에서 문제점이 발생된다.

클라이언트로 부터 체크박스에 `NULL`이라는 값이 담겨서 넘어오게되면
서버측에서는 이게 사용자가 체크를 안해서 `NULL`이 온건지, 아니면 오류로 인해 넘어오지않게 된건지 
이해하기가 어렵기때문에 서버 구현에 따라서 값이 넘어오지않은 것으로 판별하여 값을 변경하지 않을 수 있다.

### 해결방법

```html
<form action="item.html" th:action th:object="${item}" method="post">
	...
  <div class="form-check">
      <input type="checkbox" id="open" name="open" class="form-check-input">
      <input type="hidden" name="_open" value="on"/> <!-- 히든 필드 추가 -->
      <label for="open" class="form-check-label">판매 오픈</label>
  </div>
	...
</form>
```

위와 같이 히든필드를 추가하는 방식을 사용할 수 있다.

```
FormItemController : item.open=true //체크 박스를 선택하는 경우
FormItemController : item.open=false //체크 박스를 선택하지 않는 경우
```

**체크 박스 체크**
`open=on&_open=on`

체크 박스를 체크하면 스프링 MVC가 `open` 에 값이 있는 것을 확인하고 사용한다. 이때 `_open` 은 무시한다.

**체크 박스 미체크**
`_open=on`

체크 박스를 체크하지 않으면 스프링 MVC가 `_open` 만 있는 것을 확인하고, `open` 의 값이 체크되지 않았다고 인식한다.
이 경우 서버에서 `Boolean` 타입을 찍어보면 결과가 `null` 이 아니라 `false` 인 것을 확인할 수 있다.

`log.info("item.open={}", item.getOpen());`

해당과정은 체크박스를 만들때마다 `hidden` 필드를 넣어줘야한다는 불편함이 존재하기때문에 이를 편하게
해주는 방식으로 time leap에서 제공해주는 기능이 있다고한다.

## 체크 박스 - 단일2

```html
<form action="item.html" th:action th:object="${item}" method="post">
	...
    <div class="form-check">
        <input type="checkbox" id="open" th:field="*{open}" class="form-check-input">
        <label for="open" class="form-check-label">판매 오픈</label>
    </div>
	...
</form>
```

타임리프에 방식은 상당히 간단하다.

`th:field="*{open}` 코드만 추가해주면 `hidden` 필드는 지우고 사용하는 것이 가능해진다.

## 체크 박스 - 멀티

### @ModelAttribute

```java
@ModelAttribute("regions")
public Map<String, String> regions() {
    Map<String, String> regions = new LinkedHashMap<>();
    regions.put("SEOUL", "서울");
    regions.put("BUSAN", "부산");
    regions.put("JEJU", "제주");
    return regions;
}
```

model에 추가해야하고, 중복되는 code를 위와 같이 `@ModelAttribute`로 등록을 진행해주게 되면
`model.addAttribute("regions", regions);` 이와 같은 코드 작업을 모든 컨트롤러에서도 사용할 수 있도록 제공해준다고한다.

`항상 추가해야하는 정보가 아니라면`
`컨트롤러에 따라 한줄만 적어도 추가하도록 하는것이 더 좋을 거 같다.`

### 실행코드

```html
<div>
    <div>등록 지역</div>
    <div th:each="region : ${regions}" class="form-check form-check-inline">
        <input type="checkbox" th:field="${item.regions}" th:value="${region.key}" class="form-check-input">
        <label th:for="${#ids.prev('regions')}"
               th:text="${region.value}" class="form-check-label">서울</label>
    </div>
</div>
```

### 결과

```html
<!-- multi checkbox -->
<div>
<div>등록 지역</div>
	<div class="form-check form-check-inline">
		<input type="checkbox" value="SEOUL" class="form-check-input" id="regions1" name="regions">
		<input type="hidden" name="_regions" value="on"/> 
		<label for="regions1" class="form-check-label">서울</label>
	</div>
	<div class="form-check form-check-inline">
		<input type="checkbox" value="BUSAN" class="form-check-input" id="regions2" name="regions">
		<input type="hidden" name="_regions" value="on"/> 
		<label for="regions2" class="form-check-label">부산</label>
	</div>
	<div class="form-check form-check-inline">
		<input type="checkbox" value="JEJU" class="form-check-input" id="regions3" name="regions">
		<input type="hidden" name="_regions" value="on"/> 
		<label for="regions3" class="form-check-label">제주</label>
	</div>
</div>
<!-- -->
```

`th:for="${#ids.prev('regions')}"`

멀티 체크박스는 같은 이름의 여러 체크박스를 만들 수 있다. 
그런데 문제는 이렇게 반복해서 HTML 태그를 생성할 때, 생성된 HTML 태그 속성에서 `name` 은 같아도 되지만, `id` 는 모두 달라야 한다. 
따라서 타임리프는 체크박스를 `each` 루프 안에서 반복해서 만들 때 임의로 `1` ,`2` ,`3` 숫자를 뒤에 붙여준다.

`코드 해석 더 필요!!!`

## 라디오 버튼

### ENUM

```java
public enum ItemType {

    BOOK("도서"), FOOD("음식"), ETC("기타");

    private final String description;

    ItemType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
```

### @ModelAttribute

```java
@ModelAttribute("itemTypes") 
public ItemType[] itemTypes() {
	return ItemType.values(); 
}
```

### 실행코드

```html
<!-- radio button -->
<div>
<div>상품 종류</div>
<div th:each="type : ${itemTypes}" class="form-check form-check-inline">
<input type="radio" th:field="*{itemType}" th:value="${type.name()}" class="form-check-input">
<label th:for="${#ids.prev('itemType')}" th:text="${type.description}" class="form-check-label">
             BOOK
         </label>
     </div>
 </div>
```

### 결과

```html
<!-- radio button -->
<div>
	<div>상품 종류</div>
	<div class="form-check form-check-inline">
		<input type="radio" value="BOOK" class="form-check-input" id="itemType1" name="itemType">
		<label for="itemType1" class="form-check-label">도서</label> 
	</div>
	<div class="form-check form-check-inline">
		<input type="radio" value="FOOD" class="form-check-input" id="itemType2" name="itemType" checked="checked">
		<label for="itemType2" class="form-check-label">식품</label>
	</div>
	<div class="form-check form-check-inline">
		<input type="radio" value="ETC" class="form-check-input" id="itemType3" name="itemType">
		<label for="itemType3" class="form-check-label">기타</label> 
	</div>
</div>
```

### enum 직접접근

```html
<div th:each="type : ${T(hello.itemservice.domain.item.ItemType).values()}">
```

타임리프에서는 enum에 경우에는 자바 객체에 직접접근할 수 있도록 문법을 지원해준다고 하긴하는데.
불편해서 잘 사용하지는 않음

## 셀렉트 박스

```java
/**
 * FAST: 빠른 배송
 * NORMAL: 일반 배송
 * SLOW: 느린 배송
 */
@Data
@AllArgsConstructor
public class DeliveryCode {

    private String code;
    private String displayName;
}
```

### 실행코드

```html
<!-- SELECT -->
<div>
    <div>배송 방식</div>
    <select th:field="${item.deliveryCode}" class="form-select" disabled>
        <option value="">==배송 방식 선택==</option>
        <option th:each="deliveryCode : ${deliveryCodes}" th:value="${deliveryCode.code}"
                th:text="${deliveryCode.displayName}">FAST</option>
    </select>
</div>
```

### 결과

```html
<!-- SELECT -->
<div>
	<DIV>배송 방식</DIV>
	<select class="form-select" id="deliveryCode" name="deliveryCode">
		<option value="">==배송 방식 선택==</option> 
		<option value="FAST">빠른 배송</option> 
		<option value="NORMAL">일반 배송</option> 
		<option value="SLOW">느린 배송</option>
	</select>
</div>
```