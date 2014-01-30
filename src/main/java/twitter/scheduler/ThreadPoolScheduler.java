/*
   Copyright 2013 Philipp Leitner

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package twitter.scheduler;

import twitter.SentAnalysis;
import twitter.scaling.EmptyCloudObject;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author RST
 * Schedules prime search on the specified amount of threads and run them in the ThreadPool.
 */
public class ThreadPoolScheduler 
{
	/**
	 * The thread pool to run tasks in.
	 */
	private ExecutorService threadPool = null;
	
	/**
	 * Amount of threads to run 
	 */
	private int threadCounter;
	
	/**
	 * Amount of currently (still) running threads
	 */
	private AtomicInteger threadsRunning = new AtomicInteger(0);
	
	/**
	 * Accumulated result so far.
	 */
	private AtomicLong result = new AtomicLong();
	
	/**
	 * Sync object for signalling on complete operation.
	 */
	private Object sync = new Object();
	
	/**
	 * The splitter that will split initial  search region on the smaller regions for separate threads. 
	 */

	/**
	 * Creates the new instance of Thread Pool Scheduler with the specified number of threads to run and the splitter to split the provided task
	 * @param threadCounter The amount of threads to schedule execution on.
	 * @param splitter The splitter that should be used to split task on the smaller parts. 
	 */
	public ThreadPoolScheduler(int threadCounter)
	{
		this.threadCounter = threadCounter;
	}
	
	/**
	 * Runs blocking search on the specified interval with configured amount of threads and splitter.
	 * @param from The beginning of the search interval
	 * @param to The end of the search interval
	 * @return The amount of prime numbers found on the interval
	 */
	public double sentimentAnalysis(String text, String from, String to, int parts)
	{
		
		// initialization
		threadPool = Executors.newCachedThreadPool();
		threadsRunning.set(0);
		result.set(0);
		
		// starting workers
		synchronized (sync) 
		{
			threadsRunning.set(threadCounter);
			for (int x = 1; x <= parts; x++) {
				threadPool.execute(new SentAnalyser(text, from, to, parts, x));
			}
			//waiting for result
			try 
			{
				sync.wait();
			} 
			catch (InterruptedException e){}
		}
		
		threadPool.shutdown();
		
		return (double)result.get() / parts / 100;
	}
	
	/**
	 * Adds found amount to the result counter and in case this is the last thread running, informs main thread that result is ready.
	 * @param count The amount of found prime numbers.
	 */
	private void reportResult(double count)
	{

		result.addAndGet((int)(count * 100));
		
		int workers = threadsRunning.decrementAndGet();
		
		if(workers == 0)
		{
			synchronized (sync) 
			{
				sync.notifyAll();
			}
		}
	}
	
	/**
	 * @author RST
	 * Separate runnable search task with the specified range.
	 */
	private class SentAnalyser implements Runnable
	{
		String text;
		String from;
		String to;
		int parts;
		int part;

		private SentAnalyser(String text, String from, String to, int parts, int part) {
			this.text = text;
			this.from = from;
			this.to = to;
			this.parts = parts;
			this.part = part;
		}

		@Override
		public void run() 
		{
			SentAnalysis analysis = new SentAnalysis(text, from, to, parts, part);
			System.out.println("Starting thread " + part);
			analysis.run();
			reportResult(analysis.getResult());
		}
	}

	public void startEmptyTask() {
		threadPool = Executors.newCachedThreadPool();
		threadPool.execute(new EmptyTask());
	}
	
	private class EmptyTask implements Runnable
	{

		private EmptyTask() {
		
		}

		@Override
		public void run() 
		{
			EmptyCloudObject empty = new EmptyCloudObject();
			System.out.println("Starting empty thread");
			empty.run();
		}
	}
}
