package hello.itemservice.domain.item;

import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.ScriptAssert;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
//@ScriptAssert(lang = "javascript", script = "_this.price * _this.quantity>=10000", message = "수량*가격은 10000원 이상이어야합니다")
public class Item {

//    @NotNull(groups = UpdateCheck.class)//수정요구사항
    private Long id;
    
//    @NotNull(groups = {SaveCheck.class, UpdateCheck.class})
    private String itemName;
    
//    @NotNull(groups = {SaveCheck.class, UpdateCheck.class})
//    @Range(min = 1000, max = 1000000)
        private Integer price;
    
//    @NotNull(groups = {SaveCheck.class, UpdateCheck.class})
//    @Max(value = 9999,groups = {SaveCheck.class})
    private Integer quantity;

    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
