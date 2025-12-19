package com.example.backendb.grpc;

import com.example.grpc.ListUsersRequest;
import com.example.grpc.UserRequest;
import com.example.grpc.UserResponse;
import com.example.grpc.UserServiceGrpc;
import com.example.grpc.UsersList;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

@Component
public class GrpcUserClient {

    @GrpcClient("userservice")
    private UserServiceGrpc.UserServiceBlockingStub userServiceStub;

    public UserResponse getUserById(long id) {
        UserRequest request = UserRequest.newBuilder()
                .setId(id)
                .build();
        return userServiceStub.getUser(request);
    }

    public UsersList listUsers() {
        ListUsersRequest request = ListUsersRequest.newBuilder().build();
        return userServiceStub.listUsers(request);
    }
}
