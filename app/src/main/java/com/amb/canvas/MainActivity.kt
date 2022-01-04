package com.amb.canvas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import com.amb.ambcanvas.AmbCanvasView
import com.amb.canvas.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnDraw.setOnClickListener {
            binding.canvas.startDrawing()

        }

        binding.btnEraser.setOnClickListener {
            binding.canvas.enableEraser()

        }

        binding.btnClear.setOnClickListener {
            binding.canvas.clear()
        }

        binding.btnUndo.setOnClickListener {
            binding.canvas.undo()
        }

        binding.btnRedo.setOnClickListener {
            binding.canvas.redo()
        }

        binding.btnRectangle.setOnClickListener {
            binding.canvas.drawShape("RECTANGLE")

        }

        binding.btnCircle.setOnClickListener {
            binding.canvas.drawShape("CIRCLE")
        }

        binding.btnEllipse.setOnClickListener {
            binding.canvas.drawShape("ELLIPSE")
        }

        binding.btnPen.setOnClickListener {
            binding.canvas.startDrawing()
        }

        binding.btnLine.setOnClickListener {
            binding.canvas.drawShape("LINE")
        }

        binding.colorSeekBar.setOnColorChangeListener(object : ColorSeekBar.OnColorChangeListener {
            override fun onColorChangeListener(color: Int) {
                binding.canvas.paintStrokeColor = color
            }
        })

        binding.seekbarOpacity.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val opacity = ((progress * 255) / 100).toInt();
                binding.canvas.setOpacity(opacity)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

            }
        })

        binding.seekbarWidth.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val newProgress = progress.toFloat()
                binding.canvas.setPaintStrokeWidth(newProgress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })


    }
}