package com.ford.campos.testdrawer;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by ethan on 8/31/2015.
 */
public class NavigationDrawerSections {

    public static ArrayList<Information> getSectionInformation(Context context) {

        ArrayList<Information> data = new ArrayList<>();

        int[] icons = {
                R.mipmap.nujabes,
                R.mipmap.future_funk,
                R.mipmap.liked
        };

        String[] text = {
                context.getString(R.string.title_section1),
                context.getString(R.string.title_section2),
                context.getString(R.string.title_section3)
        };

        boolean[] active = {
                true,
                false,
                false,
        };

        for (int i = 0; (i < text.length) && (i < icons.length); i++) {

            Information current = new Information(
                    icons[i],
                    text[i],
                    active[i]
            );

            data.add(current);

        }

        return data;
    }


}
