/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.haoniu.aixin.adapter;

import android.view.LayoutInflater;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.haoniu.aixin.http.AppConfig;
import com.haoniu.aixin.R;
import com.haoniu.aixin.base.MyApplication;
import com.haoniu.aixin.base.MyHelper;
import com.haoniu.aixin.widget.EaseImageView;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.zds.base.ImageLoad.GlideUtils;

import java.util.List;

public class GroupAdapter extends BaseQuickAdapter<EMGroup, BaseViewHolder> {

    private LayoutInflater inflater;

    public GroupAdapter(List<EMGroup> groups) {
        super(R.layout.em_row_group, groups);
    }

    /**
     * Implement this method and use the helper to adapt the view to the given item.
     *
     * @param helper A fully initialized helper.
     * @param item   The item that needs to be displayed.
     */
    @Override
    protected void convert(BaseViewHolder helper, EMGroup item) {
        MyApplication.getInstance().setAvatar((EaseImageView) helper.getView(R.id.avatar));
        EMGroup group = EMClient.getInstance().groupManager().getGroup(item.getGroupId());
        GlideUtils.loadImageViewLoding(AppConfig.checkimg( MyHelper.getInstance().getGroupById(item.getGroupId()).getHead()), (ImageView) helper.getView(R.id.avatar), R.mipmap.defult_group_icon);
        helper.setText(R.id.name, item.getGroupName());
    }


}
