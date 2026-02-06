package com.auth.notification;

import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@Slf4j
@Service
public class NotificationService {

    public void sendWhatsAppTextMessage(String mobileNumber, String templateName, List<String> params, String mediaUrl,
            boolean isButton, String buttonUrl) {
        log.info("Sending WhatsApp message to {}: Template={}, Params={}", mobileNumber, templateName, params);
        // Implementation for sending WhatsApp message
    }

    public void sendSms(String mobileNumber, String message) {
        log.info("Sending SMS to {}: {}", mobileNumber, message);
        // Implementation for sending SMS
    }
}
