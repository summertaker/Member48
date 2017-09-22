package com.summertaker.member48;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.summertaker.member48.util.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    private static final String TAG = "CustomAdapter";

    private Context mContext;
    private List<MemberData> mDataSet;
    private static String mPath = "";

    private String mCurrentLayoutManagerType;
    private LinearLayout.LayoutParams mParamsLinear;
    private LinearLayout.LayoutParams mParamsGrid;

    public CustomAdapter(Context context, List<MemberData> dataSet, String path) {
        mContext = context;
        mDataSet = dataSet;
        mPath = path;

        float density = mContext.getResources().getDisplayMetrics().density;
        int height = (int) (500 * density);
        int margin = (int) (1 * density);
        mParamsLinear = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, height);
        mParamsLinear.setMargins(0, margin, 0, 0);
        height = (int) (200 * density);
        mParamsGrid = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, height);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout llItem;
        private final ImageView ivPicture;
        private final TextView tvName;

        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");
                }
            });
            llItem = v.findViewById(R.id.llItem);
            ivPicture = v.findViewById(R.id.ivPicture);
            tvName = v.findViewById(R.id.tvName);
        }

        public LinearLayout getLlItem() { return llItem; }
        public ImageView getIvPicture() { return ivPicture; }
        public TextView getTvName() {
            return tvName;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.member_item, viewGroup, false);

        // work here if you need to control height of your items
        // keep in mind that parent is RecyclerView in this case
        //int height = viewGroup.getMeasuredHeight() / 4;
        //v.setMinimumHeight(height);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        //Log.d(TAG, "Element " + position + " set.");

        MemberData data = mDataSet.get(position);

        LinearLayout llItem = viewHolder.getLlItem();
        ImageView ivPicture = viewHolder.getIvPicture();
        if ("GRID_LAYOUT_MANAGER".equals(mCurrentLayoutManagerType)) {
            llItem.setLayoutParams(mParamsGrid);
        } else {
            llItem.setLayoutParams(mParamsLinear);
        }

        final String thumbnailUrl = data.getThumbnailUrl();
        final String imageUrl = data.getImageUrl();

        if (thumbnailUrl == null || thumbnailUrl.isEmpty()) {
            //holder.loLoading.setVisibility(View.GONE);
            ivPicture.setImageResource(R.drawable.placeholder);
        } else {
            //Log.d(mTag, thumbnailUrl);

            String fileName = Util.getUrlToFileName(thumbnailUrl);
            File file = new File(mPath, fileName);
            if (file.exists()) {
                Picasso.with(mContext).load(file).into(ivPicture);
                //Log.d(mTag, fileName + " local loaded.");
            } else {
                Picasso.with(mContext).load(thumbnailUrl).into(ivPicture, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        //progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        //progressBar.setVisibility(View.GONE);
                        Log.e(TAG, "PICASSO IMAGE LOAD ERROR!!!");
                    }
                });

                Picasso.with(mContext).load(thumbnailUrl).into(getTarget(fileName));
            }
        }
        viewHolder.getTvName().setText(data.getName());
    }

    @Override
    public int getItemCount() {
        //Log.d(TAG, "mDataSet.size(): " + mDataSet.size());
        return mDataSet.size();
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
                Log.e(TAG, "IMAGE SAVE ERROR!!! onBitmapFailed()");
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        return target;
    }

    public String getmCurrentLayoutManagerType() {
        return mCurrentLayoutManagerType;
    }

    public void setmCurrentLayoutManagerType(String mCurrentLayoutManagerType) {
        this.mCurrentLayoutManagerType = mCurrentLayoutManagerType;
    }
}

