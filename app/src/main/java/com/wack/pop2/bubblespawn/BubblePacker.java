package com.wack.pop2.bubblespawn;

import android.util.Pair;

import com.wack.pop2.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

public class BubblePacker {

    /**
     * Given the number of bubbles we want to spawn returns their initial locations so that they
     * don't spawn on top of one another
     */
    public static List<Pair<Float, Float>> getSpawnBubblesLocations(int numBubbles, int bubbleSizePx) {
        List<Pair<Float, Float>> positions = new ArrayList<>();
        float currentWidth = 0;
        float currentHeight = 0;

        int numRows = 1;

        // layout in a grid
        for (int i = 0 ; i < numBubbles ; i++) {
            positions.add(new Pair(currentWidth, currentHeight));
            currentWidth += bubbleSizePx;

            // Move the cursor to the next row
            if (currentWidth > ScreenUtils.getSreenSize().widthPx) {
                currentWidth = 0;
                currentHeight += bubbleSizePx;
                numRows++;
            }
        }

        int numBubblesInRow = Math.min(numBubbles, (int) Math.floor(ScreenUtils.getSreenSize().widthPx / bubbleSizePx));
        float leftPadding = (ScreenUtils.getSreenSize().widthPx - numBubblesInRow * bubbleSizePx) / 2;
        float bottomPadding = - bubbleSizePx * numRows;

        List<Pair<Float, Float>> offsetPositions = new ArrayList<>();
        // offset all the positions to be centered above the screen
        for (Pair<Float, Float> position : positions) {
            offsetPositions.add(new Pair<>(position.first + leftPadding, position.second + bottomPadding));
        }
        // center all the bubbles
        return offsetPositions;
    }
}
