package org.example.upload

import retrofit2.http.Body
import retrofit2.http.POST
import upload.ApkDTO

interface Api {
    @POST("/wallet-backend/v1/app-versions")
    suspend fun postApk(@Body cmd: ApkDTO): ApkDTO
}