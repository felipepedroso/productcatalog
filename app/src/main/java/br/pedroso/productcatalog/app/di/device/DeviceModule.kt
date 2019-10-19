package br.pedroso.productcatalog.app.di.device

import android.arch.persistence.room.Room
import android.content.Context
import br.pedroso.productcatalog.device.database.RoomCatalogDatabaseDataSource
import br.pedroso.productcatalog.device.database.room.dao.CatalogDao
import br.pedroso.productcatalog.device.database.room.database.CatalogDatabase
import br.pedroso.productcatalog.device.database.room.database.DATABASE_NAME
import br.pedroso.productcatalog.domain.device.CatalogDatabaseDatasource
import br.pedroso.productcatalog.app.di.DependenciesTags
import com.github.salomonbrys.kodein.*
import java.io.File

class DeviceModule(private val context: Context) {
    val graph = Kodein.Module {
        bind<CatalogDatabaseDatasource>() with singleton {
            RoomCatalogDatabaseDataSource(
                catalogDao = instance()
            )
        }

        bind<CatalogDao>() with provider {
            val catalogDatabase: CatalogDatabase = instance()
            catalogDatabase.dao()
        }


        bind<CatalogDatabase>() with singleton {
            Room.databaseBuilder(context, CatalogDatabase::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build()
        }

        bind<File>(DependenciesTags.NETWORK_CACHE_DIR) with singleton {
            context.cacheDir
        }

    }
}