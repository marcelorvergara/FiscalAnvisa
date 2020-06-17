package anvisa.inflabnet.fiscalizacao.database.service

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import anvisa.inflabnet.fiscalizacao.cripto.CriptoConverter
import anvisa.inflabnet.fiscalizacao.cripto.DateConverter
import anvisa.inflabnet.fiscalizacao.database.dao.AnotacoesDAO
import anvisa.inflabnet.fiscalizacao.database.tabelas.Avaliacoes
import anvisa.inflabnet.fiscalizacao.database.dao.AvaliacoesDAO
import anvisa.inflabnet.fiscalizacao.database.tabelas.Fiscal
import anvisa.inflabnet.fiscalizacao.database.dao.FiscalDAO
import anvisa.inflabnet.fiscalizacao.database.tabelas.Anotacoes


//anotação com relação de entidades(tabelas) que compõe a base
@Database(
    entities = [Fiscal::class, Avaliacoes::class, Anotacoes::class],
    //para notificar mudanças da base de dados do dispositivo
    version = 1
)@TypeConverters(CriptoConverter::class, DateConverter::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun fiscalDAO(): FiscalDAO
    abstract fun avaliacoesDAO(): AvaliacoesDAO
    abstract fun anotacoesDAO():AnotacoesDAO
}