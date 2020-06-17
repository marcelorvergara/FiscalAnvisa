package anvisa.inflabnet.fiscalizacao.ui.nuvem.anotacoes

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import anvisa.inflabnet.fiscalizacao.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_edit_anot.*

/**
 * A simple [Fragment] subclass.
 */
class EditAnotFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_anot, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPrefsEditAnot: SharedPreferences = requireContext().getSharedPreferences("edit_anotacao", Context.MODE_PRIVATE)
        val dataShP = sharedPrefsEditAnot.getString("data","Data")
        val estabelecimentoShP = sharedPrefsEditAnot.getString("estabelecimento","Estabelecimento")
        val idAnotShP = sharedPrefsEditAnot.getString("idAnot","ID Anotação")
        val idFiscalShP = sharedPrefsEditAnot.getString("idFiscal","ID Fiscal")
        val pathPhotoShP = sharedPrefsEditAnot.getString("pathPhoto","Foto")
        val pathTxtShP = sharedPrefsEditAnot.getString("pathTxt","Texto Autuação")
        val tituloAnotacaoShP = sharedPrefsEditAnot.getString("tituloAnotacao","Título Autuação")

        edtTituloEditAnot.setText(tituloAnotacaoShP.toString())

        val image = pathPhotoShP?.let { base64ToBitmap(it) }
        imgAutuacao.setImageBitmap(image)

        txtIdAnotEdit.text = idAnotShP.toString()
        txtIdFiscalEdit.text = idFiscalShP.toString()

        edtAnotEditEstabelecimento.setText(estabelecimentoShP.toString())
        edtAnotEditData.setText(dataShP)
        edtAnotEditTexto.setText(pathTxtShP.toString())

        //apagar Shared Preference
        val clearSpAnot = sharedPrefsEditAnot.edit()
        clearSpAnot.clear()
        clearSpAnot.apply()

        btnAtualizarAnot.setOnClickListener {
            val titulo = edtTituloEditAnot.text.toString()
            val estabelecimento = edtAnotEditEstabelecimento.text.toString()
            val texto = edtAnotEditTexto.text.toString()
            val data = edtAnotEditData.text.toString()

            val idAnot = txtIdAnotEdit.text.toString()
            val id_fiscal = txtIdFiscalEdit.text.toString()
            val docId = idAnot + "_" + id_fiscal

            val firebaseFirestore = FirebaseFirestore.getInstance()
            val collection = firebaseFirestore.collection("Anotacoes")
                .document(docId)
                .update("dataAnota",data,"estabelecimento",estabelecimento,"pathTxt",texto,"tituloAnotcao", titulo)

            showSnackbar("Atualização executada com sucesso!")
            findNavController().navigate(R.id.action_editAnotFragment_to_navigation_nuvem)

        }

        btnExcluirAnot.setOnClickListener {

            val dialogBuilder = AlertDialog.Builder(requireContext())
            dialogBuilder.setMessage("Tem certeza que gostaria de deletar essa autuação?")
                .setCancelable(false)
                .setPositiveButton("Sim"){_, _ ->
                    //segue a deleção do item
                    val id_anot = txtIdAnotEdit.text.toString()
                    val id_fiscal = txtIdFiscalEdit.text.toString()
                    val docId = id_anot + "_" + id_fiscal

                    val firebaseFirestore = FirebaseFirestore.getInstance()
                    val collection = firebaseFirestore.collection("Anotacoes")
                        .document(docId).delete()

                    showSnackbar("Autuação excluída com sucesso!")
                    findNavController().navigate(R.id.action_editAnotFragment_to_navigation_nuvem)
                    showSnackbar("Avaliação excluída com sucesso!")
                }
                .setNegativeButton("Não") { _, _ ->
                    showSnackbar("Autuação não foi excluída!")
                }
                .setNeutralButton("Cancelar") {_, _ ->
                    showSnackbar("Operação cancelada!")
                }
            val alert = dialogBuilder.create()
            alert.setTitle("Deletar Autuação")
            alert.show()
        }

        btnVoltarParaListaAnot.setOnClickListener {
            findNavController().navigate(R.id.action_editAnotFragment_to_listarNuvemFragment2)
        }

    }

    private fun base64ToBitmap(b64: String): Bitmap? {
        val imageAsBytes: ByteArray = android.util.Base64.decode(b64.toByteArray(), android.util.Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.size)
    }

    private fun showSnackbar(msg: String?) {
        val snack = Snackbar.make(editAnotlLayout,msg.toString(), Snackbar.LENGTH_LONG)
        snack.show()
    }
}
