package org.fffd.l23o6.controller;

import com.alipay.api.AlipayApiException;
import io.github.lyc8503.spring.starter.incantation.exception.BizException;
import io.github.lyc8503.spring.starter.incantation.exception.CommonErrorType;
import io.github.lyc8503.spring.starter.incantation.pojo.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.fffd.l23o6.pojo.enum_.PaymentType;
import org.fffd.l23o6.pojo.vo.order.CreateOrderRequest;
import org.fffd.l23o6.pojo.vo.order.OrderIdVO;
import org.fffd.l23o6.pojo.vo.order.OrderVO;
import org.fffd.l23o6.pojo.vo.order.PatchOrderRequest;
import org.fffd.l23o6.service.OrderService;
import org.springframework.web.bind.annotation.*;

import cn.dev33.satoken.stp.StpUtil;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/v1/")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("order")
    public CommonResponse<OrderIdVO> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        StpUtil.checkLogin();
        return CommonResponse.success(new OrderIdVO(orderService.createOrder(StpUtil.getLoginIdAsString(), request.getTrainId(), request.getStartStationId(), request.getEndStationId(), request.getSeatType(), null, request.getPrice())));
    }

    @GetMapping("order")
    public CommonResponse<List<OrderVO>> listOrders(){
        StpUtil.checkLogin();
        return CommonResponse.success(orderService.listOrders(StpUtil.getLoginIdAsString()));
    }

    @GetMapping("order/{orderId}")
    public CommonResponse<OrderVO> getOrder(@PathVariable("orderId") Long orderId) throws AlipayApiException {
        return CommonResponse.success(orderService.getOrder(orderId));
    }

    @PatchMapping ("order/{orderId}")
    public CommonResponse<?> patchOrder(@PathVariable("orderId") Long orderId, @Valid @RequestBody PatchOrderRequest request) throws AlipayApiException {
        String responseBody = null;
        switch (request.getStatus()) {
            case PENDING_PAYMENT -> responseBody = orderService.payOrder(orderId, request.getPaymentType());
            case CANCELLED -> orderService.cancelOrder(orderId);
            case REFUNDED -> orderService.refundOrder(orderId);
            default -> throw new BizException(CommonErrorType.ILLEGAL_ARGUMENTS, "Invalid order status.");
        }
        
        return CommonResponse.success(responseBody);
    }
}