package anvisa.inflabnet.fiscalizacao.ui.resumo

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import anvisa.inflabnet.fiscalizacao.R
import anvisa.inflabnet.fiscalizacao.activities.MainActivity
import anvisa.inflabnet.fiscalizacao.cripto.CriptoString
import anvisa.inflabnet.fiscalizacao.database.model.Avaliacoes
import anvisa.inflabnet.fiscalizacao.database.service.AppDatabase
import anvisa.inflabnet.fiscalizacao.database.service.AppDatabaseService
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_resumo.*
import kotlinx.android.synthetic.main.frame_resumo.*

class ResumoFragment : Fragment() {

    private lateinit var resumoViewModel: ResumoViewModel
    private lateinit var appDatabase : AppDatabase
    private var mAuth: FirebaseAuth? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        resumoViewModel =
            ViewModelProviders.of(this).get(ResumoViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_resumo, container, false)

        val contextFrag = requireActivity().applicationContext
        appDatabase = AppDatabaseService.getInstance(contextFrag)

        return root
    }

    @SuppressLint("InflateParams")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //essa lista será usada duas situaçãoes: distinct() e contar quantas vezes um bairro está nela
        val listaDeBairros: MutableList<String> = mutableListOf()
        val bairrosLista = GetAllBairros().execute().get()

        //extraindo os bairros do objeto avaliacoes
        bairrosLista.forEach {
            it.bairro_estab?.getClearText()?.let { it1 -> listaDeBairros.add(it1) }
        }

        //gerando uma lista sem duplicatas
        val listUniqueBairros: List<String>
        listUniqueBairros = listaDeBairros.distinct()

        //Autocomplete
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            listUniqueBairros
        )

        acListBairros.setAdapter(adapter)
        acListBairros.threshold = 1
        acListBairros.text.toString()

        acListBairros.setOnDismissListener {
        }
        acListBairros.onFocusChangeListener = View.OnFocusChangeListener { _, b ->
            if (b) {
                // Sugestões dropdown
                acListBairros.showDropDown()
            }
        }


        //evento autocomplete
        acListBairros.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
            val selectedBairro = parent.getItemAtPosition(position) as String

            //escondendo o teclado
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(acListBairros.applicationWindowToken, 0)

            //preencher os dados - selectedBairro
            val mDialogView = LayoutInflater.from(context?.applicationContext).inflate(R.layout.frame_resumo, null)
            val mBuilder = AlertDialog.Builder(requireContext())
                .setView(mDialogView)
                //.setTitle("Bairro: ${selectedBairro}")
            val mAlertDialog = mBuilder.show()
            mAlertDialog.txtBairroresult.text = selectedBairro
            //contando quantas avaliações de um bairro
            var totBairros = 0
            listaDeBairros.forEach {
                if(selectedBairro.equals(it)){
                    totBairros += 1
                }
            }
            mAlertDialog.txtResultTotAvaliacoes.text = totBairros.toString()
            //contagem das respostas começa aqui usando bairrosLista
            var totR1= 0.0
            var totR2= 0.0
            var totR3= 0.0
            var totR4= 0.0
            var totR5= 0.0
            var totR6= 0.0
            val resp1Result: Double
            val resp2Result: Double
            val resp3Result: Double
            val resp4Result: Double
            val resp5Result: Double
            val resp6Result: Double
            bairrosLista.forEach {
                if (it.bairro_estab?.getClearText().equals(selectedBairro)){

                    if (it.rb1.toString().equals("true")){
                        totR1 += 1
                    }
                    if (it.rb2.toString().equals("true")){
                        totR2 += 1
                    }
                    if (it.rb3.toString().equals("true")){
                        totR3 += 1
                    }
                    if (it.rb4.toString().equals("true")){
                        totR4 += 1
                    }
                    if (it.rb5.toString().equals("true")){
                        totR5 += 1
                    }
                    if (it.rb6.toString().equals("true")){
                        totR6 += 1
                    }
                }
            }

            resp1Result = totR1/totBairros * 100
            resp2Result = totR2/totBairros * 100
            resp3Result = totR3/totBairros * 100
            resp4Result = totR4/totBairros * 100
            resp5Result = totR5/totBairros * 100
            resp6Result = totR6/totBairros * 100

            val txtResposta1Str = "$resp1Result% ok em $selectedBairro"
            val txtResposta2Str = "$resp2Result% ok em $selectedBairro"
            val txtResposta3Str = "$resp3Result% ok em $selectedBairro"
            val txtResposta4Str = "$resp4Result% ok em $selectedBairro"
            val txtResposta5Str = "$resp5Result% ok em $selectedBairro"
            val txtResposta6Str = "$resp6Result% ok em $selectedBairro"

            mAlertDialog.txtResposta1.text = txtResposta1Str
            mAlertDialog.txtResposta2.text = txtResposta2Str
            mAlertDialog.txtResposta3.text = txtResposta3Str
            mAlertDialog.txtResposta4.text = txtResposta4Str
            mAlertDialog.txtResposta5.text = txtResposta5Str
            mAlertDialog.txtResposta6.text = txtResposta6Str

            mAlertDialog.btnVoltarPerguntasFrame.setOnClickListener{
                mAlertDialog.dismiss()
            }
        }

        logoutBtnResumo.setOnClickListener {
            mAuth = FirebaseAuth.getInstance()
            mAuth!!.signOut()
            startActivity(
                Intent(requireContext(),
                    MainActivity::class.java)
            )
        }
    }

    @SuppressLint("StaticFieldLeak")
    inner class GetAllBairros:AsyncTask<Unit,Unit,List<Avaliacoes>>(){
        override fun doInBackground(vararg params: Unit?): List<Avaliacoes> {
            val listBairrosAll = appDatabase.avaliacoesDAO().getAllBairros()
            return listBairrosAll
        }

    }
}
