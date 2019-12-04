package com.haoniu.aixin.http;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.haoniu.aixin.BuildConfig;
import com.haoniu.aixin.entity.VersionInfo;
import com.haoniu.aixin.utils.CretinAutoUpdateUtils;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.json.FastJsonUtil;
import com.zds.base.upDated.interfaces.ForceExitCallBack;
import com.zds.base.upDated.model.UpdateEntity;
import com.zds.base.util.SystemUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局配置
 *
 * @author Administrator
 */
public class AppConfig {
    /**
     * 服务器
     */
    public static final String baseService = BuildConfig.BASEURL;
    /**
     * 主地址
     */
    public static final String mainUrl = baseService;// + "flyingfish/";

    /**
     * 图片地址
     */
    public static final String ImageMainUrl = "http://hbindex.mengmeng666.com/aixin";//+ "flyingfish/";

    public static  String checkimg(String path){
        if (path==null){
            return "";
        }
        if (path.contains("http://")||path.contains("https://")){
           return path;
        }else {
            return ImageMainUrl+path;
        }
    }
    /**
     * 分享地址 2019/11/28 'share/share.html'改成'aixin/share.html'
     */
    public static final String share = BuildConfig.TUIGUANGURL+ "/aixin/share/share.html?url=";

    /**
     *
     */
    public static final String guize =  BuildConfig.TUIGUANGURL+"/guize.png";

    /**
     * 配置
     */
    public static final String peizhi = mainUrl + "common/getConfig";
    /**
     * 图形验证码
     */
    public static final String tuxingCode = mainUrl + "common/getCaptcha";

    /**
     * 获取验证码
     */
    public static String getPhoneCodeUrl = mainUrl + "common/getMsgCode";
    /**
     * 获取房间列表
     */
    public static String getRoomList = mainUrl + "room/list";

    /**
     * 登录
     */
    public static String toLoginUrl = mainUrl + "user/login";
    /**
     * 注册
     */
    public static String toRegister = mainUrl + "use r/register";
    /**
     * 加入房间
     */
    public static String AddRoom = mainUrl + "room/addUserToRoom";
    /**
     * 平台公告
     */
    public static String PlatformAnnouncement = mainUrl + "sysNotice/list";
    /**
     * 房间详情
     */
    public static String getRoomDetail = mainUrl + "room/get";

    /**
     *  我的团队总人数
     */
    public static final String listCountTotal =mainUrl+"award/listCountTotal";

    /**
     * banner
     */
    public static final String getBannerUrl = mainUrl + "common/listBanner";


    /**
     * 发踩雷红包
     */
    public static String sendCLRedEnvelope = mainUrl + "redPacket/createMineRedPacket";


    /**
     * 添加支付密码
     */
    public static String addPayPassword = mainUrl + "wallet/create";
    /**
     * 修改支付密码
     */
    public static String upPassword = mainUrl + "wallet/update";

    /**
     * 检测红包
     */
    public static String getRedEnvelopeState = mainUrl + "redPacket/getIsRedPacket";
    /**
     * 抢红包
     */
    public static String grabRedEnvelope = mainUrl + "redPacket/robRedPacket";


    /**
     * 红包详情
     */
    public static String getRedPacket = mainUrl + "redPacket/getRedPacketDetail";

    /**
     * 代理流水
     */
    public static String dlls = mainUrl + "award/listAward";
    /**
     * 首页 banner
     */
    public static String bannerUrl = mainUrl + "api/carouselFigure/list";

    /**
     * 月总收益
     */
    public static final String monthMoney = mainUrl + "award/getTodayAndMonthAward";
    /**
     * 刷新token
     */
    public static String refreshToken = mainUrl + "api/frontBase/userThirdLogin/refreshLogin";

    /**
     * 玩法说明
     */
    public static String howToPlay = mainUrl + "api/frontBase/user/howToPlay?type=";

    /**
     * 获取用户信息
     */
    public static String getUserByPhone = mainUrl + "api/frontBase/user/getUserByPhone";

    /**
     * 修改密码
     */
    public static String up_Password = mainUrl + "api/frontBase/user/updatePassword";


    /**
     * 忘记密码
     */
    public static String forgetpasswordUrl = mainUrl + "user/forgetPwd";


    /**
     * 创建房间
     */
    public static String creatRoom = mainUrl + "api/redPacket/room/create";

    /**
     * 修改房间信息
     */
    public static String upRoom = mainUrl + "api/redPacket/room/updateRoom";

    /**
     * 用户信息
     */
    public static String UserInfo = mainUrl + "user/getUserInfo";

    /**
     * 退出房间
     */
    public static final String layoutRoom = mainUrl + "room/deleteUserFromGroup";
    /**
     * 充值记录
     */
    public static String BalancePayments = mainUrl + "walletRecord/listUserWalletIn";

    /**
     * 忘记支付密码
     */
    public static String forgetPassword = mainUrl + "wallet/forgetPwd";
    /**
     * 提现记录
     */
    public static final String tixianjil = mainUrl + "walletRecord/listUserWalletOut";
    /**
     * 修改昵称
     */
    public static String upDataNickName = mainUrl + "user/updateNickName";

    /**
     * 更新用户真实姓名
     */
    public static final String upDataRealName = mainUrl + "user/updateRealName";

    /**
     * 更新头像
     */
    public static String upHead = mainUrl + "user/uploadAvatar";
    /**
     * 更换手机号
     */
    public static String upPhone = mainUrl + "api/frontBase/user/updatePhone";

    /**
     * 加好友
     */
    public static String addFriend = mainUrl + "api/frontBase/userFriend/saveUserFriend";

    /**
     * 删除好友
     */
    public static String delFriend = mainUrl + "api/frontBase/userFriend/deleteUserFriend";

    /**
     * 更改密码
     */
    public static String upDataPassword = mainUrl + "user/updatePassword";


    /**
     * 群组踢人
     */
    public static String RemoveRoom = mainUrl + "api/redPacket/room/shotUserFromGroup";
    /**
     * 群主解散房间
     */
    public static String deleteGroup = mainUrl + "api/redPacket/room/deleteGroup";

    /**
     * 发个人红包
     */
    public static String sendPerRedEnvelope = mainUrl + "api/redPacket/redPacket/createGroupChatRedPacket";

    /**
     * 发接龙红包
     */
    public static String sendDZRedEnvelope = mainUrl + "api/redPacket/redPacket/createFightRedPacket";


    /**
     * 我的下级
     */
    public static final String xiaji = mainUrl + "user/listUserInvite";

    /**
     * 更新头像和昵称
     */
    public static String getUserMessageList = mainUrl + "api/frontBase/user/listUserByids";


    /**
     * 签到
     */
    public static String signIn = mainUrl + "api/frontBase/userSign/create";
    /**
     * 历史签到
     */
    public static String getSign = mainUrl + "api/frontBase/userSign/list";

    /**
     * 查询代理下会员
     */
    public static String getDlList = mainUrl + "user/listUserInvite";

    /**
     * 字典
     */
    public static String getZD = mainUrl + "api/dictionary/listByParentCode";
    /**
     * 单个配置
     */
    public static String getOneZD = mainUrl + "api/dictionary/getByParentAndItem";
    /**
     * 获取敏感词汇
     */
    public static String getMGCH = mainUrl + "api/frontBase/wordInfo/list";
    /**
     * 添加绑定支付宝
     */
    public static String addAliPay = mainUrl + "api/frontBase/user/updateAlipay";
    /**
     * 添加绑定银行卡
     */
    public static String addBank = mainUrl + "wallet/updateAccount";

    /**
     * 查询银行卡信息或者支付宝信息
     */
    public static final String bankInfo = mainUrl + "wallet/getAccount";


    /**
     *  房间通知
     */
    public static final String roomNotic=mainUrl+"sysNotice/roomNotice";

    /**
     * 充值
     */
    public static String getRecharge = mainUrl + "api/pay/pay";

    /**
     * 趣币流水
     */
    public static String getQBLS = mainUrl + "api/frontBase/userMoney/page";
    /**
     * 收入
     */
    public static String getMOney = mainUrl + "api/frontBase/userMoney/get";

    /**
     * 排行榜
     */
    public static String getBank = mainUrl + "api/frontBase/userCenter/pageFunMoneyRank";
    /**
     * 排行榜（自己）
     */
    public static String getBankMine = mainUrl + "api/frontBase/userCenter/getUserCenter";

    /**
     * 转账
     */
    public static String transfer = mainUrl + "wallet/transfer";

    /**
     * 转账记录
     */
    public static String transferRecord = mainUrl + "walletRecord/listTransferRecord";


    /**
     * 群成员
     */
    public static final String memberList=mainUrl+"roomUser/list";


    /**
     *  游戏规则图
     */
    public static final String groupPlay=mainUrl+"groupPlay/pic";

    /**
     * 积分列表
     */
    public static final String scoreList = mainUrl + "api/frontBase/exchange/page";


    /**
     * 房间加人
     */
    public static String addGroupPeople = mainUrl + "api/redPacket/room/addFriendToGroup";

    /**
     * 积分兑换
     */
    public static String creditChangeMoney = mainUrl + "api/frontBase/exchange/exchange";
    /**
     * 积分兑换记录
     */
    public static String exchangeCrediRecords = mainUrl + "api/frontBase/exchange/pageDetail";

    /**
     * 红包流水
     */
    public static String qhbls = mainUrl + "redPacket/listRedPacketLog";

    /**
     *  人工客服
     */
    public static final String rengongkefu =mainUrl+"user/peopleUserList";

    /**
     * 二维码
     */
    public static String QR = "?inviteCode=";

    /**
     * 晋升代理
     */
    public static String promote = mainUrl + "api/frontBase/exchange/promote";
    /**
     * 关于我们
     */
    public static String about = mainUrl + "api/frontBase/user/aboutOur";
    /**
     * 我的会员总数
     */
    public static String memberCount = mainUrl + "api/frontBase/user/getCountByRecommend";

    /**
     * 盈亏
     */
    public static String ykUrl = mainUrl + "api/frontBase/userMoney/profitLoss";

    /**
     * 客服
     */
    public static String serviceUrl = mainUrl + "customerServe/list";

    /**
     * 加好友
     */
    public static final String addFriendUrl=mainUrl+"userfriendship/add";

    /**
     * 好友列表
     */
    public static final String friendList=mainUrl+"userfriendship/list";

    /**
     * 游戏客服
     */
    public static final String gameServiceUrl=mainUrl+"user/gameQuestionList";

    /**
     * 我的上级
     */
    public static final String chiefUrl=mainUrl+"user/findLastAgent";

    /**
     * 群主
     */
    public static final String groupBoos=mainUrl+"user/findRoomBoos";

    /**
     * 审核
     */
    public static String review = mainUrl + "api/frontBase/userProxyDetail/audit";

    /**
     * 申请代理
     */
    public static String apply = mainUrl + "api/frontBase/userProxyDetail/apply";

    /**
     * 申请记录(审核记录)
     */
    public static String applyRecord = mainUrl + "api/frontBase/userProxyDetail/page";
    /**
     * 投诉意见
     */
    public static String complaints = mainUrl + "api/frontBase/complaintProposal/save";
    /**
     * 搜索
     */
    public static String search = mainUrl + "api/redPacket/room/getByRoomId";

    /**
     * 平台公告未读消息count
     */
    public static String unReadCount = mainUrl + "api/frontBase/noticeInfo/nonRead";
    /**
     * 平台公告设置为已读
     */
    public static String updateStatus = mainUrl + "api/frontBase/noticeInfo/updateNumStatus";
    /**
     * 未审核数量
     */
    public static String ApplyNumber = mainUrl + "api/frontBase/userProxyDetail/getCount";


    /**
     * 检查版本号
     */
    public static String checkVersion = mainUrl + "common/versionUpdate";


    /**
     * 消息免打扰设置
     */
    public static String getMessageSet = mainUrl + "api/redPacket/roomMessage/getAll";
    /**
     * 提现申请
     */
    public static String withdraw = mainUrl + "wallet/withdraw";
    /**
     * 更新群消息设置
     */
    public static String upMessageSet = mainUrl + "api/redPacket/roomMessage/update";
    /**
     * http://www.weaigu.com
     */
    public static String downloadUrl = "https://fir.im/flyingfish";

    /**
     * 检查版本
     */
    public static void checkVersion(final Context context, boolean isinge) {
        if (isinge) {
            CretinAutoUpdateUtils.getInstance(context).check(new ForceExitCallBack() {
                @Override
                public void exit() {
                    ((Activity) context).finish();
                }

                @Override
                public void isHaveVersion(boolean isHave) {

                }

                @Override
                public void cancel() {

                }
            });
        } else {
            Map<String, Object> map = new HashMap<>();
            map.put("type", "Android");
            ApiClient.requestNetHandle(context, checkVersion, "正在检测...", map, new ResultListener() {
                @Override
                public void onSuccess(String json, String msg) {
                    final VersionInfo versionInfo = FastJsonUtil.getObject(json, VersionInfo.class);
                    if (versionInfo != null) {
                        if (versionInfo.getVersionCodes() > SystemUtil.getAppVersionNumber()) {
                            new AlertDialog.Builder(context).setTitle("新版本").setMessage(versionInfo.getUpdateLogs()).setPositiveButton("更新", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface anInterface, int i) {
                                    UpdateEntity updateEntity = new UpdateEntity();
                                    updateEntity.setVersionCode(versionInfo.getVersionCodes());
                                    updateEntity.setIsForceUpdate(versionInfo.getIsForceUpdates());
                                    updateEntity.setPreBaselineCode(versionInfo.getPreBaselineCodes());
                                    updateEntity.setVersionName(versionInfo.getVersionNames());
                                    updateEntity.setDownurl(versionInfo.getDownurls());
                                    updateEntity.setUpdateLog(versionInfo.getUpdateLogs());
                                    updateEntity.setSize(versionInfo.getApkSizes());
                                    updateEntity.setHasAffectCodes(versionInfo.getHasAffectCodess());
                                    UpdateEntity var8 = updateEntity;
                                    CretinAutoUpdateUtils.getInstance(context).startUpdate(var8);
                                }
                            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface anInterface, int i) {
                                    anInterface.dismiss();
                                }
                            }).show();

                        } else {
                            ToastUtil.toast("当前已是最新版本");
                        }

                    } else {
                        ToastUtil.toast("请求数据失败");
                    }
                }

                @Override
                public void onFailure(String msg) {
                    ToastUtil.toast(msg);
                }
            });
        }
    }

}
