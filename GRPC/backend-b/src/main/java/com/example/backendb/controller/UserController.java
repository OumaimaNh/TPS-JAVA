package com.example.backendb.controller;

import com.example.backendb.grpc.GrpcUserClient;
import com.example.grpc.UserResponse;
import com.example.grpc.UsersList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private GrpcUserClient grpcUserClient;

    @GetMapping("/{id}")
    public Map<String, Object> getUserById(@PathVariable long id) {
        UserResponse response = grpcUserClient.getUserById(id);
        
        if (!response.getFound()) {
            return Map.of("found", false);
        }
        
        return Map.of(
            "found", true,
            "id", response.getId(),
            "name", response.getName(),
            "email", response.getEmail()
        );
    }

    @GetMapping
    public List<Map<String, Object>> listUsers() {
        UsersList usersList = grpcUserClient.listUsers();
        
        return usersList.getUsersList().stream()
            .map(user -> Map.of(
                "id", (Object) user.getId(),
                "name", (Object) user.getName(),
                "email", (Object) user.getEmail()
            ))
            .collect(Collectors.toList());
    }
}
