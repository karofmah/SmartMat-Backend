package idatt2106v231.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
public class User {
    /**
     * The email address of the user.
     */
    @Id
    private String email;

    /**
     * password of the user.
     */
    @Column
    private String password;

    /**
     * The first name of the user.
     */
    @Column
    private String firstName;

    /**
     * The last name of the user.
     */
    @Column
    private String lastName;

    /**
     * The phone number of the user.
     */
    @Column
    private int phoneNumber;

    /**
     * The age of the user.
     */
    @Column
    private int age;

    /**
     * The number of members in the household the user.
     */
    @Column
    private int household;

}
