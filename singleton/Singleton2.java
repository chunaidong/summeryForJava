/**
 * <pre>
 *    @author  : wangchun
 *    @time    : 2018/12/4 10:53
 *    desc    : 懒汉
 *    version : v1.0
 * </pre>
 */
public class Singleton2 {

    /**
     * 保证线程中共享
     */
    private volatile static Singleton2 instance;


    private Singleton2(){

    }

    public static Singleton2 getInstance(){
        if(instance == null){
            synchronized (Singleton2.class){
                if(instance == null){
                    instance = new Singleton2();
                }
            }
        }
        return instance;
    }
}
