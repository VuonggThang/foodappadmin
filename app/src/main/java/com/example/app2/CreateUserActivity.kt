package com.example.app2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.app2.databinding.ActivityCreateUserBinding
import com.example.app2.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class CreateUserActivity : AppCompatActivity() {
    private val binding: ActivityCreateUserBinding by lazy {
        ActivityCreateUserBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.backimageButton.setOnClickListener {
            finish()
        }
        binding.createUserButton.setOnClickListener{
            // Lấy giá trị từ EditText
            val name = binding.name.text.toString().trim()
            val emailOrPhone = binding.emailOrPhone.text.toString().trim()
            val password = binding.password.text.toString().trim()

            // Kiểm tra tính hợp lệ
            if (name.isEmpty()) {
                binding.name.error = "Vui lòng nhập tên"
                binding.name.requestFocus()
                return@setOnClickListener
            }

            if (emailOrPhone.isEmpty()) {
                binding.emailOrPhone.error = "Vui lòng nhập email hoặc số điện thoại"
                binding.emailOrPhone.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                binding.password.error = "Vui lòng nhập mật khẩu"
                binding.password.requestFocus()
                return@setOnClickListener
            }

            // Tạo người dùng mới (ví dụ: lưu vào Firebase)
            createUser(name, emailOrPhone, password)
            finish()
        }

    }

    private fun createUser(name: String, emailOrPhone: String, password: String) {
        val auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(emailOrPhone, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Tạo người dùng thành công, chuyển đến màn hình tiếp theo
                    Toast.makeText(this, "Người dùng được tạo thành công", Toast.LENGTH_SHORT).show()
                    // Có thể chuyển hướng đến một Activity khác
                } else {
                    // Thông báo lỗi
                    Toast.makeText(this, "Lỗi: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}