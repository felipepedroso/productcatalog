package br.pedroso.productcatalog.device.database

import br.pedroso.productcatalog.device.database.room.dao.CatalogDao
import br.pedroso.productcatalog.device.database.room.mappers.ProductMapper
import br.pedroso.productcatalog.domain.device.CatalogDatabaseDatasource
import br.pedroso.productcatalog.domain.entities.Product
import br.pedroso.productcatalog.domain.errors.DatabaseError
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.functions.Function

class RoomCatalogDatabaseDataSource(private val catalogDao: CatalogDao) : CatalogDatabaseDatasource {

    override fun registerProduct(vararg products: Product): Completable {
        return Completable.fromAction {
            products.forEach { product ->
                val roomProduct = ProductMapper.mapDomainToRoom(product)
                catalogDao.registerProduct(roomProduct)
            }

        }.onErrorResumeNext { Completable.error(DatabaseError(it)) }
    }

    override fun getAllProducts(): Observable<List<Product>> {
        return catalogDao
            .getAllProducts()
            .flatMapObservable { roomProducts ->
                if (roomProducts.isEmpty()) {
                    Observable.empty()
                } else {
                    Observable.just(roomProducts)
                }
            }
            .map { roomProducts ->
                roomProducts.map { ProductMapper.mapRoomToDomain(it) }
            }
            .onErrorResumeNext(DatabaseErrorMapper())
    }

    class DatabaseErrorMapper<T> : Function<Throwable, Observable<T>> {

        override fun apply(cause: Throwable): Observable<T> {
            return Observable.error(DatabaseError(cause))
        }

    }

}

