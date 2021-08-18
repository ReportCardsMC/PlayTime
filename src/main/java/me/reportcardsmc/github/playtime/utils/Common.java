package me.reportcardsmc.github.playtime.utils;

public class Common {

    public static long[] removeIndex(long[] arr, int index) {
        long[] cloned = new long[arr.length - 1];
        for (int i = 0; i < arr.length; i++) {
            if (i != index) cloned[i] = arr[i];
        }
        return cloned;
    }

    public static long[] addElement(int[] sessions, int session, boolean beginning) {
        long[] cloned = new long[sessions.length + 1];
        if (beginning) {
            int i = 1;
            cloned[0] = session;
            for (; i < cloned.length; i++) {
                cloned[i] = sessions[i - 1];
            }
            return cloned;
        }
        // OTHERWISE
        int i = 0;
        cloned[cloned.length - 1] = session;
        for (; i < cloned.length - 1; i++) {
            cloned[i] = sessions[i];
        }
        return cloned;
    }

    public static int longToInt(long value) {
        return Integer.parseInt(String.valueOf(value));
    }
}
