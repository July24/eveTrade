package com.eve.controller;

import com.eve.entity.EveOrder;
import com.eve.service.QueryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Api(description = "查询API")
@RestController
@RequestMapping("/query")
public class QueryController {
    @Autowired
    private QueryService queryService;

    @ApiOperation(value = "模糊匹配",notes = "输入中文/英文名")
    @GetMapping("/likeQuery")
    public List<String> likeQuery(String name) {
        return queryService.likeQuery(name);
    }

    @ApiOperation(value = "查询吉他订单",notes = "输入中文/英文名")
    @GetMapping("/queryJita")
    public JitaOrder addRestaurantChain(String name) {
        List<EveOrder> orderList = queryService.queryJita(name);
        JitaOrder jitaOrder = new JitaOrder();
        for(EveOrder order : orderList) {
            if(order.isBuyOrder()) {
                jitaOrder.buy.add(order);
            } else {
                jitaOrder.sell.add(order);
            }
        }
        return jitaOrder;
    }

    public class JitaOrder {
        public List<EveOrder> sell= new ArrayList<>();
        public List<EveOrder> buy = new ArrayList<>();

    }
}
