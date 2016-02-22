package com.whatswall.tools;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.whatswall.base.App;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

/**
 * �����ļ���ͨ����
 * 
 * @author Administrator
 * 
 */
public class DisposeFile {

	@SuppressLint("SdCardPath")
	public final static String PATH_SDCARD = App.cacheDir + "/";

	public final static String PATH_SDCARD_IMG_USER = PATH_SDCARD + "Img/User";
	public final static String PATH_SDCARD_SAVE_IMG = Environment
			.getExternalStorageDirectory().getPath() + "/WhatsWall/Img";

	/**
	 * ����ͼƬ��ָ��·��
	 * 
	 * @param bm
	 * @param fileName
	 * @param path
	 * @throws IOException
	 */
	public static void saveJpgFile(Bitmap bm, String fileName, String path)
			throws IOException {

		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File dirFile = new File(path);
			if (!dirFile.exists()) {
				dirFile.mkdirs();
			}
			File myCaptureFile = new File(path, fileName);
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(myCaptureFile));
			bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
		} else {
			// SD������

		}

	}

	public static Bitmap getPngFile(String path, String name) {

		System.out.println(path + "/" + name);
		return BitmapFactory.decodeFile(path + "/" + name);
	}

	/**
	 * �ж��ļ����Ƿ���ڣ��������򴴽�
	 * 
	 * @param path
	 */
	public static void isExist(String path) {
		File file = new File(path);

		if (!file.exists()) {
			file.mkdirs();
		}

	}

	/**
	 * �ж�һ�������Ƿ���ڣ��Լ��Ƿ�����
	 * 
	 * @param path
	 * @param size
	 *            ��size=-1ʱ����������С�Ƚ�
	 * @return
	 */
	public static boolean isExist(String path, int size) {

		File file = new File(path);
		if (size == -1) {
			return file.exists();
		} else {
			if (file.exists() && file.length() == size)
				return true;
		}
		return false;
	}

	/**
	 * 
	 * @param fromFile
	 *            �����Ƶ��ļ�
	 * @param toFile
	 *            ���Ƶ�Ŀ¼�ļ�
	 * @param rewrite
	 *            ���ļ�����ʱ���Ƿ����´����ļ�
	 * 
	 *            <p>
	 *            �ļ��ĸ��Ʋ�������
	 */
	public static boolean copyFile(String fromFilePath, String toFilePath,
			Boolean rewrite) {

		File fromFile = new File(fromFilePath);
		File toFile = new File(toFilePath);

		if (!fromFile.exists()) {
			return false;
		}

		if (!fromFile.isFile()) {
			return false;
		}
		if (!fromFile.canRead()) {
			return false;
		}
		if (!toFile.getParentFile().exists()) {
			toFile.getParentFile().mkdirs();
		}
		if (toFile.exists() && rewrite) {
			toFile.delete();
		}
		// �õ����ļ�������
//		String name = fromFile.toString();
//		name = name.substring(name.lastIndexOf("/"));
//		toFile = new File(toFile.toString() + name);

		try {
			FileInputStream fosfrom = new FileInputStream(fromFile);
			FileOutputStream fosto = new FileOutputStream(toFile);

			byte[] bt = new byte[1024];
			int c;
			while ((c = fosfrom.read(bt)) > 0) {
				fosto.write(bt, 0, c);
			}
			fosfrom.close();
			fosto.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}
