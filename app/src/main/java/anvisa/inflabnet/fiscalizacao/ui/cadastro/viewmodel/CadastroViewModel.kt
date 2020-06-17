package anvisa.inflabnet.fiscalizacao.ui.cadastro.viewmodel

import android.content.Context
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import anvisa.inflabnet.fiscalizacao.api.bairros.ApiClientBairros
import anvisa.inflabnet.fiscalizacao.api.bairros.Bairros
import anvisa.inflabnet.fiscalizacao.api.municipios.ApiClientMunicipios
import anvisa.inflabnet.fiscalizacao.api.municipios.Municipios
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CadastroViewModel : ViewModel() {
    var estabelecimento = MutableLiveData<Any>()
    fun setMunicipio(mun: String){
        estabelecimento.value = mun
    }

    fun listaMunicipiosAutoComplete(
        context: Context,
        acMunicipios: AutoCompleteTextView
    ) {
        ApiClientMunicipios.getMunicipiosService().all()
            .enqueue(object : Callback<List<Municipios>> {
                override fun onFailure(call: Call<List<Municipios>>, t: Throwable) {
                    Toast.makeText(context, t.message, Toast.LENGTH_LONG).show()

                }

                override fun onResponse(
                    call: Call<List<Municipios>>,
                    response: Response<List<Municipios>>
                ) {
                    val listaMunicipios = response.body()
                    val municipios: MutableList<String> = mutableListOf()
                    listaMunicipios?.forEach {
                        municipios.add(it.nome.toString())
                    }

                    if(municipios.isNotEmpty()){

                        val adapter = ArrayAdapter(
                            context,
                            android.R.layout.simple_spinner_dropdown_item,
                            municipios
                        )

                        acMunicipios.setAdapter(adapter)
                        acMunicipios.threshold = 1
                        acMunicipios.text.toString()

                        acMunicipios.setOnDismissListener {
                        }
                        acMunicipios.onFocusChangeListener = View.OnFocusChangeListener { _, b ->
                            if (b) {
                                // Sugestões dropdown
                                acMunicipios.showDropDown()
                            }
                        }
                    }


                }
            })
    }


    fun listaBairrosAutoComplete(
        requireContext: Context,
        acBairros: AutoCompleteTextView
    ) {
        ApiClientBairros.getBairrosService().all().enqueue(object : Callback<Bairros> {
            override fun onFailure(call: Call<Bairros>, t: Throwable) {
                Toast.makeText(requireContext, t.message, Toast.LENGTH_LONG).show()

            }

            override fun onResponse(call: Call<Bairros>, response: Response<Bairros>) {
                val listaBairros = response.body()
                val bairrosList: MutableList<String> = mutableListOf()


                listaBairros?.features?.forEach {
                    bairrosList.add(it.attributes.NOME)
                }

                bairrosList.sort()

                val adapter = ArrayAdapter(
                    requireContext,
                    android.R.layout.simple_spinner_dropdown_item,
                    bairrosList
                )

                acBairros.setAdapter(adapter)
                acBairros.threshold = 1
                acBairros.text.toString()

                acBairros.setOnDismissListener {
                }
                acBairros.onFocusChangeListener = View.OnFocusChangeListener { _, b ->
                    if (b) {
                        // Sugestões dropdown
                        acBairros.showDropDown()
                    }
                }
            }
        })
    }

}