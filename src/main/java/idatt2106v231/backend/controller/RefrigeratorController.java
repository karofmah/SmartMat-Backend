package idatt2106v231.backend.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
@Tag(name = "Refrigerator API", description = "API for managing refrigerators")
public class RefrigeratorController {
}
