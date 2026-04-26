package com.example.myapplication111

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btn = findViewById<Button>(R.id.btnFind)
        val result = findViewById<TextView>(R.id.result)

        val loc1 = findViewById<EditText>(R.id.location1)
        val loc2 = findViewById<EditText>(R.id.location2)
        val loc3 = findViewById<EditText>(R.id.location3)

        btn.setOnClickListener {

            val text1 = loc1.text.toString()
            val text2 = loc2.text.toString()
            val text3 = loc3.text.toString()

            if (text1.isEmpty() || text2.isEmpty() || text3.isEmpty()) {
                result.text = "모든 위치를 입력해주세요"
                return@setOnClickListener
            }

            // API 호출 시작
            getCoordinates(text1) { lat1, lon1 ->
                getCoordinates(text2) { lat2, lon2 ->
                    getCoordinates(text3) { lat3, lon3 ->

                        val midLat = (lat1 + lat2 + lat3) / 3
                        val midLon = (lon1 + lon2 + lon3) / 3

                        runOnUiThread {
                            result.text = "중간 좌표:\n위도: $midLat\n경도: $midLon"
                        }
                    }
                }
            }
        }
    }
}

/* 🔥 API 함수 (클래스 밖에 있어야 함) */
fun getCoordinates(address: String, callback: (Double, Double) -> Unit) {
    val client = OkHttpClient()

    val url = "https://dapi.kakao.com/v2/local/search/address.json?query=$address"

    val request = Request.Builder()
        .url(url)
        .addHeader("Authorization", "KakaoAK 60adc96b7c111bfffbe014fabd8f6649") // ⭐ 여기 바꿔야됨
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            e.printStackTrace()
        }

        override fun onResponse(call: Call, response: Response) {
            val body = response.body?.string()
            val json = JSONObject(body)
            val documents = json.getJSONArray("documents")

            if (documents.length() > 0) {
                val first = documents.getJSONObject(0)
                val x = first.getString("x").toDouble() // 경도
                val y = first.getString("y").toDouble() // 위도

                callback(y, x)
            }
        }
    })
}