package com.rakasiwi.barandqrcodescanner.ui.kodeBayar

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rakasiwi.barandqrcodescanner.R
import kotlinx.android.synthetic.main.kode_bayar_fragment.*

class KodeBayar : Fragment() {

    companion object {
        fun newInstance() = KodeBayar()
    }

    private lateinit var viewModel: KodeBayarViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.kode_bayar_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(KodeBayarViewModel::class.java)
        image.setImageBitmap(viewModel.generateBarCode())
    }



}