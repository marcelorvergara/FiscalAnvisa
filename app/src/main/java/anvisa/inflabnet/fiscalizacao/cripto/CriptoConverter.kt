package anvisa.inflabnet.fiscalizacao.cripto

import androidx.room.TypeConverter

class CriptoConverter {
    @TypeConverter
    fun fromCriptoString(value: CriptoString?): String? {
        return value?.getCriptoBase64()
    }

    @TypeConverter
    fun toCriptoString(value: String?): CriptoString? {
        val cripto = CriptoString()
        cripto.setCriptoBase64(value)
        return cripto
    }

}