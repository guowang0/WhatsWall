package com.whatswall.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import com.whatswall.R;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.ProgressCallback;
import com.avos.avoscloud.SaveCallback;
import com.whatswall.base.C;
import com.whatswall.entity.Room;
import com.whatswall.tools.DisposeFile;
import com.whatswall.tools.DisposeImg;
import com.whatswall.tools.Show;
import com.whatswall.tools.Time;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

public class RoomPublishActivity extends BaseActivity {

	private Room room = null;
	private EditText edit;
	private ImageView image;
	private Switch switchAnon;
	private ArrayList<Bitmap> bitmaps = null;
	private AVUser currentUser = null;

	private boolean isChooseImag = false;

	private final String TAG = "RoomPublishActivity";
	private final int maxWidth = 500;

	public static final String KEY_PHOTO_PATH = "photo_path";
	private String path = DisposeFile.PATH_SDCARD + "Img/Photo/";
	public String photoName = "photo.jpg";

	// ������
	private static final int IMAGE_REQUEST_CODE = 0;
	private static final int PHOTO_REQUEST_CODE = 1;
	private static final int IMAGE_SCALE = 2;

	private ProgressDialog pd = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_roompublish);
		setActionBarLayout(R.layout.actionbar_layout_publish);

		room = (Room) getIntent().getExtras().getSerializable("room");
		bitmaps = new ArrayList<>();
		currentUser = AVUser.getCurrentUser();

		ImageButton back = (ImageButton) findViewById(R.id.back);
		// Button publish = (Button) findViewById(R.id.publish_publish);
		edit = (EditText) findViewById(R.id.publish_edit);
		image = (ImageView) findViewById(R.id.publish_img);
		switchAnon = (Switch) findViewById(R.id.publish_switch);
		Button publish = (Button) findViewById(R.id.finish);
		TextView title = (TextView) findViewById(R.id.title);

		title.setText(R.string.textview_publish_title);

		File file = new File(path);
		if (!file.exists() && !file.isDirectory()) {
			file.mkdirs();
		}

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		image.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (!isChooseImag) {

					new AlertDialog.Builder(RoomPublishActivity.this)
							.setItems(
									new String[] { "������Ƭ", "���ͼƬ" },
									new android.content.DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {

											switch (which) {
											case 0:
												Intent intent = new Intent(
														MediaStore.ACTION_IMAGE_CAPTURE);
												intent.putExtra(
														MediaStore.EXTRA_OUTPUT,
														Uri.fromFile(new File(
																path
																		+ photoName)));
												startActivityForResult(intent,
														PHOTO_REQUEST_CODE);
												break;
											case 1:
												Intent i = new Intent(
														Intent.ACTION_PICK,
														android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

												startActivityForResult(i,
														IMAGE_REQUEST_CODE);
												break;
											default:
												break;
											}
										}
									}).create().show();
				} else {
					Intent intent = new Intent();
					intent.setClass(RoomPublishActivity.this,
							ImageViewPhotoActivity.class);
					intent.putExtra("bitmap", path + photoName);
					startActivityForResult(intent, IMAGE_SCALE);

					overridePendingTransition(R.anim.scale_, R.anim.alpha_);
				}
			}
		});

		publish.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				pd = ProgressDialog.show(RoomPublishActivity.this, null,
						"���Ժ�..", true);
				publish();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode != RESULT_CANCELED) {
			switch (requestCode) {
			case IMAGE_REQUEST_CODE:
				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaColumns.DATA };
				Cursor cursor = getContentResolver().query(selectedImage,
						filePathColumn, null, null, null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String picturePath = cursor.getString(columnIndex);
				cursor.close();
				// Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
				Bitmap bitmap = disposeBitmapSize(picturePath);
				Show.logInfo(getApplication(), TAG, picturePath);
				bitmaps.clear();
				bitmaps.add(bitmap);
				image.setImageBitmap(bitmap);
				isChooseImag = true;
				// LayoutParams params = image.getLayoutParams();
				// params.width = 300;
				// params.height = 300;
				// image.setLayoutParams(params);
				break;
			case PHOTO_REQUEST_CODE:
				Bitmap bitmap1 = disposeBitmapSize(path + photoName);
				Show.logInfo(getApplication(), TAG, path + photoName);
				bitmaps.clear();
				bitmaps.add(bitmap1);
				image.setImageBitmap(bitmap1);
				isChooseImag = true;
				// LayoutParams params1 = image.getLayoutParams();
				// params1.width = 300;
				// params1.height = 300;
				// image.setLayoutParams(params1);
				break;
			case IMAGE_SCALE:
				System.out.println("******");
				bitmaps.clear();
				image.setImageResource(R.drawable.addimg);
				isChooseImag = false;
				break;
			default:
				break;
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	private void publish() {
		if (currentUser == null) {
			Show.showToast(getApplication(), "�㻹û�е�¼!");
		} else {
			if ((edit.getText() == null || edit.getText().toString().equals(""))
					&& bitmaps.size() == 0) {
				Show.showToast(getApplication(), "����Ϊ��!");
				pdDismiss(pd);
			} else if (bitmaps.size() == 0) {
				// ��������
				publishText();
			} else if (edit.getText() == null
					|| edit.getText().toString().equals("")) {
				// ����ͼƬ
				publishImg(false);
			} else {
				// ����ͼƬ������
				publishImg(true);
			}
		}
	}

	private void publishText() {
		AVObject avObject = new AVObject(C.ClASS_CONTENT);
		avObject.put(C.CONTENT_CONTENTTYPE, C.CONTENT_TYPE_ONLYTEXT);
		avObject.put(C.CONTENT_USER, currentUser);
		avObject.put(C.CONTENT_ROOMID, room.getRoomId());
		avObject.put(C.CONTENT_ISANON, getSwitch());
		avObject.put(C.CONTENT_CONTENT, getContent());
		avObject.saveInBackground(new SaveCallback() {

			@Override
			public void done(AVException e) {
				if (null == e)
					finishPublish();
				else {
					Show.showToast(getApplication(), "����ʧ��,���Ժ�����!");
					Show.disposeError(getApplication(), TAG, e);
				}
			}
		});
	}

	private void publishImg(final boolean isText) {

		byte[] bytes = DisposeImg.getJpgBitmapBytes(bitmaps.get(0));
		final AVFile avFile = new AVFile(room.getNumber(), bytes);
		avFile.addMetaData("width", bitmaps.get(0).getWidth());
		avFile.addMetaData("height", bitmaps.get(0).getHeight());
		avFile.saveInBackground(new SaveCallback() {

			@Override
			public void done(AVException e) {
				if (null == e) {
					AVQuery<AVObject> query = new AVQuery<>("_File");
					query.whereEqualTo("objectId", avFile.getObjectId());
					query.findInBackground(new FindCallback<AVObject>() {

						@Override
						public void done(List<AVObject> arg0, AVException e) {

							if (null == e) {
								AVObject avObject = new AVObject(
										C.ClASS_CONTENT);
								JSONArray array = new JSONArray();
								array.put(avFile.getObjectId());
								AVRelation<AVObject> relation = avObject
										.getRelation(C.CONTENT_IMGRELATION);
								relation.add(arg0.get(0));
								avObject.put(C.CONTENT_USER, currentUser);
								avObject.put(C.CONTENT_IMG, array);
								avObject.put(C.CONTENT_ROOMID, room.getRoomId());
								avObject.put(C.CONTENT_ISANON, getSwitch());
								avObject.put(C.CONTENT_CONTENT, getContent());
								JSONArray array2 = new JSONArray();
								array2.put(bitmaps.get(0).getWidth());
								array2.put(bitmaps.get(0).getHeight());
								avObject.put(C.CONTENT_IMGWIDTHHEIGHT, array2);
								if (isText) {
									avObject.put(C.CONTENT_CONTENTTYPE,
											C.CONTENT_TYPE_TEXTANDIMG);
									avObject.put(C.CONTENT_CONTENT,
											getContent());
								} else {
									avObject.put(C.CONTENT_CONTENTTYPE,
											C.CONTENT_TYPE_ONLYIMG);
								}
								avObject.saveInBackground(new SaveCallback() {

									@Override
									public void done(AVException e) {
										if (null == e)
											finishPublish();
										else {
											Show.showToast(getApplication(),
													"����ʧ��,���Ժ�����!");
											Show.disposeError(getApplication(),
													TAG, e);
										}
									}
								});
							} else {
								pdDismiss(pd);
								Show.disposeError(getApplication(), TAG, e);
							}
						}
					});

				} else {
					Show.showToast(getApplication(), "�ϴ�ͼƬʧ��,���Ժ�����!");
					Show.disposeError(getApplication(), TAG, e);
				}

			}
		}, new ProgressCallback() {

			@Override
			public void done(Integer arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	private boolean getSwitch() {
		return switchAnon.isChecked();
	}

	private String getContent() {
		return edit.getText().toString();
	}

	private void finishPublish() {

		pd.dismiss();
		finish();
	}

	public void pdDismiss(ProgressDialog pd) {
		try {
			pd.dismiss();
		} catch (Exception e) {

		}
	}

	/**
	 * ��ȡ������ͼƬ����С������
	 * 
	 * @param path
	 * @return
	 */
	public Bitmap disposeBitmapSize(String path1) {

		Bitmap bitmap = DisposeImg.decodeSampledBitmapFromFile(path1, maxWidth);
		bitmap = degreeBitmap(path1, bitmap);
		// float width = bitmap.getWidth();
		// float height = bitmap.getHeight();
		// float n = 1;
		// if (width > maxWidth) {
		// n = width / maxWidth;
		// System.out.println("n=" + n);
		// width = maxWidth;
		// height = height / n;
		// }
		// bitmap = Bitmap.createScaledBitmap(bitmap, (int) width, (int) height,
		// true);
		// Show.logInfo(getApplication(), TAG,
		// "bitmap.getWidth()=" + bitmap.getWidth()
		// + "bitmap.getHeight()=" + bitmap.getHeight());

		try {
			DisposeFile.saveJpgFile(bitmap, photoName, path);
		} catch (IOException e) {

			e.printStackTrace();
		}

		return bitmap;
	}

	/**
	 * ����ͼƬ�ķ���
	 * 
	 * @param path
	 * @param bitmap
	 * @return
	 */
	public static Bitmap degreeBitmap(String path, Bitmap bitmap) {
		// ����ͼƬ��filepath��ȡ��һ��ExifInterface�Ķ���

		ExifInterface exif = null;

		try {

			exif = new ExifInterface(path);

		} catch (IOException e) {

			e.printStackTrace();

			exif = null;

		}

		int degree = 0;

		if (exif != null) {

			// ��ȡͼƬ�����������Ϣ

			int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,

			ExifInterface.ORIENTATION_UNDEFINED);

			// ������ת�Ƕ�

			switch (ori) {

			case ExifInterface.ORIENTATION_ROTATE_90:

				degree = 90;

				break;

			case ExifInterface.ORIENTATION_ROTATE_180:

				degree = 180;

				break;

			case ExifInterface.ORIENTATION_ROTATE_270:

				degree = 270;

				break;

			default:

				degree = 0;

				break;

			}
			if (degree != 0) {

				// ��תͼƬ

				Matrix m = new Matrix();

				m.postRotate(degree);

				bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),

				bitmap.getHeight(), m, true);

			}
		}
		return bitmap;
	}
}
