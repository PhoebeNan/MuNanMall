package com.zyn.mall.order.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.zyn.mall.annotations.LoginRequired;
import com.zyn.mall.api.bean.cart.OmsCartItem;
import com.zyn.mall.api.bean.order.OmsOrderItem;
import com.zyn.mall.api.bean.user.UmsMemberReceiveAddress;
import com.zyn.mall.api.service.CartService;
import com.zyn.mall.api.service.OrderService;
import com.zyn.mall.api.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhaoyanan
 * @create 2019-12-11-16:03
 */
@Controller
public class OrderController {

    @Reference
    private CartService cartService;

    @Reference
    private UserService userService;

    @Reference
    private OrderService orderService;

    @RequestMapping("submitOrder")
    @LoginRequired
    public String submitOrder(String tradeCode, String receiveAddressId, BigDecimal totalAmount, HttpServletRequest request, ModelMap modelMap) {

        String memberId = (String) request.getAttribute("memberId");

        //检查交易码的正确性
        String success = orderService.checkTradeCode(memberId,tradeCode);

        if (success.equals("success")) {

            //根据用户id获得要购买的商品详情列表(购物车)，和总价格


            //将订单和订单详情写入到数据库
            //删除购物车的对应商品


            //重定向到支付系统
            return null;
        } else {

            return "tradeFail";
        }

    }

    @RequestMapping("toTrade")
    @LoginRequired
    public String toTrade(HttpServletRequest request, ModelMap modelMap) {

        String memberId = (String) request.getAttribute("memberId");
        String memberNickname = (String) request.getAttribute("memberNickname");

        //将购物车集合转化为页面结算清单集合
        List<OmsCartItem> omsCartItems = cartService.cartList(memberId);


        List<OmsOrderItem> omsOrderItems = new ArrayList<>();

        for (OmsCartItem omsCartItem : omsCartItems) {

            if (omsCartItem.getIsChecked().equals("1")) {
                //每循环一次购物车商品对象，就封装一个商品详情到OmsOrderItem对象中
                OmsOrderItem omsOrderItem = new OmsOrderItem();
                omsOrderItem.setProductName(omsCartItem.getProductName());
                omsOrderItem.setProductPic(omsCartItem.getProductPic());
                omsOrderItem.setProductQuantity(omsCartItem.getQuantity());

                omsOrderItems.add(omsOrderItem);
            }
        }

        modelMap.put("omsOrderItems", omsOrderItems);

        //用户的收获地址
        List<UmsMemberReceiveAddress> receiveAddressByMemberId = userService.getReceiveAddressByMemberId(memberId);

        modelMap.put("receiveAddressByMemberId", receiveAddressByMemberId);
        modelMap.put("memberNickname", memberNickname);
        modelMap.put("totalAmount", getTotalAccount(omsCartItems));

        //生成交易码，并放入缓存服务器
        String tradeCode = orderService.genertorTradeCode(memberId);
        modelMap.put("tradeCode", tradeCode);

        return "trade";
    }

    /**
     * 返回勾选后的结算价格
     *
     * @param omsCartItems
     * @return
     */
    private BigDecimal getTotalAccount(List<OmsCartItem> omsCartItems) {

        BigDecimal bigDecimal = new BigDecimal("0");
        for (OmsCartItem omsCartItem : omsCartItems) {

            if (omsCartItem.getIsChecked().equals("1")) {

                bigDecimal = bigDecimal.add(omsCartItem.getTotalPrice());
            }
        }
        return bigDecimal;
    }

}
