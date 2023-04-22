package com.cas.team.e.auth.service;

import com.cas.team.e.auth.model.LoginUser;
import com.cas.team.e.auth.model.ResponseDTO;
import com.cas.team.e.auth.model.User;
import com.cas.team.e.auth.repository.UserRegistrationRepository;
import com.cas.team.e.auth.utils.CasUtils;
import org.jsoup.Jsoup;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.List;
import org.jsoup.nodes.Document;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

    public ResponseDTO getLoginUserTicket(LoginUser loginUser) throws Exception {
        CasUtils cas = new CasUtils();
        String ticket = cas.getCasTicket(loginUser);
        User user = findUserByUsername(loginUser.getUsername());
        ResponseDTO responseDTO = new ResponseDTO(ticket, user.getPermission());
        return responseDTO;
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
            List<User> userList = this.getAllUsers();
            int id = 0;
            if(userList == null || userList.isEmpty()) {
                user.setPermission("admin");
            }
            else {
                id = userList.stream().map(x -> x.getId()).max(Integer::compare).get() + 1;
                user.setPermission("user");
            }

            user.setId(id);
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
