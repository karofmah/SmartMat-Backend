package idatt2106v231.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@ToString
@Builder
@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
public class Refrigerator {

    @Id
    @Column
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int refrigeratorId;

    @NotNull
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_email", referencedColumnName = "email")
    private User user;

    @OneToMany(mappedBy = "refrigerator", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemRefrigerator> itemsInRefrigerator = new ArrayList<>();

    @OneToMany(mappedBy = "refrigerator")
    private List<Garbage> garbagePerYear = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Refrigerator that = (Refrigerator) o;
        return user.equals(that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user);
    }
}