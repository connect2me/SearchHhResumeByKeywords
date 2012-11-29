package ru.connect2me.util.hh.search.helper;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ru.connect2me.util.hh.search.config.GetHhResumeIdsByKeywordsException;


/**
 * Обработка страницы "поиск по запросу"
 *
 * @author Зайнуллин Радик
 * @version 1.0
 * @since 2012.11.18
 */
public class HandlerSearchPage {
  private static String path = "//tr[@class='output__item HH-Employer-ResumeFolders-Resume' " + 
                                 "or @class='output__item HH-Employer-ResumeFolders-Resume output__item_visited']";
  public Set<String> get(HtmlPage searchPage) throws GetHhResumeIdsByKeywordsException {
    Set<String> set = new HashSet<String>();
    // new LocalWriter().write("test/searchPage.xhtml", searchPage.asXml());
    List<HtmlTableRow> rows = (List<HtmlTableRow>) searchPage.getByXPath(path);

    for (HtmlTableRow row : rows) {
      String trStr = row.asXml();
      // new LocalWriter().write("test/row" + i++ + ".xml", trStr);
      // поиск ссылок резюме - на одной странице их может быть несколько (сколько у пользователя резюме)
      String exp = "href=\"\\/resume\\/([^\"\\?]+)[^\"\\?]*\"";
      Matcher m = Pattern.compile(exp, Pattern.DOTALL).matcher(trStr);
      while (m.find()) {
        set.add(m.group(1));
      }
    }
    return set;
  }
}