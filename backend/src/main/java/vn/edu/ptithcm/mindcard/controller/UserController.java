package vn.edu.ptithcm.mindcard.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.ptithcm.mindcard.dto.response.common.APIResponse;

@RestController
@RequestMapping("/users")
@Tag(name = "User")
public class UserController {
    @GetMapping("/me")
    public ResponseEntity<APIResponse.Success<?>> getSelfProfile(){
        return ResponseEntity.ok(APIResponse.success());
    }

    @PostMapping("/me")
    public ResponseEntity<APIResponse.Success<?>> updateSelfProfile(){
        return ResponseEntity.ok(APIResponse.success());
    }


    @GetMapping("/me/decks")
    public ResponseEntity<APIResponse.Success<?>> getSelfDecks(){
        return ResponseEntity.ok(APIResponse.success());
    }

    @GetMapping("/saved_deck")
    public ResponseEntity<APIResponse.Success<?>> getSavedDeck(){
        return ResponseEntity.ok(APIResponse.success());
    }

    
}
