package com.project.socal_login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.project.socal_login.databinding.FragmentSecondBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment :Fragment() {

    private lateinit var binding: FragmentSecondBinding
    val args: SecondFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

       val user=args.firebaseUser
       binding.textviewSecond.text="Welcome ${user?.displayName}, your email id is ${user?.email}"
        binding.btnSignOut.setOnClickListener{
            FirstFragment.mGoogleSignInClient.signOut()
            FirstFragment.auth.signOut()
            Toast.makeText(requireContext(), "Sign Out s", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }
    }


}