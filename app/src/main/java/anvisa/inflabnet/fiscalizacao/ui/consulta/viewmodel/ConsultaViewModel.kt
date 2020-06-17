package anvisa.inflabnet.fiscalizacao.ui.consulta.viewmodel

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import anvisa.inflabnet.fiscalizacao.R
import anvisa.inflabnet.fiscalizacao.database.tabelas.Avaliacoes
import anvisa.inflabnet.fiscalizacao.ui.consulta.adapter.AvaliacoesAdapter
import kotlinx.android.synthetic.main.frame_consulta.*

class ConsultaViewModel : ViewModel() {

    fun montaAlertDialog(
        listaTodos: List<Avaliacoes>,
        rvAvaliacoes: RecyclerView,
        requireActivity: FragmentActivity,
        requireContext: Context,
        applicationContext: Context?,
        consultaLayout: ConstraintLayout
    ) {
        val linearLayoutManager = LinearLayoutManager(requireActivity.applicationContext)
        rvAvaliacoes.layoutManager = linearLayoutManager
        rvAvaliacoes.scrollToPosition(listaTodos.size)
        rvAvaliacoes.adapter =
            AvaliacoesAdapter(
                listaTodos
            ) {
                val mDialogView = LayoutInflater.from(applicationContext)
                    .inflate(R.layout.frame_consulta, consultaLayout, false)
                val mBuilder = AlertDialog.Builder(requireContext)
                    .setView(mDialogView)
                //.setTitle("Restaurante: ${it.avalId}")
                val mAlertDialog = mBuilder.show()
                mAlertDialog.txtEstab.text = it.estabelecimento?.getClearText()
                mAlertDialog.txtBairro.text = it.bairro_estab?.getClearText()
                mAlertDialog.txtResposta1.text = if (it.rb1!!) "Sim" else "Não"
                mAlertDialog.txtResposta2.text = if (it.rb2!!) "Sim" else "Não"
                mAlertDialog.txtResposta3.text = if (it.rb3!!) "Sim" else "Não"
                mAlertDialog.txtResposta4.text = if (it.rb4!!) "Sim" else "Não"
                mAlertDialog.txtResposta5.text = if (it.rb5!!) "Sim" else "Não"
                mAlertDialog.txtResposta6.text = if (it.rb6!!) "Sim" else "Não"
                mAlertDialog.btnVoltarPerguntasFrame.setOnClickListener {
                    mAlertDialog.dismiss()
                }
            }
    }
}