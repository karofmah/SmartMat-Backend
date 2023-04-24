package idatt2106v231.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table
public class OpenAiKey {

    @Id
    private int id;

    @Column
    private String apiKey;

    public String getApiKey() {
        return apiKey;
    }
}