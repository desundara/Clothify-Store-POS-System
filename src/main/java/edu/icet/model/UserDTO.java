package edu.icet.model;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserDTO {
        private int userId;
        private String userName;
        private String password;
        private String email;
        private String role;
        private Integer employeeId;
        private boolean isActive = true;
        private Timestamp deletedAt;

}
