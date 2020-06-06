package anvisa.inflabnet.fiscalizacao.ui.cadastro

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import anvisa.inflabnet.fiscalizacao.R
import anvisa.inflabnet.fiscalizacao.activities.MainActivity
import anvisa.inflabnet.fiscalizacao.database.model.Fiscal
import anvisa.inflabnet.fiscalizacao.database.service.AppDatabase
import anvisa.inflabnet.fiscalizacao.database.service.AppDatabaseService
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_cadastro.*


class CadastroFragment : Fragment() {

    private lateinit var cadastroViewModel: CadastroViewModel
    private lateinit var appDatabase : AppDatabase
    private var mAuth: FirebaseAuth? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        cadastroViewModel =
            ViewModelProviders.of(requireActivity()).get(CadastroViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_cadastro, container, false)
//        val textView: TextView = root.findViewById(R.id.text_home)
//        cadastroViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })

        val contextFrag = requireActivity().applicationContext
        appDatabase = AppDatabaseService.getInstance(contextFrag)
        return root
    }

    override fun onResume() {
        super.onResume()

        //verificar se há valores
        val sharedPrefsChech: SharedPreferences = requireContext().getSharedPreferences("Armazenamento_Temporario", Context.MODE_PRIVATE)
        val municipioShP = sharedPrefsChech.getString("municipio","_Município$")
        val bairroShP = sharedPrefsChech.getString("bairro","_Bairro$")
        val estabelecimentoShP = sharedPrefsChech.getString("estabelecimento","_Estabelecimento$")

        if(!municipioShP.isNullOrBlank()){
            acMunicipios.setText(municipioShP)
        }
        if(municipioShP.equals("_Município$")){
            acMunicipios.setText("")
        }
        if(!bairroShP.isNullOrBlank()){
            acBairros.setText(bairroShP)
        }
        if (bairroShP.equals("_Bairro$")) {
            acBairros.setText("")
        }
        if(!estabelecimentoShP.isNullOrBlank()){
            edtEstabelecimento.setText(estabelecimentoShP)
        }
        if(estabelecimentoShP.equals("_Estabelecimento$")){
            edtEstabelecimento.setText("")
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //deu trabalho, mas duas APIs para consulta de municipios do RJ e bairros do Rio
        cadastroViewModel.listaMunicipiosAutoComplete(requireContext(), acMunicipios)
        cadastroViewModel.listaBairrosAutoComplete(requireContext(),acBairros)

        btnProximo.setOnClickListener {
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(btnProximo.windowToken, 0)

            if(acMunicipios.text.toString().isBlank() || acBairros.text.toString().isBlank() || edtEstabelecimento.text.toString().isBlank()){
                showSnackbar("Favro preencher todos os campos!")
            } else{
                //viewmodel só para dizer que tentei
                cadastroViewModel.setMunicipio(edtEstabelecimento.text.toString())

                //shared preferences funciona
                val sharedPrefs: SharedPreferences = requireContext().getSharedPreferences("Armazenamento_Temporario", Context.MODE_PRIVATE)
                val editor = sharedPrefs.edit()
                editor.putString("municipio", acMunicipios.text.toString())
                editor.putString("bairro", acBairros.text.toString())
                editor.putString("estabelecimento", edtEstabelecimento.text.toString())
                editor.apply()


                findNavController().navigate(R.id.action_navigation_cadastro_to_perguntasFragment )
            }
        }

        logoutBtn.setOnClickListener {
            mAuth = FirebaseAuth.getInstance()
            mAuth!!.signOut()
            startActivity(
                Intent(requireContext(),
                    MainActivity::class.java)
            )
        }
    }

    //só para verificar os Ids dos fiscais
    @SuppressLint("StaticFieldLeak")
    inner class ShowId: AsyncTask<Unit,Unit,Array<Fiscal>>(){
        override fun doInBackground(vararg params: Unit?): Array<Fiscal>? {
            val id = appDatabase.fiscalDAO().show()
            return id
        }

        override fun onPostExecute(result: Array<Fiscal>?) {
                Toast.makeText(requireContext(),"O id atual é ${result?.get(0)!!.id_fiscal}",Toast.LENGTH_SHORT).show()
        }
    }





    private fun showSnackbar(msg: String?) {
        val snack = Snackbar.make(novoEstabRoot,msg.toString(), Snackbar.LENGTH_LONG)
        snack.show()
    }
}
