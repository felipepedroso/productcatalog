package br.pedroso.productcatalog.device.database.room.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import br.pedroso.productcatalog.device.database.room.dao.CatalogDao
import br.pedroso.productcatalog.device.database.room.entities.RoomProduct

@Database(
    version = 1,
    entities = [RoomProduct::class],
    exportSchema = false
)
abstract class CatalogDatabase : RoomDatabase() {
    abstract fun dao(): CatalogDao
}

const val DATABASE_NAME = "catalog-db"