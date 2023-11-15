package com.programmingtechie.orderservice.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import com.programmingtechie.orderservice.dto.InventoryResponse;
import com.programmingtechie.orderservice.dto.OrderLineItemsDto;
import com.programmingtechie.orderservice.dto.OrderRequest;
import com.programmingtechie.orderservice.model.Order;
import com.programmingtechie.orderservice.model.OrderLineItems;
import com.programmingtechie.orderservice.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient webClient;

//    @Transactional(readOnly = true)
    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItemsDto> listOrderLineItemsDtos = orderRequest.getOrderLineItemsDtoList();

        List<OrderLineItems> orderLineItems = new ArrayList<>();

        listOrderLineItemsDtos.forEach(o -> {
            orderLineItems.add(new OrderLineItems(o.getId(), o.getSkuCode(), o.getPrice(), o.getQuantity()));
        });

        List<String> skuCodes = listOrderLineItemsDtos.stream()
        				.map(o -> o.getSkuCode())
        				.toList();
//
//        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
//                                .stream()
//                                .map(this::mapToDto)
//                                .toList();

        order.setOrderLineItemsList(orderLineItems);

        InventoryResponse[] result = webClient.get()
        		.uri("http://localhost:8082/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        boolean allProductsInStock = Arrays.asList(result).stream()
        				.allMatch(inventoryResponse -> inventoryResponse.isInStock());


        if (allProductsInStock) {
        	orderRepository.save(order);
        } else {
        	throw new IllegalArgumentException("Product is not in stock");
        }
    	System.out.println("erejkj");
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
