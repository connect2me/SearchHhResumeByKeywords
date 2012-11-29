package ru.connect2me.util.hh.search;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ru.connect2me.util.hh.search.config.GetHhResumeIdsByKeywordsException;
import ru.connect2me.util.hh.search.config.Module;
import ru.connect2me.util.hh.search.config.XMLConfiguration;
import ru.connect2me.util.hh.search.helper.ProfilePage;

/**
 * Получение всех id резюме с hh.ru по строке запроса (ее мы отдаем в запрос hh.ru)
 *
 * @author Зайнуллин Радик
 * @since 2012.11.28
 */
public class GetHhResumeIdsByKeywords extends Module implements IGetHhResumeIdsByKeywords {
  private static String checkInput = "(?:\\pL+\\s*)+";
  private Properties connectionProps;

  public GetHhResumeIdsByKeywords(Properties connectionProps) throws GetHhResumeIdsByKeywordsException {
    super(new XMLConfiguration(GetHhResumeIdsByKeywords.class.getResourceAsStream("/config-SearchHhResumeByKeywords.xml")));
    this.connectionProps = connectionProps;
  }

  public Set<String> execute(String retrievalRequest) throws GetHhResumeIdsByKeywordsException {
    Set<String> set = new HashSet<String>();
    if (!retrievalRequest.matches(checkInput)) {
      logger.debug("Входная строка '" + retrievalRequest + "' не соответствуют критерию запроса - '" + checkInput + "'");
      return /* empty */ new HashSet<String>();
    } else {
      try {
        WebClient webClient = new WebClient();
        HtmlPage profilePage = new ProfilePage(connectionProps).get(webClient);
        boolean isFind = profilePage.asXml().contains("клиент 774702");
        if (!isFind) throw new GetHhResumeIdsByKeywordsException("LoadSingleHhResume не смог залогинится на hh.ru");
        else { //Мы получили страницу с ссылками на резюме
          HtmlPage resumeSearchPage = webClient.getPage("http://hh.ru/resumesearch/result?text=" + retrievalRequest);
          String resumeSearchPageStr = resumeSearchPage.asXml();
          Matcher m = Pattern.compile("href=\"/resume/([0-9a-z]+)(?:\\?query)?").matcher(resumeSearchPageStr);
          while (m.find()){
            set.add(m.group(1));
          }
        }
      } catch (FailingHttpStatusCodeException ex) {
        throw new GetHhResumeIdsByKeywordsException("Не удалось авторизоваться на сервере hh.ru. " + ex.getMessage());
      } catch (MalformedURLException ex) {
        throw new GetHhResumeIdsByKeywordsException("Не удалось получить доступ к странице 'http://hh.ru/resumesearch'. " + ex.getMessage());
      } catch (IOException ex) {
        throw new GetHhResumeIdsByKeywordsException("Не удалось получить доступ к странице 'http://hh.ru/resumesearch'. " + ex.getMessage());
      }
    }
    return set;
  }
}