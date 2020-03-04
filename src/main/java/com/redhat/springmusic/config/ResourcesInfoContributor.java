package com.redhat.springmusic.config;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.actuate.info.Info.Builder;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

@Component
public class ResourcesInfoContributor implements InfoContributor {
	private static final Logger LOGGER = LoggerFactory.getLogger(ResourcesInfoContributor.class);

	@Override
	public void contribute(Builder builder) {
		Runtime runtime = Runtime.getRuntime();
		Map<String, String> resourcesInfo = new LinkedHashMap<>();
		resourcesInfo.put("hostname", System.getenv().getOrDefault("HOSTNAME", "unknown"));
		resourcesInfo.put("# of processors", String.valueOf(runtime.availableProcessors()));
		resourcesInfo.put("Max memory", humanReadableByteCount(runtime.maxMemory(), false));
		resourcesInfo.put("Free memory", humanReadableByteCount(runtime.freeMemory(), false));
		resourcesInfo.put("Total memory", humanReadableByteCount(runtime.totalMemory(), false));
//		resourcesInfo.put("Memory", memory(runtime));

		builder.withDetail("resources", resourcesInfo);
	}

	private static String memory(Runtime rt) {
		LOGGER.info("Starting to allocate memory...");
		StringBuilder sb = new StringBuilder();
		long maxMemory = rt.maxMemory();
		long usedMemory = 0;

		try {
			while (((float) usedMemory / maxMemory) < 0.80) {
				sb.append(System.nanoTime() + sb.toString());
				usedMemory = rt.totalMemory();
			}
		}
		catch (OutOfMemoryError e){
			// Do nothing as we expect it to happen
		}
		finally{
			String msg = String.format(
				"Allocated more than 80%% (%s) of the max allowed JVM memory size (%s)",
				humanReadableByteCount(usedMemory, false),
				humanReadableByteCount(maxMemory, false)
			);
			LOGGER.info(msg);
			return msg;
		}
	}

	private static String humanReadableByteCount(long bytes, boolean si) {
		int unit = si ? 1000 : 1024;

		if (bytes < unit) {
			return bytes + " B";
		}

		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");

		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}
}
