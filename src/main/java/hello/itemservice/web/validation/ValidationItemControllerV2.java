package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/validation/v2/items")
@RequiredArgsConstructor
public class ValidationItemControllerV2 {

    private final ItemValidator itemValidator;
    private final ItemRepository itemRepository;

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v2/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "validation/v2/addForm";
    }


//    @PostMapping("/add")
    public String addItemV1(@ModelAttribute Item item,BindingResult bindingResult, RedirectAttributes redirectAttribute, Model model ) {


        //검증 로직
        if(!StringUtils.hasText(item.getItemName())){//itemName에 값이 없으면
            bindingResult.addError(new FieldError("item","itemName","상품이름은 필수입니다.")); //modelAttribute에 담기는 그 model값
        }
        if(item.getPrice()==null || item.getPrice()>1000000){
            bindingResult.addError(new FieldError("item","price","가격은1,000 ~ 1,000,000까지 허용합니다.")); //modelAttribute에 담기는 그 model값
        }
        if(item.getQuantity()==null||item.getQuantity()>=9999){
            bindingResult.addError(new FieldError("item","quantity","수량은 최대 9,999까지 가능합니다.")); //modelAttribute에 담기는 그 model값
        }

        //특정 필드가 아닌 복합 필드 검증
        if(item.getPrice()!=null && item.getQuantity()!=null){
            int resultPrice = item.getPrice()*item.getQuantity();
            if(resultPrice<10000){
                bindingResult.addError(new ObjectError("item","가격 * 수량의 합은 10,000원 이상이어야 합니다"));
            }
        }

        //검증에 실패하면 다시 입력 폼으로
        if(bindingResult.hasErrors()){//에러가 있으면
            log.info("errors={}",bindingResult);
        //bindingresult는 model에 안담아도 자동으로 넘어감
            return "validation/v2/addForm";
        }


        Item savedItem = itemRepository.save(item);
        redirectAttribute.addAttribute("itemId", savedItem.getId());
        redirectAttribute.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }
//    @PostMapping("/add")
    public String addItemV4(@ModelAttribute Item item,BindingResult bindingResult, RedirectAttributes redirectAttribute, Model model ) {

        log.info("target ={}", bindingResult.getTarget());
        log.info("objectName = {}",bindingResult.getObjectName());
        //검증 로직
        if(!StringUtils.hasText(item.getItemName())){//itemName에 값이 없으면


            bindingResult.rejectValue("itemName","required");

        }
        if(item.getPrice()==null || item.getPrice()>1000000){

            bindingResult.rejectValue("price","range",new Object[]{1000,1000000},null);
        }
        if(item.getQuantity()==null||item.getQuantity()>=9999){

            bindingResult.rejectValue("quantity","max",new Object[]{9999},null);
        }

        //특정 필드가 아닌 복합 필드 검증
        if(item.getPrice()!=null && item.getQuantity()!=null){
            int resultPrice = item.getPrice()*item.getQuantity();
            if(resultPrice<10000){
                bindingResult.reject("totalPriceMin",new Object[]{10000,resultPrice},null);
            }
        }

        //검증에 실패하면 다시 입력 폼으로
        if(bindingResult.hasErrors()){//에러가 있으면
            log.info("errors={}",bindingResult);
            //bindingresult는 model에 안담아도 자동으로 넘어감
            return "validation/v2/addForm";
        }


        Item savedItem = itemRepository.save(item);
        redirectAttribute.addAttribute("itemId", savedItem.getId());
        redirectAttribute.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }
    @PostMapping("/add")
    public String addItemV5(@ModelAttribute Item item, BindingResult bindingResult,
                            RedirectAttributes redirectAttributes) {
        itemValidator.validate(item, bindingResult);
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "validation/v2/addForm";
        }
        //성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

//    @PostMapping("/add")
//    public String addItemV3(@ModelAttribute Item item,BindingResult bindingResult, RedirectAttributes redirectAttribute, Model model ) {
//
//        //검증 로직
//        if(!StringUtils.hasText(item.getItemName())){//itemName에 값이 없으면
//
//            bindingResult.addError(new FieldError("item","itemName",item.getItemName(),false,new String[]{"required.item.itemName"},null,null)); //modelAttribute에 담기는 그 model값
//        }
//        if(item.getPrice()==null || item.getPrice()>1000000){
//            bindingResult.addError(new FieldError("item","price",item.getPrice(),false,new String[]{"range.item.price"},new Object[]{1000,1000000},null)); //modelAttribute에 담기는 그 model값
//        }
//        if(item.getQuantity()==null||item.getQuantity()>=9999){
//            bindingResult.addError(new FieldError("item","quantity",item.getQuantity(),false,new String[]{"max.item.quantity"},new Object[]{9999},null)); //modelAttribute에 담기는 그 model값
//        }
//
//        //특정 필드가 아닌 복합 필드 검증
//        if(item.getPrice()!=null && item.getQuantity()!=null){
//            int resultPrice = item.getPrice()*item.getQuantity();
//            if(resultPrice<10000){
//                bindingResult.addError(new ObjectError("item",new String[]{"totalPriceMin"},new Object[]{10000, resultPrice},null));
//            }
//        }
//
//        //검증에 실패하면 다시 입력 폼으로
//        if(bindingResult.hasErrors()){//에러가 있으면
//            log.info("errors={}",bindingResult);
//            //bindingresult는 model에 안담아도 자동으로 넘어감
//            return "validation/v2/addForm";
//        }
//
//
//        Item savedItem = itemRepository.save(item);
//        redirectAttribute.addAttribute("itemId", savedItem.getId());
//        redirectAttribute.addAttribute("status", true);
//        return "redirect:/validation/v2/items/{itemId}";
//    }
//    @PostMapping("/add")
    public String addItemV2(@ModelAttribute Item item,BindingResult bindingResult, RedirectAttributes redirectAttribute, Model model ) {

        itemValidator.validate(item, bindingResult);


        //검증에 실패하면 다시 입력 폼으로
        if(bindingResult.hasErrors()){//에러가 있으면
            log.info("errors={}",bindingResult);
            //bindingresult는 model에 안담아도 자동으로 넘어감
            return "validation/v2/addForm";
        }


        Item savedItem = itemRepository.save(item);
        redirectAttribute.addAttribute("itemId", savedItem.getId());
        redirectAttribute.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }


    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/validation/v2/items/{itemId}";
    }

}

