package dev.mikkkkkkka.whatiknow.ui.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import dev.mikkkkkkka.whatiknow.ui.theme.WhatIKnowTheme

@AndroidEntryPoint
class AuthActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val viewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        setContent {
            WhatIKnowTheme {
                AuthRoute(
                    viewModel = viewModel,
                    onCompleted = {
                        Toast.makeText(this, "Sync connected", Toast.LENGTH_SHORT).show()
                        setResult(Activity.RESULT_OK)
                        finish()
                    },
                )
            }
        }
    }

    companion object {
        fun createIntent(context: Context): Intent = Intent(context, AuthActivity::class.java)
    }
}
