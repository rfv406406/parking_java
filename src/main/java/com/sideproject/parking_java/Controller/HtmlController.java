package com.sideproject.parking_java.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;



@Controller
public class HtmlController {
    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/id")
    public String id() {
        return "id";
    }
    
    @GetMapping("/parkingLotPage")
    public String parkingLotPage() {
        return "parking_lot_page";
    }

    @GetMapping("/selector")
    public String selector() {
        return "selector";
    }

    @GetMapping("/carPage")
    public String carPage() {
        return "car_page";
    }

    @GetMapping("/depositPage")
    public String depositPage() {
        return "deposit_page";
    }

    @GetMapping("/cashFlowRecord")
    public String cashFlowRecord() {
        return "cash_flow_record";
    }

    @GetMapping("/chatroom/**")
    public String chatroom() {
        return "chat_page";
    }
}
