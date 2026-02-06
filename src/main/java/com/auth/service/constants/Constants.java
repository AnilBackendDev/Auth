package com.auth.service.constants;

public class Constants {
        public static final String[] EXCLUDED_PATHS = { "/api/v1/auth/authenticate", "/api/v1/user/generate-otp",
                        "/api/v1/user/validate-otp", "/api/v1/user/resend-otp", "/api/v1/assist/create",
                        "/api/v1/assist/get-all",
                        "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/index.html" };

        public static final Integer ONE = 1;

        public static final String REGISTER_MSG = "The user has successfully registered.";

        public static final String EMAIL = "email";
        public static final String MOBILE_NUMBER = "mobileNumber";
        public static final String ROLE = "role";

        public static final String INVALID_CREDENTIALS = "You have entered an invalid credentials or ur registration is pending";

        public static final String SOURCE_ERR_MSG = "source should not be empty or null";
        public static final String USER_NOT_FOUND = "User not found";

        public static final String AUTHORIZATION = "Authorization";
        public static final String BEARER = "Bearer ";

        public static final String SOURCE = "source";
        public static final String SUCCESS = "success";

        public static final String ADDR_ERR_MSG = "No addresses found for user";

        public static final String OPT_CONSENT_MANAGE = "/consent/manage";

        public static final String MESSAGE = "/message/nc";

        public static final String MESSAGE_TYPE_TEMPLATE = "template";

        public static final String MESSAGE_TYPE_MEDIA = "media_template";

        public static final String RECIPIENT_TYPE = "individual";

        public static final String LOCALE = "en";

        public static final String POLICY = "deterministic";

        public static final String X_API_HEADER = "custom_data";

        public static final String WEB = "WEB";

        public static final String SEND_SMS = "message";

        public static final String SEND_WHATSAPP = "whatsapp";

}
