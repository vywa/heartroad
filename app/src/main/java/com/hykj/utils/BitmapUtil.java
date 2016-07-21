package com.hykj.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;

import com.hykj.Constant;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Log;

public class BitmapUtil {
	// 计算图片的缩放值
	private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}

	// 根据路径获得图片并压缩，返回bitmap用于显示
	public static Bitmap getSmallBitmap(String filePath) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, 800, 800);
//		Log.wtf("inSampleSize", options.inSampleSize + "");

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;

		return BitmapFactory.decodeFile(filePath, options);
	}

	/*
	 * 压缩图片，处理某些手机拍照角度旋转的问题
	 */
	public static String compressImage(String filePath, String targetPath) {

		Bitmap bm = getSmallBitmap(filePath);

		int degree = readPictureDegree(filePath);

		if (degree != 0) {// 旋转照片角度
			bm = rotateBitmap(bm, degree);
		}

		FileOutputStream out = null;
		try {
			out = new FileOutputStream(targetPath);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		bm.compress(Bitmap.CompressFormat.JPEG, 40, out);

		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return targetPath;
	}

	/**
	 * 
	 * @param path
	 * @return
	 */

	public static String writeBitmap(Bitmap bitmap, String targetPath) {

		File outputFile = new File(targetPath);

		FileOutputStream out = null;
		try {
			out = new FileOutputStream(outputFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

		return outputFile.getPath();
	}

	private static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	private static Bitmap rotateBitmap(Bitmap bitmap, int degress) {
		if (bitmap != null) {
			Matrix m = new Matrix();
			m.postRotate(degress);
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
			return bitmap;
		}
		return bitmap;
	}

	// 图片到byte数组
	public static byte[] image2byte(String path) {
		byte[] data = null;
		FileInputStream input = null;
		try {
			input = new FileInputStream(new File(path));
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			int numBytesRead = 0;
			while ((numBytesRead = input.read(buf)) != -1) {
				output.write(buf, 0, numBytesRead);
			}
			data = output.toByteArray();
			output.close();
			input.close();
		} catch (FileNotFoundException ex1) {
			ex1.printStackTrace();
		} catch (IOException ex1) {
			ex1.printStackTrace();
		}
		return data;
	}

	// byte数组到图片
	public static File byte2file(byte[] data, String pid) {
		if (data.length < 3 || pid.equals(""))
			return null;
		try {
			File tempFile = new File(Constant.DOWNLOAD_FILEPATH+"/temp");
			FileOutputStream imageOutput = new FileOutputStream(tempFile);
			imageOutput.write(data, 0, data.length);
			imageOutput.close();
			
			FileInputStream fileInputStream = new FileInputStream(tempFile);
			String end = getTypeByStream(fileInputStream);
			
			File file = new File(Constant.DOWNLOAD_FILEPATH+"/"+pid+end);
			tempFile.renameTo(file);
			
			return file;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static String getTypeByStream(FileInputStream is) {
		byte[] b = new byte[19];
		try {
			is.read(b, 0, b.length);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String type = bytesToHex(b).toUpperCase();

		if (type.contains("FFD8FF")) {
			return ".jpg";
		} else if (type.contains("89504E47")) {
			return ".png";
		} else if (type.contains("47494638")) {
			return ".gif";
		} else if (type.contains("49492A00")) {
			return ".tif";
		} else if (type.contains("424D")) {
			return ".bmp";
		} else if (type.contains("57415645")) {
			return ".wav";
		}else if(type.contains("66747970")){
			return ".arm";
		}
		return type;
	}
	
	
	private static final char[] bcdLookup = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	/** */
	/**
	 * 将字节数组转换为16进制字符串
	 * 
	 * @param bcd
	 * @return
	 */
	public static final String bytesToHex(byte[] bcd) {
		StringBuffer s = new StringBuffer(bcd.length * 2);

		for (int i = 0; i < bcd.length; i++) {
			s.append(bcdLookup[(bcd[i] >>> 4) & 0x0f]);
			s.append(bcdLookup[bcd[i] & 0x0f]);
		}

		return s.toString();
	}

	/** */
	/**
	 * 将16进制字符串转换为字节数组
	 * 
	 * @param s
	 * @return
	 */
	public static final byte[] hexToBytes(String s) {
		byte[] bytes;
		bytes = new byte[s.length() / 2];

		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = (byte) Integer.parseInt(s.substring(2 * i, 2 * i + 2), 16);
		}

		return bytes;
	}

	public final static String MD5(String s) {
		try {
			byte[] btInput = s.getBytes();
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			mdInst.update(btInput);
			byte[] md = mdInst.digest();
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < md.length; i++) {
				int val = ((int) md[i]) & 0xff;
				if (val < 16)
					sb.append("0");
				sb.append(Integer.toHexString(val));

			}
			return sb.toString();
		} catch (Exception e) {
			return null;
		}
	}
}
