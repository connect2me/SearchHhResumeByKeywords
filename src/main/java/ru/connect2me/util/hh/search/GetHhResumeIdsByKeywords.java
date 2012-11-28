package ru.connect2me.util.hh.search;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    super(new XMLConfiguration(GetHhResumeIdsByKeywords.class.getResourceAsStream("/config-GetHhResumeIdsByKeywords.xml")));
    this.connectionProps = connectionProps;
  }

  public Set<String> execute(String retrievalRequest) throws GetHhResumeIdsByKeywordsException {
    if (!retrievalRequest.matches(checkInput)) {
      logger.debug("Входная строка '" + retrievalRequest + "' не соответствуют критерию запроса - '" + checkInput + "'");
      return /* empty */ new HashSet<String>();
    } else {
      try {
        WebClient webClient = new WebClient();
        HtmlPage profilePage = new ProfilePage(connectionProps).get(webClient);
        boolean isFind = profilePage.asXml().contains("клиент 774702");
        if (!isFind) {
          throw new GetHhResumeIdsByKeywordsException("LoadSingleHhResume не смог залогинится на hh.ru");
        } else {
          //Мы авторизовались, теперь нам надо перейти на страницу http://hh.ru/resumesearch
          Page resumeSearchPage = webClient.getPage("http://hh.ru/resumesearch");
          // <input type="search" name="text" class="b-autocomplete HHSearch-Wizard-Input search__field jsxComponent-AutoComplete-Input HH-FirstPageTabs-Vacancies-Keyword" value="штукатур маляр" autocomplete="off" />
          //if (checkIfInputTextFieldExists(resumeSearchPage))
          
        }
      } catch (FailingHttpStatusCodeException ex) {
        throw new GetHhResumeIdsByKeywordsException("Не удалось авторизоваться на сервере hh.ru. " + ex.getMessage());
      } catch (MalformedURLException ex) {
        throw new GetHhResumeIdsByKeywordsException("Не удалось получить доступ к странице 'http://hh.ru/resumesearch'. " + ex.getMessage());
      } catch (IOException ex) {
        throw new GetHhResumeIdsByKeywordsException("Не удалось получить доступ к странице 'http://hh.ru/resumesearch'. " + ex.getMessage());
      }
    }
    return /* empty */ new HashSet<String>();
  }
}