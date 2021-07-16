package com.example.eroomz

import com.squareup.moshi.Json

/*
    Converts response from webserver providing the api in a form of json
    to specified format in "NearUnivActivity.kt"

    val service = client
                .baseUrl(url)
                .addConverterFactory(MoshiConverterFactory.create()) <- 여기
                .build()
                .create(UserService::class.java)
    in this case Moshi format(json to java objects)
    자세한 정보는 여기 (https://github.com/square/moshi)
 */

data class UserResponse (
    @field:Json(name = "clusters")
    val isSuccess: List<Int>?,

    @field:Json(name = "items")
    val items: List<Items>
)

data class Items(
    @field:Json(name = "item_id")
    val id: Integer,

    @field:Json(name = "lat")
    val latitude: Float,

    @field:Json(name = "lng")
    val longitude: Float,
    )

