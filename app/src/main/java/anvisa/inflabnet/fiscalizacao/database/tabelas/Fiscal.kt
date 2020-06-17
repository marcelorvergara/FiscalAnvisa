package anvisa.inflabnet.fiscalizacao.database.tabelas

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Fiscal (
    //guarda o ID do fiscal logado na aplicação
    @PrimaryKey
    var id_logado: Int = 101,
    var id_fiscal: String)