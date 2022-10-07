package com.project.socal_login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.project.socal_login.databinding.FragmentGoogleSignedBinding


class GoogleSignedFragment :Fragment() {

    private lateinit var binding: FragmentGoogleSignedBinding
    private val args: GoogleSignedFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentGoogleSignedBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

       val user=args.googleAccount
       binding.textviewSecond.text="Welcome ${user?.displayName}, your email id is ${user?.email}"
        binding.btnSignOut.setOnClickListener{
            if (FirstFragment.mGoogleSignInClient!=null){
                FirstFragment.mGoogleSignInClient.signOut()
                FirstFragment.auth.signOut()
                findNavController().navigate(R.id.action_googleSignedFragment_to_FirstFragment)
            }

        }
    }



}