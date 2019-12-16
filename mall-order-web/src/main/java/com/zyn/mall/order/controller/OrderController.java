package com.zyn.mall.order.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.zyn.mall.annotations.LoginRequired;
import com.zyn.mall.api.bean.cart.OmsCartItem;
import com.zyn.mall.api.bean.order.OmsOrder;
import com.zyn.mall.api.bean.order.OmsOrderItem;
import com.zyn.mall.api.bean.user.UmsMemberReceiveAddress;
import com.zyn.mall.api.service.CartService;
import com.zyn.mall.api.service.OrderService;
import com.zyn.mall.api.service.PmsSkuInfoService;
import com.zyn.mall.api.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

    @Reference
    private PmsSkuInfoService skuService;

    @RequestMapping("submitOrder")
    @LoginRequired
    public String submitOrder(String tradeCode, String receiveAddressId, BigDecimal totalAmount, HttpServletRequest request, ModelMap modelMap) {

        String memberId = (String) request.getAttribute("memberId");
        String memberNickname = (String) request.getAttribute("memberNickname");

        //检查交易码的正确性
        String success = orderService.checkTradeCode(memberId, tradeCode);


        if (success.equals("success")) {

            //根据用户id获得要购买的商品详情列表(购物车)，和总价格
            List<OmsCartItem> omsCartItems = cartService.cartList(memberId);

            List<OmsOrderItem> omsOrderItems = new ArrayList<>();
            //订单对象
            OmsOrder omsOrder = new OmsOrder();
            omsOrder.setAutoConfirmDay(7);
            omsOrder.setCreateTime(new Date());
            omsOrder.setMemberUsername(memberNickname);
            omsOrder.setMemberId(memberId);
            omsOrder.setNote("订单备货");

            //外部订单号，用来防止和其他系统进行交互，防止重复
            String outTradeNo = "MuNanMall";
            outTradeNo = outTradeNo + System.currentTimeMillis();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String format = simpleDateFormat.format(new Date());
            outTradeNo = outTradeNo + format;

            omsOrder.setOrderSn(outTradeNo);
            omsOrder.setPayAmount(totalAmount);
            omsOrder.setOrderType(1);

            UmsMemberReceiveAddress umsMemberReceiveAddress =userService.getReceiveAddressById(receiveAddressId);
            omsOrder.setReceiverCity(umsMemberReceiveAddress.getCity());
            omsOrder.setReceiverDetailAddress(umsMemberReceiveAddress.getDetailAddress());
            omsOrder.setReceiverName(umsMemberReceiveAddress.getName());
            omsOrder.setReceiverPhone(umsMemberReceiveAddress.getPhoneNumber());
            omsOrder.setReceiverPostCode(umsMemberReceiveAddress.getPostCode());
            omsOrder.setReceiverProvince(umsMemberReceiveAddress.getProvince());
            omsOrder.setReceiverRegion(umsMemberReceiveAddress.getRegion());

            //当前日期加一天，一天后配送
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, 1);
            Date time = calendar.getTime();
            omsOrder.setReceiveTime(time);
            omsOrder.setSourceType(0);
            omsOrder.setStatus(0);
            omsOrder.setTotalAmount(totalAmount);

            for (OmsCartItem omsCartItem : omsCartItems) {
                if (omsCartItem.getIsChecked().equals("1")) {
                    //获得商品详情列表
                    OmsOrderItem omsOrderItem = new OmsOrderItem();

                    //检验价格
                    boolean orderPrice = skuService.checkPrice(omsCartItem.getProductSkuId(),omsCartItem.getPrice());
                    if (orderPrice == false) {
                        return "tradeFail";
                    }
                    //检验库存
                    omsOrderItem.setProductPic(omsCartItem.getProductPic());
                    omsOrderItem.setProductName(omsCartItem.getProductName());


                    omsOrderItem.setOrderSn(outTradeNo);//外部订单号，用来防止和其他系统进行交互，防止重复
                    omsOrderItem.setProductCategoryId(omsCartItem.getProductCategoryId());
                    omsOrderItem.setProductPrice(omsCartItem.getPrice());
                    omsOrderItem.setRealAmount(omsCartItem.getTotalPrice());
                    omsOrderItem.setProductQuantity(omsCartItem.getQuantity());
                    omsOrderItem.setProductSkuCode("商品条形码");
                    omsOrderItem.setProductSkuId(omsCartItem.getProductSkuId());
                    omsOrderItem.setProductId(omsCartItem.getProductId());
                    omsCartItem.setProductSn("222222");//仓库对应的商品编号 在仓库中的skuid

                    omsOrderItems.add(omsOrderItem);
                }
            }

            omsOrder.setOmsOrderItems(omsOrderItems);

            //将订单和订单详情写入到数据库
            //删除购物车的对应商品
            orderService.saveOrder(omsOrder);

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
