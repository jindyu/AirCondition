package com.ihris.aircondition.data

import android.util.Log
import com.ihris.aircondition.BuildConfig
import com.ihris.aircondition.data.models.airquality.MeasureValue
import com.ihris.aircondition.data.models.monitoringstation.MonitoringStation
import com.ihris.aircondition.data.services.AirKoreaApiService
import com.ihris.aircondition.data.services.KakaoLocalApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object Repository {

    suspend fun getNearbyMonitoringStation(latitude: Double, longitude: Double): MonitoringStation?{
        val tmCoordinates = kakaoLocalApiService
            .getTmCoordinates(longitude, latitude)
            .body()
            ?.documents
            ?.firstOrNull()

        val tmX = tmCoordinates?.x
        val tmY = tmCoordinates?.y

        Log.e("Repository","-=---------------- $latitude / $longitude, $tmX / $tmY")

        return airKoreaApiService
            .getNearbyMonitoringStation(tmY!!, tmX!!)
            .body()
            ?.response
            ?.body
            ?.monitoringStations
            ?.minByOrNull { it.tm ?: Double.MAX_VALUE }
    }

    suspend fun getLatestAirQualityData(stationName: String): MeasureValue? =
        airKoreaApiService
            .getRealtimeAirQualities(stationName)
            .body()
            ?.response
            ?.body
            ?.measureValues
            ?.firstOrNull()

    private val kakaoLocalApiService: KakaoLocalApiService by lazy {
        Retrofit.Builder()
            .baseUrl(Url.KAKAO_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(buildHttpClient())
            .build()
            .create()
    }

    private val airKoreaApiService: AirKoreaApiService by lazy {
        Retrofit.Builder()
            .baseUrl(Url.AIR_KOREA_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(buildHttpClient())
            .build()
            .create()
    }

    private fun buildHttpClient(): OkHttpClient =
        OkHttpClient().newBuilder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = if(BuildConfig.DEBUG){
                        HttpLoggingInterceptor.Level.BODY
                    } else {
                        HttpLoggingInterceptor.Level.NONE
                    }
                }
            )
            .build()
}