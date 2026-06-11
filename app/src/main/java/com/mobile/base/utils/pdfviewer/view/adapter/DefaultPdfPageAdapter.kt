package com.mobile.base.utils.pdfviewer.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import com.mobile.base.databinding.PdfPageBinding
import com.mobile.base.utils.pdfviewer.utils.PdfPageQuality
import java.io.File

class DefaultPdfPageAdapter(
    file: File,
    quality: PdfPageQuality,
    dispatcher: CoroutineDispatcher,
    private val scope: CoroutineScope,
) : PdfPagesAdapter<DefaultPdfPageViewHolder>(file, quality, dispatcher) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DefaultPdfPageViewHolder {
        val view = PdfPageBinding.inflate(LayoutInflater.from(parent.context), parent, false).root
        return DefaultPdfPageViewHolder(view, scope, ::renderPage)
    }

    override fun onBindViewHolder(holder: DefaultPdfPageViewHolder, position: Int) {
        holder.bind(position)
    }
}