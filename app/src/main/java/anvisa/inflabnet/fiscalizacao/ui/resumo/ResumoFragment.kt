package anvisa.inflabnet.fiscalizacao.ui.resumo

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
import kotlinx.android.synthetic.main.fragment_resumo.*

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
        resumoViewModel.acListBairros(listUniqueBairros,acListBairros,requireContext())

        //evento autocomplete
        resumoViewModel.autocompListaBairros(
            listaDeBairros,
            bairrosLista,
            acListBairros,
            requireContext(),
            resumoLayout
        )

        //logout
        logoutBtnResumo.setOnClickListener {
            mAuth = FirebaseAuth.getInstance()
            mAuth!!.signOut()
            startActivity(
                Intent(requireContext(),
                    MainActivity::class.java
                )
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
