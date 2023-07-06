package org.fffd.l23o6.util.strategy.payment;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import org.fffd.l23o6.pojo.entity.OrderEntity;
import org.fffd.l23o6.pojo.entity.UserEntity;
import org.fffd.l23o6.pojo.enum_.OrderStatus;

import java.nio.ByteBuffer;
import java.util.UUID;

public class AlipayPaymentStrategy extends PaymentStrategy {
    AlipayConfig alipayConfig = new AlipayConfig();
    
    @Override
    public String pay(final OrderEntity order, UserEntity user) throws AlipayApiException {
        AlipayClient alipayClient = new DefaultAlipayClient(getAlipayConfig());
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        //异步接收地址，仅支持http/https，公网可访问
        request.setNotifyUrl("");
        //同步跳转地址，仅支持http/https
        request.setReturnUrl("");
        
        JSONObject bizContent = new JSONObject();
        //商户订单号，商家自定义，保持唯一性
        long timeStamp = System.currentTimeMillis();
        byte[] timeStampBytes = ByteBuffer.allocate(Long.BYTES).putLong(timeStamp).array();
        UUID uuid = UUID.nameUUIDFromBytes(timeStampBytes);
        order.setTradeId(uuid.toString());
        bizContent.put("out_trade_no", uuid.toString());
        //支付金额，最小值0.01元
        bizContent.put("total_amount", order.getPrice());
        //订单标题，不可使用特殊符号
        bizContent.put("subject", "车票");
        //电脑网站支付场景固定传值FAST_INSTANT_TRADE_PAY
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
        
        request.setBizContent(bizContent.toString());
        AlipayTradePagePayResponse response = alipayClient.pageExecute(request, "get");
        if(response.isSuccess()){
            System.out.println("调用成功");
        } else {
            System.out.println("调用失败");
        }
        
        return response.getBody();
    }
    
    @Override
    public void refund(OrderEntity order, UserEntity user) throws AlipayApiException {
        AlipayClient alipayClient = new DefaultAlipayClient(getAlipayConfig());
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("trade_no", order.getTradeId());
        bizContent.put("refund_amount", order.getPrice());
        
        request.setBizContent(bizContent.toString());
        AlipayTradeRefundResponse response = alipayClient.execute(request);
        if(response.isSuccess()){
            System.out.println("调用成功");
            order.setStatus(OrderStatus.REFUNDED);
        } else {
            System.out.println("调用失败");
        }
    }
    
    public AlipayConfig getAlipayConfig() {
        alipayConfig.setAppId("9021000122696896");
        alipayConfig.setPrivateKey("MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDB9GHiPtcvjipYDtx/w+hOZcw/8PRFfo0FRMSMVPU6dqjYpubIAyM8YLom8w5EHlpOTXsw862oJe4W4m0joQbQ/y2Uzpk/pgXBZUt49XMcu9+EG0cO5/XcQa3Eu46DGjA0DmTfU78YsEHaHIm+PehHvh7mG+cBicKTGIljDCZLL8hql3oTt4MY9eaL1J0tpQJq7W6QA6Ns9eVK6Nc6mm1DNafgQ7VDPrtaPd3Qn2ZWzz2nu8HMDtqTGW9La3ddlGi6ahVQT671LGOwZ5ff4YLWwQPb4TnJ2WRqh3T0bpyR4gj4tddvPdx9bXX7aLLc8Kj1FncdpQBlGrS2orFGfHynAgMBAAECggEBAI7fCkuypqitPJvpNpwXH8TAUBi50fZvJko/Qdltq8cJKY+OQmoLhaUwS2/tkxCymysbACgrGDzI0/2fhW29bFv/3P/EwDaWgZ7YkyPjCoycCCY0Qb/FNuKsruhAkNYaMG/oOn4YHqh9DcUxSHYO/E/8yJ4Q57XGWMIqTOndRUgT/hPWfiDN7PrRO+eZuMs9z3w/FMv72cCyCPNbiuSqBKweXfs9U3UsfbXbNDEgkVTWfdEV/ak9y6XMuEUnRdVXBFUJPBIbn1ozcSSVnblOWh83dfJ4VdIIrynnud4VfAbQWLlT3g9snHET6VlvbgsTMQXPuzTSfZILEz3ekhSPJQECgYEA6ZPPte9tWWvmuzkkZb6Dn9QeHmHYH9Asn6rYY/5DQD/BqzS/jnuCKP/yNrLEphjmmvFoNBhmMmJBlaa0f+cH5THBfNvYkwunAL9d/n2/G7kwdHDrHzMuD8D761JoycdAlErGMTGSYuwPtFPeA4olzYJNEz3xMKCsbB/bLKo19wcCgYEA1JLWWWN/0W7KymHzsBRKHiENSYvQkl9m0j/N8Mjh2UJ6Jfswleoe2ZFrRKFnQ2JfPEjl4E2M2BwC+61/pxx1rnchMo8bvnH67BKqAixXilFrB/kWLIVTh890ptpB7NvUxL5UUl/5xElaDYU3tXCxououdPeerCEe/Re8ILWjRWECgYEAvk16Vp2qtsdNpk5JFEgjD04P9+r53fmK0c+tG9Ll0evgC3AffaOtCWB2ZBBEXZs+DxEOOBuE8BnTm2zF9MszSI3AOc0YZ71/pAFGQHkNAjZgfw4c0j+tkJJH7ZMn4vXe7LL7RMtBnCz+IoqOAM5GPGikCUBXSKovsKxHst0+uHMCgYAcX+1UyT9KiWtwUFri6hJqqybG5m7U2v8ymWm39liPU5xSbZEysamkiRC0fIb4K61isqhcpH1ka3jKd4JOoDHlM+muggPGDBjeYi9nUtvsjqmmysz27GUV/88mZoPeRgnEqVNAG6ePV+s5Wsrw4TYU9QV2f4cimtk0F3L5OsWsgQKBgAT8a7LKlgK03gklBmPMy3ZXztMQCkqeG60OnwI17k96Ag6HI0nphaE82jd1e7A3GhwIilG1ySmVUe4U+s0I8hktAU9jk7oWSk2mlfC6DS/IaPcoEPDimPQ0smPDLpXNJqhGD195N90LZ5fUHSAfkroLggu4kFYa9kZE/gLE3W6q");
        alipayConfig.setAlipayPublicKey("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmPYgDnWRR7Ge9xvHPaaKIP/hBVnPGby9uJT4gzDld/HSrc9dGA1PNRxTcT2vTO2rjNM4/P7hNUM16DYi6iRXQM3bB4toxBNeIoLMHxLI46O3JY1euoHXWD+369279Y4dKptaeXJZG6NgR+jKP++R9UnIDheGg/z+E9IM98tbJjiO2MFx+nUMVj81nDfcg/Wl3GBRf6zhiwaQw/l2aeD2Xtax71rg9aprcMx1DS+hxa1m/DhCLfvv9mwUkd0+fRggWvNbcrAKghuSBMOWYmbv/7gInmgNOLYLjQfsH0JG02MTNLnjmOBo5tAC6aiTbcRVfkIQWTHteHnbgGperhirtwIDAQAB");
        alipayConfig.setServerUrl("https://openapi-sandbox.dl.alipaydev.com/gateway.do");
        alipayConfig.setCharset("utf-8");
        alipayConfig.setFormat("json");
        alipayConfig.setSignType("RSA2");
        
        return alipayConfig;
    }
    
    @Override
    public OrderStatus query(final OrderEntity order) throws AlipayApiException {
        if (order.getStatus().equals(OrderStatus.REFUNDED)) return OrderStatus.REFUNDED;
        
        AlipayClient alipayClient = new DefaultAlipayClient(getAlipayConfig());
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        JSONObject bizContent = new JSONObject();
        
        bizContent.put("out_trade_no", order.getTradeId());
        request.setBizContent(bizContent.toString());
        AlipayTradeQueryResponse response = alipayClient.execute(request);
        
        String tradeStatus = response.getTradeStatus();
        if (tradeStatus == null) return OrderStatus.PENDING_PAYMENT;
        else if (tradeStatus.equals("WAIT_BUYER_PAY")) return OrderStatus.PENDING_PAYMENT;
        else if (tradeStatus.equals("TRADE_SUCCESS")) return OrderStatus.PAID;
        else if (tradeStatus.equals("TRADE_FINISHED")) return OrderStatus.COMPLETED;
        else return OrderStatus.CANCELLED;
    }
}
