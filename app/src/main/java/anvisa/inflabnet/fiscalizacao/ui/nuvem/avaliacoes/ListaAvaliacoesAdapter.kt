package anvisa.inflabnet.fiscalizacao.ui.nuvem.avaliacoes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import anvisa.inflabnet.fiscalizacao.R
import anvisa.inflabnet.fiscalizacao.database.tabelas.Avaliacoes
import kotlinx.android.synthetic.main.avaliacao_item.view.*

class ListaAvaliacoesAdapter(private val avaliacoes : List<Avaliacoes>,private val itemClick: (Avaliacoes) -> Unit) : RecyclerView.Adapter
<ListaAvaliacoesAdapter.AvaliacaoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvaliacaoViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.avaliacao_item, parent,false)
        return AvaliacaoViewHolder(
            view,
            itemClick
        )
    }

    override fun getItemCount(): Int = avaliacoes.size

    override fun onBindViewHolder(holder: AvaliacaoViewHolder, position: Int) {
        holder.bindForecast(avaliacoes[position])
    }

    class AvaliacaoViewHolder(itemView: View, val itemClick: (Avaliacoes) -> Unit )
        : RecyclerView.ViewHolder(itemView){

        fun bindForecast(avaliacao:Avaliacoes){
            with(avaliacao){
                itemView.txtEstabelecimento.text = avaliacao.estabelecimento!!.getClearText()
                itemView.txtBairro.text = avaliacao.bairro_estab!!.getClearText()
                itemView.setOnClickListener { itemClick(this) }

            }
        }
    }
}
