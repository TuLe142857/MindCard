package vn.edu.ptithcm.mindcard.controller;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import vn.edu.ptithcm.mindcard.dto.response.common.APIResponse;

@RestController
@RequestMapping("/api/health")
public class HealthCheck {

    @GetMapping("")
    @Operation(
            summary = "HealthCheck"
    )
    public ResponseEntity<APIResponse<String>> healthCheck(){
        return ResponseEntity.ok(
                APIResponse.ok("OK")
        );
    }

}
