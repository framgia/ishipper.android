package com.framgia.ishipper.ui.fragment;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.framgia.ishipper.R;
import com.framgia.ishipper.ui.activity.ShopCreateOrderActivity;

import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by vuduychuong1994 on 7/22/16.
 */
public class ShopCreateOrderStep2Fragment extends Fragment
        implements RadioGroup.OnCheckedChangeListener {

    @BindView(R.id.edt_order_name) EditText mEdtOrderName;
    @BindView(R.id.edt_order_weight) EditText mEdtOrderWeight;
    @BindView(R.id.edt_order_price) EditText mEdtOrderPrice;
    @BindView(R.id.edt_ship_price) EditText mEdtShipPrice;
    @BindView(R.id.text_time) TextView mTextTime;
    @BindView(R.id.edt_customer_name) EditText mEdtCustomerName;
    @BindView(R.id.edt_customer_phone) EditText mEdtCustomerPhone;
    @BindView(R.id.rg_pick_time) RadioGroup mRgPickTime;
    @BindView(R.id.btn_submit) Button mBtnSubmit;
    @BindView(R.id.edt_note) EditText mEdtNote;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shop_create_order_step2, container, false);
        ButterKnife.bind(this, view);
        setEvent();
        return view;
    }

    private void setEvent() {
        mRgPickTime.setOnCheckedChangeListener(this);
    }

    @OnClick({R.id.text_time, R.id.btn_submit, R.id.rb_today_select, R.id.rb_tomorrow_select})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.text_time:
                mRgPickTime.setVisibility(
                        mRgPickTime.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                break;
            case R.id.btn_submit:
                // TODO: Set invoice
                ShopCreateOrderActivity.sInvoice.setName(mEdtOrderName.getText().toString());
                ShopCreateOrderActivity.sInvoice.setWeight(
                        Float.valueOf(mEdtOrderWeight.getText().toString()));
                ShopCreateOrderActivity.sInvoice.setPrice(
                        Float.valueOf(mEdtOrderPrice.getText().toString()));
                ShopCreateOrderActivity.sInvoice.setShippingPrice(
                        Float.valueOf(mEdtShipPrice.getText().toString()));
                ShopCreateOrderActivity.sInvoice.setDeliveryTime(mTextTime.getText().toString());
                ShopCreateOrderActivity.sInvoice.setCustomerName(mEdtCustomerName.getText().toString());
                ShopCreateOrderActivity.sInvoice.setCustomerNumber(mEdtCustomerPhone.getText().toString());
                ShopCreateOrderActivity.sInvoice.setDescription(mEdtNote.getText().toString());

                ((ShopCreateOrderActivity) getActivity()).addFragment(new ShopCreateOrderStep3Fragment());
                break;
            case R.id.rb_today_select:
                Calendar calendar = Calendar.getInstance(Locale.getDefault());
                TimePickerDialog dialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        StringBuilder timeBuilder = new StringBuilder();
                        timeBuilder.append(i).append(":").append(i1 + " ").append(getString(R.string.today));
                        mTextTime.setText(timeBuilder.toString());
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                dialog.show();
                break;
            case R.id.rb_tomorrow_select:
                Calendar cal = Calendar.getInstance(Locale.getDefault());
                TimePickerDialog timeDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        StringBuilder timeBuilder = new StringBuilder();
                        timeBuilder.append(i).append(":").append(i1 + " ").append(getString(R.string.tomorrow));
                        mTextTime.setText(timeBuilder.toString());
                    }
                }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);
                timeDialog.show();
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (i) {
            case R.id.rb_now:
                mTextTime.setText(R.string.pick_time_now);
                break;
            case R.id.rb_today_morning:
                mTextTime.setText(R.string.pick_time_today_morning);
                break;
            case R.id.rb_today_afternoon:
                mTextTime.setText(R.string.pick_time_today_afternoon);
                break;
            case R.id.rb_today_evening:
                mTextTime.setText(R.string.pick_time_today_evening);
                break;
            case R.id.rb_tomorrow_morning:
                mTextTime.setText(R.string.pick_time_tomorrow_morning);
                break;
            case R.id.rb_tomorrow_afternoon:
                mTextTime.setText(R.string.pick_time_tomorrow_afternoon);
                break;
            case R.id.rb_tomorrow_evening:
                mTextTime.setText(R.string.pick_time_tomorrow_evening);
                break;
            default:
                break;
        }
        radioGroup.setVisibility(View.GONE);
    }
}
