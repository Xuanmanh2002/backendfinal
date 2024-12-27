package com.springproject.dhVinh.SpringBootProject.controller;

import com.springproject.dhVinh.SpringBootProject.dto.PaymentDTO;
import com.springproject.dhVinh.SpringBootProject.dto.TransactionDTO;
import com.springproject.dhVinh.SpringBootProject.security.payment.VNPayConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {


    @GetMapping("/payment-infor")
    public ResponseEntity<?> transaction(@RequestParam(value = "vnp_Amount") String amount,
                                         @RequestParam(value = "vnp_BankCode") String bankCode,
                                         @RequestParam(value = "vnp_OrderInfo") String order,
                                         @RequestParam(value = "vnp_ResponseCode") String responseCode) {
        TransactionDTO transactionDTO = new TransactionDTO();
        if (responseCode.equals("00")) {
            transactionDTO.setStatus("ok");
            transactionDTO.setMessage("success");
            transactionDTO.setData("");
        } else {
            transactionDTO.setStatus("no");
            transactionDTO.setMessage("failed");
            transactionDTO.setData("");
        }
        return ResponseEntity.status(HttpStatus.OK).body(transactionDTO);
    }
}