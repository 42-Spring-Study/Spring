# 5. 검증2 - Bean Validation

## Bean Validation - 소개

검증 기능을 지금처럼 매번 코드로 작성하는 것은 상당히 번거롭다.

특정 필드에 대한 검증 로직은 대부분 빈값인지 아닌지, 특정 크기를 넘는지 아닌지와 같이 매우 일반적인 로직이다.

```java
public class Item {

		private Long id;
		
		@NotBlank
		private String itemName;
		
		@NotNull
		@Range(min = 1000, max = 1000000)
		private Integer price;
		
		@NotNull
		@Max(9999)
		private Integer quantity;
		...
}
```

**이런 검증 로직을 모든 프로젝트에 적용할 수 있게 공통화하고, 표준화 한 것이 바로 Bean Validation 이다. Bean Validation을 잘 활용하면, 애노테이션 하나로 검증 로직을 매우 편리하게 적용할 수 있다.**

## **Bean Validation 이란?**

먼저 Bean Validation은 특정한 구현체가 아니라 Bean Validation 2.0(JSR-380)이라는 기술 표준이다. 쉽게 이야기해서 검증 애노테이션과 여러 인터페이스의 모음이다. 마치 JPA가 표준 기술이고 그 구현체로 하이버네이트가 있는것과 같다.

Bean Validation을 구현한 기술중에 일반적으로 사용하는 구현체는 하이버네이트 Validator이다. 이름이 하이버네이트가 붙어서 그렇지 ORM과는 관련이 없다.

## Bean Validation - 시작

Bean Validation **의존관계 추가**

**의존관계 추가**
Bean Validation을 사용하려면 다음 의존관계를 추가해야 한다.

`build.gradle`

```
implementation 'org.springframework.boot:spring-boot-starter-validation'
```

**Bean Validation 사용 방법**

```java

public class Item {

    private Long id;

    @NotBlank
    private String itemName;

    @NotNull
    @Range(min = 1000, max = 1000000)
    private Integer price;

    @NotNull
    @Max(9999)
    private Integer quantity;
		...
}
```

`@NotBlank` : 빈값 + 공백만 있는 경우를 허용하지 않는다.
`@NotNull` : `null` 을 허용하지 않는다.
`@Range(min = 1000, max = 1000000)` : 범위 안의 값이어야 한다.
`@Max(9999)` : 최대 9999까지만 허용한다.

**test 코드**

```java
@Test
void beanValidation() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();

    Item item = new Item();
    item.setItemName(" ");
    item.setPrice(0);
    item.setQuantity(10000);

    Set<ConstraintViolation<Item>> violations = validator.validate(item);
    for (ConstraintViolation<Item> violation : violations) {
        System.out.println("violation = " + violation);
        System.out.println("violation.getMessage() = " + violation.getMessage());
    }
}
```

**검증 실행**

검증 대상( `item` )을 직접 검증기에 넣고 그 결과를 받는다. `Set` 에는 `ConstraintViolation` 이라는 검증 오류가 담긴다. 따라서 결과가 비어있으면 검증 오류가 없는 것이다.

```java
Set<ConstraintViolation<Item>> violations = validator.validate(item);
```

실행 결과

```java
violation = ConstraintViolationImpl{interpolatedMessage='9999 이하여야 합니다', propertyPath=quantity, rootBeanClass=class hello.itemservice.domain.item.Item, messageTemplate='{javax.validation.constraints.Max.message}'}
violation.getMessage() = 9999 이하여야 합니다
violation = ConstraintViolationImpl{interpolatedMessage='1000에서 1000000 사이여야 합니다', propertyPath=price, rootBeanClass=class hello.itemservice.domain.item.Item, messageTemplate='{org.hibernate.validator.constraints.Range.message}'}
violation.getMessage() = 1000에서 1000000 사이여야 합니다
violation = ConstraintViolationImpl{interpolatedMessage='공백일 수 없습니다', propertyPath=itemName, rootBeanClass=class hello.itemservice.domain.item.Item, messageTemplate='{javax.validation.constraints.NotBlank.message}'}
violation.getMessage() = 공백일 수 없습니다
```

getMessage에 경우에는 hibernate Validator에서 기본적으로 제공하는 오류메시지로 넣어준다고 한다.
원한다면 `@NotBlank(message = "공백X")` 이와 같이 변경 가능

## Bean Validation - 스프링 적용

기존코드에서 Bean Validation을 적용하는데에 있어서 추가적인 작업은 필요가 없다

```java
public class Item {

    private Long id;

    @NotBlank
    private String itemName;

    @NotNull
    @Range(min = 1000, max = 1000000)
    private Integer price;

    @NotNull
    @Max(9999)
    private Integer quantity;
    ...
}
```

```java
public String addItem(@Validated @ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
	...
}
```

위와 같이 사용할 객체에다가 조건 애노테이션들을 걸어주고, 해당 객체를 사용하는 위치에서 `@Validated`을 걸어주게되면 알아서 Bean Validation이 인식하여 검증처리를 진행한다.

**스프링 MVC는 어떻게 Bean Validator를 사용?**

스프링 부트가 `spring-boot-starter-validation` 라이브러리를 넣으면 자동으로 Bean Validator를 인지하 고 스프링에 통합한다.

**스프링 부트는 자동으로 글로벌 Validator로 등록한다.**

`LocalValidatorFactoryBean`을 글로벌 `Validator`로 등록한다. 이 `Validator`는`@NotNull`같은 애노테이션을 보고 검증을 수행한다. 이렇게 글로벌 `Validator`가 적용되어 있기 때문에 `@Valid`, `@Validated`만 적용하면 된다. 검증 오류가 발생하면 `FieldError`, `ObjectError`를 생성해서`BindingResult` 에 담아준다.

**주의**

만약에 직접 글로벌 Validator를 직접 등록하면 스프링 부트는 Bean Validator를 글로벌 `Validator` 로 등록
하지 않는다. 따라서 애노테이션 기반의 빈 검증기가 동작하지 않는다

**검증 순서**

1. `@ModelAttribute` 각각의 필드에 타입 변환 시도

1. 성공하면 다음으로

2. 실패하면 `typeMismatch` 로 `FieldError` 추가

2. Validator 적용

**바인딩에 성공한 필드만 Bean Validation 적용**
BeanValidator는 바인딩에 실패한 필드는 BeanValidation을 적용하지 않는다.
생각해보면 타입 변환에 성공해서 바인딩에 성공한 필드여야 BeanValidation 적용이 의미 있다.
(일단 모델 객체에 바인딩 받는 값이 정상으로 들어와야 검증도 의미가 있다.)

`@ModelAttribute` → 각각의필드타입변환시도 → **변환에성공한필드만 BeanValidation 적용**

**예)**

- `itemName` 에 문자 "A" 입력 → 타입 변환 성공 → `itemName` 필드에 BeanValidation 적용
- `price` 에 문자 "A" 입력 → "A"를 숫자 타입 변환 시도 실패 → typeMismatch FieldError 추가 → `**price`필드는 BeanValidation 적용 X**

## Bean Validation - 에러 코드

**@NotBlank**

- NotBlank.item.itemName
- NotBlank.itemName
- NotBlank.java.lang.String
- NotBlank

**@Range**

- Range.item.price
- Range.price
- Range.java.lang.Integer
- Range

**errors.properties** 

```java
#Bean Validation 추가 
NotBlank={0} 공백X 
Range={0}, {2} ~ {1} 허용 
Max={0}, 최대 {1}
```

`{0}` 은 필드명이고, `{1}` , `{2}` ...은 각 애노테이션 마다 다르다.

**BeanValidation 메시지 찾는 순서**

1. 생성된 메시지 코드 순서대로 `messageSource` 에서 메시지 찾기
2. 애노테이션의 `message` 속성 사용 → `@NotBlank(message = "공백! {0}")`
3. 라이브러리가 제공하는 기본 값 사용 → 공백일 수 없습니다.

## Bean Validation - 오브젝트 오류

지금까지는 필드에 경우에만 bean Validation으로 처리하는 방법을 알아봤었는데.
오브젝트 오류는 어떻게 할 수 있을까?

**오브젝트 오류 - bean Validation**

```java
@ScriptAssert(lang = "javascript", script = "_this.price * _this.quantity >= 10000")
public class Item {
		...
}
```

위와 같이 하는것이 가능하다고한다.

하지만 사용하기에는 너무 불편한 방식이라 그냥

```java
if (item.getPrice() != null && item.getQuantity() != null) {
    int resultPrice = item.getPrice() * item.getQuantity();
    if (resultPrice < 10000) {
        bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
    }
}
```

위에 코드와 같이 bean Validation을 사용하지 않고 그냥 쓰는 방식을 권장.

## Bean Validation - 수정에 적용

위에서 사용했던 방식을 응용해서 그대로 적용

`@Validated` , `BindingResult bindingResult` 등등

## Bean Validation - 한계

수정에도 추가적으로 bean Validation을 적용하는 것 까지는 완료하였는데.
수정과 등록에서 제한할 BeanValidation의 조건을 다르게 하고싶은경우에는 수정과 등록이 하나의 item객체를 공유해서 사용하기때문에 사이드 이펙트가 발생하게 된다. 이부분을 어떻게 해결할 수 있는가?

```java
public class Item {

    @NotNull // 등록시에는 id가 없으니 에러, 수정시에는 id가 필수가 되도록 하고싶음
    private Long id;
		...
}
```

## Bean Validation - groups

Bean Validation에서 제공해주는 방식으로 조건에 따라 groups을 분리하여 적용하는게 가능하다.

```java
public class Item {

    @NotNull(groups = UpdateCheck.class)
    private Long id;

    @NotBlank(groups = {SaveCheck.class, UpdateCheck.class})
    private String itemName;
    ...
}
```

위와 같이 UpdateCheck랑 SaveCheck라는 인터페이스를 만들고
각각의 그룹의 범위로 지정을 한 다음 실제로 해당 객체를 사용하는 장소에서 

```java
public String addItemV2(@Validated(SaveCheck.class) @ModelAttribute Item item, ...
```

`Validated`에 어떠한 조건을 가진 그룹을 바탕으로 사용할건지 지정이 가능하다.
그러면 지정된 그룹이 있는 조건만 가지고 검증을 진행한다.

`Validated` 에 경우에만 value를 넣을 수 있다. ( `valid`는 불가능 )

## Form 전송 객체 분리 - 소개

실제로 bean Validation에 따라서 객체를 분리하는 경우에는 group방식은 잘 사용하지않는다고한다.
그것에 대한 이유는 아래에 있다.

**폼 데이터 전달에 Item 도메인 객체 사용**

- `HTML Form -> Item -> Controller -> Item -> Repository`
    - 장점: Item 도메인 객체를 컨트롤러, 리포지토리 까지 직접 전달해서 중간에 Item을 만드는 과정이 없어서
    간단하다.
    - 단점: 간단한 경우에만 적용할 수 있다. 수정시 검증이 중복될 수 있고, groups를 사용해야 한다.

**폼 데이터 전달을 위한 별도의 객체 사용**

- `HTML Form -> ItemSaveForm -> Controller -> Item 생성 -> Repository`
    - 장점: 전송하는 폼 데이터가 복잡해도 거기에 맞춘 별도의 폼 객체를 사용해서 데이터를 전달 받을 수 있다.
    보통 등록과, 수정용으로 별도의 폼 객체를 만들기 때문에 검증이 중복되지 않는다.
    - 단점: 폼 데이터를 기반으로 컨트롤러에서 Item 객체를 생성하는 변환 과정이 추가된다.

### groups을 사용하지않는 이유

Item도메인 객체를 폼 전달 데이터로 사용하고, 그대로 쭉 넘기면 편리하겠지만, 앞에서 설명한 것과 같이 실무에서 는`Item`의 데이터만 넘어오는 것이 아니라 무수한 추가 데이터가 넘어온다. 
그리고 더 나아가서`Item` 을 생성하는데 필요한 추가 데이터를 데이터베이스나 다른 곳에서 찾아와야 할 수도 있다.
따라서 이렇게 폼 데이터 전달을 위한 별도의 객체를 사용하고, 등록, 수정용 폼 객체를 나누면 등록, 수정이 완전히 분리되기 때문에 `groups` 를 적용할 일은 드물다.

**Q: 이름은 어떻게 지어야 하나요?**
이름은 의미있게 지으면 된다. `ItemSave` 라고 해도 되고, `ItemSaveForm`,`ItemSaveRequest`,`ItemSaveDto` 등으로 사용해도 된다. 중요한 것은 일관성이다.

**Q: 등록, 수정용 뷰 템플릿이 비슷한데 합치는게 좋을까요?**
한 페이지에 그러니까 뷰 템플릿 파일을 등록과 수정을 합치는게 좋을지 고민이 될 수 있다. 각각 장단점이 있으므로 고민하는게 좋지만, 어설프게 합치면 수 많은 분기문(등록일 때, 수정일 때) 때문에 나중에 유지보수에서 고통을 맛본다.
이런 어설픈 분기문들이 보이기 시작하면 분리해야 할 신호이다.

## Form 전송 객체 분리 - 개발

실제로 사용할때는 Item이라는 객체를 사용하는 용도에 따라 `ItemSaveForm`, `ItemUpdateForm` 이라는 객체를 만들어서 사용하는 방식을 쓴다.

```java
@Data
public class ItemSaveForm {

    @NotBlank
    private String itemName;

    @NotNull
    @Range(min = 1000, max = 1000000)
    private Integer price;

    @NotNull
    @Max(value = 9999)
    private Integer quantity;
}
```

```java
@Data
public class ItemUpdateForm {

    @NotNull
    private Long id;

    @NotBlank
    private String itemName;

    @NotNull
    @Range(min = 1000, max = 1000000)
    private Integer price;

    // 수정에서는 수량은 자유롭게 변경할 수 있다.
    private Integer quantity;
}
```

위와 같은 코드가 나올 수 있게 되는데  save나 update등 사용용도에 따라 딱맞게 설계를 진행해서 사용하는게 가능해진다.
위에 코드에서도 볼 수 있듯이 `Bean Validation`의 조건을 상황에 따라 다르게 넣어줄 수도있고,
`Sava`에 경우에는 id라는 값을 따로 사용하지않으니 과감하게 빼고 사용하는 것도 가능하다.

### 사용코드

```java
public String addItem(@Validated @ModelAttribute("item") ItemSaveForm form, ...

			,,,

      Item item = new Item(form.getItemName(), form.getPrice(), form.getQuantity());

      Item savedItem = itemRepository.save(item);
      ...
}
```

내부적으로 코드로직은 위와같이 변경할 수 있다.

`@ModelAttribute("item")` 에서는 범용적으로 사용되어야하기때문에 `item` 이라는 이름으로 지정해서 넣어주고, 

```java
public Item save(Item item) {
    item.setId(++sequence);
    store.put(item.getId(), item);
    return item;
}
    
public void update(Long itemId, Item updateParam) {
		Item findItem = findById(itemId);
		findItem.setItemName(updateParam.getItemName());
		findItem.setPrice(updateParam.getPrice());
		findItem.setQuantity(updateParam.getQuantity());
}
```

실제로 데이터를 `save`하거나 `update`하는 부분에서는 `Item` 객체로 받기때문에 `Item`객체 형태로 변경후에 넘겨주는것이 필요하다.

## Bean Validation - HTTP 메시지 컨버터

`@Valid` , `@Validated` 는 `HttpMessageConverter` ( `@RequestBody` )에도 적용할 수 있다.

```java
public class ValidationItemApiController {

    @PostMapping("/add")
    public Object addItem(@RequestBody @Validated ItemSaveForm form, BindingResult bindingResult) {

        log.info("API 컨트롤러 호출 확인");

        if (bindingResult.hasErrors()) {
            log.info("검증 오류 발생 = {}", bindingResult);
            return bindingResult.getAllErrors();
        }

        log.info("성공 로직 실행");
        return form;
    }
}
```

**API의 경우 3가지 경우를 나누어 생각해야 한다.**

1. 성공 요청: 성공
2. 실패 요청: JSON을 객체로 생성하는 것 자체가 실패함
3. 검증 오류 요청: JSON을 객체로 생성하는 것은 성공했고, 검증에서 실패함

**@ModelAttribute vs @RequestBody**

HTTP 요청 파리미터를 처리하는 `@ModelAttribute` 는 각각의 필드 단위로 세밀하게 적용된다. 그래서 특정 필드에 타입이 맞지 않는 오류가 발생해도 나머지 필드는 정상 처리할 수 있었다.

`HttpMessageConverter` 는 `@ModelAttribute` 와 다르게 각각의 필드 단위로 적용되는 것이 아니라, 전체 객체 단위로 적용된다.
따라서 메시지 컨버터의 작동이 성공해서 `ItemSaveForm` 객체를 만들어야 `@Valid` , `@Validated` 가 적용된다.

`@ModelAttribute` 는 필드 단위로 정교하게 바인딩이 적용된다. 특정 필드가 바인딩 되지 않아도 나머지 필드
는 정상 바인딩 되고, Validator를 사용한 검증도 적용할 수 있다.
`@RequestBody` 는 HttpMessageConverter 단계에서 JSON 데이터를 객체로 변경하지 못하면 이후 단계 자
체가 진행되지 않고 예외가 발생한다. 컨트롤러도 호출되지 않고, Validator도 적용할 수 없다.

`TODO: 이 2개가 어떤건지 기억이 안나니 다시 공부`

**참고**
`HttpMessageConverter` 단계에서 실패하면 예외가 발생한다. 예외 발생시 원하는 모양으로 예외를 처리하는 방법은 예외 처리 부분에서 다룬다.