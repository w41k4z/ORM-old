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

    switch (modelField.getClassType().getSimpleName()) {
      case "Date":
        castedData = data == null ? null : Date.valueOf(data.toString());
        break;
      
      case "Timestamp":
        castedData = data == null ? null : Timestamp.valueOf(data.toString());
        break;
      
      case "Time":
        castedData = data == null ? null : Time.valueOf(data.toString());
        break;
      
      case "Integer":
        castedData = data == null ? null : Integer.parseInt(data.toString());
        break;
      
      case "Double":
        castedData = data == null ? null : Double.parseDouble(data.toString());
        break;
      
      default:
        castedData = data == null ? null : data.toString();
        break;
    }

    setter.invoke(object, castedData);

  }
}
