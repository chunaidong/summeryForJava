import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * <pre>
 *    @author  : wangchun
 *    @time    : 2018/12/4 11:34
 *    desc    : 自己实现lock接口
 *    version : v1.0
 * </pre>
 */
public class MyLock implements Lock{

    private boolean isLocked = false;
    private int lockCount;
    private Thread lockedThread;
    @Override
    public synchronized void lock() {
         while (isLocked && lockedThread!= Thread.currentThread()){
             try {
                 wait();
             } catch (InterruptedException e) {
                 e.printStackTrace();
             }
         }
        isLocked = true;
        lockCount++;
        lockedThread = Thread.currentThread();
    }

    @Override
    public synchronized void unlock() {
        if(lockedThread == Thread.currentThread()){
            lockCount--;
            if(lockCount == 0){
                isLocked = false;
                notify();
            }
        }
    }


    @Override
    public void lockInterruptibly() throws InterruptedException {

    }


    @Override
    public boolean tryLock() {
        return false;
    }


    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }





    @Override
    public Condition newCondition() {
        return null;
    }
}
