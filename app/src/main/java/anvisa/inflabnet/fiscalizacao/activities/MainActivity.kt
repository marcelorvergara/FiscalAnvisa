package anvisa.inflabnet.fiscalizacao.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import anvisa.inflabnet.fiscalizacao.R
import anvisa.inflabnet.fiscalizacao.database.model.Fiscal
import anvisa.inflabnet.fiscalizacao.database.service.AppDatabase
import anvisa.inflabnet.fiscalizacao.database.service.AppDatabaseService
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.Executor

class MainActivity : AppCompatActivity() {

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    private var mAuth: FirebaseAuth? = null
    private lateinit var appDatabase : AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        appDatabase = AppDatabaseService.getInstance(this)

        //iniciar DB
        InsertFiscal().execute(Fiscal(101,"101"))

        cadastroBtn.setOnClickListener {
            startActivity(
                Intent(this,
                CadastroActivity::class.java)
            )
        }

        loginBtn.setOnClickListener {
            validaLogin()
        }

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
    }

    @SuppressLint("StaticFieldLeak")
    inner class InsertFiscal() : AsyncTask<Fiscal,Unit,Unit>() {
        override fun doInBackground(vararg params: Fiscal?) {
            appDatabase.fiscalDAO().insert(params[0]!!)
        }

    }

    @SuppressLint("StaticFieldLeak")
    inner class UpdateIdFiscal : AsyncTask<String,Unit,Unit>() {
        override fun doInBackground(vararg params: String?) {
            appDatabase.fiscalDAO().atualiza(params[0]!!)
        }
    }

    private fun validaLogin() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(loginBtn.windowToken, 0)

        mAuth = FirebaseAuth.getInstance()

        if(!edtUser.text.toString().isBlank() && !passEdt.text.toString().isBlank()) {
            mAuth!!.signInWithEmailAndPassword(edtUser.text.toString(), passEdt.text.toString())
                .addOnSuccessListener {
                    if (it != null) {
                        val userId = mAuth!!.currentUser!!.uid

                        //colocar o id do fiscal na tabela
                        UpdateIdFiscal().execute(userId)

                        val novoIntt = Intent(this, HomeActivity::class.java)
                        novoIntt.putExtra("userLogin", userId)
                        //Toast.makeText(this,"ID Logando ${userId}",Toast.LENGTH_SHORT).show()
                        startActivity(novoIntt)
                    }
                }
                .addOnFailureListener {
                    val inputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(loginBtn.windowToken, 0)
                    showSnackbar(it.message)
                }
        }else{
            showSnackbar("Favor preencher usuário e password")
        }

    }

    private fun showSnackbar(msg: String?) {
        msg?.let { Snackbar.make(rootLogin, it, Snackbar.LENGTH_LONG) }?.show()
    }
}
