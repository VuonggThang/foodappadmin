package com.example.app2

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.example.app2.databinding.ActivityAddItemBinding
import com.example.app2.model.AllMenu
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.sql.DatabaseMetaData

class AddItemActivity : AppCompatActivity() {
    //food item details
    private lateinit var foodName: String
    private lateinit var foodPrice: String
    private lateinit var foodDescription: String
    private lateinit var foodIngredient: String
    private  var foodImageUri: Uri?= null
    //firebase
    private lateinit var auth:FirebaseAuth
    private lateinit var database:FirebaseDatabase

    private val binding : ActivityAddItemBinding by lazy {
        ActivityAddItemBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        //Initialize Firebase
        auth = FirebaseAuth.getInstance()
        //Initialize Firebase database instance
        database = FirebaseDatabase.getInstance()

        binding.AddItemButton.setOnClickListener{
            //get data from filed
            foodName = binding.foodName.text.toString().trim()
            foodPrice = binding.foodPrice.text.toString().trim()
            foodDescription = binding.description.text.toString().trim()
            foodIngredient = binding.ingredint.text.toString().trim()
            if(!(foodName.isBlank()||foodPrice.isBlank()||foodDescription.isBlank()||foodIngredient.isBlank())){
                uploadData()
                Toast.makeText(this,"Thêm item thành công",Toast.LENGTH_SHORT).show()
                finish()
            }else
                Toast.makeText(this,"Điền đầy đủ thông tin",Toast.LENGTH_SHORT).show()

        }
        binding.selectedIamge.setOnClickListener {
            pickImage.launch("image/*")
        }
        binding.backimageButton.setOnClickListener {
            finish()
        }

    }

    private fun uploadData() {
        //get a reference to the "menu" node in the database
        val menuRef = database.getReference("menu")
        //genrate a unique key for the  new menu item
        val newItemKey = menuRef.push().key
        if (foodImageUri!= null){
            val storageRef = FirebaseStorage.getInstance().reference
            val imageRef = storageRef.child("menu_images/${newItemKey}.jpg")
            val uploadTask = imageRef.putFile(foodImageUri!!)
            uploadTask.addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener {
                    downloadUrl ->
                    // create a new menu item
                    val newItem = AllMenu(
                        foodName = foodName,
                        foodPrice = foodPrice,
                        foodDescription = foodDescription,
                        foodIngredient = foodIngredient,
                        foodImage = downloadUrl.toString(),
                    )
                    newItemKey?.let {
                        key->
                        menuRef.child(key).setValue(newItem).addOnSuccessListener {
                            Toast.makeText(this,"dữ liệu tải lên thành công",Toast.LENGTH_SHORT).show()
                        }
                            .addOnFailureListener {
                                Toast.makeText(this,"dữ liệu tải lên không thành công",Toast.LENGTH_SHORT).show()
                            }
                    }
                }

            }.addOnFailureListener {
                Toast.makeText(this,"Image tải không thành công",Toast.LENGTH_SHORT).show()
            }
        }else {
            Toast.makeText(this,"Hãy chọn ảnh",Toast.LENGTH_SHORT).show()
        }
    }

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()){ uri ->
        if (uri != null){
            binding.selectedIamge.setImageURI(uri)
            foodImageUri = uri
        }

    }
}