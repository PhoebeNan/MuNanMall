package com.zyn.mall.mallpayment.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.zyn.mall.annotations.LoginRequired;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

/**
 * @author zhaoyanan
 * outTradeNo:代表系统外部订单号
 * @create 2019-12-17-14:59
 */
@Controller
public class PaymentController {

    @Autowired
    private AlipayClient alipayClient;

    @RequestMapping("alipay/submit")
    @LoginRequired
    @ResponseBody
    public String alipay(String outTradeNo, BigDecimal totalAmount, HttpServletRequest request, ModelMap modelMap) {

        String form="";
        try {
            form = alipayClient.pageExecute(null).getBody();//调用sdk生成表单
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return form;
    }

    @RequestMapping("index")
    @LoginRequired
    public String index(String outTradeNo, BigDecimal totalAmount, HttpServletRequest request, ModelMap modelMap) {

        String memberId = (String) request.getAttribute("memberId");
        String memberNickname = (String) request.getAttribute("memberNickname");

        modelMap.put("memberNickname", memberNickname);
        modelMap.put("outTradeNo", outTradeNo);
        modelMap.put("totalAmount", totalAmount);

        return "index";
    }

    @RequestMapping("wx/submit")
    @LoginRequired
    public String wx(String outTradeNo, BigDecimal totalAmount, HttpServletRequest request, ModelMap modelMap) {

        return null;
    }
}
