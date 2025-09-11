package com.example.splunklite.config;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfig {
  @Bean
  public JavaMailSender mailSender(Environment env){
    JavaMailSenderImpl impl = new JavaMailSenderImpl();
    String host = env.getProperty("spring.mail.host");
    if (host != null) impl.setHost(host);
    String port = env.getProperty("spring.mail.port");
    if (port != null){
      try { impl.setPort(Integer.parseInt(port)); } catch(NumberFormatException ignored){}
    }
    String user = env.getProperty("spring.mail.username");
    if (user != null) impl.setUsername(user);
    String pass = env.getProperty("spring.mail.password");
    if (pass != null) impl.setPassword(pass);

    Properties props = new Properties();
    String auth = env.getProperty("spring.mail.properties.mail.smtp.auth");
    if (auth != null) props.put("mail.smtp.auth", auth);
    String starttls = env.getProperty("spring.mail.properties.mail.smtp.starttls.enable");
    if (starttls != null) props.put("mail.smtp.starttls.enable", starttls);
    impl.setJavaMailProperties(props);

    // sensible defaults to avoid null-host causing missing bean issues
    if (impl.getHost()==null || impl.getHost().isBlank()) impl.setHost("localhost");
    if (impl.getPort()==0) impl.setPort(25);
    return impl;
  }
}
