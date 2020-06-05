package anvisa.inflabnet.fiscalizacao.ui.consulta

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import anvisa.inflabnet.fiscalizacao.R
import anvisa.inflabnet.fiscalizacao.activities.MainActivity
import anvisa.inflabnet.fiscalizacao.database.model.Avaliacoes
import anvisa.inflabnet.fiscalizacao.database.service.AppDatabase
import anvisa.inflabnet.fiscalizacao.database.service.AppDatabaseService
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_consulta.*
import kotlinx.android.synthetic.main.frame_consulta.*
import kotlinx.android.synthetic.main.frame_consulta.view.*
import kotlinx.android.synthetic.main.frame_consulta.view.btnVoltarPerguntasFrame
import kotlinx.android.synthetic.main.frame_consulta.view.txtBairro
import kotlinx.android.synthetic.main.frame_consulta.view.txtEstab
import kotlinx.android.synthetic.main.frame_consulta.view.txtResposta1
import kotlinx.android.synthetic.main.frame_consulta.view.txtResposta2
import kotlinx.android.synthetic.main.frame_consulta.view.txtResposta3
import kotlinx.android.synthetic.main.frame_consulta.view.txtResposta4
import kotlinx.android.synthetic.main.frame_consulta.view.txtResposta5
import kotlinx.android.synthetic.main.frame_consulta.view.txtResposta6

class ConsultaFragment : Fragment() {

    private lateinit var consultaViewModel: ConsultaViewModel
    private lateinit var appDatabase : AppDatabase
    private var mAuth: FirebaseAuth? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val contextFrag = requireActivity().applicationContext
        appDatabase = AppDatabaseService.getInstance(contextFrag)

        consultaViewModel =
            ViewModelProviders.of(this).get(ConsultaViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_consulta, container, false)

        return root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //ID do avaliador
        val idFiscalLogado = GetIdAvaliador().execute(101).get()

        //pegar todos os estabelecimentos do avaliador logado
        val listaTodos = GetEstabelecimentos().execute(idFiscalLogado).get()

        val linearLayoutManager = LinearLayoutManager(requireActivity().applicationContext)
        rvAvaliacoes.layoutManager = linearLayoutManager
        rvAvaliacoes.scrollToPosition(listaTodos!!.size)
        rvAvaliacoes.adapter =
            AvaliacoesAdapter(listaTodos) {
                val mDialogView = LayoutInflater.from(context?.applicationContext).inflate(R.layout.frame_consulta, null)
                val mBuilder = AlertDialog.Builder(requireContext())
                    .setView(mDialogView)
                    //.setTitle("Restaurante: ${it.avalId}")
                val mAlertDialog = mBuilder.show()
                mAlertDialog.txtEstab.text = it.estabelecimento?.getClearText()
                mAlertDialog.txtBairro.text = it.bairro_estab?.getClearText()
                mAlertDialog.txtResposta1.text = if(it.rb1 == true) "Sim" else "Não"
                mAlertDialog.txtResposta2.text = if(it.rb2 == true) "Sim" else "Não"
                mAlertDialog.txtResposta3.text = if(it.rb3 == true) "Sim" else "Não"
                mAlertDialog.txtResposta4.text = if(it.rb4 == true) "Sim" else "Não"
                mAlertDialog.txtResposta5.text = if(it.rb5 == true) "Sim" else "Não"
                mAlertDialog.txtResposta6.text = if(it.rb6 == true) "Sim" else "Não"
                mAlertDialog.btnVoltarPerguntasFrame.setOnClickListener{
                    mAlertDialog.dismiss()
                }
            }

        logoutBtnConsulta.setOnClickListener {
            mAuth = FirebaseAuth.getInstance()
            mAuth!!.signOut()
            startActivity(
                Intent(requireContext(),
                    MainActivity::class.java)
            )
        }

    }

    @SuppressLint("StaticFieldLeak")
    inner class GetEstabelecimentos: AsyncTask<String,Unit,List<Avaliacoes>>(){
        override fun doInBackground(vararg params: String?): List<Avaliacoes> {
            val listaAval = appDatabase.avaliacoesDAO().getAll(params[0]!!)
            return listaAval
        }

    }

    @SuppressLint("StaticFieldLeak")
    inner class GetIdAvaliador: AsyncTask<Int,Unit,String>() {
        override fun doInBackground(vararg params: Int?): String {
            val idFiscal = appDatabase.fiscalDAO().getIdAvaliadorAtual(params[0]!!)
            return idFiscal
        }
    }
}
