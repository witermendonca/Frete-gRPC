package br.com.zupacademy.witer

import com.google.protobuf.Any
import com.google.rpc.Code
import io.grpc.Status
import io.grpc.protobuf.StatusProto
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class FretesGrpcService : FretesServiceGrpc.FretesServiceImplBase() {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun calculaFrete(request: CalculaFreteRequest?, responseObserver: StreamObserver<CalculaFretesRequest>?) {

        logger.info("Calculando Frete para request $request")

        val cep = request?.cep

        //Valida Cep nulo e vazio.
        if (cep.isNullOrBlank()) responseObserver?.onError(
            Status.INVALID_ARGUMENT.withDescription("Cep deve ser informado.").asRuntimeException()
        )

        //Valida cep válido.
        if (!cep!!.matches("[0-9]{5}-[0-9]{3}".toRegex())) responseObserver?.onError(
            Status.INVALID_ARGUMENT
                .withDescription("Cep inválido.")
                .augmentDescription("Formato esperado do CEP deve ser 99999-999")
                .asRuntimeException()
        )

        var valor = 0.0
        //Tratando erro servidor.
        try {
            valor = Random.nextDouble(0.0, 150.0)

            //Simulação de erro
            if (valor > 100.0) throw IllegalStateException("Erro inesperado ao executar lógica de negócio.")
        } catch (e: Exception) {
            responseObserver?.onError(Status.INTERNAL
                .withDescription(e.message)
                .withCause(e)
                .asRuntimeException()
            )
        }

        //Simulação de Erro de segurança
        if (cep.endsWith("333")) {
            responseObserver?.onError(StatusProto.toStatusException(
                com.google.rpc.Status.newBuilder()
                    .setCode(Code.PERMISSION_DENIED.number)
                    .setMessage("Permissão de acesso usuario negada.")
                    .addDetails(Any.pack(ErrorDetails.newBuilder()
                        .setCode(401)
                        .setMessage("Token inválido.")
                        .build()))
                    .build())
            )
        }

        val response = CalculaFretesRequest.newBuilder()
            .setCep(request!!.cep)
            .setValor(valor)
            .build()

        logger.info("Frete Calculado: $response")
        responseObserver!!.onNext(response)
        responseObserver.onCompleted()
    }
}