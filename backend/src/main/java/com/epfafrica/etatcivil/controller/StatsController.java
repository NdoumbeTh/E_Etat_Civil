package com.epfafrica.etatcivil.controller;

import com.epfafrica.etatcivil.dto.StatsDTO;
import com.epfafrica.etatcivil.service.StatsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping
    public StatsDTO obtenir() {
        return statsService.calculer();
    }
}