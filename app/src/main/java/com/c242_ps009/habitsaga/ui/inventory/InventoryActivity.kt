package com.c242_ps009.habitsaga.ui.inventory

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.c242_ps009.habitsaga.R
import com.c242_ps009.habitsaga.databinding.ActivityInventoryBinding
import com.c242_ps009.habitsaga.databinding.ActivityShopBinding
import com.c242_ps009.habitsaga.ui.gamification.UserViewModel
import com.c242_ps009.habitsaga.utils.Connection
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class InventoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInventoryBinding
    private val userViewModel: UserViewModel by viewModels()
    private lateinit var inventoryAdapter: InventoryAdapter
    private var clickedItem: Inventory? = null
    private val auth: FirebaseAuth by lazy { Firebase.auth }
    private val inventoryViewModel: InventoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInventoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initAnim()
        setupMainView("character")

        binding.apply {
            btnRefresh.setOnClickListener {
                updateResultState(0)
                setupMainView("character")
            }

            filterChars.setOnClickListener {
                updateResultState(0)
                setupMainView("character")
            }

            filterAccessories.setOnClickListener {
                updateResultState(0)
                setupMainView("equip")
            }

            btnEquip.setOnClickListener {
                clickedItem?.let { item ->
                    userViewModel.equipItem(item)
                    Toast.makeText(this@InventoryActivity, "Equipped ${item.name}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun initAnim() {
        val fadeInCoins = ObjectAnimator.ofFloat(binding.coinsTv, "alpha", 0f, 1f).setDuration(800)
        val slideFromBottomCoins = ObjectAnimator.ofFloat(binding.coinsTv, "translationY", 100f, 0f).apply {
            duration = 800
            interpolator = DecelerateInterpolator()
        }
        val fadeInContainer = ObjectAnimator.ofFloat(binding.container, "alpha", 0f, 1f).setDuration(800)
        val slideFromBottomContainer = ObjectAnimator.ofFloat(binding.container, "translationY", 100f, 0f).apply {
            duration = 800
            interpolator = DecelerateInterpolator()
        }
        val fadeInContainer3DSupport = ObjectAnimator.ofFloat(binding.container3dSupport, "alpha", 0f, 1f).setDuration(800)
        val slideFromBottomContainer3DSupport = ObjectAnimator.ofFloat(binding.container3dSupport, "translationY", 100f, 0f).apply {
            duration = 800
            interpolator = DecelerateInterpolator()
        }
        val together = AnimatorSet().apply { playTogether(
            fadeInCoins, slideFromBottomCoins, fadeInContainer, slideFromBottomContainer, fadeInContainer3DSupport, slideFromBottomContainer3DSupport
        ) }
        AnimatorSet().apply {
            play(together)
            start()
        }
    }

    private fun setupMainView(filter: String) {
        if (Connection.checkConnection(this)) {
            setupRecyclerView()
            setupObservers(filter)
            val fadeInRV = ObjectAnimator.ofFloat(binding.itemsRv, "alpha", 0f, 1f).setDuration(500)
            val slideFromBottomRV = ObjectAnimator.ofFloat(binding.itemsRv, "translationY", 100f, 0f).apply {
                duration = 500
                interpolator = DecelerateInterpolator()
            }
            val togetherRV = AnimatorSet().apply { playTogether(fadeInRV, slideFromBottomRV) }
            AnimatorSet().apply {
                play(togetherRV)
                start()
            }
        } else {
            Toast.makeText(this, "No connection", Toast.LENGTH_SHORT).show()
            updateResultState(-1)
        }
    }

    private fun setupRecyclerView() {
        binding.itemsRv.layoutManager = GridLayoutManager(this, 4)
        inventoryAdapter = InventoryAdapter(
            onClick = { item ->
                clickedItem = item
                binding.itemTv.text = item.name
                binding.btnEquip.visibility = View.VISIBLE
                val fadeInDetail = ObjectAnimator.ofFloat(binding.itemDetail, "alpha", 0f, 1f).setDuration(500)
                val slideFromRightDetail = ObjectAnimator.ofFloat(binding.itemDetail, "translationX", 100f, 0f).apply {
                    duration = 500
                    interpolator = DecelerateInterpolator()
                }
                val togetherTV = AnimatorSet().apply { playTogether(fadeInDetail, slideFromRightDetail) }
                AnimatorSet().apply {
                    play(togetherTV)
                    start()
                }
            }
        )
        binding.itemsRv.adapter = inventoryAdapter
    }

    private fun setupObservers(filter: String) {
        inventoryViewModel.inventoryItems.observe(this) { inventoryItems ->
            val filteredList = inventoryItems.filter { it.itemCategory == filter }
            inventoryAdapter.submitList(filteredList)
        }
        inventoryViewModel.resultState.observe(this) { resultState -> updateResultState(resultState)}

        userViewModel.userData.observe(this) { user -> user?.let { binding.coinsTv.text = String.format(it.coin.toString()) } }
        userViewModel.errorMessage.observe(this) { errorMessage ->
            errorMessage?.let { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
        }

        val userId = auth.currentUser?.uid
        if (userId != null)  userViewModel.fetchUserInfo(userId)
        else binding.coinsTv.text = "0"
    }

    private fun updateResultState(resultState: Int) {
        when (resultState) {
            -1 -> {
                binding.btnRefresh.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
                binding.itemTv.visibility = View.GONE
                binding.itemsRv.visibility = View.GONE
            }
            0 -> {
                binding.btnRefresh.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
                binding.itemTv.visibility = View.GONE
                binding.itemsRv.visibility = View.GONE
            }
            1 -> {
                binding.btnRefresh.visibility = View.GONE
                binding.progressBar.visibility = View.GONE
                binding.itemTv.visibility = View.VISIBLE
                binding.itemsRv.visibility = View.VISIBLE
            }
        }
    }
}