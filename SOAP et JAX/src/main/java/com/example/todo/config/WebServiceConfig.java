package com.example.todo.config;

import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.example.todo.endpoint.TodoEndpoint;

@Configuration
public class WebServiceConfig {

    @Autowired
    private Bus bus;

    @Bean
    public EndpointImpl endpoint(TodoEndpoint todoEndpoint) {
        EndpointImpl endpoint = new EndpointImpl(bus, todoEndpoint);
        endpoint.publish("/todo");
        System.out.println("SOAP service disponible sur http://localhost:8080/ws/todo?wsdl");
        return endpoint;
    }
}
