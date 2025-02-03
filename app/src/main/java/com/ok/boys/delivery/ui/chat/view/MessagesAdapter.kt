package com.ok.boys.delivery.ui.chat.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ok.boys.delivery.R
import com.ok.boys.delivery.base.api.chat.model.BaseMessage

class MessagesAdapter(private var mContext: Context) : RecyclerView.Adapter<MessagesAdapter.MessageViewHolder>() {

    private var messages: MutableList<BaseMessage> = mutableListOf()

    companion object {
        private const val SENT = 0
        private const val RECEIVED = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = when (viewType) {
            SENT -> {
                LayoutInflater.from(parent.context).inflate(R.layout.item_sent, parent, false)
            }
            else -> {
                LayoutInflater.from(parent.context).inflate(R.layout.item_received, parent, false)
            }
        }
        return MessageViewHolder(view)
    }

    override fun getItemCount() = messages.size

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isSender == 0) {
            SENT
        } else {
            RECEIVED
        }
    }

    fun updateMessages(newMessages: List<BaseMessage>) {
        val diffCallback = MessagesDiffCallback(messages, newMessages)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        messages.clear()
        messages.addAll(newMessages)
        diffResult.dispatchUpdatesTo(this)
    }

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.message_text)
        val imgMessage: AppCompatImageView = itemView.findViewById(R.id.imgMessage)
        private val layoutImage: RelativeLayout = itemView.findViewById(R.id.layoutImage)

        fun bind(message: BaseMessage) {
            if (message.message?.convType.equals("UPLOADS")) {
                layoutImage.visibility = View.VISIBLE
                messageText.visibility = View.GONE

                Glide
                    .with(mContext)
                    .load(message.message?.attachmentLink)
                    .placeholder(R.drawable.ic_ok_boys_logo)
                    .error(R.drawable.ic_ok_boys_logo)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imgMessage)
            } else {
                layoutImage.visibility = View.GONE
                messageText.visibility = View.VISIBLE
                messageText.text = message.message?.msg
            }

            imgMessage.setOnClickListener {
                if (message.message?.convType.equals("UPLOADS")) {
                    mContext.startActivity(
                        PreviewActivity.getIntent(
                            mContext,
                            message.message?.attachmentLink!!
                        )
                    )
                }
            }
        }
    }

    class MessagesDiffCallback(
        private val oldList: List<BaseMessage>,
        private val newList: List<BaseMessage>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldMessage = oldList[oldItemPosition]
            val newMessage = newList[newItemPosition]
            return oldMessage.message?.msg == newMessage.message?.msg &&
                    oldMessage.message?.id == newMessage.message?.id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldMessage = oldList[oldItemPosition]
            val newMessage = newList[newItemPosition]
            return oldMessage == newMessage
        }
    }

    override fun onViewRecycled(holder: MessageViewHolder) {
        Glide.with(mContext).clear(holder.imgMessage)
        super.onViewRecycled(holder)
    }
}
