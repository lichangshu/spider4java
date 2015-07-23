package net.javacoding.jspider.mod.plugin.diskwriter;

import net.javacoding.jspider.api.event.JSpiderEvent;
import net.javacoding.jspider.api.event.resource.ResourceFetchedEvent;
import net.javacoding.jspider.core.logging.Log;
import net.javacoding.jspider.core.logging.LogFactory;
import net.javacoding.jspider.core.util.config.*;
import net.javacoding.jspider.spi.Plugin;

import java.io.*;
import java.net.URL;
import java.util.StringTokenizer;

/**
 * $Id: DiskWriterPlugin.java,v 1.13 2003/04/09 17:08:14 vanrogu Exp $
 *
 * $Id: DiskWriterPlugin.java,v 1.13 2003/04/09 17:08:14 vanrogu Exp $
 */
public class DiskWriterPlugin implements Plugin {

	public static final String MODULE_NAME = "DiskWriter JSpider plugin";
	public static final String MODULE_VERSION = "v1.0";
	public static final String MODULE_DESCRIPTION = "A JSpider plugin that allows the mirroring of web resources";
	public static final String MODULE_VENDOR = "http://www.javacoding.net";

	public static final String OUTPUT_ABSOLUTE = "output.absolute";
	public static final String OUTPUT_FOLDER = "output.folder";
	public static final String EXTENSION_SUFFIX = "no.extension.suffix";
	public static final String REQUEST_PARAM_SUFFIX = "request.param.suffix";

	public static final String DEFAULT_OUTPUT_FOLDER = ".";

	protected Log log;

	protected File outputFolder;
	protected File baseFolder;
	protected String extension;
	protected String paramSuffix;

	protected PrintWriter pw;

	public DiskWriterPlugin(PropertySet config) {
		log = LogFactory.getLog(DiskWriterPlugin.class);
		JSpiderConfiguration jspiderConfig = ConfigurationFactory.getConfiguration();
		File defaultOutputFolder = jspiderConfig.getDefaultOutputFolder();
		extension = config.getString(EXTENSION_SUFFIX, "/index.html");
		paramSuffix = config.getString(REQUEST_PARAM_SUFFIX, ".html");
		if (config.getBoolean(OUTPUT_ABSOLUTE, false)) {
			outputFolder = new File(config.getString(OUTPUT_FOLDER, DEFAULT_OUTPUT_FOLDER));
			baseFolder = new File("/");
		} else {
			outputFolder = new File(defaultOutputFolder, config.getString(OUTPUT_FOLDER, DEFAULT_OUTPUT_FOLDER));
			baseFolder = new File(".");
		}
		log.info("Writing to output folder: " + outputFolder);
	}

	public void initialize() {
	}

	public void shutdown() {
	}

	public String getName() {
		return MODULE_NAME;
	}

	public String getVersion() {
		return MODULE_VERSION;
	}

	public String getDescription() {
		return MODULE_DESCRIPTION;
	}

	public String getVendor() {
		return MODULE_VENDOR;
	}

	public void notify(JSpiderEvent event) {
		if (event instanceof ResourceFetchedEvent) {
			ResourceFetchedEvent rfe = (ResourceFetchedEvent) event;
			URL url = rfe.getURL();
			String path = url.getHost() + url.getPath();
			String query = url.getQuery();
			if (query == null && !includesFile(url)) {
				path = url.getHost() + url.getPath() + extension;
			}
			if (query != null) {
				path += "_reqeust.param.is_" + query + paramSuffix;
			}
			File outputFile = new File(outputFolder, path);
			outputFile.getParentFile().mkdirs();//创建父目录
			try {
				String cfile = outputFile.getCanonicalPath();
				log.debug("Writing " + cfile);
				writeFile(outputFile, rfe.getResource().getInputStream());
				log.info("Wrote " + cfile);
			} catch (IOException ex) {
				log.error(outputFile, ex);
			}
		}
	}

	protected void writeFile(File outputFile, InputStream is) {
		try {
			FileOutputStream fos = new FileOutputStream(outputFile);
			int read = is.read();
			while (read != -1) {
				fos.write(read);
				read = is.read();
			}
			fos.close();
		} catch (IOException e) {
			log.error("i/o error writing file", e);
		}
	}

	protected void ensureFolder(File current, StringTokenizer st) {
		// last part will be a file, we don't want a folder created for that one!
		String thisToken = st.nextToken();
		if (st.hasMoreTokens()) {
			File thisFolder = new File(current, thisToken);
			if (!thisFolder.exists()) {
				thisFolder.mkdir();
			}
			ensureFolder(thisFolder, st);
		}
	}

	protected boolean includesFile(URL url) {
		String urlString = url.getPath();
		return (urlString.lastIndexOf(".") > urlString.lastIndexOf('/'));
	}

}
