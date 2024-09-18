package com.ealas.restaurant_reservation_system.controller;

import com.ealas.restaurant_reservation_system.dto.PaymentDto;
import com.ealas.restaurant_reservation_system.dto.user.UserDto;
import com.ealas.restaurant_reservation_system.entity.User;
import com.ealas.restaurant_reservation_system.service.IPaymentService;
import com.ealas.restaurant_reservation_system.service.IUserService;
import com.ealas.restaurant_reservation_system.service.pdf.PaymentPdfService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final IPaymentService paymentService;
    private final IUserService userService;
    private final PaymentPdfService paymentPdfService;

    @Autowired
    public PaymentController(IPaymentService paymentService, IUserService userService, PaymentPdfService paymentPdfService) {
        this.paymentService = paymentService;
        this.userService = userService;
        this.paymentPdfService = paymentPdfService;
    }

    @Operation(summary = "Process a payment", description = "Process a payment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    // Endpoint para procesar un pago
    @PostMapping("/pay")
    public ResponseEntity<PaymentDto> processPayment(@RequestBody PaymentDto paymentDto) {
        PaymentDto createdPayment = paymentService.createPayment(paymentDto);
        return ResponseEntity.ok(createdPayment);
    }

    @Operation(summary = "Get the payment history for the current user", description = "Get the payment history for the current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("/user/history/pdf")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<byte[]> getPaymentHistoryPdf() {
        try {
            // Obtener el usuario autenticado
            User currentUser = userService.authUsuario();

            byte[] pdfContent = paymentPdfService.generatePdfForUser(currentUser.getId());

            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=payment_history.pdf");
            headers.set(HttpHeaders.CONTENT_TYPE, "application/pdf");
            headers.setContentLength(pdfContent.length);

            return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
