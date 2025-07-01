package com.liu.ai.controller;

import com.liu.ai.model.VacationRequest;
import com.liu.ai.service.VacationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vacation")
@CrossOrigin(origins = "*")
public class VacationController {

    private final VacationService vacationService;

    @Autowired
    public VacationController(VacationService vacationService) {
        this.vacationService = vacationService;
    }

    @PostMapping("/plan")
    public String generateVacationPlan(@RequestBody VacationRequest request) {
        return vacationService.generateVacationPlan(request);
    }
}
