package br.pedroso.productcatalog.app.di.network

import br.pedroso.productcatalog.domain.network.CatalogServiceDataSource
import br.pedroso.productcatalog.network.catalogService.RetrofitCatalogServiceDataSource
import br.pedroso.productcatalog.network.catalogService.retrofit.CATALOG_SERVICE_BASE_URL
import br.pedroso.productcatalog.network.catalogService.retrofit.CatalogService
import br.pedroso.productcatalog.BuildConfig
import br.pedroso.productcatalog.app.di.DependenciesTags.NETWORK_CACHE_DIR
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.singleton
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class NetworkModule {

    val graph = Kodein.Module {

        bind<CatalogServiceDataSource>() with singleton {
            RetrofitCatalogServiceDataSource(
                catalogService = instance()
            )
        }

        bind<CatalogService>() with singleton {
            val retrofit: Retrofit = instance()
            retrofit.create(CatalogService::class.java)
        }

        bind<Retrofit>() with singleton {
            val httpClient: OkHttpClient = instance()

            Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(CATALOG_SERVICE_BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(httpClient)
                .build()
        }

        bind<OkHttpClient>() with singleton {
            val builder = OkHttpClient.Builder()

            if (BuildConfig.DEBUG) {
                val loggingInterceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
                    this.level = HttpLoggingInterceptor.Level.BODY
                }
                builder.addInterceptor(loggingInterceptor)
            }

            val cache: Cache = instance()
            builder.cache(cache)

            builder.build()
        }

        bind<Cache>() with singleton {
            val cacheDir: File = instance(NETWORK_CACHE_DIR)
            Cache(cacheDir, NETWORK_CACHE_SIZE)
        }

    }

    companion object {
        private const val NETWORK_CACHE_SIZE = 10L * 1024 * 1024
    }
}