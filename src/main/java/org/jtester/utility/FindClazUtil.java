package org.jtester.utility;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * 
 * @author darui.wudr
 * 
 */
public class FindClazUtil {
	/**
	 * 判断应用程序是否运行在IDE容器里
	 * 
	 * @param clazPath
	 * @return
	 */
	protected static boolean isAppRunningInIde(String clazPath) {
		boolean in_ide = false;

		StringTokenizer tokenizer = new StringTokenizer(clazPath, File.pathSeparator);

		while (tokenizer.hasMoreTokens()) {
			String entry = (String) tokenizer.nextToken();

			if (entry.endsWith(".jar") == false) {
				in_ide = true;
				break;
			}
		}
		return in_ide;
	}

	/**
	 * 判断App是否是已jar方式运行的
	 * 
	 * @param clazPath
	 * @return
	 */
	protected static boolean isAppRunningInJar(String clazPath) {
		boolean result = false;

		StringTokenizer tokenizer = new StringTokenizer(clazPath, File.pathSeparator);

		if (tokenizer.countTokens() == 1 && clazPath.endsWith(".jar")) {
			result = true;
		}
		return result;
	}

	/**
	 * 
	 * @param clazPath
	 * @param packPath
	 * @return
	 * @throws IOException
	 */
	protected static List<String> findClazzInIdeApp(String clazPath, String packPath) throws IOException {
		List<String> clazzes = new LinkedList<String>();
		StringTokenizer tokenizer = new StringTokenizer(clazPath, File.pathSeparator);
		while (tokenizer.hasMoreTokens()) {
			String entry = tokenizer.nextToken();

			if (entry.endsWith(".jar")) {
				clazzes.addAll(findClazzInJarFile(new JarFile(entry), packPath));
			} else {
				clazzes.addAll(findClazzInIdeTarget(entry, packPath));
			}
		}

		return clazzes;
	}

	/**
	 * 获得所有jar包中package下面所有的class
	 * 
	 * @param clazPath
	 * @param packPath
	 * @return
	 * @throws IOException
	 */
	protected static List<String> findClazzInJarApp(String jarName, String packPath) throws IOException {
		List<String> clazzes = new LinkedList<String>();

		JarFile jarFile = new JarFile(jarName);
		clazzes.addAll(findClazzInJarFile(jarFile, packPath));

		Manifest manifest = jarFile.getManifest();
		if (manifest == null) {
			return clazzes;
		}

		String jarClassPath = manifest.getMainAttributes().getValue(Attributes.Name.CLASS_PATH);
		StringTokenizer tokenizer = new StringTokenizer(jarClassPath, " ");

		while (tokenizer.hasMoreTokens()) {
			String jarFileName = (String) tokenizer.nextToken();
			clazzes.addAll(findClazzInJarFile(new JarFile(jarFileName), packPath));
		}

		return clazzes;
	}

	/**
	 * 在IDE的编译路径中获取packPath下所有的class
	 * 
	 * @param target
	 * @param pathPath
	 * @return
	 */
	protected static List<String> findClazzInIdeTarget(String target, String packPath) {
		List<String> clazzes = new LinkedList<String>();
		String dir = target + File.separator + packPath.replace('.', File.separatorChar);
		File directory = new File(dir);
		String[] classnames = directory.list();
		if (classnames == null) {
			return clazzes;
		}

		for (String clazname : classnames) {
			if (clazname.endsWith(".class") == false) {
				continue;
			}
			String claz = packPath + "." + clazname.substring(0, clazname.indexOf('.'));
			clazzes.add(claz);
		}
		return clazzes;
	}

	/**
	 * 获得jar文件中package下面所有的class
	 * 
	 * @param aJarFile
	 * @param aPackage
	 * @return
	 */
	private static List<String> findClazzInJarFile(JarFile aJarFile, String packPath) {
		List<String> clazzes = new LinkedList<String>();
		Enumeration<JarEntry> jarEntries = aJarFile.entries();

		while (jarEntries.hasMoreElements()) {
			String clazName = ((JarEntry) jarEntries.nextElement()).getName();
			if (FindClazUtil.clazInPack(clazName, packPath)) {
				clazzes.add(clazName);
			}
		}
		return clazzes;
	}

	/**
	 * 判断clazName是否在packPath路径下
	 * 
	 * @param clazName
	 * @param packPath
	 * @return
	 */
	private static boolean clazInPack(String clazName, String packPath) {
		String a = FindClazUtil.pathReplace(packPath);
		String b = FindClazUtil.getPackPath(clazName);
		return a.equals(b);
	}

	/**
	 * 获得clazName的package
	 * 
	 * @param clazName
	 * @return
	 */
	private static String getPackPath(String clazName) {
		String packName = "";
		String classname = FindClazUtil.pathReplace(clazName);
		int index = classname.lastIndexOf('.');

		if (index != -1) {
			packName = classname.substring(0, index);
		}
		return packName;
	}

	/**
	 * 替换路径中所有的/\为.
	 * 
	 * @param path
	 * @return
	 */
	protected static String pathReplace(String path) {
		return path.replace('/', '.').replace('\\', '.');
	}

	/**
	 * 获得packPath路径下所有的class
	 * 
	 * @param packPath
	 * @return
	 */
	public static List<String> findClazz(String packPath) {
		String classPath = System.getProperty("java.class.path");
		try {
			List<String> clazzes = null;
			if (FindClazUtil.isAppRunningInIde(classPath)) {
				clazzes = FindClazUtil.findClazzInIdeApp(classPath, packPath);
			} else {
				clazzes = FindClazUtil.findClazzInJarApp(classPath, packPath);
			}
			return clazzes;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 获得的与claz的package相同的所有class
	 * 
	 * @param claz
	 * @return
	 */
	public static List<String> findClazz(Class<?> claz) {
		String pack = claz.getPackage().getName();
		return FindClazUtil.findClazz(pack);
	}
}
