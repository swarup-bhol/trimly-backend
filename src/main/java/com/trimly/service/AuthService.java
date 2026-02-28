package com.trimly.service;

import com.trimly.dto.AuthDto;
import com.trimly.entity.Shop;
import com.trimly.entity.User;
import com.trimly.enums.Role;
import com.trimly.enums.ShopStatus;
import com.trimly.exception.BadRequestException;
import com.trimly.exception.UnauthorizedException;
import com.trimly.repository.ShopRepository;
import com.trimly.repository.UserRepository;
import com.trimly.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final ShopRepository shopRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthDto.AuthResponse loginAdmin(AuthDto.LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));
        if (user.getRole() != Role.ADMIN) throw new UnauthorizedException("Not an admin account");
        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash()))
            throw new UnauthorizedException("Invalid credentials");
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name(), user.getId());
        return new AuthDto.AuthResponse(token, "ADMIN", user.getId(), user.getName(), null, null);
    }

    public AuthDto.AuthResponse loginBarber(AuthDto.LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));
        if (user.getRole() != Role.BARBER) throw new UnauthorizedException("Not a barber account");
        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash()))
            throw new UnauthorizedException("Invalid credentials");
        if (!user.getIsActive()) throw new UnauthorizedException("Account has been disabled");

        Shop shop = shopRepository.findByOwner(user)
                .orElseThrow(() -> new UnauthorizedException("Shop not found"));

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name(), user.getId());
        return new AuthDto.AuthResponse(token, "BARBER", user.getId(), user.getName(),
                shop.getId(), shop.getStatus().name());
    }

    @Transactional
    public AuthDto.AuthResponse loginCustomer(AuthDto.CustomerLoginRequest req) {
        // For customers: find by phone or create a new account
        // Phone-based auth - no password needed
        String email = req.getPhone().replaceAll("[^0-9]", "") + "@customer.trimly.app";

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = User.builder()
                    .name(req.getName())
                    .email(email)
                    .phone(req.getPhone())
                    .passwordHash(passwordEncoder.encode("CUSTOMER_NO_PASSWORD"))
                    .role(Role.CUSTOMER)
                    .build();
            return userRepository.save(newUser);
        });

        // Update name in case it changed
        user.setName(req.getName());
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name(), user.getId());
        return new AuthDto.AuthResponse(token, "CUSTOMER", user.getId(), user.getName(), null, null);
    }

    @Transactional
    public AuthDto.AuthResponse registerBarber(AuthDto.RegisterBarberRequest req) {
        if (userRepository.existsByEmail(req.getEmail()))
            throw new BadRequestException("Email already registered");

        User user = User.builder()
                .name(req.getOwnerName())
                .email(req.getEmail())
                .phone(req.getPhone())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .role(Role.BARBER)
                .build();
        userRepository.save(user);

        Shop shop = Shop.builder()
                .owner(user)
                .shopName(req.getShopName())
                .location(req.getLocation())
                .phone(req.getPhone())
                .emoji("✂️")
                .status(ShopStatus.PENDING)
                .build();
        shopRepository.save(shop);

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name(), user.getId());
        AuthDto.AuthResponse response = new AuthDto.AuthResponse(
                token, "BARBER", user.getId(), user.getName(), shop.getId(), "PENDING");
        response.setMessage("Registration submitted! Awaiting admin approval.");
        return response;
    }
}
