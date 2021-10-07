package com.example.paint

import android.Manifest
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.mypaint.menusheet.BlurBottomSheet
import com.example.mypaint.menusheet.BrushBottomSheet
import com.example.mypaint.menusheet.EraserBottomSheet
import com.example.paint.customview.PaintView
import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog
import com.github.dhaval2404.colorpicker.listener.ColorListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_main.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    private lateinit var paintView: PaintView

    companion object {
        var selectedColor = "#000000"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        paintView = findViewById(R.id.paintView)
        val metrics = Resources.getSystem().displayMetrics
        paintView.init(metrics)
        paintView.normal()

        color_menu.setOnClickListener {
            MaterialColorPickerDialog
                .Builder(this)
                .setColorRes(resources.getStringArray(R.array.themeColorHex).map {
                    Color.parseColor(
                        it
                    )
                }.toList())
                .setDefaultColor(selectedColor)
                .setColorListener(object : ColorListener {
                    override fun onColorSelected(color: Int, colorHex: String) {
                        Log.d("TAG", "color -> : $color")
                        Log.d("TAG", "color -> : $colorHex")
                        selectedColor = colorHex
                        paintView.setColor(colorHex)
                    }
                })
                .showBottomSheet(supportFragmentManager)
        }

        brush_menu.setOnClickListener {
            val brushBottomSheet = BrushBottomSheet(paintView)
            brushBottomSheet.show(supportFragmentManager, brushBottomSheet.tag)
        }

        eraser_menu.setOnClickListener {
            val eraserBottomSheet = EraserBottomSheet(paintView)
            eraserBottomSheet.show(supportFragmentManager, eraserBottomSheet.tag)
        }

        blur_menu.setOnClickListener {
            val blurBottomSheet = BlurBottomSheet(paintView)
            blurBottomSheet.show(supportFragmentManager, blurBottomSheet.tag)
        }

        clear_menu.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("New Sheet")
                .setMessage("Start new drawing?")
                .setPositiveButton("Yes") { _, _ ->
                    paintView.clear()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.cancel()
                }.show()
        }

        undo_menu.setOnClickListener {
            paintView.undo()
        }

        download_menu.setOnClickListener {
            storagepermission()
        }
    }


    private fun storagepermission() {
        if (haspermission()) {
//            grant permission
            paintView.download(this)

        } else {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.external_storage),
                12,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }

    private fun haspermission(): Boolean {
        return EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
//        Log.d(, "onPermissionsDenied:" + requestCode + ":" + perms.size)

        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
//        Log.d(TAG, "onPermissionsGranted:" + requestCode + ":" + perms.size)
        paintView.download(this)
    }

}