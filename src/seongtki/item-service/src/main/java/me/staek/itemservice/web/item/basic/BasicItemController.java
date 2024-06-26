package me.staek.itemservice.web.item.basic;

import lombok.RequiredArgsConstructor;
import me.staek.itemservice.domain.item.Item;
import me.staek.itemservice.domain.item.ItemRepository;
import me.staek.itemservice.domain.item.MemoryItemRepository;
import me.staek.itemservice.web.validation.dto.ItemUpdateDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/basic/items")
@RequiredArgsConstructor
public class BasicItemController {

    private final ItemRepository repository;

    @GetMapping
    public String items(Model model) {
        List<Item> items = repository.findAll();
        model.addAttribute("items", items);
        return "basic/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable Long itemId, Model model) {
        Optional<Item> finded = repository.findById(itemId);
        model.addAttribute("item", finded);
        return "basic/item";
    }

    @GetMapping("/add")
    public String item() {
        return "basic/addForm";
    }

//    @PostMapping("/add")
    public String save(@RequestParam String itemName
            , @RequestParam Integer price
            , @RequestParam Integer quantity
            , Model model) {
        model.addAttribute("item", repository.save(new Item(itemName, price, quantity)));
        return "basic/item";
    }

    /**
     * 인자로 ModelAttribute를 받으면, Model에 Item객체를 추가하지 않아도 유지된다.
     */
//    @PostMapping("/add")
    public String save2(@ModelAttribute("item") Item item) {
        repository.save(item);
        return "basic/item";
    }

    /**
     * @ModelAttribute를 생략해도 사용자 객체라면 자동 적용된다.
     * (alias는 정할 수 없다 - 인자가그대로 키로 적용됨 (맨 앞글자만 소문자로 변경)
     */
//    @PostMapping("/add")
    public String save3(Item item, Model model) {
        repository.save(item);
        return "basic/item";
    }

    /**
     * 저장 후 새로고침 시 이전요청이 발생하는데 이 경우는 다시 post요청되므로,
     * 저장이후 get요청을 하게 하여 보다 안전하게 구성한다.
     *
     * 하지만 urlencode가 안된다는 단점이 존재함
     */
//    @PostMapping("/add")
    public String save4(Item item, Model model) {
        Item saved = repository.save(item);
        return "redirect:/basic/items/" + saved.getId();
    }

    /**
     * RedirectAttributes
     * - URL 인코딩도 해주고, pathVariable , 쿼리 파라미터까지 처리해준다
     *
     * ${param.status} - 타임리프에서 쿼리 파라미터를 편리하게 조회하는 기능
     * 원래는 컨트롤러에서 모델에 직접 담고 값을 꺼내야 하지만 쿼리파라미터는 자동지원됨.
     */
    @PostMapping("/add")
    public String save5(Item item, RedirectAttributes redirectAttributes) {
        Item saved = repository.save(item);
        redirectAttributes.addAttribute("itemId", saved.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/basic/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, Model model) {
        Optional<Item> finded = repository.findById(itemId);
        model.addAttribute("item", finded);
        return "basic/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String doEdit(@PathVariable Long itemId, ItemUpdateDto item) {
        repository.update(itemId, item);
        return "redirect:/basic/items/{itemId}";
    }
}
