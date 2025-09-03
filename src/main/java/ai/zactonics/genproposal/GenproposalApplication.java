package ai.zactonics.genproposal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class GenproposalApplication {

	public static void main(String[] args) {
		SpringApplication.run(GenproposalApplication.class, args);
	}

	@RestController
    public class HelloController {

    @GetMapping("/demo")
    public String hello() {
        return "Hello, World!";
    }
}

}
