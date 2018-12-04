/**
 * <pre>
 *    @author  : wangchun
 *    @time    : 2018/12/4 10:49
 *    desc    :  饿汉式
 *    version : v1.0
 * </pre>
 */
public class Singleton1 {
    //初始化静态资源
    private static Singleton1 instance = new Singleton1();

    /**
     * 私有构造方法
      */
    private Singleton1(){
    }

    /**
     * 获取数据
     * @return
     */
    public static Singleton1 getInstance(){
        return instance;
    }
}
