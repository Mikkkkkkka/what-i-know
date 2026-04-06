package dev.mikkkkkkka.whatiknow.ui.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import dev.mikkkkkkka.whatiknow.databinding.ActivityAuthBinding

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.loginButton.setOnClickListener {
            viewModel.login(
                username = binding.usernameEditText.text?.toString().orEmpty(),
                password = binding.passwordEditText.text?.toString().orEmpty(),
            )
        }
        binding.registerButton.setOnClickListener {
            viewModel.register(
                username = binding.usernameEditText.text?.toString().orEmpty(),
                password = binding.passwordEditText.text?.toString().orEmpty(),
            )
        }

        viewModel.state.observe(this) { state ->
            binding.progressBar.isVisible = state.isLoading
            binding.errorTextView.isVisible = !state.errorMessage.isNullOrBlank()
            binding.errorTextView.text = state.errorMessage.orEmpty()
            binding.loginButton.isEnabled = !state.isLoading
            binding.registerButton.isEnabled = !state.isLoading
        }
        viewModel.complete.observe(this) { completed ->
            if (!completed) return@observe
            Toast.makeText(this, "Sync connected", Toast.LENGTH_SHORT).show()
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    companion object {
        fun createIntent(context: Context): Intent = Intent(context, AuthActivity::class.java)
    }
}
