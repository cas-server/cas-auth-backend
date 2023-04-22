package com.cas.team.e.auth.utils;

import com.cas.team.e.auth.model.LoginUser;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
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
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CasUtils {

    public CasUtils() {}

    public String getCasTicket(LoginUser loginUser) throws Exception {
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

            String responseString = response.toString();
            Pattern pattern = Pattern.compile("TGT-\\w+-\\w+[^\"]+");
            Matcher matcher = pattern.matcher(responseString);
            if (matcher.find()) {
                String ticket = matcher.group();
                return ticket;
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new Exception("login was not successful");
        }
    }

    public void deleteCASTicket(String ticket) throws Exception {
        String casServerUrl = "https://localhost:8443/cas";
        String url = casServerUrl + "/v1/tickets/" + ticket;

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("DELETE");

        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("Content-Type", "application/json");

        String userpass = "username:password";
        String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userpass.getBytes()));
        con.setRequestProperty("Authorization", basicAuth);

        int responseCode = con.getResponseCode();
        if (responseCode != 200) {
            throw new Exception("Ticket could not be deleted");
        }
    }

    public boolean validateCASTicket(String ticket) throws IOException {
        try {
            String ticketId = ticket;
            String serviceUrl = "https://localhost";

            URL url = new URL("https://localhost:8443/cas/p3/serviceValidate?ticket=" + ticketId + "&service=" + serviceUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            int status = con.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            if (content.toString().contains("INVALID_TICKET")) {
                System.out.println("invalid ticket");
                return false;
            }
            else {
                System.out.println("valid ticket");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
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
}
