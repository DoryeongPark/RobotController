package org.ollide.rosandroid;

import java.util.Vector;

/**
 * Created by Felix on 2016-07-01.
 */
public class ThemeTypes {

    private Vector<Vector<Integer>> combinations;

    final int[][] DEFINED_COMBINATIONS = {

            {Theme.ORANGE, Theme.BLACK, Theme.BLACK},
            {Theme.ORANGE, Theme.BLUE, Theme.BLACK},
            {Theme.ORANGE, Theme.PURPLE, Theme.BLACK},
            {Theme.BLACK, Theme.WHITE, Theme.WHITE},
            {Theme.BLUE, Theme.WHITE, Theme.WHITE},
            {Theme.PURPLE, Theme.WHITE, Theme.WHITE},
            {Theme.RED, Theme.WHITE, Theme.BLACK},
            {Theme.GREEN, Theme.WHITE, Theme.BLACK},
            {Theme.ORANGE, Theme.WHITE, Theme.BLACK}

    };

    public ThemeTypes(){

        combinations = new Vector<Vector<Integer>>();

        for(int i = 0; i < 9; ++i) {

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
