package com.ok.boys.delivery.ui.orders.view.tags

import android.content.Context
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.ok.boys.delivery.R
import com.ok.boys.delivery.base.api.orders.model.CategoryItem
import com.ok.boys.delivery.databinding.RowItemChipsTagBinding


class TagsItemView(context: Context) : ConstraintLayout(context) {

    private lateinit var itemViewBinding: RowItemChipsTagBinding
    init {
        inflateUi()
    }

    private fun inflateUi() {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        val view = View.inflate(context, R.layout.row_item_chips_tag, this)
        itemViewBinding = RowItemChipsTagBinding.bind(view)
    }

    fun bind( position:Int,item: CategoryItem) {
        itemViewBinding.txtTagsName.text = item.category
    }
}