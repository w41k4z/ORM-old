package orm.utilities;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;

public class Treatment {

  public static String toCamelCase(String s1, String s2) {
    StringBuffer S2 = new StringBuffer(s2);
    S2.setCharAt(0, Character.toUpperCase(S2.charAt(0)));
    return s1.concat(S2.toString());
  }

  public static String[] arrayListToStringArray(ArrayList<String> arrayList) {
    String[] newArray = new String[arrayList.size()];
    for (int i = 0; i < newArray.length; i++) {
      newArray[i] = arrayList.get(i);
    }
    return newArray;
  }

  public static String getCurrentDate() {
    return new Date(System.currentTimeMillis()).toString();
  }

  public static Date getCurrentDate(boolean isDateType) {
    if (isDateType)
      return new Date(System.currentTimeMillis());
    return null;
  }

  public static String getCurrentTimeStamp() {
    return new Timestamp(System.currentTimeMillis()).toString();
  }

  public static Timestamp getCurrentTimeStamp(boolean isTimestampType) {
    if (isTimestampType)
      return new Timestamp(System.currentTimeMillis());
    return null;
  }

}
