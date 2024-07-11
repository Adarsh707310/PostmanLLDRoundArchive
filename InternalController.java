package org.example.controllers;

import org.example.dtos.ConfigDto;
import org.example.enums.Type;
import org.example.service.FlowConfigService;
import org.example.service.RateLimitWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/invoice/internal")
@Validated
public class InternalController {

    @Autowired
    private FlowConfigService flowConfigService;

    @Autowired
    private RateLimitWrapper rateLimitWrapper;

    @PostMapping(value = "/config")
    public ResponseEntity<ConfigDto> processInvoiceApprovalConfig(@RequestBody @Valid ConfigDto configDto) {
        return ResponseEntity.ok().body(flowConfigService.processInvoiceApprovalConfig(configDto));
    }

    @GetMapping(value = "/config")
    public ResponseEntity<ConfigDto> getInvoiceApprovalConfig(@RequestParam(name = "approval-type") @NotNull Type type, @RequestParam(name = "business-id") @NotNull UUID businessId, @RequestParam(name = "location-code") @NotNull String locationCode, HttpServletRequest httpServletRequest) {

        String ip = httpServletRequest.getHeader("IP");
        Boolean isRateLimitExceeded = rateLimitWrapper.getRateLimitExceeded(ip);

        if(isRateLimitExceeded){
            throw new RuntimeException("RateLimitExceeded");
        }

        return ResponseEntity.ok().body(flowConfigService.getInvoiceApprovalConfig(type, businessId, locationCode));
    }
}
