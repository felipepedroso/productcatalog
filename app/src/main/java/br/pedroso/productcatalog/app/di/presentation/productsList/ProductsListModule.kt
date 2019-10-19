package br.pedroso.productcatalog.app.di.presentation.productsList

import br.pedroso.productcatalog.presentation.productsList.ProductsListViewModel
import br.pedroso.productcatalog.presentation.productsList.usecases.GetAllProductsFromDatabase
import br.pedroso.productcatalog.presentation.productsList.usecases.ListActiveProductsSortingByName
import br.pedroso.productcatalog.presentation.productsList.usecases.RegisterProductsOnDatabase
import br.pedroso.productcatalog.presentation.productsList.usecases.RetrieveProductsFromService
import br.pedroso.productcatalog.app.di.DependenciesTags.UI_SCHEDULER
import br.pedroso.productcatalog.app.di.DependenciesTags.WORKER_SCHEDULER
import com.github.salomonbrys.kodein.*

class ProductsListModule {
    val graph = Kodein.Module {
        bind<ProductsListViewModel>() with provider {
            ProductsListViewModel(
                uiScheduler = instance(UI_SCHEDULER),
                listActiveProductsSortingByName = instance()
            )
        }

        bind<ListActiveProductsSortingByName>() with singleton {
            ListActiveProductsSortingByName(
                getAllProductsFromDatabase = instance(),
                retrieveProductsFromService = instance(),
                registerProductsOnDatabase = instance()
            )
        }

        bind<GetAllProductsFromDatabase>() with singleton {
            GetAllProductsFromDatabase(
                workerScheduler = instance(WORKER_SCHEDULER),
                catalogDatabaseDatasource = instance()
            )
        }

        bind<RetrieveProductsFromService>() with singleton {
            RetrieveProductsFromService(
                workerScheduler = instance(WORKER_SCHEDULER),
                catalogServiceDataSource = instance()
            )
        }

        bind<RegisterProductsOnDatabase>() with singleton {
            RegisterProductsOnDatabase(
                workerScheduler = instance(WORKER_SCHEDULER),
                catalogDatabaseDatasource = instance()
            )
        }
    }
}