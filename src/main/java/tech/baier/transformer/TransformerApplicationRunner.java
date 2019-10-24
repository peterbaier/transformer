package tech.baier.transformer;

import tech.baier.transformer.services.TransformerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Slf4j
@Component
public class TransformerApplicationRunner implements ApplicationRunner {

    private final TransformerService transformerService;

    public TransformerApplicationRunner(TransformerService transformerService) {
        this.transformerService = transformerService;
    }

    @Override
    public void run(ApplicationArguments arg0) throws Exception {
        log.info("Transformer Application has started.");

        Scanner in = new Scanner(System.in);
        System.out.println("Enter a file path to transform it or 'exit' to finish:");
        String input;

        while (!(input = in.nextLine()).equals("exit")) {
            transformerService.process(input);
            System.out.println("Enter a file path to transform it or 'exit' to finish:");
        }
    }
}