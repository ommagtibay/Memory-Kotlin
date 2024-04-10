package com.obyslab.memory_kotlin

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        const val PAIR = 2
        const val DELAY = 500L  // Flip animation speed
    }

    private var ROW = 0 // Total number of row to display
    private var COLUMN = 0 // total number of column to display
    private var MAX = 0 // Total number of image view (ROW * COLUMN)
    private var HEIGHT = 0 // Card height
    private var WIDTH = 0 // Card with
    private var MARGIN = 0 // Space between cards

    private var imgTag = 0
    private var imgCtr: Int = 0
    private var match: Int = 0
    private var tmpIndex = IntArray(PAIR)

    private lateinit var table: Array<IntArray>

    private lateinit var innerLayout: Array<RelativeLayout?>
    private lateinit var innerParams: Array<RelativeLayout.LayoutParams?>

    private lateinit var imageViewOriginal: Array<ImageView?>
    private lateinit var imageViewFlip: Array<ImageView?>

    private lateinit var animator: Array<FlipAnimation?>

    private var subParams = RelativeLayout.LayoutParams(
        RelativeLayout.LayoutParams.WRAP_CONTENT,
        RelativeLayout.LayoutParams.WRAP_CONTENT
    )

    private var originalParams: RelativeLayout.LayoutParams? = null
    private var flipParams: RelativeLayout.LayoutParams? = null

    // Placeholder for image resources to be used
    private lateinit var imageResourceId: IntArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        init()
    }

    private fun init() {
        val card = Card(this@MainActivity, size = CardSize.EASY.value)

        imageResourceId = card.getResources(1)  // Select set of images to use
        card.shuffleResources(imageResourceId)  // Shuffles images from selected resources

        ROW = card.row
        COLUMN = card.column
        HEIGHT = card.height
        WIDTH = card.width
        MARGIN = card.margin
        MAX = ROW * COLUMN

        match = 0

        createView()
    }

    private fun createView() {
        table = Array(MAX / 2) { IntArray(MAX / 2) }

        originalParams = RelativeLayout.LayoutParams(HEIGHT, WIDTH)
        flipParams = RelativeLayout.LayoutParams(HEIGHT, WIDTH)

        innerLayout = arrayOfNulls(MAX)
        imageViewOriginal = arrayOfNulls(MAX)
        imageViewFlip = arrayOfNulls(MAX)
        animator = arrayOfNulls(MAX)

        innerParams = arrayOfNulls(MAX)

        val mainLayout = RelativeLayout(this)
        val subLayout = RelativeLayout(this)

        subParams.addRule(RelativeLayout.CENTER_IN_PARENT)

        /**
         * Generates random number from MAXIMUM number of images
         * This will be the position of each pair of images
         **/
        val numbers = ArrayList<Int>()
        var number = (Math.random() * MAX).toInt()

        numbers.add(number)

        for (i in 0 until MAX - 1) {
            do {
                number = (Math.random() * MAX).toInt()
            } while (numbers.contains(number))

            numbers.add(number)
        }

        /**
         * Draws the matrix into activity
         * Creates the individual card with the back image
         **/
        var index = 0
        var col = 0

        for (i in 0 until ROW) {
            for (j in 0 until COLUMN) {
                innerParams[index] = RelativeLayout.LayoutParams(HEIGHT, WIDTH)
                innerLayout[index] = RelativeLayout(this)
                imageViewOriginal[index] = ImageView(this)
                imageViewFlip[index] = ImageView(this)
                innerLayout[index]?.setId(index)
                imageViewOriginal[index]?.tag = index
                imageViewOriginal[index]?.setOnClickListener(this)
                imageViewFlip[index]?.tag = index
                imageViewFlip[index]?.setPadding(10, 10, 10, 10)

                setBackgroundColor(imageViewOriginal[index]!!) // Change back card background

                if (j > 0) {
                    innerParams[index]?.setMargins(
                        (WIDTH + MARGIN) * j,
                        (HEIGHT + MARGIN) * i,
                        0,
                        0
                    )
                } else {
                    innerParams[index]?.setMargins(0, (HEIGHT + MARGIN) * i, 0, 0)
                    innerParams[index]?.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                }

                innerLayout[index]?.addView(imageViewFlip[index], flipParams)
                innerLayout[index]?.addView(imageViewOriginal[index], originalParams)

                subLayout.addView(innerLayout[index], innerParams[index])
                col = if (col == COLUMN - 1) 0 else col + 1

                index++
            }
        }

        /** Set images to two cards */
        var imageIndex = 0
        var row = 0
        var cols = 0

        for (i in numbers) {
            imageViewFlip[i]?.setImageResource(imageResourceId[imageIndex])
            setBackgroundColor(imageViewFlip[i]!!) // Change flip card background

            if (cols == 1) {
                table[row][cols] = i
                imageIndex++
                row++
                cols = 0
            } else {
                table[row][cols] = i
                cols++
            }
        }

        mainLayout.setBackgroundColor(getColor(R.color.white))  // Change main layout background
        mainLayout.addView(subLayout, subParams)

        setContentView(mainLayout)
    }

    /**
     * Change card background
     */
    private fun setBackgroundColor(v: View) {
        v.setBackgroundResource(R.drawable.bg_template)

        val gd = v.background as GradientDrawable

        gd.setColor(getColor(R.color.red))
    }

    override fun onClick(v: View?) {
        /**
         * Checks if there are two MEMORY images displayed
         */
        if (imgCtr != 2) {
            imgTag = v?.tag.toString().toInt() // Determine which image is selected
            tmpIndex[imgCtr] = imgTag // Assign image index to temporary holder of two images index
            imgCtr++ // Counts image selected/tapped (MAXIMUM of 2)

            imageViewOriginal[imgTag]!!.setEnabled(false) // Disabled selected default image and it's MEMORY image
            imageViewFlip[imgTag]!!.setEnabled(false)

            animator[imgTag] = FlipAnimation(
                imageViewOriginal[imgTag]!!, imageViewFlip[imgTag]!!, imageViewFlip[imgTag]!!
                    .width / 2, imageViewFlip[imgTag]!!.height / 2
            )

            innerLayout[imgTag]!!.startAnimation(animator[imgTag])
        }

        if (imgCtr == 2) {
            /**
             * Disable the rest of the images if two images are open
             */
            for (i in 0 until MAX) {
                imageViewOriginal[i]!!.setEnabled(false)
            }

            Handler(Looper.getMainLooper())
                .postDelayed(runnable, DELAY)   // calls the animation for flipping images

            imgCtr = 0
        }
    }

    /**
     * Will execute card flip animation
     */
    private val runnable = Runnable {
        /**
         * Enable click for the images
         */
        var i = 0
        while (i < MAX) {
            imageViewOriginal[i]!!.setEnabled(true)
            i++
        }

        /**
         * Check if two image matches
         */
        i = 0
        while (i < MAX / 2) {
            if (table[i][0] == tmpIndex[0] && table[i][1] == tmpIndex[1] || table[i][0] == tmpIndex[1] && table[i][1] == tmpIndex[0]) {
                match++

                imageViewOriginal[tmpIndex[0]]!!.isClickable = false
                imageViewOriginal[tmpIndex[1]]!!.isClickable = false

                /**
                 * Check if all card pairs are solved
                 */
                if (match == MAX / 2) {
                    MaterialAlertDialogBuilder(this)
                        .setMessage(getString(R.string.msg_again))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.button_yes)) { _, _ ->
                            init()
                            createView()
                        }
                        .setNegativeButton(getString(R.string.button_no)) { _, _ ->
                            finish()
                        }
                        .create().show()
                }

                return@Runnable
            }

            i++
        }

        /**
         * Return MEMORY images to default image
         */
        i = tmpIndex.size - 1
        while (i >= 0) {
            animator[tmpIndex[i]] = FlipAnimation(
                imageViewOriginal[tmpIndex[i]]!!, imageViewFlip[tmpIndex[i]]!!,
                imageViewFlip[tmpIndex[i]]!!.width / 2, imageViewFlip[tmpIndex[i]]!!
                    .height / 2
            )

            if (imageViewOriginal[tmpIndex[i]]!!.visibility == View.GONE) {
                animator[tmpIndex[i]]!!.reverse()
            }

            innerLayout[tmpIndex[i]]!!.startAnimation(animator[tmpIndex[i]])
            imageViewOriginal[tmpIndex[i]]!!.setEnabled(true)
            imageViewFlip[tmpIndex[i]]!!.setEnabled(true)

            i--
        }
    }
}