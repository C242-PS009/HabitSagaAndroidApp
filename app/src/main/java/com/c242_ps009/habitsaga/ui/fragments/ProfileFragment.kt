package com.c242_ps009.habitsaga.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.c242_ps009.habitsaga.R
import com.c242_ps009.habitsaga.databinding.FragmentProfileBinding
import com.c242_ps009.habitsaga.ui.gamification.UserViewModel
import com.c242_ps009.habitsaga.ui.settings.SettingsActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val auth: FirebaseAuth by lazy { Firebase.auth }
    private val viewModel: UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.apply {
            cvSettings.setOnClickListener {
                val intent = Intent(context, SettingsActivity::class.java)
                startActivity(intent)
            }
        }

        binding.mascot.apply {
            layer1 =
                "https://raw.githubusercontent.com/C242-PS009/assets/refs/heads/master/characters/orca/xd.svg"
            layer2 =
                "https://raw.githubusercontent.com/C242-PS009/assets/refs/heads/master/equippables/glasses/xd.svg"
        }

        viewModel.userData.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.tvUsername.text = it.name
//                binding.tvLevel.text = "Level ${it.level}"
//                binding.tvCoin.text = it.coin.toString()
//                binding.tvExpProgress.text = "${it.expProgress}/100"
                binding.tvLevel.text = getString(R.string.level, it.level.toString())
                binding.tvCoin.text = getString(R.string.coin, it.coin.toString())
                binding.tvExpProgress.text = getString(R.string.exp_progress, it.expProgress.toString())
                binding.pbLevel.progress = it.expProgress
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }

        val userId = auth.currentUser?.uid
        if (userId != null) {
            viewModel.fetchUserInfo(userId)
        } else {
//            binding.tvUsername.text = "User not logged in"
//            binding.tvLevel.text = "Level: N/A"
            binding.tvUsername.text = getString(R.string.unauthorized)
            binding.tvLevel.text = getString(R.string.unauthorized)
            binding.pbLevel.progress = 0
            binding.tvCoin.text = "0"
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}