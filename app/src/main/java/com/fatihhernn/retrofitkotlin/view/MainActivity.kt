package com.fatihhernn.retrofitkotlin.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fatihhernn.retrofitkotlin.R
import com.fatihhernn.retrofitkotlin.adapter.RecyclerViewAdapter
import com.fatihhernn.retrofitkotlin.model.CryptoModel
import com.fatihhernn.retrofitkotlin.service.CryptoAPI
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), RecyclerViewAdapter.Listener{

    private val BASEURL="https://api.nomics.com/v1/"

    //gelen verilerei alacağımız bir arrayList tanımlayalım
    private var cryptoModels:ArrayList<CryptoModel>?=null

    private var recyclerViewAdapter:RecyclerViewAdapter?=null

    //Disposable => uygulama kapandığında yapılan call işlemleri de dispose eder
    //farklı farklı disposable buraya koyarak hepsinde bir kerede kurtulmamızı sağlar
    private var compositeDisposable:CompositeDisposable?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //compositeDisposable initialize edelim
        compositeDisposable=CompositeDisposable()

        //recycler view
        //layout manager oluştur, recylerview'a alt alta mı, grid olarak mı koyulup koyulmayacağını belirliyoruz
        val layoutManager:RecyclerView.LayoutManager=LinearLayoutManager(this)
        recyclerView.layoutManager=layoutManager

        loadData()
    }

    fun loadData(){

        //retrofit objemizi oluşturduk
        val retrofit=Retrofit.Builder()
            .baseUrl(BASEURL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build().create(CryptoAPI::class.java)

        compositeDisposable?.add(retrofit.getData()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::handleResponse))



        /* RX JAVADAN ONCE ESKİ VERSİYON
        val service=retrofit.create(CryptoAPI::class.java)
        val call=service.getData()

        call.enqueue(object:Callback<List<CryptoModel>>{
            override fun onResponse(
                    call: Call<List<CryptoModel>>,
                    response: Response<List<CryptoModel>>
            ) {

                if (response.isSuccessful){
                    //aşağıdaki kod boş gelmemesi durumunda gir işlem yap
                    response.body()?.let {
                        cryptoModels= ArrayList(it)

                        cryptoModels?.let {
                            recyclerViewAdapter= RecyclerViewAdapter(it,this@MainActivity)
                            recyclerView.adapter=recyclerViewAdapter
                        }
                        //recyclerViewAdapter= RecyclerViewAdapter(cryptoModels!!,this@MainActivity) => bu kodu üsteki gibi kontrol ederek aldım


                    }
                }
            }

            override fun onFailure(call: Call<List<CryptoModel>>, t: Throwable) {
                t.printStackTrace()
            }

        })

         */
    }

    private fun handleResponse(cryptoList:List<CryptoModel>){
        cryptoModels= ArrayList(cryptoList)

        cryptoModels?.let {
            recyclerViewAdapter= RecyclerViewAdapter(it,this@MainActivity)
            recyclerView.adapter=recyclerViewAdapter
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable?.clear()
    }

    override fun onItemClick(cryptoModel: CryptoModel) {
        Toast.makeText(this,"Clicked : +${cryptoModel.currency}",Toast.LENGTH_LONG).show()
    }
}

