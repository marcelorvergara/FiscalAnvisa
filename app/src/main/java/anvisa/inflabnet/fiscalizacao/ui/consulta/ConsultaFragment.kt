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
import androidx.navigation.fragment.findNavController
import anvisa.inflabnet.fiscalizacao.R
import anvisa.inflabnet.fiscalizacao.database.service.AppDatabase
import anvisa.inflabnet.fiscalizacao.database.service.AppDatabaseService
import anvisa.inflabnet.fiscalizacao.database.tabelas.Avaliacoes
import anvisa.inflabnet.fiscalizacao.login.MainActivity
import anvisa.inflabnet.fiscalizacao.ui.consulta.viewmodel.ConsultaViewModel
import com.facebook.login.LoginManager
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

        return inflater.inflate(R.layout.fragment_consulta, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnAutuacoesLista.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_consulta_to_listaAutuacoesFragment)
        }

        //ID do avaliador
        val idFiscalLogado = GetIdAvaliador().execute(101).get()

        //pegar todos os estabelecimentos do avaliador logado
        val listaTodos = GetEstabelecimentos().execute(idFiscalLogado).get()

        consultaViewModel.montaAlertDialog(listaTodos,rvAvaliacoes,requireActivity(),requireContext(),context?.applicationContext,consultaLayout)

        logoutBtnConsulta.setOnClickListener {
            LoginManager.getInstance().logOut()
            mAuth = FirebaseAuth.getInstance()
            mAuth!!.signOut()

            val novoIntt = Intent(requireContext(), MainActivity::class.java)
            novoIntt.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(novoIntt)

        }

    }


    @SuppressLint("StaticFieldLeak")
    inner class GetEstabelecimentos: AsyncTask<String,Unit,List<Avaliacoes>>(){
        override fun doInBackground(vararg params: String?): List<Avaliacoes> {
            return appDatabase.avaliacoesDAO().getAll(params[0]!!)
        }

    }

    @SuppressLint("StaticFieldLeak")
    inner class GetIdAvaliador: AsyncTask<Int,Unit,String>() {
        override fun doInBackground(vararg params: Int?): String {
            return appDatabase.fiscalDAO().getIdAvaliadorAtual(params[0]!!)
        }
    }
}
