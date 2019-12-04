package com.haoniu.aixin.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.haoniu.aixin.http.ApiClient;
import com.haoniu.aixin.http.AppConfig;
import com.haoniu.aixin.http.ResultListener;
import com.haoniu.aixin.R;
import com.haoniu.aixin.base.MyApplication;
import com.haoniu.aixin.base.MyHelper;
import com.haoniu.aixin.entity.PhbInfo;
import com.haoniu.aixin.widget.CommonDialog;
import com.haoniu.aixin.widget.EaseAlertDialog;
import com.haoniu.aixin.widget.Loading_view;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.zds.base.ImageLoad.GlideUtils;
import com.zds.base.Toast.ToastUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 排行榜
 */
public class LeaderboardsAdapter extends BaseQuickAdapter<PhbInfo, BaseViewHolder> {

    public LeaderboardsAdapter(List<PhbInfo> list) {
        super(R.layout.adapter_leaderboards, list);
    }

    @Override
    protected void convert(BaseViewHolder helper, final PhbInfo item) {
        ((TextView) helper.getView(R.id.tv_username_)).getPaint().setFakeBoldText(true);
        ((TextView) helper.getView(R.id.tv_jignyan_)).getPaint().setFakeBoldText(true);
        ((TextView) helper.getView(R.id.tv_username)).getPaint().setFakeBoldText(true);
        ((TextView) helper.getView(R.id.tv_jignyan)).getPaint().setFakeBoldText(true);
        if (helper.getPosition() == 0) {
            helper.setGone(R.id.ll_liebiao_1, true);
            helper.setGone(R.id.ll_liebiao, false);
            helper.setText(R.id.tv_username_, item.getNickname());
            helper.setText(R.id.tv_no_1, "NO." + 1);
            helper.setText(R.id.tv_jignyan_, "积分：" + item.getCredit());
            GlideUtils.loadImageViewLodingByCircle(AppConfig.checkimg( item.getAvatarUrl()), (ImageView) helper.getView(R.id.img_view_1), R.mipmap.img_default_avatar);
            if (MyHelper.getInstance().getContactList().get(item.getUserIdH() + "") != null || MyApplication.getInstance().getUserInfo().getUserId() == item.getUserId()) {
                helper.setGone(R.id.tv_add_1, false);
            } else {
                helper.setGone(R.id.tv_add_1, true);
                helper.setOnClickListener(R.id.tv_add_1, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addContact(view, item.getUserIdH() + "");
                    }
                });
            }

        } else {
            helper.setGone(R.id.ll_liebiao_1, false);
            helper.setGone(R.id.ll_liebiao, true);
            helper.setText(R.id.tv_no, "NO." + (helper.getPosition() + 1));
            helper.setText(R.id.tv_username, item.getNickname());
            helper.setText(R.id.tv_jignyan, "积分：" + item.getCredit());
            GlideUtils.loadImageViewLodingByCircle(AppConfig.checkimg( item.getAvatarUrl()), (ImageView) helper.getView(R.id.img_view), R.mipmap.img_default_avatar);
            if (MyHelper.getInstance().getContactList().get(item.getUserIdH() + "") != null || MyApplication.getInstance().getUserInfo().getUserId() == item.getUserId()) {
                helper.setGone(R.id.tv_add, false);
            } else {
                helper.setGone(R.id.tv_add, true);
                helper.setOnClickListener(R.id.tv_add, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addContact(view, item.getUserIdH() + "");
                    }
                });
            }

        }
    }

    /**
     * add contact
     */
    public void addContact(final View view, final String username) {
        if (username == null) {
            ToastUtil.toast("获取用户信息失败");
            return;
        }
        if (EMClient.getInstance().getCurrentUser().equals(username)) {
            new EaseAlertDialog(mContext, R.string.not_add_myself).show();
            return;
        }

        if (MyHelper.getInstance().getContactList().containsKey(username)) {
            new EaseAlertDialog(mContext, R.string.This_user_is_already_your_friend).show();
            return;
        }
        if (commonDialog != null && commonDialog.isShowing()) {
            commonDialog.dismiss();
        }
        commonDialog = new CommonDialog.Builder(mContext).setView(R.layout.add_friend_dialog).setOnClickListener(R.id.btn_ok, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commonDialog.dismiss();
                if (mEditText != null && mEditText.getText().toString() != null && !"".equals(mEditText.getText().toString())) {
                    addFriend(username, mEditText.getText().toString(), view);
                } else {
                    addFriend(username, "加个好友呗！", view);
                }
            }
        }).setOnClickListener(R.id.btn_cancel, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commonDialog.dismiss();
            }
        }).setText(R.id.title, "说点啥子吧").create();
        commonDialog.getView(R.id.btn_cancel).setVisibility(View.VISIBLE);
        mEditText = (EditText) commonDialog.getView(R.id.et_message);
        commonDialog.show();
    }

    EditText mEditText;
    CommonDialog commonDialog;

    private void addFriend(final String username, final String reason, final View view) {
        showLoading("正在申请添加好友...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().contactManager().addContact(username, reason);
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.toast("成功发送申请");
                        }
                    });

                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.toast(e.getMessage());
                        }
                    });
                }
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissLoading();
                    }
                });
            }
        }).start();
    }

    private void addFriend(final View view, int username) {
        Map<String, Object> map = new HashMap<>();
        map.put("friendId", username);
        ApiClient.requestNetHandle(mContext, AppConfig.addFriend, "正在添加好友...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                view.setVisibility(View.GONE);
                ToastUtil.toast(msg);
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }


    Loading_view dialog;


    public void showLoading(String message) {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        dialog = new Loading_view(mContext);
        // dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        //  dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(message);
        dialog.show();
    }

    public void dismissLoading() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}