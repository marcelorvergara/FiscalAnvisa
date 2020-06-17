package anvisa.inflabnet.fiscalizacao.ui.cadastro

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import anvisa.inflabnet.fiscalizacao.R
import anvisa.inflabnet.fiscalizacao.cripto.CriptoString
import anvisa.inflabnet.fiscalizacao.database.service.AppDatabase
import anvisa.inflabnet.fiscalizacao.database.service.AppDatabaseService
import anvisa.inflabnet.fiscalizacao.database.tabelas.Anotacoes
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_cad_autuacao.*
import kotlinx.android.synthetic.main.fragment_consulta.*
import kotlinx.android.synthetic.main.frame_data_autuacao.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.PrintWriter
import java.util.*
import kotlin.random.Random

class CadAutuacaoFragment : Fragment() {

    private lateinit var appDatabase : AppDatabase
    //foto
    private var photoFile: File? = null
    private val CAPTURE_IMAGE_REQUEST = 1
    private lateinit var mCurrentPhotoPath: String

    //variaveis para nome de arquivo
    private var tituloAutuacao:String? = null
    private var dataAutuacao:String? = null
    private var estabelecimentoShP:String? =null

    private var mAuth: FirebaseAuth? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val contextFrag = requireActivity().applicationContext
        appDatabase = AppDatabaseService.getInstance(contextFrag)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cad_autuacao, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = FirebaseAuth.getInstance()

        var loginUser = mAuth?.currentUser?.displayName
        if (loginUser.isNullOrBlank()){
            loginUser=mAuth?.currentUser?.email
        }
        txtFiscalCad.text = loginUser

        //pega a título e data da autuação
        perguntaData()
        txtTituloAutuacao.text = tituloAutuacao

        foodView.setOnClickListener{
            captureImage()
        }

        //Shared Preferences para guardar valores entre fragments
        val sharedPrefs: SharedPreferences = requireContext().getSharedPreferences("Armazenamento_Temporario", Context.MODE_PRIVATE)
        val bairroShPSpaces = sharedPrefs.getString("bairro","Bairro")
        estabelecimentoShP = sharedPrefs.getString("estabelecimento","Estabelecimento")
        val latitudeShP = sharedPrefs.getString("lat", "Latitude")
        val longitudeShP = sharedPrefs.getString("lon","Longitude")

        //ID do avaliador
        val idFiscalLogado = GetIdAvaliador().execute(101).get()

        txtNomeRestauranteAvaliacao.text = estabelecimentoShP.toString()

        update2Btn.setOnClickListener {
            if(txtTituloAutuacao.text.isBlank() || edtTextoAutuacao.text.isBlank() || edtDataAvaliacao.text.isBlank() || !this::mCurrentPhotoPath.isInitialized){
                Toast.makeText(requireContext(),"Favor preencher todos os campos.\nInclusive a foto", Toast.LENGTH_SHORT).show()
            }else{
                //nome doc.
                tituloAutuacao = estabelecimentoShP + "-" + txtTituloAutuacao.text.toString().trimEnd()
                dataAutuacao = edtDataAvaliacao.text.toString()
                val textoAutuacao = "[" + latitudeShP + "/" + longitudeShP + "] \n" + edtTextoAutuacao.text.toString()

                //foto
                val photBitmap = foodView.drawable.toBitmap(220,160)
                val imageStr = bitmapToBase64(photBitmap)
                val imageStrCripto = CriptoString()
                //criptografa string da foto
                imageStrCripto.setClearText(imageStr)

                //Arquivo texto autuação
                val filePath = gravarAutuacao(textoAutuacao)

                //objeto SQLite
                val anotacaoObj = (Anotacoes(idFiscalLogado,txtTituloAutuacao.text.toString().trimEnd(),
                    dataAutuacao!!,filePath.toString(),imageStrCripto, estabelecimentoShP!!,null))
                //guardar objetos
                GuardarAnotacao().execute(anotacaoObj)

                //atualizar tabela avaliações caso estabelecimento exista
                AtualizaAval().execute(estabelecimentoShP,bairroShPSpaces!!.trimEnd())

                //apagar Shared Preference
                val clearSp = sharedPrefs.edit()
                clearSp.clear()
                clearSp.apply()

                //apagar foto
                val fotoDelete = File(mCurrentPhotoPath)
                fotoDelete.delete()

                showSnackbar("Autuação anotada com sucesso!")

                //voltar para tela inicial
                findNavController().navigate(R.id.action_cadAutuacaoFragment_to_navigation_cadastro)

            }

        }

        voltarrrrBtn.setOnClickListener {
            findNavController().navigate(R.id.navigation_cadastro)
        }
    }

    @SuppressLint("StaticFieldLeak")
    inner class AtualizaAval: AsyncTask<String,Unit,Unit>(){
        override fun doInBackground(vararg params: String?) {
            val establishment = params[0]!!
            val baird = params[1]!!
            val trueAnotacao = true
            val listEstab = appDatabase.avaliacoesDAO().getAllBairros()
            listEstab.forEach {
                if (it.bairro_estab?.getClearText() == baird && it.estabelecimento?.getClearText() == establishment){
                    val idAval = it.avalId
                    appDatabase.avaliacoesDAO().updateAvalAnot(idAval,true)
                }
            }



        }

    }

    @Throws(IOException::class)
    private fun gravarAutuacao(texto: String): File{
        val masterKeyAlias: String = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        val newData = dataAutuacao?.replace("/","")
        val txtFileName = tituloAutuacao + "_" + newData + "_"
        val storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        var nota = File(storageDir.toString() + "/" + txtFileName + ".txt")
        var fileExist = false
        val listFiles = storageDir?.list()
        listFiles?.forEach {
            if(it == "$txtFileName.txt"){
                fileExist = true
            }
        }
        if(fileExist){
            val randomNum = Random.nextInt(25)+1
            nota = File(storageDir.toString() + "/" + txtFileName + randomNum +".txt")
            showSnackbar("Novo nome: ${"$txtFileName$randomNum.txt"}")
            val encryptedOut = EncryptedFile.Builder(
                nota, requireContext(), masterKeyAlias,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build().openFileOutput()
            val pw = PrintWriter(encryptedOut)
            pw.println(texto)
            pw.flush()
            encryptedOut.close()
        }else{
            val encryptedOut = EncryptedFile.Builder(
                nota, requireContext(), masterKeyAlias,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build().openFileOutput()
            val pw = PrintWriter(encryptedOut)
            pw.println(texto)
            pw.flush()
            encryptedOut.close()
        }

        return nota
    }

    private fun bitmapToBase64(bitmap: Bitmap): String? {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return  android.util.Base64.encodeToString(byteArray,android.util.Base64.DEFAULT)
    }


    // data autuação
    private fun perguntaData() {
        //calendário
        val c = Calendar.getInstance()
        val ano = c.get(Calendar.YEAR)
        val mes = c.get(Calendar.MONTH)
        val dia = c.get(Calendar.DAY_OF_MONTH)

        val mDialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.frame_data_autuacao, consultaLayout, false)
        val mBuilder = AlertDialog.Builder(requireContext())
            .setView(mDialogView)
        .setTitle("Títilo e Data da Autuação:")
        val mAlertDialog = mBuilder.show()

        mAlertDialog.edtDataAut.setOnFocusChangeListener { _, _ ->
            val dpDialog = DatePickerDialog(requireContext(),
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    //mostrar no edt view
                    val newMonth = month + 1
                    val dataStr = "$dayOfMonth/$newMonth/$year"
                    mAlertDialog.edtDataAut.setText(dataStr)
                    val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(edtDataAvaliacao.windowToken, 0)
                }, ano, mes, dia
            )
            dpDialog.show()


        }

        mAlertDialog.btnDataAutu.setOnClickListener {
            if(mAlertDialog.edtDataAut.text.toString().isBlank() || mAlertDialog.edtTituloAut.text.toString().isBlank()){
                Toast.makeText(requireContext(),"Favor preencher título e data da autuação", Toast.LENGTH_SHORT).show()
            }else{
                dataAutuacao = mAlertDialog.edtDataAut.text.toString()
                edtDataAvaliacao.text = dataAutuacao
                tituloAutuacao = mAlertDialog.edtTituloAut.text.toString().trimEnd()
                txtTituloAutuacao.text = tituloAutuacao
                mAlertDialog.dismiss()
            }
        }
        mAlertDialog.btnDismiss.setOnClickListener {
            findNavController().navigate(R.id.navigation_cadastro)
            mAlertDialog.dismiss()
        }

    }

    //foto abaixo
    private fun captureImage() {

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
        } else {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
                // Create the File where the photo should go
                try {
                    photoFile = createImageFile()
                    //displayMessage(requireActivity().baseContext, photoFile!!.absolutePath)
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        val photoURI = FileProvider.getUriForFile(requireContext(),
                            "anvisa.inflabnet.fiscalizacao.fileprovider",
                            photoFile!!
                        )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST)
                    }
                } catch (ex: Exception) {
                    // Error occurred while creating the File
                    displayMessage(requireActivity().baseContext,"Problema com a captura da imagem: "  + ex.message.toString())
                }
            } else {
                displayMessage(requireContext(), "Nullll")
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        //Criando o nome da imagem/foto
        val newData = dataAutuacao?.replace("/","")
        val imageFileName = estabelecimentoShP + "-" + tituloAutuacao + "-" + newData + "_"

        val storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName, /* prefixo */
            ".fig", /* sufixo */
            storageDir      /* diretório */
        )
        // caminho do arquivo
        mCurrentPhotoPath = image.absolutePath
        return image
    }

    private fun displayMessage(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    @SuppressLint("StaticFieldLeak")
    inner class GetIdAvaliador: AsyncTask<Int, Unit, String>() {
        override fun doInBackground(vararg params: Int?): String {
            return appDatabase.fiscalDAO().getIdAvaliadorAtual(params[0]!!)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAPTURE_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            val myBitmap = BitmapFactory.decodeFile(photoFile!!.absolutePath)
            foodView.setImageBitmap(myBitmap)
        } else {
            displayMessage(requireContext(), "Requisição cancelada. Alguma coisa deu errada.")
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                captureImage()
            }
        }
    }

    private fun showSnackbar(msg: String?) {
        val snack = Snackbar.make(cadAutuacaoRoot,msg.toString(), Snackbar.LENGTH_LONG)
        snack.show()
    }

    @SuppressLint("StaticFieldLeak")
    inner class GuardarAnotacao :AsyncTask<Anotacoes,Unit,Unit>(){
        override fun doInBackground(vararg params: Anotacoes?) {
            appDatabase.anotacoesDAO().insert(params[0]!!)
        }


    }
}


