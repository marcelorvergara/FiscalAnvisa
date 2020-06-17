package anvisa.inflabnet.fiscalizacao.database.dao

import androidx.room.*
import anvisa.inflabnet.fiscalizacao.database.tabelas.Anotacoes

@Dao
interface AnotacoesDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(anotacao: Anotacoes)

    @Query("Select * from Anotacoes WHERE idFiscal = :s")
    fun getAll(s: String): List<Anotacoes>

    @Delete
    fun deleteAutuacao(anotacoes: Anotacoes)

}