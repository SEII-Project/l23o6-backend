package org.fffd.l23o6.util.strategy.payment;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import org.fffd.l23o6.pojo.entity.OrderEntity;

public class AlipayPaymentStrategy extends PaymentStrategy {
    @Override
    public void pay(final OrderEntity order) throws AlipayApiException {
        AlipayClient alipayClient = new DefaultAlipayClient(new AlipayConfig());
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        //异步接收地址，仅支持http/https，公网可访问
        request.setNotifyUrl("");
        //同步跳转地址，仅支持http/https
        request.setReturnUrl("");
        
        JSONObject bizContent = new JSONObject();
        //商户订单号，商家自定义，保持唯一性
        bizContent.put("out_trade_no", order.getId().toString());
        //支付金额，最小值0.01元
        bizContent.put("total_amount", order.getPrice());
        //订单标题，不可使用特殊符号
        bizContent.put("subject", "车票");
        //电脑网站支付场景固定传值FAST_INSTANT_TRADE_PAY
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
        
        request.setBizContent(bizContent.toString());
        AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
        if(response.isSuccess()){
            System.out.println("调用成功");
        } else {
            System.out.println("调用失败");
        }
    }
    
    @Override
    public void refund(OrderEntity order) throws AlipayApiException {
        AlipayClient alipayClient = new DefaultAlipayClient(new AlipayConfig());
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("trade_no", order.getId().toString());
        bizContent.put("refund_amount", order.getPrice());
        bizContent.put("out_request_no", order.getId().toString());
        
        request.setBizContent(bizContent.toString());
        AlipayTradeRefundResponse response = alipayClient.execute(request);
        if(response.isSuccess()){
            System.out.println("调用成功");
        } else {
            System.out.println("调用失败");
        }
    }
}
