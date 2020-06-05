package anvisa.inflabnet.fiscalizacao.ui.consulta

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import anvisa.inflabnet.fiscalizacao.R
import anvisa.inflabnet.fiscalizacao.database.model.Avaliacoes
import kotlinx.android.synthetic.main.avaliacao_item.view.*

class AvaliacoesAdapter(val avaliacoes: List<Avaliacoes>, private val itemClick: (Avaliacoes) -> Unit)
    : RecyclerView.Adapter<AvaliacoesAdapter.ViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AvaliacoesAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.avaliacao_item,parent,false)
        return AvaliacoesAdapter.ViewHolder(
            view,
            itemClick
        )
    }

    override fun getItemCount(): Int {
        return avaliacoes.size
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: AvaliacoesAdapter.ViewHolder, position: Int) {
        holder.bindForecast(avaliacoes[position])
    }

    class ViewHolder(view: View, val itemClick: (Avaliacoes) -> Unit):RecyclerView.ViewHolder(view) {
        @RequiresApi(Build.VERSION_CODES.M)
        fun bindForecast(aval: Avaliacoes) {
            with(aval){
                itemView.txtBairro.text = aval.bairro_estab?.getClearText()
                itemView.txtEstabelecimento.text = aval.estabelecimento?.getClearText()
                itemView.setOnClickListener { itemClick(this) }
            }
        }
    }
}