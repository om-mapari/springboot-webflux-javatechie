package com.javatechie.webflux.handler;

import com.javatechie.webflux.dao.CustomerDao;
import com.javatechie.webflux.dto.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
public class CustomerHandler {

    @Autowired
    private CustomerDao dao;

    public Mono<ServerResponse> loadCustomer(ServerRequest request){
        Flux<Customer> customersList = dao.getCustomersStream();
        return ServerResponse.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(customersList,Customer.class); // (return data, Customer class type)
    }

    public Mono<ServerResponse> findCustomer(ServerRequest request){
        int customerId = Integer.valueOf(request.pathVariable("input")); // path parameter
        Mono<Customer> customerMono = dao.getCustomersStream()
                .filter(c -> c.getId() == customerId)
                .next();
        return ServerResponse.ok().body(customerMono,Customer.class);
    }

    public Mono<ServerResponse> saveCustomer(ServerRequest request){
        Mono<Customer> customerMono = request.bodyToMono(Customer.class); // request
        Mono<String> saveResponse = customerMono.map(dto -> dto.getId() + " : " + dto.getName());// it will return mono of string
        return ServerResponse.ok().body(saveResponse,String.class);
    }
}
