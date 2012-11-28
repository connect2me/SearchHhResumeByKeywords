package ru.connect2me.util.hh.search;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ru.connect2me.util.hh.search.config.GetHhResumeIdsByKeywordsException;
import ru.connect2me.util.hh.search.config.Module;
import ru.connect2me.util.hh.search.config.XMLConfiguration;
import ru.connect2me.util.hh.search.helper.ProfilePage;
import ru.connect2me.util.hh.search.util.Check;

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
          HtmlPage resumeSearchPage = webClient.getPage("http://hh.ru/resumesearch");
          if (checkIfSearchTextFieldExists(resumeSearchPage.asXml())) ;
          
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

  private boolean checkIfSearchTextFieldExists(String strHtml) {
    //Matcher m = Pattern.compile("<input type=\"search\" name=\"text\" class=\"b-autocomplete HHSearch-Wizard-Input search__field jsxComponent-AutoComplete-Input HH-FirstPageTabs-Vacancies-Keyword\" value=\"штукатур маляр\" autocomplete=\"off\" />").matcher(strHtml);
    if (strHtml.matches("(?s)\\s*") || !new Check().isWellFormed(strHtml)) return false;
    else {
      try {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true); // never forget this!
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(strHtml.getBytes("UTF-8")));
        XPath xpath = XPathFactory.newInstance().newXPath();

        NodeList nodes = (NodeList) xpath.evaluate("/root/tr", doc, XPathConstants.NODESET);
        for (int j = 0; j < nodes.getLength(); j++) {
          Document newXmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
          Node node = nodes.item(j);
          Node copyNode = newXmlDocument.importNode(node, true);
          newXmlDocument.appendChild(copyNode);
          NodeList nodesYear = (NodeList) xpath.evaluate("/tr/td/text()", newXmlDocument, XPathConstants.NODESET);
          NodeList nodesOrganization = (NodeList) xpath.evaluate("/tr/td/div[count(@*)=0]", newXmlDocument, XPathConstants.NODESET);
          NodeList nodesSpecialty = (NodeList) xpath.evaluate("/tr/td/div[@class='resume__education__org']", newXmlDocument, XPathConstants.NODESET);
          String year = nodesYear.item(0).getTextContent().trim().replaceAll("\r\n|\r|\n", "");
          String organization = nodesOrganization.item(0).getTextContent().trim().replaceAll("\r\n|\r|\n", "");
          String specialty = nodesSpecialty.item(0).getTextContent().trim().replaceAll("\r\n|\r|\n", "");
        }
      } catch (Exception ex) {
        //throw new ParserHtmlHhResumeToInhouseXmlException("Ошибка при разборе документа для нахождения Education " + ex.getMessage());

      }
    }
    
    return false;
  }
}