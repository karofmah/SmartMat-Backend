package idatt2106v231.backend.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
@Tag(name = "Item in refrigerator API", description = "API for managing items in refrigerators")
public class ItemRefrigeratorController {
}
