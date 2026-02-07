package com.auth.service.constants;

public class Constants {

        // ==================== SECURITY ====================

        // Public endpoints that don't require authentication
        public static final String[] EXCLUDED_PATHS = {
                        "/api/v1/auth/register",
                        "/api/v1/auth/authenticate",
                        "/api/v1/auth/refresh-token",
                        "/api/v1/auth/oauth2/**",
                        "/api/v1/auth/send-otp",
                        "/api/v1/auth/verify-otp",
                        "/api/v1/auth/login-with-otp",
                        "/api/v1/auth/forgot-password",
                        "/api/v1/auth/reset-password",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/swagger-ui.html",
                        "/swagger-ui/index.html"
        };

        // ==================== COMMON VALUES ====================

        public static final Integer ONE = 1;
        public static final String SUCCESS = "success";
        public static final String BEARER = "Bearer ";

        // ==================== JWT CLAIM KEYS ====================

        public static final String EMAIL = "email";
        public static final String MOBILE_NUMBER = "mobileNumber";
        public static final String ROLE = "role";
        public static final String SOURCE = "source";
        public static final String AUTHORIZATION = "Authorization";

        // ==================== ERROR MESSAGES ====================

        public static final String INVALID_CREDENTIALS = "Invalid credentials or registration is pending";
        public static final String USER_NOT_FOUND = "User not found";

        // ==================== NOTIFICATION TYPES ====================

        public static final String SEND_SMS = "message";
        public static final String SEND_WHATSAPP = "whatsapp";

        // ==================== WHATSAPP API ====================

        public static final String MESSAGE = "/message/nc";
        public static final String MESSAGE_TYPE_TEMPLATE = "template";
        public static final String MESSAGE_TYPE_MEDIA = "media_template";
        public static final String RECIPIENT_TYPE = "individual";
        public static final String LOCALE = "en";
        public static final String POLICY = "deterministic";
        public static final String X_API_HEADER = "custom_data";
}
