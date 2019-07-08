package com.jmd;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Uteis {
    private Uteis() {}

    public static String formataDataParaString(long timestamp) {
        Date d = new Date();
        d.setTime(timestamp);

        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, new Locale("pt","BR"));
        return sdf.format(d);
    }

    public static Date formataData(long timestamp) {
        Date d = new Date();
        d.setTime(timestamp);
        return d;
    }

    public static Date formataData(String string) {
        try {
            String myFormat = "dd/MM/yyyy";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, new Locale("pt","BR"));
            return sdf.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
