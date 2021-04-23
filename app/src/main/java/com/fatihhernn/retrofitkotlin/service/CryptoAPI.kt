package com.fatihhernn.retrofitkotlin.service

import com.fatihhernn.retrofitkotlin.model.CryptoModel
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.GET

interface CryptoAPI {


    @GET("prices?key=acf6bd642af90051bb475a867cafd79c")
    fun getData(): Observable<List<CryptoModel>>
    //fun getData(): Call<List<CryptoModel>>

}