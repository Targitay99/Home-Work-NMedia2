package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.databinding.ItemLoadingBinding

class PostLoadingStateAdapter(
    private val onInteractionListener: OnInteractionListener,
) : LoadStateAdapter<PostLoadingStateAdapter.PostLoadingViewHolder>() {

    interface OnInteractionListener {
        fun onRetry() {}
    }



    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): PostLoadingViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return PostLoadingViewHolder(
            ItemLoadingBinding.inflate(layoutInflater, parent, false),
            onInteractionListener
        )
    }

    override fun onBindViewHolder(holder: PostLoadingViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }



    class PostLoadingViewHolder(
        private val itemLoadingBinding: ItemLoadingBinding,
        private val onInteractionListener: OnInteractionListener,
        ) : RecyclerView.ViewHolder(itemLoadingBinding.root) {

        fun bind(loadState: LoadState) {
            itemLoadingBinding.apply {
                progress.isVisible = loadState is LoadState.Loading
                retryButton.isVisible = loadState is LoadState.Error
                retryButton.setOnClickListener {
                    onInteractionListener.onRetry()
                }
            }
        }
    }
}