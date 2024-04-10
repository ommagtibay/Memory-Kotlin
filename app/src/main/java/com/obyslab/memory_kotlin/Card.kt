package com.obyslab.memory_kotlin

import android.app.Activity
import android.util.DisplayMetrics
import java.util.Random

/**
 * Number of cards in board enumerated as difficulty
 */
enum class CardSize(val value: Int) {
    EASY(0),
    NORMAL(1),
    HARD(2)
}

class Card(activity: Activity, size: Int) {
    /**
     * Set of resources to be used
     *
     * Marvel icons created by Hopstarter
     * https://www.flaticon.com/free-icons/marvel
     */
    companion object {
        val RESOURCES = arrayOf(
            intArrayOf(
                R.drawable.image_01,
                R.drawable.image_02,
                R.drawable.image_03,
                R.drawable.image_04,
                R.drawable.image_05,
                R.drawable.image_06,
                R.drawable.image_07,
                R.drawable.image_08,
                R.drawable.image_09,
                R.drawable.image_10,
                R.drawable.image_11,
                R.drawable.image_12,
                R.drawable.image_13,
                R.drawable.image_14,
                R.drawable.image_15,
                R.drawable.image_16,
                R.drawable.image_17,
                R.drawable.image_18,
                R.drawable.image_19,
                R.drawable.image_20,
                R.drawable.image_21,
                R.drawable.image_22,
                R.drawable.image_23,
                R.drawable.image_24,
                R.drawable.image_25
            ),
            // Add more set of images
        )

        /**
         * Height and width of cards
         * Height and width index is based on set card size
         */
        private val HEIGHT_WIDTH = arrayOf(
            intArrayOf(300, 230, 180),
            intArrayOf(300, 230, 180),
            intArrayOf(300, 230, 180),
            intArrayOf(120, 100, 80),
            intArrayOf(200, 160, 130),
            intArrayOf(300, 240, 190),
            intArrayOf(400, 320, 260)
        )

        /**
         * Margin of cards
         * Margin index is based on card size (0,1,2)
         */
        private val MARGIN = arrayOf(
            intArrayOf(10, 20, 20),
            intArrayOf(10, 20, 20),
            intArrayOf(10, 20, 20),
            intArrayOf(10, 10, 10),
            intArrayOf(10, 10, 10),
            intArrayOf(20, 20, 20),
            intArrayOf(20, 20, 20)
        )
    }

    var row = 0
        private set

    var column = 0
        private set

    val height: Int
    val width: Int
    val margin: Int

    init {
        var density = 0
        val COLUMN = intArrayOf(3, 4, 5)
        val ROW = intArrayOf(6, 7, 8)
        val displayMetrics = DisplayMetrics()

        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)

        when (displayMetrics.densityDpi) {
            DisplayMetrics.DENSITY_LOW -> {}
            DisplayMetrics.DENSITY_MEDIUM -> density = 1
            DisplayMetrics.DENSITY_HIGH -> density = 2
            DisplayMetrics.DENSITY_XHIGH -> density = 3
            DisplayMetrics.DENSITY_XXHIGH -> density = 4
            DisplayMetrics.DENSITY_XXXHIGH -> density = 5
        }

        row = ROW[size]
        column = COLUMN[size]
        height = HEIGHT_WIDTH[density][size]
        width = HEIGHT_WIDTH[density][size]
        margin = MARGIN[density][size]
    }

    fun getResources(image: Int): IntArray {
        return if (image == 0) {
            // Make a random set of image resources
            RESOURCES[(Math.random() * RESOURCES.size).toInt()]
        } else RESOURCES[image - 1]
    }

    /**
     * Shuffles selected set of image resources
     */
    fun shuffleResources(array: IntArray) {
        var index: Int
        var temp: Int

        for (i in array.size - 1 downTo 1) {
            index = Random().nextInt(i + 1)
            temp = array[index]
            array[index] = array[i]
            array[i] = temp
        }
    }

    override fun toString(): String {
        return """
            CARD_DETAILS:
            Row : ${row}
            Column : ${column}
            Height : ${height}
            Width : ${width}
            Margin : ${margin}
            """.trimIndent()
    }
}