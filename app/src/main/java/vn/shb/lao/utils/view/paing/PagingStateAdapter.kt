package vn.shb.lao.utils.view.paing

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView

import vn.shb.lao.R
import vn.shb.lao.utils.extensions.inflate

class PagingStateAdapter(private val onRetry: () -> Unit) :
    LoadStateAdapter<PagingStateAdapter.FooterViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState) = FooterViewHolder(
        parent.inflate(R.layout.item_load_state)
    )

    override fun onBindViewHolder(holder: FooterViewHolder, loadState: LoadState) =
        holder.bind(loadState)

    inner class FooterViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val buttonRetry: View = itemView.findViewById(R.id.buttonRetry)
        private val textViewError: TextView = itemView.findViewById(R.id.textViewError)
        private val progressBar: View = itemView.findViewById(R.id.progressBar)

        init {
            buttonRetry.setOnClickListener {
                onRetry()
            }
        }

        fun bind(loadState: LoadState) {
            if (loadState is LoadState.Loading)
                showLoading()
            else if (loadState is LoadState.Error) {
                showError(loadState.error.localizedMessage ?: "")
            }
        }

        private fun showLoading() {
            buttonRetry.visibility = View.INVISIBLE
            textViewError.visibility = View.INVISIBLE
            progressBar.visibility = View.VISIBLE
        }

        private fun showError(error: String) {
            buttonRetry.visibility = View.VISIBLE
            textViewError.visibility = View.VISIBLE
            textViewError.text = error
            progressBar.visibility = View.INVISIBLE
        }
    }
}