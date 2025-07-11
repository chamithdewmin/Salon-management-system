package com.salon.Utils;

import com.salon.Model.PaymentSplit;

import java.util.ArrayList;
import java.util.List;

public class PaymentSplitter {

    public static List<PaymentSplit> split(double amount, String staffName) {
        List<PaymentSplit> splits = new ArrayList<>();

        if (staffName.equalsIgnoreCase("Owner")) {
            splits.add(new PaymentSplit("Owner", amount));
        } else {
            double half = amount / 2;
            splits.add(new PaymentSplit(staffName, half));
            splits.add(new PaymentSplit("Owner", half));
        }

        return splits;
    }
}
