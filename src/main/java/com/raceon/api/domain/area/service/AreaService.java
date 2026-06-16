package com.raceon.api.domain.area.service;

import com.raceon.api.domain.area.dto.AreaResponse;
import com.raceon.api.domain.area.repository.AreaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AreaService {

    private final AreaRepository areaRepository;

    public List<AreaResponse> getAreas(Integer level, String parentCode) {
        if (level != null && parentCode != null) {
            return areaRepository.findByAreaLevelAndParentCodeOrderByAreaCode(level, parentCode)
                    .stream().map(AreaResponse::new).toList();
        }
        if (level != null) {
            return areaRepository.findByAreaLevelOrderByAreaCode(level)
                    .stream().map(AreaResponse::new).toList();
        }
        return areaRepository.findAllByOrderByAreaCode()
                .stream().map(AreaResponse::new).toList();
    }
}
