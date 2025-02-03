import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ok.boys.delivery.R
import com.ok.boys.delivery.base.api.orders.model.OrderResponse
import com.ok.boys.delivery.databinding.RowItemOrdersBroadcastListBinding
import com.ok.boys.delivery.ui.orders.view.RequestAcceptReject
import com.ok.boys.delivery.ui.orders.view.broadcasted.BroadcastPickupAdapter
import com.ok.boys.delivery.util.ApiConstant
import com.ok.boys.delivery.util.UtilsMethod
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class BroadcastOrdersAdapter(var mContext: Context) : RecyclerView.Adapter<BroadcastOrdersAdapter.OrderViewHolder>() {

    private var listOfData: List<OrderResponse> = listOf()

    private lateinit var itemViewBinding: RowItemOrdersBroadcastListBinding
    private lateinit var broadcastPickupAdapter: BroadcastPickupAdapter

    private val itemClickSubjectAccept: PublishSubject<RequestAcceptReject> = PublishSubject.create()
    var itemClickAccept: Observable<RequestAcceptReject> = itemClickSubjectAccept.hide()

    private var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): OrderViewHolder {
        val binding = RowItemOrdersBroadcastListBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(listOfData[position], position)
    }

    override fun getItemCount(): Int = listOfData.size

    fun updateListInfo(newList: List<OrderResponse>) {
        val diffCallback = OrderDiffCallback(listOfData, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        listOfData = newList
        diffResult.dispatchUpdatesTo(this)
    }

    inner class OrderViewHolder(private val binding: RowItemOrdersBroadcastListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: OrderResponse, position: Int) {
            binding.rvPickUp.layoutManager = LinearLayoutManager(mContext)
            binding.rvPickUp.setHasFixedSize(true)
            broadcastPickupAdapter = BroadcastPickupAdapter(mContext)
            broadcastPickupAdapter.updateListInfo(item.jobAddress)
            binding.rvPickUp.adapter = broadcastPickupAdapter

            binding.txtCreated.text = UtilsMethod.timeStampFormatter(item.createdTs)
            binding.layoutDeliver.txtUserName.text = item.userAddress?.area
            binding.layoutDeliver.txtDeliveryAddess.text = item.userAddress?.address
            binding.txtDistanceKM.text = item.totalDistance.toString()

            val rupees = mContext.getString(R.string.symbol_rupees)
            binding.txtEarnAmount.text = item.deliveryAmount.let { rupees + it.toString() } ?: "-"

            binding.btnAccept.setOnClickListener {
                itemClickSubjectAccept.onNext(
                    RequestAcceptReject(
                        item.id,
                        ApiConstant.ORDER_ACCEPT
                    )
                )
            }

            binding.btnReject.setOnClickListener {
                itemClickSubjectAccept.onNext(
                    RequestAcceptReject(
                        item.id,
                        ApiConstant.ORDER_REJECT
                    )
                )
            }

            // Handle expanded state
            if (item.isExpanded && position == selectedPosition) {
                binding.clPickUpDetails.visibility = View.VISIBLE
                binding.imgExpandView.setImageResource(R.drawable.ic_up_arrow)
            } else {
                binding.clPickUpDetails.visibility = View.GONE
                binding.imgExpandView.setImageResource(R.drawable.ic_down_arrow)
            }

            binding.imgExpandView.setOnClickListener {
                item.isExpanded = !item.isExpanded
                selectedPosition = position
                notifyItemChanged(position)
            }

            binding.constraintExpand.setOnClickListener {
                item.isExpanded = !item.isExpanded
                selectedPosition = position
                notifyItemChanged(position)
            }
        }
    }

    // DiffUtil Callback for OrderResponse
    class OrderDiffCallback(
        private val oldList: List<OrderResponse>,
        private val newList: List<OrderResponse>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}
