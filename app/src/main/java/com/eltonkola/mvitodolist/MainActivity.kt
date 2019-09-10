package com.eltonkola.mvitodolist

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.eltonkola.mvitodolist.ui.DemoFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, DemoFragment.newInstance())
                .commitNow()
        }
    }


}
