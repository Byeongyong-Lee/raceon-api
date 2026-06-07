package com.raceon.api.domain.race.service;

import com.raceon.api.domain.race.entity.Race;
import com.raceon.api.domain.race.repository.RaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class RaceCrawlerService {

    private static final String LIST_URL = "http://www.roadrun.co.kr/schedule/list.php";
    private static final String DETAIL_URL = "http://www.roadrun.co.kr/schedule/view.php?no=";
    private static final Pattern SOURCE_ID_PATTERN = Pattern.compile("no=(\\d+)");

    private final RaceRepository raceRepository;

    @Scheduled(cron = "0 0 3 * * *")
    public void scheduledCrawl() {
        log.info("마라톤 대회 크롤링 시작");
        try {
            int count = crawl(LocalDate.now().getYear());
            log.info("크롤링 완료: {}개 저장/갱신", count);
        } catch (Exception e) {
            log.error("크롤링 실패: {}", e.getMessage(), e);
        }
    }

    @Transactional
    public int crawl(int year) throws IOException {
        String url = LIST_URL + "?syear_key=" + year;

        byte[] bytes = Jsoup.connect(url)
                .timeout(15000)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                .execute()
                .bodyAsBytes();

        Document doc = Jsoup.parse(new ByteArrayInputStream(bytes), "EUC-KR",
                "http://www.roadrun.co.kr");

        Elements rows = doc.select("table[width=600] tr");
        int count = 0;

        for (Element row : rows) {
            Elements tds = row.select("> td");
            if (tds.size() != 4) continue;

            String dateText = Optional.ofNullable(tds.get(0).selectFirst("font[size=4]"))
                    .map(e -> e.text().trim()).orElse("");
            if (!dateText.matches("\\d+/\\d+")) continue;

            Element nameLink = tds.get(1).selectFirst("a");
            if (nameLink == null) continue;

            String sourceId = extractSourceId(nameLink.attr("href"));
            if (sourceId == null) continue;

            String name = nameLink.text().trim();
            String course = Optional.ofNullable(tds.get(1).selectFirst("font[size=2]"))
                    .map(e -> e.text().trim()).orElse("");
            String location = tds.get(2).text().trim();

            String[] col4Parts = tds.get(3).text().trim().split("☎");
            String organizer = col4Parts[0].trim();
            String phone = col4Parts.length > 1
                    ? col4Parts[1].trim().split("\\s")[0] : "";

            Element homepageEl = tds.get(3).selectFirst("a[href^=http]");
            String homepage = homepageEl != null ? homepageEl.attr("href") : "";

            LocalDate raceDate = parseDate(dateText, year);

            raceRepository.findBySourceId(sourceId).ifPresentOrElse(
                    existing -> {
                        existing.update(name, raceDate, location, course, organizer, phone, homepage);
                        raceRepository.save(existing);
                    },
                    () -> raceRepository.save(Race.builder()
                            .sourceId(sourceId)
                            .name(name)
                            .raceDate(raceDate)
                            .location(location)
                            .course(course)
                            .organizer(organizer)
                            .phone(phone)
                            .homepage(homepage)
                            .detailUrl(DETAIL_URL + sourceId)
                            .build())
            );
            count++;
        }
        return count;
    }

    private String extractSourceId(String href) {
        Matcher m = SOURCE_ID_PATTERN.matcher(href);
        return m.find() ? m.group(1) : null;
    }

    private LocalDate parseDate(String dateText, int year) {
        try {
            String[] parts = dateText.split("/");
            return LocalDate.of(year, Integer.parseInt(parts[0].trim()),
                    Integer.parseInt(parts[1].trim()));
        } catch (Exception e) {
            return null;
        }
    }
}
