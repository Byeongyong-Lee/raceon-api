package com.raceon.api.domain.race;

import com.raceon.api.domain.race.entity.Race;
import com.raceon.api.domain.race.repository.RaceRepository;
import com.raceon.api.domain.race.service.RaceCrawlerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RaceCrawlerServiceTest {

    @Mock
    private RaceRepository raceRepository;

    @InjectMocks
    private RaceCrawlerService raceCrawlerService;

    @Test
    void crawl_실제사이트_파싱_성공() throws IOException {
        when(raceRepository.findBySourceId(anyString())).thenReturn(Optional.empty());
        when(raceRepository.save(any(Race.class))).thenAnswer(i -> i.getArgument(0));

        int count = raceCrawlerService.crawl(2026);

        System.out.println("크롤링된 대회 수: " + count);
        assertThat(count).isGreaterThan(0);
    }
}
