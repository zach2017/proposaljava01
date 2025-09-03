package ai.zactonics.genproposal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("name", "Zac");  // pass variable to template
        return "index"; // matches hello.html in templates/
    }
}
