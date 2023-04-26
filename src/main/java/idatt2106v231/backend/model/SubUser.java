package idatt2106v231.backend.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
public class SubUser {

    @Id
    private int subUserId;

    @Column
    private boolean accessLevel;

    @Column
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "masterUserId", referencedColumnName = "email")
    private User masterUser;
}
