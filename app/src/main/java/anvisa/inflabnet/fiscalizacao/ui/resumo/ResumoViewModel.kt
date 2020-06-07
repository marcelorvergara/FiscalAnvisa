package anvisa.inflabnet.fiscalizacao.ui.resumo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModel
import anvisa.inflabnet.fiscalizacao.R
import anvisa.inflabnet.fiscalizacao.database.model.Avaliacoes
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.frame_resumo.*

class ResumoViewModel : ViewModel() {

    private var mAuth: FirebaseAuth? = null

    fun acListBairros(
        listUniqueBairros: List<String>,
        acListBairros: AutoCompleteTextView,
        requireContext: Context
    ) {
        val adapter = ArrayAdapter(
            requireContext,
            android.R.layout.simple_spinner_dropdown_item,
            listUniqueBairros
        )

        acListBairros.setAdapter(adapter)
        acListBairros.threshold = 1
        acListBairros.text.toString()

        acListBairros.setOnDismissListener {
        }
        acListBairros.onFocusChangeListener = View.OnFocusChangeListener { _, b ->
            if (b) {
                // Sugestões dropdown
                acListBairros.showDropDown()
            }
        }
    }

     fun autocompListaBairros(
         listaDeBairros: MutableList<String>,
         bairrosLista: List<Avaliacoes>,
         acListBairros: AutoCompleteTextView,
         requireContext: Context,
         resumoLayout: ConstraintLayout
     ) {
        acListBairros.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                val selectedBairro = parent.getItemAtPosition(position) as String

                //escondendo o teclado
//                val imm =
//                    activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                imm.hideSoftInputFromWindow(acListBairros.applicationWindowToken, 0)

                //preencher os dados - selectedBairro
                val mDialogView = LayoutInflater.from(requireContext)
                    .inflate(R.layout.frame_resumo, resumoLayout,false)
                val mBuilder = AlertDialog.Builder(requireContext)
                    .setView(mDialogView)
                //.setTitle("Bairro: ${selectedBairro}")

                val mAlertDialog = mBuilder.show()
                mAlertDialog.txtBairroresult.text = selectedBairro
                //contando quantas avaliações de um bairro
                var totBairros = 0
                listaDeBairros.forEach {
                    if (selectedBairro.equals(it)) {
                        totBairros += 1
                    }
                }
                mAlertDialog.txtResultTotAvaliacoes.text = totBairros.toString()
                //contagem das respostas começa aqui usando bairrosLista
                var totR1 = 0.0
                var totR2 = 0.0
                var totR3 = 0.0
                var totR4 = 0.0
                var totR5 = 0.0
                var totR6 = 0.0
                val resp1Result: Double
                val resp2Result: Double
                val resp3Result: Double
                val resp4Result: Double
                val resp5Result: Double
                val resp6Result: Double
                bairrosLista.forEach {
                    if (it.bairro_estab?.getClearText().equals(selectedBairro)) {

                        if (it.rb1.toString().equals("true")) {
                            totR1 += 1
                        }
                        if (it.rb2.toString().equals("true")) {
                            totR2 += 1
                        }
                        if (it.rb3.toString().equals("true")) {
                            totR3 += 1
                        }
                        if (it.rb4.toString().equals("true")) {
                            totR4 += 1
                        }
                        if (it.rb5.toString().equals("true")) {
                            totR5 += 1
                        }
                        if (it.rb6.toString().equals("true")) {
                            totR6 += 1
                        }
                    }
                }

                resp1Result = totR1 / totBairros * 100
                resp2Result = totR2 / totBairros * 100
                resp3Result = totR3 / totBairros * 100
                resp4Result = totR4 / totBairros * 100
                resp5Result = totR5 / totBairros * 100
                resp6Result = totR6 / totBairros * 100

                val txtResposta1Str = "$resp1Result% ok em $selectedBairro"
                val txtResposta2Str = "$resp2Result% ok em $selectedBairro"
                val txtResposta3Str = "$resp3Result% ok em $selectedBairro"
                val txtResposta4Str = "$resp4Result% ok em $selectedBairro"
                val txtResposta5Str = "$resp5Result% ok em $selectedBairro"
                val txtResposta6Str = "$resp6Result% ok em $selectedBairro"

                mAlertDialog.txtResposta1.text = txtResposta1Str
                mAlertDialog.txtResposta2.text = txtResposta2Str
                mAlertDialog.txtResposta3.text = txtResposta3Str
                mAlertDialog.txtResposta4.text = txtResposta4Str
                mAlertDialog.txtResposta5.text = txtResposta5Str
                mAlertDialog.txtResposta6.text = txtResposta6Str

                mAlertDialog.btnVoltarPerguntasFrame.setOnClickListener {
                    mAlertDialog.dismiss()
                }
            }
    }


}