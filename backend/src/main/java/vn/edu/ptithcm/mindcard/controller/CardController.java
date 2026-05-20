package vn.edu.ptithcm.mindcard.controller;


import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.ptithcm.mindcard.dto.response.common.APIResponse;

@RestController
@RequestMapping("/api/cards")
@Tag(name = "Card")
public class CardController {

    @PostMapping("/{cardId}/review")
    public ResponseEntity<APIResponse.Success<?>> reviewCard(){
        return ResponseEntity.ok(APIResponse.success());
    }

}
