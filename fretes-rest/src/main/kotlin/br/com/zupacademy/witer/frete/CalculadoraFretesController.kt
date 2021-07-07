package br.com.zupacademy.witer.frete

import br.com.zupacademy.witer.CalculaFreteRequest
import br.com.zupacademy.witer.FretesServiceGrpc
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import javax.inject.Inject

@Controller
class CalculadoraFretesController(@Inject val grpcClient: FretesServiceGrpc.FretesServiceBlockingStub) {


    @Get("/api/fretes")
    fun calcula(@QueryValue cep: String): FreteResponse? {

        val response = grpcClient.calculaFrete(CalculaFreteRequest.newBuilder().setCep(cep).build())

        return FreteResponse(response.cep, response.valor)
    }
}