package com.project.socal_login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.GraphRequest.GraphJSONObjectCallback
import com.facebook.GraphResponse
import com.project.socal_login.databinding.FragmentFacebookSignedBinding
import org.json.JSONException
import org.json.JSONObject


class FacebookSignedFragment : Fragment() {

    private lateinit var binding: FragmentFacebookSignedBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentFacebookSignedBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSignOut.setOnClickListener{
            findNavController().popBackStack()
        }

      var accessToken = AccessToken.getCurrentAccessToken()
        val request = GraphRequest.newMeRequest(
            accessToken
        ) { obj, response ->
            try {
                var fullname = obj?.getString("name")
//                var dob = obj?.getString("dob")
//                var gender = obj?.getString("gender")
                binding.username.text = "Name-$fullname  "
            }catch (error:JSONException){
                error.printStackTrace()
            }

        }
        val parameters = Bundle()
        parameters.putString("fields", "id,name,link")
        request.parameters = parameters
        request.executeAsync()

    }



}