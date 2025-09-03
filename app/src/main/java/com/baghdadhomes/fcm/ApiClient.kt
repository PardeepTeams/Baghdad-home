package com.baghdadhomes.fcm

import com.google.gson.GsonBuilder
import com.baghdadhomes.PreferencesService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import okio.IOException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.*

object ApiClient {
    //private const val BASE_URL_NOTIFICATION = "https://fcm.googleapis.com/"
    //
    private const val BASE_URL_NOTIFICATION = "https://fcm.googleapis.com/"
   // const val baseUrl = "https://najafhome.com/wp-json/"
  //  const val baseUrl = "https://najafhome.com/baghdad/wp-json/"
    const val baseUrl = "https://baghdadhome.com/wp-json/"
    const val baseUrlSmsApi = "https://gateway.standingtech.com/"

    private var retrofit: Retrofit? = null
    val client: Retrofit?
        get() {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL_NOTIFICATION)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(getOkHttpClient())
                    .build()
            }
            return retrofit
        }

    fun getOkHttpClient(): OkHttpClient {
        try {
            var trustAllCerts =  arrayOf<TrustManager>( object :
                X509TrustManager {
                override fun checkClientTrusted(
                    chain: Array<out X509Certificate>?,
                    authType: String?
                ) {

                }

                override fun checkServerTrusted(
                    chain: Array<out X509Certificate>?,
                    authType: String?
                ) {

                }

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }

            }
            )

            var sslContext  = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, java.security.SecureRandom());
            var sslSocketFactory = sslContext.getSocketFactory();
            var builder:OkHttpClient.Builder =  OkHttpClient.Builder();
            val trustManager = trustAllCerts.get(0) as X509TrustManager
            builder.sslSocketFactory(sslSocketFactory,trustManager);
            builder.hostnameVerifier(object : HostnameVerifier {
                override fun verify(hostname: String?, session: SSLSession?): Boolean {
                    return true;
                }
            });
            val logger = HttpLoggingInterceptor()
            logger.level = HttpLoggingInterceptor.Level.BODY
            var okHttpClient   = builder.connectTimeout(2, TimeUnit.MINUTES) // connect timeout
                .writeTimeout(5, TimeUnit.MINUTES) // write timeout
                .readTimeout(5, TimeUnit.MINUTES) // read timeout
                //.addInterceptor(DefaultRequestInterceptor())
                .addNetworkInterceptor(logger)
                .build();
            return okHttpClient;
        } catch (e: Exception) {
            throw  RuntimeException(e);
        }
        /*    val logger = HttpLoggingInterceptor()
            logger.level = HttpLoggingInterceptor.Level.BODY

            return OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.MINUTES) // connect timeout
                .writeTimeout(5, TimeUnit.MINUTES) // write timeout
                .readTimeout(5, TimeUnit.MINUTES) // read timeout
                .addInterceptor(DefaultRequestInterceptor())
                .addNetworkInterceptor(logger)
                .build()*/
    }

    private var apiService: ApiService? = null
    val apiWithoutToken: ApiService?
        get() {
            if (apiService == null) {
                val logging = HttpLoggingInterceptor()
                logging.setLevel(HttpLoggingInterceptor.Level.BODY)

                val httpClient = OkHttpClient.Builder().connectTimeout(100, TimeUnit.SECONDS)
                httpClient.networkInterceptors().add(object : Interceptor {
                    @Throws(IOException::class)
                    override fun intercept(chain: Interceptor.Chain): Response {
                        val original = chain.request()
                        val request = original.newBuilder()
                            .method(original.method, original.body)
                            .addHeader("language", PreferencesService.instance.getLanguage())
                            .build()

                        return chain.proceed(request)
                    }
                })

                httpClient.addInterceptor(logging)
                httpClient.readTimeout(2.toLong() * 60, TimeUnit.SECONDS)
                httpClient.writeTimeout(2.toLong() * 60, TimeUnit.SECONDS)
                val client = httpClient.build()

                val gson = GsonBuilder()
                    .setLenient()
                    .create()


                val retrofit = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(getOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create(gson))

                    .build()


                apiService = retrofit.create<ApiService>(ApiService::class.java)
            }

            return apiService
        }
    val api: ApiService?
        get() {
            if (apiService == null) {
                val logging = HttpLoggingInterceptor()
                logging.setLevel(HttpLoggingInterceptor.Level.BODY)

                val httpClient = OkHttpClient.Builder().connectTimeout(100, TimeUnit.SECONDS)
                httpClient.networkInterceptors().add(object : Interceptor {
                    @Throws(IOException::class)
                    override fun intercept(chain: Interceptor.Chain): Response {
                        val original = chain.request()

                        val request = original.newBuilder()
                            .method(original.method, original.body)
                            .addHeader("language", PreferencesService.instance.getLanguage())
                            .build()

                        return chain.proceed(request)
                    }
                })

                httpClient.addInterceptor(logging)
                httpClient.readTimeout(2.toLong() * 60, TimeUnit.SECONDS)
                httpClient.writeTimeout(2.toLong() * 60, TimeUnit.SECONDS)

                val client = httpClient.build()

                val gson = GsonBuilder()
                    .setLenient()
                    .create()

                val retrofit = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(getOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()


                apiService = retrofit.create<ApiService>(ApiService::class.java)
            }

            return apiService
        }

    val api2: ApiService?
        get() {
            if (apiService == null) {
                val okHttpClient: OkHttpClient = OkHttpClient.Builder()
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(100, TimeUnit.SECONDS)
                    .build()
                val retrofit = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build()
                apiService = retrofit.create(ApiService::class.java)

            }
            /*            val logging = HttpLoggingInterceptor()
                        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

                        val httpClient = OkHttpClient.Builder().connectTimeout(100, TimeUnit.SECONDS)
                        httpClient.addInterceptor(logging)

                        httpClient.readTimeout(2.toLong() * 60, TimeUnit.SECONDS)
                        httpClient.writeTimeout(2.toLong() * 60, TimeUnit.SECONDS)

                        val client = httpClient.build()

                        val gson = GsonBuilder()
                            .setLenient()
                            .create()

                        val retrofit = Retrofit.Builder()
                            .baseUrl(baseUrl)
                            .client(client)
                            .addConverterFactory(GsonConverterFactory.create(gson))
                            .build()

                        apiService = retrofit.create<ApiService>(ApiService::class.java)
                    }

                    return apiService*/
            return apiService
        }

    val smsClient: ApiService?
        get() {
            if (apiService == null) {
                val okHttpClient: OkHttpClient = OkHttpClient.Builder()
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(100, TimeUnit.SECONDS)
                    .build()
                val retrofit = Retrofit.Builder()
                    .baseUrl(baseUrlSmsApi)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build()
                apiService = retrofit.create(ApiService::class.java)

            }
            return apiService
        }
}