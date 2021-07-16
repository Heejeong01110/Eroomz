package com.example.eroomz

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import java.io.IOException
import com.example.eroomz.UserResponse as UserResponses

class NearUnivActivity : AppCompatActivity(R.layout.activity_app_main){
    private val client = Retrofit.Builder()
    private val api_url = "https://apis.zigbang.com/"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.near_univ_activity)


        realApiCall(api_url)
    }


    // Actual API call with Retrofit
    private fun realApiCall(url: String){
        /* Creates an instance of the UserService using a simple Retrofit builder using Moshi
     * as a JSON converter, this will append the endpoints set on the UserService interface
     * (for example '/api', '/api?results=2') with the base URL set here, resulting on the
     * full URL that will be called: 'https://randomuser.me/api' */
        val service = client
                .baseUrl(url)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(UserService::class.java)
        /* Calls the endpoint set on getUsers (/ l;api) from UserService using enqueue method
     * that creates a new worker thread to make the HTTP call */
        CoroutineScope(Dispatchers.IO).launch {
            val response = service.getUsers()
            withContext(Dispatchers.Main){
                if(response.isSuccessful){
                    val items = response.body()
                    Log.d("Tag_Items", items.toString())
                } else{
                    Toast.makeText(applicationContext, response.code(), Toast.LENGTH_SHORT).show()
                }

            }
        }
    }

}

/* Kotlin data/model classes that map the JSON response, we could also add Moshi
 * annotations to help the compiler with the mappings on a production app */
data class UserResponses(val result: List<User>)
data class User(val email: String, val phone: String)

/* Retrofit service that maps the different endpoints on the API, you'd create one
 * method per endpoint, and use the @Path, @Query and other annotations to customize
 * these at runtime
 * UserResponse.kt 를 참고해서 포매팅을 하면 됩니당 */
interface UserService{
    @GET("v2/items?geohash=wydhe&sales_type_in=전세%7C월세&service_type_eq=원룸")
    suspend fun getUsers(): Response<UserResponses>
}