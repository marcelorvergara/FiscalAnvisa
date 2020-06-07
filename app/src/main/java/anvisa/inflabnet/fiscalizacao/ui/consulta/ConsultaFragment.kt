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
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import anvisa.inflabnet.fiscalizacao.R
import anvisa.inflabnet.fiscalizacao.activities.MainActivity
import anvisa.inflabnet.fiscalizacao.database.model.Avaliacoes
import anvisa.inflabnet.fiscalizacao.database.service.AppDatabase
import anvisa.inflabnet.fiscalizacao.database.service.AppDatabaseService
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_consulta.*

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

        consultaViewModel.montaAlertDialog(listaTodos,rvAvaliacoes,requireActivity(),requireContext(),context?.applicationContext,consultaLayout)

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
