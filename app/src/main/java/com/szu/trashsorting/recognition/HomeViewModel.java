package com.szu.trashsorting.recognition;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.szu.trashsorting.recognition.text.TextResultEntity;

import java.util.List;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<List<TextResultEntity>> mTrashRecognitionList = new MutableLiveData<>();

//    private LiveData<List<TextResultEntity>> mText = Transformations.map(mTrashRecognitionList, new Function<String, List<TextResultEntity>>() {
//        @Override
//        public List<TextResultEntity> apply(String input) {
//            TextRecognitionUtils textRecognitionUtils =new TextRecognitionUtils();
//            List<TextResultEntity> textResultEntityList = textRecognitionUtils.getTextRecognitionResultList(input);
//            return textResultEntityList;
//        }
//    });

    public void setTrashRecognitionList(List<TextResultEntity> textResultList) {
        mTrashRecognitionList.setValue(textResultList);
    }
    public void postTrashRecognitionList(List<TextResultEntity> textResultList) {
        mTrashRecognitionList.postValue(textResultList);
    }
    public void postTrashRecognitionNull(int result) {
        mTrashRecognitionList.postValue(null);
    }
    public LiveData<List<TextResultEntity>> getTrashRecognitionList(){
        return mTrashRecognitionList;
    }

//    public LiveData<List<TextResultEntity>> getText() {
//        return mText;
//    }
}