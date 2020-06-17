package anvisa.inflabnet.fiscalizacao.login

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import anvisa.inflabnet.fiscalizacao.R
import anvisa.inflabnet.fiscalizacao.database.service.AppDatabase
import anvisa.inflabnet.fiscalizacao.database.service.AppDatabaseService
import anvisa.inflabnet.fiscalizacao.database.tabelas.Fiscal
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    // Initialize Facebook Login button
    private var callbackManager = CallbackManager.Factory.create()!!

    private var mAuth: FirebaseAuth? = null
    private lateinit var appDatabase : AppDatabase

    override fun onStart() {
        super.onStart()
        val currentUser = mAuth?.currentUser
        if(currentUser != null){
            val user = mAuth!!.currentUser!!.uid
            UpdateIdFiscal().execute(user)

            val novoIntt = Intent(this, HomeActivity::class.java)
            novoIntt.putExtra("userLogin", user)
            startActivity(novoIntt)

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val pInfo = packageManager.getPackageInfo(packageName, 0)
        val version = pInfo.versionName
        txtVersion.text = version

        mAuth = FirebaseAuth.getInstance()

        loginBtnFace.setReadPermissions("email", "public_profile")
        loginBtnFace.registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d("TAG", "facebook:onSuccess:$loginResult")
                handleFacebookAccessToken(loginResult.accessToken)

            }
            override fun onCancel() {
                Log.d("TAG", "facebook:onCancel")
                showSnackbar("Autenticação cancelada")
            }

            override fun onError(error: FacebookException) {
                Log.d("TAG", "facebook:onError", error)
                Toast.makeText(this@MainActivity,error.message,Toast.LENGTH_SHORT).show()
                showSnackbar("Erro de autenticação ${error.message}")
            }
        })

        //iniciar DB SQLite
        appDatabase = AppDatabaseService.getInstance(this)

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

    }

    //Login facebook
    private fun handleFacebookAccessToken(token: AccessToken) {

        Log.d("TAG 1 ", "handleFacebookAccessToken:$token")
        val credential = FacebookAuthProvider.getCredential(token.token)

        mAuth?.signInWithCredential(credential)
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG 4", "signInWithCredential:success")
                    val user = mAuth!!.currentUser!!.uid
                    UpdateIdFiscal().execute(user)

                    val novoIntt = Intent(this, HomeActivity::class.java)
                    novoIntt.putExtra("userLogin", user)
                    startActivity(novoIntt)

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG 5", "signInWithCredential:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed. --> ${task.exception}",
                        Toast.LENGTH_SHORT).show()
                    //updateUI(null)
                }


            }?.addOnFailureListener {
                Log.d("TAG Failure", it.message.toString())
            }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("TAG 6 ",requestCode.toString())
        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    @SuppressLint("StaticFieldLeak")
    inner class InsertFiscal : AsyncTask<Fiscal,Unit,Unit>() {
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

    //Login Firebase
    private fun validaLogin() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(loginBtn.windowToken, 0)

        if(!edtUser.text.toString().isBlank() && !passEdt.text.toString().isBlank()) {
            mAuth!!.signInWithEmailAndPassword(edtUser.text.toString(), passEdt.text.toString())
                .addOnSuccessListener {
                    if (it != null) {
                        val userId = mAuth!!.currentUser!!.uid

                        //colocar o id do fiscal na tabela
                        UpdateIdFiscal().execute(userId)

                        val novoIntt = Intent(this, HomeActivity::class.java)
                        novoIntt.putExtra("userLogin", userId)
                        startActivity(novoIntt)
                    }
                }
                .addOnFailureListener {
                    val inputMethManag = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethManag.hideSoftInputFromWindow(loginBtn.windowToken, 0)
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
