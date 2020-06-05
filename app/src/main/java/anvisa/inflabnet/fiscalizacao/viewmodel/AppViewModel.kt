package anvisa.inflabnet.fiscalizacao.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AppViewModel: ViewModel() {

    var estabelecimento = MutableLiveData<Any>()
    fun setMunicipio(mun: String){
        estabelecimento.value = mun
    }

}