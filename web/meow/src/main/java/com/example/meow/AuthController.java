package com.example.meow;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.aot.AotServices;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Controller
public class AuthController {
    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }
    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@RequestParam String username, @RequestParam String password, HttpServletResponse response) {
        if (UserStore.addUser(username, password)) {
            return "redirect:/login";
        }
        return "redirect:/signup?error=exists";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, HttpServletResponse response) {
        if (UserStore.validateUser(username, password)) {
            String token = UserStore.createSession(username);
            Cookie cookie = new Cookie("SESSION_TOKEN", token);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            response.addCookie(cookie);
            return "redirect:/home";
        }
        return "redirect:/login?error=invalid";
    }

    @GetMapping("/home")
    public String home(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (c.getName().equals("SESSION_TOKEN")) {
                    String user = UserStore.getUsernameFromToken(c.getValue());
                    if (user != null) {
                        request.setAttribute("username", user);
                        return "home";
                    }
                }
            }
        }
        return "redirect:/login";
    }
    @PostMapping("/home/extract-text")
    @ResponseBody
    public Map<String, Object> handleFileUpload(@RequestParam("file") MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        if (file.isEmpty()) {
            result.put("error", "File is empty");
            return result;
        }
        try (PDDocument document = Loader.loadPDF(file.getInputStream().readAllBytes())) {
            document.getPages().forEach(page -> {
                try {
                    page.setAnnotations(new ArrayList<>()); // clears annotations
                } catch (Exception ignored) {
                }
            });
            PDFTextStripper stripper = new PDFTextStripper();
            String finalText = stripper.getText(document);
            result.put("fileName", file.getOriginalFilename());
            result.put("textLength", finalText.length());
            result.put("textSnippet",GeminiAPIController.callGeminiAPI(finalText)); // preview

        } catch (Exception e) {
            result.put("error", "Failed to read PDF: " + e.getMessage());
        }
        return result;
    }
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("SESSION_TOKEN".equals(cookie.getName())) {
                    // Invalidate server-side session
                    UserStore.invalidateToken(cookie.getValue());

                    // Expire the cookie
                    cookie.setMaxAge(0);
                    cookie.setPath("/");
                    response.addCookie(cookie);
                    break;
                }
            }
        }
        return "redirect:/login";
    }
}
