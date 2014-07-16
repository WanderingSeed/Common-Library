package com.morgan.library.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.nostra13.universalimageloader.core.assist.FlushedInputStream;

/**
 * This class implement IHttpSend interface, sample implement send request by
 * post or get method in both TCP and UDP Protocol.
 * 
 * @author Morgan.Ji
 */
public class HttpURLUtil {

	private static final String LOG = HttpURLUtil.class.getName();
	private static final String UTF_8 = "utf-8";
	public final static String SERVER_CONNECT_ERROR = "{code:0,message:\"服务器连接失败\"}";

	public String sendGetRequest(String requestUrl) {
		Logger.d("sendGetRequest url=", requestUrl);
		HttpURLConnection urlConnection = null;
		DataInputStream dataInputStream = null;
		InputStream is = null;
		StringBuffer sb = new StringBuffer();
		String responseBody = "";
		try {
			URL url = new URL(requestUrl);
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setConnectTimeout(20000);
			urlConnection.setReadTimeout(50000);

			int status = urlConnection.getResponseCode();
			if (status == HttpURLConnection.HTTP_OK) {
				is = urlConnection.getInputStream();
				dataInputStream = new DataInputStream(is);
				byte[] buffer = new byte[1024];
				int count = 0;
				String content = null;
				while ((count = dataInputStream.read(buffer)) != -1) {
					content = new String(buffer, 0, count, UTF_8);
					sb.append(content);
				}
				responseBody = sb.toString();
			}
		} catch (Exception e) {
			responseBody = SERVER_CONNECT_ERROR;
		} finally {
			if (dataInputStream != null) {
				try {
					dataInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
		}
		Logger.d(LOG, "get response: " + requestUrl + "/r/n" + responseBody);
		return responseBody;
	}

	public Bitmap getNetBitmap(String requestUrl) {
		Logger.d(LOG, "Bitmap get url : " + requestUrl);
		HttpURLConnection urlConnection = null;
		FlushedInputStream in = null;
		try {
			URL url = new URL(requestUrl);
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setConnectTimeout(20000);
			urlConnection.setReadTimeout(50000);
			int status = urlConnection.getResponseCode();
			if (status == HttpURLConnection.HTTP_OK) {
				in = new FlushedInputStream(urlConnection.getInputStream());
				return BitmapFactory.decodeStream(in);
			}
		} catch (Exception e) {
			Logger.e(LOG, "get bitmap error!", e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
		}
		return null;
	}

	public String sendPostRequest(String requestUrl, String content) {
		Logger.d("sendPostRequest", "url=" + requestUrl + ", content="
				+ content);
		HttpURLConnection urlConnection = null;
		BufferedInputStream bis = null;
		BufferedOutputStream out = null;
		InputStream is = null;
		String responseBody = null;
		StringBuffer sb = new StringBuffer();
		try {
			URL url = new URL(requestUrl);
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("POST");
			// Allow Inputs
			urlConnection.setDoInput(true);
			// Allow Outputs
			urlConnection.setDoOutput(true);
			// Don't use a cached copy.
			urlConnection.setUseCaches(false);
			urlConnection.setConnectTimeout(20000);
			urlConnection.setReadTimeout(50000);
			urlConnection
					.setRequestProperty("Content-Type", "application/json");
			out = new BufferedOutputStream(urlConnection.getOutputStream());
			out.write(content.getBytes(), 0, content.getBytes().length);
			out.flush();
			out.close();

			int status = urlConnection.getResponseCode();
			if (status == HttpURLConnection.HTTP_OK) {
				is = urlConnection.getInputStream();
				bis = new BufferedInputStream(is);
				byte[] buffer = new byte[1024];
				int count = 0;
				String temp;
				while ((count = bis.read(buffer)) != -1) {
					temp = new String(buffer, 0, count, UTF_8);
					sb.append(temp);
				}
				responseBody = sb.toString();
			}
		} catch (Exception e) {
			responseBody = SERVER_CONNECT_ERROR;
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
		}
		Logger.d(LOG, "get response: " + requestUrl + "/r/n" + responseBody);
		return responseBody;
	}

	/**
	 * @param url
	 *            Service net address
	 * @param params
	 *            text content
	 * @param files
	 *            pictures
	 * @return String result of Service response
	 * @throws IOException
	 */
	public String postFile(String url, Map<String, String> params,
			Map<String, File> files) {
		Logger.d("postFile", "url=" + url + " content" + params.toString());
		String BOUNDARY = java.util.UUID.randomUUID().toString();
		String PREFIX = "--", LINEND = "\r\n";
		String MULTIPART_FROM_DATA = "multipart/form-data";

		URL uri;
		String responseBody = null;
		HttpURLConnection conn = null;
		InputStream in = null;
		DataOutputStream outStream = null;
		try {
			uri = new URL(url);
			conn = (HttpURLConnection) uri.openConnection();
			conn.setReadTimeout(10 * 1000);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Charsert", UTF_8);
			conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA
					+ ";boundary=" + BOUNDARY);

			// params data
			StringBuilder headPara = new StringBuilder();
			for (Map.Entry<String, String> entry : params.entrySet()) {
				headPara.append(PREFIX);
				headPara.append(BOUNDARY);
				headPara.append(LINEND);
				headPara.append("Content-Disposition: form-data; name=\""
						+ entry.getKey() + "\"" + LINEND);
				headPara.append(LINEND);
				headPara.append(entry.getValue());
				headPara.append(LINEND);
			}

			outStream = new DataOutputStream(conn.getOutputStream());
			outStream.write(headPara.toString().getBytes());
			// file data
			InputStream is = null;
			if (files != null) {
				for (Map.Entry<String, File> file : files.entrySet()) {
					StringBuilder headFilePara = new StringBuilder();
					headFilePara.append(PREFIX);
					headFilePara.append(BOUNDARY);
					headFilePara.append(LINEND);
					headFilePara
							.append("Content-Disposition: form-data; name=\"flow\"; filename=\""
									+ file.getValue().getName() + "\"" + LINEND);
					headFilePara
							.append("Content-Type: application/octet-stream; charset="
									+ UTF_8 + LINEND);
					headFilePara.append(LINEND);
					outStream.write(headFilePara.toString().getBytes());

					try {
						is = new FileInputStream(file.getValue());
						byte[] buffer = new byte[1024];
						int len = 0;
						while ((len = is.read(buffer)) != -1) {
							outStream.write(buffer, 0, len);
						}
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						if (is != null) {
							is.close();
						}
					}
					outStream.write(LINEND.getBytes());
				}
			}

			// request end
			byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
			outStream.write(end_data);
			outStream.flush();
			// get response code
			int res = conn.getResponseCode();
			in = conn.getInputStream();
			StringBuilder resp = new StringBuilder();
			if (res == HttpURLConnection.HTTP_OK) {
				int ch;
				while ((ch = in.read()) != -1) {
					resp.append((char) ch);
				}
				responseBody = resp.toString();
			}
		} catch (IOException e) {
			responseBody = SERVER_CONNECT_ERROR;
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (outStream != null) {
				try {
					outStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		Logger.d(LOG, "get response: " + url + "/r/n" + responseBody);
		return responseBody;
	}
}
