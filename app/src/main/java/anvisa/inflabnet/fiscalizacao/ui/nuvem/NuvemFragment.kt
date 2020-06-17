package anvisa.inflabnet.fiscalizacao.ui.nuvem

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import anvisa.inflabnet.fiscalizacao.R
import anvisa.inflabnet.fiscalizacao.database.service.AppDatabase
import anvisa.inflabnet.fiscalizacao.database.service.AppDatabaseService
import anvisa.inflabnet.fiscalizacao.database.tabelas.Anotacoes
import anvisa.inflabnet.fiscalizacao.database.tabelas.Avaliacoes
import anvisa.inflabnet.fiscalizacao.login.MainActivity
import anvisa.inflabnet.fiscalizacao.ui.nuvem.anotacoes.AnotacaoDecipt
import anvisa.inflabnet.fiscalizacao.ui.nuvem.viewmodel.NuvemViewModel
import com.facebook.login.LoginManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_nuvem.*
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader

class NuvemFragment : Fragment() {

    private lateinit var nuvemViewModel: NuvemViewModel
    private lateinit var appDatabase : AppDatabase
    private var mAuth: FirebaseAuth? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        nuvemViewModel =
            ViewModelProviders.of(this).get(NuvemViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_nuvem, container, false)

        val contextFrag = requireActivity().applicationContext
        appDatabase = AppDatabaseService.getInstance(contextFrag)

        return root
    }

    @SuppressLint("InflateParams")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //shared preferences para zerar opção escolhida de lista
        val sharedPrefs: SharedPreferences = requireContext().getSharedPreferences(
            "escolha_tipo_listagem",
            Context.MODE_PRIVATE
        )
        val editor = sharedPrefs.edit()
        editor.putString("lista", "" )
        editor.apply()

        btnUpload.setOnClickListener {
            val checked = rgUpload.checkedRadioButtonId
            if(checked != -1){
                when (resources.getResourceEntryName(checked)){
                    "rbUpAvaliacoes" -> {
                        //pegar as informações do SQLite
                        //ID do avaliador logado
                        val idFiscalLogado = GetIdAvaliador().execute(101).get()
                        val listaAvaliacoes = ConsultaAvaliacoes().execute(idFiscalLogado).get()
                        //subir avaliações
                        val firebaseFirestore = FirebaseFirestore.getInstance()
                        val collection = firebaseFirestore.collection("Avaliacoes")
                        listaAvaliacoes.forEach {
                            val idDoc = it.avalId.toString() + "_" + it.id_fiscal_aval
                            val task = collection.document(idDoc).set(it)
                            //deletar os dados locais SQLite
                            DeleteAvaliacao().execute(it)
                        }
                        showSnackbar("Upload das avaliações realizao com sucesso!")

                    }
                    "rbUpAutuacoes" -> {
                        //pegar as informações do SQLite
                        //ID do avaliador logado
                        val idFiscalLogado = GetIdAvaliador().execute(101).get()
                        val listaAutuacoes = ConsultaAutuacoes().execute(idFiscalLogado).get()
                        //subir avaliações
                        val firebaseFirestore = FirebaseFirestore.getInstance()
                        val collection = firebaseFirestore.collection("Anotacoes")
                        listaAutuacoes.forEach {
                            //criar novo objeto para descriptografar o texto da anotação/autuação
                            val idFiscal = it.idFiscal
                            val titAutuacao = it.tituloAnotcao
                            val dataAutuacao = it.dataAnota
                            val notaAnotacao = lerNota(it.pathTxt!!)
                            val fotoAnotacao = it.pathPhoto
                            val estabeleAnotacao = it.estabelecimento
                            val idAnotacao = it.idAnotacao
                            val anotacaoObj =
                                AnotacaoDecipt(
                                    titAutuacao!!,
                                    estabeleAnotacao!!
                                )
                            //subindo para o firebase
                            val idDoc = it.idAnotacao.toString() + "_" + it.idFiscal
                            val task = collection.document(idDoc).set(anotacaoObj)
                            //deletar as anotações/autuações do SQLite local
                            DeleteAutuacoes().execute(it)
                        }
                        showSnackbar("Upload das autuações/anotações realizao com sucesso!")

                    }
                    "rbUpAmbos" -> {
                        //pegar as informações do SQLite
                        //ID do avaliador logado
                        val idFiscalLogado = GetIdAvaliador().execute(101).get()
                        val firebaseFirestore = FirebaseFirestore.getInstance()
                        val listaAvaliacoes = ConsultaAvaliacoes().execute(idFiscalLogado).get()
                        val listaAutuacoes = ConsultaAutuacoes().execute(idFiscalLogado).get()
                        val collectionAval = firebaseFirestore.collection("Avaliacoes")
                        listaAvaliacoes.forEach {
                            val idDoc = it.avalId.toString() + "_" + it.id_fiscal_aval
                            val task = collectionAval.document(idDoc).set(it)
                            //deletar os dados locais SQLite
                            DeleteAvaliacao().execute(it)
                        }
                        val collectionAnota = firebaseFirestore.collection("Anotacoes")
                        listaAutuacoes.forEach {
                            //criar novo objeto para descriptografar o texto da anotação/autuação
                            val idFiscal = it.idFiscal
                            val titAutuacao = it.tituloAnotcao
                            val dataAutuacao = it.dataAnota
                            val notaAnotacao = lerNota(it.pathTxt!!)
                            val fotoAnotacao = it.pathPhoto
                            val estabeleAnotacao = it.estabelecimento
                            val idAnotacao = it.idAnotacao
                            val anotacaoObj =
                                AnotacaoDecipt(
                                    titAutuacao!!,
                                    estabeleAnotacao!!
                                )
                            //subindo para o firebase
                            val idDoc = it.idAnotacao.toString() + "_" + it.idFiscal
                            val task = collectionAnota.document(idDoc).set(anotacaoObj)
                            //deletar as anotações/autuações do SQLite local
                            DeleteAutuacoes().execute(it)
                        }
                        showSnackbar("Upload das avaliações e das autuações/anotações realizao com sucesso!")
                    }
                }

            } else {
                showSnackbar("Favor excolher uma das opções!")
            }
        }

        btnListar.setOnClickListener {
            val checked = rgListar.checkedRadioButtonId
            if(checked != -1){
                when(resources.getResourceEntryName(checked)){
                    "rbListarAvaliacoes" ->{

                        //shared preferences para informar a opção escolhida
                        val sharedPrefsAval: SharedPreferences = requireContext().getSharedPreferences(
                            "escolha_tipo_listagem",
                            Context.MODE_PRIVATE
                        )
                        val editorAval = sharedPrefsAval.edit()
                        editorAval.putString("lista", "avaliacoes" )
                        editorAval.apply()
                        findNavController().navigate(R.id.action_navigation_nuvem_to_listarNuvemFragment)

                    }
                    "rbListarAutuacoes"->{

                        //shared preferences para informar a opção escolhida
                        val sharedPrefsAnot: SharedPreferences = requireContext().getSharedPreferences(
                            "escolha_tipo_listagem",
                            Context.MODE_PRIVATE
                        )
                        val editorAnot = sharedPrefsAnot.edit()
                        editorAnot.putString("lista", "autuacoes" )
                        editorAnot.apply()
                        findNavController().navigate(R.id.action_navigation_nuvem_to_listarNuvemFragment)

                    }
                }
            }
        }


        //logout
        logoutBtnResumo.setOnClickListener {
            LoginManager.getInstance().logOut()
            mAuth = FirebaseAuth.getInstance()
            mAuth!!.signOut()

            val novoIntt = Intent(requireContext(), MainActivity::class.java)
            novoIntt.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(novoIntt)

        }
    }

    @Throws(IOException::class)
    fun lerNota(nomeNota: String): String{
        //val storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val masterKeyAlias: String = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        //val nota = File(storageDir.toString()+"/"+nomeNota)
        val nota = File(nomeNota)
        val encryptedIn = EncryptedFile.Builder(
            nota, requireContext(), masterKeyAlias,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build().openFileInput()
        val br = BufferedReader(InputStreamReader(encryptedIn))
        val sb = StringBuffer()
        br.lines().forEach{ t -> sb.append(t+"\n") }
        encryptedIn.close()
        return sb.toString()
    }

    @SuppressLint("StaticFieldLeak")
    inner class DeleteAutuacoes: AsyncTask<Anotacoes,Unit,Unit>() {
        override fun doInBackground(vararg params: Anotacoes?) {
            appDatabase.anotacoesDAO().deleteAutuacao(params[0]!!)
        }

    }

    @SuppressLint("StaticFieldLeak")
    inner class ConsultaAutuacoes: AsyncTask<String,Unit,List<Anotacoes>>() {
        override fun doInBackground(vararg params: String?):List<Anotacoes> {
            return appDatabase.anotacoesDAO().getAll(params[0]!!)
        }

    }

    @SuppressLint("StaticFieldLeak")
    inner class DeleteAvaliacao:AsyncTask<Avaliacoes,Unit,Unit>() {
        override fun doInBackground(vararg params: Avaliacoes?) {
            appDatabase.avaliacoesDAO().deleteAval(params[0]!!)
        }

    }

    @SuppressLint("StaticFieldLeak")
    inner class GetIdAvaliador: AsyncTask<Int, Unit, String>() {
        override fun doInBackground(vararg params: Int?): String {
            return appDatabase.fiscalDAO().getIdAvaliadorAtual(params[0]!!)
        }
    }

    @SuppressLint("StaticFieldLeak")
    inner class ConsultaAvaliacoes: AsyncTask<String,Unit,List<Avaliacoes>>() {
        override fun doInBackground(vararg params: String?): List<Avaliacoes> {
            return appDatabase.avaliacoesDAO().getAll(params[0]!!)
        }

    }

    private fun showSnackbar(msg: String?) {
        val snack = Snackbar.make(nuvemLayout,msg.toString(), Snackbar.LENGTH_LONG)
        snack.show()
    }

}



