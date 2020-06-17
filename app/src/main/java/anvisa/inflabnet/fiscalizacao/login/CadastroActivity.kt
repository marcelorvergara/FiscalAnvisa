package anvisa.inflabnet.fiscalizacao.login

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
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

    }

    private fun criarUser() {

        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(cadastroBtnCad.windowToken, 0)

        val userLogin = edtUserCad.text.toString()
        val passLogin = edtPassCad.text.toString()
        val passLoginConfirm = edtPassCadConf.text.toString()

        if(userLogin.isBlank() || passLogin.isBlank() || passLoginConfirm.isBlank()){
            showSnackbar("Favor preencher todos os campos!")
        } else if (passLogin != passLoginConfirm){
            showSnackbar("As senhas digitadas não conferem!")
        } else{
            mAuth!!.createUserWithEmailAndPassword(userLogin, passLogin)
                .addOnSuccessListener {
                    if(it != null){
                        val user = it.user!!.email
                        Toast.makeText(this,"Usuário $user cadastrado com sucesso!",Toast.LENGTH_SHORT).show()
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
        msg?.let { Snackbar.make(rooCadastro, it, Snackbar.LENGTH_LONG) }?.show()
    }
}
