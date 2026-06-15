package vn.edu.ptithcm.mindcard.entity.embeded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Embeddable
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CardContent {

    @Column(name = "text", nullable = true, columnDefinition = "TEXT")
    private String text;

    @Column(name = "image_key", nullable = true)
    private String imageKey;

    @Column(name = "audio_key", nullable = true)
    private String audioKey;
}
