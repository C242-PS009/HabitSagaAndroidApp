package com.c242_ps009.habitsaga.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil.isValidUrl
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.c242_ps009.habitsaga.R
import com.c242_ps009.habitsaga.databinding.FragmentProfileBinding
import com.c242_ps009.habitsaga.ui.gamification.UserViewModel
import com.c242_ps009.habitsaga.ui.inventory.InventoryActivity
import com.c242_ps009.habitsaga.ui.profile.SettingsActivity
import com.c242_ps009.habitsaga.ui.shop.ShopActivity
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
            cvCustom.setOnClickListener{
                val intent = Intent(context, InventoryActivity::class.java)
                startActivity(intent)
            }

            cvSettings.setOnClickListener {
                val intent = Intent(context, SettingsActivity::class.java)
                startActivity(intent)
            }

            cvShop.setOnClickListener {
                val intent = Intent(context, ShopActivity::class.java)
                startActivity(intent)
            }
        }

        viewModel.userData.observe(viewLifecycleOwner) { user ->
            user?.let {
                Log.d("ProfileFragment", "Layer 1 URL: ${it.equippedItemLayer1}")
                Log.d("ProfileFragment", "Layer 2 URL: ${it.equippedItemLayer2}")

                // Append "/xd.png" only if URL is valid
                val layer1Url = it.equippedItemLayer1?.let { url ->
                    Log.d("ProfileFragment", "URL before append: $url")
                    if (isValidUrl(url)) {
                        "${url.trimEnd('/')}/xd.png"
                    } else {
                        Log.e("ProfileFragment", "Invalid URL for layer1: $url")
                        null
                    }
                }

                val layer2Url = it.equippedItemLayer2?.let { url ->
                    Log.d("ProfileFragment", "URL before append: $url")
                    if (isValidUrl(url)) {
                        "${url.trimEnd('/')}/xd.png"
                    } else {
                        Log.e("ProfileFragment", "Invalid URL for layer2: $url")
                        null
                    }
                }

                Log.d("ProfileFragment", "Layer 1 final URL: $layer1Url")
                Log.d("ProfileFragment", "Layer 2 final URL: $layer2Url")

                binding.mascot.apply {
                    layer1 = layer1Url
                    layer2 = layer2Url
                }
            }
        }

        viewModel.userData.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.tvUsername.text = it.name
                binding.tvLevel.text = getString(R.string.level, it.level.toString())
                binding.tvCoin.text = String.format(it.coin.toString())
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