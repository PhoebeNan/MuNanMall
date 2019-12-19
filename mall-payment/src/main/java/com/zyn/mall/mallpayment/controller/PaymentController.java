package com.zyn.mall.mallpayment.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.zyn.mall.annotations.LoginRequired;
import com.zyn.mall.api.bean.order.OmsOrder;
import com.zyn.mall.api.bean.payment.PaymentInfo;
import com.zyn.mall.api.service.OrderService;
import com.zyn.mall.api.service.PaymentService;
import com.zyn.mall.mallpayment.config.AlipayConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhaoyanan
 * outTradeNo:代表系统外部订单号
 * @create 2019-12-17-14:59
 */
@Controller
public class PaymentController {

    @Autowired
    private AlipayClient alipayClient;

    @Autowired
    private PaymentService paymentService;

    @Reference
    private OrderService orderService;

    @RequestMapping("alipay/callback/return")
    @LoginRequired
    public String alipayCallbackReturn(HttpServletRequest request, ModelMap modelMap) {

        //支付宝回调我们,对支付宝进行验签
        String sign = request.getParameter("sign");
        String trade_no = request.getParameter("trade_no");
        String out_trade_no = request.getParameter("out_trade_no");
        String trade_status = request.getParameter("trade_status");
        String total_amount = request.getParameter("total_amount");
        String subject = request.getParameter("subject");
        String call_back_content = request.getQueryString();

        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setOrderSn(out_trade_no);
        paymentInfo.setPaymentStatus("已支付");
        paymentInfo.setAlipayTradeNo(trade_no); //支付宝的校验凭证号
        paymentInfo.setCallbackContent(call_back_content); //回调请求字符串
        paymentInfo.setCallbackTime(new Date());

        //通过支付宝的paramMap进行签名验证，2.0版本的接口将paramMap参数去掉了，导致同步请求无法验签
        if(StringUtils.isNotBlank(sign)){
            //验签成功

            //更新用户的支付状态
            paymentService.updatePayment(paymentInfo);
        }

        //支付成功后，引起的的系统各种服务(订单服务的更新-》库存服务-》物流服务)

        return "finish";
    }

    @RequestMapping("alipay/submit")
    @LoginRequired
    @ResponseBody
    public String alipay(String outTradeNo, BigDecimal totalAmount, HttpServletRequest request, ModelMap modelMap) {

        //获得一个支付宝请求的客户端（他并不是一个链接，而是一个封装好的http表达请求）
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();//创建API对应的request
        alipayRequest.setReturnUrl(AlipayConfig.return_payment_url);
        alipayRequest.setNotifyUrl(AlipayConfig.notify_payment_url);

        Map<String, Object> map = new HashMap<>();
        map.put("out_trade_no", outTradeNo);
        map.put("product_code", "FAST_INSTANT_TRADE_PAY");
        map.put("total_amount", 0.01);
        map.put("subject", "iPhone11手机 暗夜绿128G（订单标题）");
        String param = JSON.toJSONString(map);

        alipayRequest.setBizContent(param);

        String form = "";
        try {
            form = alipayClient.pageExecute(alipayRequest).getBody();//调用sdk生成表单
            System.out.println(form);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        //生成并保存用户的支付信息
        OmsOrder omsOrder = orderService.getOrderOutTradeNo(outTradeNo);

        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setOrderId(omsOrder.getId());
        paymentInfo.setOrderSn(outTradeNo);
        paymentInfo.setPaymentStatus("未付款");
        paymentInfo.setSubject("iPhone11暗夜绿128G(由页面传递过来的商品标题)");
        paymentInfo.setTotalAmount(totalAmount);

        paymentService.savePaymentInfo(paymentInfo);

        //提交请求到支付宝
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
