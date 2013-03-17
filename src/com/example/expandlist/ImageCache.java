package com.example.expandlist;

import java.io.File;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.util.Log;


public class ImageCache {

	private static final String TAG = "ImageCache";
	private static ImageCache imageCache;
	private Context context;
	// 开辟8M硬缓存空间
	private final int hardCachedSize = 6 * 1024 * 1024;
	// 软引用
	private final int MAX_DOWNLOAD_THREAD = 5;// 同时下载图片的线程数量
	private static final int SOFT_CACHE_CAPACITY = 1;
	private final File imageDir;
	private final LinkedList<Task> downloadQueue;
	private BitmapFactory.Options factory;

	private static int downloadCount = 0;
	private int taskSize;
	private Bitmap defaultBitmap;

	private ImageCache(Context context) {
		super();
		this.context = context;
		downloadQueue = new LinkedList<Task>();
		imageDir = new File(Constants.PATH_LOCAL_IMAGE_LIBRARY);
		new ImageAsyncTask().execute((Void) null);
		factory = new BitmapFactory.Options();
		factory.inJustDecodeBounds = true;// 当为true时 允许查询图片不为 图片像素分配内存
		defaultBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
	}

	public static ImageCache getInstance(Context context) {
		if (imageCache == null) {
			imageCache = new ImageCache(context);

		}
		return imageCache;
	}

	// hard cache
	private final LruCache<String, BitmapCache> sHardBitmapCache = new LruCache<String, BitmapCache>(hardCachedSize) {
		@Override
		public int sizeOf(String key, BitmapCache value) {
			return value.bitmap.getRowBytes() * value.bitmap.getHeight();
			// return value.getRowBytes() * value.getHeight();
		}

		@Override
		protected void entryRemoved(boolean evicted, String key, BitmapCache oldValue, BitmapCache newValue) {
			Log.i(TAG, "hard cache is full , push to soft cache");
			// 硬引用缓存区满，将一个最不经常使用的oldvalue推入到软引用缓存区
			// if (evicted) {
			// remove(key);
			// if (oldValue != null && !oldValue.bitmap.isRecycled()) {
			// oldValue.bitmap.recycle();
			// }
			// }
			// sSoftBitmapCache.put(key, new SoftReference<BitmapCache>(oldValue));
		}
	};

	private final static LinkedHashMap<String, SoftReference<BitmapCache>> sSoftBitmapCache = new LinkedHashMap<String, SoftReference<BitmapCache>>(
			SOFT_CACHE_CAPACITY, 0.75f, true) {
		private static final long serialVersionUID = 4780156896636613685L;

		@Override
		protected boolean removeEldestEntry(Entry<String, SoftReference<BitmapCache>> eldest) {
			if (size() > SOFT_CACHE_CAPACITY) {
				Log.i(TAG, "Soft Reference limit , purge one");
				String key = eldest.getKey();

				SoftReference<BitmapCache> srf = eldest.getValue();
				if (srf != null) {
					BitmapCache bc = srf.get();
					if (bc != null) {
						Bitmap bitmap = bc.bitmap;
						if (bitmap != null && !bitmap.isRecycled()) {
							sSoftBitmapCache.remove(key);
							bitmap.recycle();
							Log.i(TAG, "recycled bitmap :" + key);

						}
					}
				}
				return true;
			}
			return false;
		}

	};

	// 缓存bitmap
	private boolean putBitmap(String key, BitmapCache bitmap) {
		if (bitmap != null) {
			synchronized (sHardBitmapCache) {
				sHardBitmapCache.put(key, bitmap);
			}
			return true;
		}
		return false;
	}

	/**
	 * 从缓存中获取bitmap
	 * 
	 * @param key
	 * @return
	 */
	private BitmapCache getBitmap(String key) {
		synchronized (sHardBitmapCache) {
			final BitmapCache bitmap = sHardBitmapCache.get(key);
			if (bitmap != null && !bitmap.bitmap.isRecycled())
				return bitmap;
		}
		// 硬引用缓存区间中读取失败，从软引用缓存区间读取
		synchronized (sSoftBitmapCache) {
			SoftReference<BitmapCache> bitmapReference = sSoftBitmapCache.get(key);
			if (bitmapReference != null) {
				final BitmapCache bitmap2 = bitmapReference.get();
				if (bitmap2 != null && !bitmap2.bitmap.isRecycled())

					return bitmap2;
				else {
					Log.v(TAG, "soft reference 已经被回收");
					sSoftBitmapCache.remove(key);
				}
			}
		}

		return null;
	}

	public void removeBitmap(String key) {
		sHardBitmapCache.remove(key);
	}

	/*private boolean downloadImage(String fileName, int sizeType) {
		boolean result = false;
		final String url = Constants.URL_PHOTO_PREFIX + fileName + "?sizeType=" + sizeType;
		final File file = new File(imageDir.getAbsolutePath() + "/" + fileName);

		if (!imageDir.exists()) {
			imageDir.mkdirs();
		}
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		final HKMallHttpClient client = new HKMallHttpClient(url, context);
		InputStream inputStream = null;
		try {
			inputStream = client.get();
			if (inputStream != null) {
				IOUtil.saveInputStreamAsFile(inputStream, file);
				result = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			IOUtil.closeIOStream(inputStream);
		}
		return result;
	}*/

	private boolean isImageExist(String fileName) {
		final File file = new File(imageDir.getAbsoluteFile() + "/" + fileName);
		return file.exists() && file.length() > 0;
	}

	/**
	 * 加载图片,如果加载失败则用默认图片
	 * 
	 * @param fileName
	 * @param listener
	 * @return
	 */
	public Bitmap loadImageWithDefault(String fileName, ImageCacheListener listener) {
		Bitmap result = loadImage(fileName, 1, listener, null);

		return result != null ? result : defaultBitmap;
	}
	
	/**
	 * 加载图片,如果加载失败则用默认图片
	 * 
	 * @param fileName
	 * @param listener
	 * @return
	 */
	public Bitmap loadImageWithDefault(String fileName,Options options, ImageCacheListener listener) {
		Bitmap result = loadImage(fileName, 1, listener, options);

		return result != null ? result : defaultBitmap;
	}

	public Bitmap loadImage(String fileName, ImageCacheListener listener) {
		return loadImage(fileName, 1, listener, null);
	}

	public Bitmap loadImage(String fileName, Options options, ImageCacheListener listener) {
		return loadImage(fileName, 1, listener, options);
	}

	/**
	 * 直接从缓存加载图片
	 * 
	 * @param fileName
	 * @return
	 */
	public Bitmap loadImageFromCache(String fileName) {
		BitmapCache bitmap = null;
		if (fileName == null) {
			return null;
		}
		bitmap = getBitmap(fileName);
		if (bitmap != null && !bitmap.bitmap.isRecycled()) {
			return bitmap.bitmap;
		} else {
			return null;
		}
	}

	/**
	 * 直接从缓存加载图片
	 * 
	 * @param fileName
	 * @return
	 */
	public Bitmap loadImageFromCacheWithDefault(String fileName) {
		BitmapCache bitmap = null;
		if (fileName == null) {
			return defaultBitmap;
		}
		bitmap = getBitmap(fileName);
		if (bitmap != null && !bitmap.bitmap.isRecycled()) {
			return bitmap.bitmap;
		} else {
			return defaultBitmap;
		}
	}

	public Bitmap loadImage(String fileName, int sizeType, ImageCacheListener listener, Options options) {
		Bitmap bitmap = null;
		BitmapCache bitmapCache = null;
		if (fileName == null) {
			return null;
		}
		bitmapCache = getBitmap(fileName);
		if (bitmapCache != null && bitmapCache.equals(options)) {
			Log.v(TAG, "get bitmap from bitmap cache!");
			return bitmapCache.bitmap;
		} else {
			if (isImageExist(fileName)) {
				Log.v(TAG, "Load image from file. " + fileName);
				String path = imageDir.getAbsolutePath() + "/" + fileName;
				int hRatio = 0;
				int wRatio = 0;

				factory.inJustDecodeBounds = true;
				bitmap = BitmapFactory.decodeFile(path, factory);
				if (options != null) {
					Log.d(TAG, options.toString());
					if (options.ratio == -1) {
						if (options.height > 0) {
							hRatio = (int) Math.ceil(factory.outHeight / options.height); // 图片是高度的几倍
						}
						if (options.width > 0) {
							wRatio = (int) Math.ceil(factory.outWidth / options.width); // 图片是宽度的几倍
						}

						if (hRatio > 1 || wRatio > 1) {
							if (hRatio > wRatio) {
								factory.inSampleSize = hRatio;
							} else
								factory.inSampleSize = wRatio;
						}
					} else {
						factory.inSampleSize = options.ratio;

					}

				} else {
					factory.inSampleSize = 0;
				}
				factory.inJustDecodeBounds = false;
				Log.d(TAG, "inSampleSize:" + factory.inSampleSize + " bitmap width:" + factory.outWidth + " factory outHeight:" + factory.outHeight);
				bitmap = BitmapFactory.decodeFile(path, factory);
				if (bitmap != null) {
					bitmapCache = new BitmapCache();
					bitmapCache.bitmap = bitmap;
					bitmapCache.options = options;
					putBitmap(fileName, bitmapCache);
				} else {
					// bitmap not invalid , deleted local file and retry download again bitmap
					File file = new File(imageDir.getAbsolutePath() + "/" + fileName);
					file.delete();
					if (!compareTask(fileName)) {
						addTask(fileName, sizeType, listener, options);
					}
				}
			} else {
				if (!compareTask(fileName)) {
					addTask(fileName, sizeType, listener, options);
				}
			}

		}

		return bitmap;
	}

	private class ImageAsyncTask extends AsyncTask<Void, Task, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			while (true) {
				// Log.i(TAG, "doInBackground");
				// Log.i(TAG, "task size:" + taskSize);
				Task task = null;
				synchronized (downloadQueue) {
					taskSize = downloadQueue.size();
					if (taskSize > 0 && downloadCount < MAX_DOWNLOAD_THREAD) {
						task = downloadQueue.removeLast();
					}
				}
				if (task != null) {
					new DownLoadThread(task).setTaskFinishListener(new TaskFinishListener() {

						@Override
						public void onFinish(Task task) {
							downloadCount--;
							publishProgress(task);
							Log.d(TAG, "file: " + task.fileName + " onFinish isSuccess:" + task.isSuccess);

						}
					}).start();
				}

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		protected void onProgressUpdate(Task... values) {
			Task task = values[0];
			if (task != null && task.isSuccess && task.listener != null) {
				Log.v(TAG,"call back onDownLoadSuccess listener!");
				task.listener.onDownLoadSuccess(task.fileName, task.options);
			}
			super.onProgressUpdate(values);
		}

	}

	public interface ImageCacheListener {
		public void onDownLoadSuccess(String fileName, Options options);
	}

	public class DownLoadThread extends Thread {
		private Task task;
		private TaskFinishListener taskFinishListener;

		public DownLoadThread(Task task) {
			super();
			this.task = task;
			downloadCount++;
		}

		@Override
		public void run() {
			//task.isSuccess = downloadImage(task.fileName, task.sizeType);
			taskFinishListener.onFinish(task);
			super.run();
		}

		public Task getTask() {
			return task;
		}

		public DownLoadThread setTaskFinishListener(TaskFinishListener taskFinishListener) {
			this.taskFinishListener = taskFinishListener;
			return this;
		}

	}

	public interface TaskFinishListener {
		public void onFinish(Task task);
	}

	public void addTask(String name, int sizeType, ImageCacheListener listener, Options options) {
		Task task = new Task();
		task.isSuccess = false;
		task.fileName = name;
		task.sizeType = sizeType;
		task.listener = listener;
		task.options = options;
		downloadQueue.add(task);
	}

	public void removeTask(Task task) {
		downloadQueue.remove(task);
	}

	/**
	 * 比较task中有无下载这个name的资源
	 * 
	 * @param name
	 * @return
	 */
	public boolean compareTask(String name) {
		for (int i = 0; i < downloadQueue.size(); i++) {
			if (downloadQueue.get(i).fileName.equals(name)) {
				return true;
			}
		}
		return false;
	}

	private class Task {
		public int sizeType = 1;
		public String fileName;
		public boolean isSuccess = false;
		public ImageCacheListener listener;
		public Options options;
	}

	public void clearAll() {
		Log.i(TAG, "clearAll");
		downloadQueue.clear();
		sHardBitmapCache.evictAll();
		sSoftBitmapCache.clear();
		System.gc();

	}

	/**
	 * decode bitmap size
	 * 
	 * @author JianFeng
	 * 
	 */
	public static class Options {
		public float width = -1;// pixel
		public float height = -1;// pixel
		public int ratio = -1;// 缩放比例

		@Override
		public String toString() {

			return "Options --> witdh:" + width + " height:" + height + " ratio:" + ratio;
		}

	}

	private class BitmapCache {
		public Options options;
		public Bitmap bitmap;

		public boolean equals(Options options) {
			if (options == null && this.options == null) {
				return true;
			} else if (options != null && this.options != null && options.height == this.options.height && options.width == this.options.width) {
				return true;
			} else {
				return false;
			}
		}
	}
}
