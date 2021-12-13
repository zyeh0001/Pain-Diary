package viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private MutableLiveData<String> mText;
    private MutableLiveData<String> uEmail;
    public SharedViewModel(){
        mText = new MutableLiveData<String>();
        uEmail = new MutableLiveData<String>();
    }
    public void setMessage(String message) {
        mText.setValue(message);
    }
    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<String> getEmail() {
        return uEmail;
    }

    public void setEmail(String email) {
        uEmail.setValue(email);
    }
}
