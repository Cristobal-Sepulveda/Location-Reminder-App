package com.udacity.project4.authentication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.R
import com.udacity.project4.databinding.ActivityAuthenticationBinding
import com.udacity.project4.RemindersActivity


/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */
class AuthenticationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthenticationBinding

    companion object {
        const val TAG = "LoginFragment"
        const val SIGN_IN_RESULT_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_authentication)

        binding.loginButton.setOnClickListener {
                launchSignInFlow()
        }

    }
    // Give users the option to sign in / register with their email or Google account.
    // If users choose to register with their email,
    // they will need to create a password as well.
    private fun launchSignInFlow() {
        /** What im doing here is telling the firebaseUI how i want to let the user log-in */
        val providers = arrayListOf(AuthUI.IdpConfig.EmailBuilder().build(),
                AuthUI.IdpConfig.GoogleBuilder().build())

        /** Create and launch sign-in intent.
         *  We listen to the response of this activity with the
         *  SIGN_IN_REQUEST_CODE*/
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().
        setAvailableProviders(providers).
        setTheme(R.style.LoginTheme).
        setLogo(R.drawable.map)
                .build(), SIGN_IN_RESULT_CODE)
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        /** Here im specifically listening for the sign-in request code to come back,
         * if its still the same, process the login, else, its because the login was unsuccessful*/
        if (requestCode == SIGN_IN_RESULT_CODE){
            //we start by getting the response from the resulting intent
            val response = IdpResponse.fromResultIntent(data)
            //then we check the resultCode to see what the result of the login was
            if(resultCode == Activity.RESULT_OK){
                //User successfully signed in
                Log.i(TAG, "Successfully signed in user ${FirebaseAuth.getInstance().currentUser?.displayName}")
                val firebaseAuth = FirebaseAuth.getInstance()
                if(firebaseAuth.currentUser != null) {
                    val intent = Intent(this, RemindersActivity::class.java)
                    finish()
                    startActivity(intent)
                }
            }else{
                Log.i(TAG, "Sign in was unsuccessful ${response?.error?.errorCode}")
            }
        }
    }

//          TODO: If the user was authenticated, send him to RemindersActivity

//          TODO: a bonus is to customize the sign in flow to look nice using :
        //https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md#custom-layout

}

