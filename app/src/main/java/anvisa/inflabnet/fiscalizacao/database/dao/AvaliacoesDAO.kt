package anvisa.inflabnet.fiscalizacao.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import anvisa.inflabnet.fiscalizacao.database.tabelas.Avaliacoes

@Dao
interface AvaliacoesDAO {

    @Insert
    fun guardaAvaliacao(avaliacoes: Avaliacoes)

    @Query("Select * from Avaliacoes WHERE id_fiscal_aval = :s")
    fun getAll(s: String): List<Avaliacoes>

    @Query("Select * from Avaliacoes")
    fun getAllBairros() : List<Avaliacoes>

    @Query("Update Avaliacoes set anotacao = :condicao where avalId = :idAval")
    fun updateAvalAnot(idAval: Int, condicao: Boolean)

    @Delete
    fun deleteAval(avaliacoes: Avaliacoes)

}