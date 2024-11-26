package com.c242_ps009.habitsaga.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.c242_ps009.habitsaga.databinding.FragmentProfile2Binding
import com.c242_ps009.habitsaga.databinding.FragmentProfileBinding
import com.c242_ps009.habitsaga.ui.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfile2Binding? = null
    private val binding get() = _binding!!
    private val auth: FirebaseAuth by lazy { Firebase.auth }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfile2Binding.inflate(inflater, container, false)

        binding.mascot.init(
            "https://raw.githubusercontent.com/C242-PS009/assets/refs/heads/master/characters/orca/xd.svg",
            "https://raw.githubusercontent.com/C242-PS009/assets/refs/heads/master/equippables/glasses/xd.svg",
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.btnLogout.setOnClickListener {
//            auth.signOut()
//
//            // Redirect to the login screen and clear the back stack
//            val intent = Intent(requireContext(), LoginActivity::class.java).apply {
//                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//            }
//            startActivity(intent)
//            requireActivity().finish()
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}