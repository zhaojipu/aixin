package com.haoniu.aixin.http;

import android.content.Context;

import com.haoniu.aixin.BuildConfig;
import com.haoniu.aixin.base.Constant;
import com.haoniu.aixin.base.MyApplication;
import com.haoniu.aixin.base.Storage;
import com.haoniu.aixin.entity.EventCenter;
import com.haoniu.aixin.utils.EventUtil;
import com.haoniu.aixin.widget.Loading_view;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.zds.base.aes.AESCipher;
import com.zds.base.json.FastJsonUtil;
import com.zds.base.log.XLog;
import com.zds.base.util.StringUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


/**
 * 作   者：赵大帅
 * 描   述: 网络请求工具类
 * 日   期: 2017/11/13 16:05
 * 更新日期: 2017/11/13
 */
public class ApiClient {

    static Loading_view progressDialog = null;

    /**
     * 请求网络数据接口
     * post
     *
     * @param context
     * @param url
     * @param log
     * @param mapP
     * @param listener
     */
    public static void requestNetHandle(final Context context, String url, String log, final Map<String, Object> mapP, final ResultListener listener) {
        if (!MyApplication.getInstance().isNetworkConnected()) {
            //没网络
            listener.onFailure("网络连接异常,请检查您的网络设置");
            return;
        }
        try {
            showDialog(log, context);
            OkGo.<String>post(url)
                    .tag(context)
                    .upJson(aesParams(mapP))
                    .headers("token", Storage.getToken())
                    .execute(new StringCallback() {
                                 /**
                                  * 对返回数据进行操作的回调， UI线程
                                  *
                                  * @param response
                                  */
                                 @Override
                                 public void onSuccess(Response<String> response) {
                                     fomartDataAES(response, listener);
                                 }

                                 @Override
                                 public void onFinish() {
                                     super.onFinish();
                                     listener.onFinsh();
                                     dismiss();
                                 }

                                 @Override
                                 public void onError(Response<String> response) {
                                     try {
                                         listener.onFailure(response.getException().getMessage());
                                     } catch (Exception e) {
                                         XLog.error(e);
                                     }
                                 }
                             }

                    );

        } catch (Exception e) {
            listener.onFailure(e.getMessage());
        }

    }

    /**
     * 请求网络数据接口
     * post
     *
     * @param context
     * @param url
     * @param log
     * @param file
     * @param listener
     */
    public static void requestNetHandleFile(final Context context, String url, String log, File file, final ResultListener listener) {
        if (!MyApplication.getInstance().isNetworkConnected()) {
            //没网络
            listener.onFailure("网络连接异常,请检查您的网络设置");
            return;
        }
        try {
            showDialog(log, context);
            OkGo.<String>post(url)
                    .tag(context)
                    .isMultipart(true)
                    .params("file",file)
                    .headers("token", Storage.getToken())
                    .execute(new StringCallback() {
                                 /**
                                  * 对返回数据进行操作的回调， UI线程
                                  *
                                  * @param response
                                  */
                                 @Override
                                 public void onSuccess(Response<String> response) {
                                     fomartDataAES(response, listener);
                                 }

                                 @Override
                                 public void onFinish() {
                                     super.onFinish();
                                     listener.onFinsh();
                                     dismiss();
                                 }

                                 @Override
                                 public void onError(Response<String> response) {
                                     try {
                                         listener.onFailure(response.getException().getMessage());
                                     } catch (Exception e) {
                                         XLog.error(e);
                                     }

                                 }
                             }

                    );

        } catch (Exception e) {
            listener.onFailure(e.getMessage());
        }

    }

    /**
     * 请求网络数据接口
     *
     * @param context
     * @param url
     * @param log
     * @param mapP
     * @param listener
     */
    public static void requestNetHandleVGet(final Context context, String url, String log, Map<String, Object> mapP, final ResultListener listener) {
        if (!MyApplication.getInstance().isNetworkConnected()) {
            //没网络
            listener.onFailure("网络连接异常,请检查您的网络设置");
            return;
        }
        try {
            showDialog(log, context);
            OkGo.<String>get(url)
                    .tag(context)
                    .params(getFormatMap(mapP))
                    .execute(new StringCallback() {
                                 @Override
                                 public void onSuccess(Response<String> response) {
                                     XLog.json(response.body().toString());
                                     listener.onSuccess(response.body().toString(), "");
                                 }

                                 @Override
                                 public void onFinish() {
                                     super.onFinish();
                                     listener.onFinsh();
                                     dismiss();
                                 }

                                 @Override
                                 public void onError(Response<String> response) {
                                     try {
                                         listener.onFailure(response.getException().getMessage());
                                     } catch (Exception e) {
                                         XLog.error(e);
                                     }

                                 }
                             }

                    );

        } catch (Exception e) {
            listener.onFailure(e.getMessage());
        }
    }

    /**
     * 请求网络数据接口
     *
     * @param context
     * @param url
     * @param log
     * @param mapP
     * @param listener
     */
    public static void requestNetHandleByGet(final Context context, String url, String log, Map<String, Object> mapP, final ResultListener listener) {
        if (!MyApplication.getInstance().isNetworkConnected()) {
            //没网络
            listener.onFailure("网络连接异常,请检查您的网络设置");
            return;
        }
        try {
            showDialog(log, context);
            OkGo.<String>get(url)
                    .tag(context)
                    .params(getFormatMap(mapP))
                    .headers("token", Storage.getToken())
                    .execute(new StringCallback() {
                                 @Override
                                 public void onSuccess(Response<String> response) {
                                     fomartData(response, listener);
                                 }

                                 @Override
                                 public void onFinish() {
                                     super.onFinish();
                                     listener.onFinsh();
                                     dismiss();
                                 }

                                 @Override
                                 public void onError(Response<String> response) {
                                     try {
                                         listener.onFailure(response.getException().getMessage());
                                     } catch (Exception e) {
                                         XLog.error(e);
                                     }
                                 }
                             }

                    );

        } catch (Exception e) {
            listener.onFailure(e.getMessage());
        }
    }

    private static Map<String, String> getFormatMap(Map<String, Object> mapP) {
        Map<String, String> map = new HashMap<>();
        if (null != mapP && mapP.size() > 0) {
            for (String key : mapP.keySet()) {
                map.put(key, String.valueOf(mapP.get(key)));
            }
        }
        return map;
    }

    /**
     * show dialog
     */
    private static void showDialog(String log, Context context) {
        if (!StringUtil.isEmpty(log)) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = null;
            progressDialog = new Loading_view(context);
            progressDialog.setMessage(log);
            progressDialog.show();
        }
    }

    /**
     * dis dialog
     */
    private static void dismiss() {
        if (null != progressDialog && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    /**
     * 格式化请求数据
     */
    private static void fomartDataAES(Response<String> response, ResultListener listener) {
        try {
            String json = response.body();
            if (BuildConfig.ISENCRYPTION) {
                json = AESCipher.decrypt(response.body());
            }
            ServerData serverData = FastJsonUtil.getObject(json, ServerData.class);
            if (null != serverData) {
                switch (serverData.getCode()) {
                    //请求成功
                    case Constant.CODESUCCESS:
                        listener.onSuccess(FastJsonUtil.toJSONString(serverData.getData()), serverData.getMessage() == null ? "" : serverData.getMessage());
                        break;
                    //token 异常
                    case Constant.CODETOKENERROR:
                        listener.onFailure(serverData.getMessage());
                        EventBus.getDefault().post(new EventCenter(EventUtil.LOSETOKEN));
                        break;
                    //请求失败 错误
                    case Constant.CODEERROR:
                        listener.onFailure(serverData.getMessage() == null ? "" : serverData.getMessage());
                        break;
                    //未注册
                    case Constant.CODENORIGISTER:
                        listener.onFailure(serverData.getMessage());
                        EventBus.getDefault().post(new EventCenter(EventUtil.TOREGISTER));
                        break;
                    default:
                        listener.onFailure(serverData.getMessage());
                        break;
                }
            } else {
                listener.onFailure("解析异常");
            }
        } catch (Exception e) {
            e.getStackTrace();
            listener.onFailure(e.getMessage());
        }
    }

    /**
     * 格式化请求数据
     */
    private static void fomartData(Response<String> response, ResultListener listener) {
        try {
            ServerData serverData = FastJsonUtil.getObject(response.body(), ServerData.class);
            if (null != serverData) {
                switch (serverData.getCode()) {
                    //请求成功
                    case Constant.CODESUCCESS:
                        listener.onSuccess(FastJsonUtil.toJSONString(serverData.getData()), serverData.getMessage() == null ? "" : serverData.getMessage());
                        break;
                    //token 异常
                    case Constant.CODETOKENERROR:
                        listener.onFailure(serverData.getMessage());
                        EventBus.getDefault().post(new EventCenter(EventUtil.LOSETOKEN));
                        break;
                    //请求失败 错误
                    case Constant.CODEERROR:
                        listener.onFailure(serverData.getMessage() == null ? "" : serverData.getMessage());
                        break;
                    //未注册
                    case Constant.CODENORIGISTER:
                        listener.onFailure(serverData.getMessage());
                        EventBus.getDefault().post(new EventCenter(EventUtil.TOREGISTER));
                        break;
                    default:
                        listener.onFailure(serverData.getMessage());
                        break;
                }
            } else {
                listener.onFailure("解析异常");
            }
        } catch (Exception e) {
            e.getStackTrace();
            listener.onFailure(e.getMessage());
        }
    }

    /**
     * 传参加密
     */
    private static String aesParams(Map<String, Object> map) {
        if (BuildConfig.ISENCRYPTION) {
            return AESCipher.encrypt(FastJsonUtil.toJSONString(map));
        } else {
            return FastJsonUtil.toJSONString(map);
        }

    }
}
