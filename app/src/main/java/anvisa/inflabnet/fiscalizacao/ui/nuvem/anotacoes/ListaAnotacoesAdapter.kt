package anvisa.inflabnet.fiscalizacao.ui.nuvem.anotacoes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import anvisa.inflabnet.fiscalizacao.R
import anvisa.inflabnet.fiscalizacao.database.tabelas.Anotacoes
import kotlinx.android.synthetic.main.anotacoes_item.view.*

class ListaAnotacoesAdapter(private val avaliacoes : List<Anotacoes>,private val itemClick: (Anotacoes) -> Unit) : RecyclerView.Adapter
<ListaAnotacoesAdapter.AnotacoesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnotacoesViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.anotacoes_item, parent,false)
        return AnotacoesViewHolder(
            view,
            itemClick
        )
    }

    override fun getItemCount(): Int = avaliacoes.size

    override fun onBindViewHolder(holder: AnotacoesViewHolder, position: Int) {
        holder.bindForecast(avaliacoes[position])
    }

    class AnotacoesViewHolder(itemView: View, val itemClick: (Anotacoes) -> Unit )
        : RecyclerView.ViewHolder(itemView){

        fun bindForecast(anot:Anotacoes){
            with(anot){
                itemView.txtEstabelecimento.text = anot.estabelecimento
                itemView.txtDataAnota.text = anot.dataAnota
                itemView.txtTituloAnota.text = anot.tituloAnotcao
                itemView.setOnClickListener { itemClick(this) }

            }
        }
    }
}