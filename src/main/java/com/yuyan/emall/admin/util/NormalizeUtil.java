package com.yuyan.emall.admin.util;

import java.util.ArrayList;

/**
 * Created by admin on 2017/5/23.
 */
public class NormalizeUtil {
    static Float[] normalize(final Float[] vec) {
        float sum = 0.0f;
        for (int i = 0; i < vec.length; ++i) sum += vec[i] * vec[i];
        final float divisor = (float) Math.sqrt(sum);
        Float[] a = new Float[vec.length];
        for (int i = 0; i < vec.length; ++i) a[i] = vec[i]/divisor;
        return a;
    }

    public static void SquaryNormalizeFun(){
        ArrayList<Float> tmp = new ArrayList<Float>();

        for(int i=0;i<5000000;i++){
            tmp.add((float) i);
        }
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long start = System.currentTimeMillis();
        Float[] inputVector = tmp.toArray(new Float[tmp.size()]);
        long end = System.currentTimeMillis();
        System.out.println((end - start)/1000);
        Float[] resultVector = normalize(inputVector);

        System.out.print(String.format("%f", (resultVector[5000000-1])) + " ");
        System.out.print(String.format("%f", (resultVector[5000000-1000000])) + " ");
        System.out.print(String.format("%f", (resultVector[5000000-1500500])) + " ");
        System.out.print(String.format("%f", (resultVector[500])) + " ");

    }

    public static void main(String[] args) {
        logNormalizeFun();
    }

    public static void  logNormalizeFun(){

        System.out.println(Math.log(5000000));
        System.out.println(Math.log(4000000));
        System.out.println(Math.log(3000000));
        System.out.println(Math.log(5000));

    }
}
