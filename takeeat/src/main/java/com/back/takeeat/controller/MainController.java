package com.back.takeeat.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@Controller
public class MainController {

    @Value("${KAKAO_API_KEY}")
    String KAKAO_API_KEY;

    @GetMapping("/")
    public String mainPage(Model model, HttpSession session) {

        model.addAttribute("KAKAO_API_KEY", KAKAO_API_KEY);
        return "mainPage/index";
    }

    @PostMapping("/saveGPSInfo")
    public ResponseEntity<String> saveGPSInfo(@RequestBody Map<String, Object> gpsData, HttpSession session) {
        double latitude = 0.0;
        double longitude = 0.0;
        if (gpsData.get("latitude") instanceof Double) {
            latitude = (Double) gpsData.get("latitude");
            longitude = (Double) gpsData.get("longitude");
        } else if (gpsData.get("latitude") instanceof String) {
            latitude = Double.parseDouble((String) gpsData.get("latitude"));
            longitude = Double.parseDouble((String) gpsData.get("longitude"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("데이터 저장 실패");
        }
        String addr = (String) gpsData.get("addr");

        session.setAttribute("latitude", latitude);
        session.setAttribute("longitude", longitude);
        session.setAttribute("addr", addr);

        return ResponseEntity.ok("위치 정보 저장 성공");
    }
}
