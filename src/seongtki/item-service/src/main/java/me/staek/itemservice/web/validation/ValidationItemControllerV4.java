package me.staek.itemservice.web.validation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.staek.itemservice.domain.item.*;
import me.staek.itemservice.web.validation.dto.ItemSaveDto;
import me.staek.itemservice.web.validation.dto.ItemUpdateDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Slf4j
@Controller
@RequestMapping("/validation/v4/items")
@RequiredArgsConstructor
public class ValidationItemControllerV4 {

    private final ItemRepository repository;

    /**
     * 컨트롤러 클래스가 호출될 때마다 자동호출된다 (리팩토링 필요)
     */
    @ModelAttribute("regions")
    public Map<String, String> regions() {
        Map<String, String> regions = new LinkedHashMap<>();
        regions.put("SEOUL", "서울");
        regions.put("BUSAN", "부산");
        regions.put("JEJU", "제주");
        return regions;
    }

    /**
     * 컨트롤러 클래스가 호출될 때마다 자동호출된다 (리팩토링 필요)
     */
    @ModelAttribute("itemTypes")
    public ItemType[] itemTypes() {
        return ItemType.values();
    }

    /**
     * 컨트롤러 클래스가 호출될 때마다 자동호출된다 (리팩토링 필요)
     */
    @ModelAttribute("deliveryCodes")
    public List<DeliveryCode> deliveryCodes() {
        List<DeliveryCode> deliveryCodes = new ArrayList<>();
        deliveryCodes.add(new DeliveryCode("FAST", "빠른 배송"));
        deliveryCodes.add(new DeliveryCode("NORMAL", "일반 배송"));
        deliveryCodes.add(new DeliveryCode("SLOW", "느린 배송"));
        return deliveryCodes;
    }

    @GetMapping
    public String items(Model model) {
        List<Item> items = repository.findAll();
        model.addAttribute("items", items);
        return "validation/v4/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable Long itemId, Model model) {
        Optional<Item> finded = repository.findById(itemId);
        model.addAttribute("item", finded.get());
        return "validation/v4/item";
    }

    @GetMapping("/add")
    public String item(Model model) {
        model.addAttribute("item", new Item());
        return "validation/v4/addForm";
    }

    /**
     */
    @PostMapping("/add")
    public String addItem(@Validated @ModelAttribute("item") ItemSaveDto itemDto, BindingResult br, RedirectAttributes redirectAttributes) {

        // 복합 필드 검증
        if (itemDto.getPrice() != null && itemDto.getQuantity() != null) {
            int retPrice = itemDto.getPrice() * itemDto.getQuantity();
            if (retPrice < 10000)
                br.reject("totalPriceMin", new Object[]{10000, retPrice}, null);
        }

        // 검증 실패 시 입력폼으로 이동
        if (br.hasErrors()) {
            log.info("errors={}", br);
            return "validation/v4/addForm";
        }

        Item item = new Item(itemDto.getItemName(), itemDto.getPrice(), itemDto.getQuantity());
        Item saved = repository.save(item);
        redirectAttributes.addAttribute("itemId", saved.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v4/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, Model model) {
        Item finded = repository.findById(itemId).get();
        model.addAttribute("item", finded);
        return "validation/v4/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String doEdit2(@PathVariable Long itemId, @Validated @ModelAttribute("item") ItemUpdateDto itemDto, BindingResult br) {

        // 복합 필드 검증
        if (itemDto.getPrice() != null && itemDto.getQuantity() != null) {
            int retPrice = itemDto.getPrice() * itemDto.getQuantity();
            if (retPrice < 10000)
                br.reject("totalPriceMin", new Object[]{10000, retPrice}, null);
        }

        // 검증 실패 시 입력폼으로 이동
        if (br.hasErrors()) {
            log.info("errors={}", br);
            return "validation/v4/editForm";
        }

//        ItemUpdateDto item = new ItemUpdateDto(itemDto.getItemName(), itemDto.getPrice(), itemDto.getQuantity());
        repository.update(itemId, itemDto);
        return "redirect:/validation/v4/items/{itemId}";
    }
}
