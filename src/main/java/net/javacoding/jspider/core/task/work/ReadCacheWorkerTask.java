/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.javacoding.jspider.core.task.work;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.javacoding.jspider.core.SpiderContext;
import net.javacoding.jspider.core.task.WorkerTask;

/**
 *
 * @author changshu.li
 */
public class ReadCacheWorkerTask extends BaseWorkerTaskImpl {

	private final File file;
	private static Map<String, ReadCacheWorkerTask> map = new ConcurrentHashMap();

	/**
	 * each SpiderContext must have different cache dir !!
	 *
	 * @param context
	 * @param cachedir
	 * @return
	 */
	public static ReadCacheWorkerTask getCacheFlagWorkerTask(SpiderContext context, File cachedir) {
		if (map.get(context.toString()) == null) {
			synchronized (ReadCacheWorkerTask.class) {
				if (map.get(context.toString()) == null) {
					map.put(context.toString(), new ReadCacheWorkerTask(context, cachedir));
				}
			}
		}
		return map.get(context.toString());
	}

	private ReadCacheWorkerTask(SpiderContext context, File cachedir) {
		super(context, WorkerTask.WORKERTASK_SPIDERTASK);
		this.file = cachedir;
	}

	@Override
	public void prepare() {
	}

	@Override
	public void execute() {
		try {
			String ff = file.getCanonicalPath();
			synchronized (ff) {

			}
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		this.context.getAgent().registerEvent(null, null);
	}

	/**
	 * Not delete empty directory!
	 *
	 * @param file
	 * @param i
	 * @return
	 * @throws IOException
	 */
	private static List<String> readAndDeleteFile(File file) throws IOException {
		List<String> list = Collections.EMPTY_LIST;
		if (file.isDirectory()) {
			File[] ffs = file.listFiles();
			for (File f : ffs) {
				if (f.isDirectory()) {
					list = readAndDeleteFile(file);
					if (!list.isEmpty()) {//Is empty contine read !
						break;
					}
				}
			}
		} else {
			return Collections.singletonList(file.getCanonicalPath());
		}
		return list;
	}
}
