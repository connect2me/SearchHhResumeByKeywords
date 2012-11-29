package ru.connect2me.util.hh.search;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ru.connect2me.util.hh.search.config.GetHhResumeIdsByKeywordsException;
import ru.connect2me.util.hh.search.config.Module;
import ru.connect2me.util.hh.search.config.XMLConfiguration;
import ru.connect2me.util.hh.search.helper.HandlerSearchPage;
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
        if (!isFind) {
          throw new GetHhResumeIdsByKeywordsException("LoadSingleHhResume не смог залогинится на hh.ru");
        } else {
          //Мы получили страницу с ссылками на резюме
          HtmlPage resumeSearchPage = webClient.getPage("http://hh.ru/resumesearch/result?text=" + retrievalRequest);

          String resumeSearchPageStr = resumeSearchPage.asXml();
          Matcher m = Pattern.compile("a target=\"_blank\" href=\"/resume/([0-9a-z]+)(?:\\?query)?").matcher(resumeSearchPageStr);
          while (m.find()){
            System.out.println(m.group(1));
          }
          // разбор полученных ссылок, получение номеров вакансий
//          for (HtmlAnchor anchor : searchList) {
//            // HtmlAnchor[<a href="/resumesearch/result?actionSearch=actionSearch&amp;areaId=113&amp;p.salaryFrom=0&amp;p.salaryTo=0&amp;p.currencyCode=RUR&amp;p.gender=-1&amp;p.includeNoGender=true&amp;p.ageFrom=0&amp;p.ageTo=0&amp;p.includeNoAge=true&amp;p.educationId=0&amp;p.searchPeriod=30&amp;p.orderByMode=2&amp;p.relocationSearch=true&amp;p.includeNoSalary=true&amp;p.itemsOnPage=20&amp;p.keyword1=%5B%7B%22w%22%3A%22developer%22%2C%22l%22%3A%22normal%22%2C%22p%22%3A%5B%22full_text%22%5D%7D%5D%3D%3D%3D%21%3D%3D%3D">]
//            String anchorStr = anchor.toString();
//            Matcher m = Pattern.compile("(/resumesearch/.+)(?=\">\\])").matcher(anchorStr);
//            String link = null;
//            if (!m.find()) {
//              logger.debug("Не смогли получить ссылку на страницу с набором резюме из сохранного запроса.");
//            } else {
//              link = m.group();
//              HtmlPage searchPage = webClient.getPage("http://hh.ru" + link); // anchor.click();
//              set.addAll(new HandlerSearchPage().get(searchPage));
//            }
//          }
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