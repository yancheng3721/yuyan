package public_static;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2017/5/31.
 */
public class PublicStaticTest {
    public static int a = 10;
    public static String string = "1111111";
    public static List<String> list = new ArrayList<String>();

    public static void main(String[] args) {
        list.add("");
        testStatic();
        sleepSeconds(10);
        testStatic();
        System.gc();

        sleepSeconds(10);

    }
    public static void sleepSeconds(int sec){
        try {
            Thread.sleep(sec*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static void testStatic(){
        int count = 50000000;
        for(int i = 0;i<count;i++){
            PublicStaticTest.a=i+a;
            //PublicStaticTest.string = "b"+"d";
            PublicStaticTest.list.set(0, ""+i);
        }
    }



}
