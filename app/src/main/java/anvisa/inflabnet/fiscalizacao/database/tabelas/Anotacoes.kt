package anvisa.inflabnet.fiscalizacao.database.tabelas

import androidx.room.Entity
import androidx.room.PrimaryKey
import anvisa.inflabnet.fiscalizacao.cripto.CriptoString

@Entity
class Anotacoes (
    var idFiscal:String? = null,
    var tituloAnotcao: String? = null,
    var dataAnota: String? = null,
    var pathTxt: String? = null,
    var pathPhoto: CriptoString? = null,
    var estabelecimento: String? = null,
    @PrimaryKey(autoGenerate = true)
    var idAnotacao: Int? = null)