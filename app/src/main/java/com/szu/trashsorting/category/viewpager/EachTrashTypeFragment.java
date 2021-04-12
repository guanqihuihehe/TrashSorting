package com.szu.trashsorting.category.viewpager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.szu.trashsorting.common.web.WebActivity;
import com.szu.trashsorting.R;


public class EachTrashTypeFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private EachTrashTypePageViewModel eachTrashTypePageViewModel;

    String trashDetailUrl  ;
    public static EachTrashTypeFragment newInstance(int pageIndex) {
        EachTrashTypeFragment fragment = new EachTrashTypeFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, pageIndex);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eachTrashTypePageViewModel = ViewModelProviders.of(this).get(EachTrashTypePageViewModel.class);
        int pageIndex = 1;
        if (getArguments() != null) {
            pageIndex = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        eachTrashTypePageViewModel.setPageIndex(pageIndex);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_trash, container, false);
        final ImageView trashTypeImage = root.findViewById(R.id.trash_image);
        final TextView tipsType = root.findViewById(R.id.tips_type);
        final TextView tipsExplain = root.findViewById(R.id.tips_explain);
        final TextView tipsContain = root.findViewById(R.id.tips_contain);
        final TextView tipsTip = root.findViewById(R.id.tips_tip);

        final Context mainContext=this.getActivity();

        eachTrashTypePageViewModel.getText().observe(getViewLifecycleOwner(), new Observer<TrashTypeEntity>() {
            @Override
            public void onChanged(@Nullable TrashTypeEntity trashTypeEntity) {
                trashTypeImage.setImageResource(trashTypeEntity.getPicture());
                tipsType.setText(trashTypeEntity.getTitle());
                tipsContain.setText(trashTypeEntity.getContain());
                tipsExplain.setText(trashTypeEntity.getExplain());
                tipsTip.setText(trashTypeEntity.getTip());
                trashDetailUrl = trashTypeEntity.getUrl();
            }
        });

        trashTypeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mainContext, WebActivity.class);
                intent.putExtra("url",trashDetailUrl);
                startActivity(intent);
            }
        });
        return root;
    }
}