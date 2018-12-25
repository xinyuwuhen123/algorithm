package com.waha;
/**
 * 交替打印奇偶数
 * 用condition来做信号量实现奇数偶数的交替打印
 * @author wanghao
 * @date 2018年12月25日
 */

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class NumberPrint {
	
	private int index;

	private Lock lock = new ReentrantLock();
	
	private Condition oddConditon = lock.newCondition();
	
	private Condition evenCondition = lock.newCondition();
	
	public void printOdd() throws InterruptedException {
		lock.lock();
		try {
			while (index < 1000) {
				if (index % 2 == 0)
					oddConditon.await();
				System.out.println(Thread.currentThread().getName() + ":" + index++);
				evenCondition.signal();
			}
		} finally {
			lock.unlock();
		}
	}
	
	/**
	 * 举例。如果到了999，偶数线程await，奇数线程不论之前被signal之后抢占到了锁还是evenCondition.await让出了锁，都能获得执行的权限，打印1000
	 * @throws InterruptedException
	 */
	public void printEven() throws InterruptedException {
		lock.lock();
		try {
			while (index < 1000) {
				if (index % 2 == 1)
					evenCondition.await();
				System.out.println(Thread.currentThread().getName() + ":" + index++);
				oddConditon.signal();
			}
		} finally {
			lock.unlock();
		}
	}
	
	public static void main(String[] args) {
		NumberPrint numberPrint = new NumberPrint();
		Thread thread1 = new Thread(() -> {try {
			numberPrint.printOdd();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}});
		thread1.setName("odd thread");
		Thread thread2 = new Thread(() -> {try {
			numberPrint.printEven();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}});
		thread2.setName("eve thread");
		
		thread2.start();
		thread1.start();
	}
}
