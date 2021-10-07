package com.example.mypaint.menusheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.paint.MainActivity
import com.example.paint.R
import com.example.paint.customview.PaintView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.erase_bottom_sheet.*

class EraserBottomSheet(private val paintView: PaintView) : BottomSheetDialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.erase_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        i_stroke_width.setOnClickListener {
            paintView.setEraser(10)
            MainActivity.selectedColor = "#ffffff"
            dismiss()
        }

        ii_stroke_width.setOnClickListener {
            paintView.setEraser(15)
            MainActivity.selectedColor = "#ffffff"
            dismiss()
        }

        iii_stroke_width.setOnClickListener {
            paintView.setEraser(35)
            MainActivity.selectedColor = "#ffffff"
            dismiss()
        }

        iv_stroke_width.setOnClickListener {
            paintView.setEraser(50)
            MainActivity.selectedColor = "#ffffff"
            dismiss()
        }
    }
}