package com.szu.trashsorting.category.viewpager;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class EachTrashTypePageViewModel extends ViewModel {

    private final MutableLiveData<Integer> mPageIndex = new MutableLiveData<>();
    private final LiveData<TrashTypeEntity> mPageContent = Transformations.map(mPageIndex, new Function<Integer, TrashTypeEntity>() {
        @Override
        public TrashTypeEntity apply(Integer input) {
            TrashTypeUtils trashTypeUtils=new TrashTypeUtils();
            return trashTypeUtils.getTrash(input);
        }
    });

    public void setPageIndex(int index) {
        mPageIndex.setValue(index);
    }

    public LiveData<TrashTypeEntity> getText() {
        return mPageContent;
    }
}