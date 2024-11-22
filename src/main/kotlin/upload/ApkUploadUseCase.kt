package org.example.upload

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import upload.ApkDTO
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.security.DigestInputStream
import java.security.MessageDigest
import kotlin.concurrent.thread

class ApkUploadUseCase(
    val versionCode: Long = 15,
    val versionName: String = "1.0.5",
    val is_force_upgrade: Boolean = true,
    val upgrade_content: String = "• MPC i18n\n• Fix asset detail bug\n• Fix other issue",
    val apk_url: String = "https://wisdom-pkg.s3.us-east-1.amazonaws.com/wisdomuae-${versionName}.apk"
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

        runBlocking {
            upload()
        }
    }

    suspend fun upload() {
        val path = "/Users/lcj/HuoLian/wisdom-uae-Android/product/mobile/build/outputs/apk/debug/$versionName.apk"
        val sha_256 = calculateFileHash(File(path))
        println("sha_256=$sha_256")
        //010240176e992f1fd656634153b0f09a911f8071b838ce2e230fe0edb5d7a2a9
        val cmd = ApkDTO(
            version_code = versionCode,
            version_name = versionName,
            upgrade_content = upgrade_content,
            pkg_url = apk_url,
            is_force_upgrade = is_force_upgrade,
            app_platform = 1,
            hash_256 = sha_256,
            pkg_size = File(path).length(),
        )
        val result = runCatching { walletApi.postApk(cmd) }

        if (result.isFailure) {
            println("it=$result")
            return
        }

        println("Upload successful")
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

    suspend fun calculateHash(stream: InputStream): String {
        return withContext(Dispatchers.IO) {
            val digest = MessageDigest.getInstance("SHA-512")
            val digestStream = DigestInputStream(stream, digest)
            while (digestStream.read() != -1) {
                // The DigestInputStream does the work; nothing for us to do.
            }
            digest.digest().joinToString(":") { "%02x".format(it) }
        }
    }
}