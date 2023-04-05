package orm.utilities;

import java.lang.reflect.Method;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;

import orm.database.object.relation.ModelField;

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

  public static void setObjectFieldValue(Object object, Object data, ModelField modelField) throws Exception {

    Object castedData;
    Method setter = object.getClass().getMethod(toCamelCase("set", modelField.getOriginalName()),
        modelField.getClassType());

    if (data == null || data.toString().trim().length() == 0 || data.toString().trim().toLowerCase().equals("null")) {
      castedData = null;
    } else {
      switch (modelField.getClassType().getSimpleName()) {
        case "Date":
          castedData = convertToSqlDate(data.toString().trim());
          break;

        case "Timestamp":
          castedData = Timestamp.valueOf(data.toString().trim());
          break;

        case "Time":
          castedData = Time.valueOf(data.toString().trim());
          break;

        case "Integer":
          castedData = Integer.parseInt(data.toString().trim());
          break;

        case "Double":
          castedData = Double.parseDouble(data.toString().trim());
          break;

        default:
          castedData = data.toString().trim();
          break;
      }
    }

    setter.invoke(object, castedData);

  }

  public static Date convertToSqlDate(String date) throws Exception {
    try {
      return Date.valueOf(date);
    } catch (IllegalArgumentException e) {
      try {
        java.text.SimpleDateFormat sourceFormat = new java.text.SimpleDateFormat("dd/MM/yy");
        java.util.Date utilDate = sourceFormat.parse(date);
        java.text.SimpleDateFormat targetFormat = new java.text.SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = targetFormat.format(utilDate);
        return java.sql.Date.valueOf(formattedDate);
      } catch (IllegalArgumentException e2) {
        throw new Exception("DateFormatException: " + date
            + " is not a valid date format.\n\nValid format : [dd/MM/yy], [yyyy-MM-dd]");
      }
    }
  }
}
