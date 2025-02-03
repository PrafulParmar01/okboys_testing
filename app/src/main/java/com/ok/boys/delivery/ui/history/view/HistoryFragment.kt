package com.ok.boys.delivery.ui.history.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.ok.boys.delivery.application.OkBoysApplication
import com.ok.boys.delivery.base.BaseFragment
import com.ok.boys.delivery.base.ViewModelFactory
import com.ok.boys.delivery.base.api.orders.model.OrderListRequest
import com.ok.boys.delivery.base.api.orders.model.OrderResponse
import com.ok.boys.delivery.base.extentions.autoDispose
import com.ok.boys.delivery.base.extentions.getViewModel
import com.ok.boys.delivery.base.extentions.subscribeAndObserveOnMainThread
import com.ok.boys.delivery.base.network.ErrorViewState
import com.ok.boys.delivery.databinding.FragmentHistoryBinding
import com.ok.boys.delivery.ui.history.viewmodel.HistoryViewModel
import com.ok.boys.delivery.ui.history.viewmodel.HistoryViewState
import com.ok.boys.delivery.ui.orders.viewmodel.OrderCategoryViewState
import com.ok.boys.delivery.ui.orders.viewmodel.OrderViewModel
import com.ok.boys.delivery.util.ApiConstant
import com.ok.boys.delivery.util.ApiConstant.IS_HISTORY
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection
import javax.inject.Inject


class HistoryFragment : BaseFragment() {

    private lateinit var binding: FragmentHistoryBinding
    private var isFirstTime = false

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<HistoryViewModel>
    private lateinit var historyViewModel: HistoryViewModel

    @Inject
    lateinit var orderViewModelFactory: ViewModelFactory<OrderViewModel>
    private lateinit var orderViewModel: OrderViewModel

    private lateinit var historyOrderAdapter: HistoryOrdersAdapter
    private var listOfData: MutableList<OrderResponse> = mutableListOf()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        OkBoysApplication.component.inject(this)
        initView()
        viewModelObservers()
    }


    private fun initView() {
        historyViewModel = getViewModel(viewModelFactory)
        orderViewModel = getViewModel(orderViewModelFactory)
        orderViewModel.getOrderCategory()

        binding.swipeRefreshLayout.setOnRefreshListener { direction ->
            if (direction == SwipyRefreshLayoutDirection.TOP) {
                isFirstTime = true
                onCallHistoryOrders()
            }
        }

        binding.recyclerViewHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewHistory.setHasFixedSize(true)
        historyOrderAdapter = HistoryOrdersAdapter(requireContext())
        binding.recyclerViewHistory.adapter = historyOrderAdapter

        historyOrderAdapter.itemClickExpand.subscribe { orderItem ->
            listOfData.forEachIndexed { index, item ->
                if (item.orderNumber == orderItem.orderNumber) {
                    item.isExpanded = !item.isExpanded
                    historyOrderAdapter.notifyItemChanged(index)  // Notify that this item changed
                }
            }
        }.autoDispose(compositeDisposable)

        onCallHistoryOrders()
    }

    private fun onCallHistoryOrders() {
        ApiConstant.IS_TOKEN_PASSED = true
        val request = OrderListRequest()
        request.pageNumber = 1
        request.noOfRecordsPerPage = 100
        historyViewModel.getHistoryList(request)
    }


    private fun viewModelObservers() {
        historyViewModel.historyViewState.subscribeAndObserveOnMainThread {
            when (it) {
                is HistoryViewState.SuccessResponse -> {
                    if (it.successMessage.statusCode == 200) {
                        val responseList = it.successMessage.responseList
                        if (responseList.isNullOrEmpty()) {
                            onHandleError(false,onErrorNoData(IS_HISTORY))
                        } else {
                            binding.recyclerViewHistory.visibility = View.VISIBLE
                            binding.layoutData.parentView.visibility = View.GONE
                            listOfData.clear()
                            listOfData.addAll(it.successMessage.responseList)
                            historyOrderAdapter.updateListInfo(listOfData)
                        }
                    } else {
                        onHandleError(false,onErrorNoData(IS_HISTORY))
                    }
                }

                is HistoryViewState.ErrorMessage -> {
                    onHandleError(true, it.errorMessage)
                }

                is HistoryViewState.LoadingState -> {
                    binding.swipeRefreshLayout.isRefreshing = false
                    if (!isFirstTime) {
                        if (it.isLoading) {
                            binding.progressBar.visibility = View.VISIBLE
                        } else {
                            binding.progressBar.visibility = View.GONE
                        }
                    } else {
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }.autoDispose()

        orderViewModel.orderCategoryViewState.subscribeAndObserveOnMainThread {
            when (it) {
                is OrderCategoryViewState.SuccessResponse -> {
                    if (it.successMessage.statusCode == 200) {
                        OkBoysApplication.categoryList = it.successMessage.response!!.category
                    }
                }
                is OrderCategoryViewState.ErrorMessage -> {
                }

                is OrderCategoryViewState.LoadingState -> {
                }
            }
        }.autoDispose()
    }


    private fun onHandleError(isError: Boolean, it: ErrorViewState) {
        if (isError) {
            binding.layoutData.parentView.visibility = View.VISIBLE
            binding.recyclerViewHistory.visibility = View.GONE
            binding.progressBar.visibility = View.GONE
            binding.layoutData.txtMsgOne.text = it.errorTitle.uppercase()
            binding.layoutData.txtMsgTwo.text = it.errorDescription
        } else {
            binding.layoutData.parentView.visibility = View.VISIBLE
            binding.recyclerViewHistory.visibility = View.GONE
            binding.progressBar.visibility = View.GONE
            binding.layoutData.txtMsgOne.text = it.errorTitle.uppercase()
            binding.layoutData.txtMsgTwo.text = it.errorDescription
        }
    }
}