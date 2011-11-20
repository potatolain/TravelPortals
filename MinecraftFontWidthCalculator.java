package com.bukkit.cppchriscpp.TravelPortals;

/**
 * DO WHAT YOU WANT TO PUBLIC LICENSE
 * (Obtained from: http://www.astalavista.com/proxy/browse.php/Oi8vcGFz/dGViaW4u/Y29tL0Ry/Ulh2YlJl/b5/)
 */
public class MinecraftFontWidthCalculator {

    private static String weirdHardcodedStuffIFoundInMinecraft = " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_'abcdefghijklmnopqrstuvwxyz{|}~¦ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»";
    private static int[] charWidth = {1,9,9,8,8,8,8,7,9,8,9,9,8,9,9,9,8,8,8,8,9,9,8,9,8,8,8,8,8,9,9,9,4,2,5,6,6,6,6,3,5,5,5,6,2,6,2,6,6,6,6,6,6,6,6,6,6,6,2,2,5,6,5,6,7,6,6,6,6,6,6,6,6,4,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,4,6,4,6,6,3,6,6,6,6,6,5,6,6,2,6,5,3,6,6,6,6,6,6,6,4,6,6,6,6,6,6,5,2,5,7,6,6,6,6,6,6,6,6,6,6,6,6,4,6,3,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,4,6,6,3,6,6,6,6,6,6,6,7,6,6,6,2,6,6,8,9,9,6,6,6,8,8,6,8,8,8,8,8,6,6,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,6,9,9,9,5,9,9,8,7,7,8,7,8,8,8,7,8,8,7,9,9,6,7,7,7,7,7,9,6,7,8,7,6,6,9,7,6,7,1};

    // Custom function to get the maximum width of the string.
    public static int getMaxStringWidth()
    {
        return getStringWidth("---------------------------------------------------");
    }


    // taken directly from notchcode. enjoy
    public static int getStringWidth(String s) {
        if (s == null) {
            return 0;
        }
        int i = 0;
        for (int j = 0; j < s.length(); j++) {
            if (s.charAt(j) == '\247') {
                j++;
                continue;
            }
            int k = weirdHardcodedStuffIFoundInMinecraft.indexOf(s.charAt(j));
            if (k >= 0) {
                i += charWidth[k + 32];
            }
        }

        return i;
    }
}