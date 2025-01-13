package com.nianticlabs.pokemongo.activities

import android.os.Bundle
import android.view.KeyEvent
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.nianticlabs.pokemongo.R
import com.nianticlabs.pokemongo.activities.MainActivity.Companion.setSharedVars
import com.nianticlabs.pokemongo.databinding.ActivityDriverManagerBinding
import com.nianticlabs.pokemongo.fragments.DriverListFragment
import java.io.File

class DriverManagerActivity : AppCompatActivity() {
    private var binding: ActivityDriverManagerBinding? = null
    private var backButton: ImageButton? = null
    private var ratManagerToolBar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDriverManagerBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        backButton = findViewById(R.id.backButton)
        backButton?.setOnClickListener {
            onKeyDown(KeyEvent.KEYCODE_BACK, null)
        }

        ratManagerToolBar = findViewById(R.id.driverManagerToolbar)
        ratManagerToolBar?.setTitle(R.string.driver_manager_title)

        fragmentLoader(DriverListFragment())
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish()
        }

        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        setSharedVars(this)
    }

    private fun fragmentLoader(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.rat_manager_content, fragment)
            commit()
        }
    }

    companion object {
        private val gson = Gson()

        fun generateICDFile(driverLib: String, destIcd: File) {
            val json = gson.toJson(mapOf(
                "ICD" to mapOf(
                    "api_version" to "1.1.296",
                    "library_path" to driverLib
                ),
                "file_format_version" to "1.0.0"
            ))

            destIcd.writeText(json)
        }
    }
}