package anvisa.inflabnet.fiscalizacao.database.tabelas

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import anvisa.inflabnet.fiscalizacao.cripto.CriptoString

@Entity
class Avaliacoes (
    @ColumnInfo(name = "id_fiscal_aval") var id_fiscal_aval: String? = null,
    @ColumnInfo(name = "anotacao") var anotacao: Boolean = false,
    @ColumnInfo(name = "estabelecimento") var estabelecimento: CriptoString? = null,
    @ColumnInfo(name = "municipio") var municipio: String? = null,
    @ColumnInfo(name = "bairro_estab") var bairro_estab: CriptoString? = null,
    @ColumnInfo(name = "rb1") var rb1: Boolean? = null,
    @ColumnInfo(name = "rb2") var rb2: Boolean? = null,
    @ColumnInfo(name = "rb3") var rb3: Boolean? = null,
    @ColumnInfo(name = "rb4") var rb4: Boolean? = null,
    @ColumnInfo(name = "rb5") var rb5: Boolean? = null,
    @ColumnInfo(name = "rb6") var rb6: Boolean? = null,
    @PrimaryKey(autoGenerate = true) val avalId: Int = 0)