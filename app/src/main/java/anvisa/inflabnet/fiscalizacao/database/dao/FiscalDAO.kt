package anvisa.inflabnet.fiscalizacao.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import anvisa.inflabnet.fiscalizacao.database.tabelas.Fiscal

@Dao
interface FiscalDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(fiscal: Fiscal)

    @Query("UPDATE Fiscal SET id_fiscal = :id WHERE id_logado = 101")
    fun atualiza(id: String)

    @Query("Select * from Fiscal")
    fun show(): Array<Fiscal>

    @Query ("Select id_fiscal from Fiscal WHERE id_logado = :i")
    fun getIdAvaliadorAtual(i: Int) : String

}