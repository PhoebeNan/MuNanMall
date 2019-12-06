package com.zyn.mall.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.zyn.mall.annotations.LoginRequired;
import com.zyn.mall.api.bean.cart.OmsCartItem;
import com.zyn.mall.api.bean.sku.PmsSkuInfo;
import com.zyn.mall.api.service.CartService;
import com.zyn.mall.api.service.PmsSkuInfoService;
import com.zyn.mall.utils.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author zhaoyanan
 * @create 2019-11-30-10:56
 */
@Controller
@CrossOrigin
public class CartController {

    @Reference
    private PmsSkuInfoService pmsSkuInfoService;

    @Reference
    private CartService cartService;

    @RequestMapping("toTrade")
    @LoginRequired
    public String toTrade(HttpServletRequest request,ModelMap modelMap) {

        String memberId = (String) request.getAttribute("memberId");
        String memberNickname = (String) request.getAttribute("memberNickname");

        return "toTradeTest";
    }

    @RequestMapping("checkCart")
    @LoginRequired(loginSuccess = false)
    public String checkCart(HttpServletRequest request,String isChecked, String skuId, ModelMap modelMap) {

        //用户的id
        String memberId = "1";
        request.getAttribute("memberId");
        request.getAttribute("memberNickname");

        //调用服务，修改选中状态
        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setMemberId(memberId);
        omsCartItem.setProductSkuId(skuId);
        omsCartItem.setIsChecked(isChecked);

        cartService.checkCart(omsCartItem);

        //将最新的数据从缓存中查出，渲染到内嵌页
        List<OmsCartItem> omsCartItems = cartService.cartList(memberId);

        modelMap.put("cartList", omsCartItems);

        //结算总价格
        BigDecimal totalAccount = getTotalAccount(omsCartItems);
        modelMap.put("totalAccount", totalAccount);

        return "cartListInner";

    }

    @RequestMapping("cartList")
    @LoginRequired(loginSuccess = false)
    public String cartList(HttpServletRequest request, ModelMap modelMap) {

        List<OmsCartItem> omsCartItems = new ArrayList<>();
        //设置浏览器cookie中的键
        String cookieKey = "cartListCookie";

        //用户的id
        String memberId = "1";
        if (StringUtils.isNotBlank(memberId)) {

            //用户已经登录,调用购物车服务，从redis缓存中查询购物车信息
            omsCartItems = cartService.cartList(memberId);

        } else {

            //用户没有登录，从浏览器cookie中查询
            String cookieValue = CookieUtil.getCookieValue(request, cookieKey, true);
            if (StringUtils.isNotBlank(cookieValue)) {
                omsCartItems = JSON.parseArray(cookieValue, OmsCartItem.class);
            }
        }

        for (OmsCartItem omsCartItem : omsCartItems) {
            omsCartItem.setTotalPrice(omsCartItem.getPrice().multiply(omsCartItem.getQuantity()));
        }

        modelMap.put("cartList", omsCartItems);

        //结算总价格
        BigDecimal totalAccount = getTotalAccount(omsCartItems);
        modelMap.put("totalAccount", totalAccount);

        return "cartList";
    }

    /**
     * 返回勾选后的结算价格
     * @param omsCartItems
     * @return
     */
    private BigDecimal getTotalAccount(List<OmsCartItem> omsCartItems) {

        BigDecimal bigDecimal = new BigDecimal("0");
        for (OmsCartItem omsCartItem : omsCartItems) {

            if(omsCartItem.getIsChecked().equals("1")){

                bigDecimal = bigDecimal.add(omsCartItem.getTotalPrice());
            }
        }
        return bigDecimal;
    }

    @RequestMapping("addToCart")
    @LoginRequired(loginSuccess = false)
    public String addToCart(@RequestParam("skuId") String skuId, @RequestParam("quantity") int quantity,
                            HttpServletRequest request, HttpServletResponse response) {


        //创建集合，将购物车对象存入其中，并转换成json字符串
        List<OmsCartItem> omsCartItemList = new ArrayList<>();

        //调用商品服务查询商品sku信息
        PmsSkuInfo pmsSkuInfo = pmsSkuInfoService.skuInfoBySkuIdFromRedis(skuId, "");

        //将商品信息封装成购物车对象
        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setCreateDate(new Date());
        omsCartItem.setDeleteStatus(0);
        omsCartItem.setMemberNickname("会员昵称");
        omsCartItem.setModifyDate(new Date());
        omsCartItem.setPrice(pmsSkuInfo.getPrice());
        omsCartItem.setProductAttr("商品销售属性的json字符串");
        omsCartItem.setProductBrand("商品的商标");
        omsCartItem.setProductCategoryId(pmsSkuInfo.getCatalog3Id());
        omsCartItem.setProductId(pmsSkuInfo.getSpuId());
        omsCartItem.setProductName(pmsSkuInfo.getSkuName());
        omsCartItem.setProductPic(pmsSkuInfo.getSkuDefaultImg());
        omsCartItem.setProductSkuCode("商品sku条码");
        omsCartItem.setProductSkuId(pmsSkuInfo.getId());
        omsCartItem.setProductSn("商品sku的sn码");
        omsCartItem.setProductSubTitle("商品副标题（卖点）");
        omsCartItem.setQuantity(new BigDecimal(quantity));

        //判断用户是否登录
        String memberId = "1";

        //设置浏览器cookie中的键
        String cookieKey = "cartListCookie";

        if (StringUtils.isBlank(memberId)) {

            //用户没有登录


            //设置此cookie的过期时间
            int cookieMaxAge = 60 * 60 * 24 * 3;

            //cookie里原有的购物车数据
            String cookieValues = CookieUtil.getCookieValue(request, cookieKey, true);
            if (StringUtils.isNotBlank(cookieValues)) {

                //若添加的购物车对象在cookie列表中存在，则进行更新商品数量和价格
                List<OmsCartItem> cartItems = JSON.parseArray(cookieValues, OmsCartItem.class);
                //cookie里原有的购物车数据不为空
                boolean cartCookie = if_cart_cookie_exit(cartItems, omsCartItem);

                if (cartCookie) {

                    for (OmsCartItem cartItem : cartItems) {
                        if (cartItem.getProductSkuId().equals(omsCartItem.getProductSkuId())) {
                            //只有cookie中的商品id与先添加的商品id相等的情况下，才能增加购物车的总价格和数量
                            cartItem.setPrice(cartItem.getPrice().add(omsCartItem.getPrice()));
                            //增加数量
                            cartItem.setQuantity(cartItem.getQuantity().add(omsCartItem.getQuantity()));
                        }
                    }

                } else {
                    //若添加的购物车对象在cookie列表中不存在，则新增当前购物车
                    omsCartItemList.add(omsCartItem);
                }

            } else {
                //若cookie的值为空，则进行增加商品数据
                omsCartItemList.add(omsCartItem);
            }

            //创建集合，将购物车对象存入其中，并转换成json字符串
//            omsCartItemList.add(omsCartItem);
            String cookieValue = JSON.toJSONString(omsCartItemList);


            //在浏览器客户端中将购物车对象添加到cookie中
            CookieUtil.setCookie(request, response, cookieKey, cookieValue, cookieMaxAge, true);

        } else {
            //用户已登录，将购物车对象放入redis和db中
            //从数据库db中查询购物车数据
            OmsCartItem omsCartItemFromDb = cartService.ifCartExitByUser(memberId, skuId);

            //若查询的结果为空，说明该用户没有添加过当前商品
            if (omsCartItemFromDb == null) {
                //添加到数据库中
                omsCartItem.setMemberId(memberId);
                omsCartItem.setQuantity(new BigDecimal(quantity));
                cartService.addCart(omsCartItem);

            } else {
                //该用户添加过当前商品
                omsCartItemFromDb.setQuantity(omsCartItemFromDb.getQuantity().add(omsCartItem.getQuantity()));
                omsCartItemFromDb.setPrice(omsCartItemFromDb.getPrice().add(omsCartItem.getPrice()));
                cartService.setCart(omsCartItemFromDb);
            }

            //同步带redis缓存
            cartService.flushCartCache(memberId);

        }

        return "redirect:/success.html";
    }

    /**
     * //判断cookie里原有的购物车数据是否为空
     *
     * @param cookieValues
     * @param omsCartItem
     * @return true代表不为空
     */
    private boolean if_cart_cookie_exit(List<OmsCartItem> cookieValues, OmsCartItem omsCartItem) {

        boolean cartExit = false;
        for (OmsCartItem cookieValue : cookieValues) {
            if (cookieValue.getProductSkuId().equals(omsCartItem.getProductSkuId())) {
                cartExit = true;
            }
        }

        return cartExit;
    }

}
