package com.summertaker.member48;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.summertaker.member48.common.BaseApplication;
import com.summertaker.member48.common.BaseDataAdapter;
import com.summertaker.member48.util.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MemberAdapter extends BaseDataAdapter {
    private String mTag;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<MemberData> mDataList = null;
    private MemberInterface mMemberInterface;

    private static String mPath = "";

    public MemberAdapter(Context context, ArrayList<MemberData> dataList, MemberInterface memberInterface) {
        this.mTag = "== " + this.getClass().getSimpleName();
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mDataList = dataList;
        this.mMemberInterface = memberInterface;

        // MainActivity에서 미리 권한을 획득함
        mPath = BaseApplication.getDataPath();
        //Log.d(mTag, "mPath: " + mPath);

        File dir = new File(mPath);
        if (!dir.exists()) {
            boolean isSuccess = dir.mkdirs();
            //if (isSuccess) {
            //    Log.d(mTag, "created.");
            //} else {
            //    Log.d(mTag, "mkdir failed.");
            //}
        } else {
            //Log.d(mTag, "exists.");
        }
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        final MemberData data = mDataList.get(position);

        if (convertView == null) {
            holder = new ViewHolder();

            convertView = mLayoutInflater.inflate(R.layout.member_item, null);

            //holder.loLoading = (RelativeLayout) convertView.findViewById(R.id.loLoading);
            //holder.pbLoading = (ProgressBar) convertView.findViewById(R.id.pbLoading);

            holder.cardView = convertView.findViewById(R.id.cardView);
            holder.ivPicture = convertView.findViewById(R.id.ivPicture);
            holder.tvName = convertView.findViewById(R.id.tvName);
            holder.ivClose = convertView.findViewById(R.id.ivClose);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final String thumbnailUrl = data.getThumbnailUrl();
        final String imageUrl = data.getImageUrl();

        if (thumbnailUrl == null || thumbnailUrl.isEmpty()) {
            //holder.loLoading.setVisibility(View.GONE);
            holder.ivPicture.setImageResource(R.drawable.placeholder);
        } else {
            //Log.d(mTag, thumbnailUrl);

            String fileName = Util.getUrlToFileName(thumbnailUrl);
            File file = new File(mPath, fileName);
            if (file.exists()) {
                Picasso.with(mContext).load(file).into(holder.ivPicture);
                //Log.d(mTag, fileName + " local loaded.");
            } else {
                Picasso.with(mContext).load(thumbnailUrl).into(holder.ivPicture, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        //progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        //progressBar.setVisibility(View.GONE);
                        Log.e(mTag, "PICASSO IMAGE LOAD ERROR!!!");
                    }
                });

                Picasso.with(mContext).load(thumbnailUrl).into(getTarget(fileName));
            }

            //Glide.with(mContext).load(thumbnailUrl).into(holder.ivPicture);
        }

        holder.ivPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMemberInterface.onPictureClick(position, imageUrl);
            }
        });

        holder.tvName.setText(data.getName());

        holder.ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMemberInterface.onCloseClick(position);
            }
        });

        return convertView;
    }

    //target to save
    private Target getTarget(final String fileName) {
        Target target = new Target() {

            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        boolean isSuccess;

                        File file = new File(mPath, fileName);
                        if (file.exists()) {
                            isSuccess = file.delete();
                            //Log.d("==", fileName + " deleted.");
                        }
                        try {
                            isSuccess = file.createNewFile();
                            if (isSuccess) {
                                FileOutputStream ostream = new FileOutputStream(file);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                                ostream.flush();
                                ostream.close();
                                //Log.d("==", fileName + " created.");
                            } else {
                                Log.e("==", fileName + " FAILED.");
                            }
                        } catch (IOException e) {
                            Log.e("IOException", e.getLocalizedMessage());
                        }
                    }
                }).start();
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.e(mTag, "IMAGE SAVE ERROR!!! onBitmapFailed()");
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        return target;
    }

    static class ViewHolder {
        // RelativeLayout loLoading;
        //ProgressBar pbLoading;
        CardView cardView;
        ImageView ivPicture;
        TextView tvName;
        ImageView ivClose;
    }
}