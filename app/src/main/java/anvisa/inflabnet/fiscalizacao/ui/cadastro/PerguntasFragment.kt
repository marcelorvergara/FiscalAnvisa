package anvisa.inflabnet.fiscalizacao.ui.cadastro

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import anvisa.inflabnet.fiscalizacao.viewmodel.AppViewModel
import anvisa.inflabnet.fiscalizacao.R
import anvisa.inflabnet.fiscalizacao.cripto.CriptoString
import anvisa.inflabnet.fiscalizacao.database.model.Avaliacoes
import anvisa.inflabnet.fiscalizacao.database.service.AppDatabase
import anvisa.inflabnet.fiscalizacao.database.service.AppDatabaseService
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_perguntas.*


class PerguntasFragment : Fragment() {

    private lateinit var appDatabase : AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val contextFrag = requireActivity().applicationContext
        appDatabase = AppDatabaseService.getInstance(contextFrag)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_perguntas, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnVoltarPerguntas.setOnClickListener {
            findNavController().navigate(R.id.action_perguntasFragment_to_navigation_cadastro)
        }

        //viewmodel não funciona
        var appViewModel: AppViewModel? = null
        activity?.let {
            appViewModel = ViewModelProviders.of(it)[AppViewModel::class.java]
        }

        appViewModel?.estabelecimento?.observe(viewLifecycleOwner, object : Observer<Any>{
            override fun onChanged(t: Any?) {
                txtBairro.text = t.toString()
            }
        })

        btnGravar.setOnClickListener {

            var resiltado1 = false
            var resiltado2 = false
            var resiltado3 = false
            var resiltado4 = false
            var resiltado5 = false
            var resiltado6 = false

            //pergunta 1
            val checked1 = rgPergunta1.checkedRadioButtonId
            if (checked1 != -1) {
                val result = resources.getResourceEntryName(checked1)
                when(result){
                    "radioButton1" -> {
                        resiltado1 = true
                    }
                    "radioButton2" -> {
                        resiltado1 = false
                    }
                }
            }

            //pergunta 2
            val checked2 = rgPergunta2.checkedRadioButtonId
            if (checked2 != -1) {
                val result = resources.getResourceEntryName(checked2)
                when(result){
                    "radioButton3" -> {
                        resiltado2 = true
                    }
                    "radioButton4" -> {
                        resiltado2 = false
                    }
                }
            }

            //pergunta 3
            val checked3 = rgPergunta3.checkedRadioButtonId
            if (checked3 != -1) {
                val result = resources.getResourceEntryName(checked3)
                when(result){
                    "radioButton5" -> {
                        resiltado3 = true
                    }
                    "radioButton6" -> {
                        resiltado3 = false
                    }
                }
            }

            //pergunta 4
            val checked4 = rgPergunta4.checkedRadioButtonId
            if (checked4 != -1) {
                val result = resources.getResourceEntryName(checked4)
                when(result){
                    "radioButton7" -> {
                        resiltado4 = true
                    }
                    "radioButton8" -> {
                        resiltado4 = false
                    }
                }
            }

            //pergunta 5
            val checked5 = rgPergunta5.checkedRadioButtonId
            if (checked5 != -1) {
                val result = resources.getResourceEntryName(checked5)
                when(result){
                    "radioButton9" -> {
                        resiltado5 = true
                    }
                    "radioButton10" -> {
                        resiltado5 = false
                    }
                }
            }

            //pergunta 6
            val checked6 = rgPergunta6.checkedRadioButtonId
            if (checked6 != -1) {
                val result = resources.getResourceEntryName(checked6)
                when(result){
                    "radioButton11" -> {
                        resiltado6 = true
                    }
                    "radioButton12" -> {
                        resiltado6 = false
                    }
                }
            }

            //checar se estão marcados
            if(checked1 == -1 || checked2 == -1 || checked3 == -1 || checked4 == -1 || checked5 == -1 || checked6 == -1){
                showSnackbar("É necessário marcar todas as opções!")
            } else{
                //vamos de shared preferences
                val sharedPrefs: SharedPreferences = requireContext().getSharedPreferences("Armazenamento_Temporario", Context.MODE_PRIVATE)
                val municipioShP = sharedPrefs.getString("municipio","Município")
                val bairroShPSpaces = sharedPrefs.getString("bairro","Bairro")
                val estabelecimentoShP = sharedPrefs.getString("estabelecimento","Estabelecimento")
                //removendo os espaços no final do bairro que vem do JSON
                val bairroShP = bairroShPSpaces?.trimEnd()

                //ID do avaliador
                val idFiscalLogado = GetIdAvaliador().execute(101).get()

                //cripto
                val estabelecimentoCripto = CriptoString()
                estabelecimentoCripto.setClearText(estabelecimentoShP)
                val bairroCripto = CriptoString()
                bairroCripto.setClearText(bairroShP)

                //montar o objeto a ser guardado no banco
                val avaliacaoObj = Avaliacoes(
                    idFiscalLogado,
                    estabelecimentoCripto,
                    municipioShP.toString(),
                    bairroCripto,
                    rb1 = resiltado1,
                    rb2 = resiltado2,
                    rb3 = resiltado3,
                    rb4 = resiltado4,
                    rb5 = resiltado5,
                    rb6 = resiltado6
                )

                //gravar no banco
                GuardarAvaliacao().execute(avaliacaoObj)

                //apagar Shared Preference
                val clearSp = sharedPrefs.edit()
                clearSp.clear()
                clearSp.apply()

                //voltar para tela inicial
                findNavController().navigate(R.id.action_perguntasFragment_to_navigation_cadastro)

            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    inner class GuardarAvaliacao: AsyncTask<Avaliacoes,Unit,Unit>() {
        override fun doInBackground(vararg params: Avaliacoes?) {
            appDatabase.avaliacoesDAO().guardaAvaliacao(params[0]!!)
        }

        override fun onPostExecute(result: Unit?) {
            showSnackbar("Avaliação guardada com sucesso!")
        }
    }

    @SuppressLint("StaticFieldLeak")
    inner class GetIdAvaliador: AsyncTask<Int,Unit,String>() {
        override fun doInBackground(vararg params: Int?): String {
            val idFiscal = appDatabase.fiscalDAO().getIdAvaliadorAtual(params[0]!!)
            return idFiscal
        }
    }

    private fun showSnackbar(msg: String?) {
        val snack = Snackbar.make(perguntasRoot,msg.toString(), Snackbar.LENGTH_LONG)
        snack.show()
    }
}




