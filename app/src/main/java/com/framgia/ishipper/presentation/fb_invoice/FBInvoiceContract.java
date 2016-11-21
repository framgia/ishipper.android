package com.framgia.ishipper.presentation.fb_invoice;

import com.framgia.ishipper.base.BasePresenter;
import com.framgia.ishipper.base.BaseView;

/**
 * Created by dinhduc on 21/11/2016.
 */

class FBInvoiceContract {
    interface View extends BaseView {

    }

    interface Presenter extends BasePresenter {
        void showPinnedInvoice();

        void backToFacebookInvoice();
    }
}
