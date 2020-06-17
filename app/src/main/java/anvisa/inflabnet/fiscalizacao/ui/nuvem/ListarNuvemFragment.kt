package anvisa.inflabnet.fiscalizacao.ui.nuvem

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import anvisa.inflabnet.fiscalizacao.R
import anvisa.inflabnet.fiscalizacao.ui.nuvem.viewmodel.NuvemViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_listar_nuvem.*

/**
 * A simple [Fragment] subclass.
 */
class ListarNuvemFragment : Fragment() {

    private lateinit var nuvemViewModel: NuvemViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        nuvemViewModel =
            ViewModelProviders.of(this).get(NuvemViewModel::class.java)
        return inflater.inflate(R.layout.fragment_listar_nuvem, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //vamos de shared preferences
        val sharedPrefs: SharedPreferences = requireContext().getSharedPreferences("escolha_tipo_listagem", Context.MODE_PRIVATE)
        val listaEscolhida = sharedPrefs.getString("lista","")
        val escolhido: String

        escolhido = if (listaEscolhida.toString() == "avaliacoes"){
            "Avaliações"
        } else{
            "Autuações"
        }

        //zerando o shared preferences
        val editor = sharedPrefs.edit()
        editor.putString("lista", "" )
        editor.apply()

        txtTipoLista.text = escolhido

        //botão voltar
        btnVoltarNuven.setOnClickListener {
            findNavController().navigate(R.id.action_listarNuvemFragment_to_navigation_nuvem)
        }

        //avaliações

        if (escolhido == "Avaliações") {
            nuvemViewModel.avaliacoes(rootListaNuvemView, requireContext(), txtSelected)
        }

        //anotações/autuações
        if(escolhido == "Autuações"){
            nuvemViewModel.editAutuacao(rootListaNuvemView,requireContext(),txtSelected)
        }

        btnEditAval.setOnClickListener {
            if(txtTipoLista.text.toString() == "Avaliações"){
                if(txtSelected.text.isNullOrBlank()){
                    showSnackbar("É necessário escolher uma avaliação")
                } else{
                    findNavController().navigate(R.id.action_listarNuvemFragment_to_editAvalFragment)
                }
            }
            if(txtTipoLista.text.toString() == "Autuações"){
                if(txtSelected.text.isNullOrBlank()){
                    showSnackbar("É necessário escolher uma avaliação")
                } else{
                    findNavController().navigate(R.id.action_listarNuvemFragment_to_editAnotFragment)
                }

            }


        }

    }



    private fun showSnackbar(msg: String?) {
        val snack = Snackbar.make(rootListaNuvemView,msg.toString(), Snackbar.LENGTH_LONG)
        snack.show()
    }

}
