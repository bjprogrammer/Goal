package com.goalwise.goal.model;

import android.widget.TextView;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.library.baseAdapters.BR;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ListResponse extends BaseObservable {
    @Bindable
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        notifyPropertyChanged(BR.status);

    }

    private String status;

    private List<Data> data;

    @Bindable
    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
        notifyPropertyChanged(BR.data);
    }

    public static class Data extends BaseObservable{

        @SerializedName("fundname")
        private String name;

        @Bindable
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
            notifyPropertyChanged(BR.name);
        }

        @Bindable
        public int getMinimumAmount() {
            return minimumAmount;
        }

        public void setMinimumAmount(int minimumAmount) {
            this.minimumAmount = minimumAmount;
            notifyPropertyChanged(BR.minimumAmount);
        }

        @Bindable
        public int getMultiple() {
            return multiple;
        }

        public void setMultiple(int multiple) {
            this.multiple = multiple;
            notifyPropertyChanged(BR.multiple);
        }

        @SerializedName("minsipamount")
        private int minimumAmount;

        @SerializedName("minsipmultiple")
        private int multiple;

        @Bindable
        public List<Integer> getDates() {
            return dates;
        }

        public void setDates(List<Integer> dates) {
            this.dates = dates;
            notifyPropertyChanged(BR.dates);
        }

        @SerializedName("sipdates")
        private List<Integer> dates;
    }

    @BindingAdapter({"text"})
    public static void setText(TextView view, List<Integer> dates) {
        String text = " ";
        for (int i=0; i<dates.size(); i++){
            text = text+ dates.get(i);

            if(i!=dates.size()-1){
                text = text+ ", ";
            }
            else
            {
                text = text+ " ";
            }
        }

        view.setText(text);
    }

    @BindingAdapter({"minimumAmount"})
    public static void setMinimum(TextView view, int minAmount) {
        view.setText(" ₹ "+ minAmount+"");
    }

    @BindingAdapter({"multiple"})
    public static void sendMultiple(TextView view, int multiple) {
        view.setText(" ₹ "+ multiple+"");
    }
}
