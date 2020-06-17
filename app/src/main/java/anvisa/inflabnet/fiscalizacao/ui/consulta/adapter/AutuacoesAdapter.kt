package anvisa.inflabnet.fiscalizacao.ui.consulta.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import anvisa.inflabnet.fiscalizacao.R
import anvisa.inflabnet.fiscalizacao.database.tabelas.Anotacoes
import kotlinx.android.synthetic.main.anotacoes_item.view.*

class AutuacoesAdapter(private val anotacoes: List<Anotacoes>, private val itemClick: (Anotacoes) -> Unit)
    : RecyclerView.Adapter<AutuacoesAdapter.ViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.anotacoes_item,parent,false)
        return ViewHolder(
            view,
            itemClick
        )
    }

    override fun getItemCount(): Int {
        return anotacoes.size
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindForecast(anotacoes[position])
    }

    class ViewHolder(view: View, val itemClick: (Anotacoes) -> Unit):RecyclerView.ViewHolder(view) {
        @RequiresApi(Build.VERSION_CODES.M)
        fun bindForecast(anot: Anotacoes) {
            with(anot){
               itemView.txtTituloAnota.text = anot.tituloAnotcao
               itemView.txtDataAnota.text = anot.dataAnota
               itemView.txtEstabelecimento.text = anot.estabelecimento
               itemView.setOnClickListener { itemClick(this) }
            }
        }
    }
}