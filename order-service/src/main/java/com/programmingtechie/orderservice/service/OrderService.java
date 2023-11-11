package com.programmingtechie.orderservice.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.programmingtechie.orderservice.dto.OrderLineItemsDto;
import com.programmingtechie.orderservice.dto.OrderRequest;
import com.programmingtechie.orderservice.model.Order;
import com.programmingtechie.orderservice.model.OrderLineItems;
import com.programmingtechie.orderservice.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItemsDto> listOrderLineItemsDtos = orderRequest.getOrderLineItemsDtoList();

        List<OrderLineItems> orderLineItems = new ArrayList<>();

        listOrderLineItemsDtos.forEach(o -> {
            orderLineItems.add(new OrderLineItems(o.getId(), o.getSkuCode(), o.getPrice(), o.getQuantity()));
        });
//
//        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
//                                .stream()
//                                .map(this::mapToDto)
//                                .toList();

        order.setOrderLineItemsList(orderLineItems);

        orderRepository.save(order);
    }

    private OrderLineItems mapToDto(OrderLineItemsDto dto) {
        OrderLineItems orderLineItems = new OrderLineItems();

        orderLineItems.setId(dto.getId());
        orderLineItems.setSkuCode(dto.getSkuCode());
        orderLineItems.setPrice(dto.getPrice());
        orderLineItems.setQuantity(dto.getQuantity());

        return orderLineItems;
    }

}
