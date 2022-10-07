package com.project.socal_login

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.project.socal_login.databinding.FragmentLoginBinding
import java.util.*


class FirstFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private var userEmail = ""
    private var userPassword = ""
    lateinit var callbackManager: CallbackManager

    companion object {
        var globalUser: FirebaseUser? = null
        lateinit var auth: FirebaseAuth
        lateinit var mGoogleSignInClient: GoogleSignInClient

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        FacebookSdk.sdkInitialize(requireActivity().applicationContext);
        val facebookLoginButton = LoginButton(requireContext())
        auth = FirebaseAuth.getInstance()

        callbackManager = CallbackManager.Factory.create()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().requestIdToken(getString(R.string.default_web_client_id))
            .requestProfile()
            .build()


        facebookLoginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {

            override fun onSuccess(result: LoginResult) {
                Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_FirstFragment_to_facebookSignedFragment2)

            }

            override fun onCancel() {
                Toast.makeText(context, "cancel", Toast.LENGTH_SHORT).show()

            }

            override fun onError(error: FacebookException) {
                Toast.makeText(context, "error ${error.cause}", Toast.LENGTH_SHORT).show()

            }
        })

        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
        setUpClickListener()

    }

    private fun setUpClickListener() {
        binding.btnLogin.setOnClickListener {
            signInUser()
        }
        binding.btnFirebaseLogin.setOnClickListener {
            extractCredentials()
            if (isValidEmail(userEmail)) {
                auth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(requireContext(), getString(R.string.user_created), Toast.LENGTH_SHORT).show()
                        navigateToNextOnLogIn(auth.currentUser)
                    } else {
                        Toast.makeText(context, getString(R.string.authentication_failed), Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), getString(R.string.not_valid_format), Toast.LENGTH_SHORT).show()
            }

        }
        binding.btnGoogle.setOnClickListener {

            googleSignIn(mGoogleSignInClient)


        }
        binding.btnFacebook.setOnClickListener {
            if (!isFaceBookLogin()) {
                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"))
            } else {
                Toast.makeText(context, "User Already login", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_FirstFragment_to_facebookSignedFragment2)
            }

        }
    }

    private fun googleSignIn(mGoogleSignInClient: GoogleSignInClient) {

        if (!isUserSignedIn()) {
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, 100)
        } else {
            Toast.makeText(requireContext(), "User already login", Toast.LENGTH_SHORT).show()
        }

    }

    private fun isUserSignedIn(): Boolean {

        val account = GoogleSignIn.getLastSignedInAccount(requireContext())
        return account != null

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == 100) {

            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)

            } catch (e: ApiException) {
                Toast.makeText(requireContext(), "Google sign in failed", Toast.LENGTH_SHORT).show()
                Log.w("TAG", "Google sign in failed", e)
            }

        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }
        super.onActivityResult(requestCode, resultCode, data)

    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {

        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)

        auth.signInWithCredential(credential).addOnSuccessListener {
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
            val action=FirstFragmentDirections.actionFirstFragmentToSecondFragment(auth.currentUser)
            findNavController().navigate(action)

        }.addOnFailureListener{
            Toast.makeText(requireContext(), "${it.cause}", Toast.LENGTH_SHORT).show()
        }
    }



    private fun signInUser() {
        extractCredentials()
        if (isValidEmail(userEmail) && isValidPassWord(userPassword)) {
            auth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener {
                if (it.isSuccessful) {
                    navigateToNextOnLogIn(auth.currentUser)
                    Toast.makeText(requireContext(), getString(R.string.success), Toast.LENGTH_SHORT).show()
                    globalUser = auth.currentUser

                } else {
                    Toast.makeText(requireContext(), getString(R.string.authentication_failed), Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(requireContext(), getString(R.string.invalid_format_message), Toast.LENGTH_SHORT).show()
        }


    }

    private fun isValidPassWord(userPassword: String): Boolean {
        return userPassword.isNotEmpty() && userPassword.length > 5
    }

    private fun extractCredentials() {
        userEmail = binding.etEmail.text.toString().trim()
        userPassword = binding.etUserPassword.text.toString().trim()
    }

    override fun onStart() {
        super.onStart()
        var token = AccessToken.getCurrentAccessToken()
        if (auth != null) {
            navigateToNextOnLogIn(auth.currentUser)

            if (globalUser == null) {
                globalUser = auth.currentUser
//                Toast.makeText(requireContext(), "welcome back ${globalUser?.email}", Toast.LENGTH_SHORT).show()
            }

        } else if (isUserSignedIn()) {
            var account: GoogleSignInAccount = GoogleSignIn.getLastSignedInAccount(requireContext())!!
            var action = FirstFragmentDirections.actionFirstFragmentToGoogleSignedFragment(account)
            findNavController().navigate(action)

        } else if (token != null && !token.isExpired) {
            findNavController().navigate(R.id.action_FirstFragment_to_facebookSignedFragment2)
        }

    }

    private fun isValidEmail(target: CharSequence?): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    private fun navigateToNextOnLogIn(currentUser: FirebaseUser?) {
        if (currentUser == null) {
            return
        } else {
            val action = FirstFragmentDirections.actionFirstFragmentToSecondFragment(currentUser)
            findNavController().navigate(action)
        }
    }

    private fun isFaceBookLogin(): Boolean {
        var accessToken = AccessToken.getCurrentAccessToken()
        return (accessToken != null && !accessToken!!.isExpired)
    }


}