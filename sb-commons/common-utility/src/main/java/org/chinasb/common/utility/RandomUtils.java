package org.chinasb.common.utility;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

/**
 * 强随机数工具类(线程安全)
 * @version 1.0.0
 * @author zhujuan
 * @created 2013-12-4
 */
public class RandomUtils {
    
    private static ThreadLocal<Random> threadLocal = new ThreadLocal<Random>();

    public static void setRandomProvider(Random random) {
        threadLocal.set(random);
    }

    private static Random getRandomProvider() {
        Random random = (Random) threadLocal.get();
        if (random == null) {
            String algorithm = "SHA-256";
            try {
                random = SecureRandom.getInstance(algorithm);
            } catch (NoSuchAlgorithmException e) {
                random = new SecureRandom();
            }
            threadLocal.set(random);
        }
        return random;
    }

    public static boolean nextBoolean() {
        return getRandomProvider().nextBoolean();
    }

    public static double nextDouble() {
        return getRandomProvider().nextDouble();
    }

    public static float nextFloat() {
        return getRandomProvider().nextFloat();
    }

    public static int nextInt() {
        return getRandomProvider().nextInt();
    }

    public static int nextInt(int n) {
        return getRandomProvider().nextInt(n);
    }

    public static long nextLong() {
        return getRandomProvider().nextLong();
    }
}
