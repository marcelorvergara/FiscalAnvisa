package anvisa.inflabnet.fiscalizacao.ui.nuvem.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import anvisa.inflabnet.fiscalizacao.database.tabelas.Anotacoes
import anvisa.inflabnet.fiscalizacao.database.tabelas.Avaliacoes
import anvisa.inflabnet.fiscalizacao.ui.nuvem.anotacoes.ListaAnotacoesAdapter
import anvisa.inflabnet.fiscalizacao.ui.nuvem.avaliacoes.ListaAvaliacoesAdapter
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_listar_nuvem.view.*

class NuvemViewModel : ViewModel() {

    fun avaliacoes(
        rootListaNuvemView: ConstraintLayout,
        requireContext: Context,
        txtSelected: TextView
    ) {
        val firebaseFirestore = FirebaseFirestore.getInstance()
        val collection = firebaseFirestore.collection("Avaliacoes")
        collection.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            if (firebaseFirestoreException != null) {
                Log.e("Firestore", firebaseFirestoreException.message)
            } else {
                if (querySnapshot != null) {
                    val avaliacoes = querySnapshot.toObjects(Avaliacoes::class.java)
                    rootListaNuvemView.rvListaItens.layoutManager = LinearLayoutManager(requireContext)
                    rootListaNuvemView.rvListaItens.adapter = ListaAvaliacoesAdapter(avaliacoes) {
                            //shared preferences para levar infos para próximo fragment
                            val sharedPrefsAval: SharedPreferences =
                                requireContext.getSharedPreferences(
                                    "edit_avaliacao",
                                    Context.MODE_PRIVATE
                                )
                            val editorAval = sharedPrefsAval.edit()
                            editorAval.putString("municipio", it.municipio)
                            editorAval.putString("bairro", it.bairro_estab?.getClearText())
                            editorAval.putString(
                                "estabelecimento",
                                it.estabelecimento?.getClearText()
                            )
                            editorAval.putString("anotacao", it.anotacao.toString())
                            editorAval.putString("p1", it.rb1.toString())
                            editorAval.putString("p2", it.rb2.toString())
                            editorAval.putString("p3", it.rb3.toString())
                            editorAval.putString("p4", it.rb4.toString())
                            editorAval.putString("p5", it.rb5.toString())
                            editorAval.putString("p6", it.rb6.toString())

                            //para referenciar o documento do firebase
                            editorAval.putString("avalId", it.avalId.toString())
                            editorAval.putString("id_fiscal_aval", it.id_fiscal_aval.toString())

                            editorAval.apply()

                            txtSelected.text = it.estabelecimento?.getClearText()

                        }
                }
            }
        }
   }

    fun editAutuacao(
        rootListaNuvemView: ConstraintLayout,
        requireContext: Context,
        txtSelected: TextView
    ) {
        val firebaseFirestore = FirebaseFirestore.getInstance()
        val collection = firebaseFirestore.collection("Anotacoes")
        collection.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            if (firebaseFirestoreException != null) {
                Log.e("Firestore", firebaseFirestoreException.message.toString())
            } else {
                if (querySnapshot != null) {
                    val anotacoes = querySnapshot.toObjects(Anotacoes::class.java)
                    rootListaNuvemView.rvListaItens.layoutManager = LinearLayoutManager(requireContext)
                    rootListaNuvemView.rvListaItens.adapter =
                        ListaAnotacoesAdapter(anotacoes) {
                            txtSelected.text = it.estabelecimento
                            val sharedPrefsAnot: SharedPreferences =
                                requireContext.getSharedPreferences("edit_anotacao",Context.MODE_PRIVATE)
                            val editorAnot = sharedPrefsAnot.edit()
                            editorAnot.putString("data", it.dataAnota)
                            editorAnot.putString("estabelecimento", it.estabelecimento)
                            editorAnot.putString("idAnot", it.idAnotacao.toString())
                            editorAnot.putString("idFiscal", it.idFiscal)
                            editorAnot.putString("pathPhoto", it.pathPhoto?.getClearText())
                            editorAnot.putString("pathTxt", it.pathTxt)
                            editorAnot.putString("tituloAnotacao", it.tituloAnotcao)
                            editorAnot.apply()

                        }
                }
            }
        }
    }
}