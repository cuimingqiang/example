package com.cmq.demo.main

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cmq.base.RouteRetrofit
import com.cmq.base.bind
import com.cmq.base.dp
import com.cmq.demo.R
import com.cmq.demo.app.RouteConfig
import java.lang.reflect.Field

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        bind<RecyclerView>(R.id.recyclerView).value.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            val padding = 1.dp
            addItemDecoration(object :RecyclerView.ItemDecoration(){
                override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                    outRect.set(0,0,0,padding)
                }
            })
            adapter = Adapter().apply {
                data = arrayListOf(
                        DataItem("换肤", "可以更换主题皮肤") {
                            RouteRetrofit.getRouteConfig(RouteConfig::class.java).startTestSkinActivity()
                        },
                        DataItem("DEX替换", "冷修复") {
                            RouteRetrofit.getRouteConfig(RouteConfig::class.java).startTestDexActivity()
                        },
                        DataItem("方法替换", "无需重启,直接替换方法") {
                            val theme = resources.newTheme().apply { applyStyle(R.style.Theme_Example,true) }

                        },
                        //AMS核心分析三
                        //hook Instrumentation
                        DataItem("HookActivity启动", "可以启动未注册的Activity") {
                            ParseApk.parse(this@MainActivity.application)
                        },
                        DataItem("音视频", "播放音视频已经直播信息") {
                            RouteRetrofit.getRouteConfig(RouteConfig::class.java).startTestAvActivity()
                        }
                )
            }
        }
    }


    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if(ev?.action == MotionEvent.ACTION_UP){
           var t = ViewGroup::class.java.getDeclaredField("mFirstTouchTarget") as Field
            t.isAccessible = true
           var value = t.get(window.decorView)
           var child = value.javaClass.getDeclaredField("child") as Field

            println(child.get(value).toString())
        }
        return super.dispatchTouchEvent(ev)
    }

}