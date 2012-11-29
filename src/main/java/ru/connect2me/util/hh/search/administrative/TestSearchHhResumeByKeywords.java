package ru.connect2me.util.hh.search.administrative;

import java.util.Properties;
import java.util.Set;
import ru.connect2me.util.hh.search.GetHhResumeIdsByKeywords;
import ru.connect2me.util.hh.search.config.GetHhResumeIdsByKeywordsException;

public class TestSearchHhResumeByKeywords {

  public static void main(String[] args) {
    try {
      Properties connectionProps = new Properties();
      connectionProps.put("page", "http://hh.ru/logon.do");
      connectionProps.put("user", "a8019111@yandex.ru");
      connectionProps.put("pwd", "YDz5iM");

      Set<String> set = new GetHhResumeIdsByKeywords(connectionProps).execute("Дизайнер одежды", 4);
      System.out.println("set.size() -> " + set.size());
    } catch (GetHhResumeIdsByKeywordsException ex) {
      System.out.println("Не удалось загрузить резюме." + ex.getMessage());
    }
  }
}