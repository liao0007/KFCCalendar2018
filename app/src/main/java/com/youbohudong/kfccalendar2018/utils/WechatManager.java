package com.youbohudong.kfccalendar2018.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.ExecutionException;

public class WechatManager {
    private static final String TransactionTypeImage = "TransactionTypeImage";
    private static final String TransactionTypeWebpage = "TransactionTypeWebpage";

    private static final String ApiKey = "wx9b7b3c02f132a518";

    public static IWXAPI createWXAPI(Context context) {
        return WXAPIFactory.createWXAPI(context, ApiKey);
    }

    public static void shareUrl(final Context context, String url, String title, String thumbnailImageUrl, final int mTargetScene) {
        final WXMediaMessage mediaMessage = new WXMediaMessage(new WXWebpageObject(url));
        mediaMessage.title = title;

        try {
            Bitmap bitmap = Glide.with(context).load(thumbnailImageUrl).asBitmap().into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
            Bitmap thumbnail = createThumbnailFromBitmap(bitmap, 30f, 30f);
            mediaMessage.thumbData = Util.bmpToByteArray(thumbnail, false);
            share(context, mediaMessage, TransactionTypeWebpage, mTargetScene);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static void shareImage(Context context, Bitmap bitmap, int mTargetScene) {
        WXImageObject mediaObject = new WXImageObject(createThumbnailFromBitmap(bitmap, 1920f, 10180f));

        WXMediaMessage mediaMessage = new WXMediaMessage();
        mediaMessage.mediaObject = mediaObject;
        share(context, mediaMessage, TransactionTypeImage, mTargetScene);
    }

    private static Bitmap createThumbnailFromBitmap(Bitmap bitmap, float hh, float ww) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;

        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置缩放比例


        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        byte[] byteArray = stream.toByteArray();
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, newOpts);
    }

    private static void share(Context context, WXMediaMessage mediaMessage, String transactionType, int mTargetScene) {
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction(transactionType);
        req.message = mediaMessage;
        req.scene = mTargetScene;
        createWXAPI(context).sendReq(req);
    }

    private static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }
}
