package hello.itemservice.web.basic;

import hello.itemservice.item.Item;
import hello.itemservice.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.PostConstruct;
import java.util.List;

@Controller
@RequestMapping("/basic/items")
@RequiredArgsConstructor
public class BasicItemController {

    private final ItemRepository itemRepository;

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "basic/items.html";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "basic/item";
    }

    @GetMapping("/add")
    public String addForm() {
        return "basic/addForm";
    }

    // 같은 URL이지만 GET/POST로 역할을 구분했다.
    //@PostMapping("/add")
    public String saveV1(
            @RequestParam String itemName,
            @RequestParam int price,
            @RequestParam Integer quantity,
            Model model
    ) {
        Item item = new Item(itemName, price, quantity);
        itemRepository.save(item);

        model.addAttribute("item", item);

        return "basic/item";
    }

   // @PostMapping("/add")
    public String saveV2(@ModelAttribute("item") Item item, Model model
    ) {
        itemRepository.save(item);

      // 생략 가능  model.addAttribute("item", item);

        return "basic/item";
    }

    //@PostMapping("/add")
    public String saveV3(@ModelAttribute Item item
    ) {
        // 지정한 객체를 Item - > item 소문자로 이름바꿔서 자동으로 attribute에 추가한다
        itemRepository.save(item);

        return "basic/item";
    }

    @PostMapping("/add")
    public String saveV4( Item item, RedirectAttributes redirectAttributes
    ) {
        // 어노테이션도 요청 파라미터와 이름이 같은 객체/ 파라미터라면 어노테이션도 생략 가능하다
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);

        return "redirect:/basic/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute(item);
        return "basic/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable long itemId, @ModelAttribute Item item) {
       itemRepository.update(itemId,item);
        return "redirect:/basic/items/{itemId}";
        //컨트롤러에 매핑된 @PathVariable 의 값은 redirect 에도 사용 할 수 있다.
    }


    @PostConstruct
    void init() {

        itemRepository.save(new Item("item1", 1000, 100));
        itemRepository.save(new Item("item2", 1000, 100));
    }

}
