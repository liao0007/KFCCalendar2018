package com.youbohudong.kfccalendar2018.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


/**
 * Created by donglianhua on 2017/3/20.
 * <p>
 * 自定义接收器
 * <p>
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class JPushReceiver extends BroadcastReceiver {
    private static final String TAG = "JPush";

    //    private NotifyOrder notifyOrder;
    @Override
    public void onReceive(Context context, Intent intent) {
//        Bundle bundle = intent.getExtras();
//        Log.d(TAG, "[JPushReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
//
//        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
//            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
//            Log.d(TAG, "[JPushReceiver] 接收Registration Id : " + regId);
//            //send the Registration Id to your server...
//
//        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
//            Log.d(TAG, "[JPushReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
////            processCustomMessage(context, bundle);
//
//        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
//            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
//            Log.d(TAG, "[JPushReceiver] 接收到推送下来的通知的ID: " + notifactionId);
//            Cfg.jpush_content = bundle.getString(JPushInterface.EXTRA_EXTRA);
//
//
//        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
//            Log.d(TAG, "[JPushReceiver] 用户点击打开了通知");
//            //打开自定义的Activity
//            Gson gson = new Gson();
//            NotifyOrder notifyOrder = gson.fromJson(Cfg.jpush_content, NotifyOrder.class);
//            if (notifyOrder != null) {
//                String type = notifyOrder.getType();
//                if ("1".equals(type)) { //直接跳转广告页面
//                    Intent i = new Intent(context, HomeBanerWebViewActivity.class);
//                    i.putExtra("TITLE", "钢票网");
//                    i.putExtra("URL", notifyOrder.getLink());
//                    //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    context.startActivity(i);
//                }else if("2".equals(type)){ //直接跳转到团团赚二级页面
//                    String h5_link = notifyOrder.getLink();
//                    if (UserHelper.isLogin()) {//登录状态
//                        Intent i = new Intent(context, VIPRulesActivity.class);
//                        i.putExtra("h5_link", h5_link);
//                        i.putExtra("identity", UserHelper.getUserInfo().getIdentity());
//                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        context.startActivity(i);
//
//                    }else{//未登录
//                        Intent i = new Intent(context, VIPRulesActivity.class);
//                        i.putExtra("h5_link", h5_link);
//                        i.putExtra("identity", 0);
//                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        context.startActivity(i);
//                    }
//                }else if("3".equals(type)){
//                    String id = notifyOrder.getLink();
//                    int auto_id = Integer.parseInt(id);
//                    String product_type = notifyOrder.getProduct_type();
//
//                    if ("YSZK".equalsIgnoreCase(product_type)) {
//                        //应收账款
//                        Intent i = new Intent(context, Detail2Activity.class);
//                        i.putExtra("auto_id", auto_id);
//                        i.putExtra("title", "钢票网");
//                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        context.startActivity(i);
//                    } else if("GENERAL".equalsIgnoreCase(product_type)){
//                        Intent i = new Intent(context, DetailActivity.class);
//                        i.putExtra("auto_id", auto_id);
//                        i.putExtra("title", "钢票网");
//                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        context.startActivity(i);
//                    }
//                }
//
//
//                else {
//                    if (isBackground(context)) {
//                        //唤醒app
//                        Intent launchIntent = context.getPackageManager().
//                                getLaunchIntentForPackage("com.gangpiao.gangpiaowang");
//                        launchIntent.setFlags(
//                                Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//                        context.startActivity(launchIntent);
//                    }
////                    else {
////                        Intent i = new Intent(context, MainActivity.class);
////                        i.putExtras(bundle);
////                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
////                        context.startActivity(i);
////                    }
//                }
//                Cfg.jpush_content = "";
//            }
//
//        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
//            Log.d(TAG, "[JPushReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
//            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..
//
//        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
//            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
//            Log.w(TAG, "[JPushReceiver]" + intent.getAction() + " connected state switchCamera to " + connected);
//        } else {
//            Log.d(TAG, "[JPushReceiver] Unhandled intent - " + intent.getAction());
//        }
//    }
//
//    public static boolean isBackground(Context context) {
//        ActivityManager activityManager = (ActivityManager) context
//                .getSystemService(Context.ACTIVITY_SERVICE);
//        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
//                .getRunningAppProcesses();
//        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
//            if (appProcess.processName.equals(context.getPackageName())) {
//                /*
//                BACKGROUND=400 EMPTY=500 FOREGROUND=100
//                GONE=1000 PERCEPTIBLE=130 SERVICE=300 ISIBLE=200
//                 */
//                Log.i(context.getPackageName(), "此appimportace ="
//                        + appProcess.importance
//                        + ",context.getClass().getName()="
//                        + context.getClass().getName());
//                if (appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
//                    Log.i(context.getPackageName(), "处于后台"
//                            + appProcess.processName);
//                    return true;
//                } else {
//                    Log.i(context.getPackageName(), "处于前台"
//                            + appProcess.processName);
//                    return false;
//                }
//            }
//        }
//        return false;
//    }
//
//    // 打印所有的 intent extra 数据
//    private static String printBundle(Bundle bundle) {
//        StringBuilder sb = new StringBuilder();
//        for (String key : bundle.keySet()) {
//            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
//                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
//            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
//                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
//            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
//                if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
//                    Log.i(TAG, "This message has no Extra data");
//                    continue;
//                }
//
//                try {
//                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
//                    Iterator<String> it = json.keys();
//
//                    while (it.hasNext()) {
//                        String myKey = it.next().toString();
//                        sb.append("\nkey:" + key + ", value: [" +
//                                myKey + " - " + json.optString(myKey) + "]");
//                    }
//                } catch (JSONException e) {
//                    Log.e(TAG, "Get message extra JSON error!");
//                }
//
//            } else {
//                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
//            }
//        }
//        return sb.toString();
//    }
//
//    //send msg to MainActivity
//    private void processCustomMessage(Context context, Bundle bundle) {
////        if (MainActivity.isForeground) {
////            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
////            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
////            Intent msgIntent = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
////            msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
////            if (!ExampleUtil.isEmpty(extras)) {
////                try {
////                    JSONObject extraJson = new JSONObject(extras);
////                    if (extraJson.length() > 0) {
////                        msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
////                    }
////                } catch (JSONException e) {
////
////                }
////            }
////            context.sendBroadcast(msgIntent);
////        }
    }
}

