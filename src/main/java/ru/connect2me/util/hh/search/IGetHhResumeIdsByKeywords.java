package ru.connect2me.util.hh.search;

import java.util.Set;
import ru.connect2me.util.hh.search.config.GetHhResumeIdsByKeywordsException;

/**
 * Это интерфейс для использования во внешних (по отношению к библиотеке) приложениях.
 * @author Зайнуллин Радик
 * @since 2012.11.28
 */
public interface IGetHhResumeIdsByKeywords {
   public Set<String> execute(String retrievalRequest) throws GetHhResumeIdsByKeywordsException;
}
