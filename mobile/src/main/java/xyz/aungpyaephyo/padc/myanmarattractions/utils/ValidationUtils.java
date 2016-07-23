package xyz.aungpyaephyo.padc.myanmarattractions.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by NayLinAung on 7/21/2016.
 */
public class ValidationUtils {
    // should be imported into Utils class
    public static boolean isEmailValid(String email) {
        Pattern pattern = Pattern.compile(MyanmarAttractionsConstants.EMAIL_PATTERN, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }
}
