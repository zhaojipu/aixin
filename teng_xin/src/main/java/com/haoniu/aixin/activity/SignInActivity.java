package com.haoniu.aixin.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haoniu.aixin.http.ApiClient;
import com.haoniu.aixin.http.AppConfig;
import com.haoniu.aixin.http.ResultListener;
import com.haoniu.aixin.R;
import com.haoniu.aixin.base.BaseActivity;
import com.haoniu.aixin.base.MyApplication;
import com.haoniu.aixin.entity.EventCenter;
import com.haoniu.aixin.entity.SingInInfo;
import com.haoniu.aixin.utils.EventUtil;
import com.haoniu.aixin.widget.MySelectorDecorator;
import com.haoniu.aixin.widget.MySelectorJYDecorator;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.zds.base.json.FastJsonUtil;
import com.zds.base.log.XLog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.prolificinteractive.materialcalendarview.MaterialCalendarView.SELECTION_MODE_MULTIPLE;

/**
 * 作   者：赵大帅
 * 描   述: 签到
 * 邮   箱: 2510722254@qqq.com
 * 日   期: 2017/11/27 18:56
 * 更新日期: 2017/11/27
 */
public class SignInActivity extends BaseActivity {
    @BindView(R.id.bar)
    View mBar;
    @BindView(R.id.ll_back)
    LinearLayout mLlBack;
    @BindView(R.id.toolbar_subtitle)
    TextView mToolbarSubtitle;
    @BindView(R.id.img_right)
    ImageView mImgRight;
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.calendarView)
    MaterialCalendarView mCalendarView;
    @BindView(R.id.tv_jifen)
    TextView mTvJifen;
    @BindView(R.id.tv_duihuan)
    TextView mTvDuihuan;
    @BindView(R.id.sign)
    TextView mSign;
    @BindView(R.id.tv_date)
    TextView mTvDate;
    @BindView(R.id.rel_qiandao)
    RelativeLayout mRelQiandao;
    @BindView(R.id.tv_message)
    TextView mTvMessage;


    /**
     * 替代onCreate的使用
     *
     * @param bundle
     */
    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_sign_in);
    }

    MySelectorDecorator mDecorator;
    List<Date> lists;

    /**
     * 初始化逻辑
     */
    @Override
    protected void initLogic() {
        setTitle("签到");
        mCalendarView.setSelectionMode(SELECTION_MODE_MULTIPLE);
        lists = new ArrayList<>();
        mDecorator = new MySelectorDecorator(this, lists);
        mCalendarView.addDecorators(mDecorator, new MySelectorJYDecorator());
        mCalendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
                String dataStr = simpleDateFormat.format(date.getDate());
                getDate(dataStr);
            }
        });
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
        String dataStr = simpleDateFormat.format(new Date());
        getDateNow(dataStr);
       // mTvJifen.setText(MyApplication.getInstance().getUserInfo().getCredit() + "积分");
    }


    /**
     * EventBus接收消息
     *
     * @param center 获取事件总线信息
     */
    @Override
    protected void onEventComing(EventCenter center) {
        if (center.getEventCode() == EventUtil.FLUSHUSERINFO) {
         //   mTvJifen.setText(MyApplication.getInstance().getUserInfo().getCredit() + "积分");
        }
    }

    /**
     * Bundle  传递数据
     *
     * @param extras
     */
    @Override
    protected void getBundleExtras(Bundle extras) {

    }

    private void getDate(String dateStr) {
        Map<String, Object> map = new HashMap<>();
        map.put("years", dateStr);
        ApiClient.requestNetHandleByGet(this, AppConfig.getSign, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                List<SingInInfo> list = FastJsonUtil.getList(json, SingInInfo.class);
                try {
                    if (list != null && list.size() > 0) {
                        lists.clear();
                        for (SingInInfo singInInfo : list) {
                            lists.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(singInInfo.getCreateTime()));
                            mDecorator.setDates(lists);
                            // mCalendarView.setSelectedDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(singInInfo.getCreateTime()));
                            mCalendarView.addDecorator(mDecorator);
                        }
                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onFailure(String msg) {
               toast(msg);
            }
        });
    }

    //获取当前签到信息
    private void getDateNow(String dateStr) {
        Map<String, Object> map = new HashMap<>();
        map.put("years", dateStr);
        ApiClient.requestNetHandleByGet(this, AppConfig.getSign, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                List<SingInInfo> list = FastJsonUtil.getList(json, SingInInfo.class);
                try {
                    if (list != null && list.size() > 0) {
                        lists.clear();
                        for (SingInInfo singInInfo : list) {
                            if (isSameDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(singInInfo.getCreateTime()), new Date())) {
                                mTvMessage.setText("今日已签到，获得" + singInInfo.getSignCredit() + "积分");
                                mSign.setText("已签到");
                                mRelQiandao.setClickable(false);
                            }
                            lists.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(singInInfo.getCreateTime()));
                            mDecorator.setDates(lists);
                            // mCalendarView.setSelectedDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(singInInfo.getCreateTime()));
                            mCalendarView.addDecorator(mDecorator);
                        }
                        mTvDate.setText("连续签到" + list.get(0).getSeriesCount() + "天");


                    }
                } catch (Exception e) {

                }

            }

            @Override
            public void onFailure(String msg) {
               toast(msg);
            }
        });
    }

    private void signIn() {
        Map<String, Object> map = new HashMap<>();
        ApiClient.requestNetHandleByGet(this, AppConfig.signIn, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                XLog.json(json);
               toast(msg);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
                String dataStr = simpleDateFormat.format(new Date());
                getDateNow(dataStr);
                MyApplication.getInstance().UpUserInfo();
            }

            @Override
            public void onFailure(String msg) {
               toast(msg);
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.tv_duihuan, R.id.rel_qiandao})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_duihuan:
                startActivity(RedeemActivity.class);
                break;
            case R.id.rel_qiandao:
                signIn();
                break;
        }
    }

    private boolean isSameDate(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        boolean isSameYear = cal1.get(Calendar.YEAR) == cal2
                .get(Calendar.YEAR);
        boolean isSameMonth = isSameYear
                && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
        boolean isSameDate = isSameMonth
                && cal1.get(Calendar.DAY_OF_MONTH) == cal2
                .get(Calendar.DAY_OF_MONTH);

        return isSameDate;
    }
}
