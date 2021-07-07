package br.com.zupacademy.witer.grpcClient

import br.com.zupacademy.witer.FretesServiceGrpc
import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import javax.inject.Singleton

@Factory
class GrpcClientFactory {

    @Singleton
    fun fretesClientStub(@GrpcChannel("fretes") chanel: ManagedChannel): FretesServiceGrpc.FretesServiceBlockingStub? {

        return FretesServiceGrpc.newBlockingStub(chanel)
    }
}