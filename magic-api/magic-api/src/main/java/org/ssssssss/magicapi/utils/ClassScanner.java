package org.ssssssss.magicapi.utils;


import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.ssssssss.script.asm.ClassReader;
import org.ssssssss.script.functions.ObjectConvertExtension;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


/**
 * class 扫描器
 *
 * @author mxd
 */
public class ClassScanner {

	public static List<String> scan() throws URISyntaxException, IOException {
		Set<String> classes = new HashSet<>();
		if (Double.parseDouble(System.getProperty("java.specification.version")) >= 11) {
			classes.addAll(latestJdkScan());
		} else {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			do {
				if (loader instanceof URLClassLoader) {
					classes.addAll(scan(((URLClassLoader) loader).getURLs()));
				}
			} while ((loader = loader.getParent()) != null);
		}
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		do {
			if (loader instanceof URLClassLoader) {
				classes.addAll(scan(((URLClassLoader) loader).getURLs()));
			}
		} while ((loader = loader.getParent()) != null);
		classes.addAll(addJavaLibrary());
		return new ArrayList<>(classes);
	}

	public static List<String> latestJdkScan() throws IOException {
		ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
		Resource[] resources = resourcePatternResolver.getResources("classpath*:**/**.class");
		return Arrays.asList(resources).parallelStream().map(it -> {
			try {
				if (isClass(it.getURL().getPath())) {
					if ("\"classes\"".contains(it.getURL().getPath())) {
						return it.getURL().getPath().split("classes")[1].substring(1).replace(".class", "").replaceAll("\\/", ".");
					} else if (it.getURL().getPath().split("!").length > 1) {
						return it.getURL().getPath().split("!")[1].substring(1).replace(".class", "").replaceAll("\\/", ".");
					} else {
						try (InputStream stream = it.getInputStream()) {
							return new ClassReader(stream).getClassName().replace("/", ".");
						} catch (Exception e) {
							return null;
						}
					}
				}
			} catch (Exception e) {
				return null;
			}
			return null;
		}).filter(Objects::nonNull).distinct().sorted(Comparator.comparing(Objects::toString)).collect(Collectors.toList());
	}

	public static String compress(List<String> classes) {
		Collections.sort(classes);
		String currentPackage = "";
		StringBuffer buf = new StringBuffer();
		int classCount = 0;
		for (String fullName : classes) {
			String packageName = "";
			String className = fullName;
			if (fullName.contains(".")) {
				int index = fullName.lastIndexOf(".");
				className = fullName.substring(index + 1);
				packageName = fullName.substring(0, index);
			}
			if (className.equals("package-info")) {
				continue;
			}
			if (currentPackage.equals(packageName)) {
				if (classCount > 0) {
					buf.append(",");
				}
				buf.append(className);
				classCount++;
			} else {
				currentPackage = packageName;
				if (buf.length() > 0) {
					buf.append("\n");
				}
				buf.append(packageName);
				buf.append(":");
				buf.append(className);
				classCount = 1;
			}
		}
		return buf.toString();
	}

	private static Set<String> scan(URL[] urls) throws URISyntaxException {
		Set<String> classes = new HashSet<>();
		if (urls != null) {
			for (URL url : urls) {
				String protocol = url.getProtocol();
				if ("file".equalsIgnoreCase(protocol)) {
					String path = url.getPath();
					if (path.toLowerCase().endsWith(".jar")) {
						classes.addAll(scanJarFile(url));
					} else {
						classes.addAll(scanDirectory(new File(url.toURI()), null));
					}
				} else if ("jar".equalsIgnoreCase(protocol)) {
					classes.addAll(scanJarFile(url));
				}
			}
		}
		return classes;
	}

	private static Set<String> addJavaLibrary() {
		int version = checkJavaVersion();
		if (version >= 9) {
			return addJava9PlusLibrary();
		}
		return addJava8Library();
	}

	private static int checkJavaVersion() {
		String version = System.getProperty("java.version");
		int index = version.indexOf(".");
		if (index > -1) {
			String first = version.substring(0, index);
			if (!"1".equals(first)) {
				return ObjectConvertExtension.asInt(first, -1);
			} else {
				int endIndex = version.indexOf(".", index + 1);
				return ObjectConvertExtension.asInt(version.substring(index + 1, endIndex), -1);
			}
		}
		return -1;
	}

	/**
	 * jdk 8
	 */
	private static Set<String> addJava8Library() {
		try {
			// 直接反射调用..
			Object classpath = Class.forName("sun.misc.Launcher").getMethod("getBootstrapClassPath").invoke(null);
			return scan((URL[]) classpath.getClass().getMethod("getURLs").invoke(classpath));
		} catch (Exception ignored) {
		}
		return Collections.emptySet();
	}

	/**
	 * jdk 9+
	 */
	private static Set<String> addJava9PlusLibrary() {
		Set<String> classes = new HashSet<>();
		try {
			Class<?> moduleLayer = Class.forName("java.lang.ModuleLayer");
			Object boot = moduleLayer.getMethod("boot").invoke(null);
			Object configuration = moduleLayer.getMethod("configuration").invoke(boot);
			//Set<ResolvedModule>
			Set<?> modules = (Set<?>) Class.forName("java.lang.module.Configuration").getMethod("modules").invoke(configuration);
			Method reference = Class.forName("java.lang.module.ResolvedModule").getMethod("reference");
			Method open = Class.forName("java.lang.module.ModuleReference").getMethod("open");
			Method list = Class.forName("java.lang.module.ModuleReader").getMethod("list");
			modules.forEach(module -> {
			});
			for (Object module : modules) {
				Object ref = reference.invoke(module);
				try (Closeable reader = (Closeable) open.invoke(ref)) {
					@SuppressWarnings("unchecked")
					Stream<String> stream = (Stream<String>) list.invoke(reader);
					stream.filter(ClassScanner::isClass).forEach(className -> classes.add(className.substring(0, className.length() - 6).replace("/", ".")));
				} catch (IOException ignored) {
				}
			}
		} catch (Exception ignored) {
		}
		return classes;
	}

	private static List<String> scanDirectory(File dir, String packageName) {
		File[] files = dir.listFiles();
		List<String> classes = new ArrayList<>();
		if (files != null) {
			for (File file : files) {
				String name = file.getName();
				if (file.isDirectory()) {
					classes.addAll(scanDirectory(file, packageName == null ? name : packageName + "." + name));
				} else if (name.endsWith(".class") && !name.contains("$")) {
					classes.add(filterFullName(packageName + "." + name.substring(0, name.length() - 6)));
				}
			}
		}
		return classes;
	}

	private static String filterFullName(String fullName) {
		if (fullName.startsWith("BOOT-INF.classes.")) {
			fullName = fullName.substring(17);
		}
		return fullName;
	}

	private static List<String> scanJarFile(URL url) {
		List<String> classes = new ArrayList<>();
		try (ZipInputStream zis = new ZipInputStream(url.openStream())) {
			ZipEntry entry;
			while ((entry = zis.getNextEntry()) != null) {
				if (!entry.getName().contains("META-INF")) {
					String className = entry.getName();
					if (isClass(className)) {
						classes.add(filterFullName(className.substring(0, className.length() - 6).replace("/", ".")));
					}
				}
			}
		} catch (IOException ignored) {

		}
		return classes;
	}

	private static boolean isClass(String className) {
		return className.endsWith(".class") && !className.contains("$") && !className.contains("module-info");
	}
}
