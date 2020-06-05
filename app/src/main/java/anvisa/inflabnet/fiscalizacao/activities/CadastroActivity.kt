package anvisa.inflabnet.fiscalizacao.activities

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import anvisa.inflabnet.fiscalizacao.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_cadastro.*

class CadastroActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro)

        mAuth = FirebaseAuth.getInstance()
        cadastroBtnCad.setOnClickListener { criarUser() }

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
    }

    private fun criarUser() {

        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(cadastroBtnCad.windowToken, 0)

        val userLogin = edtUserCad.text.toString()
        val passLogin = edtPassCad.text.toString()
        val passLoginConfirm = edtPassCadConf.text.toString()

        if(userLogin.isBlank() || passLogin.isBlank() || passLoginConfirm.isBlank()){
            showSnackbar("Favor preencher todos os campos!")
        } else if (!passLogin.equals(passLoginConfirm)){
            showSnackbar("As senhas digitadas não conferem!")
        } else{
            mAuth!!.createUserWithEmailAndPassword(userLogin, passLogin)
                .addOnSuccessListener {
                    if(it != null){
                        val user = it.user!!.email
                        showSnackbar(user)
                        clearFields()
                        val novoIntt = Intent(this, MainActivity::class.java)
                        novoIntt.putExtra("userLogin", userLogin)
                        startActivity(novoIntt)
                    }
                }
                .addOnFailureListener {
                    showSnackbar(it.message.toString())
                }
        }
    }

    private fun clearFields() {
        edtUserCad.setText("")
        edtPassCad.setText("")
    }

    private fun showSnackbar(msg: String?) {
        val snack = msg?.let { Snackbar.make(rooCadastro, it, Snackbar.LENGTH_LONG) }
        if (snack != null) {
            snack.show()
        }
    }
}
