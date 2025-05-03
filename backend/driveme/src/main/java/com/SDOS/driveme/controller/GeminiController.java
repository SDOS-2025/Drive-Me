package com.SDOS.driveme.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.SDOS.driveme.services.GeminiService;

@RestController
@RequestMapping("/api/chat")
public class GeminiController {

    private final GeminiService geminiService;

    public GeminiController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @PostMapping
    public ResponseEntity<String> chat(@RequestBody List<Map<String, String>> messages) {

        // If it's the first message in the chat, add the app context
        if (messages.size() == 1) {
            String appContext = """
                    You are a helpful assistant for the DriveMe platform.

                    DriveMe is a service where users can hire professional drivers to drive their own cars.
                    Users must sign up, verify their identity, and can then book a driver using the 'Book a Driver' section.
                    The user selects the pickup location, destination, time, and ride type (one-way/round-trip).
                    Drivers are assigned based on location and availability. Payments are handled through Razorpay.
                    Only verified users can hire drivers. Support is available 24/7.
                    """;

            // Inject app context at the top of the message list
            messages.add(0, Map.of(
                "role", "user",
                "message", appContext
            ));
        }

        String reply = geminiService.getResponse(messages);
        return ResponseEntity.ok(reply);
    }
}
