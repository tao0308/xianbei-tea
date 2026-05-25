package com.xianbei.tea.controller;

import com.xianbei.tea.service.OrderEventService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/sse")
public class SseController {
    private final OrderEventService orderEventService;

    public SseController(OrderEventService orderEventService) {
        this.orderEventService = orderEventService;
    }

    @GetMapping("/orders")
    public SseEmitter subscribe() {
        return orderEventService.subscribe();
    }
}
