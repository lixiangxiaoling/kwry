package hwd.kuworuye.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.lzy.okhttputils.OkHttpUtils;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import hwd.kuworuye.R;
import hwd.kuworuye.activity.ContentActivity;
import hwd.kuworuye.adapter.SaleListShowAdapter;
import hwd.kuworuye.base.FBaseFragment;
import hwd.kuworuye.constants.Constant;
import hwd.kuworuye.event.CommRefreshListEvent;
import hwd.kuworuye.utils.SharedPreferencesUtil;
import hwd.kuworuye.utils.Util;

/**
 * Created by Administrator on 2017/3/6.
 */
public class CommApplyForFragment extends FBaseFragment implements View.OnClickListener {

    @BindView(R.id.vp_activity_apply_for)
    ViewPager mVpOrderManager;
    Unbinder unbinder;
    @BindView(R.id.bt_apply_for_activity)
    Button mBtApplyForActivity;
    @BindView(R.id.mci_activity_apply)
    MagicIndicator mMciActivityApply;

    private ArrayList<String> tableData = new ArrayList<>();
    private ArrayList<Fragment> fragmentList = new ArrayList<>();
    private String mText;
    private String mHomefragment;
    private boolean firstShowAll;

    @Override
    protected int inflateContentView() {

        return R.layout.fragment_activity_apply_for;
    }

    public static Fragment newInstance(Bundle bundle) {
        CommApplyForFragment fragment = new CommApplyForFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private void initData() {

        String userType = (String) SharedPreferencesUtil.getInstance(getContext()).read(Constant.USERTYPE);
        //1代表经销商；
        if ("1".equals(userType)) {
            tableData.add("全部");
            tableData.add("审批中");
            tableData.add("交易完成");
            tableData.add("已拒绝");
        } else {
            tableData.add("全部");
            tableData.add("审批中");
            tableData.add("待我审批");
            tableData.add("交易完成");
            tableData.add("已拒绝");
            //只有经销商才有下单按钮；
            mBtApplyForActivity.setVisibility(View.GONE);
        }
        Bundle arguments = getArguments();
        if (arguments != null) {
            mHomefragment = arguments.getString("homefragment");

            mBtApplyForActivity.setText(mHomefragment);
        }
        mText = (String) mBtApplyForActivity.getText();
        initTitle();
        mBtApplyForActivity.setOnClickListener(this);
    }

    private void initTitle() {
        for (int i = 0; i < tableData.size(); i++) {
            Bundle bundle = new Bundle();
            bundle.putString(Constant.JOIN_APPLYFROR_WAY, mHomefragment);
            fragmentList.add(ApplyForFragment.newInstance(bundle));
        }

        mVpOrderManager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 0 && positionOffset == 0.0 && !firstShowAll) {
                    EventBus.getDefault().post(tableData.get(0));
                }
            }

            @Override
            public void onPageSelected(int position) {
                firstShowAll = true;
                EventBus.getDefault().post(tableData.get(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        SaleListShowAdapter adapter = new SaleListShowAdapter(getFragmentManager(), fragmentList, tableData);
        mVpOrderManager.setAdapter(adapter);


        mMciActivityApply.setBackgroundColor(Color.parseColor("#ffffff"));
        CommonNavigator commonNavigator = new CommonNavigator(getContext());
        commonNavigator.setScrollPivotX(0.25f);
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return tableData == null ? 0 : tableData.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new SimplePagerTitleView(context);
                simplePagerTitleView.setText(tableData.get(index));
                simplePagerTitleView.setNormalColor(Color.parseColor("#333333"));
                simplePagerTitleView.setSelectedColor(Color.parseColor("#55aee9"));
                simplePagerTitleView.setTextSize(14);
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mVpOrderManager.setCurrentItem(index);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                indicator.setYOffset(UIUtil.dip2px(context, 3));
                indicator.setColors(Color.parseColor("#55aee9"));
                return indicator;
            }

            @Override
            public float getTitleWeight(Context context, int index) {
                return super.getTitleWeight(context, index);

            }
        });
        mMciActivityApply.setNavigator(commonNavigator);
        ViewPagerHelper.bind(mMciActivityApply, mVpOrderManager);
    }



    /**
     * 回到全部列表；
     *
     * @param event
     */
    @Subscribe
    public void refreshEvent(CommRefreshListEvent event) {
        boolean refresh = event.isRefresh();
        if (refresh) {
            mVpOrderManager.setCurrentItem(0);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        EventBus.getDefault().register(this);
        initData();
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
        OkHttpUtils.getInstance().cancelTag(getContext());
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(getContext(), ContentActivity.class);
        switch (mHomefragment) {
            case "促销活动申请":
                intent.putExtra(Constant.CONTENT_TYPE, Constant.APPLY_FOR_ACTIVITY_SUBMIT);
                startActivity(intent);
                break;
            case "产品陈列费用申请":
                intent.putExtra(Constant.CONTENT_TYPE, Constant.DISPLAY_APPLY_EDIT);
                intent.putExtra(Constant.APPLY_ACTIVITY_EDIT_JOIN_WAY, mHomefragment);
                startActivity(intent);
                break;
            case "其他费用申请":
                intent.putExtra(Constant.CONTENT_TYPE, Constant.OTHER_COST_SUBMIT);
                intent.putExtra(Constant.APPLY_ACTIVITY_EDIT_JOIN_WAY, mHomefragment);
                startActivity(intent);
                break;
            case "进场费用申请":
                Util.openContentActivity(getContext(), ContentActivity.class, Constant.SUBMIT_JOIN_SITE_COST_APPLY);
                break;

        }
    }

}
