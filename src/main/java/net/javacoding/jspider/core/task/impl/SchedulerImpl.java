package net.javacoding.jspider.core.task.impl;

import java.io.Serializable;
import net.javacoding.jspider.core.exception.*;
import net.javacoding.jspider.core.task.*;
import net.javacoding.jspider.core.task.work.DecideOnSpideringTask;

import java.net.URL;
import java.util.*;
import net.javacoding.jspider.core.cache.CacheFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Default implementation of a Task scheduler
 *
 * $Id: SchedulerImpl.java,v 1.17 2003/04/25 21:29:04 vanrogu Exp $
 *
 * @author G�nther Van Roey
 */
public class SchedulerImpl implements Scheduler {

	private static final Log logger = LogFactory.getLog(SchedulerImpl.class);
	/**
	 * List of fetch tasks to be carried out.
	 */
	protected List fetchTasks;

	/**
	 * List of thinker tasks to be carried out.
	 */
	protected List thinkerTasks;

	/**
	 * Set of tasks that have been assigned.
	 */
	protected Set assignedSpiderTasks;
	protected Set assignedThinkerTasks;

	protected int spiderTasksDone;
	protected int thinkerTasksDone;
	protected int cacheSize = 0;
	public static final String CACHE_PREFIX = "Task-" + SchedulerImpl.class.getSimpleName() + "-";

	private static final int DEFAULT_FETCH_SIZE = 50;

	protected Map blocked;

	int blockedCount = 0;

	public int getBlockedCount() {
		return blockedCount;
	}

	public int getAssignedCount() {
		return assignedSpiderTasks.size() + assignedThinkerTasks.size();
	}

	public int getJobCount() {
		return getThinkerJobCount() + getSpiderJobCount();
	}

	public int getThinkerJobCount() {
		return thinkerTasksDone + assignedThinkerTasks.size() + thinkerTasks.size();
	}

	public int getSpiderJobCount() {
		return cacheSize + spiderTasksDone + assignedSpiderTasks.size() + fetchTasks.size();
	}

	public int getJobsDone() {
		return getSpiderJobsDone() + getThinkerJobsDone();
	}

	public int getSpiderJobsDone() {
		return spiderTasksDone;
	}

	public int getThinkerJobsDone() {
		return thinkerTasksDone;
	}

	/**
	 * Public constructor?
	 */
	public SchedulerImpl() {
		fetchTasks = new ArrayList();
		thinkerTasks = new ArrayList();
		assignedThinkerTasks = new HashSet();
		assignedSpiderTasks = new HashSet();
		blocked = new HashMap();
	}

	public void block(URL siteURL, DecideOnSpideringTask task) {
		ArrayList al = (ArrayList) blocked.get(siteURL);
		if (al == null) {
			al = new ArrayList();
			blocked.put(siteURL, al);
		}
		int before = al.size();
		al.add(task);
		int after = al.size();

		int diff = after - before;
		blockedCount += diff;
	}

	public DecideOnSpideringTask[] unblock(URL siteURL) {
		ArrayList al = (ArrayList) blocked.remove(siteURL);
		if (al == null) {
			return new DecideOnSpideringTask[0];
		} else {
			blockedCount -= al.size();
			return (DecideOnSpideringTask[]) al.toArray(new DecideOnSpideringTask[al.size()]);
		}
	}

	/**
	 * Schedules a task to be processed.
	 *
	 * @param task task to be scheduled
	 */
	public synchronized void schedule(WorkerTask task) {
		if (task.getType() == WorkerTask.WORKERTASK_SPIDERTASK) {
			//is or not Serializable
			if (fetchTasks.size() > DEFAULT_FETCH_SIZE && task instanceof Serializable) {
				cacheSize += 1;
				this.putCache((Serializable) task);
			} else {
				fetchTasks.add(task);
			}
		} else {
			thinkerTasks.add(task);
		}
	}

	/**
	 * Flags a task as done.
	 *
	 * @param task task that was complete
	 */
	public synchronized void flagDone(WorkerTask task) {
		if (task.getType() == WorkerTask.WORKERTASK_THINKERTASK) {
			assignedThinkerTasks.remove(task);
			thinkerTasksDone++;
		} else {
			assignedSpiderTasks.remove(task);
			spiderTasksDone++;
		}
	}

	public synchronized WorkerTask getThinkerTask() throws TaskAssignmentException {
		if (thinkerTasks.size() > 0) {
			WorkerTask task = (WorkerTask) thinkerTasks.remove(0);
			assignedThinkerTasks.add(task);
			return task;
		}
		if (allTasksDone()) {
			throw new SpideringDoneException();
		} else {
			throw new NoSuitableItemFoundException();
		}
	}

	/**
	 * Returns a fetch task to be carried out.
	 *
	 * @return WorkerTask task to be done
	 * @throws TaskAssignmentException notifies when the work is done or there
	 * are no current outstanding tasks.
	 */
	@Override
	public synchronized WorkerTask getFethTask() throws TaskAssignmentException {
		if (fetchTasks.size() > 0) {
			WorkerTask task = (WorkerTask) fetchTasks.remove(0);
			assignedSpiderTasks.add(task);
			return task;
		} else if (cacheSize > 0) {
			Serializable c = this.getCache();
			while (c != null) {
				cacheSize = cacheSize - 1;
				logger.warn("Get cache is null! It is a ehcache config Bug, must reconfiger it! position: " + cacheSize);
			}
			return (WorkerTask) c;
		}
		if (allTasksDone()) {
			throw new SpideringDoneException();
		} else {
			throw new NoSuitableItemFoundException();
		}
	}

	/**
	 * Determines whether all the tasks are done. If there are no more tasks
	 * scheduled for process, and no ongoing tasks, it is impossible that new
	 * work will arrive, so the spidering is done.
	 *
	 * @return boolean value determining whether all work is done
	 */
	public synchronized boolean allTasksDone() {
		return (fetchTasks.size() == 0
				&& thinkerTasks.size() == 0
				&& assignedSpiderTasks.size() == 0
				&& assignedThinkerTasks.size() == 0
				&& blocked.size() == 0);
	}

	/*
	 public synchronized String toString ( ) {
	 StringBuffer sb = new StringBuffer();
	 Iterator it = this.thinkerTasks.iterator();
	 while ( it.hasNext() ) {
	 System.out.println("TH . " + it.next().getClass());
	 }
	 it = this.assignedThinkerTasks.iterator();
	 while ( it.hasNext() ) {
	 System.out.println("TH A " + it.next().getClass());
	 }
	 it = this.fetchTasks.iterator();
	 while ( it.hasNext() ) {
	 System.out.println("SP . " + it.next().getClass());
	 }
	 it = this.assignedSpiderTasks.iterator();
	 while ( it.hasNext() ) {
	 System.out.println("SP A " + it.next().getClass());
	 }
	 return sb.toString();
	 }   */
	//must thread safe!
	private String putCache(Serializable s) {
		String key = CACHE_PREFIX + cacheSize;
		CacheFactory.getCommonCache().cache(key, s);
		return key;
	}

	//must thread safe! and not Clear! for simple not clear but you can!
	private Serializable getCache() {
		String key = CACHE_PREFIX + cacheSize;
		return CacheFactory.getCommonCache().getCacheSerializable(key);
	}
}
