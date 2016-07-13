package org.ollide.rosandroid;

import java.util.Vector;

/**
 * Created by Felix on 2016-07-01.
 */
public class ColorCombinations {

    private Vector<Vector<Integer>> combinations;

    final int[][] DEFINED_COMBINATIONS = {
            { ColorAdjuster.ORANGE, ColorAdjuster.BLACK, ColorAdjuster.BLACK },
            { ColorAdjuster.BLUE, ColorAdjuster.ORANGE, ColorAdjuster.BLACK },
            { ColorAdjuster.PURPLE, ColorAdjuster.WHITE, ColorAdjuster.BLACK },
            { ColorAdjuster.WHITE, ColorAdjuster.PURPLE, ColorAdjuster.PURPLE },
            { ColorAdjuster.BLUE, ColorAdjuster.WHITE, ColorAdjuster.BLACK },
            { ColorAdjuster.BLUE, ColorAdjuster.WHITE, ColorAdjuster.WHITE },
            { ColorAdjuster.ORANGE, ColorAdjuster.WHITE, ColorAdjuster.BLACK },
            { ColorAdjuster.RED, ColorAdjuster.WHITE, ColorAdjuster.BLACK },
            { ColorAdjuster.BLACK, ColorAdjuster.WHITE, ColorAdjuster.WHITE },
            { ColorAdjuster.BLACK, ColorAdjuster.WHITE, ColorAdjuster.ORANGE },
            { ColorAdjuster.GREEN, ColorAdjuster.WHITE, ColorAdjuster.BLACK },
            { ColorAdjuster.GREEN, ColorAdjuster.WHITE, ColorAdjuster.WHITE },
            { ColorAdjuster.PURPLE, ColorAdjuster.ORANGE, ColorAdjuster.BLACK },
            { ColorAdjuster.PURPLE, ColorAdjuster.ORANGE, ColorAdjuster.WHITE },
            { ColorAdjuster.GREEN, ColorAdjuster.ORANGE, ColorAdjuster.BLACK },
            { ColorAdjuster.RED, ColorAdjuster.ORANGE, ColorAdjuster.BLACK },
            { ColorAdjuster.BLACK, ColorAdjuster.BLUE, ColorAdjuster.WHITE },
            { ColorAdjuster.RED, ColorAdjuster.BLUE, ColorAdjuster.BLACK }
    };

    public ColorCombinations(){

        combinations = new Vector<Vector<Integer>>();

        for(int i = 0; i < 18; ++i) {

            Vector<Integer> combination = new Vector<Integer>();

            for (int j = 0; j < 3; ++j)
                combination.add(DEFINED_COMBINATIONS[i][j]);

            combinations.add(combination);

        }

    }

    public Vector<Integer> at(int position){

        return combinations.elementAt(position);

    }

}
