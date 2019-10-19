package br.pedroso.productcatalog.device.database.room.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import br.pedroso.productcatalog.device.database.room.entities.CATALOG_TABLE_NAME
import br.pedroso.productcatalog.device.database.room.entities.RoomProduct
import io.reactivex.Single

@Dao
interface CatalogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun registerProduct(roomProduct: RoomProduct)

    @Query("SELECT * FROM $CATALOG_TABLE_NAME")
    fun getAllProducts(): Single<List<RoomProduct>>
}