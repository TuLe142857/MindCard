package vn.edu.ptithcm.mindcard.entity.embeded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Embeddable
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CardContent {

    @Column(name = "text", nullable = true, columnDefinition = "TEXT")
    private String text;

    @Column(name = "image_key", nullable = true)
    private String imageKey;

    @Column(name = "image_key", nullable = true)
    private String audioKey;
}
