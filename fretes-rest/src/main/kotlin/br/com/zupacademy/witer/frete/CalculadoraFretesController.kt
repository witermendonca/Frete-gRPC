package br.com.zupacademy.witer.frete

import br.com.zupacademy.witer.CalculaFreteRequest
import br.com.zupacademy.witer.ErrorDetails
import br.com.zupacademy.witer.FretesServiceGrpc
import com.google.protobuf.Any
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.protobuf.StatusProto
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.exceptions.HttpStatusException
import javax.inject.Inject

@Controller
class CalculadoraFretesController(@Inject val grpcClient: FretesServiceGrpc.FretesServiceBlockingStub) {


    @Get("/api/fretes")
    fun calcula(@QueryValue cep: String): FreteResponse? {

        try {
            val response = grpcClient.calculaFrete(CalculaFreteRequest.newBuilder().setCep(cep).build())
            return FreteResponse(response.cep, response.valor)
        } catch (e: StatusRuntimeException) {

            val statusCode = e.status.code
            val description = e.status.description

            if (statusCode == Status.Code.INVALID_ARGUMENT) {
                throw HttpStatusException(HttpStatus.BAD_REQUEST, description)
            }

            if (statusCode == Status.Code.PERMISSION_DENIED){

                val statusProto = StatusProto.fromThrowable(e)
                if (statusProto == null) throw HttpStatusException(HttpStatus.UNAUTHORIZED, description)

                val anyDetails: Any = statusProto.detailsList.get(0)
                val erroDetails = anyDetails.unpack(ErrorDetails::class.java)
                throw HttpStatusException(HttpStatus.UNAUTHORIZED, "${erroDetails.code}: ${erroDetails.message}")




            }

            throw HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
        }


    }
}