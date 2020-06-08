package anvisa.inflabnet.fiscalizacao.api.bairros

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClientBairros {

    private var instance : Retrofit? = null
    private const val url : String = "https://pgeo3.rio.rj.gov.br/"
    private fun getInstance(): Retrofit {
        if (instance == null){
            instance = Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return instance as Retrofit
    }

    fun getBairrosService() : BairrosService
            = getInstance()
        .create(BairrosService::class.java)

}