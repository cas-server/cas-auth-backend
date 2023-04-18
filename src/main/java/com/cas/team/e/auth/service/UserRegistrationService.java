package com.cas.team.e.auth.service;

import com.cas.team.e.auth.model.LoginUser;
import com.cas.team.e.auth.model.User;
import com.cas.team.e.auth.repository.UserRegistrationRepository;
import org.springframework.stereotype.Service;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class UserRegistrationService {
    private final UserRegistrationRepository userRegistrationRepository;
    private final String key = "354asdg4asdf451563ew4r1tasdf";

    public UserRegistrationService(UserRegistrationRepository userRegistrationRepository) {
        this.userRegistrationRepository = userRegistrationRepository;
    }

    public static void disableCertificateValidation() {
        TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                }
        };

        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }

        HostnameVerifier allHostsValid = (hostname, session) -> true;
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    }

    public LoginUser getLoginUserTicket(LoginUser loginUser) throws Exception {
        try {
            disableCertificateValidation();

            URL url = new URL("https://localhost:8443/cas/v1/tickets");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            String requestBody = "username=" + URLEncoder.encode(loginUser.getUsername(), StandardCharsets.UTF_8) +
                    "&password=" + URLEncoder.encode(loginUser.getPassword(), StandardCharsets.UTF_8);

            byte[] requestBodyBytes = requestBody.getBytes(StandardCharsets.UTF_8);
            OutputStream os = conn.getOutputStream();
            os.write(requestBodyBytes);
            os.flush();
            os.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

        } catch (Exception e) {
            throw new Exception("login was not successful");
        }

        return loginUser;
    }

    public User findUserByUsername(String username) {
        return userRegistrationRepository.findUserByUsername(username);
    }

    /**
     * Checks if user with the same username already exists and throws exception if true;
     * Creates new user-document in mongoDB database if username does not exist
     * @param user
     * @return
     * @throws Exception
     */
    public User saveUser(User user) throws Exception {
        User existingUser = userRegistrationRepository.findUserByUsername(user.getUsername());

        if (existingUser != null) {
            throw new Exception("Username already exists");
        }
        else {
            List<User> allUsers = getAllUsers();
            Integer maxId = allUsers.stream().map(x -> x.getId()).max(Integer::compare).get() + 1;
            user.setId(maxId);
        }

        return userRegistrationRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRegistrationRepository.findAll();
    }

    public void deleteUser(Integer id) {
        userRegistrationRepository.deleteById(id);
    }
}
