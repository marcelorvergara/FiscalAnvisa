package anvisa.inflabnet.fiscalizacao.database.service

import android.content.Context
import androidx.room.Room

class AppDatabaseService {
    companion object{
        private var instance : AppDatabase? = null
        private const val database_name = "appDatabase.sql"
        fun getInstance(context: Context): AppDatabase {
            if(instance ==null){
                instance = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    database_name
                ).build()
                //).fallbackToDestructiveMigration().build()
            }
            return instance as AppDatabase
        }
    }
}