package anvisa.inflabnet.fiscalizacao.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import anvisa.inflabnet.fiscalizacao.cripto.CriptoString

@Entity
class Avaliacoes (
    @ColumnInfo(name = "id_fiscal_aval") var id_fiscal_aval: String,
    @ColumnInfo(name = "estabelecimento") var estabelecimento: CriptoString?,
    @ColumnInfo(name = "municipio") var municipio: String,
    @ColumnInfo(name = "bairro_estab") var bairro_estab: CriptoString?,
    @ColumnInfo(name = "rb1") var rb1: Boolean,
    @ColumnInfo(name = "rb2") var rb2: Boolean,
    @ColumnInfo(name = "rb3") var rb3: Boolean,
    @ColumnInfo(name = "rb4") var rb4: Boolean,
    @ColumnInfo(name = "rb5") var rb5: Boolean,
    @ColumnInfo(name = "rb6") var rb6: Boolean,
    @PrimaryKey(autoGenerate = true) val avalId: Int = 0)