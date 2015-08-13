/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.javacoding.jspider.core.task.work;

import java.util.HashMap;
import java.util.Map;
import net.javacoding.jspider.core.AgentFactory;
import net.javacoding.jspider.core.SpiderContext;
import net.javacoding.jspider.core.event.impl.LoadCacheURLFoundEvent;
import net.javacoding.jspider.core.task.WorkerTask;

/**
 *
 * @author changshu.li
 */
public class CacheFlagWorkerTask extends BaseWorkerTaskImpl {

	private static Map<String, CacheFlagWorkerTask> map = new HashMap();

	/**
	 * each SpiderContext must have different cache dir !!
	 *
	 * @param context
	 * @return
	 */
	public static CacheFlagWorkerTask getCacheFlagWorkerTask(SpiderContext context) {
		if (!map.containsKey(context.toString())) {
			synchronized (CacheFlagWorkerTask.class) {
				if (map.get(context.toString()) == null) {
					map.put(context.toString(), new CacheFlagWorkerTask(context));
				}
			}
		}
		return map.get(context.toString());
	}

	private CacheFlagWorkerTask(SpiderContext context) {
		super(context, WorkerTask.WORKERTASK_SPIDERTASK);
	}

	@Override
	public void prepare() {
	}

	@Override
	public void execute() {
		//Need load cache url task!!
		AgentFactory.getAgent(context).registerEvent(null, new LoadCacheURLFoundEvent(context));
	}

}
