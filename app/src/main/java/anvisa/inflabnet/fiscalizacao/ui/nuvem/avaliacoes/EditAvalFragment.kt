package anvisa.inflabnet.fiscalizacao.ui.nuvem.avaliacoes

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import anvisa.inflabnet.fiscalizacao.R
import anvisa.inflabnet.fiscalizacao.cripto.CriptoString
import anvisa.inflabnet.fiscalizacao.database.tabelas.Avaliacoes
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_edit_aval.*

/**
 * A simple [Fragment] subclass.
 */
class EditAvalFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_aval, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnVoltarParaLista.setOnClickListener {
            findNavController().navigate(R.id.action_editAvalFragment_to_navigation_nuvem)
        }

        //vamos de shared preferences
        val sharedPrefsEditAval: SharedPreferences = requireContext().getSharedPreferences("edit_avaliacao", Context.MODE_PRIVATE)
        val municipioShP = sharedPrefsEditAval.getString("municipio","Município")
        val bairroShPSpaces = sharedPrefsEditAval.getString("bairro","Bairro")
        val estabelecimentoShP = sharedPrefsEditAval.getString("estabelecimento","Estabelecimento")
        val anotacaoShP = sharedPrefsEditAval.getString("anotacao","Anotação")
        val rb1ShP = sharedPrefsEditAval.getString("p1","Resposta_1")
        val rb2ShP = sharedPrefsEditAval.getString("p2","Resposta_2")
        val rb3ShP = sharedPrefsEditAval.getString("p3","Resposta_3")
        val rb4ShP = sharedPrefsEditAval.getString("p4","Resposta_4")
        val rb5ShP = sharedPrefsEditAval.getString("p5","Resposta_5")
        val rb6ShP = sharedPrefsEditAval.getString("p6","Resposta_6")

        val idAval = sharedPrefsEditAval.getString("avalId","avalId")
        val idFiscal = sharedPrefsEditAval.getString("id_fiscal_aval","id_fiscal_aval")

        edtEstabEdit.setText(estabelecimentoShP)
        edtMunipEdit.setText(municipioShP)
        edtBairroEdit.setText(bairroShPSpaces)
        if (anotacaoShP.toString() == "true"){
            txtAutuacao.text = getString(R.string.possui_autuacao)
            txtAutuacao.setTextColor(Color.parseColor("#C70039"))
            txtAutuacaoTag.text = getString(R.string.true_str)
        }else{
            txtAutuacao.text = getString(R.string.nao_possui_autuacao)
            txtAutuacaoTag.text = getString(R.string.false_str)
        }

        if(rb1ShP.toString() == "true"){
            rgPergunta1.check(R.id.radioButton20)
        } else{
            rgPergunta1.check(R.id.radioButton21)
        }

        if(rb2ShP.toString() == "true"){
            rgPergunta2.check(R.id.radioButton22)
        } else{
            rgPergunta2.check(R.id.radioButton23)
        }

        if(rb3ShP.toString() == "true"){
            rgPergunta3.check(R.id.radioButton24)
        } else{
            rgPergunta3.check(R.id.radioButton25)
        }

        if(rb4ShP.toString() == "true"){
            rgPergunta4.check(R.id.radioButton26)
        } else{
            rgPergunta4.check(R.id.radioButton27)
        }

        if(rb5ShP.toString() == "true"){
            rgPergunta5.check(R.id.radioButton28)
        } else{
            rgPergunta5.check(R.id.radioButton29)
        }

        if(rb6ShP.toString() == "true"){
            rgPergunta6.check(R.id.radioButton30)
        } else{
            rgPergunta6.check(R.id.radioButton31)
        }

        txtIdAval.text = idAval.toString()
        txtIdFiscal.text = idFiscal.toString()

        //apagar Shared Preference
        val clearSp = sharedPrefsEditAval.edit()
        clearSp.clear()
        clearSp.apply()

        //atualizar firebase com novos valores
        btnAtualizar.setOnClickListener {
            val estabelecimento = edtEstabEdit.text.toString()
            val estabCripto = CriptoString()
            estabCripto.setClearText(estabelecimento)
            val municipio = edtMunipEdit.text.toString()
            val bairro = edtBairroEdit.text.toString()
            val bairroCripto = CriptoString()
            bairroCripto.setClearText(bairro)

            val autuacaoValue: Boolean
            val autuacao = txtAutuacaoTag.text.toString()
            autuacaoValue = autuacao=="true"

            var resiltado1 = false
            var resiltado2 = false
            var resiltado3 = false
            var resiltado4 = false
            var resiltado5 = false
            var resiltado6 = false

            //pergunta 1
            val checked1 = rgPergunta1.checkedRadioButtonId
            if (checked1 != -1) {
                when(resources.getResourceEntryName(checked1)){
                    "radioButton20" -> {
                        resiltado1 = true
                    }
                    "radioButton21" -> {
                        resiltado1 = false
                    }
                }
            }

            //pergunta 2
            val checked2 = rgPergunta2.checkedRadioButtonId
            if (checked2 != -1) {
                when(resources.getResourceEntryName(checked2)){
                    "radioButton22" -> {
                        resiltado2 = true
                    }
                    "radioButton23" -> {
                        resiltado2 = false
                    }
                }
            }

            //pergunta 3
            val checked3 = rgPergunta3.checkedRadioButtonId
            if (checked3 != -1) {
                when(resources.getResourceEntryName(checked3)){
                    "radioButton24" -> {
                        resiltado3 = true
                    }
                    "radioButton25" -> {
                        resiltado3 = false
                    }
                }
            }

            //pergunta 4
            val checked4 = rgPergunta4.checkedRadioButtonId
            if (checked4 != -1) {
                when(resources.getResourceEntryName(checked4)){
                    "radioButton26" -> {
                        resiltado4 = true
                    }
                    "radioButton27" -> {
                        resiltado4 = false
                    }
                }
            }

            //pergunta 5
            val checked5 = rgPergunta5.checkedRadioButtonId
            if (checked5 != -1) {
                when(resources.getResourceEntryName(checked5)){
                    "radioButton28" -> {
                        resiltado5 = true
                    }
                    "radioButton29" -> {
                        resiltado5 = false
                    }
                }
            }

            //pergunta 6
            val checked6 = rgPergunta6.checkedRadioButtonId
            if (checked6 != -1) {
                when(resources.getResourceEntryName(checked6)){
                    "radioButton30" -> {
                        resiltado6 = true
                    }
                    "radioButton31" -> {
                        resiltado6 = false
                    }
                }
            }

            //montar o objeto para update no firebase
            val avaliacaoEditedObj = Avaliacoes(txtIdFiscal.text.toString(),autuacaoValue,estabCripto,municipio,bairroCripto,
            resiltado1,resiltado2,resiltado3,resiltado4,resiltado5,resiltado6,
                (txtIdAval.text as String).toInt())
            val docId = txtIdAval.text.toString() + "_" + txtIdFiscal.text.toString()
            val firebaseFirestore = FirebaseFirestore.getInstance()
            val collection = firebaseFirestore.collection("Avaliacoes")
            collection.document(docId).set(avaliacaoEditedObj)
            showSnackbar("Atualização executada com sucesso!")

            findNavController().navigate(R.id.action_editAvalFragment_to_navigation_nuvem)

        }

        btnExcluir.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(requireContext())
            dialogBuilder.setMessage("Tem certeza que gostaria de deletar essa avaliação?")
                .setCancelable(false)
                .setPositiveButton("Sim"){_, _ ->
                    //segue a deleção do item
                    val docId = txtIdAval.text.toString() + "_" + txtIdFiscal.text.toString()
                    val firebaseFirestore = FirebaseFirestore.getInstance()
                    val collection = firebaseFirestore.collection("Avaliacoes")
                    collection.document(docId).delete()
                    showSnackbar("Avaliação excluída com sucesso!")
                    findNavController().navigate(R.id.action_editAvalFragment_to_navigation_nuvem)
                }
                .setNegativeButton("Não") { _, _ ->
                    showSnackbar("Avaliação não foi excluída!")
                }
                .setNeutralButton("Cancelar") {_, _ ->
                    showSnackbar("Operação cancelada!")
                }
            val alert = dialogBuilder.create()
            alert.setTitle("Deletar Avaliação")
            alert.show()
        }

        btnVoltarParaLista.setOnClickListener {
            findNavController().navigate(R.id.action_editAvalFragment_to_listarNuvemFragment)
        }
    }


    private fun showSnackbar(msg: String?) {
        val snack = Snackbar.make(editAvalLayout,msg.toString(), Snackbar.LENGTH_LONG)
        snack.show()
    }
}
