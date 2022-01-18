package com.ringga.security.ui.home
/*=================== T H A N K   Y O U ===================*/
/*============= TELAH MENGUNAKAN CODE SAYA ================*/
            /* https://github.com/ringga-dev */
/*=========================================================*/
/*     R I N G G A   S E P T I A  P R I B A D I            */
/*=========================================================*/


import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import com.ringga.security.R
import com.ringga.security.data.api.RetrofitClient
import com.ringga.security.data.model.auth.BaseRespon
import com.ringga.security.data.model.patrol.ScanPatrol
import com.ringga.security.database.PreferencesToken
import com.ringga.security.database.PreferencesToken.Companion.getToken
import com.ringga.security.database.SharedPrefManager
import com.ringga.security.ui.auth.LoginActivity
import com.ringga.security.ui.history.*
import com.ringga.security.ui.home.fm.EtowaUserFragment
import com.ringga.security.ui.home.fm.PatrolFragment
import com.ringga.security.ui.home.fm.VisitorFragment
import com.ringga.security.ui.patrol.ListPatrolActivity
import com.ringga.security.ui.profile.EditProfileActivity
import com.ringga.security.ui.profile.ProfileActivity
import com.ringga.security.ui.scan.*
import com.ringga.security.ui.ui_user.UserLateActivity
import kotlinx.android.synthetic.main.activity_home.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class HomeActivity : AppCompatActivity() {


    private val TAG = "HomeActivity"
    private var permissionsRequired = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )



    private val PERMISSION_CALLBACK_CONSTANT = 100
    private val REQUEST_PERMISSION_SETTING = 101
    private var permissionStatus: SharedPreferences? = null
    private var sentToSettings = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        val myProfile = SharedPrefManager.getInstance(this)?.user
        val key = getToken(this)

        //menu patrol


        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, PatrolFragment.newInstance())
            .commitNow()

        patrol.setOnClickListener {

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment, PatrolFragment.newInstance())
                .commitNow()
        }
        visistor.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment, VisitorFragment.newInstance())
                .commitNow()
        }
        karyawan.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment, EtowaUserFragment.newInstance())
                .commitNow()
        }









        Log.e(TAG, "Refreshed token: ${getToken(this)}")
        permissionStatus = getSharedPreferences("permissionStatus", Context.MODE_PRIVATE)
        requestPermission()
    }






    private fun requestPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                permissionsRequired[0]
            ) != PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(
                this,
                permissionsRequired[1]
            ) != PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(
                this,
                permissionsRequired[2]
            ) != PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(
                this,
                permissionsRequired[3]
            ) != PackageManager.PERMISSION_GRANTED

        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[0])
                || ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[1])
                || ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[2])
                || ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[3])
            ) {
                //Show Information about why you need the permission
                getAlertDialog()
            } else if (permissionStatus!!.getBoolean(permissionsRequired[0], false)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Need Multiple Permissions")
                builder.setMessage("This app needs permissions.")
                builder.setPositiveButton("Grant") { dialog, which ->
                    dialog.cancel()
                    sentToSettings = true
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivityForResult(intent, REQUEST_PERMISSION_SETTING)
                    Toast.makeText(
                        applicationContext,
                        "Go to Permissions to Grant ",
                        Toast.LENGTH_LONG
                    ).show()
                }
                builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }
                builder.show()
            } else {
                //just request the permission
                ActivityCompat.requestPermissions(
                    this,
                    permissionsRequired,
                    PERMISSION_CALLBACK_CONSTANT
                )
            }


            val editor = permissionStatus!!.edit()
            editor.putBoolean(permissionsRequired[0], true)
            editor.apply()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CALLBACK_CONSTANT) {
            //check if all permissions are granted
            var allgranted = false
            for (i in grantResults.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    allgranted = true
                } else {
                    allgranted = false
                    break
                }
            }

            if (allgranted) {


                showAlertSuccess()

            } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[0])
                || ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[1])
                || ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[2])
                || ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[3])
                || ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[4])
            ) {
                getAlertDialog()
            } else {
                Toast.makeText(applicationContext, "Unable to get Permission", Toast.LENGTH_LONG)
                    .show()


            }
        }
    }

    private fun showAlertSuccess() {
        val inflate = layoutInflater
        val infla_view = inflate.inflate(R.layout.custom_alert_ok, null)
        val cencel = infla_view.findViewById<ImageView>(R.id.btn_close)
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setView(infla_view)
        alertDialog.setCancelable(false)

        val dialog = alertDialog.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        cencel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun getAlertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Need Multiple Permissions")
        builder.setMessage("This app needs permissions.")
        builder.setPositiveButton("Grant") { dialog, which ->
            dialog.cancel()
            ActivityCompat.requestPermissions(
                this,
                permissionsRequired,
                PERMISSION_CALLBACK_CONSTANT
            )
        }
        builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }
        builder.show()
    }

    override fun onPostResume() {
        super.onPostResume()
        if (sentToSettings) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    permissionsRequired[0]
                ) == PackageManager.PERMISSION_GRANTED
            ) {

            }
        }
    }






    override fun onResume() {
        super.onResume()

        if (!SharedPrefManager.getInstance(this)!!.isLoggedIn) {
            startActivity(Intent(baseContext, LoginActivity::class.java))
            finish()
        }
    }

}