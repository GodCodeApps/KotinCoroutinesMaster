package com.example.kotincoroutinesmaster

import android.os.Bundle
import android.os.Looper
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_scrolling.*
import kotlinx.coroutines.*

class ScrollingActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    val arrayList = IntArray(10001)
    var testStartTime: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)
        loadDataAndShow()
        setSupportActionBar(toolbar)
        fab.setOnClickListener { view ->
            testStartTime = System.currentTimeMillis()
            var str = test2()

            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

    }

    // suspend 方法能够使协程执行暂停，等执行完毕后在返回结果，同时不会阻塞线程 只暂停协程，但不阻塞线程。直到方法返回结果，
    // 这样也不用写 Callback 来取结果，可以使用同步的方式来写异步代码
    //Dispatchers.Default	共享后台线程池里的线程
    //Dispatchers.Main	Android主线程
    //Dispatchers.IO	共享后台线程池里的线程
    //Dispatchers.Unconfined	不限制，使用父Coroutine的现场
    //newSingleThreadContext	使用新的线程
    // CoroutineScope by MainScope()
    fun test2() {
        //阻塞
        var job = runBlocking(Dispatchers.IO) {
            var thread = Thread.currentThread() == Looper.getMainLooper().thread
            Log.e("Mainthread", "runBlocking==$thread")
            arrayList.forEachIndexed { index, i ->
                if (index == 10000) {
                    val testTime = System.currentTimeMillis() - testStartTime
                    Log.e("test", "${"runBlocking=="}$testTime")
                }
            }


            //非阻塞
            GlobalScope.launch(Dispatchers.IO) {
                var thread = Thread.currentThread() == Looper.getMainLooper().thread
                Log.e("Mainthread", "launch1==$thread")
                var str = test1()
                //切换主线程
                withContext(Dispatchers.Main) {
                    var thread = Thread.currentThread() == Looper.getMainLooper().thread
                    Log.e("Mainthread", "launch1==$thread$str")
                }
            }


            //协程作用域
            coroutineScope {
                //非阻塞
                launch {
                    test1()
                    var thread = Thread.currentThread() == Looper.getMainLooper().thread
                    Log.e("Mainthread", "launch2==$thread")
                    arrayList.forEachIndexed { index, i ->
                        if (index == 10000) {
                            val testTime = System.currentTimeMillis() - testStartTime
                            Log.e("test", "${"launch=="}$testTime")
                        }
                    }

                }
                Log.e("test", "coroutineScope====")
            }


            Log.e("test", "runBlocking====")
        }
        Log.e("test", "runBlocking")

    }


    fun test1(): String {
        arrayList.forEachIndexed { index, i ->
            if (index == 10000) {
                val testTime = System.currentTimeMillis() - testStartTime
                Log.e("test", "${"test1=="}$testTime")
            }
            return "结束啦"
        }
        return "结束啦"
    }

    fun loadDataAndShow() {
        GlobalScope.launch(Dispatchers.IO) {
            //IO 线程里拉取数据
            var result = fetchData()
            withContext(Dispatchers.Main) {
                //主线程里更新 UI
                Log.e("test", "主线程里更新 UI==$result")
            }
        }
    }

    suspend fun fetchData(): String {
        delay(2000)
        Log.e("test", "delay2000")
        return "content"
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }






















































    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_scrolling, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
