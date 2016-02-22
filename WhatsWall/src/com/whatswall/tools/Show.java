package com.whatswall.tools;

import com.avos.avoscloud.AVException;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class Show {

	public static void showToast(Context mContext, String string) {

		Toast.makeText(mContext, string, Toast.LENGTH_SHORT).show();
	}

	public static void disposeError(Context mContext, String tag, AVException e) {
		if (mContext != null)
			showToast(mContext, "Error:" + getErrprString(e.getCode(), tag)
					+ "");
		Log.e(tag, e.toString() + "");
	}

	public static void disposeError(Context mContext, String tag, Exception e) {

		if (mContext != null)
			showToast(mContext, "Error:" + "-1");
		Log.e(tag, e.toString() + "");
	}

	public static void logInfo(Context mContext, String tag, String i) {

		Log.i(tag, i + "");
	}

	public static String getErrprString(int code, String tag) {
		switch (code) {
		case 100:
			return "��������ʧ�ܻ����粻�ȶ�!";
		case 127:
			return "��Ч���ֻ�����!";
		case 1:
			if (tag.equals("RegisterActivity"))
				return "��Ч����֤���������֤��̫Ƶ����!";
			else
				return "��������ʧ�ܻ����粻�ȶ�!";
		case 120:
			return "����û�л�����!";
		default:
			return code + "";
		}

	}
}
