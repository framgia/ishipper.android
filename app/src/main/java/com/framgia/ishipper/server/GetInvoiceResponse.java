package com.framgia.ishipper.server;
import com.framgia.ishipper.model.Invoice;
import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Created by vuduychuong1994 on 8/9/16.
 */
public class GetInvoiceResponse {

    @SerializedName("invoices") private List<Invoice> mInvoiceList;

    public List<Invoice> getInvoiceList() {
        return mInvoiceList;
    }

    public void setInvoiceList(List<Invoice> invoiceList) {
        mInvoiceList = invoiceList;
    }
}
