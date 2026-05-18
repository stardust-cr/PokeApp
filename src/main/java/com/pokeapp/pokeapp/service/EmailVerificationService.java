package com.pokeapp.pokeapp.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailVerificationService {
    private final JavaMailSender mailSender;
    private final Map<String, CodigoInfo> codigos = new ConcurrentHashMap<>();

    public EmailVerificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarCodigo(String email) {
        String codigo = generarCodigo();
        codigos.put(email.toLowerCase(), new CodigoInfo(codigo, LocalDateTime.now().plusMinutes(10)));
        enviarEmail(email, codigo);
    }

    public boolean verificar(String email, String codigoIntroducido) {
        CodigoInfo info = codigos.get(email.toLowerCase());
        if (info == null) return false;                          // no se envió código
        if (LocalDateTime.now().isAfter(info.expira)) {         // expirado
            codigos.remove(email.toLowerCase());
            return false;
        }
        if (!info.codigo.equals(codigoIntroducido.trim())) return false; // incorrecto
        codigos.remove(email.toLowerCase());                    // usar solo una vez
        return true;
    }

    public boolean tieneCodigoPendiente(String email) {
        CodigoInfo info = codigos.get(email.toLowerCase());
        return info != null && LocalDateTime.now().isBefore(info.expira);
    }

    private String generarCodigo() {
        SecureRandom rnd = new SecureRandom();
        int num = rnd.nextInt(900000) + 100000; // 100000–999999
        return String.valueOf(num);
    }

    private void enviarEmail(String destinatario, String codigo) {
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setTo(destinatario);
            helper.setSubject("PokeApp — Tu código de verificación");
            helper.setText(construirHtml(codigo), true); // true = es HTML

            mailSender.send(mensaje);
        } catch (Exception e) {
            throw new RuntimeException("Error al enviar el email de verificación: " + e.getMessage());
        }
    }

    private String construirHtml(String codigo) {
        return """
            <div style="font-family:Arial,sans-serif;max-width:480px;margin:0 auto;background:#f9f9f9;border-radius:12px;overflow:hidden">
              <div style="background:#E63946;padding:28px;text-align:center">
                <h1 style="color:white;margin:0;font-size:1.8rem;text-shadow:2px 2px 4px rgba(0,0,0,.3)">
                  🔴 PokeApp
                </h1>
              </div>
              <div style="padding:32px 36px">
                <h2 style="color:#1a1a1a;margin-bottom:8px">Verifica tu cuenta</h2>
                <p style="color:#555;margin-bottom:24px;line-height:1.6">
                  Usa el siguiente código para completar tu registro.
                  <br>Caduca en <strong>10 minutos</strong>.
                </p>
                <div style="background:#1a1a1a;border-radius:10px;padding:20px;text-align:center;margin-bottom:24px">
                  <span style="font-size:2.8rem;font-weight:bold;color:#E63946;letter-spacing:10px">
                    %s
                  </span>
                </div>
                <p style="color:#aaa;font-size:.82rem;text-align:center">
                  Si no solicitaste este código, ignora este mensaje.
                </p>
              </div>
            </div>
            """.formatted(codigo);
    }

    private record CodigoInfo(String codigo, LocalDateTime expira) {}
}
