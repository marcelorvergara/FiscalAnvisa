package anvisa.inflabnet.fiscalizacao.ui.consulta

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import anvisa.inflabnet.fiscalizacao.R
import anvisa.inflabnet.fiscalizacao.database.service.AppDatabase
import anvisa.inflabnet.fiscalizacao.database.service.AppDatabaseService
import anvisa.inflabnet.fiscalizacao.database.tabelas.Anotacoes
import anvisa.inflabnet.fiscalizacao.login.MainActivity
import anvisa.inflabnet.fiscalizacao.ui.consulta.adapter.AutuacoesAdapter
import anvisa.inflabnet.fiscalizacao.ui.consulta.viewmodel.ConsultaViewModel
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_lista_autuacoes.*
import kotlinx.android.synthetic.main.frame_anotacao.*
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader

/**
 * A simple [Fragment] subclass.
 */
class ListaAutuacoesFragment : Fragment() {

    private lateinit var consultaViewModel: ConsultaViewModel
    private lateinit var appDatabase : AppDatabase
    private var mAuth: FirebaseAuth? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val contextFrag = requireActivity().applicationContext
        appDatabase = AppDatabaseService.getInstance(contextFrag)

        consultaViewModel =
            ViewModelProviders.of(this).get(ConsultaViewModel::class.java)
        return inflater.inflate(R.layout.fragment_lista_autuacoes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        mAuth = FirebaseAuth.getInstance()

        var loginUser = mAuth?.currentUser?.displayName
        if (loginUser.isNullOrBlank()){
            loginUser=mAuth?.currentUser?.email
        }
        txtFiscalLista.text = loginUser

        //ID do avaliador
        val idFiscalLogado = GetIdAvaliador().execute(101).get()

        //pegar autuações
        val listAutuacoes = GetAutuacoes().execute(idFiscalLogado).get()

        val linearLayoutManager = LinearLayoutManager(requireActivity().applicationContext)
        rvListAutu.layoutManager = linearLayoutManager
        rvListAutu.scrollToPosition(listAutuacoes.size)
        rvListAutu.adapter =
            AutuacoesAdapter(
                listAutuacoes
            ) {

                val mDialogView = LayoutInflater.from(requireContext())
                    .inflate(R.layout.frame_anotacao, rootListaAnot, false)
                val mBuilder = AlertDialog.Builder(requireContext())
                    .setView(mDialogView)
                //.setTitle("Bairro: ${selectedBairro}")

                val mAlertDialog = mBuilder.show()
                mAlertDialog.txtAutTit.text = it.tituloAnotcao
                //foto
                val imageDecode = it.pathPhoto!!.getClearText()
                val imageConverted = imageDecode?.let { it1 -> base64ToBitmap(it1) }
                mAlertDialog.imgAutu.setImageBitmap(imageConverted)
                mAlertDialog.txtAutuacao.append(it.pathTxt?.let { it1 -> lerNota(it1) })

                mAlertDialog.btnBacktoTheFuture.setOnClickListener {
                    mAlertDialog.dismiss()
                }
            }

        voltarBtnconsulta.setOnClickListener {
            findNavController().navigate(R.id.navigation_consulta)
        }

        logoutBtnListar.setOnClickListener {
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

    private fun base64ToBitmap(b64: String): Bitmap? {
        val imageAsBytes: ByteArray = android.util.Base64.decode(b64.toByteArray(), android.util.Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.size)
    }

    @SuppressLint("StaticFieldLeak")
    inner class GetAutuacoes: AsyncTask<String, Unit, List<Anotacoes>>() {
        override fun doInBackground(vararg params: String?): List<Anotacoes> {
            return appDatabase.anotacoesDAO().getAll(params[0]!!)
        }
    }

    @SuppressLint("StaticFieldLeak")
    inner class GetIdAvaliador: AsyncTask<Int, Unit, String>() {
        override fun doInBackground(vararg params: Int?): String {
            return appDatabase.fiscalDAO().getIdAvaliadorAtual(params[0]!!)
        }
    }
}
