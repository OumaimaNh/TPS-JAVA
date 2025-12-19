package com.example.backenda.grpc;

import com.example.backenda.model.User;
import com.example.backenda.repository.UserRepository;
import com.example.grpc.UserRequest;
import com.example.grpc.UserResponse;
import com.example.grpc.ListUsersRequest;
import com.example.grpc.UsersList;
import com.example.grpc.UserServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@GrpcService
public class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void getUser(UserRequest request, StreamObserver<UserResponse> responseObserver) {
        long userId = request.getId();
        Optional<User> userOptional = userRepository.findById(userId);

        UserResponse response;
        userOptional.ifPresentOrElse(
            user -> {
                UserResponse userResponse = UserResponse.newBuilder()
                    .setId(user.getId())
                    .setName(user.getName())
                    .setEmail(user.getEmail())
                    .setFound(true)
                    .build();
                responseObserver.onNext(userResponse);
                responseObserver.onCompleted();
            },
            () -> {
                UserResponse notFoundResponse = UserResponse.newBuilder()
                    .setFound(false)
                    .build();
                responseObserver.onNext(notFoundResponse);
                responseObserver.onCompleted();
            }
        );
    }

    @Override
    public void listUsers(ListUsersRequest request, StreamObserver<UsersList> responseObserver) {
        Iterable<User> users = userRepository.findAll();
        
        UsersList.Builder usersListBuilder = UsersList.newBuilder();
        
        for (User user : users) {
            UserResponse userResponse = UserResponse.newBuilder()
                .setId(user.getId())
                .setName(user.getName())
                .setEmail(user.getEmail())
                .setFound(true)
                .build();
            usersListBuilder.addUsers(userResponse);
        }
        
        responseObserver.onNext(usersListBuilder.build());
        responseObserver.onCompleted();
    }
}
