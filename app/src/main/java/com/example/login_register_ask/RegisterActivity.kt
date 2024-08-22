package com.example.login_register_ask

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.login_register_ask.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("users")
        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnRegi.setOnClickListener {
            val email = binding.RgiEmail.text.toString()
            val password = binding.RgiPass.text.toString()
            val confpass = binding.RgiConfPass.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && confpass.isNotEmpty()) {
                registerUser(email, password , confpass)
                if (password == confpass) {
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, "Register Sucessful", Toast.LENGTH_SHORT)
                                    .show()
                                startActivity(Intent(this, LoginActivity::class.java))
                                finish()
                            } else {
                                Toast.makeText(this, "Register Unsuccessful", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Plese enter Email and Password", Toast.LENGTH_SHORT)
                        .show()
                    Toast.makeText(this@RegisterActivity,"All Field Are mandatory",Toast.LENGTH_SHORT).show()
                }

            }

        }
        binding.Regitext.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun registerUser(email: String, pass: String ,confpass: String){
        databaseReference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists()){
                    val id = databaseReference.push().key
                    val userData = UserData(id,email,pass,confpass)
                    databaseReference.child(id!!).setValue(userData)
                    Toast.makeText(this@RegisterActivity,"Register Successful",Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@RegisterActivity,LoginActivity::class.java))
                    finish()
                }else{
                    Toast.makeText(this@RegisterActivity,"User Already Exists",Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@RegisterActivity,"Database Error : ${databaseError.message}",Toast.LENGTH_SHORT).show()
            }
        })
    }
}