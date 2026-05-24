package com.pokeapp.pokeapp.controller;

import java.io.IOException;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.pokeapp.pokeapp.model.User;
import com.pokeapp.pokeapp.repository.UserRepository;
import com.pokeapp.pokeapp.service.EmailVerificationService;

@Controller
@RequestMapping("/web")
public class WebController {

    private final AuthenticationManager authManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationService emailVerificationService;

    public WebController(AuthenticationManager authManager,
                         UserRepository userRepository,
                         PasswordEncoder passwordEncoder,
                         EmailVerificationService emailVerificationService) {
        this.authManager = authManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailVerificationService = emailVerificationService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String loginSubmit(@RequestParam String username,
                              @RequestParam String password,
                              HttpServletRequest request,
                              HttpServletResponse response,
                              HttpSession session,
                              Model model) {
        try {
            Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            new HttpSessionSecurityContextRepository().saveContext(
                SecurityContextHolder.getContext(), request, response
            );
            session.setAttribute("username", username);
            return "redirect:/web/home";
        } catch (BadCredentialsException e) {
            model.addAttribute("error", "Usuario o contraseña incorrectos");
            return "login";
        }
    }

    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) return "redirect:/web/login";
        model.addAttribute("username", username);
        User usuario = userRepository.findByUsername(username).orElse(null);
        model.addAttribute("isAdmin", usuario != null && "ADMIN".equals(usuario.getRol()));
        return "home";
    }

    @GetMapping("/pokedex")
    public String pokedex(HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) return "redirect:/web/login";
        return "pokedex";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        SecurityContextHolder.clearContext();
        session.invalidate();
        return "redirect:/web/login?logout";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String registerSubmit(@RequestParam String username,
                                 @RequestParam String password,
                                 @RequestParam String email,
                                 @RequestParam("image") MultipartFile image,
                                 HttpSession session,
                                 Model model) {
        if (userRepository.existsByUsername(username)) {
            model.addAttribute("error", "El nombre de usuario ya está en uso.");
            return "register";
        }
        if (userRepository.existsByEmail(email)) {
            model.addAttribute("error", "Este email ya está registrado.");
            return "register";
        }
        try {
            session.setAttribute("pendiente_username", username);
            session.setAttribute("pendiente_email", email);
            session.setAttribute("pendiente_password", passwordEncoder.encode(password));
            if (!image.isEmpty()) {
                session.setAttribute("pendiente_imagen", image.getBytes());
            }
        } catch (IOException e) {
            model.addAttribute("error", "Error al procesar la imagen.");
            return "register";
        }
        try {
            emailVerificationService.enviarCodigo(email);
        } catch (Exception e) {
            model.addAttribute("error", "No se pudo enviar el email de verificación.");
            return "register";
        }
        return "redirect:/web/verificar-email";
    }

    @GetMapping("/verificar-email")
    public String verificarEmailPage(HttpSession session, Model model) {
        String email = (String) session.getAttribute("pendiente_email");
        if (email == null) return "redirect:/web/register";
        model.addAttribute("emailOculto", ocultarEmail(email));
        return "verificaremail";
    }

    @PostMapping("/verificar-email")
    public String verificarEmailSubmit(@RequestParam String codigo,
                                       HttpSession session,
                                       Model model) {
        String email    = (String) session.getAttribute("pendiente_email");
        String username = (String) session.getAttribute("pendiente_username");
        String password = (String) session.getAttribute("pendiente_password");
        if (email == null || username == null) return "redirect:/web/register";
        if (!emailVerificationService.verificar(email, codigo)) {
            model.addAttribute("emailOculto", ocultarEmail(email));
            model.addAttribute("error", "Código incorrecto o expirado.");
            return "verificaremail";
        }
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        byte[] imagen = (byte[]) session.getAttribute("pendiente_imagen");
        if (imagen != null) user.setProfileImage(imagen);
        userRepository.save(user);
        session.removeAttribute("pendiente_username");
        session.removeAttribute("pendiente_email");
        session.removeAttribute("pendiente_password");
        session.removeAttribute("pendiente_imagen");
        return "redirect:/web/login?signup=true";
    }

    @PostMapping("/reenviar-codigo")
    public String reenviarCodigo(HttpSession session, Model model) {
        String email = (String) session.getAttribute("pendiente_email");
        if (email == null) return "redirect:/web/register";
        try {
            emailVerificationService.enviarCodigo(email);
            model.addAttribute("emailOculto", ocultarEmail(email));
            model.addAttribute("reenviadoOk", true);
        } catch (Exception e) {
            model.addAttribute("emailOculto", ocultarEmail(email));
            model.addAttribute("error", "No se pudo reenviar el email.");
        }
        return "verificaremail";
    }

    @GetMapping("/check-email")
    @ResponseBody
    public Map<String, Boolean> checkEmail(@RequestParam String email) {
        return Map.of("existe", userRepository.existsByEmail(email));
    }

    private String ocultarEmail(String email) {
        int at = email.indexOf('@');
        if (at <= 2) return email;
        return email.substring(0, 2) + "***" + email.substring(at);
    }

    // ── Avatar ──
    @GetMapping("/perfil/avatar")
    @ResponseBody
    public ResponseEntity<byte[]> avatar(HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) return ResponseEntity.notFound().build();
        User usuario = userRepository.findByUsername(username).orElse(null);
        if (usuario == null || usuario.getProfileImage() == null)
            return ResponseEntity.notFound().build();
        byte[] img = usuario.getProfileImage();
        String contentType = "image/jpeg";
        if (img.length > 3) {
            if ((img[0] & 0xFF) == 0x89 && img[1] == 0x50) contentType = "image/png";
            else if ((img[0] & 0xFF) == 0x47 && img[1] == 0x49) contentType = "image/gif";
            else if ((img[0] & 0xFF) == 0x52 && img[1] == 0x49) contentType = "image/webp";
        }
        return ResponseEntity.ok()
                .header("Content-Type", contentType)
                .header("Cache-Control", "no-cache")
                .body(img);
    }

    // ── Perfil ──
    @GetMapping("/perfil")
    public String perfil(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) return "redirect:/web/login";
        User usuario = userRepository.findByUsername(username).orElse(null);
        if (usuario == null) return "redirect:/web/login";
        model.addAttribute("username", usuario.getUsername());
        model.addAttribute("email", usuario.getEmail());
        model.addAttribute("tieneAvatar", usuario.getProfileImage() != null);
        String okMsg  = (String) session.getAttribute("perfilOk");
        String errMsg = (String) session.getAttribute("perfilError");
        if (okMsg  != null) { model.addAttribute("okMsg",  okMsg);  session.removeAttribute("perfilOk"); }
        if (errMsg != null) { model.addAttribute("errMsg", errMsg); session.removeAttribute("perfilError"); }
        return "perfil";
    }

    @GetMapping("/test-hash")
    @ResponseBody
    public String testHash() {
        return passwordEncoder.encode("password123");
    }

    @PostMapping("/perfil/username")
    public String cambiarUsername(@RequestParam String nuevoUsername,
                                   HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) return "redirect:/web/login";
        if (userRepository.existsByUsername(nuevoUsername)) {
            session.setAttribute("perfilError", "Ese nombre ya está en uso.");
            return "redirect:/web/perfil";
        }
        User usuario = userRepository.findByUsername(username).orElse(null);
        if (usuario == null) return "redirect:/web/login";
        usuario.setUsername(nuevoUsername);
        userRepository.save(usuario);
        session.setAttribute("username", nuevoUsername);
        session.setAttribute("perfilOk", "Nombre actualizado correctamente.");
        return "redirect:/web/perfil";
    }

    @PostMapping("/perfil/avatar")
    public String cambiarAvatar(@RequestParam("imagen") MultipartFile imagen,
                                 HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) return "redirect:/web/login";
        User usuario = userRepository.findByUsername(username).orElse(null);
        if (usuario == null) return "redirect:/web/login";
        if (!imagen.isEmpty()) {
            try {
                usuario.setProfileImage(imagen.getBytes());
                userRepository.save(usuario);
                session.setAttribute("perfilOk", "Foto actualizada correctamente.");
            } catch (IOException e) {
                session.setAttribute("perfilError", "Error al procesar la imagen.");
            }
        }
        return "redirect:/web/perfil";
    }

    @PostMapping("/perfil/password")
    public String cambiarPassword(@RequestParam String passwordActual,
                                   @RequestParam String passwordNueva,
                                   HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) return "redirect:/web/login";
        User usuario = userRepository.findByUsername(username).orElse(null);
        if (usuario == null) return "redirect:/web/login";
        if (!passwordEncoder.matches(passwordActual, usuario.getPassword())) {
            session.setAttribute("perfilError", "La contraseña actual es incorrecta.");
            return "redirect:/web/perfil";
        }
        usuario.setPassword(passwordEncoder.encode(passwordNueva));
        userRepository.save(usuario);
        session.setAttribute("perfilOk", "Contraseña actualizada correctamente.");
        return "redirect:/web/perfil";
    }
}
