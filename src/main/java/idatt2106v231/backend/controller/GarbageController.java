package idatt2106v231.backend.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/garbages")
@CrossOrigin("http://localhost:8000/")
@Tag(name = "Garbage API", description = "API for managing garbages")
public class GarbageController {

}
