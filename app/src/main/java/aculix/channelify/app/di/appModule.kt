package aculix.channelify.app.di

import aculix.channelify.app.BuildConfig
import aculix.channelify.app.R
import aculix.channelify.app.db.ChannelifyDatabase
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.room.Room
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.security.MessageDigest


val appModule = module {

    single { provideApplicationSignature(androidContext()) }
    single { provideOkHttpClient(androidContext(), get()) }
    single { provideRetrofit(get()) }
    single { provideRoomDatabase(androidApplication()) }
}

@SuppressLint("PackageManagerGetSignatures")
fun provideApplicationSignature(context: Context): List<String> {
    val signatureList: List<String>
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val signature = context.packageManager.getPackageInfo(
                BuildConfig.APPLICATION_ID,
                PackageManager.GET_SIGNING_CERTIFICATES
            ).signingInfo
            signatureList = if (signature.hasMultipleSigners()) {
                signature.apkContentsSigners.map {
                    val digest = MessageDigest.getInstance("SHA")
                    digest.update(it.toByteArray())
                    bytesToHex(digest.digest())
                }
            } else {
                signature.signingCertificateHistory.map {
                    val digest = MessageDigest.getInstance("SHA")
                    digest.update(it.toByteArray())
                    bytesToHex(digest.digest())
                }
            }
        } else {
            val signature = context.packageManager.getPackageInfo(
                BuildConfig.APPLICATION_ID,
                PackageManager.GET_SIGNATURES
            ).signatures
            signatureList = signature.map {
                val digest = MessageDigest.getInstance("SHA")
                digest.update(it.toByteArray())
                bytesToHex(digest.digest())
            }
        }
        return signatureList
    } catch (e: Exception) {
        Timber.e(e.stackTrace.toString())
    }
    return emptyList()
}

fun bytesToHex(bytes: ByteArray): String {
    val hexArray =
        charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')
    val hexChars = CharArray(bytes.size * 2)
    var v: Int
    for (j in bytes.indices) {
        v = bytes[j].toInt() and 0xFF
        hexChars[j * 2] = hexArray[v.ushr(4)]
        hexChars[j * 2 + 1] = hexArray[v and 0x0F]
    }
    return String(hexChars)
}

private fun provideOkHttpClient(
    androidContext: Context,
    signatureList: List<String>
): OkHttpClient {
    val httpClient = OkHttpClient.Builder()

    httpClient.addInterceptor(object : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val original = chain.request()
            val originalHttpUrl = original.url

            val url = originalHttpUrl.newBuilder()
                .addQueryParameter("key", androidContext.getString(R.string.youtube_api_key))
                .build()

            val requestBuilder = original.newBuilder().url(url)
                .addHeader("X-Android-Package", BuildConfig.APPLICATION_ID)
                .addHeader("X-Android-Cert", signatureList[0])
            val request = requestBuilder.build()

            return chain.proceed(request)
        }
    })

    return httpClient.build()
}

private fun provideRetrofit(httpClient: OkHttpClient) = Retrofit.Builder()
    .baseUrl("https://www.googleapis.com/youtube/v3/")
    .client(httpClient)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

private fun provideRoomDatabase(androidApplication: Application) =
    Room.databaseBuilder(androidApplication, ChannelifyDatabase::class.java, "channelify-db")
        .build()
