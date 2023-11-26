package com.example.blackbox;
import static org.junit.Assert.assertEquals;

import com.example.blackbox.tag.ColorSpinnerAdapter;
import com.example.blackbox.tag.TagColor;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;


public class ColorSpinnerAdapterTest {
    private ArrayList<TagColor> basicColors;
    @Before
    public void getTagColors(){
        ArrayList<TagColor> colors = new ArrayList<>();
        colors.add(new TagColor("Red", 1));
        colors.add(new TagColor("Orange", 2));
        colors.add(new TagColor("Yellow", 3));
        colors.add(new TagColor("Green", 4));
        colors.add(new TagColor("Blue", 5));
        colors.add(new TagColor("Purple", 6));
        basicColors = colors;
    }

    public ColorSpinnerAdapter getDefaultColorSpinner(){
        return new ColorSpinnerAdapter(null, new ArrayList<>(basicColors));
    }

    @Test
    public void testCorrectColors(){
        ColorSpinnerAdapter spinner = getDefaultColorSpinner();
        ArrayList<TagColor> returnedColors = spinner.getColors();
        assertEquals(returnedColors, basicColors);
    }
    @Test
    public void testGetColorIndex(){
        ColorSpinnerAdapter spinner = getDefaultColorSpinner();
        // test that the correct index can be retrieved for a color in the list
        assertEquals(spinner.getColorIndex(basicColors.get(3).getName()), 3);
        // test that a color not in the list returns -1
        assertEquals(spinner.getColorIndex("Random String"), -1);
    }
}
