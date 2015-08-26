/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.javacoding.jspider.core.task.work;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import net.javacoding.jspider.api.model.Site;
import net.javacoding.jspider.core.SpiderContext;
import net.javacoding.jspider.core.task.WorkerTask;
import net.javacoding.jspider.core.util.URLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author changshu.li
 */
public class SerializableSpiderHttpURLTask implements WorkerTask, Serializable {

	private static Log log = LogFactory.getLog(SerializableSpiderHttpURLTask.class);
	private final static Map<String, SpiderContext> contexts = new HashMap();

	private URL foundURL;
	private String contextToString;
	transient private SpiderHttpURLTask task;
	transient private SpiderContext context;

	/**
	 * each SpiderContext must have different cache dir !!
	 *
	 * @param context
	 * @param foundURL
	 * @return
	 */
	public static SerializableSpiderHttpURLTask createCacheFlagWorkerTask(SpiderContext context, URL foundURL) {
		if (!contexts.containsKey(context.toString())) {
			synchronized (SerializableSpiderHttpURLTask.class) {
				if (contexts.get(context.toString()) == null) {
					contexts.put(context.toString(), context);
				}
			}
		} else {
			if (contexts.get(context.toString()) != context) {
				throw new RuntimeException("Different context must has different toString result !");
			}
		}
		return new SerializableSpiderHttpURLTask(context, foundURL);
	}

	public SerializableSpiderHttpURLTask(SpiderContext context, URL findURL) {
		this.context = context;
		this.foundURL = findURL;
		this.contextToString = context.toString();
	}

	@Override
	public void prepare() {
		URL siteURL = URLUtil.getSiteURL(this.foundURL);
		Site site = context.getStorage().getSiteDAO().find(siteURL);
		task = new SpiderHttpURLTask(context, foundURL, site);
		task.prepare();
	}

	@Override
	public void execute() {
		if (task == null) {
			log.error("Must run prepare first !");
		} else {
			task.execute();
		}
	}

	//=======================
	//自己实现序列化方法
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
		out.writeObject(this.foundURL);
		out.writeObject(this.contextToString);
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		this.foundURL = (URL) in.readObject();
		this.contextToString = (String) in.readObject();
		this.context = contexts.get(contextToString);
		if (this.context == null) {
			throw new IOException("Not find context !");
		}
	}

	@Override
	public int getType() {
		return WorkerTask.WORKERTASK_SPIDERTASK;
	}

	@Override
	public void tearDown() {
		context.getAgent().flagDone(this);
		task.tearDown();
	}
}
