package com.ringga.security.ui.profile
/*=================== T H A N K   Y O U ===================*/
/*============= TELAH MENGUNAKAN CODE SAYA ================*/
/* https://github.com/ringga-dev */
/*=========================================================*/
/*     R I N G G A   S E P T I A  P R I B A D I            */
/*=========================================================*/
import android.R.attr
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import com.ringga.security.R
import com.ringga.security.data.api.RetrofitClient
import com.ringga.security.data.model.auth.BaseRespon
import com.ringga.security.data.util.UploadRequestBody
import com.ringga.security.database.SharedPrefManager
import com.ringga.security.ui.auth.LoginActivity
import com.ringga.security.util.toast
import kotlinx.android.synthetic.main.activity_profile.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import android.R.attr.data
import android.content.IntentSender
import android.graphics.Canvas
import android.provider.Settings

import androidx.documentfile.provider.DocumentFile
import coil.api.load
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.ringga.security.data.api.RetrofitClient.BASE_URL
import com.ringga.security.util.getFileName


class ProfileActivity : AppCompatActivity() {

    lateinit var currentPhotoPath: String
    private var selectetImage: Uri? = null
    private val REQUEST_CODE_IMAGE_PICKER = 100
    private val REQUEST_CODE_IMAGE_CAMERA = 101
    private var nameFile: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val myProfile = SharedPrefManager.getInstance(this)?.user

        tv_name.text = myProfile?.name
        tv_id_bet.text = myProfile?.id_bet
        tv_devisi.text = myProfile?.devisi
        tv_email.text = myProfile?.email
        tv_phone.text = myProfile?.no_phone
        profile_image.load(BASE_URL + "/assets/image/profile/" + myProfile?.image)

        lokasi.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }

        btn_edit.setOnClickListener {
            startActivity(Intent(this, EditPasswordActivity::class.java))
        }

        btn_log_aut.setOnClickListener {
            alert()
        }

        chage_image.setOnClickListener {
            showPictureDialog()
        }
        showSettingAlert()
    }






    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(
            pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> openImageChooser()
                1 -> dispatchTakePictureIntent()
            }
        }
        pictureDialog.show()
    }

    private fun openImageChooser() {
        Intent(Intent.ACTION_PICK).also {
            it.type = "image/*"
            val mimeTypes = arrayOf("image/jpeg", "image/png")
            it.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            startActivityForResult(it, REQUEST_CODE_IMAGE_PICKER)
        }
    }

    private fun uploadImage() {
        //mendapatkan data user
        val mypref = SharedPrefManager.getInstance(this)!!.user
        val id = mypref.id
        //inputan user


        if (nameFile != null) {
            val parcelFileDescriptor =
                contentResolver.openFileDescriptor(selectetImage!!, "r", null) ?: return
            val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
            val file = File(cacheDir, nameFile)
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            val body = UploadRequestBody(file, "image")
//            mendapatkan data lokasi


            RetrofitClient.instance.uploadImage(
                id,
                MultipartBody.Part.createFormData(
                    "image",
                    file.name,
                    body
                )
            ).enqueue(object : Callback<BaseRespon> {
                override fun onResponse(call: Call<BaseRespon>, response: Response<BaseRespon>) {
                    response.body()?.let {
                        if (response.body()?.stts == true) {
                            SharedPrefManager.getInstance(this@ProfileActivity)?.clear()
                            toast(this@ProfileActivity, response.body()?.msg.toString())
                            startActivity(Intent(this@ProfileActivity, LoginActivity::class.java))
                        } else {
                            toast(this@ProfileActivity, response.body()?.msg.toString())
                        }

                    }
                }

                override fun onFailure(call: Call<BaseRespon>, t: Throwable) {
                    toast(this@ProfileActivity, t.message.toString())
                }

            })

        } else if (nameFile == null) {
            val parcelFileDescriptor =
                contentResolver.openFileDescriptor(selectetImage!!, "r", null) ?: return
            val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
            val file = File(cacheDir, contentResolver.getFileName(selectetImage!!))
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)

            val body = UploadRequestBody(file, "image")
//            mendapatkan data lokasi


            RetrofitClient.instance.uploadImage(
                id,
                MultipartBody.Part.createFormData(
                    "image",
                    file.name,
                    body
                )
            ).enqueue(object : Callback<BaseRespon> {
                override fun onResponse(call: Call<BaseRespon>, response: Response<BaseRespon>) {
                    if (response.body()?.stts == true) {
                        SharedPrefManager.getInstance(this@ProfileActivity)?.clear()
                        toast(this@ProfileActivity, response.body()?.msg.toString())
                        startActivity(Intent(this@ProfileActivity, LoginActivity::class.java))
                    } else {
                        toast(this@ProfileActivity, response.body()?.msg.toString())
                    }
                }

                override fun onFailure(call: Call<BaseRespon>, t: Throwable) {
                    toast(this@ProfileActivity, t.message.toString())
                }

            })

        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_IMAGE_CAMERA && resultCode == RESULT_OK) {
            val f = File(currentPhotoPath);
            profile_image.setImageURI(Uri.fromFile(f))
            Log.d("tag", "alamat file" + f.name)

            val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            val contentUri = Uri.fromFile(f)
            mediaScanIntent.data = contentUri
            this.sendBroadcast(mediaScanIntent)

            selectetImage = contentUri
            nameFile = f.name
//            profile_image.text = f.totalSpace.toString()
            uploadImage()
        } else if (requestCode == REQUEST_CODE_IMAGE_PICKER && resultCode == RESULT_OK) {
            if (data != null) {
                Log.d("tag", "alamat file" + data.data)
                selectetImage = data.data
                profile_image.setImageURI(selectetImage)
//                name_image.text =selectetImage.toString()
                uploadImage()
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
//        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        val storageDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun dispatchTakePictureIntent() {
        Toast.makeText(this, "open Camera ", Toast.LENGTH_LONG).show()
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {

                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.ringga.android.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_CODE_IMAGE_CAMERA)
                }
            }
        }
    }

    private fun alert() {
        val dialog = AlertDialog.Builder(this)
            // Judul
            .setTitle("Log out")
            // Pesan yang di tamopilkan
            .setMessage("apa anda yakin untuk keluar")
            .setPositiveButton(
                "Ya, saya ingin keluar",
                DialogInterface.OnClickListener { dialogInterface, i ->
                    SharedPrefManager.getInstance(this)!!.clear()
                    startActivity(Intent(baseContext, LoginActivity::class.java))
                    this.finish()
                })
            .setNegativeButton(
                "Tidak Terimakasih",
                DialogInterface.OnClickListener { dialogInterface, i ->
                    Toast.makeText(this, "di batalkan", Toast.LENGTH_LONG).show()
                })
            .show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
    }

    private fun showSettingAlert() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("GPS setting!")
        alertDialog.setMessage("GPS is not enabled, Do you want to go to settings menu? ")
        alertDialog.setPositiveButton(
            "Setting"
        ) { dialog, which ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            this.startActivity(intent)
        }
        alertDialog.setNegativeButton(
            "Cancel"
        ) { dialog, which -> dialog.cancel() }
        alertDialog.show()
    }

}