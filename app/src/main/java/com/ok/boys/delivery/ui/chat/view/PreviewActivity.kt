package com.ok.boys.delivery.ui.chat.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ok.boys.delivery.R
import com.ok.boys.delivery.application.OkBoysApplication
import com.ok.boys.delivery.base.BaseActivity
import com.ok.boys.delivery.databinding.ActivityPreviewBinding

class PreviewActivity : BaseActivity() {

    private lateinit var binding: ActivityPreviewBinding
    private var imageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        OkBoysApplication.component.inject(this)
        initViews()
    }

    companion object {
        fun getIntent(context: Context, image: String): Intent {
            return Intent(context, PreviewActivity::class.java).apply {
                putExtra("image", image)
            }
        }
    }

    private fun initViews() {
        binding.chatToolBar.txtTitle.text = "Preview"

        binding.chatToolBar.btnBack.setOnClickListener {
            onBackPressed()
        }

        imageUrl = intent.getStringExtra("image")
        imageUrl?.let { url ->
            Glide.with(this)
                .load(url)
                .placeholder(R.drawable.ic_ok_boys_logo)
                .error(R.drawable.ic_ok_boys_logo)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.imgMessage)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.imgMessage.let {
            Glide.with(applicationContext).clear(it)
        }
    }
}
