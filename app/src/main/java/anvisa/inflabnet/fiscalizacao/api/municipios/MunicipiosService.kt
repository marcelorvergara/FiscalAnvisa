package anvisa.inflabnet.fiscalizacao.api.municipios

import anvisa.inflabnet.fiscalizacao.api.municipios.Municipios
import retrofit2.Call
import retrofit2.http.GET

interface MunicipiosService {

    @GET("/api/v1/localidades/estados/33/municipios")
    fun all(): Call<List<Municipios>>

}