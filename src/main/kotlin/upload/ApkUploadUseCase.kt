package org.example.upload

import kotlinx.coroutines.runBlocking
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import upload.ApkDTO
import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest
import kotlin.concurrent.thread

class ApkUploadUseCase(
    val versionCode: Long = 7,
    val versionName: String = "0.0.7",
) {
    val baseUrl = "https://api.wisdom-bank.com"
    val retrofit = Retrofit.Builder()
        .apply {
            baseUrl(baseUrl)
            addConverterFactory(GsonConverterFactory.create())
            //addConverterFactory(MoshiConverterFactory.create())
        }
        .build()
    val walletApi: Api = retrofit.create()
    operator fun invoke() {
        val apk_url = "https://wisdom-pkg.s3.us-east-1.amazonaws.com/wisdomuae-0.0.7.apk"
        runBlocking {
            upload(apk_url)
        }
    }

    suspend fun upload(apk_url: String) {
//        val path = "/home/lcj/Huolian/wisdom-uae-Android/product/mobile/build/outputs/apk/debug/0.0.6.apk"
        val path = "/Users/lcj/HuoLian/wisdom-uae-Android/product/mobile/build/outputs/apk/debug/$versionName.apk"
        val sha_256 = calculateFileHash(File(path))
        println("sha_256=$sha_256")
        //010240176e992f1fd656634153b0f09a911f8071b838ce2e230fe0edb5d7a2a9
        val cmd = ApkDTO(
            version_code = versionCode,
            version_name = versionName,
            upgrade_content = "1.Assets module add select network\n2.Refactor wallet module\n3.Bug fix",
            pkg_url = apk_url,
            is_force_upgrade = false,
            app_platform = 1,
            hash_256 = sha_256,
        )

        val result = runCatching { walletApi.postApk(cmd) }
        if (result.isFailure) {
            println("it=$result")
        }
        if (result.isSuccess) {
            println("Upload successful")
        }
    }

    fun calculateFileHash(file: File, algorithm: String = "SHA-256"): String {
        val digest = MessageDigest.getInstance(algorithm)
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)

        FileInputStream(file).use { input ->
            var bytesRead: Int
            while (input.read(buffer).also { bytesRead = it } != -1) {
                digest.update(buffer, 0, bytesRead)
            }
        }

        // 转换为十六进制字符串
        val hashBytes = digest.digest()
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}