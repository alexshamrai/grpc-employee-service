package io.github.alexshamrai;

import io.github.alexshamrai.common.GrpcLoggingInterceptor;
import io.github.alexshamrai.grpc.EmployeeServiceGrpc;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.qameta.allure.grpc.AllureGrpc;
import org.junit.jupiter.api.BeforeAll;

import java.util.List;

public abstract class BaseTest {

    protected static EmployeeServiceGrpc.EmployeeServiceBlockingStub blockingStub;
    protected static Channel channel;

    @BeforeAll
    public static void init(){
        channel = ManagedChannelBuilder
            .forAddress("localhost", 6565)
            .intercept(List.of(new GrpcLoggingInterceptor(), new AllureGrpc()))
            .usePlaintext()
            .build();

        blockingStub = EmployeeServiceGrpc.newBlockingStub(channel);
    }
}
