package com.marat.electrodecatalog.ui

import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.marat.electrodecatalog.R
import com.marat.electrodecatalog.ui.electrode_list.ElectrodeListFragment
import com.marat.electrodecatalog.utils.doOnApplyWindowInsets
import com.marat.electrodecatalog.utils.updatePadding
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        configureSystemBars()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupInsets()
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ElectrodeListFragment())
                .commitNow()
        }
    }

    private fun configureSystemBars() {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = window.decorView.systemUiVisibility
            window.statusBarColor = ContextCompat.getColor(this, android.R.color.transparent)
        } else {
            window.statusBarColor = ContextCompat.getColor(this, R.color.statusBarColor)
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or
                    View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }

        window.navigationBarColor = ContextCompat.getColor(this, R.color.navigationBarColor)
    }

    private fun setupInsets() {
        fragmentContainer.doOnApplyWindowInsets { view, insets, initialPadding ->
            view.updatePadding(
                left = initialPadding.left + insets.systemWindowInsetLeft,
                right = initialPadding.right + insets.systemWindowInsetRight
            )
            insets.replaceSystemWindowInsets(
                Rect(
                    0,
                    insets.systemWindowInsetTop,
                    0,
                    insets.systemWindowInsetBottom
                )
            )
        }
    }

}

