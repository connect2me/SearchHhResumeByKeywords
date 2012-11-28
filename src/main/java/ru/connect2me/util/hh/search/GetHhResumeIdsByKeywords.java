package ru.connect2me.util.hh.search;

import java.util.Set;
import ru.connect2me.util.hh.search.config.GetHhResumeIdsByKeywordsException;

/**
 * Получение всех id резуюме с hh.ru по строке запроса (ее мы отдаем в запрос hh.ru)
 * @author Зайнуллин Радик
 * @since 2012.11.28
 */
public class GetHhResumeIdsByKeywords implements IGetHhResumeIdsByKeywords {
    public Set<String> execute(String retrievalRequest) throws GetHhResumeIdsByKeywordsException {
        return null;
    }
}