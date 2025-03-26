package com.hsf302.ecommerce.config;



import com.hsf302.ecommerce.constant.PredefinedRole;
import com.hsf302.ecommerce.entity.Role;
import com.hsf302.ecommerce.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppInit {

    @Bean
    public CommandLineRunner initDatabase(RoleRepository roleRepository) {
        return args -> {
            // Kiểm tra và khởi tạo Role MEMBER
            if (roleRepository.findByName(PredefinedRole.ROLE_CUSTOMER).isEmpty()) {
                Role customerRole = Role.builder()
                        .name(PredefinedRole.ROLE_CUSTOMER)
                        .build();
                roleRepository.save(customerRole);
                System.out.println("Initialized ROLE_CUSTOMER");
            }

            // Kiểm tra và khởi tạo Role ADMIN
            if (roleRepository.findByName(PredefinedRole.ROLE_ADMIN).isEmpty()) {
                Role adminRole = new Role();
                adminRole.setName(PredefinedRole.ROLE_ADMIN);
                roleRepository.save(adminRole);
                System.out.println("Initialized ROLE_ADMIN");
            }
        };
    }
}
