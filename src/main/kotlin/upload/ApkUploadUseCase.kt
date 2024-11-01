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

class ApkUploadUseCase {
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
        val apk_url = "https://wisdom-pkg.s3.us-east-1.amazonaws.com/wisdomuae-0.0.5.apk"
        runBlocking {
            upload(apk_url)
        }
    }

    suspend fun upload(apk_url: String) {
        val path = "/Users/lcj/HuoLian/wisdom-uae-Android/product/mobile/build/outputs/apk/debug/wisdom-0.0.5.apk"
        val hash_256 = calculateFileHash(File(path))
        println("hash=$hash_256")
        //010240176e992f1fd656634153b0f09a911f8071b838ce2e230fe0edb5d7a2a9
        val cmd = ApkDTO(
            version_code = 5,
            version_name = "0.0.5",
            upgrade_content = "1.Fix mint nft id issue\n2.Fix my assets sort issue\n3.Fix add token issue\n4.optimize business logic\n5.Bug fix",
            pkg_url = apk_url,
            is_force_upgrade = true,
            app_platform = 1,
            hash_256 = hash_256,
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