package com.example.kidsdrawingapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import javax.net.ssl.ManagerFactoryParameters


class MainActivity : AppCompatActivity() {

    private var drawingview:DrawingView?=null
    private var mImageButtonCurrentPaint:ImageButton?=null

    val opengallerylauncher:ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result->
            if(result.resultCode== RESULT_OK&&result.data!=null){
                val imgbackground:ImageView=findViewById(R.id.id_background)
                imgbackground.setImageURI(result.data?.data)
            }
        }

    val requestlauncher:ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
            permission->
            permission.entries.forEach{
                val name=it.key
                val isgranted=it.value
                if(isgranted){
                    Toast.makeText(this,
                        "Permission  Granted,Now You Can Read the Storage Values", Toast.LENGTH_SHORT).show()
                    val pickIntent= Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    opengallerylauncher.launch(pickIntent)

                }else{
                    Toast.makeText(this, "Permission is Denied", Toast.LENGTH_SHORT).show()
                }
            }

        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawingview=findViewById(R.id.drawing_view)
        drawingview?.setSizeforBrush(20.toFloat())

        val linearlayoutpaintcolors=findViewById<LinearLayout>(R.id.ll_paint_colors)
        mImageButtonCurrentPaint=linearlayoutpaintcolors[1]as ImageButton
        mImageButtonCurrentPaint!!.setImageDrawable(
            ContextCompat.getDrawable(this,R.drawable.pallet_pressed)
        )
        val ib_undo:ImageButton=findViewById(R.id.ib_undo)
        ib_undo.setOnClickListener {
            drawingview?.onclickundo()
        }

        val ib_brush: ImageButton =findViewById(R.id.ib_brush)
        ib_brush.setOnClickListener {
            showBrushSizeChooserDialog()

        }
        val permission:ImageButton=findViewById(R.id.ib_gallery)
        permission.setOnClickListener {
                permissionmethod()
        }
    }


    private fun showBrushSizeChooserDialog(){
        val brushDialog=Dialog(this)
        brushDialog.setContentView(R.layout.dialog_brush_size)
        brushDialog.setTitle("Brush size: ")
        val smallBtn:ImageButton=brushDialog.findViewById(R.id.ib_small_brush)
        smallBtn.setOnClickListener{
            drawingview?.setSizeforBrush(10.toFloat())
            brushDialog.dismiss()
        }
        val mdmBtn:ImageButton=brushDialog.findViewById(R.id.ib_medium_brush_brush)
        mdmBtn.setOnClickListener{
            drawingview?.setSizeforBrush(20.toFloat())
            brushDialog.dismiss()
        }
        val largeBtn:ImageButton=brushDialog.findViewById(R.id.ib_large_brush)
        largeBtn.setOnClickListener{
            drawingview?.setSizeforBrush(30.toFloat())
            brushDialog.dismiss()
        }

        brushDialog.show()



    }
    fun paintclicked(view: View){
        if(view!==mImageButtonCurrentPaint){
            val imgbtn=view as ImageButton
            var colortag=imgbtn.tag.toString()
                drawingview?.setcolor(colortag)
            imgbtn.setImageDrawable(
                ContextCompat.getDrawable(this,R.drawable.pallet_pressed)
            )
            mImageButtonCurrentPaint!!.setImageDrawable(
                ContextCompat.getDrawable(this,R.drawable.pallet_normal)
            )

            mImageButtonCurrentPaint=view
        }

    }
    private fun permissionmethod(){
        if(ActivityCompat.shouldShowRequestPermissionRationale( this,Manifest.permission.READ_EXTERNAL_STORAGE)){
            showRationaleDialog("Kids Drawing App","Kids Drawing App Need To Access Your External Storage")
        }else{
            requestlauncher.launch(
                arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            )
        }

    }
    private fun showRationaleDialog(
        title:String,
        message:String,
    ){
        val builder: AlertDialog.Builder= AlertDialog.Builder(this)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton("Cancel"){dialog,_->
                dialog.dismiss()
            }
    }
}