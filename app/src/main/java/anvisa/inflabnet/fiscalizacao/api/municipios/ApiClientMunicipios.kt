package anvisa.inflabnet.fiscalizacao.api.municipios

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClientMunicipios {
    private var instance : Retrofit? = null
    private const val url : String = "https://servicodados.ibge.gov.br"
    private fun getInstance(): Retrofit {
        if (instance == null){
            instance = Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return instance as Retrofit
    }

    fun getMunicipiosService() : MunicipiosService
            = getInstance()
        .create(MunicipiosService::class.java)
}